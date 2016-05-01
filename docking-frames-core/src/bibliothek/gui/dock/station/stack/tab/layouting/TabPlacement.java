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

import bibliothek.gui.dock.station.stack.tab.TabPane;
import bibliothek.gui.dock.title.DockTitle.Orientation;

/**
 * Tells at which side tabs are placed on a {@link TabPane} (or a similar ui element).
 * @author Benjamin Sigg
 */
public enum TabPlacement{
	/** top side, horizontal */
	TOP_OF_DOCKABLE,
	/** bottom side, horizontal */
	BOTTOM_OF_DOCKABLE,
	/** to the left, vertical */
	LEFT_OF_DOCKABLE, 
	/** to the right, vertical */
	RIGHT_OF_DOCKABLE;
	
	/**
	 * Tells whether this side is a horizontal side.
	 * @return <code>true</code> if {@link #TOP_OF_DOCKABLE} or {@link #BOTTOM_OF_DOCKABLE}
	 */
	public boolean isHorizontal(){
		return this == TOP_OF_DOCKABLE || this == BOTTOM_OF_DOCKABLE;
	}
	
	/**
	 * Tells whether this side is a vertical side.
	 * @return <code>true</code> if {@link #LEFT_OF_DOCKABLE} or {@link #RIGHT_OF_DOCKABLE}
	 */
	public boolean isVertical(){
		return this == LEFT_OF_DOCKABLE || this == RIGHT_OF_DOCKABLE;
	}
	
	/**
	 * Transforms <code>this</code> into an {@link Orientation}.
	 * @return transformed <code>this</code>
	 */
	public Orientation toOrientation(){
		switch( this ){
			case TOP_OF_DOCKABLE: return Orientation.NORTH_SIDED;
			case BOTTOM_OF_DOCKABLE: return Orientation.SOUTH_SIDED;
			case LEFT_OF_DOCKABLE: return Orientation.WEST_SIDED;
			case RIGHT_OF_DOCKABLE: return Orientation.EAST_SIDED;
			default: throw new IllegalStateException( "unknown tab placement: " + this );
		}
	}
}
