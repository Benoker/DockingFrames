/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.station.support;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.event.DockStationListener;

/**
 * This manager stores {@link DockStationListener DockStationListeners}
 * and provides methods to invoke the listeners.
 * @author Benjamin Sigg
 *
 */
public class DockStationListenerManager {
	private List<DockStationListener> listeners = new ArrayList<DockStationListener>();
    private DockStation station;
    
    /**
     * Constructs a new manager.
     * @param station The station which shall be used as origin, not <code>null</code>
     */
    public DockStationListenerManager( DockStation station ){
    	if( station == null )
    		throw new IllegalArgumentException( "station must not be null" );
    	
    	this.station = station;
    }
    
    /**
     * Gets the station for which this manager collects listeners.
     * @return the station
     */
    public DockStation getStation() {
        return station;
    }
    
    /**
     * Adds a listener to this manager.
     * @param listener the new listener
     */
    public void addListener( DockStationListener listener ){
    	listeners.add( listener );
    }
    
    /**
     * Removes an earlier added listener.
     * @param listener the listener to remove
     */
    public void removeListener( DockStationListener listener ){
    	listeners.remove( listener );
    }
	
	/**
     * Invokes the method {@link DockStationListener#dockableVisibiltySet(DockStation, Dockable, boolean)}
     * on all registered {@link DockStationListener DockStationListeners}.
     * @param dockable the {@link Dockable} whose visibility has changed
     * @param value the new state
     */
    public void fireDockableVisibilitySet( Dockable dockable, boolean value ){
    	for( DockStationListener listener : listeners.toArray( new DockStationListener[ listeners.size() ] ))
            listener.dockableVisibiltySet( station, dockable, value );
    }
    
    /**
     * Invokes the method {@link DockStationListener#dockableAdded(DockStation, Dockable)}
     * on all registered {@link DockStationListener DockStationListeners}.
     * @param dockable the {@link Dockable} which was added
     */
    public void fireDockableAdded( Dockable dockable ){
        for( DockStationListener listener : listeners.toArray( new DockStationListener[ listeners.size() ] ))
            listener.dockableAdded( station, dockable );
    }
    
    /**
     * Invokes the method {@link DockStationListener#dockableRemoved(DockStation, Dockable)}
     * on all registered {@link DockStationListener DockStationListeners}.
     * @param dockable the {@link Dockable} which was removed
     */
    public void fireDockableRemoved( Dockable dockable ){
        for( DockStationListener listener : listeners.toArray( new DockStationListener[ listeners.size() ] ))
            listener.dockableRemoved( station, dockable );
    }
    
    /**
     * Invokes the method {@link DockStationListener#dockableAdding(DockStation, Dockable)}
     * on all registered {@link DockStationListener DockStationListeners}.
     * @param dockable the {@link Dockable} which will be added
     */
    public void fireDockableAdding( Dockable dockable ){
        for( DockStationListener listener : listeners.toArray( new DockStationListener[ listeners.size() ] ))
            listener.dockableAdding( station, dockable );
    }
    
    /**
     * Invokes the method {@link DockStationListener#dockableRemoving(DockStation, Dockable)}
     * on all registered {@link DockStationListener DockStationListeners}.
     * @param dockable the {@link Dockable} which will be removed
     */
    public void fireDockableRemoving( Dockable dockable ){
        for( DockStationListener listener : listeners.toArray( new DockStationListener[ listeners.size() ] ))
            listener.dockableRemoving( station, dockable );
    }
}
