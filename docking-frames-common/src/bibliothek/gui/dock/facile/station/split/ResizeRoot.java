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

import bibliothek.gui.dock.station.split.Root;

/**
 * Represents a {@link Root}.
 * @author Benjamin Sigg
 * @param <T> the kind of meta-data clients use
 */
public class ResizeRoot<T> extends ResizeElement<T>{
    /** the root which is represented by this root-element */
    private Root root;
    
    /** the one child of this root */
    @SuppressWarnings( "unchecked" )
    private ResizeElement<T>[] child = new ResizeElement[1];
    
    /**
     * Creates a new root-element
     * @param layout the layout that uses this root
     * @param root the root which is represented by this root-element
     */
    public ResizeRoot( LockedResizeLayoutManager<T> layout, Root root ){
        super( null, layout );
        this.root = root;
        this.child[0] = layout.toElement( this, root.getChild() );
    }
    
    /**
     * Gets the root which is represented by this root-element.
     * @return the root
     */
    public Root getRoot() {
        return root;
    }
    
    @Override
    public ResizeRoot<T> getResizeRoot() {
        return this;
    }
    
    /**
     * Gets the one child of this root
     * @return the child or <code>null</code>
     */
    public ResizeElement<T> getChild() {
        return child[0];
    }
    
    @Override
    protected ResizeElement<T>[] getChildren() {
        if( child[0] == null )
            return null;
        
        return child;
    }
    
    @Override
    protected ResizeRequest createRequest() {
        if( child[0] == null )
            return null;
        return child[0].getRequest();
    }
    
    @Override
    public void adapt( double deltaWidth, double deltaHeight ) {
        if( child[0] != null )
            child[0].adapt( deltaWidth, deltaHeight );
    }
}