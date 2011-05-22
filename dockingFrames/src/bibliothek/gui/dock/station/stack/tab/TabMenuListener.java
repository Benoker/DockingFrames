/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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

/**
 * This listener can be added to a {@link TabMenu} and keeps track of the number of
 * children the menu has.
 * @author Benjamin Sigg
 */
public interface TabMenuListener {
	/**
	 * Called after some children have been added to <code>source</code>.
	 * @param source the source of the event
	 * @param offset the index of the first new child
	 * @param length the number of children that were added, at least 1
	 */
	public void dockablesAdded( TabMenu source, int offset, int length );
	
	/**
	 * Called after some children have been removed from <code>source</code>.
	 * @param source the source of the event
	 * @param offset the index of the first removed child
	 * @param length the number of children that were removed, at least 1
	 */
	public void dockablesRemoved( TabMenu source, int offset, int length );
}
