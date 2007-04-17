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

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.dock.title.DockTitle;

/**
 * Carries information where to put a {@link Dockable} onto a {@link SplitDockStation}.
 * The information is collected by the station itself.
 * @author Benjamin Sigg
 */
public class PutInfo{
    /**
     * Information where the mouse is.
     * @author Benjamin Sigg
     */
    public static enum Put{
        /** the mouse is on top */
        TOP, 
        /** the mouse is on the left */
        LEFT,
        /** the mouse is on the right */
        RIGHT,
        /** the mouse is at the bottom */
        BOTTOM,
        /** the mouse is in the center of an area */
        CENTER,
        /** the mouse is over a {@link DockTitle} */
        TITLE
    }; 
    
    /** The node which is the anchor for {@link #put} */
    private SplitNode node;
    /** The location of the mouse in respect to {@link #node} */
    private Put put;
    /** <code>true</code> if some lines should be painted onto the station */
    private boolean draw;
    /** The {@link Dockable} which will be dropped */
    private Dockable dockable;
    /** The location of the divider if the {@link #dockable} is put aside the {@link #node} */
    private double divider;
    /** The old size of {@link #dockable} */
    private int oldSize;
    
    /**
     * Creates a new PutInfo.
     * @param node the node to which <code>put</code> belongs
     * @param put where to put the {@link Dockable} in respect to <code>node</code>
     */
    public PutInfo( SplitNode node, Put put ){
        this.node = node;
        this.put = put;
    }
    
    /**
     * Sets the {@link Dockable} which will be added to the station.
     * @param dockable the Dockable or <code>null</code>
     */
    public void setDockable( Dockable dockable ) {
        this.dockable = dockable;
    }
    
    /**
     * Gets the Dockable which will be added to the station.
     * @return the Dockable or <code>null</code>
     */
    public Dockable getDockable() {
        return dockable;
    }
    
    /**
     * Sets the node which might become neighbor or parent of the new child.
     * @param node the node or <code>null</code>
     */
    public void setNode( SplitNode node ) {
        this.node = node;
    }
    
    /**
     * Gets the future neighbor or parent.
     * @return the node or <code>null</code>
     */
    public SplitNode getNode() {
        return node;
    }
    
    /**
     * Tells where the mouse is, in respect to {@link #getNode() node}.
     * @param put the location of the mouse or <code>null</code>
     */
    public void setPut( Put put ) {
        this.put = put;
    }
    
    /**
     * Gets the location of the mouse.
     * @return the location or <code>null</code>
     */
    public Put getPut() {
        return put;
    }
    
    /**
     * Sets the preferred location that a divider should have if the {@link #getDockable() dockable}
     * will have a neighbor.
     * @param divider the location of the divider
     */
    public void setDivider( double divider ) {
        this.divider = divider;
    }
   
    /**
     * Gets the preferred location of the divider.
     * @return the location
     */
    public double getDivider() {
        return divider;
    }
    
    /**
     * Sets whether the station should paint some lines to indicate where the 
     * {@link #getDockable() dockable} will be added, or not.
     * @param draw <code>true</code> if the station should paint something
     */
    public void setDraw( boolean draw ) {
        this.draw = draw;
    }
    
    /**
     * Tells whether the station should paint some lines.
     * @return <code>true</code> if the station should paint
     * @see #setDraw(boolean)
     */
    public boolean isDraw() {
        return draw;
    }
    
    /**
     * Sets the size that the {@link #getDockable() dockable} had before
     * it was dragged around.
     * @param oldSize the size (width or height, the interpretation 
     * depends on the value of {@link #getPut() put})
     */
    public void setOldSize( int oldSize ) {
        this.oldSize = oldSize;
    }
    
    /**
     * Gets the old size of the {@link #getDockable() dockable}.
     * @return the size
     * @see #setOldSize(int)
     */
    public int getOldSize() {
        return oldSize;
    }
}