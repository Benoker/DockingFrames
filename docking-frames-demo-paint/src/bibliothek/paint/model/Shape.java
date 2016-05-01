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
package bibliothek.paint.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 * A shape represents some drawable figure.
 * @author Benjamin Sigg
 *
 */
public abstract class Shape {
    /** the color used to paint this shape */
    private Color color = Color.BLACK;
    
    /** the first location of this shape */
    private Point pointA;
    
    /** the second location of this shape */
    private Point pointB;
    
    /**
     * Gets the color which is used to paint this shape
     * @return the color
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Sets the color which is used to paint this shape
     * @param color the color
     */
    public void setColor( Color color ) {
        this.color = color;
    }
    
    /**
     * Gets the first point of the boundaries of this shape.
     * @return one edge of the boundaries
     */
    public Point getPointA() {
        return pointA;
    }
    
    /**
     * Sets the first point of the boundaries of this shape.
     * @param pointA
     */
    public void setPointA( Point pointA ) {
        this.pointA = pointA;
    }
    
    /**
     * Gets the second point of the boundaries of this shape.
     * @return one edge of the boundaries
     */
    public Point getPointB() {
        return pointB;
    }
    
    /**
     * Sets the second point of boundaries of this shape.
     * @param pointB the second point
     */
    public void setPointB( Point pointB ) {
        this.pointB = pointB;
    }
    
    /**
     * Paints this shape
     * @param g graphics context
     * @param stretch a value with which each coordinate has to be multiplied
     */
    public abstract void paint( Graphics g, double stretch );
    
    /**
     * Multiplies <code>coordinate</code> with <code>stretch</code> and
     * returns a rounded value.
     * @param coordinate some coordinate
     * @param stretch a factor <code>coordinate</code> is to be multiplied with.
     * @return the new coordinate
     */
    protected int stretch( int coordinate, double stretch ){
    	return (int)( coordinate * stretch );
    }
}
