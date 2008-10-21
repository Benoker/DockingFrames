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
package bibliothek.gui.dock.facile.state;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * A wrapper around a {@link DockStation}, this wrapper marks a station 
 * as beeing able to show a maximized {@link Dockable}.
 * @author Benjamin Sigg
 */
public interface MaximizeArea {
    /**
     * Gets a unique identifier for this area. The identifier must not
     * be shared with any other area that is used by the same controller.
     * @return the unique identifier
     */
    public String getUniqueId();
    
    /**
     * Gets the station which is represented by this {@link MaximizeArea}.
     * @return the station, never <code>null</code>
     */
    public DockStation getStation();
    
    /**
     * Adds a listener to this area. This listener will be informed whenever the
     * maximized dockable changes.
     * @param listener the new listener
     */
    public void addMaximizeAreaListener( MaximizeAreaListener listener );
    
    /**
     * Removes a listener from this area
     * @param listener the listener to remove
     */
    public void removeMaximizeAreaListener( MaximizeAreaListener listener );
    
    /**
     * Somehow makes <code>dockable</code> child of this station. This
     * method should add <code>dockable</code> at a place were it can 
     * be removed without destroying the original layout.
     * @param dockable a new element which should become child of
     * {@link #getStation() this station}
     */
    public void dropAside( Dockable dockable );
    
    /**
     * Sets the element which should be maximized. The element
     * is always a child of {@link #getStation() the station}.
     * @param dockable the element to maximize or <code>null</code> if
     * no element should be maximized
     */
    public void setMaximizedDockable( Dockable dockable );
    
    /**
     * Gets the element which is currently maximized.
     * @return the currently maximized element or <code>null</code> if 
     * no element is maximized
     */
    public Dockable getMaximizedDockable();
}
