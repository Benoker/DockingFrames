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
package bibliothek.gui.dock.facile.station.split;

/**
 * Represents the mismatch in size that some node has.
 * @author Benjamin Sigg
 */
public class ResizeRequest{
    /** the mismatch in width */
    private double deltaWidth;
    /** the mismatch in height */
    private double deltaHeight;
    /** how much of the mismatch should be spend by the parent of the element which requested this, -1 if this should be ignored */
    private int fractionWidth;
    /** how much of the mismatch should be spend by the parent of the element which requested this, -1 if this should be ignored */
    private int fractionHeight;
    
    /**
     * Creates a new size request with fraction-width/height set to 1.
     * @param deltaWidth the amount of space that is missing in width
     * @param deltaHeight the amount of space that is missing in height
     */
    public ResizeRequest( double deltaWidth, double deltaHeight ){
        this( deltaWidth, deltaHeight, 1, 1 );
    }
    
    /**
     * Creates a new size request.
     * @param deltaWidth the amount of space that is missing in width
     * @param deltaHeight the amount of space that is missing in height
     * @param fractionWidth the fraction of space the parent of the asking node
     * should provide. If this value is set to x, then the parent would grant 1/x
     * of the request. -1 indicates that the width should be ignored.
     * @param fractionHeight the fraction of space the parent of the asking node
     * should provide. If this value is set to x, then the parent would grant 1/x
     * of the request. -1 indicates that the height should be ignored.
     */
    public ResizeRequest( double deltaWidth, double deltaHeight, int fractionWidth, int fractionHeight ){
        this.deltaWidth = deltaWidth;
        this.deltaHeight = deltaHeight;
        this.fractionWidth = fractionWidth;
        this.fractionHeight = fractionHeight;
    }
    
    /**
     * Gets the mismatch in width this request represents.
     * @return the mismatch
     */
    public double getDeltaWidth() {
        return deltaWidth;
    }
    
    /**
     * Gets the mismatch in height this request represents.
     * @return the mismatch
     */
    public double getDeltaHeight() {
        return deltaHeight;
    }
    
    /**
     * Gets the fraction of the mismatch the parent of the element that issued
     * this request should provide. A value of -1 indicates that this request
     * does not care about the width.
     * @return the fraction
     */
    public int getFractionWidth() {
        return fractionWidth;
    }
    
    /**
     * Gets the fraction of the mismatch the parent of the element that issued
     * this request should provide. A value of -1 indicates that this request
     * does not care about the height.
     * @return the fraction
     */
    public int getFractionHeight() {
        return fractionHeight;
    }
}