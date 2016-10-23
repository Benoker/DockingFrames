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
import bibliothek.gui.dock.station.toolbar.layout.DockablePlaceholderToolbarGrid;
import bibliothek.util.FrameworkOnly;

/**
 * The {@link ToolbarColumnModel} provides a clearly defined way to access and monitor the columns of a 
 * {@link ToolbarGroupDockStation}. The model acts as facade for the real data structures inside
 * {@link ToolbarGroupDockStation}, which are usually not accessible.<br>
 * The model does not offer any information that could not be retrieved through the methods of {@link DockablePlaceholderToolbarGrid},
 * but it offers an API to register observers and be notified about changes within the columns.<br>
 * Clients should not implement this interface.
 * @author Benjamin Sigg
 * @param <D> the dockable class itself
 * @param <P> the wrapper used to describe a {@link Dockable}
 */
@FrameworkOnly
public interface ToolbarColumnModel<D,P> {
	/**
	 * Gets the total number of columns that are currently available.
	 * @return the total number of columns
	 */
	public int getColumnCount();
	
	/**
	 * Gets the <code>index</code>'th column of this model.
	 * @param index the index of the column
	 * @return the column, not <code>null</code>
	 * @throws IllegalArgumentException if <code>index</code> is not within the boundaries
	 */
	public ToolbarColumn<D,P> getColumn( int index );
	
	/**
	 * Searches the column which contains <code>dockable</code>.
	 * @param dockable the item to search
	 * @return the column containing <code>dockable</code> or <code>null</code> if not found
	 */
	public ToolbarColumn<D,P> getColumn( D dockable );
	
	/**
	 * Adds the observer <code>listener</code> to this model.
	 * @param listener the new observer, not <code>null</code>
	 */
	public void addListener( ToolbarColumnModelListener<D,P> listener );
	
	/**
	 * Removes the observer <code>listener</code> from this model.
	 * @param listener the observer to remove
	 */
	public void removeListener( ToolbarColumnModelListener<D,P> listener );
}
