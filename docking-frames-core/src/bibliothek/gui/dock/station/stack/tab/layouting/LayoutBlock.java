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
package bibliothek.gui.dock.station.stack.tab.layouting;

import bibliothek.gui.dock.station.stack.tab.AbstractTabLayoutManager;
import bibliothek.gui.dock.station.stack.tab.TabPane;

/**
 * Represents one or many components that have some unknown layout and are used
 * by an {@link AbstractTabLayoutManager} to layout the contents of a {@link TabPane}.
 * @author Benjamin Sigg
 */
public interface LayoutBlock {
	/**
	 * Creates a map of all sizes for which this block knows how to make an optimal
	 * layout. If for example this block consists of 12 buttons, then different
	 * sizes might lead to a row of 12 blocks, to 2 rows of 6 blocks, to
	 * 3 rows of 4 blocks, etc...  
	 * @return the map of sizes, may be empty or <code>null</code>
	 */
	public Size[] getSizes();
	
	/**
	 * Sets the layout of this block, the exact meaning of <code>size</code>
	 * depends on this block. In general keys returned by the last call of
	 * {@link #getSizes()} must be accepted, for any  other key the behavior 
	 * is unspecified.
	 * @param size the new layout
	 * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
	 * or cannot be read
	 */
	public void setLayout( Size size );
	
	/**
	 * Sets the boundaries of this block. The component(s) represented by
	 * this block must somehow fit into the rectangle.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width in pixel
	 * @param height the height in pixel
	 */
	public void setBounds( int x, int y, int width, int height );
	
	/**
	 * Tells this {@link LayoutBlock} at which side of the tabs it is shown.
	 * @param placement the placement, not <code>null</code>
	 * @throws IllegalArgumentException if <code>placement</code> is <code>null</code>
	 */
	public void setOrientation( TabPlacement placement );
}
