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

import java.awt.Dimension;
import java.awt.Rectangle;

import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;

/**
 * The default {@link AxisConversion} assumes:
 * <ul>
 *  <li>the model is a line at the top of some rectangle, the point 0/0 is the top left point of that rectangle</li>
 *  <li>the view is a line at one side of some rectangle</li>
 * </ul>
 * @author Benjamin Sigg
 */
public class DefaultAxisConversion implements AxisConversion{
	
	/** side of the view */
	private TabPlacement side;
	
	/** available space for the view */
	private Rectangle space;
	
	/**
	 * Creates a new axis converter
	 * @param space the space available for the view, not <code>null</code>
	 * @param side the side of the available <code>space</code> at which the 
	 * view hangs, not <code>null</code>.
	 */
	public DefaultAxisConversion( Rectangle space, TabPlacement side ){
		if( space == null )
			throw new IllegalArgumentException( "space must not be null" );
		
		if( side == null )
			throw new IllegalArgumentException( "side must not be null" );
		
		this.side = side;
		this.space = space;
	}

	public Dimension modelToView( Dimension size ){
		switch( side ){
			case TOP_OF_DOCKABLE:
			case BOTTOM_OF_DOCKABLE:
				return new Dimension( size );
			case LEFT_OF_DOCKABLE:
			case RIGHT_OF_DOCKABLE:
				return new Dimension( size.height, size.width );
			default:
				throw new IllegalStateException( "unknown side: " + side );
		}
	}

	public Rectangle modelToView( Rectangle bounds ){
		switch( side ){
			case TOP_OF_DOCKABLE:
				return new Rectangle( space.x+bounds.x, space.y+bounds.y, bounds.width, bounds.height );
			case BOTTOM_OF_DOCKABLE:
				return new Rectangle( space.x+bounds.x, space.y+space.height-bounds.height-bounds.y, bounds.width, bounds.height );
			case LEFT_OF_DOCKABLE:
				return new Rectangle( space.x+bounds.y, space.y+bounds.x, bounds.height, bounds.width );
			case RIGHT_OF_DOCKABLE:
				return new Rectangle( space.x + space.width - bounds.y - bounds.height, space.y + bounds.x, bounds.height, bounds.width );
			default:
				throw new IllegalStateException( "unknown side: " + side );	
		}
	}

	public Dimension viewToModel( Dimension size ){
		switch( side ){
			case TOP_OF_DOCKABLE:
			case BOTTOM_OF_DOCKABLE:
				return new Dimension( size );
			case LEFT_OF_DOCKABLE:
			case RIGHT_OF_DOCKABLE:
				return new Dimension( size.height, size.width );
			default:
				throw new IllegalStateException( "unknown side: " + side );
		}
	}

	public Rectangle viewToModel( Rectangle bounds ){
		switch( side ){
			case TOP_OF_DOCKABLE:
				return new Rectangle( bounds.x-space.x, bounds.y-space.y, bounds.width, bounds.height );
			case BOTTOM_OF_DOCKABLE:
				return new Rectangle( bounds.x-space.x, bounds.y+bounds.height-space.y-space.height, bounds.width, bounds.height );
			case LEFT_OF_DOCKABLE:
				return new Rectangle( space.x-bounds.y, bounds.x-space.y, bounds.height, bounds.width );
			case RIGHT_OF_DOCKABLE:
				return new Rectangle( space.y - bounds.y, space.x + space.width - bounds.width - bounds.x, bounds.height, bounds.width );
			default:
				throw new IllegalStateException( "unknown side: " + side );
		}
	}
}
