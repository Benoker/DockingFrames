/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui;

import java.awt.Window;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabDockAction;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.control.DockRegister;
import bibliothek.gui.dock.dockable.DefaultDockableFactory;
import bibliothek.gui.dock.event.*;
import bibliothek.gui.dock.frontend.FrontendEntry;
import bibliothek.gui.dock.frontend.MissingDockableStrategy;
import bibliothek.gui.dock.frontend.RegisteringDockFactory;
import bibliothek.gui.dock.frontend.Setting;
import bibliothek.gui.dock.layout.*;
import bibliothek.gui.dock.security.SecureFlapDockStationFactory;
import bibliothek.gui.dock.security.SecureScreenDockStationFactory;
import bibliothek.gui.dock.security.SecureSplitDockStationFactory;
import bibliothek.gui.dock.security.SecureStackDockStationFactory;
import bibliothek.gui.dock.station.flap.FlapDockPropertyFactory;
import bibliothek.gui.dock.station.flap.FlapDockStationFactory;
import bibliothek.gui.dock.station.screen.ScreenDockPropertyFactory;
import bibliothek.gui.dock.station.screen.ScreenDockStationFactory;
import bibliothek.gui.dock.station.split.SplitDockPropertyFactory;
import bibliothek.gui.dock.station.split.SplitDockStationFactory;
import bibliothek.gui.dock.station.stack.StackDockPropertyFactory;
import bibliothek.gui.dock.station.stack.StackDockStationFactory;
import bibliothek.gui.dock.util.*;
import bibliothek.util.Version;
import bibliothek.util.container.Single;
import bibliothek.util.xml.XAttribute;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XException;

/**
 * A DockFrontend provides some methods to handle the storage of various layouts.
 * The frontend can save the current layout (the location of all Dockables) and
 * later restore it. Each set of properties is stored in a {@link Setting}. Subclasses
 * might override the following methods to store additional information:
 * <ul>
 *  <li>{@link #createSetting()}</li>
 *  <li>{@link #getSetting(boolean)} and {@link #setSetting(Setting, boolean)}</li>
 *  <li>{@link #write(Setting, boolean, DataOutputStream)} and {@link #read(boolean, DataInputStream)} or
 *      {@link Setting#write(DockSituation, PropertyTransformer, boolean, DataOutputStream)} and 
 *      {@link Setting#read(DockSituation, PropertyTransformer, boolean, DataInputStream)}</li>
 *  <li>{@link #writeXML(Setting, boolean, XElement)} and {@link #readXML(boolean, XElement)} or
 *      {@link Setting#writeXML(DockSituation, PropertyTransformer, boolean, XElement)} and
 *      {@link Setting#readXML(DockSituation, PropertyTransformer, boolean, XElement)} </li>
 * </ul><br>
 * The frontend has a list of Dockables. It assumes that these Dockables never
 * changes. The frontend can add a "close"-button to these Dockables. The location
 * of these Dockables is stored as well. Dockables which are not {@link #add(Dockable, String) added}
 * to this frontend, are just ignored.<br>
 * <b>Note:</b> Clients must provide a set of root stations 
 * ({@link #addRoot(DockStation, String) addRoot}). The frontend will only 
 * store the locations of children of these roots. The frontend adds these
 * roots also to its {@link DockController controller}, but the frontend does
 * not observe the controller, and so all changes must be applied directly
 * on the frontend (on the other hand, clients may use more than one frontend).<br>
 * Clients must also provide some {@link #registerFactory(DockFactory) factories} 
 * to allow the storage of their elements. The default-factories are already
 * installed.
 * @author Benjamin Sigg
 */
public class DockFrontend {
    public static final PropertyKey<KeyStroke> HIDE_ACCELERATOR = 
        new PropertyKey<KeyStroke>( "frontend hide accelerator" );
    
    /** prefix used for {@link Dockable}s when creating a new {@link PredefinedDockSituation} */
    protected static final String DOCKABLE_KEY_PREFIX = "dockable";
    
    /** prefix used for {@link DockStation root}s when creating a new {@link PredefinedDockSituation} */
    protected static final String ROOT_KEY_PREFIX = "root";
    
	/** The controller where roots are added */
    private DockController controller;
    /** An action and actionguard which hides Dockables */
    private Hider hider;
    
    /** The locations of the known Dockables */
    private Map<String, DockInfo> dockables = new HashMap<String, DockInfo>();
    /** the identifiers of the {@link DockInfo}s which should stay around even if their dockable is removed */
    private Set<String> empty = new HashSet<String>();
    
    /** The station which is used to add Dockables if no other station is explicitly requested */
    private DockStation defaultStation;
    /** The roots of this frontend */
    private Map<String, RootInfo> roots = new HashMap<String, RootInfo>();
    
    /** A set of factories needed to store Dockables */
    private Set<DockFactory<? extends DockElement, ?>> dockFactories =
        new HashSet<DockFactory<? extends DockElement,?>>();
    
    /** A set of factories needed to read Dockables that are missing in the cache */
    private Set<DockFactory<? extends Dockable, ?>> backupDockFactories =
        new HashSet<DockFactory<? extends Dockable,?>>();
    
    /** A set of factories needed to store additional information about Dockables */
    private Set<AdjacentDockFactory<?>> adjacentDockFactories =
        new HashSet<AdjacentDockFactory<?>>();
    
    /** A set of factories needed to store {@link DockableProperty properties} */
    private Set<DockablePropertyFactory> propertyFactories = new HashSet<DockablePropertyFactory>();
    
    /** The name of the setting which is currently loaded */
    private String currentSetting;
    /** A map of all known settings */
    private Map<String, Setting> settings = new HashMap<String, Setting>();
    
    /** A list of observers */
    private List<DockFrontendListener> listeners = new ArrayList<DockFrontendListener>();
    
    /** A list of observers to be notified if a {@link Dockable} gets closed or opened */
    private List<VetoableDockFrontendListener> vetoableListeners = new ArrayList<VetoableDockFrontendListener>();
    
    /** A filter for elements which should not be changed when writing or reading a normal setting */
    private DockSituationIgnore ignoreForEntry;
    /**
     *  A filter for elements which should not be changed when writing or reading the 
     *  final setting during the startup or shutdown of the application. 
     */
    private DockSituationIgnore ignoreForFinal;
    
    /** tells what to do with the location information of missing {@link Dockable}s */
    private MissingDockableStrategy missingDockable = MissingDockableStrategy.DISCARD_ALL;
    
    /**
     * Tells whether to show the hide-action on hideable dockables or not
     */
    private boolean showHideAction = true;
    
    /** the default value for {@link DockInfo#entryLayout} */
    private boolean defaultEntryLayout = true;
    
    /** the default value for {@link DockInfo#hideActionVisible} */
    private boolean defaultHideable = false;
    
    /**
     * Whether {@link #fireShown(Dockable)} and {@link #fireHidden(Dockable)} should be called
     * automatically when triggered by a {@link DockRegister}-event or not.
     */
    private int onAutoFire = 0;
    
    /**
     * The last {@link Setting} that was {@link #setSetting(Setting, boolean) applied}
     * with the entry flag set to <code>false</code>. Can be <code>null</code>.
     */
    private Setting lastAppliedFullSetting = null;
    
    /**
     * The last {@link Setting} that was {@link #setSetting(Setting, boolean) applied}
     * with the entry flag set to <code>true</code>. Can be <code>null</code>.
     */
    private Setting lastAppliedEntrySetting = null;
    
    /**
     * Constructs a new frontend, creates a new controller.
     */
    public DockFrontend(){
        this( new DockController(), new NullWindowProvider() );
    }
    
    /**
     * Constructs a new frontend, creates a new controller. Registers a
     * {@link ScreenDockStationFactory}, which can only be created if the owner
     * of the dialogs is known.
     * @param owner the owner of the dialogs of a {@link ScreenDockStationFactory},
     * may be <code>null</code>
     */
    public DockFrontend( Window owner ){
        this( new DockController(), owner == null ? new NullWindowProvider() : new DirectWindowProvider( owner ) );
    }
    
    /**
     * Constructs a new frontend, creates a new controller. Registers a
     * {@link ScreenDockStationFactory}, which can only be created if the owner
     * of the dialogs is known.
     * @param owner the owner of the dialogs of a {@link ScreenDockStationFactory},
     * may be <code>null</code>
     */
    public DockFrontend( WindowProvider owner ){
        this( new DockController(), owner );
    }
    
    /**
     * Constructs a new frontend.
     * @param controller the controller used to store root stations
     */
    public DockFrontend( DockController controller ){
        this( controller, new NullWindowProvider() );
    }
    
    /**
     * Constructs a new frontend, tries to set up a {@link ScreenDockStationFactory}
     * and sets the root window of <code>controller</code> to <code>owner</code>.
     * @param controller the controller used to store the root stations
     * @param owner the owner of the dialog of a {@link ScreenDockStation},
     * may be <code>null</code>
     */
    public DockFrontend( DockController controller, Window owner ){
        this( controller, owner == null ? new NullWindowProvider() : new DirectWindowProvider( owner ));
    }

    /**
     * Constructs a new frontend, tries to set up a {@link ScreenDockStationFactory}
     * and sets the root window of <code>controller</code> to <code>owner</code>.
     * @param controller the controller used to store the root stations
     * @param owner the owner of the dialog of a {@link ScreenDockStation},
     * may be <code>null</code>
     */
    public DockFrontend( DockController controller, WindowProvider owner ){
        if( controller == null )
            throw new IllegalArgumentException( "controller must not be null" );
        
        this.controller = controller;
        controller.setRootWindowProvider( owner );
        
        hider = createHider();
        controller.addActionGuard( hider );
        
        registerFactory( new DefaultDockableFactory() );
        registerFactory( new SplitDockStationFactory() );
        registerFactory( new SecureSplitDockStationFactory() );
        registerFactory( new StackDockStationFactory() );
        registerFactory( new SecureStackDockStationFactory() );
        registerFactory( new FlapDockStationFactory() );
        registerFactory( new SecureFlapDockStationFactory() );
        
        registerFactory( new ScreenDockStationFactory( controller.getRootWindowProvider() ));
        registerFactory( new SecureScreenDockStationFactory( controller.getRootWindowProvider() ));
        
        registerFactory( new SplitDockPropertyFactory() );
        registerFactory( new StackDockPropertyFactory() );
        registerFactory( new FlapDockPropertyFactory() );
        registerFactory( new ScreenDockPropertyFactory() );
        
        controller.getRegister().addDockRegisterListener( new DockAdapter(){
            @Override
            public void dockableRegistered( DockController controller, Dockable dockable ) {
                if( onAutoFire == 0 ){
                    fireShown( dockable );
                    fireShown( dockable, false );
                }
            }
            @Override
            public void dockableUnregistered( DockController controller, Dockable dockable ) {
                if( onAutoFire == 0 ){
                    fireHidden( dockable );
                    fireHidden( dockable, false );
                }
            }
        });
    }
    
    /**
     * Gets the controller which is used by this frontend.
     * @return the controller
     */
    public DockController getController() {
        return controller;
    }
    
    /**
     * Sets the window which is used as root for any dialog, can be <code>null</code>.
     * @param owner the owning window
     * @see DockController#setRootWindowProvider(WindowProvider)
     */
    public void setOwner( WindowProvider owner ){
        controller.setRootWindowProvider( owner );
    }
    
    /**
     * Gets the current provider for the root window. Note that this might not
     * be the same as given to {@link #setOwner(WindowProvider)}, however it
     * will return the same value.
     * @return the provider, never <code>null</code>
     */
    public WindowProvider getOwner(){
        return controller.getRootWindowProvider();
    }
    
    /**
     * Gets the list of {@link Dockable Dockables} which are added to this frontend.
     * @return the Dockables
     * @deprecated please use {@link #listDockables()}
     */
    @Deprecated
    public Collection<Dockable> getDockables(){
        return listDockables();
    }
    
    /**
     * Adds a listener to this frontend. The listener will recieve notifications
     * if anything changes on this frontend. 
     * @param listener the observer
     */
    public void addFrontendListener( DockFrontendListener listener ){
        listeners.add( listener );
    }
    
    /**
     * Removes an earlier added listener from this frontend.
     * @param listener the observer which will be removed
     */
    public void removeFrontendListener( DockFrontendListener listener ){
        listeners.remove( listener );
    }
    
    /**
     * Adds <code>listener</code> to this frontend. The listener will be notified
     * when a {@link Dockable} will be or is closed.
     * @param listener the new listener
     */
    public void addVetoableListener( VetoableDockFrontendListener listener ){
        vetoableListeners.add( listener );
    }
    
    /**
     * Removes <code>listener</code> from this frontend.
     * @param listener the listener to remove
     */
    public void removeVetoableListener( VetoableDockFrontendListener listener ){
        vetoableListeners.remove( listener );
    }
    
    /**
     * Registers a factory to write and read {@link Dockable Dockables} and
     * {@link DockStation DockStations}.
     * @param factory the new factory
     */
    public void registerFactory( DockFactory<? extends DockElement, ?> factory ){
    	if( factory == null )
    		throw new IllegalArgumentException( "factory must not be null" );
    	
        dockFactories.add( factory );
        fillMissing( factory );
    }

    /**
     * Registers a factory to write and read {@link Dockable}s and {@link DockStation}s.
     * @param factory the new factory
     * @param backup if <code>true</code>, then <code>factory</code> is registered
     * as {@link #registerBackupFactory(DockFactory) backup factory} as well.
     */
    public void registerFactory( DockFactory<? extends Dockable, ?> factory, boolean backup ){
        if( factory == null )
            throw new IllegalArgumentException( "factory must not be null" );
        
        dockFactories.add( factory );
        if( backup )
            backupDockFactories.add( factory );
        fillMissing( factory );
    }
    
    /**
     * Register a backup factory. A backup factory is used to create a {@link Dockable}
     * that is expected to be in the cache, but is missing. The new {@link Dockable}
     * is automatically added to this frontend.
     * @param factory a new factory
     */
    public void registerBackupFactory( DockFactory<? extends Dockable, ?> factory ){
        if( factory == null )
            throw new IllegalArgumentException( "factory must not be null" );
        
        backupDockFactories.add( factory );
        fillMissing( factory );
    }
    
    /**
     * Registers a factory that stores additional information for a set of
     * {@link Dockable}s.
     * @param factory the additional factory, not <code>null</code>
     */
    public void registerAdjacentFactory( AdjacentDockFactory<?> factory ){
        if( factory == null )
            throw new IllegalArgumentException( "factory must not be null" );
        
        adjacentDockFactories.add( factory );
    }
    
    /**
     * Removes a factory from this frontend. This method does not remove
     * backup factories.
     * @param factory the factory to remove
     * @see #unregisterBackupFactory(DockFactory)
     */
    public void unregisterFactory( DockFactory<? extends DockElement, ?> factory ){
        dockFactories.remove( factory );
    }
    
    /**
     * Removes a backup factory from this frontend.
     * @param factory the factory to remove
     */
    public void unregisterBackupFactory( DockFactory<? extends DockElement, ?> factory ){
        backupDockFactories.remove( factory );
    }
    
    /**
     * Removes an additional factory from this frontend.
     * @param factory the factory to remove
     */
    public void unregisterAdjacentFactory( AdjacentDockFactory<?> factory ){
        adjacentDockFactories.remove( factory );
    }
    
    /**
     * Registers a factory to write and read properties. Clients only need this
     * method if they provide a new type of {@link DockStation}.
     * @param factory the new factory
     */
    public void registerFactory( DockablePropertyFactory factory ){
    	if( factory == null )
    		throw new IllegalArgumentException( "factory must not be null" );
        propertyFactories.add( factory );
    }
    
    /**
     * Adds a Dockable to this frontend. The frontend provides a "close"-button
     * for <code>dockable</code>. The frontend also assumes that <code>dockable</code>
     * can be reused when reading a setting. That means, that the factory which
     * matches the key of <code>dockable</code> does not create a new instance
     * when reading the preferences of <code>dockable</code>. You should note that
     * the frontend does not support {@link Dockable Dockables} whose lifespan
     * ends when they are made invisible.
     * @param id the unique name of the Dockable
     * @param dockable the new Dockable
     * @throws IllegalArgumentException if either of <code>dockable</code> or
     * <code>id</code> is <code>null</code>, or if <code>id</code> is not
     * unique.
     */
    public void addDockable( String id, Dockable dockable ){
        add( dockable, id );
    }
    
    /**
     * Adds a Dockable to this frontend. The frontend provides a "close"-button
     * for <code>dockable</code>. The frontend also assumes that <code>dockable</code>
     * can be reused when reading a setting. That means, that the factory which
     * matches the key of <code>dockable</code> does not create a new instance
     * when reading the preferences of <code>dockable</code>. You should note that
     * the frontend does not support {@link Dockable Dockables} whose lifespan
     * ends when they are made invisible.
     * @param dockable the new Dockable
     * @param name the unique name of the Dockable
     * @throws IllegalArgumentException if either of <code>dockable</code> or
     * <code>name</code> is <code>null</code>, or if <code>name</code> is not
     * unique.
     * @deprecated replaced by {@link #addDockable(String, Dockable)}, since <code>name</code>
     * is used as key in a map, it should come first. Note: this method might
     * be removed in future releases.
     */
    @Deprecated
    public void add( Dockable dockable, String name ){
        if( dockable == null )
            throw new IllegalArgumentException( "Dockable must not be null" );
        
        if( name == null )
            throw new IllegalArgumentException( "name must not be null" );
        
        DockInfo info = dockables.get( name );
        if( info != null ){
            if( info.getDockable() == null ){
                info.setDockable( dockable );
                info.updateHideAction();
            }
            else
                throw new IllegalArgumentException( "There is already a dockable registered with name " + name );
        }
        else{
            info = new DockInfo( dockable, name );
            dockables.put( name, info );    
        }
        
        DockLayoutComposition layout = info.getLayout();
        if( layout != null ){
            try{
                DockSituation situation = createSituation( false );
                layout = situation.fillMissing( layout );
                situation.convert( layout );
                info.setLayout( null );
            }
            catch( IOException ex ){
                throw new IllegalArgumentException( "Cannot read old layout information", ex );
            }
        }
        
        fireAdded( dockable );
    }
    
    /**
     * Sets the strategy how to deal with location information of {@link Dockable}s
     * which are missing and which are not marked as {@link #addEmpty(String) empty}.<br>
     * If information passes the strategy, then a new {@link #addEmpty(String) empty info}
     * will be added to store it. Note that setting the strategy does only
     * affect future actions, information already stored or discarded will not
     * be rescued or thrown away.
     * @param missingDockable the new strategy, <code>null</code> is valid and
     * will force this frontend to discard any information.
     */
    public void setMissingDockableStrategy( MissingDockableStrategy missingDockable ) {
        if( missingDockable == null )
            this.missingDockable = MissingDockableStrategy.DISCARD_ALL;
        else
            this.missingDockable = missingDockable;
    }
    
    /**
     * Gets the strategy that is applied for location information of 
     * missing {@link Dockable}s.
     * @return the strategy, never <code>null</code>
     * @see #setMissingDockableStrategy(MissingDockableStrategy)
     */
    public MissingDockableStrategy getMissingDockable() {
        return missingDockable;
    }
    
    /**
     * Gets an independent map containing all Dockables registered to this
     * frontend.
     * @return the map of Dockables
     */
    public Map<String, Dockable> getNamedDockables(){
    	Map<String, Dockable> result = new HashMap<String, Dockable>();
    	for( Map.Entry<String, DockInfo> entry : dockables.entrySet() ){
    	    if( entry.getValue().getDockable() != null )
    	        result.put( entry.getKey(), entry.getValue().getDockable() );
    	}
    	return result;
    }
    
    /**
     * Gets the {@link Dockable} which was {@link #add(Dockable, String) added}
     * to this frontend with the name <code>name</code>.
     * @param name the name of a {@link Dockable}
     * @return the element or <code>null</code>
     */
    public Dockable getDockable( String name ){
        DockInfo info = getInfo( name );
        return info == null ? null : info.dockable;
    }
    
    /**
     * Searches the name of <code>dockable</code> as it was given to
     * {@link #add(Dockable, String)}.
     * @param dockable some element whose name is searched
     * @return the name or <code>null</code>
     */
    public String getNameOf( Dockable dockable ){
        if( dockable == null )
            throw new NullPointerException( "dockable is null" );
        
        for( Map.Entry<String, DockInfo> entry : dockables.entrySet() ){
            if( entry.getValue().dockable == dockable )
                return entry.getKey();
        }
        return null;
    }
    
    /**
     * Adds a root to this frontend. Only {@link Dockable Dockables} which are
     * children of a root can be stored. The frontend forwards the roots to
     * its {@link #getController() controller} 
     * (through the {@link DockController#add(DockStation) add}-method). Note
     * that the frontend does not observe its controller and therefore does not
     * know whether there are other roots registered at the controller.<br>
     * Clients should also provide a {@link #setDefaultStation(DockStation) default station}.
     * @param id the unique name of the station
     * @param station the new station
     * @throws IllegalArgumentException if <code>station</code> or <code>name</code>
     * is <code>null</code>, or if <code>name</code> is not unique.
     */
    public void addRoot( String id, DockStation station ){
        addRoot( station, id );
    }
    
    /**
     * Adds a root to this frontend. Only {@link Dockable Dockables} which are
     * children of a root can be stored. The frontend forwards the roots to
     * its {@link #getController() controller} 
     * (through the {@link DockController#add(DockStation) add}-method). Note
     * that the frontend does not observe its controller and therefore does not
     * know whether there are other roots registered at the controller.<br>
     * Clients should also provide a {@link #setDefaultStation(DockStation) default station}.
     * @param station the new station
     * @param name the unique name of the station
     * @throws IllegalArgumentException if <code>station</code> or <code>name</code>
     * is <code>null</code>, or if <code>name</code> is not unique.
     * @deprecated replaced by {@link #addRoot(String, DockStation)}, since 
     * <code>name</code> is used as key in a map it should come first
     */
    @Deprecated
    public void addRoot( DockStation station, String name ){
        if( station == null )
            throw new IllegalArgumentException( "Stations must not be null" );
        
        if( name == null )
            throw new IllegalArgumentException( "name must not be null" );
        
        if( roots.containsKey( name ))
        	throw new IllegalArgumentException( "There is already a station registered with name " + name );
        
        controller.getRegister().add( station );
        roots.put( name, new RootInfo( station, name ));
    }
    
    /**
     * Gets the root with the designated name.
     * @param name the name of the root
     * @return the station or <code>null</code>
     */
    public DockStation getRoot( String name ){
        RootInfo info = roots.get( name );
        if( info == null )
            return null;
        
        return info.getStation();
    }
     
    /**
     * Gets a modifiable array containing all {@link DockStation}s which are
     * registered as root.
     * @return the list of roots
     */
    public DockStation[] getRoots(){
        DockStation[] stations = new DockStation[ roots.size() ];
        int i = 0;
        for( RootInfo info : roots.values() ){
            stations[i++] = info.station;
        }
        return stations;
    }
    
    /**
     * Adds a representative for some {@link DockElement}. Note that no two
     * representatives can have the same
     * {@link DockElementRepresentative#getComponent() component}. If two have
     * the same, then the second one overrides the first one.
     * @param representative the new representative
     * @see DockController#addRepresentative(DockElementRepresentative)
     */
    public void addRepresentative( DockElementRepresentative representative ){
        controller.addRepresentative( representative );
    }
    
    /**
     * Removes <code>representative</code> from this frontend.
     * @param representative the element to remove
     * @see DockController#removeRepresentative(DockElementRepresentative)
     */
    public void removeRepresentative( DockElementRepresentative representative ){
        controller.removeRepresentative( representative );
    }
    
    /**
     * Sets the default station of this frontend. The default station is needed
     * to add {@link Dockable Dockables} whose location could not be stored
     * earlier or whose location has become invalid.
     * @param defaultStation the default station, can be <code>null</code>
     */
    public void setDefaultStation( DockStation defaultStation ) {
    	if( defaultStation != null && getRoot( defaultStation ) == null )
    		throw new IllegalArgumentException( "The default station must be registered as root" );
    	
        this.defaultStation = defaultStation;
    }
    
    /**
     * Gets the default station of this frontend. This is either the value of
     * {@link #setDefaultStation(DockStation)} or a root picked at random.
     * @return the station, might be <code>null</code>
     */
    public DockStation getDefaultStation() {
        if( defaultStation != null )
            return defaultStation;
        
        Iterator<RootInfo> infos = roots.values().iterator();
        if( infos.hasNext() )
            return infos.next().getStation();
        
        return null;
    }
    
    /**
     * Removes a {@link Dockable} which was earlier added to this frontend.
     * @param dockable the element to remove
     */
    public void remove( Dockable dockable ){
        DockInfo info = getInfo( dockable );
        if( info != null ){
            boolean hideable = info.isHideable();
            info.setHideable( false );
            
            if( empty.contains( info.getKey() )){
                fireRemoved( dockable );
                info.setDockable( null );
                info.setHideable( hideable );
            }
            else{
                dockables.remove( info.getKey() );
                fireRemoved( dockable );
            }
        }
    }

    /**
     * Adds the name of a {@link Dockable} whose properties should be stored
     * in this frontend even if the {@link Dockable} itself is not 
     * registered.<br>
     * Note that <code>this</code> can add "empty infos" automatically
     * when calling {@link #setSetting(Setting, boolean)} and information
     * is found that is not associated with any {@link Dockable}, but
     * whose key passes the methods of {@link MissingDockableStrategy}.
     * @param name the name of the dockable
     */
    public void addEmpty( String name ){
        if( name == null )
            throw new IllegalArgumentException( "name must not be null" );
        
        empty.add( name );
        if( !dockables.containsKey( name )){
            dockables.put( name, new DockInfo( null, name ));
        }
    }
    
    /**
     * Removes the properties of a non existing {@link Dockable} and/or 
     * changes the flag to store information about the non existing 
     * <code>Dockable</code><code>name</code> to <code>false</code>.
     * @param name the empty element to remove
     */
    public void removeEmpty( String name ){
        empty.remove( name );
        DockInfo info = getInfo( name );
        if( info != null ){
            if( info.getDockable() == null ){
                dockables.remove( name );
            }
        }
    }
    
    /**
     * Gets a list of all keys that are marked as <code>empty</code>.
     * @param all if <code>true</code> then just all keys are returned, if 
     * <code>false</code> then only those keys are returned for which no
     * {@link Dockable} is registered.
     * @return the list of keys marked as empty, may be <code>null</code>
     * @see #addEmpty(String)
     * @see #removeEmpty(String)
     */
    public String[] listEmpty( boolean all ){
        if( all ){
            return empty.toArray( new String[ empty.size() ] );
        }
        else{
            List<String> result = new ArrayList<String>();
            for( String key : empty ){
                DockInfo info = getInfo( key );
                if( info.getDockable() == null ){
                    result.add( key );
                }
            }
            return result.toArray( new String[ result.size() ] );
        }
    }
    
    /**
     * Removes a root from this frontend. If the root is the 
     * {@link #setDefaultStation(DockStation) default station}, then the
     * default station is set to <code>null</code>.
     * @param station the root to remove
     */
    public void removeRoot( DockStation station ){
        RootInfo info = getRoot( station );
        if( info != null ){
        	if( defaultStation == info.getStation() )
        		defaultStation = null;
        	
            roots.remove( info.getName() );
            controller.getRegister().remove( station );
        }
    }
    
    /**
     * Tells whether this {@link DockFrontend} currently knows where to
     * put <code>dockable</code>.
     * @param dockable the element whose location might be known
     * @return <code>true</code> if the location of <code>dockable</code> is known
     */
    public boolean hasLocation( Dockable dockable ){
    	DockInfo info = getInfo( dockable );
    	if( info == null )
    		return false;
    	
    	if( isShown( dockable ))
    		return true;
    	
    	return info.root != null && info.location != null;
    }
    
    /**
     * Sets a filter which is applied when saving or loading a normal entry.
     * @param ignoreForEntry the filter, can be <code>null</code>
     */
    public void setIgnoreForEntry(DockSituationIgnore ignoreForEntry) {
		this.ignoreForEntry = ignoreForEntry;
	}
    
    /**
     * Gets the filter which is used when saving or loading a normal entry.
     * @return the filter, might be <code>null</code>
     */
    public DockSituationIgnore getIgnoreForEntry() {
		return ignoreForEntry;
	}
    
    /**
     * Sets the filter which is applied when saving or loading the final layout
     * at the startup or shutdown of the application. 
     * @param ignoreForFinal the filter, can be <code>null</code>
     */
    public void setIgnoreForFinal(DockSituationIgnore ignoreForFinal) {
		this.ignoreForFinal = ignoreForFinal;
	}

    /**
     * Gets the filter which is applied when saving or loading the final layout
     * at the startup or shutdown of the application. 
     * @return the filter, can be <code>null</code>
     */
    public DockSituationIgnore getIgnoreForFinal() {
		return ignoreForFinal;
	}
    
    /**
     * Gets the set of properties which have a controller-global influence.
     * @return the set of properties
     */
   	public DockProperties getDockProperties(){
   		return controller.getProperties();
   	}
   	
   	/**
   	 * Gets the last {@link Setting} that was given to {@link #setSetting(Setting, boolean)}
   	 * when the entry-parameter was set to <code>false</code>. This might be
   	 * <code>null</code> if no setting was yet applied.
   	 * @return the setting, can be <code>null</code>
   	 */
   	public Setting getLastAppliedFullSetting() {
        return lastAppliedFullSetting;
    }

    /**
     * Gets the last {@link Setting} that was given to {@link #setSetting(Setting, boolean)}
     * when the entry-parameter was set to <code>true</code>. This might be
     * <code>null</code> if no setting was yet applied or a non-entry setting
     * was applied.
     * @return the setting, can be <code>null</code>
     */
   	public Setting getLastAppliedEntrySetting() {
        return lastAppliedEntrySetting;
    }
   	
    /**
     * Gets a set of the names of all known settings.
     * @return the set of names
     */
    public Set<String> getSettings(){
        Set<String> keys = settings.keySet();
        return Collections.unmodifiableSet( keys );
    }
    
    /**
     * Gets the name of the setting which was loaded or saved the last time.
     * @return the name, might be <code>null</code> if no setting was saved yet
     */
    public String getCurrentSetting(){
        return currentSetting;
    }
    
    /**
     * Sets the name of the current setting. If there is already a setting
     * with this name, then this setting is loaded. Otherwise the
     * current setting is saved with the new name.
     * @param setting the name of the new setting
     */
    public void setCurrentSetting( String setting ){
    	if( setting == null )
    		throw new IllegalArgumentException( "the name of a setting must not be null" );
    	
    	if( settings.containsKey( setting ))
    		load( setting );
    	else
    		save( setting );
    }

    /**
     * Tells whether <code>dockable</code> is hidden or not.
     * @param dockable the element whose state is asked
     * @return <code>true</code> if <code>dockable</code> is not visible
     */
    public boolean isHidden( Dockable dockable ){
        return dockable.getController() == null;
    }
    
    /**
     * Tells whether <code>dockable</code> is visible or not.
     * @param dockable the element whose state is asked
     * @return <code>true</code> if <code>dockable</code> is visible
     */
    public boolean isShown( Dockable dockable ){
        return dockable.getController() != null;
    }
    
    /**
     * Sets the default setting for {@link #setHideable(Dockable, boolean)}. This
     * default value is stored as soon as the identifier of a {@link Dockable} 
     * becomes known and further changes of the default value will not affect it. 
     * @param defaultHideable the default value
     * @see #setHideable(Dockable, boolean)
     */
    public void setDefaultHideable( boolean defaultHideable ) {
        this.defaultHideable = defaultHideable;
    }
    
    /**
     * Gets the default value of {@link #setHideable(Dockable, boolean)}.
     * @return the default value
     * @see #setDefaultHideable(boolean)
     */
    public boolean isDefaultHideable() {
        return defaultHideable;
    }
    
    /**
     * Tells whether there is a "close"-action for <code>dockable</code> or not.
     * @param dockable the element whose state is asked, must be known to this
     * frontend.
     * @return <code>true</code> if <code>dockable</code> has a close-action
     */
    public boolean isHideable( Dockable dockable ){
        DockInfo info = getInfo( dockable );
        if( info == null )
            throw new IllegalArgumentException( "Dockable not registered" );
        
        return info.isHideable();
    }
    
    /**
     * Sets whether to show add a close-action or not to <code>dockable</code>.
     * Changes are affected immediately.
     * @param dockable the element whose state will be changed
     * @param hideable the new state
     * @throws IllegalArgumentException if <code>dockable</code> is not known 
     * to this frontend
     */
    public void setHideable( Dockable dockable, boolean hideable ){
        DockInfo info = getInfo( dockable );
        if( info == null )
            throw new IllegalArgumentException( "Dockable not registered" );
        
        if( info.isHideable() != hideable ){
            info.setHideable( hideable );
            fireHideable( dockable, hideable );
        }
    }
    
    /**
     * Sets whether to show the hide-action or not. That property only affects
     * the elements visible to the user, not the logic how to handle Dockables.
     * This property is useful for clients which supply their own action 
     * (which might invoke {@link #hide(Dockable) hide}).
     * @param show whether to show the action
     * @see #setHideable(Dockable, boolean)
     */
    public void setShowHideAction( boolean show ){
    	if( showHideAction != show ){
    		showHideAction = show;
    		
    		for( DockInfo info : dockables.values() )
    			info.updateHideAction();
    	}
    }
    
    /**
     * Tells whether the hide-action is shown or not.
     * @return <code>true</code> if the action is shown on 
     * {@link #isHideable(Dockable) hideable} dockables or <code>false</code>
     * otherwise
     */
    public boolean isShowHideAction(){
    	return showHideAction;
    }
    
    /**
     * Sets the default value for {@link #setEntryLayout(Dockable, boolean)}.
     * This default value is stored as soon as the identifier of a {@link Dockable}
     * becomes known and will not be affected by further changes of the default
     * value.
     * @param defaultEntryLayout whether the contents of {@link Dockable}s should
     * be stored in <code>entry</code> {@link Setting}s or not
     * @see #getSetting(boolean)
     */
    public void setDefaultEntryLayout( boolean defaultEntryLayout ) {
        this.defaultEntryLayout = defaultEntryLayout;
    }
    
    /**
     * Gets the default value of {@link #isEntryLayout(Dockable)}.
     * @return the default value
     */
    public boolean isDefaultEntryLayout() {
        return defaultEntryLayout;
    }
    
    /**
     * Sets whether the layout of <code>dockable</code> should be stored
     * for <code>entry</code> {@link Setting}s.
     * @param dockable the element whose state is to be set
     * @param layout the new state
     * @throws IllegalArgumentException if <code>dockable</code> is not
     * known
     * @see #getSetting(boolean)
     */
    public void setEntryLayout( Dockable dockable, boolean layout ){
        DockInfo info = getInfo( dockable );
        if( info == null )
            throw new IllegalArgumentException( "dockable not registered" );
        
        info.setEntryLayout( layout );
    }
    
    /**
     * Sets whether the layout of <code>id</code> should be stored
     * for <code>entry</code> {@link Setting}s.
     * @param id the id of the element whose state is to be changed
     * @param layout the new state
     * @throws IllegalArgumentException if <code>id</code> is not
     * known
     * @see #getSetting(boolean)
     */
    public void setEntryLayout( String id, boolean layout ){
        DockInfo info = getInfo( id );
        if( info == null )
            throw new IllegalArgumentException( "no entry present for: " + id );
        
        info.setEntryLayout( layout );
    }
    
    /**
     * Tells whether the layout of <code>dockable</code> should be stored
     * for <code>entry</code> {@link Setting}s.
     * @param dockable the element whose state is asked
     * @return the state
     * @throws IllegalArgumentException if <code>dockable</code> is not known
     * @see #getSetting(boolean)
     */
    public boolean isEntryLayout( Dockable dockable ){
        DockInfo info = getInfo( dockable );
        if( info == null )
            throw new IllegalArgumentException( "dockable not registered" );
        
        return info.isEntryLayout();
    }

    /**
     * Tells whether the layout of <code>id</code> should be stored
     * for <code>entry</code> {@link Setting}s.
     * @param id the identifier of an element whose state is requested
     * @return the state
     * @throws IllegalArgumentException if <code>id</code> is not known
     * @see #getSetting(boolean)
     */
    public boolean isEntryLayout( String id ){
        DockInfo info = getInfo( id );
        if( info == null )
            throw new IllegalArgumentException( "no entry present for: " + id );
        
        return info.isEntryLayout();
    }
    
    /**
     * Ensures that <code>dockable</code> is child of a root known to this
     * frontend.
     * @param dockable the element which should be made visible
     * @throws IllegalStateException if the {@link #getDefaultStation() default station} is
     * needed but can't be found 
     */
    public void show( Dockable dockable ){
        show( dockable, true );
    }
    
    /**
     * Ensures that <code>dockable</code> is child of a root known to this
     * frontend.
     * @param dockable the element which should be made visible
     * @param cancelable whether a {@link VetoableDockFrontendListener} can 
     * cancel the operation or not
     * @throws IllegalStateException if the {@link #getDefaultStation() default station} is
     * needed but can't be found 
     */
    public void show( Dockable dockable, boolean cancelable ){
        try{
            onAutoFire++;
        
            if( isHidden( dockable )){
                if( fireAllShowing( dockable, cancelable ))
                    return;
                
                DockInfo info = getInfo( dockable );
            	if( info == null ){
            		DockStation station = getDefaultStation();
            		if( station == null )
                		throw new IllegalStateException( "Can't find the default station" );
            		station.drop( dockable );
            	}
            	else{
            		String root = info.getRoot();
                    DockableProperty location = info.getLocation();
                    
                    DockStation station;
                    if( root == null )
                        station = getDefaultStation();
                    else
                        station = getRoot( root );
                    
                    if( station == null ){
                    	station = getDefaultStation();
                    	if( station == null )
                    		throw new IllegalStateException( "Can't find the default station" );
                    }
                    
                    if( location == null )
                        getDefaultStation().drop( dockable );
                    else{
                        if( !station.drop( dockable, location ))
                            getDefaultStation().drop( dockable );
                    }
            	}
            	
            	fireAllShown( dockable, null );
            	fireAllShown( dockable, true );
            }
        }
        finally{
            onAutoFire--;
        }
    }
    
    /**
     * Makes <code>dockable</code> invisible. The location of <code>dockable</code>
     * is saved, and if made visible again, it will reappear at its old location.
     * @param dockable the element which should be hidden
     */
    public void hide( Dockable dockable ){
        hide( dockable, true );
    }

    /**
     * Makes <code>dockable</code> invisible. The location of <code>dockable</code>
     * is saved, and if made visible again, it will reappear at its old location.
     * @param dockable the element which should be hidden
     * @param cancelable whether a {@link VetoableDockFrontendListener} can cancel
     * the operation or not
     */
    public void hide( Dockable dockable, boolean cancelable ){
        try{
            onAutoFire++;
            if( isShown( dockable )){
                if( dockable.getDockParent() == null || !fireAllHiding( dockable, cancelable )){
                    DockInfo info = getInfo( dockable );

                    if( info != null ){
                        info.updateLocation();
                    }

                    if( dockable.getDockParent() != null ){
                        dockable.getDockParent().drag( dockable );
                        fireAllHidden( dockable, null );
                        fireAllHidden( dockable, true );
                    }
                }
            }
        }
        finally{
            onAutoFire--;
        }
    }

    /**
     * Saves the current layout under the name of the {@link #getCurrentSetting() current setting}.
     * @throws IllegalStateException if the name of the current setting is <code>null</code>
     */
    public void save(){
        if( currentSetting == null )
            throw new IllegalStateException( "No setting loaded yet" );
        
        save( currentSetting );
    }
    
    /**
     * Saves the current layout with the specified name.
     * @param name the name for the setting
     */
    public void save( String name ){
    	if( name == null )
    		throw new IllegalArgumentException( "name must not be null" );
    	
    	Setting setting = getSetting( true );
    	settings.put( name, setting );
        currentSetting = name;
        fireSaved( name );
    }
    
    /**
     * Loads a setting of this frontend.
     * @param name the name of the setting
     * @throws IllegalArgumentException if no setting <code>name</code> could be found
     */
    public void load( String name ){
        if( name == null )
    		throw new IllegalArgumentException( "name must not be null" );
    	
    	Setting setting = settings.get( name );
        if( setting == null )
            throw new IllegalArgumentException( "Unknown setting \""+ name +"\"");
        
        currentSetting = name;
        
        setSetting( setting, true );
        
        fireLoaded( name );
    }
    
    /**
     * Creates a new {@link Setting} which describes the current set of
     * properties of this frontend. The setting contains information about
     * the location of each {@link Dockable}.
     * @param entry <code>true</code> if only the information for an ordinary
     * entry should be stored, <code>false</code> if the setting should contain
     * as much information as possible.
     * @return the setting
     * @see #createSetting()
     */
    public Setting getSetting( boolean entry ){
        Setting setting = createSetting();
        
        DockSituation situation = createSituation( entry );
        
        for( RootInfo info : roots.values() ){
            DockLayoutComposition layout = situation.convert( info.getStation() );
            setting.putRoot( info.getName(), layout );
        }
        
        for( DockInfo info : dockables.values() ){
            Dockable dockable = info.getDockable();
            
            if( dockable == null || dockable.getController() == null ){
                DockLayoutComposition layout = null;
                if( !entry || info.isEntryLayout() ){
                    if( dockable != null ){
                        layout = situation.convert( dockable );
                    }
                    else{
                        layout = info.getLayout();
                    }
                }
                
                setting.addInvisible( info.getKey(), info.getRoot(), layout, info.getLocation() );
            }
        }
        
        return setting;
    }
    
    /**
     * Changes the content of all root-stations according to <code>setting</code>.<br>
     * This method may add new {@link #addEmpty(String) empty infos} if it finds
     * information for a non existing, non empty {@link Dockable} but whose
     * key passes the methods of {@link MissingDockableStrategy}.
     * @param setting a new set of properties
     * @param entry <code>true</code> if only information for an ordinary
     * entry should be extracted, <code>false</code> if as much information
     * as possible should be extracted. The value of this argument should
     * be the same as was used when {@link #getSetting(boolean)} was called.
     */
    public void setSetting( Setting setting, boolean entry ){
        if( entry ){
            lastAppliedEntrySetting = setting;
        }
        else{
            lastAppliedEntrySetting = null;
            lastAppliedFullSetting = setting;
        }
        
        try{
            onAutoFire++;
            controller.getRegister().setStalled( true );
            
            DockSituation situation = createSituation( entry );
            
            DockSituationIgnore ignore = situation.getIgnore();
            if( ignore == null ){
                ignore = new DockSituationIgnore(){
                    public boolean ignoreChildren( DockStation station ) {
                        return false;
                    }
                    public boolean ignoreElement( DockElement element ) {
                        return false;
                    }
                };
            }
            
            clean( ignore );
            
            for( RootInfo info : roots.values() ){
                DockLayoutComposition layout = setting.getRoot( info.getName() );
                if( layout != null ){
                    layout = situation.fillMissing( layout );
                    
                    Map<String, DockableProperty> missingLocations = 
                        listEstimateLocations( situation, layout );
                    if( missingLocations != null ){
                        for( Map.Entry<String, DockableProperty> missing : missingLocations.entrySet() ){
                            String key = missing.getKey();
                            DockInfo dockInfo = getInfo( key );
                            
                            if( dockInfo == null && missingDockable.shouldStoreShown( key )){
                                addEmpty( key );
                                dockInfo = getInfo( key );
                            }
                            
                            if( dockInfo != null ){
                                dockInfo.setLocation( info.getName(), missing.getValue() );
                                dockInfo.setShown( true );
                            }
                        }
                    }
                    
                    Map<String, DockLayoutComposition> missingLayouts = listLayouts( situation, layout );
                    
                    if( missingLayouts != null ){
                        for( Map.Entry<String, DockLayoutComposition> missing : missingLayouts.entrySet() ){
                            String key = missing.getKey();
                            DockInfo dockInfo = getInfo( key );
                            
                            if( dockInfo == null && missingDockable.shouldStoreShown( key )){
                                addEmpty( key );
                                dockInfo = getInfo( key );
                            }
                            
                            if( dockInfo != null ){
                                dockInfo.setShown( true );
                                if( !entry || dockInfo.isEntryLayout() ){
                                    dockInfo.setLayout( missing.getValue() );
                                }
                            }
                        }
                        
                    }
                    
                    situation.convert( layout );
                }
            }
            
            for( int i = 0, n = setting.getInvisibleCount(); i<n; i++ ){
                String key = setting.getInvisibleKey( i );
                DockInfo info = getInfo( key );
                
                if( info == null && missingDockable.shouldStoreHidden( key )){
                    addEmpty( key );
                    info = getInfo( key );
                }
                
                if( info != null ){
                    info.setShown( false );
                    info.setLocation( 
                            setting.getInvisibleRoot( i ), 
                            setting.getInvisibleLocation( i ) );
                    
                    DockLayoutComposition layout = setting.getInvisibleLayout( i );
                    if( layout != null ){
                        layout = situation.fillMissing( layout );
                    }
                    if( info.getDockable() != null ){
                        situation.convert( layout );
                        layout = null;
                    }
                    info.setLayout( layout );
                }
            }
        }
        catch( IOException e ){
            throw new IllegalArgumentException( "Cannot set Setting", e );
        }
        catch( XException e ){
            throw new IllegalArgumentException( "Cannot set Setting", e );
        }
        finally{
            onAutoFire--;
            controller.getRegister().setStalled( false );
        }
        
        for( DockInfo info : dockables.values() ){
            if( info.getDockable() != null && !info.isHideable() && isHidden( info.getDockable() )){
                show( info.getDockable() );
            }
        }
    }
    
    /**
     * Tries to fill gaps in the layout information.
     * <ul>
     *  <li>Tries to read empty {@link DockInfo}s which have a layout and a position.</li>
     *  <li>Tries to replace raw data in {@link DockLayoutComposition}s</li>
     * </ul>
     */
    private void fillMissing(){
        DockSituation situation = createSituation( false );
        
        // fill gaps
        for( Setting setting : settings.values() ){
            setting.fillMissing( situation );
        }
        
        if( lastAppliedFullSetting != null && !settings.containsValue( lastAppliedFullSetting )){
            lastAppliedFullSetting.fillMissing( situation );
        }
        
        if( lastAppliedEntrySetting != null && lastAppliedEntrySetting != lastAppliedFullSetting && !settings.containsValue( lastAppliedEntrySetting )){
            lastAppliedEntrySetting.fillMissing( situation );
        }
        
        // try fill in missing dockables which have a name
        List<FrontendEntry> entries = listFrontendEntries();
        
        for( FrontendEntry entry : entries ){
            if( entry.getDockable() == null && entry.getLayout() != null ){
                DockElement element = situation.convert( entry.getLayout() );
                Dockable dockable = element == null ? null : element.asDockable();
                if( dockable != null ){
                    entry.setLayout( null );
                    addDockable( entry.getKey(), dockable );
                }
            }
        }
    }
    
    /**
     * Tries to locate and create those {@link Dockable}s for which location 
     * information can be found in the last applied {@link Setting} and which
     * use the newly added factory <code>factory</code>.
     * @param factory the new factory
     */
    private void fillMissing( DockFactory<?,?> factory ){
        fillMissing();
        
        Setting last = getLastAppliedEntrySetting();
        boolean entry = true;
        
        if( last == null ){
            last = getLastAppliedFullSetting();
            entry = false;
        }
        
        if( last == null ){
            return;
        }
        
        DockSituation situation = createSituation( entry );
        String factoryId = situation.convertFactoryId( factory );
        
        for( String root : roots.keySet() ){
            DockLayoutComposition composition = last.getRoot( root );
            if( composition != null ){
                estimateLocations( situation, composition );
                fillMissing( root, composition, factory, factoryId );
            }
        }
    }
    
    /**
     * Searches for elements which can be created by the factory
     * <code>factory</code> and creates them.
     * @param root the root station
     * @param composition the composition to search in
     * @param factory the factory to look out for
     * @param factoryId the identifier of the factory, translated for the {@link DockLayoutComposition}
     */
    @SuppressWarnings("unchecked")
    private void fillMissing( String root, DockLayoutComposition composition, DockFactory<?, ?> factory, String factoryId ){
        DockLayoutInfo info = composition.getLayout();
        if( info.getKind() == DockLayoutInfo.Data.DOCK_LAYOUT ){
            if( info.getDataLayout().getFactoryID().equals( factoryId )){
                DockableProperty location = info.getLocation();
                if( location != null ){
                    DockFactory<DockElement, Object> normalizedFactory = (DockFactory<DockElement, Object>)factory;
                    if( missingDockable.shouldCreate( normalizedFactory, info.getDataLayout().getData() ) ){
                        DockElement element = normalizedFactory.layout( info.getDataLayout().getData() );
                        if( element != null ){
                            Dockable dockable = element.asDockable();
                            if( dockable != null ){
                                RootInfo rootInfo = roots.get( root );
                                if( !rootInfo.getStation().drop( dockable, location ) ){
                                    rootInfo.getStation().drop( dockable );
                                }
                            }
                        }
                    }
                }
            }
        }
        
        List<DockLayoutComposition> children = composition.getChildren();
        if( children != null ){
            for( DockLayoutComposition child : children ){
                fillMissing( root, child, factory, factoryId );
            }
        }
    }
    
    /**
     * Tries to estimate the location of missing {@link Dockable}s. The
     * default implementation works with any {@link PredefinedDockSituation}.
     * @param situation the situation to use for transforming information
     * @param layout the layout to analyze
     * @return a map with <code>Dockable</code>-names as key or <code>null</code>
     */
    protected Map<String, DockableProperty> listEstimateLocations( DockSituation situation, DockLayoutComposition layout ){
        if( situation instanceof PredefinedDockSituation ){
            Map<String, DockableProperty> map = ((PredefinedDockSituation)situation).listEstimatedLocations( layout, true );
            Map<String, DockableProperty> result = new HashMap<String, DockableProperty>();
            
            for( Map.Entry<String, DockableProperty> entry : map.entrySet() ){
                String key = entry.getKey();
                if( key.startsWith( DOCKABLE_KEY_PREFIX ))
                    result.put( key.substring( DOCKABLE_KEY_PREFIX.length() ), entry.getValue() );
                else if( key.startsWith( ROOT_KEY_PREFIX ))
                    result.put( key.substring( ROOT_KEY_PREFIX.length() ), entry.getValue() );
                else
                    result.put( key, entry.getValue() );
            }
            
            return result;
        }
        return null;
    }
    
    /**
     * Tries to fill the property {@link DockLayoutInfo#getLocation() location}
     * for each element in <code>layout</code>. The default implementation only
     * works if <code>situation</code> is an instance of {@link PredefinedDockSituation}.
     * @param situation the situation to use for transforming information
     * @param layout the layout to estimate
     */
    protected void estimateLocations( DockSituation situation, DockLayoutComposition layout ){
        if( situation instanceof PredefinedDockSituation ){
            ((PredefinedDockSituation)situation).estimateLocations( layout );
        }
    }
    
    /**
     * Tries to estimate the layouts of missing {@link Dockable}s. The
     * default implementation works with any {@link PredefinedDockSituation}.
     * @param situation the situation to use for transforming information
     * @param layout the layout to analyze
     * @return a map with <code>Dockable</code>-names as key or <code>null</code>
     */
    protected Map<String, DockLayoutComposition> listLayouts( DockSituation situation, DockLayoutComposition layout ){
        if( situation instanceof PredefinedDockSituation ){
            Map<String, DockLayoutComposition> map = ((PredefinedDockSituation)situation).listLayouts( layout, true );
            Map<String, DockLayoutComposition> result = new HashMap<String, DockLayoutComposition>();
            
            for( Map.Entry<String, DockLayoutComposition> entry : map.entrySet() ){
                String key = entry.getKey();
                if( key.startsWith( DOCKABLE_KEY_PREFIX ))
                    result.put( key.substring( DOCKABLE_KEY_PREFIX.length() ), entry.getValue() );
                else if( key.startsWith( ROOT_KEY_PREFIX ))
                    result.put( key.substring( ROOT_KEY_PREFIX.length() ), entry.getValue() );
                else
                    result.put( key, entry.getValue() );
            }
            
            return result;
        }
        return null;
    }
    
    /**
     * Gets a set of all {@link Dockable} which are known to this frontend
     * and which are visible.
     * @return the set of the visible elements
     */
    public Set<Dockable> listShownDockables(){
        Set<Dockable> set = new HashSet<Dockable>();
        for( DockInfo info : dockables.values() ){
            if( info.getDockable() != null && isShown( info.getDockable() )){
                set.add( info.getDockable() );
            }
        }
        return set;
    }
    
    /**
     * Gets a list of all {@link Dockable}s which are registered at this
     * frontend.
     * @return the list of elements
     */
    public List<Dockable> listDockables(){
    	List<Dockable> result = new ArrayList<Dockable>( dockables.size() );
    	for( DockInfo info : dockables.values() ){
    	    if( info.getDockable() != null ){
    	        result.add( info.getDockable() );
    	    }
    	}
    	
    	return result;
    }
    
    /**
     * Gets a list of all informations known of any {@link Dockable}
     * that is or might be registered at this frontend.
     * @return all known information. Changes to this list itself will not
     * affect this frontend, changes to the entries however might have
     * effects.
     */
    public List<FrontendEntry> listFrontendEntries(){
        return new ArrayList<FrontendEntry>( dockables.values() );
    }
    
    /**
     * Gets all the information known about the {@link Dockable} with
     * name <code>key</code>.
     * @param key some key of a dockable
     * @return all information known or <code>null</code> if nothing is available
     */
    public FrontendEntry getFrontendEntry( String key ){
        return dockables.get( key );
    }
    
    /**
     * Removes all child-parent relations expect the ones filtered out
     * by <code>ignore</code>.
     * @param ignore a filter, never <code>null</code>
     */
    protected void clean( DockSituationIgnore ignore ){
        for( RootInfo root : roots.values() ){
            if( !ignore.ignoreElement( root.getStation() )){
                clean( root.getStation(), ignore );
            }
        }
    }
    
    /**
     * Removes all recursively all children from <code>station</code>, but only
     * if the children are not filtered by <code>ignore</code>.
     * @param station a station to clean
     * @param ignore a filter
     */
    protected void clean( DockStation station, DockSituationIgnore ignore ){
        try{
            controller.getRegister().setStalled( true );

            if( !ignore.ignoreChildren( station ) ){
                for( int i = station.getDockableCount()-1; i >= 0; i-- ){
                    Dockable dockable = station.getDockable( i );
                    if( !ignore.ignoreElement( dockable )){
                        DockStation check = dockable.asDockStation();
                        if( check != null )
                            clean( check, ignore );
                        
                        station.drag( dockable );
                    }
                }
            }
        }
        finally{
            controller.getRegister().setStalled( false );
        }
    }
    
    /**
     * Deletes the setting with the given <code>name</code>.
     * @param name the name of the setting to delete
     * @return <code>true</code> if the setting was deleted, <code>false</code>
     * if the setting was unknown anyway.
     */
    public boolean delete( String name ){
    	if( name == null )
    		throw new IllegalArgumentException( "name must not be null" );
    	
        boolean deleted = settings.remove( name ) != null;
        if( deleted ){
        	if( name.equals( currentSetting ))
        		currentSetting = null;
        	
            fireDeleted( name );
        }
        return deleted;
    }
    
    /**
     * Writes all settings of this frontend, including the current layout,
     * into <code>out</code>.
     * @param out the stream to write into
     * @throws IOException if there are any problems
     */
    public void write( DataOutputStream out ) throws IOException{
        Version.write( out, Version.VERSION_1_0_4 );
        
        if( currentSetting == null )
            out.writeBoolean( false );
        else{
            out.writeBoolean( true );
            out.writeUTF( currentSetting );
        }
        
        out.writeInt( settings.size() );
        for( Map.Entry<String, Setting> setting : settings.entrySet() ){
            out.writeUTF( setting.getKey() );
            write( setting.getValue(), true, out );
        }
        
        write( getSetting( false ), false, out );
    }
    
    /**
     * Calls {@link Setting#write(DockSituation, PropertyTransformer, boolean, DataOutputStream)}
     * @param setting the setting which will be written
     * @param entry whether <code>setting</code> is an ordinary entry, or
     * the finall setting that contains more data. 
     * @param out the stream to write into
     * @throws IOException if an I/O-error occurs
     */
    protected void write( Setting setting, boolean entry, DataOutputStream out ) throws IOException{
        DockSituation situation = createSituation( entry );
        PropertyTransformer properties = createTransformer();
        setting.write( situation, properties, entry, out );
    }
    
    /**
     * Reads the settings of this frontend from <code>in</code>. The layout
     * will be changed according to the contents that are read.
     * @param in the stream to read from
     * @throws IOException if there are any problems
     */
    public void read( DataInputStream in ) throws IOException{
        Version version = Version.read( in );
        version.checkCurrent();
        
        if( in.readBoolean() )
            currentSetting = in.readUTF();
        else
            currentSetting = null;
        
        int count = in.readInt();
        for( int i = 0; i < count; i++ ){
            String key = in.readUTF();
            Setting setting = read( true, in );
            settings.put( key, setting );
            fireRead( key );
        }
        
        setSetting( read( false, in ), false );
    }
    
    /**
     * Calls first {@link #createSetting()} and then
     * {@link Setting#read(DockSituation, PropertyTransformer, boolean, DataInputStream)}.
     * @param entry whether the set of properties is used as ordinary entry,
     * or contains more data than usuall.
     * @param in the stream to read from
     * @return the new setting
     * @throws IOException if an I/O-error occurs
     * @see #createSetting()
     */
    protected Setting read( boolean entry, DataInputStream in ) throws IOException{
        Setting setting = createSetting();
        DockSituation situation = createSituation( entry );
        PropertyTransformer properties = createTransformer();
        setting.read( situation, properties, entry, in );
        return setting;
    }
    
    /**
     * Writes all properties of this frontend into an xml element.
     * @param element the element to write into, this method will not
     * change the attributes of <code>element</code>
     */
    public void writeXML( XElement element ){
        if( !settings.isEmpty() ){
            XElement xsettings = element.addElement( "settings" );
            for( Map.Entry<String, Setting> setting : settings.entrySet() ){
                XElement xsetting = xsettings.addElement( "setting" );
                xsetting.addString( "name", setting.getKey() );
                writeXML( setting.getValue(), true, xsetting );
            }
        }
        
        XElement xcurrent = element.addElement( "current" );
        if( currentSetting != null )
            xcurrent.addString( "name", currentSetting );
        
        writeXML( getSetting( false ), false, xcurrent );
    }
    
    /**
     * Calls {@link Setting#writeXML(DockSituation, PropertyTransformer, boolean, XElement)}.
     * @param setting the setting to write
     * @param entry whether <code>setting</code> is an ordinary entry, or
     * the finall setting that contains more data.
     * @param element the xml element to write into, this method does not
     * change the attributes of the entry
     */
    protected void writeXML( Setting setting, boolean entry, XElement element ){
        DockSituation situation = createSituation( entry );
        PropertyTransformer properties = createTransformer();
        setting.writeXML( situation, properties, entry, element );
    }
    
    /**
     * Reads the contents of this frontend from an xml element.
     * @param element the element to read
     */
    public void readXML( XElement element ){
        XElement xsettings = element.getElement( "settings" );
        if( xsettings != null ){
            for( XElement xsetting : xsettings.getElements( "setting" )){
                String key = xsetting.getString( "name" );
                Setting setting = readXML( true, xsetting );
                settings.put( key, setting );
                fireRead( key );
            }
        }
        
        XElement xcurrent = element.getElement( "current" );
        if( xcurrent != null ){
            XAttribute xname = xcurrent.getAttribute( "name" );
            if( xname != null )
                currentSetting = xname.getString();
            
            setSetting( readXML( false, xcurrent), false );
        }
    }
    
    /**
     * Calls {@link #createSetting()} and then
     * {@link Setting#readXML(DockSituation, PropertyTransformer, boolean, XElement)}.
     * @param entry whether the set of properties is used as ordinary entry,
     * or contains more data than usuall.
     * @param element the xml element containg the data for the new setting
     * @return the new setting
     * @see #createSetting()
     */
    protected Setting readXML( boolean entry, XElement element ){
        Setting setting = createSetting();
        DockSituation situation = createSituation( entry );
        PropertyTransformer properties = createTransformer();
        setting.readXML( situation, properties, entry, element );
        return setting;
    }
    
    /**
     * Invoked every time before the current setting is written into a stream.<br>
     * Note: the frontend and the file formats heavily depend on the internal
     * implementation of this method. Overriding this method is possible, but
     * extreme care should be applied when doing so. A good solution would be
     * to call {@link #createInternalSituation(boolean)}, then change and return
     * the result of that method.<br>
     * This method just calls <code>return createInternalSituation( entry );</code>.<br>
     * Subclasses overridding this method may also need to override 
     * {@link #listEstimateLocations(DockSituation, DockLayoutComposition)}
     * and {@link #listLayouts(DockSituation, DockLayoutComposition)}
     * @param entry <code>true</code> if the situation is used for a regular setting,
     * <code>false</code> if the situation is used as the final setting which will
     * be loaded the next time the application starts.
     * @return the situation
     */
    @SuppressWarnings("unchecked")
    protected DockSituation createSituation( boolean entry ){
        return createInternalSituation( entry );
    }
    
    /**
     * Creates a {@link DockSituation} which represents all the knowledge this
     * frontend currently has. This method is declared final, clients should
     * override {@link #createSituation(boolean)} if they need to introduce their
     * own implementation of {@link DockSituation}.
     * @param entry <code>true</code> if the situation is used for a regular setting,
     * <code>false</code> if the situation is used as the final setting which will
     * be loaded the next time the application starts.
     * @return the situation
     */
    @SuppressWarnings("unchecked")
    protected final PredefinedDockSituation createInternalSituation( final boolean entry ){
        PredefinedDockSituation situation = new PredefinedDockSituation(){
            @Override
            protected boolean shouldLayout( DockElement element ) {
                if( entry ){
                    Dockable dockable = element.asDockable();
                    if( dockable != null ){
                        DockInfo info = getInfo( dockable );
                        if( info != null ){
                            info.isEntryLayout();
                        }
                    }
                }
                
                return true;
            }
        };
        
        for( DockInfo info : dockables.values() ){
            if( info.getDockable() != null ){
                situation.put( DOCKABLE_KEY_PREFIX + info.getKey(), info.getDockable() );
            }
        }
        
        for( RootInfo info : roots.values() ){
            situation.put( ROOT_KEY_PREFIX + info.getName(), info.getStation() );
        }
        
        for( DockFactory<?,?> factory : dockFactories ){
            situation.add( factory );
        }
        
        for( DockFactory backup : backupDockFactories ){
            situation.addBackup( new RegisteringDockFactory( this, backup ) );
        }
        
        for( AdjacentDockFactory<?> factory : adjacentDockFactories ){
            situation.addAdjacent( factory );
        }
        
        if( entry )
        	situation.setIgnore( getIgnoreForEntry() );
        else
        	situation.setIgnore( getIgnoreForFinal() );
        
        return situation;
    }
    
    /**
     * Invoked every time before properties are written or read.
     * @return a transformer to read or write properties.
     */
    protected PropertyTransformer createTransformer(){
        PropertyTransformer transformer = new PropertyTransformer();
        for( DockablePropertyFactory factory : propertyFactories )
            transformer.addFactory( factory );
        return transformer;
    }
    
    /**
     * Creates the action that is added to all known dockables, and which
     * is called the "close"-action.
     * @return the action
     */
    protected Hider createHider() {
		return new Hider();
	}
    
    /**
     * Creates a bag that contains all information needed to describe the 
     * current set of properties.
     * @return the new bag
     */
    protected Setting createSetting(){
        return new Setting();
    }
    
    /**
     * Gets the action which is added to all known Dockables, and which is
     * called the "close"-action. Clients may use this method set another
     * text, icon, ... to the action.
     * @return the action
     */
    public Hider getHider() {
		return hider;
	}
    
    /**
     * Gets the information about <code>dockable</code>.
     * @param dockable the element whose states are asked
     * @return the states or <code>null</code>
     */
    private DockInfo getInfo( Dockable dockable ){
        if( dockable == null )
            throw new NullPointerException( "dockable is null" );
        
        for( DockInfo info : dockables.values() )
            if( info.getDockable() == dockable )
                return info;
        
        return null;
    }
    
    /**
     * Gets the information for the element with the designated name.
     * @param name the name of the element whose states are asked
     * @return the states or <code>null</code>
     */
    private DockInfo getInfo( String name ){
        return dockables.get( name );
    }
    
    /**
     * Gets information about the root <code>station</code>.
     * @param station a root
     * @return the information or <code>null</code>
     */
    private RootInfo getRoot( DockStation station ){
        for( RootInfo info : roots.values() )
            if( info.getStation() == station )
                return info;
        
        return null;
    }
    
    /**
     * Gets an independent array containing all currently registered listeners. 
     * @return the array of listeners
     */
    protected DockFrontendListener[] listeners(){
        return listeners.toArray( new DockFrontendListener[ listeners.size() ]);
    }
    
    /**
     * Gets an independent array containing all currently registered
     * {@link VetoableDockFrontendListener}s.
     * @return the array of listeners
     */
    protected VetoableDockFrontendListener[] vetoableListeners(){
        return vetoableListeners.toArray( new VetoableDockFrontendListener[ vetoableListeners.size() ] );
    }
    
    /**
     * Calls the method {@link VetoableDockFrontendListener#hiding(VetoableDockFrontendEvent)}
     * for all elements in the tree beginning with <code>dockable</code>.
     * @param dockable the root of the tree of elements to remove
     * @param cancelable whether the operation can be aborted
     * @return <code>true</code> if the operation was aborted, <code>false</code>
     * if not.
     */
    protected boolean fireAllHiding( Dockable dockable, final boolean cancelable ){
        if( vetoableListeners.size() == 0 )
            return false;
        
        final Single<Boolean> result = new Single<Boolean>( false );
        
        DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
            @Override
            public void handleDockable( Dockable dockable ) {
                result.setA( fireHiding( dockable, result.getA(), cancelable ) );
            }
        });
        
        return result.getA();
    }
    
    /**
     * Invokes the method {@link VetoableDockFrontendListener#hiding(VetoableDockFrontendEvent)}
     * until no listeners remain.
     * @param dockable the element for which to invoke the listeners 
     * @param canceled whether the operation already is aborted
     * @param cancelable whether the operation can be aborted
     * @return <code>true</code> if the operation is aborted
     */
    protected boolean fireHiding( Dockable dockable, boolean canceled, boolean cancelable ){
        VetoableDockFrontendEvent event = new VetoableDockFrontendEvent( this, dockable, cancelable, true );
        if( canceled )
            event.cancel();
        
        for( VetoableDockFrontendListener listener : vetoableListeners() ){
            listener.hiding( event );
        }
        return event.isCanceled();
    }
    
    /**
     * Invokes the method {@link VetoableDockFrontendListener#hidden(VetoableDockFrontendEvent)}
     * for all listeners for all elemets of the tree with root <code>dockable</code>.
     * @param dockable the element that was hidden
     * @param expected whether this event was expected or unexpected
     */
    protected void fireAllHidden( Dockable dockable, final boolean expected ){
        DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
            @Override
            public void handleDockable( Dockable dockable ) {
                fireHidden( dockable, expected );
            }
        });
    }
    
    /**
     * Invokes the method {@link VetoableDockFrontendListener#hidden(VetoableDockFrontendEvent)}
     * for all listeners.
     * @param dockable the element that was hidden
     * @param expected whether this event was expected or unexpected
     */
    protected void fireHidden( Dockable dockable, boolean expected ){
        VetoableDockFrontendEvent event = new VetoableDockFrontendEvent( this, dockable, false, expected );
        for( VetoableDockFrontendListener listener : vetoableListeners() )
            listener.hidden( event );
    }
    
    /**
     * Invokes the method {@link DockFrontendListener#hidden(DockFrontend, Dockable)}
     * on all listeners for <code>dockable</code> and all its children.
     * @param dockable the hidden element
     * @param processed Set of {@link Dockable}s for which the event is already fired,
     * will be modified by this method, can be <code>null</code>
     */
    protected void fireAllHidden( Dockable dockable, final Set<Dockable> processed ){
        DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
            @Override
            public void handleDockable( Dockable dockable ) {
                if( processed == null || processed.add( dockable )){
                    fireHidden( dockable );
                    
                    DockInfo info = getInfo( dockable );
                    if( info != null ){
                        info.setShown( false );
                    }
                }
            }
        });
    }
    
    /**
     * Invokes the method {@link DockFrontendListener#hidden(DockFrontend, Dockable)}
     * on all listeners.
     * @param dockable the hidden element
     */
    protected void fireHidden( Dockable dockable ){
        for( DockFrontendListener listener : listeners() )
            listener.hidden( this, dockable );
    }
    
    /**
     * Invokes the method {@link DockFrontendListener#added(DockFrontend, Dockable)}
     * on all listeners.
     * @param dockable the added element
     */
    protected void fireAdded( Dockable dockable ){
        for( DockFrontendListener listener : listeners() )
            listener.added( this, dockable );
    }

    /**
     * Invokes the method {@link DockFrontendListener#hideable(DockFrontend, Dockable, boolean)}
     * on all listeners.
     * @param dockable the element whose state changed
     * @param value the new state
     */
    protected void fireHideable( Dockable dockable, boolean value ){
        for( DockFrontendListener listener : listeners() )
            listener.hideable( this, dockable, value );
    }
    
    /**
     * Invokes the method {@link DockFrontendListener#removed(DockFrontend, Dockable)}
     * on all listeners.
     * @param dockable the removed element
     */
    protected void fireRemoved( Dockable dockable ){
        for( DockFrontendListener listener : listeners() )
            listener.removed( this, dockable );
    }
    
    /**
     * Calls {@link VetoableDockFrontendListener#showing(VetoableDockFrontendEvent)}
     * for all elements in the tree with root <code>dockable</code>.
     * @param dockable the root of the tree that will become visible
     * @param cancelable whether the operation can be canceled
     * @return <code>true</code> if the operation was aborted, <code>false</code>
     * if the operation can continue
     */
    protected boolean fireAllShowing( Dockable dockable, final boolean cancelable ){
        if( vetoableListeners.size() == 0 )
            return false;
        
        final Single<Boolean> cancel = new Single<Boolean>( false );
        
        DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
            @Override
            public void handleDockable( Dockable dockable ) {
                cancel.setA( fireShowing( dockable, cancel.getA(), cancelable ));    
            }
        });
        
        return cancel.getA();
    }
    
    /**
     * Calls {@link VetoableDockFrontendListener#showing(VetoableDockFrontendEvent)}
     * for <code>dockable</code>.
     * @param dockable the element that will be shown
     * @param canceled whether the operation is already canceled
     * @param cancelable whether the operation can be canceled
     * @return whether the operation was aborted
     */
    protected boolean fireShowing( Dockable dockable, boolean canceled, boolean cancelable ){
        VetoableDockFrontendEvent event = new VetoableDockFrontendEvent( this, dockable, cancelable, true );
        
        if( canceled )
            event.cancel();
        
        for( VetoableDockFrontendListener listener : vetoableListeners() ){
            listener.showing( event );
        }
        
        return event.isCanceled();
    }
    
    /**
     * Invokes the method {@link VetoableDockFrontendListener#shown(VetoableDockFrontendEvent)}
     * for all elements in the tree with root <code>dockable</code>.
     * @param dockable the root of the tree that is shown
     * @param expected whether the event is expected or not
     */
    protected void fireAllShown( Dockable dockable, final boolean expected ){
        if( vetoableListeners.size() == 0 )
            return;
        
        DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
            @Override
            public void handleDockable( Dockable dockable ) {
                fireShown( dockable, expected );
            }
        });
    }
    
    /**
     * Invokes the method {@link VetoableDockFrontendListener#shown(VetoableDockFrontendEvent)}
     * on all listeners.
     * @param dockable the element that was shown
     * @param expected whether the event was expected or not
     */
    protected void fireShown( Dockable dockable, boolean expected ){
        VetoableDockFrontendEvent event = new VetoableDockFrontendEvent( this, dockable, false, expected );
        for( VetoableDockFrontendListener listener : vetoableListeners() )
            listener.shown( event );
    }

    /**
     * Invokes the method {@link DockFrontendListener#shown(DockFrontend, Dockable)}
     * on all listeners for <code>dockable</code> and all its children.
     * @param dockable the shown element
     * @param processed Set of {@link Dockable}s whose event is already fired,
     * will be modified by this method, can be <code>null</code>
     */
    protected void fireAllShown( Dockable dockable, final Set<Dockable> processed ){
        DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
            @Override
            public void handleDockable( Dockable dockable ) {
                if( processed == null || processed.add( dockable )){
                    fireShown( dockable );
                    
                    DockInfo info = getInfo( dockable );
                    if( info != null ){
                        info.setShown( true );
                    }
                }
            }
        });
    }
    
    /**
     * Invokes the method {@link DockFrontendListener#shown(DockFrontend, Dockable)}
     * on all listeners.
     * @param dockable the shown element
     */
    protected void fireShown( Dockable dockable ){
        for( DockFrontendListener listener : listeners() )
            listener.shown( this, dockable );
    }
    
    /**
     * Invokes the method {@link DockFrontendListener#saved(DockFrontend, String)}
     * on all listeners.
     * @param name the name of the saved setting
     */
    protected void fireSaved( String name ){
        for( DockFrontendListener listener : listeners() )
            listener.saved( this, name );
    }

    /**
     * Invokes the method {@link DockFrontendListener#loaded(DockFrontend, String)}
     * on all listeners.
     * @param name the name of the loaded setting
     */
    protected void fireLoaded( String name ){
        for( DockFrontendListener listener : listeners() )
            listener.loaded( this, name );
    }
    
    /**
     * Invokes the method {@link DockFrontendListener#read(DockFrontend, String)}
     * on all listeners.
     * @param name the name of the read setting
     */
    protected void fireRead( String name ){
        for( DockFrontendListener listener : listeners() )
            listener.read( this, name );
    }
    
    
    /**
     * Invokes the method {@link DockFrontendListener#deleted(DockFrontend, String)}
     * on all listeners.
     * @param name the name of the deleted setting
     */    
    protected void fireDeleted( String name ){
        for( DockFrontendListener listener : listeners() )
            listener.deleted( this, name );
    }

    
    /**
     * Information about a {@link Dockable}.
     * @author Benjamin Sigg
     */
    private class DockInfo implements FrontendEntry{
    	/** The element for which information is stored */
        private Dockable dockable;
        /** The name of the element */
        private String key;
        /** <code>true</code> if the element has a "close"-action, <code>false</code> otherwise */
        private boolean hideable;
        /** The {@link DockActionSource} which is used for {@link #dockable} */
        private DefaultDockActionSource source;
        
        /** The name of the root on which {@link #dockable} was, when it was made invisible */
        private String root;
        /** The location of {@link #dockable} on the station named {@link #root} */
        private DockableProperty location;
        
        /** if set, then every entry-Setting can store the layout of this element */
        private boolean entryLayout;
        /** Information about the layout of this {@link #dockable}, can be <code>null</code> */
        private DockLayoutComposition layout;
        /** Whether the hide-action is currently visible or not */
        private boolean hideActionVisible;
        
        /** whether {@link #dockable} is or should be shown */
        private boolean shown = false;
        
        /**
         * Creates a new DockInfo.
         * @param dockable the element whose informations are stored
         * @param key the name of the element
         */
        public DockInfo( Dockable dockable, String key ){
            this.dockable = dockable;
            this.key = key;
            
            entryLayout = defaultEntryLayout;
            
            source = new DefaultDockActionSource( new LocationHint( LocationHint.ACTION_GUARD, LocationHint.RIGHT_OF_ALL ));
            
            hideActionVisible = false;
            
            setHideable( defaultHideable );
        }
        
        public void setShown( boolean shown ) {
            this.shown = shown;
        }
        
        public boolean isShown() {
            return shown;
        }
        
        public boolean isEntryLayout() {
            return entryLayout;
        }
        
        public void setEntryLayout( boolean entryLayout ) {
            this.entryLayout = entryLayout;
        }
        
        public boolean isHideable() {
            return hideable;
        }
        
        public void setHideable( boolean hideable ) {
        	this.hideable = hideable;
        	updateHideAction();
        }

        /**
         * Updates the visibility-state of the hide action
         */
        public void updateHideAction(){
        	boolean shouldShow = hideable && showHideAction;
        	if( shouldShow != hideActionVisible ){
        		hideActionVisible = shouldShow;
        		if( shouldShow ){
        			source.add( hider );
        		}
        		else{
        			source.remove( hider );
        		}
        	}
        }
        
        /**
         * Gets the {@link DockActionSource} which will be added to the offers
         * of {@link #getDockable() the element}.
         * @return the additional source
         */
        public DefaultDockActionSource getSource() {
            return source;
        }
        
        public Dockable getDockable() {
            return dockable;
        }
        
        /**
         * Exchanges the dockable which is stored in this {@link DockInfo}
         * @param dockable the new dockable, can be <code>null</code>
         */
        public void setDockable( Dockable dockable ) {
            this.dockable = dockable;
        }
        
        public String getKey() {
            return key;
        }
        
        /**
         * Updates the values of {@link #getRoot() root} and {@link #getLocation() location}
         * according to the current location of {@link #getDockable() the element}.
         */
        public void updateLocation(){
            DockStation station = DockUtilities.getRoot( dockable );
            if( station == null )
                return;
            
            RootInfo info = DockFrontend.this.getRoot( station );
            if( info == null )
                return;
            
            root = info.getName();
            location = DockUtilities.getPropertyChain( station, dockable );
        }
        
        public void setLocation( String root, DockableProperty location ){
            this.root = root;
            this.location = location;
        }
        
        public String getRoot() {
            return root;
        }
        
        public DockableProperty getLocation() {
            return location;
        }
        
        public void setLayout( DockLayoutComposition layout ) {
            this.layout = layout;
        }
        
        public DockLayoutComposition getLayout() {
            return layout;
        }
    }
    
    /**
     * Stores information about a root-station.
     * @author Benjamin Sigg
     */
    private static class RootInfo{
    	/** the root */
        private DockStation station;
        /** the name of the root */
        private String name;
        
        /**
         * Creates a new object.
         * @param station the root
         * @param name the name of the root
         */
        public RootInfo( DockStation station, String name ){
            this.name = name;
            this.station = station;
        }
        
        /**
         * Gets the name of the station stored in this object.
         * @return the name
         */
        public String getName() {
            return name;
        }
        
        /**
         * Gets the root-station.
         * @return the root
         */
        public DockStation getStation() {
            return station;
        }
    }
    
    /**
     * An object which is action and {@link ActionGuard} at the same time. The
     * action is always to invoke {@link DockFrontend#hide(Dockable) hide} of
     * the enclosing a {@link DockFrontend}. The guard reacts on all 
     * {@link Dockable Dockables} which are known to the enclosing frontend.
     * @author Benjamin Sigg
     */
    @EclipseTabDockAction
    public class Hider extends SimpleButtonAction implements ActionGuard, IconManagerListener{
    	/**
         * Creates a new action/guard.
         */
        public Hider(){
        	setText( DockUI.getDefaultDockUI().getString( "close" ));
            setTooltip( DockUI.getDefaultDockUI().getString( "close.tooltip" ));
            
            controller.getIcons().add( "close", this );
            setIcon( controller.getIcons().getIcon( "close" ));
            
            PropertyValue<KeyStroke> stroke = new PropertyValue<KeyStroke>( HIDE_ACCELERATOR ){
                @Override
                protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
                    setAccelerator( newValue );
                }
            };
            stroke.setProperties( controller );
            setAccelerator( stroke.getValue() );
        }
        
        public void iconChanged( String key, Icon icon ) {
            setIcon( icon );
        }
        
        public DockActionSource getSource( Dockable dockable ) {
        	DockInfo info = getInfo( dockable );
        	if( info == null ){
        		return new DefaultDockActionSource( 
        				new LocationHint( LocationHint.ACTION_GUARD, LocationHint.RIGHT_OF_ALL ),
        				this );
        	}
        	else{
        		return info.getSource();
        	}
        }

        public boolean react( Dockable dockable ) {
        	DockInfo info = getInfo( dockable );
            return info != null;
        }

        @Override
        public void action( Dockable dockable ) {
            hide( dockable );
        }
    }
}
