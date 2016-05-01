/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.common.layout;

import bibliothek.gui.dock.facile.station.split.*;

/**
 * This {@link ConflictResolver} has a alternative strategy how to handle cases
 * where two requests collide and a satisfying solution is not possible: the
 * two affected trees are checked for their "fully locked state" and if one
 * is fully locked but not the other, then the request of the fully locked one
 * is answered. Fully locked means that each child of some node has a request. 
 * @author Benjamin Sigg
 *
 */
public class FullLockConflictResolver extends DefaultConflictResolver<RequestDimension>{
    @Override
    public double resolveHorizontal( ResizeNode<RequestDimension> node,
            ResizeRequest left, double deltaLeft, ResizeRequest right,
            double deltaRight ) {

        if( left.getFractionWidth() == 1 && right.getFractionWidth() == 1 ){
            boolean leftLocked = checkHorizontalFullLock( node.getLeft() );
            boolean rightLocked = checkHorizontalFullLock( node.getRight() );

            if( !leftLocked && rightLocked ){
                return deltaRight;
            }
            else if( leftLocked && !rightLocked ){
                return deltaLeft;
            }
        }

        return super.resolveHorizontal( node, left, deltaLeft, right, deltaRight );
    }
    @Override
    public double resolveVertical( ResizeNode<RequestDimension> node,
            ResizeRequest top, double deltaTop, ResizeRequest bottom,
            double deltaBottom ) {
        
        if( top.getFractionWidth() == 1 && bottom.getFractionWidth() == 1 ){
            boolean topLocked = checkVerticalFullLock( node.getLeft() );
            boolean bottomLocked = checkVerticalFullLock( node.getRight() );
            
            if( !topLocked && bottomLocked ){
                return deltaBottom;
            }
            else if( topLocked && !bottomLocked ){
                return deltaTop;
            }
        }

        return super.resolveVertical( node, top, deltaTop, bottom, deltaBottom );
    }

    /**
     * Checks whether <code>element</code> is fully locked in its horizontal dimension.
     * Fully locked means that: <br>
     * <ul>
     *  <li>if <code>element</code> is a node: its children have both requests
     *  for a change in width and its children are also
     *  {@link #checkHorizontalFullLock(ResizeElement) fully locked}</li>
     *  <li>if <code>element</code> is a leaf: it has a resize request for the width</li>
     *  <li>in any other case: <code>true</code></li>
     * </ul> 
     * @param element the element to check
     * @return <code>true</code> if the horizontal dimension has very high
     * priority for this node, <code>false</code> otherwise
     */
    protected boolean checkHorizontalFullLock( ResizeElement<RequestDimension> element ){
        if( element instanceof ResizeNode<?> ){
            ResizeNode<RequestDimension> node = (ResizeNode<RequestDimension>)element;

            ResizeRequest leftRequest = node.getLeft().getRequest();
            ResizeRequest rightRequest = node.getRight().getRequest();

            if( leftRequest == null || leftRequest.getFractionWidth() == -1 )
                return false;

            if( rightRequest == null || rightRequest.getFractionWidth() == -1 )
                return false;

            if( !checkHorizontalFullLock( node.getLeft()))
                return false;

            if( !checkHorizontalFullLock( node.getRight()))
                return false;
        }
        else if( element instanceof ResizeLeaf<?> ){
            ResizeLeaf<RequestDimension> leaf = (ResizeLeaf<RequestDimension>)element;
            ResizeRequest request = leaf.getRequest();
            if( request == null || request.getFractionWidth() == -1 )
                return false;
        }

        return true;
    }
    

    /**
     * Checks whether <code>element</code> is fully locked in its vertical dimension.
     * Fully locked means that: <br>
     * <ul>
     *  <li>if <code>element</code> is a node: its children have both requests
     *  for a change in height and its children are also
     *  {@link #checkHorizontalFullLock(ResizeElement) fully locked}</li>
     *  <li>if <code>element</code> is a leaf: it has a resize request for the height</li>
     *  <li>in any other case: <code>true</code></li>
     * </ul> 
     * @param element the element to check
     * @return <code>true</code> if the vertical dimension has very high
     * priority for this node, <code>false</code> otherwise
     */
    protected boolean checkVerticalFullLock( ResizeElement<RequestDimension> element ){
        if( element instanceof ResizeNode<?> ){
            ResizeNode<RequestDimension> node = (ResizeNode<RequestDimension>)element;

            ResizeRequest leftRequest = node.getLeft().getRequest();
            ResizeRequest rightRequest = node.getRight().getRequest();

            if( leftRequest == null || leftRequest.getFractionHeight() == -1 )
                return false;

            if( rightRequest == null || rightRequest.getFractionHeight() == -1 )
                return false;

            if( !checkVerticalFullLock( node.getLeft()))
                return false;

            if( !checkVerticalFullLock( node.getRight()))
                return false;
        }
        else if( element instanceof ResizeLeaf<?> ){
            ResizeLeaf<RequestDimension> leaf = (ResizeLeaf<RequestDimension>)element;
            ResizeRequest request = leaf.getRequest();
            if( request == null || request.getFractionHeight() == -1 )
                return false;
        }

        return true;
    }
}
