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

import bibliothek.gui.dock.SplitDockStation;

/**
 * Represents one node of the tree which represents the split-tree of
 * a {@link SplitDockStation}.
 * @author Benjamin Sigg
 */
public abstract class ResizeElement<T>{
    /** the last request that was issued */
    private ResizeRequest request;
    /** the parent of this node */
    private ResizeElement<T> parent;
    
    /** the origin of this element */
    private LockedResizeLayoutManager<T> layout;
    
    /**
     * Creates a new element.
     * @param parent the parent of <code>this</code>
     * @param layout the origin of this element
     */
    public ResizeElement( ResizeElement<T> parent, LockedResizeLayoutManager<T> layout ){
        this.parent = parent;
        this.layout = layout;
    }
    
    /**
     * Gets the parent of this node
     * @return the parent or <code>null</code> if this is a root
     */
    public ResizeElement<T> getParent() {
        return parent;
    }
    
    /**
     * Gets the layout that created this element.
     * @return the origin of this element
     */
    public LockedResizeLayoutManager<T> getLayout() {
        return layout;
    }
    
    /**
     * Creates the initial request of changed sizes. 
     * @return the initial request or <code>null</code>
     */
    protected abstract ResizeRequest createRequest();
    
    /**
     * Gets the initial request for the size change.
     * @return the initial request or <code>null</code>
     */
    public ResizeRequest getRequest(){
        return request;
    }
    
    /**
     * Gets the children of this element.
     * @return the children or <code>null</code>
     */
    protected abstract ResizeElement<T>[] getChildren();
    
    /**
     * Called before the bounds of a tree are updated, can be used
     * to store some properties that are later needed to create
     * the {@link ResizeRequest}.
     */
    public void prepareResize(){
        ResizeElement<T>[] children = getChildren();
        if( children != null ){
            for( ResizeElement<T> child : children ){
                child.prepareResize();
            }
        }
    }
    
    /**
     * Calls {@link #createRequest()} on <code>this</code> and recursively
     * on all children. Stores the result for later analysis.
     */
    public void prepareRequests(){
        ResizeElement<T>[] children = getChildren();
        if( children != null ){
            for( ResizeElement<T> child : children ){
                child.prepareRequests();
            }
        }
        request = createRequest();
    }
    
    /**
     * Checks whether this {@link ResizeElement} is valid. A valid {@link ResizeElement} has no children
     * that are <code>null</code>.
     * @return <code>true</code> if this element can actually be used
     */
    public boolean isValid(){
    	ResizeElement<T>[] children = getChildren();
        if( children != null ){
            for( ResizeElement<T> child : children ){
                if( child == null ){
                	return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Adapts the size of the children of this element given the size change
     * the parent could provide.
     * @param deltaWidth the change of this elements width
     * @param deltaHeight the change of this elements height
     */
    public abstract void adapt( double deltaWidth, double deltaHeight );
    
    /**
     * Gets the root of this tree.
     * @return the root
     */
    public ResizeRoot<T> getResizeRoot(){
        return parent.getResizeRoot();
    }
}