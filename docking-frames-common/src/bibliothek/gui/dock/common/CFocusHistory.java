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
package bibliothek.gui.dock.common;

import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.util.Filter;

/**
 * Describes which {@link CDockable} did have the focus in which order.
 * @author Benjamin Sigg
 */
public interface CFocusHistory {
	/**
	 * Gets the entire history of focused {@link CDockable}s, the most recent focused dockable
	 * is at the beginning of the array.
	 * @return the history starting with the most recently focused {@link CDockable}
	 */
	public CDockable[] getHistory();
	
	/**
	 * Gets the first {@link CDockable} matching <code>filter</code>. This method first searches
	 * through {@link #getHistory() the history}, and afterwards visits all {@link CDockable}s that 
	 * were not in the history.
	 * @param filter the filter applied to all {@link CDockable}s
	 * @return the first dockable matching <code>filter</code>
	 */
	public CDockable getFirst( Filter<CDockable> filter );
}
