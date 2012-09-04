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
package bibliothek.gui.dock.extension.css;

import bibliothek.gui.dock.extension.css.path.CssPathListener;

/**
 * Represents the path of a {@link CssItem}. {@link CssPath} are mutable and should
 * be monitored with a {@link CssPathListener}. Each {@link CssPath} is only a list
 * of {@link CssNode}. Changes in the {@link CssNode}s should be forwarded to the
 * {@link CssPathListener}s.<br>
 * In general paths have to be optimized for use with attached {@link CssPathListener}s.
 * @author Benjamin Sigg
 */
public interface CssPath {
	/**
	 * Gets the number of {@link CssNode}s of this path.
	 * @return the number of nodes, should be at least 1, but a path without
	 * nodes is considered valid as well
	 */
	public int getSize();
	
	/**
	 * Gets the <code>index</code>'th node of this path.
	 * @param index the index of the node
 	 * @return the node
	 */
	public CssNode getNode( int index );
	
	/**
	 * Adds the observer <code>listener</code> to this path.
	 * @param listener the new observer, not <code>null</code>
	 */
	public void addPathListener( CssPathListener listener );
	
	/**
	 * Removes the observer <code>listener</code> from this path.
	 * @param listener the listener to remove
	 */
	public void removePathListener( CssPathListener listener );
}
