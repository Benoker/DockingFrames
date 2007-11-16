package bibliothek.gui.dock.common.action;

import java.util.*;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockableProperty;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.common.util.Resources;
import bibliothek.gui.dock.event.DockRelocatorListener;
import bibliothek.gui.dock.event.SplitDockListener;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.ScreenDockStation;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.dock.util.DockUtilities;

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
    /** the areas used to show a maximized element */
    private Set<SplitDockStation> maxi = new HashSet<SplitDockStation>();
    
    /** the default station for maximized elements */
    private SplitDockStation defaultMaxi;
    
    /** the areas used to show a minimized element */
    private Set<FlapDockStation> mini = new HashSet<FlapDockStation>();
    
    /** the default station for minimized elements */
    private FlapDockStation defaultMini;
    
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
    
    /**
     * Creates a new manager. Adds listeners to the <code>controller</code>
     * and adds <code>this</code> as {@link ActionGuard} to the <code>controller</code>.
     * @param controller the controller which is observed by this manager.
     */
    public StateManager( DockController controller ){
        super( MINIMIZED, NORMALIZED, MAXIMIZED, EXTERNALIZED );
        controller.addActionGuard( this );
        controller.getRelocator().addDockRelocatorListener( listener );
        
        ResourceBundle bundle = Resources.getBundle();
        
        getIngoingAction( MINIMIZED ).setText( bundle.getString( "minimize.in" ) );
        getIngoingAction( MINIMIZED ).setTooltipText( bundle.getString( "minimize.in.tooltip" ) );
        //getOutgoingAction( MINIMIZED ).setText( bundle.getString( "minimize.out" ) );
        //getOutgoingAction( MINIMIZED ).setTooltipText( bundle.getString( "minimize.out.tooltip" ) );
        
        getIngoingAction( NORMALIZED ).setText( bundle.getString( "normalize.in" ) );
        getIngoingAction( NORMALIZED ).setTooltipText( bundle.getString( "normalize.in.tooltip" ) );
        //getOutgoingAction( NORMALIZED ).setText( bundle.getString( "normalize.out" ) );
        //getOutgoingAction( NORMALIZED ).setTooltipText( bundle.getString( "normalize.out.tooltip" ) );
        
        getIngoingAction( MAXIMIZED ).setText( bundle.getString( "maximize.in" ) );
        getIngoingAction( MAXIMIZED ).setTooltipText( bundle.getString( "maximize.in.tooltip" ) );
        //getOutgoingAction( MAXIMIZED ).setText( bundle.getString( "maximize.out" ) );
        //getOutgoingAction( MAXIMIZED ).setTooltipText( bundle.getString( "maximize.out.tooltip" ) );
        
        getIngoingAction( EXTERNALIZED ).setText( bundle.getString( "externalize.in" ) );
        getIngoingAction( EXTERNALIZED ).setTooltipText( bundle.getString( "externalize.in.tooltip" ) );
        //getOutgoingAction( EXTERNALIZED ).setText( bundle.getString( "externalize.out" ) );
        //getOutgoingAction( EXTERNALIZED ).setTooltipText( bundle.getString( "externalize.out.tooltip" ) );
    }
    
    /**
     * Adds a station to which a {@link Dockable} can be <i>normalized</i>
     * or <i>maximized</i>. If this is the first call to this method, then
     * <code>station</code> becomes the default station for this kind or
     * operation.
     * @param name the name of the station
     * @param station the new station.
     */
    public void add( String name, SplitDockStation station ){
        if( name == null )
            throw new NullPointerException( "name must not be null" );
        
        if( stations.containsKey( name ))
            throw new IllegalArgumentException( "There is already a station registered with that name" );
        
        if( station == null )
            throw new NullPointerException( "station must not be null" );
        
        stations.put( name, station );
        maxi.add( station );
        if( defaultMaxi == null )
            defaultMaxi = station;
        
        station.addSplitDockStationListener( listener );
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
        if( maxi.contains( dockable ) || mini.contains( dockable ) || external.contains( dockable ))
            return NORMALIZED;
        
        DockStation parent = dockable.getDockParent();
        while( parent != null ){
            if( mini.contains( parent ))
                return MINIMIZED;
            
            if( external.contains( parent ))
                return EXTERNALIZED;
            
            if( maxi.contains( parent )){
                SplitDockStation split = (SplitDockStation)parent;
                if( split.isFullScreen() && split.getFullScreen() == dockable )
                    return MAXIMIZED;
                else
                    return NORMALIZED;
            }
            
            dockable = parent.asDockable();
            parent = dockable == null ? null : dockable.getDockParent();
        }
        
        return NORMALIZED;
    }

    @Override
    protected String getDefaultMode( Dockable dockable ) {
        return NORMALIZED;
    }

    @Override
    protected void transition( String oldMode, String newMode, Dockable dockable ) {
        // store the old location
        store( oldMode, dockable );
        
        // read last location
        Location location = getProperties( newMode, dockable );
        boolean done = false;
        if( location != null ){
            DockStation station = stations.get( location.root );
            if( station != null && station != dockable.getDockParent() ){
                station.drop( dockable, location.location );
                done = true;
            }
        }
        
        if( !done ){
            // put onto default location
            if( MINIMIZED.equals( newMode ))
                checkedDrop( defaultMini, dockable );
            else if( EXTERNALIZED.equals( newMode ))
                checkedDrop( defaultExternal, dockable );
            else // normal or maximized
                checkedDrop( defaultMaxi, dockable );
        }
        
        boolean maximized = MAXIMIZED.equals( newMode );
        // make sure a maximized element is really maximized and
        // make sure the element is nor maximized when not needed
        
        DockStation parent = dockable.getDockParent();
        while( dockable != null && parent != null ){
            if( maxi.contains( parent ) && parent instanceof SplitDockStation ){
                ((SplitDockStation)parent).setFullScreen( maximized ? dockable : null );
            }
            
            dockable = parent.asDockable();
            parent = dockable == null ? null : dockable.getDockParent();
        }
    }
    
    /**
     * Drops <code>dockable</code> onto <code>station</code>, but only
     * if <code>station</code> is not the parent of <code>dockable</code>.
     * @param station the new parent of <code>dockable</code>
     * @param dockable the new child of <code>station</code>
     */
    private void checkedDrop( DockStation station, Dockable dockable ){
        if( station != dockable.getDockParent() )
            station.drop( dockable );
    }

    /**
     * Stores the location of <code>dockable</code> under the key <code>mode</code>.
     * @param mode the mode <code>dockable</code> is currently in
     * @param dockable the element whose location will be stored
     */
    private void store( String mode, Dockable dockable ){
        String root = null;
        DockStation rootStation = null;
        
        for( Map.Entry<String, DockStation> station : stations.entrySet() ){
            if( DockUtilities.isAnchestor( station.getValue(), dockable )){
                root = station.getKey();
                rootStation = station.getValue();
                break;
            }
        }
        
        if( root == null ){
            setProperties( mode, dockable, null );
        }
        else{
            Location location = new Location();
            location.root = root;
            location.location = DockUtilities.getPropertyChain( rootStation, dockable );
            setProperties( mode, dockable, location );
        }
    }
    
    /**
     * Describes the location of a {@link Dockable}.
     * @author Benjamin Sigg
     */
    private class Location{
        /** the name of the root station */
        public String root;
        /** the exact location */
        public DockableProperty location;
    }
    
    /**
     * A listener informing the enclosing manager when a {@link Dockable} changes
     * its mode.
     * @author Benjamin Sigg
     */
    private class Listener implements SplitDockListener, DockRelocatorListener{
        public void fullScreenDockableChanged( SplitDockStation station, Dockable oldFullScreen, Dockable newFullScreen ) {
            if( oldFullScreen != null )
                validate( oldFullScreen );
            
            if( newFullScreen != null )
                validate( newFullScreen );
        }

        public void dockableDrag( DockController controller, Dockable dockable, DockStation station ) {
            store( currentMode( dockable ), dockable );
        }

        public void dockablePut( DockController controller, Dockable dockable, DockStation station ) {
            validate( dockable );
        }
    }
}
