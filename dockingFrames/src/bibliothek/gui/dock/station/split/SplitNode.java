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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.DockAcceptance;
import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.station.SplitDockStation;

/**
 * The internal representation of a {@link SplitDockStation} is a tree. The subclasses of SplitNode build this tree.
 * @author Benjamin Sigg
 */
public abstract class SplitNode{
	/** Internal access to the owner-station */
    private SplitDockAccess access;
    /** Parent node of this node */
    private SplitNode parent;
    /** Bounds of this node on the station */
    protected double x, y, width, height;
    
    /**
     * Creates a new SplitNode.
     * @param access the access to the owner of this node. Must not be <code>null</code>
     */
    protected SplitNode( SplitDockAccess access ){
        if( access == null )
            throw new IllegalArgumentException( "Access must not be null" );
        this.access = access;
    }
    
    /**
     * Gets the relative x-coordinate of this node on the owner-station. The coordinates
     * are measured as fraction of the size of the owner-station.
     * @return A value between 0 and 1
     */
    public double getX() {
        return x;
    }
    
    /**
     * Gets the relative y-coordinate of this node on the owner-station. The coordinates
     * are measured as fraction of the size of the owner-station.
     * @return A value between 0 and 1
     */
    public double getY() {
        return y;
    }
    
    /**
     * Gets the relative width of this node in relation to the owner-station.
     * @return a value between 0 and 1
     */
    public double getWidth() {
        return width;
    }
    
    /**
     * Gets the relative height of this node in relation to the owner-station.
     * @return a value between 0 and 1
     */
    public double getHeight() {
        return height;
    }
    
    /**
     * Sets the parent of this node. 
     * @param parent the new parent, can be <code>null</code>
     */
    public void setParent( SplitNode parent ){
        this.parent = parent;
    }
    
    /**
     * Gets the parent of this node.
     * @return the parent, can be <code>null</code>
     */
    public SplitNode getParent(){
        return parent;
    }
    
    /**
     * Gets access to the owner-station
     * @return the access
     */
    protected SplitDockAccess getAccess(){
        return access;
    }
    
    /**
     * Gets the minimal size of this node.
     * @return the minimal size in pixel
     */
    public abstract Dimension getMinimumSize();
    
    /**
     * Updates the bounds of this node. If the node represents a {@link Component}, then 
     * the bounds of the component have to be updated as well.
     * @param x the relative x-coordinate
     * @param y the relative y-coordinate
     * @param width the relative width of the node
     * @param height the relative height of the node
     * @param factorW a factor to be multiplied with <code>x</code> and <code>width</code> 
     * to get the size of the node in pixel
     * @param factorH a factor to be multiplied with <code>y</code> and <code>height</code>
     * to get the size of the node in pixel 
     */
    public void updateBounds( double x, double y, double width, 
            double height, double factorW, double factorH ){
        
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    /**
     * Gets the size and location of this node in pixel where the point
     * 0/0 is equal to the point 0/0 on the owner-station.
     * @return the size and location
     */
    public Rectangle getBounds(){
        Root root = getRoot();
        double fw = root.getWidthFactor();
        double fh = root.getHeightFactor();
        Rectangle rec = new Rectangle( 
                (int)(x * fw + 0.5),
                (int)(y * fh + 0.5),
                (int)(width * fw + 0.5),
                (int)(height * fh + 0.5 ));
        
        rec.width = Math.max( 0, rec.width );
        rec.height = Math.max( 0, rec.height );
        return rec;
    }
    
    /**
     * Gets the size of this node in pixel.
     * @return the size of the node
     */
    public Dimension getSize(){
        Root root = getRoot();
        double fw = root.getWidthFactor();
        double fh = root.getHeightFactor();
        return new Dimension( 
                (int)(width * fw + 0.5),
                (int)(height * fh + 0.5 ));
    }
    
    /**
     * Gets the root of the tree in which this node is
     * @return the root
     */
    protected Root getRoot(){
        return parent.getRoot();
    }
    
    /**
     * Determines where to drop the {@link Dockable} <code>drop</code> 
     * if the mouse is at location x/y.
     * @param x the x-coordinate of the mouse
     * @param y the y-coordinate of the mouse
     * @param factorW a factor to be multiplied with the relative 
     * {@link #getX() x} and {@link #getWidth() width} to get the 
     * size in pixel. 
     * @param factorH a factor to be multiplied with the relative
     * {@link #getY() y} and {@link #getHeight() height} to get the
     * size in pixel.
     * @param drop the {@link Dockable} which will be dropped 
     * @return where to drop the dockable or <code>null</code> if
     * the dockable can't be dropped
     */
    public abstract PutInfo getPut( int x, int y, double factorW, double factorH, Dockable drop );
    
    /**
     * Gets the leaf which represents <code>dockable</code>.
     * @param dockable the Dockable whose leaf is searched
     * @return the leaf or <code>null</code> if no leaf was found
     */
    public abstract Leaf getLeaf( Dockable dockable );
    
    /**
     * Gets the Node whose divider area contains the point x/y. Only searches
     * in the subtree with this node as root.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the Node containing the point, if no Node was found,
     * <code>null</code> is returned
     */
    public abstract Node getDividerNode( int x, int y );
        
    /**
     * Invokes one of the methods of the <code>visitor</code> for every
     * child in the subtree with this as root.
     * @param visitor the visitor
     */
    public abstract void visit( SplitNodeVisitor visitor );
    
    /**
     * Writes the structure of the subtree with this as root into <code>out</code>.
     * @param children a map that contains for every {@link Dockable}
     * an id. This id will be written into <code>out</code> to represent
     * the {@link Dockable}.
     * @param out the stream to fill
     * @throws IOException if the stream throws an exception
     */
    public abstract void write( Map<Dockable, Integer> children, DataOutputStream out ) throws IOException;
    
    /**
     * Writes the contents of a single child into <code>out</code>.
     * @param element the element to store
     * @param children the unique ids of all {@link Dockable Dockables} which
     * may occur.
     * @param out the stream to fill
     * @throws IOException if the stream throws an exception
     * @see #readChild(Map, DataInputStream)
     */
    protected void writeChild( SplitNode element, Map<Dockable, Integer> children, DataOutputStream out ) throws IOException{
        out.writeBoolean( element instanceof Node );
        element.write( children, out );
    }
    
    /**
     * Reads an earlier written node from the stream <code>in</code>.
     * @param children a map of substitutions for ids that are found in the stream.
     * @param in the source
     * @return the newly created node
     * @throws IOException if the stream throws an exception
     * @see #write(Map, DataOutputStream)
     */
    public abstract SplitNode read( Map<Integer, Dockable> children, DataInputStream in ) throws IOException;
    
    /**
     * Reads a single node which was earlier written.
     * @param children a map of substitutions for ids that are found in the stream.
     * @param in the source
     * @return the newly read node
     * @throws IOException if the stream throws an exception
     * @see #writeChild(SplitNode, Map, DataOutputStream)
     */
    protected SplitNode readChild( Map<Integer, Dockable> children, DataInputStream in ) throws IOException {
        if( in.readBoolean() ){
            Node node = new Node( getAccess() );
            return node.read( children, in );
        }
        else{
            Leaf leaf = new Leaf( getAccess() );
            return leaf.read( children, in );
        }
    }
    
    /**
     * Creates or replaces children according to the values found in 
     * <code>key</code>.
     * @param key the key to read
     */
    public abstract void evolve( SplitDockTree.Key key );
    
    /**
     * Writes the contents of this node into <code>tree</code>.
     * @param tree the tree to write into
     * @return the key of this node
     */
    public abstract SplitDockTree.Key submit( SplitDockTree tree );
    
    /**
     * Creates a new node using the contents of <code>key</code>.
     * @param key the key to read
     * @return the new node
     */
    protected SplitNode create( SplitDockTree.Key key ){
    	if( key.getTree().isDockable( key )){
            SplitDockStation split = access.getOwner();
            DockController controller = split.getController();
            DockAcceptance acceptance = controller == null ? null : controller.getAcceptance();
            Dockable[] dockables = key.getTree().getDockables( key );
            Leaf leaf;
            if( dockables.length == 1 ){
                if( !dockables[0].accept( split ) || 
                        !split.accept( dockables[0] ))
                    throw new SplitDropTreeException( split, "No acceptance for " + dockables[0] );
                
                if( acceptance != null ){
                    if( !acceptance.accept( split, dockables[0] ))
                        throw new SplitDropTreeException( split, "DockAcceptance does not allow child " + dockables[0] );
                }
                
                leaf = access.createLeaf( dockables[0] );
            }
            else{
                if( !dockables[0].accept( split, dockables[1] ) ||
                        !dockables[1].accept( split, dockables[1] ))
                        throw new SplitDropTreeException( split, 
                                "No acceptance for combination of " + dockables[0] + " and " + dockables[1] );
                
                if( acceptance != null ){
                    if( !acceptance.accept( split, dockables[0], dockables[1] ))
                        throw new SplitDropTreeException( split,
                                "DockAcceptance does not allow to combine " + dockables[0] + " and " + dockables[1] );
                }
                
                Dockable combination = access.getOwner().getCombiner().combine( dockables[0], dockables[1], access.getOwner() );
                if( dockables.length == 2 )
                    leaf = access.createLeaf( combination );
                else{
                    DockStation station = combination.asDockStation();
                    if( station == null )
                        throw new SplitDropTreeException( access.getOwner(), "Combination of two Dockables does not create a new station" );
                    
                    for( int i = 2; i < dockables.length; i++ ){
                        Dockable dockable = dockables[ i ];
                        if( !dockable.accept( station ) || !station.accept( dockable ))
                            throw new SplitDropTreeException( access.getOwner(), "No acceptance of " + dockable + " and " + station );
                        
                        if( acceptance != null ){
                            if( !acceptance.accept( station, dockable ))
                                throw new SplitDropTreeException( split,
                                        "DockAcceptance does not allow " + dockable + " as child of " + station );
                        }
                        
                        station.drop( dockable );
                    }
                    
                    leaf = access.createLeaf( combination );
                }
            }
            
    		leaf.evolve( key );
    		return leaf;
    	}
    	else{
    		Node node = new Node( getAccess() );
        	node.evolve( key );
        	return node;
    	}
    }
    
    /**
     * Calculates how much of the rectangle given by the property lies inside
     * this node and how much of this node lies in the rectangle. The result
     * is a value between 0 and 1 which is 1 only if this node and the rectangle
     * are identical. The result is 0 if they do not have a shared area.
     * @param property the property that gives a rectangle
     * @return Area of intersection divided by the maxima of the area
     * of the rectangle and of this node.
     */
    public double intersection( SplitDockProperty property ){
        double rx1 = Math.max( x, property.getX() );
        double ry1 = Math.max( y, property.getY() );
        double rx2 = Math.min( x+width, property.getX() + property.getWidth() );
        double ry2 = Math.min( y+height, property.getY() + property.getHeight() );
        
        if( rx1 > rx2 || ry1 > ry2 )
            return 0;
        
        if( property.getWidth() == 0 || property.getHeight() == 0 )
            return 0;
        
        double max = Math.max( property.getWidth()*property.getHeight(), width*height );
        
        return (rx2-rx1)*(ry2-ry1) / max;
    }
    
    /**
     * Calculates on which side of the node the point <code>kx/ky</code> lies.
     * @param kx the relative x-coordinate of the point
     * @param ky the relative y-coordinate of the point
     * @return One side of the node
     */
    public PutInfo.Put relativeSidePut( double kx, double ky ){
        if( above( x, y, x+width, y+height, kx, ky )){
            if( above( x, y+height, x+width, y, kx, ky ))
               return PutInfo.Put.TOP;
            else
               return PutInfo.Put.RIGHT;
        }
        else{
            if( above( x, y+height, x+width, y, kx, ky ))
                return PutInfo.Put.LEFT;
             else
                return PutInfo.Put.BOTTOM;                
        }
    }

    /**
     * Calculates whether the point <code>x/y</code> lies above
     * the line going through <code>x1/y1</code> and <code>x2/y2</code>.
     * @param x1 the x-coordinate of the first point on the line
     * @param y1 the y-coordinate of the first point on the line
     * @param x2 the x-coordinate of the second point on the line
     * @param y2 the y-coordinate of the second point on the line
     * @param x the x-coordinate of the point which may be above the line
     * @param y the y-coordinate of the point which may be above the line
     * @return <code>true</code> if the point lies above the line, <code>false</code>
     * otherwise
     */
    public static boolean above( double x1, double y1, double x2, double y2, double x, double y ){
        double a = y1 - y2;
        double b = x2 - x1;
        
        if( b == 0 )
            return false;
        
        double c = a*x1 + b*y1;
        double sy = (c - a*x) / b;
        
        return y < sy;
    }
}