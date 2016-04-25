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

import bibliothek.gui.dock.station.split.Leaf;

/**
 * Represents a {@link Leaf}.
 * @author Benjamin Sigg
 */
public class ResizeLeaf<T> extends ResizeElement<T>{
    /** the leaf that is represented by this ResizeLeaf */
    private Leaf leaf;
    /** temporary data created by the LayoutManager */
    private T temporary;
    
    /**
     * Creates a new leaf element.
     * @param layout the layout manager that uses this leafs
     * @param parent the parent of this node
     * @param leaf the leaf that is represented by this element
     */
    public ResizeLeaf( LockedResizeLayoutManager<T> layout, ResizeElement<T> parent, Leaf leaf ){
        super( parent, layout );
        this.leaf = leaf;
    }
    
    /**
     * Gets the leaf which is represented by this leaf-element.
     * @return the origin
     */
    public Leaf getLeaf() {
        return leaf;
    }
    
    @Override
    protected ResizeRequest createRequest() {
        return getLayout().getRequest( temporary, leaf );
    }
    
    @Override
    public void prepareResize() {
        temporary = getLayout().prepareResize( leaf );
        super.prepareResize();
    }
    
    @Override
    protected ResizeElement<T>[] getChildren() {
        return null;
    }
    
    @Override
    public void adapt( double deltaWidth, double deltaHeight ) {
        // nothing to do
    }
}