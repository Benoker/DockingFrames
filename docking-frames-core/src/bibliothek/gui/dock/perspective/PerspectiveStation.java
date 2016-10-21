/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.perspective;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.support.PlaceholderMap;

/**
 * An abstract representation of a {@link DockStation} in a {@link Perspective}.
 * @author Benjamin Sigg
 */
public interface PerspectiveStation extends PerspectiveElement{
	/**
	 * Gets the number of children this station has.
	 * @return the number of children
	 */
	public int getDockableCount();
	
	/**
	 * Gets the index'th child of this station.
	 * @param index the index of a child
	 * @return the child, not <code>null</code>
	 */
	public PerspectiveDockable getDockable( int index );
	
    /**
     * Gets precise information about the location of a child of this station.
     * @param child a child of this station, this child's location is asked
     * @param target an optional hint telling for which dockable the location information
     * will be used, can be <code>null</code>. This hint can be used to find a placeholder
     * that should be part of the result.
     * @return the location
     */
    public DockableProperty getDockableProperty( PerspectiveDockable child, PerspectiveDockable target );
	
	/**
	 * Converts the contents of this station into a map of placeholders.
	 * @return a map of placeholders describing the contents of this station
	 */
	public PlaceholderMap getPlaceholders();
	
	/**
	 * Informs this station of the content that it should show
	 * @param placeholders a map that was earlier created by {@link #getPlaceholders()}
	 */
	public void setPlaceholders( PlaceholderMap placeholders );
	
	/**
	 * Removes a child of this station, can leave behind a placeholder.
	 * @param dockable the element to remove
	 * @return <code>true</code> if <code>dockable</code> was removed, <code>false</code> otherwise
	 */
	public boolean remove( PerspectiveDockable dockable );
	
	/**
	 * Replaces <code>oldDockable</code> by <code>newDockable</code>. This method should behave the same way
	 * is if <code>oldDockable</code> was removed and <code>newDockable</code> added at the same location.
	 * @param oldDockable some child of this station
	 * @param newDockable the replacement for <code>oldDockable</code>
	 * @throws IllegalArgumentException if <code>oldDockable</code> is not a child of this station or if
	 * <code>newDockable</code> cannot be added as child to this station
	 */
	public void replace( PerspectiveDockable oldDockable, PerspectiveDockable newDockable );
}
