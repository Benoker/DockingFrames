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
import bibliothek.gui.dock.station.split.SplitDockProperty;

/**
 * A location describing a normalized element by giving its relative position
 * and size (relative meaning that the element is positioned in a rectangle of
 * size 1/1).
 * @author Benjamin Sigg
 */
public class CRectangleLocation extends AbstractStackholdingLocation{
	/** the parenting split station */
	private CSplitLocation parent;
	
	/** the relative x-coordinate */
	private double x;
	/** the relative y-coordinate */
	private double y;
	/** the relative width */
	private double width;
	/** the relative height */
	private double height;
	
	/**
	 * Creates a new location.
	 * @param parent the parent that knows the id of the root station
	 * @param x the relative x-coordinate, a value between 0 and 1 is preferred
	 * @param y the relative y-coordinate, a value between 0 and 1 is preferred
	 * @param width the relative width, a value between 0 and 1 is preferred
	 * @param height the relative height, a value between 0 and 1 is preferred
	 */
	public CRectangleLocation( CSplitLocation parent, double x, double y, double width, double height ){
		if( parent == null )
			throw new NullPointerException( "parent is null" );
		
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Gets the relative x-coordinate
	 * @return a value between 0 and 1
	 */
	public double getX(){
		return x;
	}
	
	/**
	 * Gets the relative y-coordinate
	 * @return a value between 0 and 1
	 */
	public double getY(){
		return y;
	}
	
	/**
	 * Gets the relative width
	 * @return a value between 0 and 1
	 */
	public double getWidth(){
		return width;
	}
	
	/**
	 * Gest the relative height
	 * @return a value between 0 and 1
	 */
	public double getHeight(){
		return height;
	}
	
	@Override
	public String findRoot(){
		return parent.findRoot();
	}

	@Override
	public CLocation getParent(){
		return parent;
	}
	
	@Override
	public ExtendedMode findMode(){
		return ExtendedMode.NORMALIZED;
	}
	
	@Override
	public DockableProperty findProperty( DockableProperty successor ){
		SplitDockProperty split = new SplitDockProperty( x, y, width, height );
		split.setSuccessor( successor );
		if( parent != null ){
			return parent.findProperty( split );
		}
		return split;
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
	    return String.valueOf( parent ) + " [normal " + x + " " + y + " " + width + " " + height + "]";
	}
}
