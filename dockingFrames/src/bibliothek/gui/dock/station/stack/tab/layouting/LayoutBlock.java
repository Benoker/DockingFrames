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

import java.awt.Dimension;

import bibliothek.gui.dock.station.stack.tab.AbstractTabLayoutManager;
import bibliothek.gui.dock.station.stack.tab.TabPane;

/**
 * Represents one or many components that have some unknown layout and are used
 * by an {@link AbstractTabLayoutManager} to layout the contents of a {@link TabPane}.
 * @author Benjamin Sigg
 */
public interface LayoutBlock {
	/**
	 * Gets the preferred size of this block.
	 * @return the preferred size, may be <code>null</code> if {@link #isVisible()}
	 * returns <code>false</code>
	 */
	public Dimension getPreferredSize();
	
	/**
	 * Gets the minimal size this block should have.
	 * @return the preferred size, may be <code>null</code> if {@link #isVisible()}
	 * returns <code>false</code>
	 */
	public Dimension getMinimumSize();
	
	/**
	 * Tells whether this block is actually used.
	 * @return <code>true</code> if the components behind this block are
	 * visible, <code>false</code> otherwise.
	 */
	public boolean isVisible();
	
	/**
	 * Sets the boundaries of this block. The component(s) represented by
	 * this block must somehow fit into the rectangle.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width in pixel
	 * @param height the height in pixel
	 */
	public void setBounds( int x, int y, int width, int height );
}
