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
package bibliothek.gui.dock.control;

import java.util.*;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockRegisterListener;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.station.LayoutLocked;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A set of all {@link bibliothek.gui.Dockable Dockables} and
 * {@link bibliothek.gui.DockStation DockStations} currently used in the
 * system. 
 * @author Benjamin Sigg
 */
@LayoutLocked( locked=false )
public class DockRegister {
	/** the known stations */
    private List<DockStation> stations = new ArrayList<DockStation>();
    /** the known dockables */
    private List<Dockable> dockables = new ArrayList<Dockable>();
    
    /** The controller for which the dockables and stations are stored */
    private DockController controller;
    
    /** a list of registerListeners which are informed whenever the registered dockables and stations change */
    private List<DockRegisterListener> registerListeners = new ArrayList<DockRegisterListener>();
    
    /** an observer of the stations */
    private StationListener stationListener = new StationListener();
    
    /** tells whether register and unregister-events should be stalled or not */
    private int stalled = 0;
    
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
    	registerListeners.add( listener );
    }
    
    /**
     * Removes a listener from this register.
     * @param listener the listener to remove
     * @see #addDockRegisterListener(DockRegisterListener)
     */
    public void removeDockRegisterListener( DockRegisterListener listener ){
    	registerListeners.remove( listener );
    }
    
    /**
     * Removes all registerListeners and connections to the stations and dockables
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
    	add( station, true );
    }
    
    /**
     * Adds a station to this register. The associated controller allows the user to
     * drag and drop children from and to <code>station</code>. If
     * the children of <code>station</code> are stations itself, then
     * they will be added automatically
     * @param station the new station
     * @param requiresListeners if <code>true</code>, then {@link #stationListener} is added to any {@link DockStation}
     * encountered in the tree beginning with <code>station</code>
     */
    private void add( DockStation station, final boolean requiresListeners ){
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
                    register( station, requiresListeners );
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
     * Gets the number of dockables registered at this {@link DockRegister}.
     * @return the number of dockables
     */
    public int getDockableCount(){
        return dockables.size();
    }
    
    /**
     * Gets the index'th {@link Dockable} that is registered at this {@link DockRegister}.
     * @param index the location of the {@link Dockable}
     * @return the element
     */
    public Dockable getDockable( int index ){
        return dockables.get( index );
    }
    
    /**
     * Gets a list of all Dockables.
     * @return the list of Dockables
     */
    public Dockable[] listDockables(){
    	return dockables.toArray( new Dockable[ dockables.size() ] );
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
     * @param requiresListener if <code>true</code>, then the {@link DockStationListener} of this 
     * {@link DockRegister} will be added to <code>station</code>, if <code>false</code> the
     * listener will not be added
     */
    protected void register( DockStation station, boolean requiresListener ){
        if( !stations.contains( station )){
        	fireDockStationRegistering( station );
            
            stations.add( station );
            
            station.setController( controller );
            station.updateTheme();
            
            if( requiresListener ){
            	station.addDockStationListener( stationListener );
            }
            
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
     * Gets a list of all registerListeners which are registered.
     * @return the list of registerListeners
     */
    protected DockRegisterListener[] listDockRegisterListeners(){
    	return registerListeners.toArray( new DockRegisterListener[ registerListeners.size() ] );
    }
    
    /**
     * Informs all registerListeners that a {@link Dockable} will be registered.
     * @param dockable the Dockable which will be registered
     */
    protected void fireDockableRegistering( Dockable dockable ){
        for( DockRegisterListener listener : listDockRegisterListeners() )
            listener.dockableRegistering( controller, dockable );
    }
    
    /**
     * Informs all registerListeners that a {@link Dockable} has been registered.
     * @param dockable the registered Dockable
     */
    protected void fireDockableRegistered( Dockable dockable ){
        for( DockRegisterListener listener : listDockRegisterListeners() )
            listener.dockableRegistered( controller, dockable );
    }

    /**
     * Informs all registerListeners that a {@link Dockable} has been
     * unregistered.
     * @param dockable the unregistered Dockable
     */
    protected void fireDockableUnregistered( Dockable dockable ){
        for( DockRegisterListener listener : listDockRegisterListeners() )
            listener.dockableUnregistered( controller, dockable );
    }

    /**
     * Informs all registerListeners that <code>station</code> will be registered.
     * @param station the new station
     */
    protected void fireDockStationRegistering( DockStation station ){
        for( DockRegisterListener listener : listDockRegisterListeners() )
            listener.dockStationRegistering( controller, station );
    }
    
    /**
     * Informs all registerListeners that <code>station</code> has been registered.
     * @param station the new station
     */
    protected void fireDockStationRegistered( DockStation station ){
        for( DockRegisterListener listener : listDockRegisterListeners() )
            listener.dockStationRegistered( controller, station );
    }
    
    /**
     * Informs all registerListeners that <code>station</code> has been unregistered.
     * @param station the unregistered station
     */
    protected void fireDockStationUnregistered( DockStation station ){
        for( DockRegisterListener listener : listDockRegisterListeners() )
            listener.dockStationUnregistered( controller, station );
    }
    
    /**
     * Informs all RegisterListeners that <code>dockable</code> cycled
     * the register.
     * @param dockable the cycling element
     */
    protected void fireStalledChange( Dockable dockable ){
        for( DockRegisterListener listener : listDockRegisterListeners() )
            listener.dockableCycledRegister( controller, dockable );
    }
    
    /**
     * Sets whether the listener to all {@link DockStation} should forward changes
     * of the tree to the <code>un-/register</code>-methods or not. If the
     * register was stalled and now the argument is <code>false</code>, then
     * all pending events will be handled immediately.<br>
     * Nested calls to this method are possible, if <code>setStalled</code> was
     * called two times with <code>true</code>, then the events will be fired only
     * after <code>setStalled</code> was called twice with <code>false</code>.
     * @param stalled <code>true</code> if events should be stalled, <code>false</code>
     * if all pending events should be handled and new events should be handled
     * immediately
     */
    public void setStalled( boolean stalled ){
		if( stalled )
		    this.stalled++;
		else
		    this.stalled--;
		
		// recover from too many false-stalled calls
		if( this.stalled < 0 )
			this.stalled = 0;
		
		if( this.stalled == 0 ){
			stationListener.fire();
		}
	}
    
    /**
     * Whether the register is currently stalled and does not forward
     * changes to the tree.
     * @return <code>true</code> if stalled
     */
    public boolean isStalled(){
    	return stalled > 0;
    }

    /** tells what state a changing {@link Dockable} currently is in, used by the {@link StationListener} only */
    private enum Status{
    	ADDED, REMOVED, ADDED_AND_REMOVED, REMOVED_AND_ADDED
    }
    
    /**
     * A listener to the controller of the enclosing register. Ensures that 
     * stations and dockables are known even while the tree of elements is changed.
     * @author Benjamin Sigg
     */
    private class StationListener extends DockStationAdapter{
    	/** the current state of changing elements */
    	private Map<Dockable, Status> changeMap = new HashMap<Dockable, Status>();
    	/** the order in which the elements of {@link #changeMap} first appeared */
    	private LinkedList<Dockable> changeQueue = new LinkedList<Dockable>();
    	
        /** whether this listener is currently firing the stalled events */
        private boolean firing = false;
        
        public void fire(){
            if( !firing ){
                try{
                    firing = true;
                    
                    while( !changeQueue.isEmpty() ){
                    	Dockable next = changeQueue.removeFirst();
                    	Status status = changeMap.remove( next );
                    	switch( status ){
                    		case ADDED:
                    			addDockable( next, false );
                    			break;
                    		case REMOVED:
                    			removeDockable( next );
                    			break;
                    		
                    		case ADDED_AND_REMOVED:
                    		case REMOVED_AND_ADDED:
                    			fireStalledChange( next );
                    			break;
                    	}
                    }
                }
                finally{
                    firing = false;
                }
            }
        }
        
        @Override
        public void dockableAdding( DockStation station, Dockable dockable ) {
            if( stalled > 0 ){
                DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
                    @Override
                    public void handleDockable( Dockable dockable ) {
                    	Status current = changeMap.get( dockable );
                    	if( current == null ){
                    		changeMap.put( dockable, Status.ADDED );
                    		changeQueue.add( dockable );
                    	}
                    	else{
                    		switch( current ){
                    			case REMOVED:
                    				changeMap.put( dockable, Status.REMOVED_AND_ADDED );
                    				break;
                    			case ADDED_AND_REMOVED:
                    				changeMap.put( dockable, Status.ADDED );
                    				break;
                    		}
                    	}
                    }
                    
                    @Override
                    public void handleDockStation( DockStation station ) {
                    	station.addDockStationListener( StationListener.this );
                    }
                });
            }
            else{
                addDockable( dockable, true );
            }
        }
        
        /**
         * Adds a Dockable either as station or as pure Dockable to this
         * controller.
         * @param dockable the Dockable to register
         * @param requiresListener whether the listener of this {@link DockRegister} has
         * to be added to the {@link DockStation} <code>dockable</code> or not
         */
        private void addDockable( Dockable dockable, boolean requiresListener ){
            DockStation asStation = dockable.asDockStation();
            
            if( asStation != null ){
                add( asStation, requiresListener );
            }
            else{
                register( dockable );
            }
        }

        @Override
        public void dockableRemoving( DockStation station, Dockable dockable ) {
            if( stalled > 0 ){
                DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
                    @Override
                    public void handleDockable( Dockable dockable ) {
                    	Status current = changeMap.get( dockable );
                    	if( current == null ){
                    		changeMap.put( dockable, Status.REMOVED );
                    		changeQueue.add( dockable );
                    	}
                    	else{
                    		switch( current ){
                    			case ADDED:
                    				changeMap.put( dockable, Status.ADDED_AND_REMOVED );
                    				break;
                    			case REMOVED_AND_ADDED:
                    				changeMap.put( dockable, Status.REMOVED );
                    				break;
                    		}
                    	}
                    }
                });
            }
        }
        
        @Override
        public void dockableRemoved( DockStation station, Dockable dockable ) {
        	if( dockable.getDockParent() != null && dockable.getDockParent() != station ){
        		throw new IllegalStateException( "the parent of dockable is wrong: it is neither null nor '" + station + "'" );
        	}
            dockable.setDockParent( null );
            
            DockStation asStation = dockable.asDockStation();
            if( asStation != null ){
            	asStation.removeDockStationListener( StationListener.this );
            }
            
            if( stalled == 0 ){
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
            
            if( asStation != null ){
                remove( asStation );
            }
            else{
                unregister( dockable );
            }
        }
    }
}
