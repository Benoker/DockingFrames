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

import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.event.DockStationAdapter;

/**
 * Knows for every child of a DockStation whether it is visible or not. The 
 * station can call the {@link #fire()}-method to send events to listeners,
 * if the visibility of some children has changed. The manager fires events
 * automatically if a {@link Dockable} is added or removed from the 
 * parent station. The manager tries to minimize the number of messages
 * sent to the listeners.
 * @author Benjamin Sigg
 */
public class DockableVisibilityManager extends DockStationAdapter{
    private DockStationListenerManager listeners;
    private Map<Dockable, Boolean> visibility = new HashMap<Dockable, Boolean>();
    
    /**
     * Constructs a new manager
     * @param listeners the listeners used to fire events
     */
    public DockableVisibilityManager( DockStationListenerManager listeners ){
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
            boolean visible = station.isVisible( dockable );
            if( !visibility.containsKey( dockable ) || 
                    visibility.get( dockable ) != visible ){
                listeners.fireDockableVisibilitySet( dockable, visible );
                visibility.put( dockable, visible );
            }
        }
    }
    
    @Override
    public void dockableAdded( DockStation station, Dockable dockable ) {
        boolean visible = station.isVisible( dockable );
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
