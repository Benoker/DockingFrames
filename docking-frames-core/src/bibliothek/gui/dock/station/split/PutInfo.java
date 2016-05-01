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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.title.DockTitle;

/**
 * Carries information where to put a {@link Dockable} onto a {@link SplitDockStation}.
 * @author Benjamin Sigg
 */
public class PutInfo {
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
    /** The {@link Dockable} which will be dropped */
    private Dockable dockable;
    /** The location of the divider if the {@link #dockable} is put aside the {@link #node} */
    private double divider;
    /** The old size of {@link #dockable} */
    private int oldSize;
    
    /** the leaf that was moved */
    private Leaf leaf;
    
    /** information needed for a {@link Combiner} */
    private CombinerSource combinerSource;
    
    /** information created by a {@link Combiner} */
    private CombinerTarget combinerTarget;
    
    /** whether the mouse position would result in {@link Put#CENTER} */
    private boolean combining;
    
    /**
     * Creates a new PutInfo.
     * @param node the node to which <code>put</code> belongs
     * @param put where to put the {@link Dockable} in respect to <code>node</code>
     * @param dockable the element that will be dropped
     * @param combining whether the mouse position alone would set <code>put</code> to {@link Put#CENTER} or {@link Put#TITLE}
     */
    public PutInfo( SplitNode node, Put put, Dockable dockable, boolean combining ){
        this.node = node;
        this.put = put;
        this.dockable = dockable;
        this.combining = combining;
    }
    
    /**
     * If this {@link PutInfo} describes the current layout, then nothing will happen if it is applied.
     * @return whether this info describes the current layout
     */
    public boolean willHaveNoEffect(){
    	if( node instanceof Leaf ){
    		SplitNode parent = node.getParent();
    		if( parent instanceof Node ){
    			if( ((Node)parent).getOrientation() == Orientation.HORIZONTAL ){
    				if( put == Put.TOP || put == Put.BOTTOM ){
    					parent = null;
    				}
    			}
    			else{
    				if( put == Put.LEFT || put == Put.RIGHT ){
    					parent = null;
    				}
    			}
    			if( parent != null ){
	    			SplitNode neighbor = null;
	    			if( put == Put.LEFT || put == Put.TOP ){
	    				neighbor = ((Node)parent).getLeft();
	    			}
	    			else if( put == Put.RIGHT || put == Put.BOTTOM ){
	    				neighbor = ((Node)parent).getRight();
	    			}
	    			
	    			
	    			if( neighbor instanceof Leaf && ((Leaf)neighbor).getDockable() == dockable ){
	    				return true;
	    			}
    			}
    		}
    	}
    	return false;
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
     * Tells whether the mouse position alone would result in {@link #getPut()} equaling
     * {@link Put#CENTER} or {@link Put#TITLE}.
     * @return whether the mouse is in the center position
     */
    public boolean isCombining(){
		return combining;
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
    
    /**
     * Sets the leaf which contains {@link #getDockable() dockable}
     * @param leaf the leaf
     */
    public void setLeaf( Leaf leaf ) {
        this.leaf = leaf;
    }

    /**
     * Gets the leaf which contains {@link #getDockable() dockable}
     * @return the leaf or <code>null</code>
     */
    public Leaf getLeaf() {
        return leaf;
    }
    
    /**
     * Sets combination information that can be used for {@link Combiner#combine(CombinerSource, CombinerTarget)}.
     * @param source information about the two {@link Dockable}s to merge
     * @param target information about how to merge the {@link Dockable}s
     */
    public void setCombination( CombinerSource source, CombinerTarget target ){
    	this.combinerSource = source;
    	this.combinerTarget = target;
    }
    
    /**
     * Gets information about the two {@link Dockable}s that are going to be merged.
     * @return the merge information, can be <code>null</code>
     */
    public CombinerSource getCombinerSource(){
		return combinerSource;
	}
    
    /**
     * Gets information about how to merge the two {@link Dockable}s.
     * @return the merge information, can be <code>null</code>
     */
    public CombinerTarget getCombinerTarget(){
		return combinerTarget;
	}
}