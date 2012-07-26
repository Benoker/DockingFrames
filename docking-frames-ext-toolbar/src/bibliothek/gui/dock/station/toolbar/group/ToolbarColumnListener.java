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

/**
 * This observer can be added to a {@link ToolbarColumn} and receives events if the content
 * of the column changes.
 * @author Benjamin Sigg
 * @param <D> the the dockable class itself
 * @param <P> the wrapper class used to describe dockables
 */
public interface ToolbarColumnListener<D,P> {
	/**
	 * Called if an item was added to <code>column</code> at index <code>index</code>.
	 * @param column the source of the event
	 * @param item the item that was added
	 * @param dockable the dockable that was added
	 * @param index the index of the item that was added
	 */
	public void inserted( ToolbarColumn<D,P> column, P item, D dockable, int index );
	
	/**
	 * Called if an item was removed from <code>column</code>.
	 * @param column the source of the event
	 * @param item the item that was removed
	 * @param dockable the dockable that was removed
	 * @param index the index of the item that was removed
	 */
	public void removed( ToolbarColumn<D,P> column, P item, D dockable, int index );
}
