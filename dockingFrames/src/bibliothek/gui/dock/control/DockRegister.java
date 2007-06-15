package bibliothek.gui.dock.control;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockRegisterListener;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A set of all {@link bibliothek.gui.Dockable Dockables} and
 * {@link bibliothek.gui.DockStation DockStations} currently used in the
 * system. 
 * @author Benjamin Sigg
 *
 */
public class DockRegister {
	/** the known stations */
    private List<DockStation> stations = new ArrayList<DockStation>();
    /** the known dockables */
    private List<Dockable> dockables = new ArrayList<Dockable>();
    
    /** The controller for which the dockables and stations are stored */
    private DockController controller;
    
    /** a list of listeners which are informed whenever the registered dockables and stations change */
    private List<DockRegisterListener> listeners = new ArrayList<DockRegisterListener>();
    
    /** an observer of the stations */
    private StationListener stationListener = new StationListener();
    
    /** tells whether register and unregister-events should be stalled or not */
    private boolean stalled = false;
    
    /**
     * Creates a new register.
     * @param controller the controller for which the dockables and stations
     * are stored.
     */
    public DockRegister( DockController controller ){
    	if( controller == null )
    		throw new IllegalArgumentException( "controller must not be null" );
    	
    	this.controller = controller;
    }
    
    /**
     * Gets the controller for which this register stores Dockables and DockStations.
     * @return the controller
     */
    public DockController getController(){
		return controller;
	}
    
    /**
     * Registers a listener which will receive notifications when a
     * {@link Dockable} or a {@link DockStation} is added or removed from
     * this register.
     * @param listener the new listener
     */
    public void addDockRegisterListener( DockRegisterListener listener ){
    	listeners.add( listener );
    }
    
    /**
     * Removes a listener from this register.
     * @param listener the listener to remove
     * @see #addDockRegisterListener(DockRegisterListener)
     */
    public void removeDockRegisterListener( DockRegisterListener listener ){
    	listeners.remove( listener );
    }
    
    /**
     * Removes all listeners and connections to the stations and dockables
     * known to this register.
     */
    public void kill(){
        List<DockStation> stations = new ArrayList<DockStation>( this.stations );
        for( DockStation station : stations )
            remove( station );
    }
    
    /**
     * Adds a station to this register. The associated controller allows the user to
     * drag and drop children from and to <code>station</code>. If
     * the children of <code>station</code> are stations itself, then
     * they will be added automatically
     * @param station the new station
     */
    public void add( DockStation station ){
    	if( station == null )
            throw new NullPointerException( "Station must not be null" );
    	
        if( !stations.contains( station )){
            DockController other = station.getController();
            if( other != null && other != controller ){
                other.getRegister().remove( station );
            }
            
            DockUtilities.visit( station, new DockUtilities.DockVisitor(){
                @Override
                public void handleDockable( Dockable dockable ) {
                    register( dockable );
                }
                @Override
                public void handleDockStation( DockStation station ) {
                    register( station );
                }
            });
        }
    }
    
    /**
     * Removes a station which was managed by this register.
     * @param station the station to remove
     */
    public void remove( DockStation station ){
        if( stations.contains( station )){
            Dockable dock = station.asDockable();
            if( dock != null ){
                DockStation parent = dock.getDockParent();
                if( parent != null )
                    parent.drag( dock );
            }
            
            DockUtilities.visit( station, new DockUtilities.DockVisitor(){
                @Override
                public void handleDockable( Dockable dockable ) {
                    unregister( dockable );
                }
                @Override
                public void handleDockStation( DockStation station ) {
                    unregister( station );
                }
            });
        }
    }
    
    /**
     * Gest the number of stations that are registered.
     * @return the number of stations
     * @see #add(DockStation)
     */
    public int getStationCount(){
        return stations.size();
    }
    
    /**
     * Gets the station at the specified position.
     * @param index the location
     * @return the station
     */
    public DockStation getStation( int index ){
        return stations.get( index );
    }
    
    /**
     * Gets an array containing all known {@link DockStation DockStations}.
     * @return the modifiable array of stations
     */
    public DockStation[] listDockStations(){
    	return stations.toArray( new DockStation[ stations.size() ] );
    }

    /**
     * Gets a list of stations which have no parent and are therefore
     * the roots of the dock-trees.
     * @return the roots
     */
    public DockStation[] listRoots(){
        List<DockStation> list = new LinkedList<DockStation>();
        for( DockStation station : stations ){
            Dockable dockable = station.asDockable();
            if( dockable == null || dockable.getDockParent() == null )
                list.add( station );
        }
        
        return list.toArray( new DockStation[ list.size() ] );
    }
    
    
    /**
     * Registers <code>dockable</code>, the associated controller will know the titles
     * of <code>dockable</code> to allow drag and drop operations.<br>
     * Clients and subclasses should not call this method.
     * @param dockable a new Dockable
     */
    protected void register( Dockable dockable ){
        if( !dockables.contains( dockable )){
            fireDockableRegistering( dockable );
            
            dockables.add( dockable );
            dockable.setController( controller );
            
            fireDockableRegistered( dockable );
        }
    }
    
    /**
     * Unregisters <code>dockable</code>, the associated controller will no longer 
     * support drag and drop for <code>dockable</code>.<br>
     * Clients and subclasses should not call this method.
     * @param dockable the element to remove
     */
    protected void unregister( Dockable dockable ){
        if( dockables.remove( dockable ) ){
            dockable.setController( null );
            
            fireDockableUnregistered( dockable );
        }
    }
    
    /**
     * Registers <code>station</code>, the associated controller will support
     * drag and drop for <code>station</code>.<br>
     * Clients and subclasses should not call this method.
     * @param station the station to add
     */
    protected void register( DockStation station ){
        if( !stations.contains( station )){
        	fireDockStationRegistering( station );
            
            stations.add( station );
            station.addDockStationListener( stationListener );
            station.setController( controller );
            station.updateTheme();
            
            fireDockStationRegistered( station );
        }
    }
    
    /**
     * Unregisters <code>station</code>, the associated controller will no longer
     * support drag and drop operations for <code>station</code>.<br>
     * Clients and subclasses should not call this method.
     * @param station the station to remove
     */
    protected void unregister( DockStation station ){
        if( stations.remove( station ) ){
            station.setController( null );
            station.removeDockStationListener( stationListener );
            
            fireDockStationUnregistered( station );
        }
    }

    /**
     * Gets a list of all listeners which are registered.
     * @return the list of listeners
     */
    protected DockRegisterListener[] listDockRegisterListeners(){
    	return listeners.toArray( new DockRegisterListener[ listeners.size() ] );
    }
    
    /**
     * Informs all listeners that a {@link Dockable} will be registered.
     * @param dockable the Dockable which will be registered
     */
    protected void fireDockableRegistering( Dockable dockable ){
        for( DockRegisterListener listener : listDockRegisterListeners() )
            listener.dockableRegistering( controller, dockable );
    }
    
    /**
     * Informs all listeners that a {@link Dockable} has been registered.
     * @param dockable the registered Dockable
     */
    protected void fireDockableRegistered( Dockable dockable ){
        for( DockRegisterListener listener : listDockRegisterListeners() )
            listener.dockableRegistered( controller, dockable );
    }

    /**
     * Informs all listeners that a {@link Dockable} has been
     * unregistered.
     * @param dockable the unregistered Dockable
     */
    protected void fireDockableUnregistered( Dockable dockable ){
        for( DockRegisterListener listener : listDockRegisterListeners() )
            listener.dockableUnregistered( controller, dockable );
    }

    /**
     * Informs all listeners that <code>station</code> will be registered.
     * @param station the new station
     */
    protected void fireDockStationRegistering( DockStation station ){
        for( DockRegisterListener listener : listDockRegisterListeners() )
            listener.dockStationRegistering( controller, station );
    }
    
    /**
     * Informs all listeners that <code>station</code> has been registered.
     * @param station the new station
     */
    protected void fireDockStationRegistered( DockStation station ){
        for( DockRegisterListener listener : listDockRegisterListeners() )
            listener.dockStationRegistered( controller, station );
    }
    
    /**
     * Informs all listeners that <code>station</code> has been unregistered.
     * @param station the unregistered station
     */
    protected void fireDockStationUnregistered( DockStation station ){
        for( DockRegisterListener listener : listDockRegisterListeners() )
            listener.dockStationUnregistered( controller, station );
    }
    
    /**
     * Sets whether the listener to all {@link DockStation} should forward changes
     * of the tree to the <code>un-/register</code>-methods or not. If the
     * register was stalled and now the argument is <code>false</code>, then
     * all pending events will be handled immediately.
     * @param stalled <code>true</code> if events should be stalled, <code>false</code>
     * if all pending events should be handled and new events should be handled
     * immediately
     */
    public void setStalled( boolean stalled ){
		this.stalled = stalled;
		if( !stalled ){
			stationListener.fire();
		}
	}
    
    /**
     * A listener to the controller of the enclosing register. Ensures that 
     * stations and dockables are known even while the tree of elements is changed.
     * @author Benjamin Sigg
     */
    private class StationListener extends DockStationAdapter{
        /** a set of Dockables which were removed during a drag and drop operation */
        private Set<Dockable> removedOnPut = new HashSet<Dockable>();
        /** a set of Dockable which were added during a drag and drop operation */
        private Set<Dockable> addedOnPut = new HashSet<Dockable>();
        
        public void fire(){
        	for( Dockable d : removedOnPut )
                removeDockable( d );
            
            for( Dockable d : addedOnPut )
                addDockable( d );
            
            removedOnPut.clear();
            addedOnPut.clear();
        }
        
        @Override
        public void dockableAdding( DockStation station, Dockable dockable ) {
            dockable.setDockParent( station );
            
            if( stalled ){
                DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
                    @Override
                    public void handleDockable( Dockable dockable ) {
                        addedOnPut.add( dockable );
                        removedOnPut.remove( dockable );
                    }
                });
            }
            else{
                addDockable( dockable );
            }
        }
        
        /**
         * Adds a Dockable either as station or as pure Dockable to this
         * controller.
         * @param dockable the Dockable to register
         */
        private void addDockable( Dockable dockable ){
            DockStation asStation = dockable.asDockStation();
            
            if( asStation != null )
                add( asStation );
            else
                register( dockable );
        }

        @Override
        public void dockableRemoving( DockStation station, Dockable dockable ) {
            if( stalled ){
                DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
                    @Override
                    public void handleDockable( Dockable dockable ) {
                        addedOnPut.remove( dockable );
                        removedOnPut.add( dockable );
                    }
                });
            }
        }
        
        @Override
        public void dockableRemoved( DockStation station, Dockable dockable ) {
            dockable.setDockParent( null );
            
            if( !stalled ){
                removeDockable( dockable );
            }
        }
        
        /**
         * Removes a Dockable either as station or as pure Dockable from
         * this controller.
         * @param dockable the Dockable to unregister
         */
        private void removeDockable( Dockable dockable ){
            DockStation asStation = dockable.asDockStation();
            
            if( asStation != null )
                remove( asStation );
            else
                unregister( dockable );
        }
    }
}
