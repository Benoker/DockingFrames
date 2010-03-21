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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.station.support.PlaceholderMap;

/**
 * The internal representation of a {@link SplitDockStation} is a tree. The subclasses of SplitNode build this tree.
 * @author Benjamin Sigg
 */
public abstract class SplitNode{
	/** Parent node of this node */
    private SplitNode parent;
    /** Bounds of this node on the station */
    protected double x, y, width, height;
    /** Internal access to the owner-station */
    private SplitDockAccess access;

    /** keys for {@link Dockable}s that are not visible but linked with this node */
    private List<Path> placeholders = new ArrayList<Path>();
    /** advanced placeholder information about a {@link DockStation} that was child of this node */
    private PlaceholderMap placeholderMap;
    
    /** a (hopefully) unique of for this node */
    private long id;
    
    /**
     * Creates a new SplitNode.
     * @param access the access to the owner of this node. Must not be <code>null</code>
     * @param id the unique id of this node, -1 indicates that the id must be created
     */
    protected SplitNode( SplitDockAccess access, long id ){
        if( access == null )
            throw new IllegalArgumentException( "Access must not be null" );
        this.access = access;
        if( id < 0 )
        	this.id = access.uniqueID();
        else
        	this.id = id;
    }
    
    /**
     * Gets the station this node belongs to.
     * @return the station
     */
    public SplitDockStation getStation(){
        return access.getOwner();
    }
    
	/**
	 * Gets all the keys that are stored in this placeholder
	 * @return all the keys
	 */
	public Path[] getPlaceholders(){
		if( placeholders == null )
			return new Path[]{};
		return placeholders.toArray( new Path[ placeholders.size() ] );
	}
	
	/**
	 * Stores an additional placeholder in this node.
	 * @param placeholder the additional placeholder
	 */
	public void addPlaceholder( Path placeholder ){
		if( placeholders == null ){
			placeholders = new ArrayList<Path>();
		}
		placeholders.add( placeholder );
	}
	
	/**
	 * Tells whether this node is associated with at least one placeholder.
	 * @return whether there is at least one placeholder
	 */
	public boolean hasPlaceholders(){
		return placeholders != null && !placeholders.isEmpty();
	}
	
	/**
	 * Tells whether this node contains <code>placeholder</code>.
	 * @param placeholder the placeholder to search
	 * @return <code>true</code> if <code>placeholder</code> was found
	 */
	public boolean hasPlaceholder( Path placeholder ){
		if( placeholder == null )
			return false;
		return placeholders.contains( placeholder );
	}
	
	/**
	 * Sets all the placeholders of this node
	 * @param placeholders all the placeholders, can be <code>null</code> or empty
	 */
	public void setPlaceholders( Path[] placeholders ){
		if( this.placeholders != null ){
			this.placeholders.clear();
		}
		if( placeholders != null ){
			for( Path placeholder : placeholders ){
				addPlaceholder( placeholder );
			}
		}
	}
	
	/**
	 * Removes a placeholder from this node.
	 * @param placeholder the placeholder to remove
	 * @return <code>true</code> if the placeholder was removed
	 */
	public boolean removePlaceholder( Path placeholder ){
		if( placeholders != null ){
			return placeholders.remove( placeholder );
		}
		return false;
	}
	
	/**
	 * Removes all placeholders in <code>placeholders</code> from this node
	 * @param placeholders the placeholders to remove
	 */
	public void removePlaceholders( Set<Path> placeholders ){
		if( this.placeholders != null ){
			this.placeholders.removeAll( placeholders );
		}
	}
	
	/**
	 * Sets information about the placeholders of a {@link DockStation} that was 
	 * child of this node.
	 * @param placeholderMap the placeholder information, can be <code>null</code>
	 */
	public void setPlaceholderMap( PlaceholderMap placeholderMap ){
		if( this.placeholderMap != null ){
			this.placeholderMap.setPlaceholderStrategy( null );
		}
		
		this.placeholderMap = placeholderMap;
		if( this.placeholderMap != null ){
			this.placeholderMap.setPlaceholderStrategy( getAccess().getOwner().getPlaceholderStrategy() );
		}
	}
	
	/**
	 * Gets placeholder information of a child {@link DockStation}.
	 * @return the placeholder information, can be <code>null</code>
	 */
	public PlaceholderMap getPlaceholderMap(){
		return placeholderMap;
	}
    
	/**
	 * Tells whether this node still has any use or can safely be removed from the tree
	 * @return <code>true</code> if this node has to remain in the tree, <code>false</code>
	 * otherwise
	 */
	public abstract boolean isOfUse();
	
	/**
	 * Replaces this node with <code>node</code>. Does nothing if this node has no parent.
	 * @param node the replacement, not <code>null</code>
	 */
	public void replace( SplitNode node ){
		if( node == null )
			throw new IllegalArgumentException( "node must not be null" );
		
		SplitNode parent = getParent();
		if( parent != null ){
			int location = parent.getChildLocation( this );
			parent.setChild( node, location );
		}
	}
	
    /**
     * Removes this node from its parent, if there is a parent. The subtree
     * remains intact and no {@link Dockable}s are removed from the station.
     * @param shrink whether this node should attempt to shrink the tree such
     * that no holes are left after this node was deleted
     */
    public void delete( boolean shrink ){
    	PlaceholderMap map = getPlaceholderMap();
    	if( map != null ){
    		map.setPlaceholderStrategy( null );
    	}
    	
        SplitNode parent = getParent();
        if( parent != null ){
            if( shrink ){
                if( parent instanceof Root ){
                    ((Root)parent).setChild( null );
                }
                else{
                	if( !parent.hasPlaceholders() ){
                		Node node = (Node)parent;
	                    SplitNode other = node.getLeft() == this ? node.getRight() : node.getLeft();
	                    
	                    parent = node.getParent();
	                    if( parent != null ){
	                        int location = parent.getChildLocation( node );
	                        parent.setChild( other, location );
	                    }
                    }
                }
            }
            else{
                int location = parent.getChildLocation( this );
                parent.setChild( null, location );
            }
        }
    }
    
    /**
     * Creates a new {@link Leaf}
     * @param id the unique identifier of the new leaf, can be -1
     * @return the new leaf
     */
    public Leaf createLeaf( long id ){
        return new Leaf( access, id );
    }
    
    /**
     * Creates a new {@link Node}.
     * @param id the unique identifier of the new node, can be -1
     * @return the new node
     */
    public Node createNode( long id ){
        return new Node( access, id );
    }
    
    /**
     * Creates a new {@link Placeholder}.
     * @param id the unique identifier of the new leaf, can be -1
     * @return the new leaf
     */
    public Placeholder createPlaceholder( long id ){
    	return new Placeholder( access, id );
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
     * Gets the (hopefully) unique id of this node.
     * @return the unique id
     */
    public long getId(){
		return id;
	}
    
    /**
     * Gets access to the owner-station
     * @return the access
     */
    protected SplitDockAccess getAccess(){
        return access;
    }
    
    /**
     * Tells whether this node (or one of this children) contains element that
     * are visible to the user.
     * @return <code>true</code> if this node or one of its children contains
     * a graphical element
     */
    public abstract boolean isVisible();
    
    /**
     * Gets the root of a subtree such that the root is visible and such that the
     * is the uppermost visible node.
     * @return the visible root, can be <code>null</code>, <code>this</code> or any
     * child of this node
     */
    public abstract SplitNode getVisible();
    
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
     * @param updateComponentBounds whether to update the bounds of {@link Component}s
     * that are in the tree. If set to <code>false</code>, then all updates stay within
     * the tree and the graphical user interface is not changed. That can be useful
     * if more than one round of updates is necessary. If in doubt, set this parameter
     * to <code>true</code>.
     */
    public void updateBounds( double x, double y, double width,  double height, double factorW, double factorH, boolean updateComponentBounds ){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    /**
     * Gets the root of the tree in which this node is
     * @return the root or <code>null</code>
     */
    public Root getRoot(){
        if( parent == null )
            return null;
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
     * Tells whether the coordinates x/y lie inside the override-zone of
     * the {@link SplitDockStation} or not.
     * @param x the x-coordinate of the mouse
     * @param y the y-coordinate of the mouse
     * @param factorW a factor to be multiplied with the relative 
     * {@link #getX() x} and {@link #getWidth() width} to get the 
     * size in pixel. 
     * @param factorH a factor to be multiplied with the relative
     * {@link #getY() y} and {@link #getHeight() height} to get the
     * size in pixel.
     * @return <code>true</code> if the station should not allow child-stations
     * to make a drop when the mouse is at x/y
     */
    public abstract boolean isInOverrideZone( int x, int y, double factorW, double factorH );
    
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
     * Gets the location of a child.
     * @param child a child of this node
     * @return the location of <code>child</code> or -1 if the child is unknown
     */
    public abstract int getChildLocation( SplitNode child );
    
    /**
     * Adds a child to this node at a given location.
     * @param child the new child
     * @param location the location of the child
     */
    public abstract void setChild( SplitNode child, int location );
    
    /**
     * Invokes one of the methods of the <code>visitor</code> for every
     * child in the subtree with this as root.
     * @param visitor the visitor
     */
    public abstract void visit( SplitNodeVisitor visitor );
    
    /**
     * Creates or replaces children according to the values found in 
     * <code>key</code>. Note that this method does not remove and {@link Dockable}s
     * from the station. They must be removed explicitly using {@link Leaf#setDockable(Dockable, boolean)}
     * @param key the key to read
     * @param checkValidity whether to ensure that all new {@link Dockable}s are
     * acceptable or not.
     */
    public abstract void evolve( SplitDockTree.Key key, boolean checkValidity );
    
    /**
     * If there are elements left in <code>property</code>, then the next node
     * is to be read and the <code>insert</code>-method of the matching child
     * to be called.<br>
     * If there are no children, then <code>dockable</code> has to be inserted
     * as new child.<br>
     * Otherwise this element is to be replaced by a node containing
     * <code>this</code> and the a leaf with <code>dockable</code>.<br>
     * Subclasses may wary this scheme in order to optimize or to find a better
     * place for the <code>dockable</code>. 
     * @param property a list of nodes
     * @param depth the index of the node that corresponds to this
     * @param dockable the element to insert
     * @return <code>true</code> if the element was inserted, <code>false</code>
     * otherwise
     */
    public abstract boolean insert( SplitDockPathProperty property, int depth, Dockable dockable );
    
    /**
     * Recursively searches for a node or leaf that uses the placeholder specified by
     * <code>property</code> and inserts the <code>dockable</code> there. Also removes
     * the placeholder from this node.
     * @param property the placeholder to search
     * @param dockable the new element
     * @return <code>true</code> if the element was inserted, <code>false</code>
     * otherwise
     */
    public abstract boolean insert( SplitDockPlaceholderProperty property, Dockable dockable );
    
    /**
     * Writes the contents of this node into a new tree create by <code>factory</code>.
     * @param <N> the type of element the <code>factory</code> will create
     * @param factory the factory transforming the elements of the tree into a
     * new form.
     * @return the representation of this node
     */
    public abstract <N> N submit( SplitTreeFactory<N> factory );

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        toString( 0, builder );
        return builder.toString();
    }
    
    /**
     * Writes some contents of this node into <code>out</code>.
     * @param tabs the number of tabs that should be added before the text if
     * a new line is necessary.
     * @param out the container to write into
     */
    public abstract void toString( int tabs, StringBuilder out );
    

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
     * Gets the size and location of this node in pixel where the point
     * 0/0 is equal to the point 0/0 on the owner-station. This method calculates
     * these values anew, clients interested in the current bounds should
     * use {@link VisibleSplitNode#getCurrentBounds()}.
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
        
        JComponent base = getAccess().getOwner().getBasePane();
        
        Insets insets = base.getInsets();
        
        int x = 0;
        int y = 0;
        int width = base.getWidth();
        int height = base.getHeight();
        if( insets != null ){
            x = insets.left;
            y = insets.top;
            width -= insets.left + insets.right;
            height -= insets.top + insets.bottom;
        }
        
        rec.x = Math.min( width, Math.max( x, rec.x ));
        rec.y = Math.min( height, Math.max( y, rec.y ));
        
        rec.width  = Math.min( width - rec.x + x, Math.max( 0, rec.width ));
        rec.height = Math.min( height - rec.y + y, Math.max( 0, rec.height ));
        
        return rec;
    }
    
    /**
     * Creates a leaf for <code>dockable</code>. Does not inform any
     * {@link DockStationListener} about the change.
     * @param dockable the element to put into a leaf
     * @param fire whether to inform any {@link DockStationListener}s about
     * the new element
     * @param id the unique identifier of the new leaf, can be -1
     * @return the new leaf or <code>null</code> if the leaf would not be valid
     */
    protected Leaf create( Dockable dockable, boolean fire, long id ){
        SplitDockStation split = access.getOwner();
        DockController controller = split.getController();
        DockAcceptance acceptance = controller == null ? null : controller.getAcceptance();
        
        if( !dockable.accept( split ) || !split.accept( dockable ))
            return null;
        
        if( acceptance != null ){
            if( !acceptance.accept( split, dockable ))
                return null;
        }
        
        Leaf leaf = createLeaf( id );
        leaf.setDockable( dockable, fire );
        return leaf;
    }
    
    /**
     * Creates a new node using the contents of <code>key</code>.
     * @param key the key to read
     * @param checkValidity whether to ensure that all new {@link Dockable}s
     * are acceptable or not.
     * @return the new node
     */
    protected SplitNode create( SplitDockTree.Key key, boolean checkValidity ){
    	SplitDockTree tree = key.getTree();
    	
    	if( tree.isDockable( key )){
    		Dockable[] dockables = tree.getDockables( key );
    		if( dockables == null || dockables.length == 0 ){
    			Path[] placeholders = tree.getPlaceholders( key );
    			Placeholder leaf = createPlaceholder( key.getNodeId() );
    			leaf.setPlaceholders( placeholders );
    			return leaf;
    		}
    		else{
    			SplitDockStation split = access.getOwner();
    			DockController controller = split.getController();
    			DockAcceptance acceptance = controller == null ? null : controller.getAcceptance();
    			Leaf leaf;
    			boolean removePlaceholderMap = false;
    			
    			if( dockables.length == 1 ){
    				if( checkValidity ){
    					if( !dockables[0].accept( split ) || 
    							!split.accept( dockables[0] ))
    						throw new SplitDropTreeException( split, "No acceptance for " + dockables[0] );

    					if( acceptance != null ){
    						if( !acceptance.accept( split, dockables[0] ))
    							throw new SplitDropTreeException( split, "DockAcceptance does not allow child " + dockables[0] );
    					}
    				}

    				leaf = createLeaf( key.getNodeId() );
    				leaf.setDockable( dockables[0], true );
    			}
    			else{
    				if( checkValidity ){
    					if( !dockables[0].accept( split, dockables[1] ) ||
    							!dockables[1].accept( split, dockables[1] ))
    						throw new SplitDropTreeException( split, 
    								"No acceptance for combination of " + dockables[0] + " and " + dockables[1] );

    					if( acceptance != null ){
    						if( !acceptance.accept( split, dockables[0], dockables[1] ))
    							throw new SplitDropTreeException( split,
    									"DockAcceptance does not allow to combine " + dockables[0] + " and " + dockables[1] );
    					}
    				}

    				Dockable combination = access.getOwner().getCombiner().combine( dockables[0], dockables[1], access.getOwner(), key.getTree().getPlaceholderMap( key ) );
    				removePlaceholderMap = true;
    				if( dockables.length == 2 ){
    					leaf = createLeaf( key.getNodeId() );
    					leaf.setDockable( combination, true );

    					DockStation station = combination.asDockStation();
    					if( station != null ){
    						Dockable selected = key.getTree().getSelected( key );
    						if( selected != null )
    							station.setFrontDockable( selected );
    					}
    				}
    				else{
    					DockStation station = combination.asDockStation();
    					if( station == null )
    						throw new SplitDropTreeException( access.getOwner(), "Combination of two Dockables does not create a new station" );

    					leaf = createLeaf( key.getNodeId() );
    					leaf.setDockable( combination, true );

    					for( int i = 2; i < dockables.length; i++ ){
    						Dockable dockable = dockables[ i ];
    						if( checkValidity ){
    							if( !dockable.accept( station ) || !station.accept( dockable ))
    								throw new SplitDropTreeException( access.getOwner(), "No acceptance of " + dockable + " and " + station );

    							if( acceptance != null ){
    								if( !acceptance.accept( station, dockable ))
    									throw new SplitDropTreeException( split,
    											"DockAcceptance does not allow " + dockable + " as child of " + station );
    							}
    						}

    						station.drop( dockable );
    					}

    					Dockable selected = key.getTree().getSelected( key );
    					if( selected != null )
    						station.setFrontDockable( selected );
    				}
    			}
    			
    			leaf.evolve( key, checkValidity );
    			if( removePlaceholderMap ){
    				leaf.setPlaceholderMap( null );
    			}
    			return leaf;
    		}
    	}
    	else{
    		Node node = createNode( key.getNodeId() );
    		node.evolve( key, checkValidity );
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