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
    
    public Point getPointA() {
        return pointA;
    }
    
    public void setPointA( Point pointA ) {
        this.pointA = pointA;
    }
    
    public Point getPointB() {
        return pointB;
    }
    
    public void setPointB( Point pointB ) {
        this.pointB = pointB;
    }
    
    /**
     * Paints this shape
     * @param g graphics context
     */
    public abstract void paint( Graphics g );
}
