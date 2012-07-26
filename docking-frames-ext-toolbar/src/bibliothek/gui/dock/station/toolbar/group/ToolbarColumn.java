/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.station.toolbar.group;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.util.FrameworkOnly;

/**
 * Represents one column of a {@link ToolbarGroupDockStation}. This interface
 * is not intended for subclassing.
 * @author Benjamin Sigg
 * @param <D> the dockable class itself
 * @param <P> the kind of object used to describe a {@link Dockable}
 */
@FrameworkOnly
public interface ToolbarColumn<D,P> {
	/**
	 * Tells how many {@link Dockable}s are shown in this column.
	 * @return the total number of {@link Dockable}s, at least <code>0</code>.
	 */
	public int getDockableCount();
	
	/**
	 * Gets the <code>index</code>'th {@link Dockable} of this column.
	 * @param index the index of the {@link Dockable}
	 * @return the element at <code>index</code>, never <code>null</code>
	 * @throws IllegalArgumentException if <code>index</code> is out of bounds
	 */
	public D getDockable( int index );
	
	/**
	 * Gets the index of <code>dockable</code>.
	 * @param dockable the item to search
	 * @return its index of -1 if the item was not fount
	 */
	public int indexOf( Dockable dockable );
	
	/**
	 * Gets a wrapper item that represents the {@link Dockable} at <code>index</code>.
	 * @param index the index of the item
	 * @return the element at <code>index</code>, never <code>null</code>
	 */
	public P getItem( int index );
	
	/**
	 * Gets the location of this column in its parent {@link ToolbarColumnModel}.
	 * @return the location of this column
	 */
	public int getColumnIndex();
	
	/**
	 * Adds the new observer <code>listener</code> to this column.
	 * @param listener the new observer
	 */
	public void addListener( ToolbarColumnListener<D,P> listener );
	
	/**
	 * Removes the observer <code>listener</code> from this column.
	 * @param listener the listener to remove
	 */
	public void removeListener( ToolbarColumnListener<D,P> listener );
}
