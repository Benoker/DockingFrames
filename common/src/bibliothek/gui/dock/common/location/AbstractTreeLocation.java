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
package bibliothek.gui.dock.common.location;

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.split.SplitDockPathProperty;

/**
 * A location that is child of the "normalized"-area, and describing a turn
 * in a path leading to the final element. A turn divides the basic rectangle
 * along a vertical or horizontal line, and continues the search in one of the
 * newly created, smaller rectangles.
 * @author Benjamin Sigg
 */
public abstract class AbstractTreeLocation extends AbstractStackholdingLocation{
	/** the relative size of this location, a value between 0 and 1 */
	private double size;
	/** in which rectangle the children of this location lie */
	private Side side;
	
	/**
	 * Creates a new location.
	 * @param size the size of this location, the parent-location is taken 
	 * and the area of the rectangle that location yields multiplied by
	 * <code>size</code> gives the area of the rectangle yield by this location.
	 * @param side given the rectangle yield by the parent-location, this 
	 * parameters tells which part the rectangle yield by this location occupies.
	 */
	public AbstractTreeLocation( double size, Side side ){
		if( size < 0 )
			throw new IllegalArgumentException( "Size must be at least 0" );
		if( size > 1 )
			throw new IllegalArgumentException( "Size must be no more than 1" );
		if( Double.isNaN( size ))
			throw new IllegalArgumentException( "Size must not be NaN" );
		
		if( side == null )
			throw new NullPointerException( "side must not be null" );
		
		this.size = size;
		this.side = side;
	}
	
	/**
	 * Given the rectangle yield by the parent-location, this 
	 * property tells which part the rectangle yield by this location occupies.
	 * @return the side
	 */
	public Side getSide(){
		return side;
	}
	
	/**
	 * Gets the size of this location. The parent-location is taken 
	 * and the area of the rectangle that location yields multiplied by
	 * <code>size</code> gives the area of the rectangle yield by this location.
	 * @return the relative size
	 */
	public double getSize(){
		return size;
	}
	
	/**
	 * Creates a new location which is based at the north side of this 
	 * location.
	 * @param size the relative size, a number between 0 and 1
	 * @return the new location
	 */
	public TreeLocationNode north( double size ){
		return new TreeLocationNode( this, size, Side.NORTH );
	}

	/**
	 * Creates a new location which is based at the south side of this 
	 * location.
	 * @param size the relative size, a number between 0 and 1
	 * @return the new location
	 */
	public TreeLocationNode south( double size ){
		return new TreeLocationNode( this, size, Side.SOUTH );
	}
	
	/**
	 * Creates a new location which is based at the east side of this 
	 * location.
	 * @param size the relative size, a number between 0 and 1
	 * @return the new location
	 */
	public TreeLocationNode east( double size ){
		return new TreeLocationNode( this, size, Side.EAST );
	}
	
	/**
	 * Creates a new location which is based at the west side of this 
	 * location.
	 * @param size the relative size, a number between 0 and 1
	 * @return the new location
	 */
	public TreeLocationNode west( double size ){
		return new TreeLocationNode( this, size, Side.WEST );
	}
	
	@Override
	public ExtendedMode findMode(){
		return ExtendedMode.NORMALIZED;
	}
	
	/**
	 * Asks the parent, or in the case of the root creates, the 
	 * {@link DockableProperty} describing the this location.
	 * @return the property
	 */
	protected abstract SplitDockPathProperty findParentProperty();
	
	@Override
	public SplitDockPathProperty findProperty( DockableProperty successor ){
		SplitDockPathProperty property = findParentProperty();
		property.setSuccessor( successor );
		
		switch( side ){
			case NORTH:
				property.add( SplitDockPathProperty.Location.TOP, size );
				break;
			case SOUTH:
				property.add( SplitDockPathProperty.Location.BOTTOM, size );
				break;
			case EAST:
				property.add( SplitDockPathProperty.Location.RIGHT, size );
				break;
			case WEST:
				property.add( SplitDockPathProperty.Location.LEFT, size );
				break;
		}
		
		return property;
	}
	
	@Override
	public CLocation aside() {
	    return stack( 1 );
	}
	
	@Override
	public String toString() {
	    return "[normal " + side + " " + size + "]";
	}
}
