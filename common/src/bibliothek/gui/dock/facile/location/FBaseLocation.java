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
package bibliothek.gui.dock.facile.location;

import bibliothek.gui.dock.facile.FContentArea;
import bibliothek.gui.dock.facile.FControl;
import bibliothek.gui.dock.facile.FLocation;
import bibliothek.gui.dock.facile.intern.FDockable.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;

/**
 * A location based on a {@link FContentArea}.
 * @author Benjamin Sigg
 */
public class FBaseLocation extends FRootLocation{
	/** the contentarea to which this location relates, can be <code>null</code> */
	private FContentArea content;
	
	public FBaseLocation( FContentArea center ){
		this.content = center;
	}
	
	public FBaseLocation(){
		// do nothing
	}
	
	/**
	 * Gets the contentarea to which this location relates.
	 * @return the center or <code>null</code> if the default center is meant.
	 */
	public FContentArea getContentArea(){
		return content;
	}
	
	/**
	 * Creates a location describing a normalized element at a given location.
	 * Note that the normalized area is seen as a rectangle of size 1/1.
	 * @param x the x-coordinate, a value between 0 and 1
	 * @param y the y-coordinate, a value between 0 and 1
	 * @param width the width, <code>x + width</code> should be less or equal to 1
	 * @param height the height, <code>y + height</code> should be less or equal to 1
	 * @return the new location
	 */
	public FCenterRectangleLocation normalRectangle( double x, double y, double width, double height ){
		return new FCenterRectangleLocation( this, x, y, width, height );
	}
	
	/**
	 * Creates a location describing a normalized element at the north of 
	 * the normalized-area.
	 * @param size the relative size of the element, a value between 0 (no space)
	 * and 1 (all space).
	 * @return the new location
	 */
	public FCenterTreeLocationRoot normalNorth( double size ){
		return new FCenterTreeLocationRoot( this, size, Side.NORTH );
	}	

	/**
	 * Creates a location describing a normalized element at the south of 
	 * the normalized-area.
	 * @param size the relative size of the element, a value between 0 (no space)
	 * and 1 (all space).
	 * @return the new location
	 */
	public FCenterTreeLocationRoot normalSouth( double size ){
		return new FCenterTreeLocationRoot( this, size, Side.SOUTH );
	}
	
	/**
	 * Creates a location describing a normalized element at the east of 
	 * the normalized-area.
	 * @param size the relative size of the element, a value between 0 (no space)
	 * and 1 (all space).
	 * @return the new location
	 */
	public FCenterTreeLocationRoot normalEast( double size ){
		return new FCenterTreeLocationRoot( this, size, Side.EAST );
	}

	/**
	 * Creates a location describing a normalized element at the west of 
	 * the normalized-area.
	 * @param size the relative size of the element, a value between 0 (no space)
	 * and 1 (all space).
	 * @return the new location
	 */
	public FCenterTreeLocationRoot normalWest( double size ){
		return new FCenterTreeLocationRoot( this, size, Side.WEST );
	}
	
	/**
	 * Creates a location describing a minimized element at the top.
	 * @return the new location
	 */
	public FMinimizedLocation minimalNorth(){
		return minimalNorth( Integer.MAX_VALUE );
	}
	
	/**
	 * Creates a location describing a minimized element at the top.
	 * @param index the location in the list of minimized elements
	 * @return the new location
	 */
	public FMinimizedLocation minimalNorth( int index ){
		return new FMinimizedLocation( this, Side.NORTH, index );
	}

	/**
	 * Creates a location describing a minimized element at the bottom.
	 * @return the new location
	 */
	public FMinimizedLocation minimalSouth(){
		return minimalSouth( Integer.MAX_VALUE );
	}
	
	/**
	 * Creates a location describing a minimized element at the bottom.
	 * @param index the location in the list of minimized elements
	 * @return the new location
	 */
	public FMinimizedLocation minimalSouth( int index ){
		return new FMinimizedLocation( this, Side.SOUTH, index );
	}
	
	/**
	 * Creates a location describing a minimized element at the right.
	 * @return the new location
	 */
	public FMinimizedLocation minimalEast(){
		return minimalEast( Integer.MAX_VALUE );
	}
	
	/**
	 * Creates a location describing a minimized element at the right.
	 * @param index the location in the list of minimized elements
	 * @return the new location
	 */
	public FMinimizedLocation minimalEast( int index ){
		return new FMinimizedLocation( this, Side.EAST, index );
	}
	
	/**
	 * Creates a location describing a minimized element at the left.
	 * @return the new location
	 */
	public FMinimizedLocation minimalWest(){
		return minimalWest( Integer.MAX_VALUE );
	}
	
	/**
	 * Creates a location describing a minimized element at the left.
	 * @param index the location in the list of minimized elements
	 * @return the new location
	 */
	public FMinimizedLocation minimalWest( int index ){
		return new FMinimizedLocation( this, Side.WEST, index );
	}
	
	@Override
	public DockableProperty findProperty( DockableProperty successor ){
		return null;
	}
	
	@Override
	public ExtendedMode findMode(){
		return null;
	}
	
	@Override
	public String findRootNormal() {
	    FContentArea center = getContentArea();
        if( center == null )
            return FContentArea.getCenterIdentifier( FControl.CONTENT_AREA_STATIONS_ID );
        else
            return center.getCenterIdentifier();
	}
	
	@Override
	public String findRoot(){
		return null;
	}
	
	@Override
	public FLocation aside() {
	    return this;
	}
	
	@Override
	public String toString() {
	    if( content == null )
	        return "[base]";
	    
	    return "[base " + content.getUniqueId() + "]";
	}
}
