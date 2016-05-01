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

import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockStationAdapter;

/**
 * Knows for every child of a {@link DockStation} whether it is visible or not. The 
 * station can call the {@link #fire()}-method to send events to listeners,
 * if the visibility of some children has changed. The manager fires events
 * automatically if a {@link Dockable} is added or removed from the 
 * parent station. The manager tries to minimize the number of messages
 * sent to the listeners.
 * @author Benjamin Sigg
 */
public class DockableShowingManager extends DockStationAdapter{
    private DockStationListenerManager listeners;
    private Map<Dockable, Boolean> visibility = new HashMap<Dockable, Boolean>();
    
    /**
     * Constructs a new manager
     * @param listeners the listeners used to fire events
     */
    public DockableShowingManager( DockStationListenerManager listeners ){
        if( listeners == null )
            throw new IllegalArgumentException( "Listeners must not be null" );
        
        this.listeners = listeners;
        listeners.addListener( this );
    }
    
    /**
     * Checks which {@link Dockable Dockables} have changed their state and
     * fires events for them.
     */
    public void fire(){
        DockStation station = listeners.getStation();
        for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
            Dockable dockable = station.getDockable(i);
            boolean visible = station.isChildShowing( dockable );
            if( !visibility.containsKey( dockable ) || visibility.get( dockable ) != visible ){
                listeners.fireDockableVisibilitySet( dockable, visible );
                visibility.put( dockable, visible );
            }
        }
    }
    
    @Override
    public void dockableAdded( DockStation station, Dockable dockable ) {
        boolean visible = station.isChildShowing( dockable );
        listeners.fireDockableVisibilitySet( dockable, visible );
        visibility.put( dockable, visible );
    }
    
    @Override
    public void dockableRemoved( DockStation station, Dockable dockable ) {
        Boolean old = visibility.remove( dockable );
        if( old != null && old )
            listeners.fireDockableVisibilitySet( dockable, false );
    }
}
