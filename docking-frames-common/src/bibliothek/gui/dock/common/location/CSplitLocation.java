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

import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.split.SplitDockProperty;

/**
 * This location is used to describe a {@link SplitDockStation}.
 * @author Benjamin Sigg
 */
public class CSplitLocation extends CLocation{
	/** the parent location of this location */
	private CLocation parent;
	
	/**
	 * Creates a new location
	 */
	public CSplitLocation(){
		// nothing
	}
	
	/**
	 * Creates a new location
	 * @param parent the parent location, can be <code>null</code>
	 */
	public CSplitLocation( CLocation parent ){
		this.parent = parent;
	}
	
	/**
	 * @deprecated see {@link CLocation#aside()} for an explanation.
	 */
	@Deprecated
    @Override
    public CLocation aside() {
        return this;
    }
    
    @Override
    public CLocation getParent(){
    	return parent;
    }

    /**
     * Creates a location which occupies a sub-rectangle of the {@link SplitDockStation}
     * which is described by this location. All arguments of this method must
     * be between 0 and 1, and <code>x+width</code> and <code>y+height</code> should
     * be smaller or equal to 1.
     * @param x the relative x coordinate
     * @param y the relative y coordinate
     * @param width the relative width
     * @param height the relative height
     * @return the location describing a rectangle
     */
    public CRectangleLocation rectangle( double x, double y, double width, double height ){
        return new CRectangleLocation( this, x, y, width, height );
    }

    /**
     * Creates a new location which represents a position in a stack
     * that covers the whole {@link SplitDockStation}.
     * @return the new location
     */
    public CStackLocation stack(){
        return rectangle( 0, 0, 1, 1 ).stack();
    }
    
    /**
     * Creates a new location which represents a position in a stack
     * that covers the whole {@link SplitDockStation}.
     * @param index the index within the stack
     * @return the new location
     */
    public CStackLocation stack( int index ){
        return rectangle( 0, 0, 1, 1 ).stack( index );
    }
    
    /**
     * Creates a location that describes a space in the upper part of
     * the {@link SplitDockStation}.
     * @param size the size of the space, between 0 and 1
     * @return the new location
     */
    public TreeLocationRoot north( double size ){
    	return north( size, -1 );
    }
    
    /**
     * Creates a location that describes a space in the upper part of
     * the {@link SplitDockStation}.
     * @param size the size of the space, between 0 and 1
     * @param nodeId the unique identifier of the new node, can be -1
     * @return the new location
     */
    public TreeLocationRoot north( double size, long nodeId ){ 
        return new TreeLocationRoot( this, size, Side.NORTH, nodeId );
    }
    
    /**
     * Creates a location that describes a space in the lower part of
     * the {@link SplitDockStation}.
     * @param size the size of the space, between 0 and 1
     * @return the new location
     */
    public TreeLocationRoot south( double size ){
    	return south( size, -1 );
    }
    
    /**
     * Creates a location that describes a space in the lower part of
     * the {@link SplitDockStation}.
     * @param size the size of the space, between 0 and 1
     * @param nodeId the unique identifier of the new node, can be -1
     * @return the new location
     */
    public TreeLocationRoot south( double size, long nodeId ){
        return new TreeLocationRoot( this, size, Side.SOUTH, nodeId );
    }
    
    /**
     * Creates a location that describes a space in the right part of
     * the {@link SplitDockStation}.
     * @param size the size of the space, between 0 and 1
     * @return the new location
     */
    public TreeLocationRoot east( double size ){ 
    	return east( size, -1 );
    }
    
    /**
     * Creates a location that describes a space in the right part of
     * the {@link SplitDockStation}.
     * @param size the size of the space, between 0 and 1
     * @param nodeId the unique identifier of the new node, can be -1
     * @return the new location
     */
    public TreeLocationRoot east( double size, long nodeId ){ 
        return new TreeLocationRoot( this, size, Side.EAST, nodeId );
    }
    
    /**
     * Creates a location that describes a space in the left part of
     * the {@link SplitDockStation}.
     * @param size the size of the space, between 0 and 1
     * @return the new location
     */
    public TreeLocationRoot west( double size ){
    	return west( size, -1 );
    }
    
    /**
     * Creates a location that describes a space in the left part of
     * the {@link SplitDockStation}.
     * @param size the size of the space, between 0 and 1
     * @param nodeId the unique identifier of the new node, can be -1
     * @return the new location
     */
    public TreeLocationRoot west( double size, long nodeId ){ 
        return new TreeLocationRoot( this, size, Side.WEST, nodeId );
    }
    
    @Override
    public ExtendedMode findMode() {
    	if( parent != null ){
    		return parent.findMode();
    	}
        return ExtendedMode.NORMALIZED;
    }

    @Override
    public String findRoot(){
    	if( parent != null ){
    		return parent.findRoot();
    	}
    	return null;
    }
    
    @Override
    public DockableProperty findProperty( DockableProperty successor ) {
    	if( successor == null ){
    		successor = new SplitDockProperty( 0, 0, 1, 1 );
    	}
    	
        if( parent != null ){
        	return parent.findProperty( successor );
        }
        return successor;
    }
    
    @Override
    public String toString(){
    	if( parent == null ){
    		return "[split]";
    	}
    	else{
    		return parent.toString() + " [split]";
    	}
    }
}
