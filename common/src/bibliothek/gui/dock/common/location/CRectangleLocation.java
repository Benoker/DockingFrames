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
import bibliothek.gui.dock.common.intern.CDockable.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.split.SplitDockProperty;

/**
 * A location describing a normalized element by giving its relative position
 * and size (relative meaning that the element is positioned in a rectangle of
 * size 1/1).
 * @author Benjamin Sigg
 */
public class CRectangleLocation extends AbstractStackholdingLocation{
	/** the root element telling which area is the normalized-area */
	private CRootLocation root;
	
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
	 * @param root the root element telling which area is the normalized-area.
	 * @param x the relative x-coordinate, a value between 0 and 1 is preferred
	 * @param y the relative y-coordinate, a value between 0 and 1 is preferred
	 * @param width the relative width, a value between 0 and 1 is preferred
	 * @param height the relative height, a value between 0 and 1 is preferred
	 */
	public CRectangleLocation( CRootLocation root, double x, double y, double width, double height ){
		if( root == null )
			throw new NullPointerException( "base is null" );
		
		this.root = root;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public String findRoot(){
		return root.findRootNormal();
	}

	@Override
	public ExtendedMode findMode(){
		return ExtendedMode.NORMALIZED;
	}
	
	@Override
	public DockableProperty findProperty( DockableProperty successor ){
		SplitDockProperty split = new SplitDockProperty( x, y, width, height );
		split.setSuccessor( successor );
		return split;
	}

    @Override
    public CLocation aside() {
        return stack( 1 );
    }
    
	@Override
	public String toString() {
	    return "[normal " + x + " " + y + " " + width + " " + height + "]";
	}
}
