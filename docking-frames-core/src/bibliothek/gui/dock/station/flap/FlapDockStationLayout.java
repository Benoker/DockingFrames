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
package bibliothek.gui.dock.station.flap;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.FlapDockStation.Direction;
import bibliothek.gui.dock.station.support.PlaceholderMap;

/**
 * A layout describing the content of a {@link FlapDockStation}.
 * @author Benjamin Sigg
 */
public class FlapDockStationLayout {
    /** whether the direction of the window is chosen automatically or not */
    private boolean autoDirection;
    
    /** the direction of the window */
    private Direction direction;
    
    /** placeholders for all the items, contains also the encoded {@link Dockable}s. */
    private PlaceholderMap placeholders;

    /**
     * Creates a new layout
     * @param autoDirection whether the direction of the window is chosen
     * automatically
     * @param direction the direction into which the window opens
     * @param placeholders placeholders for all the items, contains also the encoded {@link Dockable}s.
     */
    public FlapDockStationLayout( boolean autoDirection, Direction direction, PlaceholderMap placeholders ) {
        this.autoDirection = autoDirection;
        this.direction = direction;
        this.placeholders = placeholders;
    }
    
    /**
     * Gets the direction into which the window will open
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }
    
    /**
     * Tells whether the direction is chosen automatically or not.
     * @return <code>true</code> if the direction is chosen automatically
     */
    public boolean isAutoDirection() {
        return autoDirection;
    }
    
    /**
     * Gets all the items.
     * @return the location of all items and all placeholders
     */
    public PlaceholderMap getPlaceholders(){
		return placeholders;
	}
}
