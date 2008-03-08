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
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.station.split.*;

/**
 * A {@link SplitLayoutManager} that can lock the size of some {@link Dockable}s
 * during resize. This class is intended to be subclassed,
 * @param <T> the type of the temporary data this manager works with 
 * @author Benjamin Sigg
 */
public abstract class LockedResizeLayoutManager<T> extends DelegatingSplitLayoutManager{
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
    
    @Override
    public void updateBounds( Root root, double x, double y, double factorW, double factorH ) {
        Rectangle current = root.getCurrentBounds();
        Rectangle bounds = root.getBounds();
        
        boolean resize = !current.equals( bounds );
        if( resize ){
            resize = current.width > 10 && current.height > 10 && bounds.width > 10 && bounds.height > 10;
        }
        
        if( !resize ){
            super.updateBounds( root, x, y, factorW, factorH );
        }
        else{
            updateBoundsLocked( root, x, y, factorW, factorH );
        }
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
        ResizeElement element = toElement( null, root );
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
    protected abstract ResizeRequest getRequest( T t, Leaf leaf );
    
    /**
     * Called before the resize takes place, subclasses might store some
     * properties.
     * @param leaf some leaf
     * @return some temporary data that gets forwarded to {@link #getRequest(Object, Leaf)},
     * can be <code>null</code>
     */
    protected abstract T prepareResize( Leaf leaf );
    
    /**
     * Transforms a {@link SplitNode} into the matching kind of {@link ResizeElement}.
     * The subtree of <code>node</code> is transformed as well.
     * @param parent the parent of the new element
     * @param node some root, node, leaf or <code>null</code>
     * @return some root, node, leaf or <code>null</code>
     */
    protected ResizeElement toElement( ResizeElement parent, SplitNode node ){
        if( node instanceof Root )
            return new ResizeRoot( (Root)node );
        if( node instanceof Node )
            return new ResizeNode( parent, (Node)node );
        if( node instanceof Leaf )
            return new ResizeLeaf( parent, (Leaf)node );
        
        return null;
    }
    
    /**
     * Represents one node of the tree which represents the split-tree of
     * a {@link SplitDockStation}.
     * @author Benjamin Sigg
     */
    protected abstract class ResizeElement{
        /** the last request that was issued */
        private ResizeRequest request;
        /** the parent of this node */
        private ResizeElement parent;
        
        /**
         * Creates a new element.
         * @param parent the parent of <code>this</code>
         */
        public ResizeElement( ResizeElement parent ){
            this.parent = parent;
        }
        
        /**
         * Gets the parent of this node
         * @return the parent or <code>null</code> if this is a root
         */
        public ResizeElement getParent() {
            return parent;
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
        protected abstract ResizeElement[] getChildren();
        
        /**
         * Called before the bounds of a tree are updated, can be used
         * to store some properties that are later needed to create
         * the {@link ResizeRequest}.
         */
        public void prepareResize(){
            ResizeElement[] children = getChildren();
            if( children != null ){
                for( ResizeElement child : children )
                    child.prepareResize();
            }
        }
        
        /**
         * Calls {@link #createRequest()} on <code>this</code> and recursively
         * on all children. Stores the result for later analysis.
         */
        public void prepareRequests(){
            ResizeElement[] children = getChildren();
            if( children != null ){
                for( ResizeElement child : children )
                    child.prepareRequests();
            }
            request = createRequest();
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
        public ResizeRoot getResizeRoot(){
            return parent.getResizeRoot();
        }
    }
    
    /**
     * Represents the mismatch in size that some node has.
     * @author Benjamin Sigg
     */
    protected static class ResizeRequest{
        /** the mismatch in width */
        private double deltaWidth;
        /** the mismatch in height */
        private double deltaHeight;
        /** how much of the mismatch should be spend by the parent of the element which requested this */
        private int fractionWidth;
        /** how much of the mismatch should be spend by the parent of the element which requested this */
        private int fractionHeight;
        
        public ResizeRequest( double deltaWidth, double deltaHeight ){
            this( deltaWidth, deltaHeight, 1, 1 );
        }
        
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
         * this request should provide.
         * @return the fraction
         */
        public int getFractionWidth() {
            return fractionWidth;
        }
        
        /**
         * Gets the fraction of the mismatch the parent of the element that issued
         * this request should provide.
         * @return the fraction
         */
        public int getFractionHeight() {
            return fractionHeight;
        }
    }
     
    /**
     * Represents a {@link Leaf}.
     * @author Benjamin Sigg
     */
    protected class ResizeLeaf extends ResizeElement{
        /** the leaf that is represented by this ResizeLeaf */
        private Leaf leaf;
        /** temporary data created by the LayoutManager */
        private T temporary;
        
        /**
         * Creates a new leaf element.
         * @param parent the parent of this node
         * @param leaf the leaf that is represented by this element
         */
        public ResizeLeaf( ResizeElement parent, Leaf leaf ){
            super( parent );
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
            return LockedResizeLayoutManager.this.getRequest( temporary, leaf );
        }
        
        @Override
        public void prepareResize() {
            temporary = LockedResizeLayoutManager.this.prepareResize( leaf );
            super.prepareResize();
        }
        
        @Override
        protected ResizeElement[] getChildren() {
            return null;
        }
        
        @Override
        public void adapt( double deltaWidth, double deltaHeight ) {
            // nothing to do
        }
    }
    
    /**
     * Represents a {@link Node}.
     * @author Benjamin Sigg
     */
    protected class ResizeNode extends ResizeElement{
        /** the node that is represented by this node-element */
        private Node node;
        
        /** size of the divider before the resize */
        private double dividerSize;
        
        /** the two children of this node */
        @SuppressWarnings("unchecked")
        private ResizeElement[] children = new LockedResizeLayoutManager.ResizeElement[2];
        
        /**
         * Creates a new node-element.
         * @param parent the parent of this node
         * @param node the node that is represented by this node-element
         */
        public ResizeNode( ResizeElement parent, Node node ){
            super( parent );
            this.node = node;
            children[0] = toElement( this, node.getLeft() );
            children[1] = toElement( this, node.getRight() );
        }
        
        /**
         * Gets the node that is represented by this element
         * @return the node
         */
        public Node getNode() {
            return node;
        }
        
        /**
         * Gets the left or top child of this node
         * @return the left or top child
         */
        public ResizeElement getLeft() {
            return children[0];
        }
        
        /**
         * Gets the right or bottom child of this node
         * @return the left or bottom child
         */
        public ResizeElement getRight() {
            return children[1];
        }
        
        @Override
        protected ResizeElement[] getChildren() {
            return children;
        }
        
        @Override
        public void prepareResize() {
            super.prepareResize();
            if( node.getOrientation() == Orientation.HORIZONTAL )
                dividerSize = getDividerWidth();
            else
                dividerSize = getDividerHeight();
        }
        
        /**
         * Gets the space that a divider needs in the width.
         * @return the width of a divider
         */
        protected double getDividerWidth(){
            return node.getStation().getDividerSize() / node.getRoot().getWidthFactor();
        }
        
        /**
         * Gets the space that a divider needs in the height.
         * @return the height of a divider
         */
        protected double getDividerHeight(){
            return node.getStation().getDividerSize() / node.getRoot().getHeightFactor();
        }

        @Override
        protected ResizeRequest createRequest() {
             ResizeRequest alpha = getLeft().getRequest();
             ResizeRequest beta = getRight().getRequest();
             boolean horizontal = node.getOrientation() == Orientation.HORIZONTAL;
             
             if( alpha == null && beta == null )
                 return null;
             
             if( horizontal ){
                 if( alpha == null ){
                     return new ResizeRequest( 
                             beta.getDeltaWidth(), 
                             beta.getDeltaHeight(),
                             beta.getFractionWidth()+1,
                             beta.getFractionHeight());
                 }
                 
                 if( beta == null ){
                     return new ResizeRequest( 
                             alpha.getDeltaWidth(), 
                             alpha.getDeltaHeight(),
                             alpha.getFractionWidth()+1,
                             alpha.getFractionHeight());
                 }
             
             
                 return new ResizeRequest(
                         alpha.getDeltaWidth() / alpha.getFractionWidth() + beta.getDeltaWidth() / beta.getFractionWidth() +
                             getDividerWidth() - dividerSize,
                         Math.max( alpha.getDeltaHeight(), beta.getDeltaHeight() ),
                         1,
                         Math.min( alpha.getFractionHeight(), beta.getFractionHeight() ));
             }
             else{
                 if( alpha == null ){
                     return new ResizeRequest( 
                             beta.getDeltaWidth(), 
                             beta.getDeltaHeight(),
                             beta.getFractionWidth(),
                             beta.getFractionHeight()+1);
                 }
                 
                 if( beta == null ){
                     return new ResizeRequest( 
                             alpha.getDeltaWidth(), 
                             alpha.getDeltaHeight(),
                             alpha.getFractionWidth(),
                             alpha.getFractionHeight()+1);
                 }
                 
                 return new ResizeRequest(
                         Math.max( alpha.getDeltaWidth(), beta.getDeltaWidth() ),
                         alpha.getDeltaHeight() / alpha.getFractionHeight() +  beta.getDeltaHeight() / beta.getFractionHeight() +
                             getDividerHeight() - dividerSize,
                         Math.min( alpha.getFractionWidth(), beta.getFractionWidth() ),
                         1 );
             }
        }
        
        @Override
        public void adapt( double deltaWidth, double deltaHeight ) {
            ResizeRequest alpha = getLeft().getRequest();
            ResizeRequest beta = getRight().getRequest();
            
            boolean horizontal = node.getOrientation() == Orientation.HORIZONTAL;
            
            if( alpha == null && beta == null ){
                if( horizontal ){
                    getLeft().adapt( deltaWidth * node.getDivider(), deltaHeight );
                    getRight().adapt( deltaWidth * (1-node.getDivider()), deltaHeight );
                }
                else{
                    getLeft().adapt( deltaWidth, deltaHeight * node.getDivider());
                    getRight().adapt( deltaHeight, deltaHeight * (1-node.getDivider()));
                }
            }
            else if( horizontal ){
                double divider = node.getDivider();
                double width = node.getWidth();
                
                double dividerWidth = getDividerWidth();
                
                double leftBefore = width * divider - dividerWidth / 2.0;
                double rightBefore = width * (1-divider) - dividerWidth / 2.0;
                double delta;
                
                width += deltaWidth;
                
                if( beta == null ){ // alpha != null
                    double request = alpha.getDeltaWidth() / alpha.getFractionWidth();
                    request -= deltaWidth * divider;
                    delta = width <= 0.0 ? 0.0 : request / width;
                }
                else if( alpha == null ){ // beta != null
                    double request = beta.getDeltaWidth() / beta.getFractionWidth();
                    request -= deltaWidth * (1-divider);
                    delta = width <= 0.0 ? 0.0 : -request / width;
                }
                else{
                    double requestLeft = alpha.getDeltaWidth() / alpha.getFractionWidth();
                    double requestRight = beta.getDeltaWidth() / beta.getFractionWidth();
                    
                    requestLeft -= deltaWidth * divider;
                    requestRight -= deltaWidth * (1-divider);
                    
                    double deltaLeft = width <= 0.0 ? 0.0 : requestLeft / width;
                    double deltaRight = width <= 0.0 ? 0.0 : -requestRight / width;
                    
                    if( alpha.getFractionWidth() == 1 && beta.getFractionWidth() > 1 )
                        delta = deltaLeft;
                    else if( alpha.getFractionWidth() > 1 && beta.getFractionWidth() == 1 )
                        delta = deltaRight;
                    else
                        delta = (deltaLeft * alpha.getFractionWidth() + deltaRight * beta.getFractionWidth()) /
                            (alpha.getFractionWidth() + beta.getFractionWidth() );
                }
                
                divider += delta;
                divider = Math.min( 1.0, Math.max( 0.0, divider ) );
                
                node.setDivider( divider );
                
                double leftAfter = width * divider - dividerWidth / 2.0;
                double rightAfter = width * (1-divider) - dividerWidth / 2.0;
                
                getLeft().adapt( leftAfter - leftBefore, deltaHeight );
                getRight().adapt( rightAfter - rightBefore, deltaHeight );
            }
            else{ // vertical
                double divider = node.getDivider();
                double height = node.getHeight();
                
                double dividerHeight = getDividerHeight();
                
                double topBefore = height * divider - dividerHeight / 2.0;
                double bottomBefore = height * (1-divider) - dividerHeight / 2.0;
                double delta;
                
                height += deltaHeight;
                
                if( beta == null ){ // alpha != null
                    double request = alpha.getDeltaHeight() / alpha.getFractionHeight();
                    request -= deltaHeight * divider;
                    delta = height <= 0.0 ? 0.0 : request / height;
                }
                else if( alpha == null ){ // beta != null
                    double request = beta.getDeltaHeight() / beta.getFractionHeight();
                    request -= deltaHeight * (1-divider);
                    delta = height <= 0.0 ? 0.0 : -request / height;
                }
                else{
                    double requestTop = alpha.getDeltaHeight() / alpha.getFractionHeight();
                    double requestBottom = beta.getDeltaHeight() / beta.getFractionHeight();
                    
                    requestTop -= deltaHeight * divider;
                    requestBottom -= deltaHeight * (1-divider);
                    
                    double deltaTop = height <= 0.0 ? 0.0 : requestTop / height;
                    double deltaBottom = height <= 0.0 ? 0.0 : -requestBottom / height;
                    
                    if( alpha.getFractionHeight() == 1 && beta.getFractionHeight() > 1 )
                        delta = deltaTop;
                    else if( alpha.getFractionHeight() > 1 && beta.getFractionHeight() == 1 )
                        delta = deltaBottom;
                    else
                        delta = (deltaTop * alpha.getFractionHeight() + deltaBottom * beta.getFractionHeight()) /
                            (alpha.getFractionHeight() + beta.getFractionHeight() );
                }
                
                divider += delta;
                divider = Math.min( 1.0, Math.max( 0.0, divider ) );
                
                node.setDivider( divider );
                
                double topAfter = height * divider - dividerHeight / 2.0;
                double bottomAfter = height * (1-divider) - dividerHeight / 2.0;
                
                getLeft().adapt( deltaWidth, topAfter - topBefore );
                getRight().adapt( deltaWidth, bottomAfter - bottomBefore );
            }
        }
    }
    
    /**
     * Represents a {@link Root}.
     * @author Benjamin Sigg
     */
    protected class ResizeRoot extends ResizeElement{
        /** the root which is represented by this root-element */
        private Root root;
        
        /** the one child of this root */
        @SuppressWarnings( "unchecked" )
        private ResizeElement[] child = new LockedResizeLayoutManager.ResizeElement[1];
        
        /**
         * Creates a new root-element
         * @param root the root which is represented by this root-element
         */
        public ResizeRoot( Root root ){
            super( null );
            this.root = root;
            this.child[0] = toElement( this, root.getChild() );
        }
        
        /**
         * Gets the root which is represented by this root-element.
         * @return the root
         */
        public Root getRoot() {
            return root;
        }
        
        @Override
        public ResizeRoot getResizeRoot() {
            return this;
        }
        
        /**
         * Gets the one child of this root
         * @return the child or <code>null</code>
         */
        public ResizeElement getChild() {
            return child[0];
        }
        
        @Override
        protected ResizeElement[] getChildren() {
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
}


