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

/**
 * Some side of a rectangle.
 * @author Benjamin Sigg
 */
public enum Side{
	/** top side, horizontal */
	TOP,
	/** bottom side, horizontal */
	BOTTOM,
	/** to the left, vertical */
	LEFT, 
	/** to the right, vertical */
	RIGHT;
	
	/**
	 * Tells whether this side is a horizontal side.
	 * @return <code>true</code> if {@link #TOP} or {@link #BOTTOM}
	 */
	public boolean isHorizontal(){
		return this == TOP || this == BOTTOM;
	}
	
	/**
	 * Tells whether this side is a vertical side.
	 * @return <code>true</code> if {@link #LEFT} or {@link #RIGHT}
	 */
	public boolean isVertical(){
		return this == LEFT || this == RIGHT;
	}
}
