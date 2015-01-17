/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2015 Benjamin Sigg
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

package bibliothek.gui.dock.station;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.util.Path;

/**
 * Represents the placeholders of a {@link DockStation}. Placeholders are unique identifiers that can be attached
 * to certain locations on a station. During runtime placeholders may be replaced by real {@link Dockable}s.
 * @author Benjamin Sigg
 */
public interface PlaceholderMapping {
	/**
	 * Gets the {@link DockStation} which created this mapping in its {@link DockStation#getPlaceholderMapping()}.
	 * @return the creator and owner of this mapping
	 */
	public DockStation getStation();
	
    /**
     * First searches the location of <code>dockable</code>, then adds <code>placeholder</code> to that 
     * location. If another dockable is dropped on this station, and that item is associated with <code>placeholder</code>,
     * then it will be put at the same position as <code>dockable</code>.
     * This method will remove <code>placeholder</code> from any other position on the station.
     * @param dockable a child of this station, must not be <code>null</code>
     * @param placeholder the placeholder to add, must not be <code>null</code>
     * @throws IllegalArgumentException if <code>dockable</code> is not a child of this station, or if any argument is <code>null</code>
     */
    public void addPlaceholder( Dockable dockable, Path placeholder );
    
    /**
     * Tells whether the {@link #getStation() station} has any reference to <code>placeholder</code>.
     * @param placeholder the placeholder to search
     * @return <code>true</code> if <code>placeholder</code> was found
     */
    public boolean hasPlaceholder( Path placeholder );
    
    /**
     * Searches for the placeholder <code>placeholder</code> and gets the {@link Dockable} that is currently sitting
     * at the location described by <code>placeholder</code>. 
     * @param placeholder the placeholder to search
     * @return the dockable at <code>placeholder</code>, or <code>null</code> either because <code>placeholder</code> 
     * could not be found, or because <code>placeholder</code> describes a position that does currently not contain
     * a {@link Dockable}
     */
    public Dockable getDockableAt( Path placeholder );
    
    /**
     * Gets a {@link DockableProperty} that describes the location of <code>placeholder</code>. The result of this
     * method is undefined if <code>placeholder</code> is not found.
     * @param placeholder the placeholder whose location is searched
     * @return the location, may be <code>null</code> if <code>placeholder</code> is not found   
     */
    public DockableProperty getLocationAt( Path placeholder );
    
    /**
     * Removes all occurrences of <code>placeholder</code> from this station.
     * @param placeholder the placeholder to remove, must not be <code>null</code>
     */
    public void removePlaceholder( Path placeholder );
}
