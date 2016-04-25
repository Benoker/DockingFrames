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

package bibliothek.gui.dock.title;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * These events are used for {@link DockTitle DockTitles}, to transfer information
 * between {@link DockStation station} and title. See the method 
 * {@link DockTitle#changed(DockTitleEvent)} for more details.
 * @author Benjamin Sigg
 */
public class DockTitleEvent {
    /** The station on which the title is shown */
    private DockStation station;
    
    /** The dockable for which the title is rendered */
    private Dockable dockable;
    
    /**
     * Constructs a new event. 
     * @param dockable the {@link Dockable} for which the target-title
     * is rendered
     */
    public DockTitleEvent( Dockable dockable ){
        this( null, dockable );
    }

    /**
     * Constructs a new event. This constructor should only be called
     * if a {@link DockStation} itself sends the event. Other components
     * should use {@link #DockTitleEvent(Dockable)}.
     * @param station the station on which the target-title is displayed
     * @param dockable the {@link Dockable} for which the target-title
     * is rendered
     */
    public DockTitleEvent( DockStation station, Dockable dockable ){
        if( dockable == null )
            throw new IllegalArgumentException( "dockable must not be null" );
        
        this.dockable = dockable;
        this.station = station;
    }
    
    /**
     * Gets the station which created the event, and on which the target-title
     * is displayed.
     * @return the station, <code>null</code> if the event was not sent
     * by the station
     */
    public DockStation getStation() {
        return station;
    }
    
    /**
     * Gets the {@link Dockable} for which the target-title is used.
     * @return the owner of the title
     */
    public Dockable getDockable() {
        return dockable;
    }
    
}
