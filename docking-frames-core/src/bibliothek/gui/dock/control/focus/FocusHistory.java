/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.control.focus;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * This interface tells the order in which the {@link Dockable}s got the focus.
 * @author Benjamin Sigg
 */
public interface FocusHistory {
	/**
	 * Gets an array containing all known {@link Dockable}s which did have the focus. The first element in the array
	 * is the did not have the focus for the longest time, while the last element in the array is the element that
	 * may have the focus right now.
	 * @return a copy of the history
	 */
	public Dockable[] getHistory();
	
	/**
	 * Searches the one {@link Dockable} that had the focus lately, and that is a child of one
	 * of <code>parent</code>.
	 * @param parents the stations whose child is searched
	 * @return a {@link Dockable} that had the focus and which is a child of one of the parents, or
	 * <code>null</code> if no such {@link Dockable} can be found
	 */
	public Dockable getNewestOn( DockStation... parents );
}
