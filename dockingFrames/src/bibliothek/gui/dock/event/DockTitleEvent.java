/**
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

package bibliothek.gui.dock.event;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.title.DockTitle;

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
    
    private boolean active, preferred;
    
    /**
     * Constructs a new event. 
     * @param dockable the {@link Dockable} for which the target-title
     * is rendered
     * @param active <code>true</code> if <code>dockable</code> is the
     * selected and focused child, <code>false</code> otherwise
     */
    public DockTitleEvent( Dockable dockable, boolean active ){
        this( null, dockable, active );
    }

    /**
     * Constructs a new event. This constructor should only be called
     * if a {@link DockStation} itself sends the event. Other components
     * should use {@link #DockTitleEvent(Dockable, boolean)}.
     * @param station the station on which the target-title is displayed
     * @param dockable the {@link Dockable} for which the target-title
     * is rendered
     * @param active <code>true</code> if <code>dockable</code> is the
     * selected and focused child, <code>false</code> otherwise
     */
    public DockTitleEvent( DockStation station, Dockable dockable, boolean active ){
        if( dockable == null )
            throw new IllegalArgumentException( "dockable must not be null" );
        
        this.station = station;
        this.active = active;
    }
    
    /**
     * Gets the title which created the event, and on which the target-title
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
    
    /**
     * Returns whether the target-title should be painted in a "focused"-state.
     * @return <code>true</code> if the {@link Dockable} is focused,
     * <code>false</code> otherwise.
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Tells whether the {@link Dockable} is preferred in some way by the station.
     * If so, a {@link DockTitle} can be drawn slightly different than
     * a normal title.
     * @return <code>true</code> if the {@link Dockable} is a very special
     * {@link Dockable}
     */
    public boolean isPreferred() {
        return preferred;
    }
    
    /**
     * Sets whether the {@link Dockable} is preferred.
     * @param preferred <code>true</code> if the target-title should be
     * painted in a special way
     * @see #isPreferred()
     */
    public void setPreferred( boolean preferred ) {
        this.preferred = preferred;
    }
}
