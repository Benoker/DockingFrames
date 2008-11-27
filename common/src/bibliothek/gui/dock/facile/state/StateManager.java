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
package bibliothek.gui.dock.facile.state;

import java.awt.Component;
import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.event.DockRegisterListener;
import bibliothek.gui.dock.event.DockRelocatorListener;
import bibliothek.gui.dock.event.IconManagerListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.PropertyTransformer;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;
import bibliothek.gui.dock.support.action.ModeTransitionConverter;
import bibliothek.gui.dock.support.action.ModeTransitionManager;
import bibliothek.gui.dock.support.action.ModeTransitionSetting;
import bibliothek.gui.dock.support.util.Resources;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A manager that can minimize/normalize/maximize and externalize a
 * {@link Dockable}.<br>
 * <ul>
 *  <li>minimized: the element is child of a {@link FlapDockStation}</li>
 *  <li>maximized: the element is child of a {@link SplitDockStation}, and
 *  in fullscreen-mode</li>
 *  <li>externalized: the element is child of {@link ScreenDockStation}</li>
 *  <li>normalized: everything else</li>
 * </ul>
 * @author Benjamin Sigg
 */
public class StateManager extends ModeTransitionManager<StateManager.Location> {
    /** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "minimize"-action */
    public static final String ICON_MANAGER_KEY_MINIMIZE = "statemanager.minimize";
    /** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "maximize"-action */
    public static final String ICON_MANAGER_KEY_MAXIMIZE = "statemanager.maximize";
    /** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "normalize"-action */
    public static final String ICON_MANAGER_KEY_NORMALIZE = "statemanager.normalize";
    /** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "externalize"-action */
    public static final String ICON_MANAGER_KEY_EXTERNALIZE = "statemanager.externalize";

    /** the areas used to show a maximized element */
    private Set<SplitDockStation> normal = new HashSet<SplitDockStation>();

    /** the default station for normalized elements */
    private SplitDockStation defaultNormal;

    /** the areas used to show a minimized element */
    private Set<FlapDockStation> mini = new HashSet<FlapDockStation>();

    /** the default station for minimized elements */
    private FlapDockStation defaultMini;

    /** areas used to show maximized elements */
    private Set<MaximizeArea> maxi = new HashSet<MaximizeArea>();

    /** the default area to use for maximized elements */
    private MaximizeArea defaultMaxi;

    /** the areas used to show a externalized element */
    private Set<ScreenDockStation> external = new HashSet<ScreenDockStation>();

    /** the default station for externalized elements */
    private ScreenDockStation defaultExternal;

    /** all stations known to this manager */
    private Map<String, DockStation> stations = new HashMap<String, DockStation>();

    /** key for the minimized mode */
    public static final String MINIMIZED = "mini";

    /** key for the maximized mode */
    public static final String MAXIMIZED = "maxi";

    /** key for the normalized mode */
    public static final String NORMALIZED = "normal";

    /** key for the externalized mode */
    public static final String EXTERNALIZED = "extern";

    /** listener to some elements known to this manager, ensures that {@link Dockable}s are associated with the correct mode */
    private Listener listener = new Listener();

    /** the controller of this realm */
    private DockController controller;

    /** 
     * The location of the last element which was maximized, one entry for each
     * {@link #maxi area}.
     */
    private Map<String, Location> lastMaximizedLocation = new HashMap<String, Location>();

    /** 
     * The mode of the last element which was maximized, one entry for each
     * {@link #maxi area}.
     */
    private Map<String, String> lastMaximizedMode = new HashMap<String, String>();

    /** Whether a transition is currently in progress or not */
    private boolean onTransition = false;

    /**
     * Creates a new manager. Adds listeners to the <code>controller</code>
     * and adds <code>this</code> as {@link ActionGuard} to the <code>controller</code>.
     * @param controller the controller which is observed by this manager. 
     */
    public StateManager( DockController controller ){
        this( controller, true );
    }

    /**
     * Creates a new manager. Adds listeners to the <code>controller</code>
     * and adds <code>this</code> as {@link ActionGuard} to the <code>controller</code>.
     * @param controller the controller which is observed by this manager.
     * @param init whether to initialize all the element of this manager or not.
     * If <code>false</code>, then {@link #init()} has to be called. 
     */
    protected StateManager( DockController controller, boolean init ){
        super( MINIMIZED, NORMALIZED, MAXIMIZED, EXTERNALIZED );
        this.controller = controller;
        if( init )
            init();
    }

    /**
     * Initializes all the elements of this manager. That means adding
     * listeners to the {@link DockController}, and setting text and icons of
     * the {@link DockAction}s used by this {@link ModeTransitionManager}.
     */
    protected void init(){
        controller.addActionGuard( this );
        controller.getRelocator().addDockRelocatorListener( listener );
        controller.getRegister().addDockRegisterListener( listener );

        ResourceBundle bundle = Resources.getBundle();

        getIngoingAction( MINIMIZED ).setText( bundle.getString( "minimize.in" ) );
        getIngoingAction( MINIMIZED ).setTooltip( bundle.getString( "minimize.in.tooltip" ) );

        getIngoingAction( NORMALIZED ).setText( bundle.getString( "normalize.in" ) );
        getIngoingAction( NORMALIZED ).setTooltip( bundle.getString( "normalize.in.tooltip" ) );

        getIngoingAction( MAXIMIZED ).setText( bundle.getString( "maximize.in" ) );
        getIngoingAction( MAXIMIZED ).setTooltip( bundle.getString( "maximize.in.tooltip" ) );

        getIngoingAction( EXTERNALIZED ).setText( bundle.getString( "externalize.in" ) );
        getIngoingAction( EXTERNALIZED ).setTooltip( bundle.getString( "externalize.in.tooltip" ) );

        IconManager icons = controller.getIcons();
        icons.setIconDefault( ICON_MANAGER_KEY_MAXIMIZE, Resources.getIcon( "maximize" ) );
        icons.setIconDefault( ICON_MANAGER_KEY_MINIMIZE, Resources.getIcon( "minimize" ) );
        icons.setIconDefault( ICON_MANAGER_KEY_NORMALIZE, Resources.getIcon( "normalize" ) );
        icons.setIconDefault( ICON_MANAGER_KEY_EXTERNALIZE, Resources.getIcon( "externalize" ) );

        wire( icons, ICON_MANAGER_KEY_MAXIMIZE, getIngoingAction( MAXIMIZED ));
        wire( icons, ICON_MANAGER_KEY_MINIMIZE, getIngoingAction( MINIMIZED ));
        wire( icons, ICON_MANAGER_KEY_NORMALIZE, getIngoingAction( NORMALIZED ));
        wire( icons, ICON_MANAGER_KEY_EXTERNALIZE, getIngoingAction( EXTERNALIZED ));
    }

    /**
     * Adds a listener to <code>icons</code> which will change the icon of
     * <code>action</code> whenever the icon <code>key</code> is changed.
     * @param icons the set of icons
     * @param key the name of the icon to observe
     * @param action the action whose icon will be changed
     */
    private void wire( IconManager icons, String key, final SimpleButtonAction action ){
        icons.add( key, new IconManagerListener(){
            public void iconChanged( String key, Icon icon ){
                action.setIcon( icon );
            }
        });
        action.setIcon( icons.getIcon( key ) );
    }

    @Override
    public <B> ModeTransitionSetting<Location, B> getSetting( ModeTransitionConverter<Location, B> converter ) {
        StateManagerSetting<B> setting = (StateManagerSetting<B>)super.getSetting( converter );
        setting.setLastMaximizedLocation( lastMaximizedLocation );
        setting.setLastMaximizedMode( lastMaximizedMode );
        return setting;
    }

    @Override
    public void setSetting( ModeTransitionSetting<Location, ?> setting ){
        if( setting instanceof StateManagerSetting ){
            StateManagerSetting<?> stateManagerSetting = (StateManagerSetting<?>)setting;
            lastMaximizedLocation = stateManagerSetting.getLastMaximizedLocation();
            lastMaximizedMode = stateManagerSetting.getLastMaximizedMode();
        }
        else{
            lastMaximizedLocation = null;
            lastMaximizedMode = null;
        }
        super.setSetting( setting );
        rebuildAll();
    }

    @Override
    protected <B> StateManagerSetting<B> createSetting(
            ModeTransitionConverter<Location, B> converter ) {
        return new StateManagerSetting<B>( converter );
    }

    /**
     * Adds a station to which a {@link Dockable} can be <i>normalized</i>
     * or <i>maximized</i>. If this is the first call to this method, then
     * <code>station</code> becomes the default station for this kind or
     * operation.
     * @param name the name of the station
     * @param station the new station
     */
    public void add( String name, SplitDockStation station ){
    	add( name, station, true );
    }
    
    /**
     * Adds a station to which a {@link Dockable} can be <i>normalized</i>
     * or <i>maximized</i>. If this is the first call to this method, then
     * <code>station</code> becomes the default station for this kind or
     * operation.
     * @param name the name of the station
     * @param station the new station
     * @param allowAutoDefault tells whether <code>station</code> can automatically
     * become the default station for some actions like maximizing
     */
    public void add( String name, SplitDockStation station, boolean allowAutoDefault ){
        if( name == null )
            throw new NullPointerException( "name must not be null" );

        if( stations.containsKey( name ))
            throw new IllegalArgumentException( "There is already a station registered with that name" );

        if( station == null )
            throw new NullPointerException( "station must not be null" );

        stations.put( name, station );
        normal.add( station );
        
        if( allowAutoDefault ){
        	if( defaultNormal == null )
        		defaultNormal = station;

        	if( defaultMaxi == null ){
        		setMaximizingArea( new MaximizeSplitDockStation( "dock.default", station ) );
        	}
        }
    }

    /**
     * Adds a new maximizing-area to this manager. When the user maximizes
     * a {@link Dockable} then such an area becomes the new parent of the
     * element.
     * @param area a new area, must not be <code>null</code> and not use
     * an already existing identifier
     */
    public void addMaximizingArea( MaximizeArea area ){
        if( area == null )
            throw new IllegalArgumentException( "area must not be null" );

        for( MaximizeArea check : maxi ){
            if( check.getUniqueId().equals( area.getUniqueId() )){
                throw new IllegalArgumentException( "the unique identifier '" + area.getUniqueId() + "' is already in use" );
            }
        }

        ensureNothingMaximized();

        maxi.add( area );

        area.addMaximizeAreaListener( listener );

        if( defaultMaxi == null )
            defaultMaxi = area;
    }

    /**
     * Gets a list of all the {@link MaximizeArea}s that are currently 
     * registered, the list is in not ordered.
     * @return the list of areas, not <code>null</code>
     */
    public MaximizeArea[] getMaximizingAreas(){
        return maxi.toArray( new MaximizeArea[ maxi.size() ] );
    }

    /**
     * Removes <code>area</code> from the list of potential parents of
     * maximized {@link Dockable}s.
     * @param area the are to remove
     */
    public void removeMaximizingArea( MaximizeArea area ){
        if( area == null ){
            throw new IllegalArgumentException( "area must not be null" );
        }

        ensureNothingMaximized();

        maxi.remove( area );

        area.removeMaximizeAreaListener( listener );

        lastMaximizedLocation.remove( area.getUniqueId() );
        lastMaximizedMode.remove( area.getUniqueId() );

        if( defaultMaxi == area ){
            defaultMaxi = null;

            if( !maxi.isEmpty() ){
                defaultMaxi = maxi.iterator().next();
            }
        }
    }

    /**
     * Sets the default maximizing-area for this manager. Calls
     * {@link #addMaximizingArea(MaximizeArea)} if necessary.
     * @param area the new default area, not <code>null</code>
     */
    public void setMaximizingArea( MaximizeArea area ){
        ensureNothingMaximized();

        if( !maxi.contains( area )){
            addMaximizingArea( area );
        }
        defaultMaxi = area;
    }

    /**
     * Gets the current default maximizing-area.
     * @return the maximizing area, can be <code>null</code>
     * @see #setMaximizingArea(MaximizeArea)
     */
    public MaximizeArea getMaximizingArea(){
        return defaultMaxi;
    }

    /**
     * Sets the station which should be used for maximized {@link Dockable}s.
     * Any currently maximized elements will be normalized.
     * @param name the unique identifier of the station that should become
     * the maximize area
     * @deprecated better use {@link #setMaximizingArea(MaximizeArea)}
     */
    @Deprecated
    public void setMaximizingStation( String name ) {
        DockStation station = stations.get( name );
        if( station == null )
            throw new IllegalArgumentException( "No station registered with name " + name );

        if( !( station instanceof SplitDockStation ))
            throw new IllegalArgumentException( "Station is not a SplidDockStation " + name );

        setMaximizingArea( new MaximizeSplitDockStation( "dock.default", (SplitDockStation)station ) );
    }

    /**
     * Adds a station to which a {@link Dockable} can be <i>minimized</i>.
     * If this is the first call to this method, then
     * <code>station</code> becomes the default station for this kind or
     * operation.
     * @param name the name of the station
     * @param station the new station.
     */
    public void add( String name, FlapDockStation station ){
        if( name == null )
            throw new NullPointerException( "name must not be null" );

        if( stations.containsKey( name ))
            throw new IllegalArgumentException( "There is already a station registered with that name" );

        if( station == null )
            throw new NullPointerException( "station must not be null" );

        stations.put( name, station );
        mini.add( station );
        if( defaultMini == null )
            defaultMini = station;
    }

    /**
     * Adds a station to which a {@link Dockable} can be <i>externalized</i>.
     * If this is the first call to this method, then
     * <code>station</code> becomes the default station for this kind or
     * operation.
     * @param name the name of the station
     * @param station the new station.
     */
    public void add( String name, ScreenDockStation station ){
        if( name == null )
            throw new NullPointerException( "name must not be null" );

        if( stations.containsKey( name ))
            throw new IllegalArgumentException( "There is already a station registered with that name" );

        if( station == null )
            throw new NullPointerException( "station must not be null" );

        stations.put( name, station );
        external.add( station );
        if( defaultExternal == null )
            defaultExternal = station;
    }

    /**
     * Removes the {@link DockStation} <code>name</code> from this manager. If
     * the station is a default-station, then this property is set to <code>null</code>.
     * @param name the name of the station to remove
     */
    public void remove( String name ){
        if( name == null )
            throw new NullPointerException( "name must not be null" );

        DockStation station = stations.remove( name );
        normal.remove( name );
        mini.remove( name );
        external.remove( name );

        if( station == defaultExternal )
            defaultExternal = null;

        if( station == defaultMini )
            defaultMini = null;

        if( station == defaultNormal )
            defaultNormal = null;

        if( station == maxi )
            maxi = null;
    }

    /**
     * Ensures that <code>dockable</code> is not hidden behind another 
     * {@link Dockable}. That does not mean that <code>dockable</code> becomes
     * visible, just that it is easier reachable without the need to change
     * modes of any <code>Dockable</code>s.  
     * @param dockable the element which should not be hidden
     */
    public void ensureNotHidden( Dockable dockable ){
        AffectedSet set = new AffectedSet();

        DockStation parent = dockable.getDockParent();
        while( parent != null ){
            MaximizeArea area = getMaximizeArea( parent );
            if( area != null ){
                if( area.getMaximizedDockable() != null && area.getMaximizedDockable() != dockable ){
                    unmaximize( area.getMaximizedDockable(), set );
                }
            }

            dockable = parent.asDockable();
            parent = dockable == null ? null : dockable.getDockParent();
        }

        set.finish();
    }

    /**
     * Ensures that there is no maximized element.
     * @return <code>true</code> if at least one element was affected by changes,
     * <code>false</code> if nothing happened.
     */
    public boolean ensureNothingMaximized(){
        if( maxi == null )
            return false;

        AffectedSet set = new AffectedSet();

        for( MaximizeArea area : maxi ){
            if( area.getMaximizedDockable() != null ){
                unmaximize( area.getMaximizedDockable(), set );
            }
        }

        set.finish();

        return !set.isEmpty();
    }

    /**
     * Gets the one {@link Dockable} which is considered to be the root of
     * all maximized <code>Dockable</code>s.
     * @return the root of all currently maximized <code>Dockable</code>s or <code>null</code>
     */
    public Dockable getMaximized(){
        if( defaultMaxi == null )
            return null;

        return defaultMaxi.getMaximizedDockable();
    }

    /**
     * Searches the first {@link MaximizeArea} which is a parent
     * of <code>dockable</code>. This method will never return
     * <code>dockable</code> itself.
     * @param dockable the element whose maximize area is searched
     * @return the area or <code>null</code>
     */
    protected MaximizeArea getMaximizeArea( Dockable dockable ){
        DockStation parent = dockable.getDockParent();
        while( parent != null ){
            MaximizeArea area = getMaximizeArea( parent );
            if( area != null )
                return area;

            dockable = parent.asDockable();
            if( dockable == null ){
                parent = null;
            }
            else{
                parent = dockable.getDockParent();
            }
        }
        return null;
    }

    /**
     * Searches the one {@link MaximizeArea} whose station is
     * <code>station</code>.
     * @param station the station whose area is searched
     * @return the area or <code>null</code> if not found
     */
    protected MaximizeArea getMaximizeArea( DockStation station ){
        for( MaximizeArea area : maxi ){
            if( area.getStation() == station ){
                return area;
            }
        }
        return null;
    }

    @Override
    protected String[] availableModes( String current, Dockable dockable ) {
        if( MINIMIZED.equals( current ))
            return new String[]{ NORMALIZED, MAXIMIZED, EXTERNALIZED };

        if( NORMALIZED.equals( current ))
            return new String[]{ MINIMIZED, MAXIMIZED, EXTERNALIZED };

        if( MAXIMIZED.equals( current ))
            return new String[]{ NORMALIZED, MINIMIZED, EXTERNALIZED };

        if( EXTERNALIZED.equals( current ))
            return new String[]{ NORMALIZED, MAXIMIZED, MINIMIZED };

        return new String[]{ MINIMIZED, NORMALIZED, MAXIMIZED, EXTERNALIZED };
    }

    @Override
    protected String currentMode( Dockable dockable ) {
        if( normal.contains( dockable ) || mini.contains( dockable ) )
            return NORMALIZED;

        String sharp = currentModeSharp( dockable );
        if( sharp != null )
            return sharp;

        return NORMALIZED;
    }

    /**
     * Searches the current mode of <code>dockable</code> and returns
     * the mode.
     * @param dockable the element whose mode is searched
     * @return the mode or <code>null</code> if the mode could not be found
     */
    protected String currentModeSharp( Dockable dockable ) {
        DockStation parent = dockable.getDockParent();
        while( parent != null ){
            if( mini.contains( parent ))
                return MINIMIZED;

            if( external.contains( parent ))
                return EXTERNALIZED;

            for( MaximizeArea maxi : this.maxi ){
                if( maxi.getStation() == parent ){
                    if( maxi.getMaximizedDockable() == dockable )
                        return MAXIMIZED;
                    else
                        return NORMALIZED;
                }
            }

            if( normal.contains( parent )){
                return NORMALIZED;
            }

            dockable = parent.asDockable();
            parent = dockable == null ? null : dockable.getDockParent();
        }

        return null;
    }

    /**
     * Guesses the mode a child of <code>station</code> would have if it is
     * dropped on <code>station</code>.
     * @param station some station
     * @return the mode or <code>null</code>. The mode is one of 
     * {@link #NORMALIZED}, {@link #MINIMIZED} or {@link #EXTERNALIZED}.
     * The mode {@link #MAXIMIZED} will not be considered.
     */
    protected String childsMode( DockStation station ){
        while( station != null ){
            if( normal.contains( station ))
                return NORMALIZED;
            if( mini.contains( station ))
                return MINIMIZED;
            if( external.contains( station ))
                return EXTERNALIZED;

            Dockable dockable = station.asDockable();
            station = dockable == null ? null : dockable.getDockParent();
        }
        return null;
    }

    @Override
    protected String getDefaultMode( Dockable dockable ) {
        return NORMALIZED;
    }

    /**
     * Tells whether this {@link StateManager} currently is executing a
     * transition.
     * @return <code>true</code> if currently in a transition.
     * @see #transition(String, String, Dockable)
     */
    public boolean isOnTransition() {
        return onTransition;
    }

    @Override
    protected void transition( String oldMode, String newMode, Dockable dockable ) {
        AffectedSet affected = new AffectedSet();
        onTransition = true;
        try{
            try{
                controller.getRegister().setStalled( true );

                if( oldMode != null ){
                    store( dockable );
                }

                change( oldMode, newMode, dockable, affected );

                if( !MINIMIZED.equals( newMode ))
                    controller.setFocusedDockable( dockable, true );
            }
            finally{
                controller.getRegister().setStalled( false );
                onTransition = false;
                affected.finish();
            }
        }
        finally{
            onTransition = false;
        }
    }

    @Override
    protected void transitionDuringRead( String id, String oldMode, String newMode, Dockable dockable ) {
        // ignore
    }

    /**
     * Gets the element which must be maximized when the user requests that
     * <code>dockable</code> is maximized.
     * @param dockable some element, not <code>null</code>
     * @return the element that must be maximized, might be <code>dockable</code>
     * itself, not <code>null</code>
     */
    protected Dockable getMaximizingElement( Dockable dockable ){
        DockStation station = dockable.getDockParent();
        if( station == null )
            return dockable;

        if( !(station instanceof StackDockStation ))
            return dockable;

        return station.asDockable();
    }

    /**
     * Gets the element which would be maximized if <code>old</code> is currently
     * maximized, and <code>dockable</code> is or will not be maximized.
     * @param old some element
     * @param dockable some element, might be <code>old</code>
     * @return the element which would be maximized if <code>dockable</code> is
     * no longer maximized, can be <code>null</code>
     */
    protected Dockable getMaximizingElement( Dockable old, Dockable dockable ){
        if( old == dockable )
            return null;

        if( old instanceof DockStation ){
            DockStation station = (DockStation)old;
            if( station.getDockableCount() == 2 ){
                if( station.getDockable( 0 ) == dockable )
                    return station.getDockable( 1 );
                if( station.getDockable( 1 ) == dockable )
                    return station.getDockable( 0 );
            }
            if( station.getDockableCount() < 2  )
                return null;
        }

        return old;
    }

    /**
     * Changes the mode of <code>dockable</code>. Modes are
     * {@link #MINIMIZED}, {@link #NORMALIZED}, {@link #MAXIMIZED} and
     * {@link #EXTERNALIZED}.
     * @param current the current mode, can be <code>null</code>
     * @param destination the mode <code>dockable</code> would like to have
     * @param dockable the element whose mode is to change
     * @param affected a set of <code>Dockable</code>s which will be filled by the
     * elements that change their mode because of this method
     */
    private void change( String current, String destination, Dockable dockable, AffectedSet affected ){
        change( current, destination, dockable, getProperties( destination, dockable ), affected );
    }

    /**
     * Changes the mode of <code>dockable</code>. Modes are
     * {@link #MINIMIZED}, {@link #NORMALIZED}, {@link #MAXIMIZED} and
     * {@link #EXTERNALIZED}.
     * @param current the current mode, can be <code>null</code>
     * @param destination the mode <code>dockable</code> would like to have
     * @param dockable the element whose mode is to change
     * @param location the new location, can be <code>null</code>. The location
     * should match the <code>destination</code>, otherwise the <code>dockable</code>
     * might be placed at the wrong location.
     * @param affected a set of <code>Dockable</code>s which will be filled by the
     * elements that change their mode because of this method
     */
    private void change( String current, String destination, Dockable dockable, Location location, AffectedSet affected ){
        if( MAXIMIZED.equals( destination ))
            maximize( current, dockable, affected );
        if( MINIMIZED.equals( destination ))
            minimize( dockable, location, affected );
        if( NORMALIZED.equals( destination ))
            normalize( dockable, location, affected );
        if( EXTERNALIZED.equals( destination ))
            externalize( dockable, location, affected );
    }

    /**
     * Ensures that <code>dockable</code> is maximized.
     * @param current the mode <code>dockable</code> is currently in,
     * can be <code>null</code> to indicate that <code>dockable</code>
     * is newly added.
     * @param dockable the element that should be made maximized
     * @param affected a set of <code>Dockable</code>s which will be filled by the
     * elements that change their mode because of this method
     */
    private void maximize( String current, Dockable dockable, AffectedSet affected ){
        maximize( current, null, dockable, affected );
    }

    /**
     * Ensures that <code>dockable</code> is maximized.
     * @param current the mode <code>dockable</code> is currently in,
     * can be <code>null</code> to indicate that <code>dockable</code>
     * is newly added.
     * @param area the future parent of <code>dockable</code>, can be <code>null</code>
     * @param dockable the element that should be made maximized
     * @param affected a set of <code>Dockable</code>s which will be filled by the
     * elements that change their mode because of this method
     */    
    private void maximize( String current, MaximizeArea area, Dockable dockable, AffectedSet affected ){
        if( current == null || !NORMALIZED.equals( current )){
            change( current, NORMALIZED, dockable, affected );
            current = NORMALIZED;
        }

        Dockable maximizing = getMaximizingElement( dockable );
        if( maximizing != dockable )
            store( maximizing );

        if( area == null )
            area = getMaximizeArea( maximizing );

        if( area == null )
            area = defaultMaxi;

        if( getName( maximizing ) == null ){
            lastMaximizedLocation.put( area.getUniqueId(), currentLocation( current, dockable ));
            lastMaximizedMode.put( area.getUniqueId(), current );
        }
        else{
            lastMaximizedLocation.remove( area.getUniqueId() );
            lastMaximizedMode.remove( area.getUniqueId() );
        }

        if( maximizing.getDockParent() == area.getStation() ){
            area.setMaximizedDockable( maximizing );
        }
        else{
            if( maximizing.getDockParent() != null )
                maximizing.getDockParent().drag( maximizing );

            area.dropAside( maximizing );
            area.setMaximizedDockable( maximizing );
        }

        affected.add( maximizing );
    }

    /**
     * Ensures that <code>dockable</code> is not maximized.
     * @param dockable the element that might be maximized currently
     * @param affected a set of <code>Dockable</code>s which will be filled by the
     * elements that change their mode because of this method
     */
    private void unmaximize( Dockable dockable, AffectedSet affected ){
        MaximizeArea area = getMaximizeArea( dockable );
        if( area != null && area.getMaximizedDockable() != null ){
            affected.add( dockable );

            dockable = area.getMaximizedDockable();
            area.setMaximizedDockable( null );

            String key = area.getUniqueId();

            if( lastMaximizedLocation.get( key ) != null ){
                change( 
                        MAXIMIZED, 
                        lastMaximizedMode.remove( key ),
                        dockable,
                        lastMaximizedLocation.remove( key ),
                        affected );
            }
            else{
                String mode = previousMode( dockable );
                if( mode == null )
                    mode = getDefaultMode( dockable );

                if( MAXIMIZED.equals( mode ))
                    mode = NORMALIZED;

                change( MAXIMIZED, mode, dockable, affected );
            }
        }
    }

    /**
     * Ensures that either the {@link MaximizeArea} <code>station</code> or its
     * nearest parent does not show a maximized element.
     * @param station an area or a child of an area
     * @param affected elements whose mode changes will be added to this set
     */
    private void unmaximize( DockStation station, AffectedSet affected ){
        while( station != null ){
            MaximizeArea area = getMaximizeArea( station );
            if( area != null ){
                Dockable dockable = area.getMaximizedDockable();
                if( dockable != null ){
                    unmaximize( dockable, affected );
                    return;
                }
            }

            Dockable dockable = station.asDockable();
            if( dockable == null )
                return;

            station = dockable.getDockParent();
        }
    }

    /**
     * Makes sure that <code>dockable</code> is normalized, where
     * <code>location</code> describes the new position of <code>dockable</code>.
     * @param dockable the element to normalize, can already be in 
     * normalized-state.
     * @param location a location describing the new position of <code>dockable</code>,
     * the behavior is unspecified if <code>location</code> does not describe
     * a normalized position. Can be <code>null</code>.
     * @param affected a set of <code>Dockable</code>s which will be filled by the
     * elements that change their mode because of this method
     */
    private void normalize( Dockable dockable, Location location, AffectedSet affected ){
        boolean done = false;
        affected.add( dockable );

        if( location != null ){
            if( location.location.getSuccessor() == null ){
                String[] history = history( dockable );
                if( history != null && history.length >= 2 ){
                    String last = history[ history.length-1 ];
                    String secondLast = history[ history.length-2 ];
                    if( NORMALIZED.equals( secondLast ) && MAXIMIZED.equals( last )){
                        DockStation station = stations.get( location.root );
                        MaximizeArea area = getMaximizeArea( station );
                        if( area != null ){
                            area.setMaximizedDockable( null );
                            done = true;
                        }
                    }
                }
            }
        }

        if( !done ){
            // wherever it is, ensure that it does not leave a station in
            // a dubious state
            unmaximize( dockable, affected );

            // ensure it does land on a parent without maximized children
            if( location != null ){
                DockStation station = stations.get( location.root );
                if( station != null ){
                    unmaximize( station, affected );
                }
            }

            if( !drop( location, dockable )){
                if( !isValidNormalized( dockable )){
                    DockStation defaultStation = getDefaultNormal( dockable );
                    if( defaultStation != null ){
                        unmaximize( defaultStation, affected );
                    }
                    checkedDrop( defaultStation, dockable, null );
                }
            }
        }
    }

    /**
     * Tells whether the element <code>dockable</code> is on a valid normalized
     * area or not.
     * @param dockable the element to check
     * @return <code>true</code> if <code>dockable</code> can remain at the
     * location that it currently has
     */
    protected boolean isValidNormalized( Dockable dockable ){
        DockStation normal = getDefaultNormal( dockable );
        if( normal == null )
            return false;

        return DockUtilities.isAncestor( normal, dockable );
    }

    /**
     * Gets the {@link DockStation} which should be used as default normal
     * parent for <code>dockable</code>.
     * @param dockable some {@link Dockable}
     * @return the preferred normal parent for <code>dockable</code> or <code>null</code>
     */
    protected DockStation getDefaultNormal( Dockable dockable ){
        return defaultNormal;
    }

    /**
     * Makes sure that <code>dockable</code> is minimized, where
     * <code>location</code> describes the new position of <code>dockable</code>.
     * @param dockable the element to minimize, can already be in 
     * minimized-state.
     * @param location a location describing the new position of <code>dockable</code>,
     * the behavior is unspecified if <code>location</code> does not describe
     * a minimized position. Can be <code>null</code>.
     * @param affected a set of <code>Dockable</code>s which will be filled by the
     * elements that change their mode because of this method
     */
    private void minimize( Dockable dockable, Location location, AffectedSet affected ){
        Dockable maximized = null;
        MaximizeArea maxiarea = null;

        affected.add( dockable );

        if( MAXIMIZED.equals( currentMode( dockable ) ) ){
            maxiarea = getMaximizeArea( dockable );
            Dockable maximizedNow = maxiarea.getMaximizedDockable();
            if( maximizedNow != null )
                maximized = getMaximizingElement( maximizedNow, dockable );

            unmaximize( maximizedNow, affected );
        }

        if( !drop( location, dockable )){
            checkedDrop( defaultMini, dockable, null );
        }

        if( maximized != null && maximized.getDockParent() != null )
            maximize( currentMode( maximized ), maxiarea, maximized, affected );
    }

    /**
     * Makes sure that <code>dockable</code> is externalized, where
     * <code>location</code> describes the new position of <code>dockable</code>.
     * @param dockable the element to externalized, can already be in 
     * externalize-state.
     * @param location a location describing the new position of <code>dockable</code>,
     * the behavior is unspecified if <code>location</code> does not describe
     * an externalized position. Can be <code>null</code>.
     * @param affected a set of <code>Dockable</code>s which will be filled by the
     * elements that change their mode because of this method
     */
    private void externalize( Dockable dockable, Location location, AffectedSet affected ){
        Dockable maximized = null;
        MaximizeArea maxiarea = null;

        if( MAXIMIZED.equals( currentMode( dockable ) ) ){
            maxiarea = getMaximizeArea( dockable );

            Dockable nowMaximized = maxiarea.getMaximizedDockable();
            if( nowMaximized != null )
                maximized = getMaximizingElement( nowMaximized, dockable );

            unmaximize( nowMaximized, affected );
        }

        affected.add( dockable );

        if( !drop( location, dockable )){
            if( dockable.getDockParent() != defaultExternal ){
                Component component = dockable.getComponent();
                component.invalidate();

                Component parent = component;
                while( parent.getParent() != null )
                    parent = parent.getParent();
                parent.validate();

                Point corner = new Point();
                SwingUtilities.convertPointToScreen( corner, dockable.getComponent() );

                ScreenDockProperty property = new ScreenDockProperty( 
                        corner.x, corner.y, component.getWidth(), component.getHeight() );

                boolean externDone = defaultExternal.drop( dockable, property, false );

                if( !externDone )
                    defaultExternal.drop( dockable );
            }
        }

        if( maximized != null && maximized.getDockParent() != null )
            maximize( currentMode( maximized ), maxiarea, maximized, affected );
    }

    /**
     * Searches the station matching <code>location</code> and drops
     * <code>dockable</code> there.
     * @param location the new location, can be <code>null</code>
     * @param dockable the element to drop
     * @return <code>true</code> if the operation was successful, 
     * <code>false</code> if not.
     */
    private boolean drop( Location location, Dockable dockable ){
        if( location == null )
            return false;

        DockStation station = stations.get( location.root );
        if( station == null )
            return false;

        return station.drop( dockable, location.getLocation() );
    }

    /**
     * Drops <code>dockable</code> onto <code>station</code>, but only
     * if <code>station</code> is not the parent of <code>dockable</code>.
     * @param station the new parent of <code>dockable</code>
     * @param dockable the new child of <code>station</code>
     * @param location the preferred location, can be <code>null</code>
     */
    private void checkedDrop( DockStation station, Dockable dockable, DockableProperty location ){
        if( station != null && station != dockable.getDockParent() ){
            if( station == dockable ){
                throw new IllegalStateException( "Trying to drop a DockStation on itself. " +
                        "If you are using a CControl: make sure that the CControl has a root " +
                        "station for all ExtendedModes. Do not use CDockables as " +
                        "root station or do not call CDockable.setVisible if " +
                "you use them as root station.");
            }

            boolean done = false;

            if( location != null )
                done = station.drop( dockable, location );

            if( !done )
                station.drop( dockable );
        }
    }

    /**
     * Stores for each {@link Dockable} in the tree with the root <code>dockable</code>
     * the location associated to their current mode.
     * @param dockable a root of a tree
     */
    protected void store( Dockable dockable ){
        DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
            @Override
            public void handleDockable( Dockable check ) {
                String current = currentMode( check );
                if( current != null )
                    store( current, check );
            }
        });
    }

    /**
     * Stores the location of <code>dockable</code> under the key <code>mode</code>.
     * @param mode the mode <code>dockable</code> is currently in
     * @param dockable the element whose location will be stored
     */
    protected void store( String mode, Dockable dockable ){
        // there is not much to store for "maximized"...
        if( !MAXIMIZED.equals( mode )){
            setProperties( mode, dockable, currentLocation( mode, dockable ) );
        }
    }

    /**
     * Stores for each {@link Dockable} of the tree with the root 
     * <code>dockable</code> the current mode.
     * @param dockable the element whose mode should be stored
     */
    protected void putMode( Dockable dockable ){
        DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
            @Override
            public void handleDockable( Dockable check ) {
                String current = currentMode( check );
                if( current != null )
                    putMode( check, current );
            }
        });
    }

    /**
     * Creates the {@link Location} describing the location of <code>dockable</code>.
     * @param mode the current mode
     * @param dockable the element whose {@link Location} is created
     * @return the new {@link Location} or <code>null</code>
     */
    protected Location currentLocation( String mode, Dockable dockable ){
        String root = null;
        DockStation rootStation = null;

        rootStation = dockable.getDockParent();

        while( rootStation != null && root == null ){
            for( Map.Entry<String, DockStation> station : stations.entrySet() ){
                if( station.getValue() == rootStation ){
                    root = station.getKey();
                    break;
                }
            }

            if( root == null ){
                Dockable temp = rootStation.asDockable();
                rootStation = temp == null ? null : temp.getDockParent();
            }
        }

        if( root == null || rootStation == dockable ){
            return null;
        }
        else{
            DockableProperty location = DockUtilities.getPropertyChain( rootStation, dockable );
            return new Location( root, location );
        }
    }

    /**
     * Gets the name of the root of <code>dockable</code>.
     * @param dockable the element whose station is searched
     * @return the name of the root
     */
    protected String getRootName( Dockable dockable ){
        for( Map.Entry<String, DockStation> station : stations.entrySet() ){
            if( DockUtilities.isAncestor( station.getValue(), dockable )){
                return station.getKey();
            }
        }

        return null;
    }

    /**
     * Gets the station that is registered under <code>name</code>.
     * @param name the name of the station
     * @return the station or <code>null</code>
     */
    protected DockStation getStation( String name ){
        return stations.get( name );
    }

    /**
     * Describes the location of a {@link Dockable}.
     * @author Benjamin Sigg
     */
    public static class Location{
        /** the name of the root station */
        private String root;
        /** the exact location */
        private DockableProperty location;

        /**
         * Creates a new location.
         * @param root the name of the root station
         * @param location the location relatively to the root station
         */
        public Location( String root, DockableProperty location ){
            this.root = root;
            this.location = location;
        }

        /**
         * Gets the name of the root-station.
         * @return the name
         */
        public String getRoot() {
            return root;
        }

        /**
         * Gets the exact location.
         * @return the location
         */
        public DockableProperty getLocation() {
            return location;
        }
    }

    /**
     * A transformer to read or write {@link Location}s.
     * @author Benjamin Sigg
     *
     */
    public static class LocationConverter implements ModeTransitionConverter<Location, Location>{
        /** transformer to read or write single {@link DockableProperty}s */
        private PropertyTransformer transformer = new PropertyTransformer();

        public Location convertToSetting( Location a ) {
            return a;
        }
        public Location convertToWorld( Location b ) {
            return b;
        }

        public void writeProperty( Location element, DataOutputStream out ) throws IOException {
            Version.write( out, Version.VERSION_1_0_4 );
            out.writeUTF( element.root );
            transformer.write( element.location, out );
        }

        public Location readProperty( DataInputStream in ) throws IOException {
            Version version = Version.read( in );
            version.checkCurrent();
            String root = in.readUTF();
            DockableProperty location = transformer.read( in );
            return new Location( root, location );
        }

        public void writePropertyXML( Location b, XElement element ) {
            element.addElement( "root" ).setString( b.getRoot() );
            transformer.writeXML( b.getLocation(), element.addElement( "location" ) );
        }

        public Location readPropertyXML( XElement element ) {
            return new Location(
                    element.getElement( "root" ).getString(),
                    transformer.readXML( element.getElement( "location" ) ));
        }
    }

    /**
     * A set of properties used to store the contents of a {@link StateManager}
     * @author Benjamin Sigg
     * @param <B> the internal representation of the properties
     */
    public static class StateManagerSetting<B> extends ModeTransitionSetting<Location,B>{
        private Map<String,String> lastMaximizedMode;
        private Map<String,B> lastMaximizedLocation;

        /**
         * Creates a new setting.
         * @param converter converts internal and external properties
         */
        public StateManagerSetting( ModeTransitionConverter<Location, B> converter ) {
            super( converter );
        }

        /**
         * Sets the mode the last maximized elements were in.
         * @param lastMaximizedMode the modes or <code>null</code>
         */
        public void setLastMaximizedMode( Map<String,String> lastMaximizedMode ) {
            this.lastMaximizedMode = lastMaximizedMode;
        }

        /**
         * Gets the mode the last maximized elements were in.
         * @return the modes or <code>null</code>
         */
        public Map<String,String> getLastMaximizedMode() {
            return lastMaximizedMode;
        }

        /**
         * Sets the location of the last elements that were maximized.
         * @param lastMaximizedLocation the locations or <code>null</code>
         */
        public void setLastMaximizedLocation( Map<String,Location> lastMaximizedLocation ) {
            if( lastMaximizedLocation == null ){
                this.lastMaximizedLocation = null;
            }
            else{
                this.lastMaximizedLocation = new HashMap<String, B>();
                for( Map.Entry<String, Location> entry : lastMaximizedLocation.entrySet() ){
                    this.lastMaximizedLocation.put( entry.getKey(), getConverter().convertToSetting( entry.getValue() ) );
                }
            }
        }

        /**
         * Gets the location of the last elements that were maximized.
         * @return the locations or <code>null</code>
         */
        public Map<String,Location> getLastMaximizedLocation() {
            if( lastMaximizedLocation == null )
                return null;

            Map<String, Location> result = new HashMap<String, Location>();
            for( Map.Entry<String, B> entry : this.lastMaximizedLocation.entrySet() ){
                result.put( entry.getKey(), getConverter().convertToWorld( entry.getValue() ) );
            }
            return result;
        }

        @Override
        public void write( DataOutputStream out ) throws IOException {
            /*Version.write( out, Version.VERSION_1_0_4 );
    	    super.write( out );
    	    if( lastMaximizedMode == null ){
    		out.writeBoolean( false );
    	    }
    	    else{
    		out.writeBoolean( true );
    		out.writeUTF( lastMaximizedMode );
    	    }
    
    	    if( lastMaximizedLocation == null ){
    		out.writeBoolean( false );
    	    }
    	    else{
    		out.writeBoolean( true );
    		getConverter().writeProperty( lastMaximizedLocation, out );
    	    }*/

            Version.write( out, Version.VERSION_1_0_7 );
            super.write( out );
            if( lastMaximizedMode == null ){
                out.writeInt( 0 );
            }
            else{
                int count = 0;
                for( String check : lastMaximizedMode.values() ){
                    if( check != null ){
                        count++;
                    }
                }

                out.writeInt( count );
                for( Map.Entry<String, String> entry : lastMaximizedMode.entrySet() ){
                    if( entry.getValue() != null ){
                        out.writeUTF( entry.getKey() );
                        out.writeUTF( entry.getValue() );
                    }
                }
            }

            if( lastMaximizedLocation == null ){
                out.writeInt( 0 );
            }
            else{
                int count = 0;
                for( B mode : lastMaximizedLocation.values() ){
                    if( mode != null ){
                        count++;
                    }
                }

                out.writeInt( count );
                for( Map.Entry<String, B> entry : lastMaximizedLocation.entrySet() ){
                    if( entry.getValue() != null ){
                        out.writeUTF( entry.getKey() );
                        getConverter().writeProperty( entry.getValue(), out );
                    }
                }
            }
        }

        @Override
        public void read( DataInputStream in ) throws IOException {
            Version version = Version.read( in );
            version.checkCurrent();

            lastMaximizedLocation = new HashMap<String, B>();
            lastMaximizedMode = new HashMap<String, String>();

            if( version.compareTo( Version.VERSION_1_0_7 ) < 0 ){
                super.read( in );

                // ignore these settings from the old format
                if( in.readBoolean() ){
                    in.readUTF();
                }

                if( in.readBoolean() ){
                    getConverter().readProperty( in );
                }
            }
            else{
                super.read( in );
                
                int count = in.readInt();
                for( int i = 0; i < count; i++ ){
                    String key = in.readUTF();
                    String value = in.readUTF();
                    lastMaximizedMode.put( key, value );
                }

                count = in.readInt();
                for( int i = 0; i < count; i++ ){
                    String key = in.readUTF();
                    B value = getConverter().readProperty( in );
                    lastMaximizedLocation.put( key, value );
                }
            }
        }

        @Override
        public void writeXML( XElement element ) {
            super.writeXML( element.addElement( "states" ) );

            Set<String> keys = new HashSet<String>();
            if( lastMaximizedLocation != null ){
                keys.addAll( lastMaximizedLocation.keySet() );
            }
            if( lastMaximizedMode != null ){
                keys.addAll( lastMaximizedMode.keySet() );
            }

            if( !keys.isEmpty() ){
                XElement xmaximized = element.addElement( "maximized" );

                for( String key : keys ){
                    String mode = lastMaximizedMode.get( key );
                    B location = lastMaximizedLocation.get( key );

                    if( mode != null || location != null ){
                        XElement xitem = xmaximized.addElement( "item" );
                        xitem.addString( "id", key );
                        if( mode != null ){
                            xitem.addElement( "mode" ).setString( mode );
                        }
                        if( location != null ){
                            getConverter().writePropertyXML( location, xitem.addElement( "location" ) );
                        }
                    }
                }
            }
        }


        @Override
        public void readXML( XElement element ) {
            XElement states = element.getElement( "states" );
            if( states == null ){
                super.readXML( element );                
            }
            else{
                super.readXML( states );

                lastMaximizedLocation = new HashMap<String, B>();
                lastMaximizedMode = new HashMap<String, String>();

                XElement xmaximized = element.getElement( "maximized" );

                if( xmaximized != null ){
                    for( XElement xitem : xmaximized.getElements( "item" )){
                        String key = xitem.getString( "id" );

                        XElement xmode = xitem.getElement( "mode" );
                        if( xmode != null ){
                            lastMaximizedMode.put( key, xmode.getString() );
                        }

                        XElement xlocation = xitem.getElement( "location" );
                        if( xlocation != null ){
                            lastMaximizedLocation.put( key, getConverter().readPropertyXML( xlocation ) );
                        }
                    }
                }
            }
        }
    }

    /**
     * A set of {@link Dockable}s built while changing the mode of some
     * <code>Dockable</code>s. This set contains all the <code>Dockable</code>
     * which might have changed their mode.
     * @author Benjamin Sigg
     */
    private class AffectedSet{
        /** the changed elements */
        private Set<Dockable> set = new HashSet<Dockable>();

        /**
         * Adds <code>dockable</code> and its children to this set.
         * @param dockable the element to add
         */
        public void add( Dockable dockable ){
            set.add( dockable );
            DockStation station = dockable.asDockStation();
            if( station != null ){
                for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
                    add( station.getDockable( i ));
                }
            }
        }

        /**
         * Performs the clean up operations that are required after some
         * <code>Dockable</code>s have changed their mode.<br>
         * This includes calling {@link StateManager#putMode(Dockable, String)}
         * for each element known to this set.
         */
        public void finish(){
            for( Dockable dockable : set ){
                putMode( dockable, currentMode( dockable ) );
            }
        }

        /**
         * Tells whether there are no entries in this set.
         * @return <code>true</code> if there are no entries
         */
        public boolean isEmpty(){
            return set.isEmpty();
        }
    }

    /**
     * A listener informing the enclosing manager when a {@link Dockable} changes
     * its mode.
     * @author Benjamin Sigg
     */
    private class Listener implements MaximizeAreaListener, DockRelocatorListener, DockRegisterListener, DockHierarchyListener{
        public void maximizedChanged( MaximizeArea area, Dockable oldElement, Dockable newElement ) {
            if( !onTransition ){
                if( oldElement != null )
                    putMode( oldElement );

                if( newElement != null )
                    putMode( newElement );
            }
        }

        public void init( DockController controller, Dockable dockable ) {
            // ignore
        }

        public void cancel( DockController controller, Dockable dockable ) {
            // ignore
        }

        public void drag( DockController controller, Dockable dockable, DockStation station ) {
            store( currentMode( dockable ), dockable );
        }

        public void drop( DockController controller, Dockable dockable, DockStation station ) {
            AffectedSet affected = new AffectedSet();
            unmaximize( station, affected );
            affected.finish();
        }

        public void dockStationRegistered( DockController controller, DockStation station ){
            // ignore
        }

        public void dockStationRegistering( DockController controller, DockStation station ){
            // ignore
        }

        public void dockStationUnregistered( DockController controller, DockStation station ){
            // ignore
        }

        public void dockableRegistered( DockController controller, Dockable dockable ){
            dockable.addDockHierarchyListener( this );
        }

        public void dockableRegistering( DockController controller, Dockable dockable ){
            // ignore
        }

        public void dockableUnregistered( DockController controller, Dockable dockable ){
            dockable.removeDockHierarchyListener( this );
        }

        public void controllerChanged( DockHierarchyEvent event ) {
            // ignore
        }

        public void dockableCycledRegister( DockController controller, Dockable dockable ) {
            // ignore
        }

        public void hierarchyChanged( DockHierarchyEvent event ) {
            if( !onTransition ){
                rebuild( event.getDockable() );
            }
        }
    }
}
