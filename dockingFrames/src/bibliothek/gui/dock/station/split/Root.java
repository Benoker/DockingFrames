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

import java.awt.Dimension;
import java.awt.Insets;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;

/**
 * The root of the tree that represents the internal structure of a 
 * {@link SplitDockStation}.
 * @author Benjamin Sigg
 */
public class Root extends VisibleSplitNode{
	/** the single child of this root */
    private SplitNode child;
    
    /** tells whether the subtree has changed since the last reset */
    private boolean treeChanged = true;
    
    /**
     * Creates a new root.
     * @param access the access to internal methods of the
     * {@link SplitDockStation}, must not be <code>null</code>
     */
    public Root( SplitDockAccess access ){
        super( access, -1 );
    }
    
    /**
     * Creates a new root.
     * @param access the access to internal methods of the
     * {@link SplitDockStation}, must not be <code>null</code>
     * @param id the unique identifier of this root
     */
    public Root( SplitDockAccess access, long id ){
        super( access, id );
    }
    
    @Override
    protected void treeChanged(){
	    treeChanged = true;
    }
    
    /**
     * Tells whether the tree below this root has changed (children have
     * been added or removed) since the boundaries of this root were
     * last updated. 
     * @return <code>true</code> if the tree changed
     */
    public boolean hasTreeChanged(){
    	return treeChanged;
    }
    
    /**
     * Sets the child of this root. Every root has only one child.<br>
     * Note that setting the child to <code>null</code> does not delete
     * the child from the system, only a call to {@link SplitNode#delete(boolean)}
     * does that.
     * @param child the child of the root, can be <code>null</code>
     */
    public void setChild( SplitNode child ){
    	if( this.child != child ){
	        if( this.child != null )
	            this.child.setParent( null );
	        this.child = child;
	        if( child != null ){
	            child.delete( false );
	            child.setParent( this );
	        }
	        
	        treeChanged();
	        if( child != null ){
	        	ensureIdUniqueAsync();
	        }
	        
	        getAccess().getOwner().revalidate();
	        getAccess().getOwner().repaint();
    	}
    }
    
    /**
     * Gets the child of this root.
     * @return the child or <code>null</code>
     * @see #setChild(SplitNode)
     */
    public SplitNode getChild() {
        return child;
    }
    
    @Override
    public int getChildLocation( SplitNode child ) {
        if( child == this.child )
            return 0;
        
        return -1;
    }
    
    @Override
    public void setChild( SplitNode child, int location ) {
        if( location == 0 )
            setChild( child );
        else
            throw new IllegalArgumentException( "Location invalid: " + location );
    }
    
    @Override
    public int getMaxChildrenCount(){
    	return 1;
    }
    
    @Override
    public SplitNode getChild( int location ){
	    if( location == 0 ){
	    	return getChild();
	    }
	    return null;
    }
    
    @Override
    public Root getRoot() {
        return this;
    }
    
    @Override
    public boolean isOfUse(){
    	return true;
    }
    
    @Override
    public Dimension getMinimumSize() {
    	Dimension result = child == null ? null : child.getMinimumSize();
    	if( result == null )
    		return new Dimension( 0, 0 );
    	return result;
    }
        
    /**
     * Gets the factor which has to be multiplied with relative x coordinates
     * and widths to get their size in pixel.
     * @return the horizontal stretch factor
     */
    public double getWidthFactor(){
        Insets insets = getAccess().getOwner().getInsets();
        return getAccess().getOwner().getWidth() - insets.left - insets.right;
    }
    
    /**
     * Gets the factor which has to be multiplied with a relative y coordinate
     * or height to get their size in pixel.
     * @return the vertical stretch factor
     */
    public double getHeightFactor(){
        Insets insets = getAccess().getOwner().getInsets();
        return getAccess().getOwner().getHeight() - insets.top - insets.bottom;
    }
    
    /**
     * Gets the preferred operation when dragging the {@link Dockable}
     * <code>drop</code> to the location <code>x/y</code>.
     * @param x the x-coordinate of the mouse
     * @param y the y-coordinate of the mouse
     * @param drop the Dockable which will be dropped
     * @return where to trop the Dockable or <code>null</code>
     */
    public PutInfo getPut( int x, int y, Dockable drop ){
        double factorW = getWidthFactor();
        double factorH = getHeightFactor();
        
        return getPut( x, y, factorW, factorH, drop );           
    }
    
    /**
     * Tells whether the location x/y is in the override zone.
     * @param x the x-coordinate of the mouse
     * @param y the y-coordinate of the mouse
     * @return <code>true</code> if this station should have priority
     * over all other stations when the mouse is in x/y.
     */
    public boolean isInOverrideZone( int x, int y ){
        double factorW = getWidthFactor();
        double factorH = getHeightFactor();
        
        return isInOverrideZone( x, y, factorW, factorH );
    }
    
    @Override
    public void evolve( SplitDockTree<Dockable>.Key key, boolean checkValidity, Map<Leaf, Dockable> linksToSet ){
    	setChild( create( key, checkValidity, linksToSet ) );
    }
    
    @Override
    public boolean insert( SplitDockPlaceholderProperty property, Dockable dockable ){
    	boolean done = child != null && child.insert( property, dockable );
    	if( !done ){
    		return getAccess().drop( dockable, property.toSplitLocation( this ), this );
    	}
    	return true;
    }
    
    @Override
    public boolean insert( SplitDockPathProperty property, int depth, Dockable dockable ) {
        if( child == null ){
        	long id = property.getLeafId();
        	Leaf leaf = create( dockable, id );
            if( leaf == null )
                return false;
            setChild( leaf );
            leaf.setDockable( dockable, true );
            return true;
        }
        else
            return child.insert( property, depth, dockable );
    }
    

    @Override
    public <N> N submit( SplitTreeFactory<N> factory ) {
        if( child == null )
            return factory.root( null, getId() );
        else
            return factory.root( child.submit( factory ), getId() );
    }
    
    @Override
    public boolean isVisible(){
	    return true;
    }
    
    @Override
    public SplitNode getVisible(){
	    return this;
    }
    
    @Override
    public void updateBounds( double x, double y, double width, double height, double factorW, double factorH, boolean components ) {
        super.updateBounds( x, y, width, height, factorW, factorH, components );
        if( child != null )
            child.updateBounds( x, y, width, height, factorW, factorH, components );
        treeChanged = false;
    }

    @Override
    public PutInfo getPut( int x, int y, double factorW, double factorH, Dockable drop ) {
        if( !getBounds().contains( x, y ))
            return null;
        
        if( child != null )
            return child.getPut( x, y, factorW, factorH, drop );
        else
            return null;
    }
    
    @Override
    public boolean isInOverrideZone( int x, int y, double factorW, double factorH ) {
        if( !getBounds().contains( x, y ))
            return false;
        
        if( child != null )
            return child.isInOverrideZone( x, y, factorW, factorH );
        else
            return false;
    }
    
    @Override
    public Leaf getLeaf( Dockable dockable ) {
        return child == null ? null : child.getLeaf( dockable );
    }
    
    @Override
    public Node getDividerNode( int x, int y ) {
        if( child == null )
            return null;
        else
            return child.getDividerNode( x, y );
    }

    @Override
    public void visit( SplitNodeVisitor visitor ) {
        visitor.handleRoot( this );
        if( child != null )
            child.visit( visitor );
    }
    
    @Override
    public void toString( int tabs, StringBuilder out ) {
        out.append( "Root [id=" );
        out.append( getId() );
        out.append( "]\n" );
        for( int i = 0; i < tabs+1; i++ )
            out.append( '\t' );
        
        if( child != null )
            child.toString( tabs+1, out );
        else
            out.append( "<null>" );
    }
}