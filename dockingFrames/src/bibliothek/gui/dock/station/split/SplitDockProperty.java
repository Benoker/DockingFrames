/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.station.split;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.dock.AbstractDockableProperty;
import bibliothek.gui.dock.station.SplitDockStation;

/**
 * This property stores the location and the size of each child of a 
 * {@link SplitDockStation}. The property asumes that the station itself
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
    
    public String getFactoryID() {
        return SplitDockPropertyFactory.ID;
    }

    public void store( DataOutputStream out ) throws IOException {
        out.writeDouble( x );
        out.writeDouble( y );
        out.writeDouble( width );
        out.writeDouble( height );
    }

    public void load( DataInputStream in ) throws IOException {
        x = in.readDouble();
        y = in.readDouble();
        width = in.readDouble();
        height = in.readDouble();
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
}
