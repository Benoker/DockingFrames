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

package bibliothek.gui.dock.station.split;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.layout.AbstractDockableProperty;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * This property stores the location and the size of each child of a 
 * {@link SplitDockStation}. The property assumes that the station itself
 * has a size of 1/1, and the top left edge is 0/0.
 * @author Benjamin Sigg
 */
public class SplitDockProperty extends AbstractDockableProperty {
	/** A property covering the right quarter of the station */
    public static final SplitDockProperty EAST = new SplitDockProperty( 0.75, 0, 0.25, 1 );
    
    /** A property covering the left quarter of the station */
    public static final SplitDockProperty WEST = new SplitDockProperty( 0, 0, 0.25, 1 );
    
    /** A property covering the top quarter of the station */
    public static final SplitDockProperty NORTH = new SplitDockProperty( 0, 0, 1, 0.25 );
    
    /** A property covering the bottom quarter of the station */
    public static final SplitDockProperty SOUTH = new SplitDockProperty( 0, 0.75, 1, 0.25 );
    
    private double x, y, width, height;
    
    /**
     * Constructs a new property with size and location equal to 0/0
     */
    public SplitDockProperty(){
    	// do nothing
    }
    
    /**
     * Creates a new property with the given values. The coordinates and
     * the sizes should be in the range of 0 and 1, but the {@link SplitDockStation}
     * can handle values that are out of bounds.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param width the width of the child
     * @param height the height of the child
     */
    public SplitDockProperty( double x, double y, double width, double height ){
        setX( x );
        setY( y );
        setWidth( width );
        setHeight( height );
    }
    

    public DockableProperty copy() {
        SplitDockProperty copy = new SplitDockProperty( x, y, width, height );
        copy( copy );
        return copy;
    }
    
    public String getFactoryID() {
        return SplitDockPropertyFactory.ID;
    }

    public void store( DataOutputStream out ) throws IOException {
        Version.write( out, Version.VERSION_1_0_4 );
        out.writeDouble( x );
        out.writeDouble( y );
        out.writeDouble( width );
        out.writeDouble( height );
    }
    
    public void store( XElement element ) {
        element.addElement( "x" ).setDouble( x );
        element.addElement( "y" ).setDouble( y );
        element.addElement( "width" ).setDouble( width );
        element.addElement( "height" ).setDouble( height );
    }

    public void load( DataInputStream in ) throws IOException {
        Version version = Version.read( in );
        version.checkCurrent();
        x = in.readDouble();
        y = in.readDouble();
        width = in.readDouble();
        height = in.readDouble();
    }
    
    public void load( XElement element ) {
        x = element.getElement( "x" ).getDouble();
        y = element.getElement( "y" ).getDouble();
        width = element.getElement( "width" ).getDouble();
        height = element.getElement( "height" ).getDouble();
    }

    /**
     * Gets the height of the child.
     * @return the height
     * @see #setHeight(double)
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets the height of the child.
     * @param height the height, should be between 0 and 1
     */
    public void setHeight( double height ) {
        this.height = height;
    }

    /**
     * Gets the width of the child.
     * @return the width
     * @see #setWidth(double)
     */
    public double getWidth() {
        return width;
    }

    /**
     * Sets the width of the child.
     * @param width the width, should be between 0 and 1
     */
    public void setWidth( double width ) {
        this.width = width;
    }

    /**
     * Gets the x-coordinate of the child.
     * @return the x-coordinate
     * @see #setX(double)
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of the child.
     * @param x the coordinate, should be between 0 and 1
     */
    public void setX( double x ) {
        this.x = x;
    }

    /**
     * Gets the y-coordinate of the child.
     * @return the coordinate
     * @see #setY(double)
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of the child.
     * @param y the coordinate, should be between 0 and 1
     */
    public void setY( double y ) {
        this.y = y;
    }
    
    @Override
    public String toString() {
    	return getClass().getName() + "[x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
    }

	@Override
	public int hashCode(){
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits( height );
		result = prime * result + (int)(temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits( width );
		result = prime * result + (int)(temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits( x );
		result = prime * result + (int)(temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits( y );
		result = prime * result + (int)(temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals( Object obj ){
		if( this == obj )
			return true;
		if( !super.equals( obj ) )
			return false;
		if( !(obj instanceof SplitDockProperty) )
			return false;
		SplitDockProperty other = (SplitDockProperty)obj;
		if( Double.doubleToLongBits( height ) != Double
				.doubleToLongBits( other.height ) )
			return false;
		if( Double.doubleToLongBits( width ) != Double
				.doubleToLongBits( other.width ) )
			return false;
		if( Double.doubleToLongBits( x ) != Double.doubleToLongBits( other.x ) )
			return false;
		if( Double.doubleToLongBits( y ) != Double.doubleToLongBits( other.y ) )
			return false;
		return true;
	}
}
