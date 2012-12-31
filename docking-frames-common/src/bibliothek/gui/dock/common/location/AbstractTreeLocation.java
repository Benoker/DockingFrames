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
	/** the unique identifier of the node that is represented by this location */
	private long nodeId = -1;
	
	/**
	 * Creates a new location.
	 * @param size the size of this location, the parent-location is taken 
	 * and the area of the rectangle that location yields multiplied by
	 * <code>size</code> gives the area of the rectangle yield by this location.
	 * @param side given the rectangle yield by the parent-location, this 
	 * parameters tells which part the rectangle yield by this location occupies.
	 * @param nodeId a unique identifier for the node represented by this location, can be -1
	 */
	public AbstractTreeLocation( double size, Side side, long nodeId ){
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
		this.nodeId = nodeId;
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
	 * Gets the identifier of the node represented by this location.
	 * @return the identifier or -1 is not set
	 */
	public long getNodeId(){
		return nodeId;
	}
	
	/**
	 * Creates a new location which is based at the north side of this 
	 * location.
	 * @param size the relative size, a number between 0 and 1
	 * @return the new location
	 */
	public TreeLocationNode north( double size ){
		return north( size, -1 );
	}
	
	/**
	 * Creates a new location which is based at the north side of this 
	 * location.
	 * @param size the relative size, a number between 0 and 1
	 * @param nodeId the unique identifier of the new node, can be -1
	 * @return the new location
	 */
	public TreeLocationNode north( double size, long nodeId ){
		return new TreeLocationNode( this, size, Side.NORTH, nodeId );
	}

	/**
	 * Creates a new location which is based at the south side of this 
	 * location.
	 * @param size the relative size, a number between 0 and 1
	 * @return the new location
	 */
	public TreeLocationNode south( double size ){
		return south( size, -1 );
	}
	
	/**
	 * Creates a new location which is based at the south side of this 
	 * location.
	 * @param size the relative size, a number between 0 and 1
	 * @param nodeId the unique identifier of the new node, can be -1
	 * @return the new location
	 */
	public TreeLocationNode south( double size, long nodeId ){
		return new TreeLocationNode( this, size, Side.SOUTH, nodeId );
	}
	
	/**
	 * Creates a new location which is based at the east side of this 
	 * location.
	 * @param size the relative size, a number between 0 and 1
	 * @return the new location
	 */
	public TreeLocationNode east( double size ){
		return east( size, -1 );
	}
	
	/**
	 * Creates a new location which is based at the east side of this 
	 * location.
	 * @param size the relative size, a number between 0 and 1
	 * @param nodeId the unique identifier of the new node, can be -1
	 * @return the new location
	 */
	public TreeLocationNode east( double size, long nodeId ){
		return new TreeLocationNode( this, size, Side.EAST, nodeId );
	}
	
	/**
	 * Creates a new location which is based at the west side of this 
	 * location.
	 * @param size the relative size, a number between 0 and 1
	 * @return the new location
	 */
	public TreeLocationNode west( double size ){
		return west( size, -1 );
	}
	
	/**
	 * Creates a new location which is based at the west side of this 
	 * location.
	 * @param size the relative size, a number between 0 and 1
	 * @param nodeId the unique identifier of the new node, can be -1
	 * @return the new location
	 */
	public TreeLocationNode west( double size, long nodeId ){
		return new TreeLocationNode( this, size, Side.WEST, nodeId );
	}
	
	/**
	 * Creates a new leaf of this path.
	 * @param leafId the unique identifier of the leaf, can be -1
	 * @return the new leaf
	 */
	public TreeLocationLeaf leaf( long leafId ){
		return new TreeLocationLeaf( this, leafId );
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
				property.add( SplitDockPathProperty.Location.TOP, size, nodeId );
				break;
			case SOUTH:
				property.add( SplitDockPathProperty.Location.BOTTOM, size, nodeId );
				break;
			case EAST:
				property.add( SplitDockPathProperty.Location.RIGHT, size, nodeId );
				break;
			case WEST:
				property.add( SplitDockPathProperty.Location.LEFT, size, nodeId );
				break;
		}
		
		return property;
	}
	
	/**
	 * @deprecated see {@link CLocation#aside()} for an explanation.
	 */
	@Deprecated
	@Override
	public CLocation aside() {
	    return stack( 1 );
	}
	
	@Override
	public String toString() {
	    return "[normal " + side + " " + size + "]";
	}
}
