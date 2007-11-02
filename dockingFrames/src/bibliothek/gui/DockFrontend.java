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
import java.io.*;
import java.util.*;

import javax.swing.Icon;

import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabDockAction;
import bibliothek.gui.dock.*;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.event.DockFrontendListener;
import bibliothek.gui.dock.event.IconManagerListener;
import bibliothek.gui.dock.station.ScreenDockStation;
import bibliothek.gui.dock.station.flap.FlapDockPropertyFactory;
import bibliothek.gui.dock.station.flap.FlapDockStationFactory;
import bibliothek.gui.dock.station.screen.ScreenDockPropertyFactory;
import bibliothek.gui.dock.station.screen.ScreenDockStationFactory;
import bibliothek.gui.dock.station.split.SplitDockPropertyFactory;
import bibliothek.gui.dock.station.split.SplitDockStationFactory;
import bibliothek.gui.dock.station.stack.StackDockPropertyFactory;
import bibliothek.gui.dock.station.stack.StackDockStationFactory;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A DockFrontend provides some methods to handle the storage of various layouts.
 * The frontend can save the current layout (the location of all Dockables) and
 * later restore it.<br>
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
	/** The controller whhere roots are added */
    private DockController controller;
    /** An action and actionguard which hides Dockables */
    private Hider hider;
    
    /** The locations of the known Dockables */
    private Map<String, DockInfo> dockables = new HashMap<String, DockInfo>();
    
    /** The station which is used to add Dockables if no other station is explicitly requested */
    private DockStation defaultStation;
    /** The roots of this frontend */
    private Map<String, RootInfo> roots = new HashMap<String, RootInfo>();
    
    /** A set of factories needed to store Dockables */
    private Set<DockFactory<? extends DockElement>> dockFactories = new HashSet<DockFactory<? extends DockElement>>();
    /** A set of factories needed to store {@link DockableProperty properties} */
    private Set<DockablePropertyFactory> propertyFactories = new HashSet<DockablePropertyFactory>();
    
    /** The name of the setting which is currently loaded */
    private String currentSetting;
    /** A map of all known settings */
    private Map<String, byte[]> settings = new HashMap<String, byte[]>();
    
    /** A list of observers */
    private List<DockFrontendListener> listeners = new ArrayList<DockFrontendListener>();
    
    /** A filter for elements which should not be changed when writing or reading a normal setting */
    private DockSituationIgnore ignoreForEntry;
    /**
     *  A filter for elements which should not be changed when writing or reading the 
     *  final setting during the startup or shutdown of the application. 
     */
    private DockSituationIgnore ignoreForFinal;
    
    /**
     * Tells whether to show the hide-action on hideable dockables or not
     */
    private boolean showHideAction = true;
    
    /**
     * Constructs a new frontend, creates a new controller.
     */
    public DockFrontend(){
        this( new DockController(), null );
        getController().setSingleParentRemove( true );
    }
    
    /**
     * Constructs a new frontend, creates a new controller. Registers a
     * {@link ScreenDockStationFactory}, which can only be created if the owner
     * of the dialogs is known.
     * @param owner the owner of the dialogs of a {@link ScreenDockStationFactory},
     * may be <code>null</code>
     */
    public DockFrontend( Window owner ){
        this( new DockController(), owner );
        getController().setSingleParentRemove( true );
    }
    
    /**
     * Constructs a new frontend.
     * @param controller the controller used to store root stations
     */
    public DockFrontend( DockController controller ){
        this( controller, null );
    }
    
    /**
     * Constructs a new frontend, tries to set up a {@link ScreenDockStationFactory}.
     * @param controller the controller used to store the root stations
     * @param owner the owner of the dialog of a {@link ScreenDockStation},
     * may be <code>null</code>
     */
    public DockFrontend( DockController controller, Window owner ){
        if( controller == null )
            throw new IllegalArgumentException( "controller must not be null" );
        
        this.controller = controller;
        hider = createHider();
        controller.addActionGuard( hider );
        
        registerFactory( new DefaultDockableFactory() );
        registerFactory( new SplitDockStationFactory() );
        registerFactory( new StackDockStationFactory() );
        registerFactory( new FlapDockStationFactory() );
        
        if( owner != null )
            registerFactory( new ScreenDockStationFactory( owner ));
        
        registerFactory( new SplitDockPropertyFactory() );
        registerFactory( new StackDockPropertyFactory() );
        registerFactory( new FlapDockPropertyFactory() );
        registerFactory( new ScreenDockPropertyFactory() );
    }
    
    /**
     * Gets the controller which is used by this frontend.
     * @return the controller
     */
    public DockController getController() {
        return controller;
    }
    
    /**
     * Gets the list of {@link Dockable Dockables} which are added to this frontend.
     * @return the Dockables
     */
    public Collection<Dockable> getDockables(){
        List<Dockable> list = new ArrayList<Dockable>( dockables.size() );
        for( DockInfo info : dockables.values() )
            list.add( info.getDockable() );
        return list;
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
     * Registers a factory to write and read {@link Dockable Dockables} and
     * {@link DockStation DockStations}
     * @param factory the new factory
     */
    public void registerFactory( DockFactory<? extends DockElement> factory ){
    	if( factory == null )
    		throw new IllegalArgumentException( "factory must not be null" );
        dockFactories.add( factory );
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
     * @param dockable the new Dockable
     * @param name the unique name of the Dockable
     * @throws IllegalArgumentException if either of <code>dockable</code> or
     * <code>name</code> is <code>null</code>, or if <code>name</code> is not
     * unique.
     */
    public void add( Dockable dockable, String name ){
        if( dockable == null )
            throw new IllegalArgumentException( "Dockable must not be null" );
        
        if( name == null )
            throw new IllegalArgumentException( "name must not be null" );
        
        if( dockables.containsKey( name ))
        	throw new IllegalArgumentException( "There is already a dockable registered with name " + name );
        
        dockables.put( name, new DockInfo( dockable, name ));
    }
    
    /**
     * Gets an independent map containing all Dockables registered to this
     * frontend.
     * @return the map of Dockables
     */
    public Map<String, Dockable> getNamedDockables(){
    	Map<String, Dockable> result = new HashMap<String, Dockable>();
    	for( Map.Entry<String, DockInfo> entry : dockables.entrySet() ){
    		result.put( entry.getKey(), entry.getValue().getDockable() );
    	}
    	return result;
    }
    
    /**
     * Adds a root to this frontend. Only {@link Dockable Dockables} which are
     * children of a root can be stored. The frontend forwards the roots to
     * its {@link #getController() controller} 
     * (through the {@link DockController#add(DockStation) add}-method). Note
     * that the frontend does not observ its controller and therefore does not
     * know whether there are other roots registered at the controller.<br>
     * Clients should also provide a {@link #setDefaultStation(DockStation) default station}.
     * @param station the new station
     * @param name the unique name of the station
     * @throws IllegalArgumentException if <code>station</code> or <code>name</code>
     * is <code>null</code>, or if <code>name</code> is not unique.
     */
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
        	info.setHideable( false );
            dockables.remove( info.getKey() );
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
        
        info.setHideable( hideable );
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
     * Ensures that <code>dockable</code> is child of a root known to this
     * frontend.
     * @param dockable the element which should be made visible
     * @throws IllegalArgumentException if <code>dockable</code> is not known
     * @throws IllegalStateException if the {@link #getDefaultStation() default station} is
     * needed but can't be found 
     */
    public void show( Dockable dockable ){
        DockInfo info = getInfo( dockable );
        if( info == null )
            throw new IllegalArgumentException( "Dockable not registered at this frontend" );
        
        if( isHidden( dockable )){
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
            
            fireShowed( dockable );
        }
    }
    
    /**
     * Makes <code>dockable</code> invisible. The location of <code>dockable</code>
     * is saved, and if made visible again, it will reappear at its old location.
     * @param dockable the element which should be hidden
     * @throws IllegalArgumentException if <code>dockable</code> is not known
     */
    public void hide( Dockable dockable ){
        DockInfo info = getInfo( dockable );
        if( info == null )
            throw new IllegalArgumentException( "Dockable not registered at this frontend" );
        
        if( isShown( dockable )){
            info.updateLocation();
            dockable.getDockParent().drag( dockable );
            fireHidden( dockable );
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
        try{
        	if( name == null )
        		throw new IllegalArgumentException( "name must not be null" );
        	
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream( out );
            
            save( dout, true );
            
            dout.close();
            settings.put( name, out.toByteArray() );
            currentSetting = name;
            fireSaved( name );
        }
        catch( IOException ex ){
            throw new IllegalStateException( ex );
        }
    }
    
    /**
     * Writes the current layout as byte-stream into <code>out</code>.
     * @param out the stream to write into
     * @param entry <code>true</code> if just an ordinary setting should be written,
     * <code>false</code> if the final setting should be written.
     * @throws IOException if there are any problems
     */
    protected void save( DataOutputStream out, boolean entry ) throws IOException{
        DockSituation situation = createSituation( entry );
        PropertyTransformer properties = createTransformer();
        
        Map<String, DockStation> stations = new HashMap<String, DockStation>();
        for( RootInfo info : roots.values() )
            stations.put( info.getName(), info.getStation() );
        
        situation.write( stations, out );
        
        List<DockInfo> hidden = new ArrayList<DockInfo>();
        
        for( DockInfo info : dockables.values() ){
            if( info.getDockable().getController() == null ){
                if( info.getLocation() != null ){
                    hidden.add( info );
                }
            }
        }
        
        out.writeInt( hidden.size() );
        for( DockInfo info : hidden ){
            out.writeUTF( info.getKey() );
            out.writeUTF( info.getRoot() );
            properties.write( info.getLocation(), out );
        }
    }
    
    /**
     * Loads a setting of this frontend.
     * @param name the name of the setting
     * @throws IllegalArgumentException if no setting <code>name</code> could be found
     */
    public void load( String name ){
        try{
        	if( name == null )
        		throw new IllegalArgumentException( "name must not be null" );
        	
            if( !settings.containsKey( name ))
                throw new IllegalArgumentException( "Unknown setting \""+ name +"\"");
            
            currentSetting = name;
            
            ByteArrayInputStream in = new ByteArrayInputStream( settings.get( name ));
            DataInputStream din = new DataInputStream( in );
            
            load( din, true );
            
            din.close();
            fireLoaded( name );
        }
        catch( IOException ex ){
            throw new IllegalStateException( ex );
        }
    }
    
    /**
     * Gets a set of all {@link Dockable} which are known to this frontend
     * and which are visible.
     * @return the set of the visible elements
     */
    public Set<Dockable> listShownDockables(){
        Set<Dockable> set = new HashSet<Dockable>();
        for( DockInfo info : dockables.values() )
            if( isShown( info.getDockable() ))
                set.add( info.getDockable() );
        return set;
    }
    
    /**
     * Gets a list of all {@link Dockable}s which are registered at this
     * frontend.
     * @return the list of elements
     */
    public List<Dockable> listDockables(){
    	List<Dockable> result = new ArrayList<Dockable>( dockables.size() );
    	for( DockInfo info : dockables.values() )
    		result.add( info.getDockable() );
    	
    	return result;
    }
    
    /**
     * Reads a setting that was earlier converted into a byte-stream.
     * @param in the stream to read
     * @param entry <code>true</code> if the setting is just an ordinary setting,
     * <code>false</code> if a final setting is read.
     * @throws IOException if there are any problems
     */
    protected void load( DataInputStream in, boolean entry ) throws IOException{
        Set<Dockable> oldVisible = listShownDockables();
        
        DockSituation situation = createSituation( entry );
        PropertyTransformer properties = createTransformer();
        
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
        
        situation.read( in );
        
        int count = in.readInt();
        for( int i = 0; i < count; i++ ){
            String key = in.readUTF();
            String root = in.readUTF();
            DockableProperty property = properties.read( in );
            DockInfo info = getInfo( key );
            if( info != null )
            	info.setLocation( root, property );
        }
        
        Set<Dockable> newVisible = listShownDockables();
        
        for( Dockable hide : oldVisible )
            if( !newVisible.contains( hide ))
                fireHidden( hide );
        
        for( Dockable show : newVisible )
            if( !oldVisible.contains( show ))
                fireShowed( show );
        
        for( DockInfo info : dockables.values() ){
            if( !info.isHideable() && isHidden( info.getDockable() )){
                show( info.getDockable() );
            }
        }
    }
    
    /**
     * Removes all child-parent relations expect the ones filtered out
     * by <code>ignore</code>.
     * @param ignore a filter, never <code>null</code>
     */
    protected void clean( DockSituationIgnore ignore ){
        for( RootInfo root : roots.values() ){
            clean( root.getStation(), ignore );
        }
    }
    
    /**
     * Removes all recursively all children from <code>station</code>, but only
     * if neither the station nor its children are filtered by <code>ignore</code>.
     * @param station a station to clean
     * @param ignore a filter
     */
    protected void clean( DockStation station, DockSituationIgnore ignore ){
        if( !ignore.ignoreChildren( station ) && !ignore.ignoreElement( station ))
            while( station.getDockableCount() > 0 )
                clean( station.getDockable( 0 ), ignore );
    }
    
    /**
     * Removes <code>dockable</code> from its parent, but only if 
     * it is not filtered by <code>ignore</code>. If <code>dockable</code> is
     * a station, {@link #clean(DockStation, DockSituationIgnore)} should also
     * be called.
     * @param dockable the element to remove from its parent.
     * @param ignore a filter
     */
    protected void clean( Dockable dockable, DockSituationIgnore ignore ){
        if( !ignore.ignoreElement( dockable )){
            DockStation station = dockable.asDockStation();
            if( station != null )
                clean( station, ignore );
            
            if( dockable.getDockParent() != null )
                dockable.getDockParent().drag( dockable );
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
        if( currentSetting == null )
            out.writeBoolean( false );
        else{
            out.writeBoolean( true );
            out.writeUTF( currentSetting );
        }
        
        out.writeInt( settings.size() );
        for( Map.Entry<String, byte[]> setting : settings.entrySet() ){
            out.writeUTF( setting.getKey() );
            out.writeInt( setting.getValue().length );
            out.write( setting.getValue() );
        }
        
        save( out, false );
    }
    
    /**
     * Reads the settings of this frontend from <code>in</code>. The layout
     * will be changed according to the contents that are read.
     * @param in the stream to read from
     * @throws IOException if there are any problems
     */
    public void read( DataInputStream in ) throws IOException{
        if( in.readBoolean() )
            currentSetting = in.readUTF();
        else
            currentSetting = null;
        
        int count = in.readInt();
        for( int i = 0; i < count; i++ ){
            String key = in.readUTF();
            int length = in.readInt();
            byte[] value = new byte[length];
            
            int read = 0;
            while( read < length ){
                int input = in.read( value, read, length-read );
                if( input < 0 )
                    throw new EOFException();
                
                read += input;
            }
            
            settings.put( key, value );
        }
        
        load( in, false );
    }
    
    /**
     * Invoked every time before the current setting is written into a stream.
     * @param entry <code>true</code> if the situation is used for a regular setting,
     * <code>false</code> if the situation is used as the final setting which will
     * be loaded the next time the application starts.
     * @return the situation
     */
    protected DockSituation createSituation( boolean entry ){
        PredefinedDockSituation situation = new PredefinedDockSituation();
        for( DockInfo info : dockables.values() ){
            situation.put( "dockable" + info.getKey(), info.getDockable() );
        }
        
        for( RootInfo info : roots.values() ){
            situation.put( "root" + info.getName(), info.getStation() );
        }
        
        for( DockFactory<?> factory : dockFactories )
            situation.add( factory );
        
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
     * Invokes the method {@link DockFrontendListener#hidden(DockFrontend, Dockable)}
     * on all listeners.
     * @param dockable the hidden element
     */
    protected void fireHidden( Dockable dockable ){
        for( DockFrontendListener listener : listeners() )
            listener.hidden( this, dockable );
    }

    /**
     * Invokes the method {@link DockFrontendListener#showed(DockFrontend, Dockable)}
     * on all listeners.
     * @param dockable the shown element
     */
    protected void fireShowed( Dockable dockable ){
        for( DockFrontendListener listener : listeners() )
            listener.showed( this, dockable );
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
    private class DockInfo{
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
        /** Whether the hide-action is currently visible or not */
        private boolean hideActionVisible;
        
        /**
         * Creates a new DockInfo.
         * @param dockable the element whose informations are stored
         * @param key the name of the element
         */
        public DockInfo( Dockable dockable, String key ){
            this.dockable = dockable;
            this.key = key;
            source = new DefaultDockActionSource( new LocationHint( LocationHint.ACTION_GUARD, LocationHint.RIGHT_OF_ALL ));
            
            hideActionVisible = false;
            
            setHideable( true );
        }
        
        /**
         * Tells whether to show a "close"-action for the {@link #getDockable() dockable}
         * or not.
         * @return <code>true</code> if the element can be made invisible.
         */
        public boolean isHideable() {
            return hideable;
        }
        
        /**
         * Sets whether {@link #getDockable() the element} can be made
         * invisible or not.
         * @param hideable the new state
         */
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
        
        /**
         * The element for which this object stores information.
         * @return the element
         */
        public Dockable getDockable() {
            return dockable;
        }
        
        /**
         * The name which is used for this object.
         * @return the name
         */
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
        
        /**
         * Sets the location of {@link #getDockable() the element}. Note that this
         * just stores the location, no effect will be visible for the user.
         * @param root the root, might be <code>null</code>
         * @param location the location, might be <code>null</code>
         */
        public void setLocation( String root, DockableProperty location ){
            this.root = root;
            this.location = location;
        }
        
        /**
         * Gets the name of the station on which {@link #getDockable() the element}
         * was the last time when it was made invisible.
         * @return the name or <code>null</code>
         * @see #updateLocation()
         */
        public String getRoot() {
            return root;
        }
        
        /**
         * Gets the location of {@link #getDockable() the element} which it had
         * the last time it was made invisible.
         * @return the location or <code>null</code>
         * @see #updateLocation()
         */
        public DockableProperty getLocation() {
            return location;
        }
    }
    
    /**
     * Stores information about a root-station.
     * @author Benjamin Sigg
     */
    private class RootInfo{
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
            setTooltipText( DockUI.getDefaultDockUI().getString( "close.tooltip" ));
            
            controller.getIcons().add( "close", this );
            setIcon( controller.getIcons().getIcon( "close" ));
        }
        
        public void iconChanged( String key, Icon icon ) {
            setIcon( icon );
        }
        
        public DockActionSource getSource( Dockable dockable ) {
            return getInfo( dockable ).getSource();
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
