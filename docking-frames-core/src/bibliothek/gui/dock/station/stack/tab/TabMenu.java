/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.station.stack.tab;

import bibliothek.gui.Dockable;

/**
 * A {@link TabMenu} is a list of one or several {@link Dockable}s, the user
 * can open the menu and select one of them.
 * @author Benjamin Sigg
 */
public interface TabMenu extends TabPaneComponent{
	/**
	 * Gets all the {@link Dockable}s that are shown in this menu.
	 * @return the list of elements in this menu
	 */
	public Dockable[] getDockables();
	
	/**
	 * Gets the index'th {@link Dockable} of this menu.
	 * @param index the index of an item
	 * @return the item
	 */
	public Dockable getDockable( int index );
	
	/**
	 * Gets the number of {@link Dockable}s shown on this menu.
	 * @return the number of items
	 */
	public int getDockableCount();
	
	/**
	 * Adds a new listener to this menu.
	 * @param listener the new listener, not <code>null</code>
	 */
	public void addTabMenuListener( TabMenuListener listener );
	
	/**
	 * Removes <code>listener</code> from this menu.
	 * @param listener the listener to remove
	 */
	public void removeTabMenuListener( TabMenuListener listener );
}
