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

import java.awt.Rectangle;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.split.DefaultSplitLayoutManager;
import bibliothek.gui.dock.station.split.Leaf;
import bibliothek.gui.dock.station.split.Node;
import bibliothek.gui.dock.station.split.Root;
import bibliothek.gui.dock.station.split.SplitLayoutManager;
import bibliothek.gui.dock.station.split.SplitNode;

/**
 * A {@link SplitLayoutManager} that can lock the size of some {@link Dockable}s
 * during resize. This class is intended to be subclassed.
 * @param <T> the type of the temporary data this manager works with 
 * @author Benjamin Sigg
 */
public abstract class LockedResizeLayoutManager<T> extends DelegatingSplitLayoutManager{
    /**
     * Tells how to merge the {@link ResizeRequest}s of this manager.
     */
    private ConflictResolver<T> conflictResolver = new DefaultConflictResolver<T>();
    
    /**
     * Creates a new manager using the {@link DefaultSplitLayoutManager}
     * as delegate.
     */
    public LockedResizeLayoutManager(){
        this( new DefaultSplitLayoutManager() );
    }
    
    /**
     * Creates a new manager.
     * @param delegate the base functionality 
     */
    public LockedResizeLayoutManager( SplitLayoutManager delegate ){
        super( delegate );
    }
    
    /**
     * Sets the {@link ConflictResolver} that will determine how to merge
     * {@link ResizeRequest}s and how to resolve conflicts.
     * @param conflictResolver the new policy, not <code>null</code>
     */
    public void setConflictResolver( ConflictResolver<T> conflictResolver ) {
        if( conflictResolver == null )
            throw new IllegalArgumentException( "conflictResolver must not be null" );
        this.conflictResolver = conflictResolver;
    }
    
    /**
     * Gets the policy that tells how two {@link ResizeRequest}s are merged.
     * @return the policy
     */
    public ConflictResolver<T> getConflictResolver() {
        return conflictResolver;
    }
    
    @Override
    public void updateBounds( Root root, double x, double y, double factorW, double factorH ) {
        Rectangle current = root.getCurrentBounds();
        Rectangle bounds = root.getBounds();
        
        boolean resize = isResize( root ); 

        if( resize ){
            resize = current.width > 10 && current.height > 10 && bounds.width > 10 && bounds.height > 10;
        }
        
        if( resize ){
        	updateBoundsLocked( root, x, y, factorW, factorH );
        }
        else if( hasTreeChanged( root )){
        	updateBoundsLocked( root, x, y, factorW, factorH );	
        }
        else{ 
        	super.updateBounds( root, x, y, factorW, factorH );
        }
    }

    /**
     * Tells whether the current operation is a resize operation. The locked sizes will only be respected if
     * the operation is a resize operation.
     * @param root the item that is going to be updated
     * @return whether a resize operation is in progress
     */
    protected boolean isResize( Root root ){
    	Rectangle current = root.getCurrentBounds();
        Rectangle bounds = root.getBounds();
        
        return !current.equals( bounds );
    }

    /**
     * Tells whether the current operation happens because the tree has changed (e.g. a leaf has been added or removed). 
     * This method is only called if {@link #isResize(Root)} already returned <code>false</code>. 
     * @param root the item that is going to be updated
     * @return whether the tree has changed
     */
    protected boolean hasTreeChanged( Root root ){
    	return root.hasTreeChanged();
    }
    
    /**
     * Updates the bounds of <code>root</code> and all its children and does
     * consider all {@link ResizeRequest}.
     * @param root the root element of a tree to update
     * @param x the left coordinate of <code>root</code>
     * @param y the top coordinate of <code>root</code>
     * @param factorW a factor all x-coordinates have to be multiplied with
     * in order to get the pixel coordinates
     * @param factorH a factor all y-coordinates have to be multiplied with
     * in order to get the pixel coordinates
     */
    public void updateBoundsLocked( Root root, double x, double y, double factorW, double factorH ){
        ResizeElement<T> element = toElement( null, root );
        element.prepareResize();
        root.updateBounds( x, y, 1, 1, factorW, factorH, false );
        element.prepareRequests();
        element.adapt( 0, 0 );
        root.updateBounds( x, y, 1, 1, factorW, factorH, true );
    }

    
    /**
     * Gets the size request that changes the size of <code>leaf</code> such
     * that it has a valid size again.
     * @param t the data that was created in {@link #prepareResize(Leaf)} or <code>null</code>
     * @param leaf the leaf which size is not yet valid.
     * @return the preferred size or <code>null</code>
     */
    public abstract ResizeRequest getRequest( T t, Leaf leaf );
    
    /**
     * Called before the resize takes place, subclasses might store some
     * properties.
     * @param leaf some leaf
     * @return some temporary data that gets forwarded to {@link #getRequest(Object, Leaf)},
     * can be <code>null</code>
     */
    public abstract T prepareResize( Leaf leaf );
    
    /**
     * Transforms a {@link SplitNode} into the matching kind of {@link ResizeElement}.
     * The subtree of <code>node</code> is transformed as well.
     * @param parent the parent of the new element
     * @param node some root, node, leaf or <code>null</code>
     * @return some root, node, leaf or <code>null</code>
     */
    public ResizeElement<T> toElement( ResizeElement<T> parent, SplitNode node ){
    	for( int i = 0; i < 5; i++ ){
    		ResizeElement<T> result = asyncToElement( parent, node );
    		if( result == null || result.isValid() ){
    			return result;
    		}
    		
    		try {
				Thread.sleep( 20 );
			}
			catch( InterruptedException e ) {
				// ignore
			}
    	}
    	
    	System.err.println( "LockedResizeLayoutManager.toElement: Potential race condition detected, converting SplitNode to ResizeElement failed 5 times in a row. The node is ignored." );
    	return null;
    }
    
    /**
     * Called by {@link #toElement(ResizeElement, SplitNode)}, tries to create a {@link ResizeElement} out of
     * <code>node</code> in an environment where race conditions are possible. This is a best effort method, callers
     * should check the result using the method {@link ResizeElement#isValid()}.
     * @param parent the parent of the new element
     * @param node some root, node, leaf or <code>null</code>
     * @return some root, node, leaf or <code>null</code>
     */
    protected ResizeElement<T> asyncToElement( ResizeElement<T> parent, SplitNode node ){
    	if( node instanceof Root ){
            return new ResizeRoot<T>( this, (Root)node );
    	}
        if( node instanceof Node ){
        	Node real = (Node)node;
        	SplitNode left = real.getLeft();
        	SplitNode right = real.getRight();
        	
        	boolean leftVisible = left == null ? false : left.isVisible();
        	boolean rightVisible = right == null ? false : right.isVisible();
        	if( leftVisible && rightVisible ){
        		return new ResizeNode<T>( this, parent, (Node)node );
        	}
        	if( leftVisible ){
        		return toElement( parent, left.getVisible() );
        	}
        	if( rightVisible ){
        		return toElement( parent, right.getVisible() );
        	}
        	return null;
        }
        if( node instanceof Leaf ){
            return new ResizeLeaf<T>( this, parent, (Leaf)node );
        }
        
        return null;
    }
}


