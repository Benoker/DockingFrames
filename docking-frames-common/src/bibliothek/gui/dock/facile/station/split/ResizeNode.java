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

import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.station.split.Node;

/**
 * Represents a {@link Node}.
 * @author Benjamin Sigg
 */
public class ResizeNode<T> extends ResizeElement<T>{
    /** the node that is represented by this node-element */
    private Node node;
    
    /** size of the divider before the resize */
    private double dividerSize;
    
    /** the two children of this node */
    @SuppressWarnings("unchecked")
    private ResizeElement<T>[] children = new ResizeElement[2];
    
    /**
     * Creates a new node-element.
     * @param layout the layout that uses this node
     * @param parent the parent of this node
     * @param node the node that is represented by this node-element
     */
    public ResizeNode( LockedResizeLayoutManager<T> layout, ResizeElement<T> parent, Node node ){
        super( parent, layout );
        this.node = node;
        children[0] = layout.toElement( this, node.getLeft() );
        children[1] = layout.toElement( this, node.getRight() );
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
    public ResizeElement<T> getLeft() {
        return children[0];
    }
    
    /**
     * Gets the right or bottom child of this node
     * @return the left or bottom child
     */
    public ResizeElement<T> getRight() {
        return children[1];
    }
    
    @Override
    protected ResizeElement<T>[] getChildren() {
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
    public double getDividerWidth(){
        return node.getStation().getDividerSize() / node.getRoot().getWidthFactor();
    }
    
    /**
     * Gets the space that a divider needs in the height.
     * @return the height of a divider
     */
    public double getDividerHeight(){
        return node.getStation().getDividerSize() / node.getRoot().getHeightFactor();
    }

    /**
     * Gets the space the divider needs right now.
     * @return the space the divider occupies now
     */
    public double getNewDividerSize(){
        if( node.getOrientation() == Orientation.HORIZONTAL )
            return getDividerWidth();
        else
            return getDividerHeight();
    }
    
    /**
     * Gets the space the divider needed before the resize.
     * @return the old size needed by the divider
     */
    public double getOldDividerSize(){
        return dividerSize;
    }
    
    @Override
    protected ResizeRequest createRequest() {
         ResizeRequest alpha = getLeft().getRequest();
         ResizeRequest beta = getRight().getRequest();
         boolean horizontal = node.getOrientation() == Orientation.HORIZONTAL;
         
         if( horizontal ){
             return getLayout().getConflictResolver().requestHorizontal( alpha, beta, this );
         }
         else{
             return getLayout().getConflictResolver().requestVertical( alpha, beta, this );
         }
    }
    
    @Override
    public void adapt( double deltaWidth, double deltaHeight ) {
        ResizeRequest alpha = getLeft().getRequest();
        ResizeRequest beta = getRight().getRequest();
        
        boolean horizontal = node.getOrientation() == Orientation.HORIZONTAL;
        
        if( horizontal ){
            if( alpha != null && alpha.getFractionWidth() == -1 )
                alpha = null;
            if( beta != null && beta.getFractionWidth() == -1 )
                beta = null;
        }
        else{
            if( alpha != null && alpha.getFractionHeight() == -1 )
                alpha = null;
            if( beta != null && beta.getFractionHeight() == -1 )
                beta = null;
        }
     
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
                
                delta = getLayout().getConflictResolver().resolveHorizontal( this, alpha, deltaLeft, beta, deltaRight );
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

                delta = getLayout().getConflictResolver().resolveVertical( this, alpha, deltaTop, beta, deltaBottom );
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