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

package bibliothek.gui.dock.station.support;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
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
     * Invokes the method {@link DockStationListener#dockableShowingChanged(DockStation, Dockable, boolean)}
     * on all registered {@link DockStationListener DockStationListeners}.
     * @param dockable the {@link Dockable} whose visibility has changed
     * @param value the new state
     */
    public void fireDockableVisibilitySet( Dockable dockable, boolean value ){
    	for( DockStationListener listener : listeners.toArray( new DockStationListener[ listeners.size() ] ))
            listener.dockableShowingChanged( station, dockable, value );
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
    
    /**
     * Invokes the method {@link DockStationListener#dockableSelected(DockStation, Dockable, Dockable)}
     * on all registered {@link DockStationListener}s.
     * @param oldSelected the element which was selected earlier
     * @param newSelected the selected {@link Dockable}
     */
    public void fireDockableSelected( Dockable oldSelected, Dockable newSelected ){
        for( DockStationListener listener : listeners.toArray( new DockStationListener[ listeners.size() ] ))
            listener.dockableSelected( station, oldSelected, newSelected );
    }
    
    /**
     * Invokes {@link DockStationListener#dockablesRepositioned(DockStation, Dockable[])} on all
     * registered {@link DockStationListener}s.
     * @param dockables the elements that have a new position
     */
    public void fireDockablesRepositioned( Dockable... dockables ){
    	for( DockStationListener listener : listeners.toArray( new DockStationListener[ listeners.size() ] ))
            listener.dockablesRepositioned( station, dockables );
    }
}
