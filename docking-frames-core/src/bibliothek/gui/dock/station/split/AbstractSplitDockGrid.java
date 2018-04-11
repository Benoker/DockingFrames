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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;


/**
 * A class that provides a grid for representations of {@link Dockable Dockables}. The grid can
 * be transformed into a {@link SplitDockTree} which has values that would
 * layout the components as they are in the grid. The algorithms used in this
 * class can handle overlapping elements and holes, however results are much better
 * if there are no disturbances in the grid.<br>
 * There is also a possibility to tell the tree, where dividers should be made. 
 * @author Benjamin Sigg
 * @param <D> the kind of object that represents a {@link Dockable}
 * @see SplitDockStation#dropTree(SplitDockTree)
 */
public abstract class AbstractSplitDockGrid<D> {
	/** The Dockables known to this grid */
	private List<Node<D>> nodes = new ArrayList<Node<D>>();
	/** The dividing lines which should appear */
	private List<Line> lines = new ArrayList<Line>();
	
	/** Whether to {@link #unpack(double, double, double, double)} all {@link Node}s before adding new {@link Dockable}s */
	private boolean unpack = true;
	
    /**
     * Creates a new, empty grid.
     */
    public AbstractSplitDockGrid(){
        // do nothing
    }
    
    /**
     * Whether to automatically call {@link #unpack(double, double, double, double)} before adding any new {@link Dockable}s
     * to this grid. Default: true.
     * @param unpack whether to unpack automatically
     */
    public void setUnpack( boolean unpack ){
		this.unpack = unpack;
	}
    
    /**
     * Tells whether {@link #unpack(double, double, double, double)} is called automatically before adding new {@link Dockable}s
     * to this grid.
     * @return whether {@link #unpack(double, double, double, double)} is called
     */
    public boolean isUnpack(){
		return unpack;
	}
    
    /**
     * Creates a grid by reading a string which represents a grid.<br>
     * The argument <code>layout</code> is a string divided by newline
     * <code>"\n"</code>. Every line represents a y-coordinate, the position
     * of a character in a line represents a x-coordinate. The minimal and 
     * the maximal x- and y-coordinates for a character is searched, and
     * used to call {@link #addDockable(double, double, double, double, Object...) addDockable},
     * where the <code>Dockable</code>-array is taken from the {@link Map} 
     * <code>dockables</code>.
     * @param layout the layout, a string divided by newlines
     * @param dockables the Dockables to add, only entries whose character is
     * in the String <code>layout</code>.
     */
    public AbstractSplitDockGrid( String layout, Map<Character, D[]> dockables ){
        String[] lines = layout.split( "\n" );
        Set<Character> chars = new HashSet<Character>();
        
        for( int i = 0, n = layout.length(); i<n; i++ )
            chars.add( layout.charAt( i ));
                
        for( Character c : chars ){
            D[] list = dockables.get( c );
            if( list != null ){
                int minx = Integer.MAX_VALUE;
                int miny = Integer.MAX_VALUE;
                int maxx = Integer.MIN_VALUE;
                int maxy = Integer.MIN_VALUE;
            
                for( int y = 0; y < lines.length; y++ ){
                    for( int x = 0, n = lines[y].length(); x<n; x++ ){
                        if( lines[y].charAt( x ) == c.charValue() ){
                            minx = Math.min( minx, x );
                            maxx = Math.max( maxx, x );
                            miny = Math.min( miny, y );
                            maxy = Math.max( maxy, y );
                        }
                    }
                }
                
                addDockable( minx, miny, maxx-minx, maxy-miny, list );
            }
        }
    }
    
    /**
     * Creates a D-array of length <code>size</code>.
     * @param size the size of the new array
     * @return the new array
     */
    protected abstract D[] array( int size );
    
    /**
     * Unpacks any existing {@link DockStation} at location <code>x,y,width,height</code>. All children
     * of all {@link DockStation}s are removed and re-added as if {@link #addDockable(double, double, double, double, Object...)}
     * would have been called multiple times.
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param width the width, more than 0
	 * @param height the height, more than 0
     */
    public void unpack( double x, double y, double width, double height ){
    	Node<D> node = nodeAt( x, y, width, height );
    	if( node != null && node.dockables != null ){
    		List<D> copy = new ArrayList<D>();
    		for( D dockable : node.dockables ){
    			for( D unpacked : unpack( dockable )){
    				copy.add( unpacked );
    			}
    		}
    		D[] unpacked = copy.toArray( array( copy.size() ));
    		node.dockables = unpacked;
    	}
    }
    
    /**
     * Unpacks <code>dockable</code>. Unpacking means converting <code>dockable</code> in something like a {@link DockStation}
     * and returning all the children {@link Dockable}s.
     * @param dockable the dockable to unpack
     * @return either <code>dockable</code> or all its children
     */
    protected abstract D[] unpack( D dockable );
    
    /**
     * Gets all the dockables that were {@link #addDockable(double, double, double, double, Object...) added}
     * to this grid at location <code>x,y,width,height</code>
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param width the width, more than 0
	 * @param height the height, more than 0
     * @return the dockables, <code>null</code> if there are no dockables at this location
     */
    public D[] getDockables( double x, double y, double width, double height ){
    	Node<D> node = nodeAt( x, y, width, height );
    	if( node == null ){
    		return null;
    	}
    	D[] copy = array( node.dockables.length );
    	System.arraycopy( node.dockables, 0, copy, 0, copy.length );
    	return copy;
    }
    
	/**
	 * Adds <code>dockable</code> to the grid. The coordinates are not absolute,
	 * only the relative location and size matters. If there are already 
	 * <code>dockables</code> at the exact same location, then 
	 * the <code>dockables</code> are stacked.
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param width the width, more than 0
	 * @param height the height, more than 0
     * @param dockables the <code>Dockable</code>s to add
	 */
	public void addDockable( double x, double y, double width, double height, D... dockables ){
		if( dockables == null )
			throw new IllegalArgumentException( "Dockable must not be null" );
		
        if( dockables.length == 0 )
            throw new IllegalArgumentException( "Dockables must at least have one element" );
        
        for( D dockable : dockables )
            if( dockable == null )
                throw new IllegalArgumentException( "Entry of dockables-array is null" );
        
        if( width < 0 )
            throw new IllegalArgumentException( "width < 0" );
        
        if( height < 0 )
            throw new IllegalArgumentException( "height < 0" );
        
        if( isUnpack() ){
        	unpack( x, y, width, height );
        }
        
        Node<D> node = nodeAt( x, y, width, height );
        int insert = 0;
        
        if( node.dockables == null ){
        	node.dockables = array( dockables.length );
        }
        else{
        	D[] oldDockables = node.dockables;
            insert = oldDockables.length;
            node.dockables = array( oldDockables.length + dockables.length );
            System.arraycopy( oldDockables, 0, node.dockables, 0, oldDockables.length ); 
        }
        
        System.arraycopy( dockables, 0, node.dockables, insert, dockables.length );
	}
	
	/**
	 * Adds <code>placeholders</code> to the grid. The coordinates are not absolute,
	 * only the relative location and size matters. If there are already items at the exact same location, then
	 * the new placeholders are just added to them.
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param width the width, more than 0
	 * @param height the height, more than 0
	 * @param placeholders the new placeholders to add
	 */
	public void addPlaceholders( double x, double y, double width, double height, Path... placeholders ){
		if( placeholders == null )
			throw new IllegalArgumentException( "Placeholders must not be null" );
		
        if( placeholders.length == 0 )
            throw new IllegalArgumentException( "Placeholders must at least have one element" );
        
        for( Path placeholder : placeholders )
            if( placeholder == null )
                throw new IllegalArgumentException( "Entry of placeholders-array is null" );
        
        if( width < 0 )
            throw new IllegalArgumentException( "width < 0" );
        
        if( height < 0 )
            throw new IllegalArgumentException( "height < 0" );
        
        if( isUnpack() ){
        	unpack( x, y, width, height );
        }
        
        Node<D> node = nodeAt( x, y, width, height );
        int insert = 0;
        
        if( node.placeholders == null ){
        	node.placeholders = new Path[placeholders.length];
        }
        else{
        	Path[] oldPlaceholders = node.placeholders;
            insert = oldPlaceholders.length;
            node.placeholders = new Path[ oldPlaceholders.length + placeholders.length ];
            System.arraycopy( oldPlaceholders, 0, node.placeholders, 0, oldPlaceholders.length ); 
        }
        
        System.arraycopy( placeholders, 0, node.placeholders, insert, placeholders.length );
	}
	
	/**
	 * Sets the {@link PlaceholderMap} <code>map</code> for the items at the given location. The map may be used
	 * if a {@link DockStation} is creating during runtime at this location.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width of the elements
	 * @param height the height of the elements
	 * @param map the map, can be <code>null</code>
	 * @throws IllegalArgumentException if there is no node at <code>x/y/width/height</code>
	 */
	public void setPlaceholderMap( double x, double y, double width, double height, PlaceholderMap map ){
		for( Node<D> node : nodes ){
            if( node.x == x && node.y == y && node.width == width && node.height == height ){
                node.placeholderMap = map;
                return;
            }
        } 
        
        throw new IllegalArgumentException( "there are no dockables registered with the given coordinates" );
	}
	
	private Node<D> nodeAt( double x, double y, double width, double height ){
		for( Node<D> existingNode : nodes ){
            if( existingNode.x == x && existingNode.y == y &&  existingNode.width == width && existingNode.height == height){
                return existingNode;
            }
        } 
        
        Node<D> node = new Node<D>();
        node.x = x;
        node.y = y;
        node.width = width;
        node.height = height;
        nodes.add( node );
		return node;
	}
	
	/**
	 * Marks <code>dockable</code> as selected in the stack of elements that
	 * are on position <code>x, y, width, height</code>. This method requires
	 * that {@link #addDockable(double, double, double, double, Object...) add}
	 * was called with the exact same coordinates and with <code>dockable</code>.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width of the elements
	 * @param height the height of the elements
	 * @param dockable the element to select, not <code>null</code>
	 * @throws IllegalArgumentException if <code>width</code> or <code>height</code>
	 * are below 0, if <code>dockable</code> is <code>null</code>, if 
	 * {@link #addDockable(double, double, double, double, Object...) add}
	 * was never called with the arguments
	 */
	public void setSelected( double x, double y, double width, double height, D dockable ){
		if( dockable == null )
			throw new IllegalArgumentException( "dockable is null" );
		
        if( width < 0 )
            throw new IllegalArgumentException( "width < 0" );
        
        if( height < 0 )
            throw new IllegalArgumentException( "height < 0" );
        
        if( isUnpack() ){
        	unpack( x, y, width, height );
        }

        for( Node<D> node : nodes ){
            if( node.x == x && 
                    node.y == y && 
                    node.width == width && 
                    node.height == height){
                
            	for( D check : node.dockables ){
            		if( check == dockable ){
            			node.selected = dockable;
            			return;
            		}
            	}
            	
            	throw new IllegalArgumentException( "dockable is not in the described stack" );
            }
        } 
        
        throw new IllegalArgumentException( "there are no dockables registered with the given coordinates" );
	}
	
	/**
	 * Adds a vertical dividing line.
	 * @param x the x-coordinate of the line
	 * @param y1 the y-coordinate of the first endpoint
	 * @param y2 the y-coordinate of the second endpoint
	 */
	public void addVerticalDivider( double x, double y1, double y2 ){
		Line line = new Line();
		line.horizontal = false;
		line.alpha = x;
		line.betaMin = Math.min( y1, y2 );
		line.betaMax = Math.max( y1, y2 );
		lines.add( line );
	}
	
	/**
	 * Adds a horizonal dividing line.
	 * @param x1 the x-coordinate of the first endpoint
	 * @param x2 the x-coordinate of the second endpoint
	 * @param y the y-coordinate of the line
	 */
	public void addHorizontalDivider( double x1, double x2, double y ){
		Line line = new Line();
		line.horizontal = true;
		line.alpha = y;
		line.betaMin = Math.min( x1, x2 );
		line.betaMax = Math.max( x1, x2 );
		lines.add( line );
	}
	
	/**
	 * Fills the contents of this grid into <code>tree</code>.
	 * @param tree the tree to fill
	 */
	protected void fillTree( SplitDockTree<D> tree ){
		Node<D> root = tree();
		if( root != null ){
			SplitDockTree<D>.Key key = root.put( tree );
			tree.root( key );
		}	
	}
	
	/**
	 * Gets a list containing all lines of this grid.
	 * @return the list
	 */
	protected List<Line> getLines(){
		return lines;
	}
	
	/**
	 * Gets a list containing all nodes of this grid.
	 * @return the nodes
	 */
	protected List<Node<D>> getNodes(){
		return nodes;
	}
	
	/**
	 * Gets all the nodes of this grid. Each node is a set of <code>D</code>s 
	 * and their location.
	 * @return the nodes, the list is not modifiable
	 */
	public List<GridNode<D>> getGridNodes(){
		return Collections.<GridNode<D>>unmodifiableList( nodes );
	}

	/**
	 * Gets the one node containing <code>dockable</code>. This method checks for 
	 * equality using the <code>==</code> operation.
	 * @param dockable the item to search
	 * @return the node or <code>null</code> if not found
	 */
	protected Node<D> getNode( D dockable ){
		for( Node<D> node : nodes ){
			if( node.dockables != null ){
				for( D item : node.dockables ){
					if( item == dockable ){
						return node;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Transforms the grid into a tree and returns the root.
	 * @return the root, can be <code>null</code>
	 */
	protected Node<D> tree(){
		List<Node<D>> nodes = new ArrayList<Node<D>>( this.nodes );
		
		if( nodes.isEmpty() )
			return null;
		
		while( nodes.size() > 1 ){
			int size = nodes.size();
			
			int bestA = 0, bestB = 0;
			double bestDiff = Double.MAX_VALUE;
			
			for( int i = 0; i < size; i++ ){
				for( int j = i+1; j < size; j++ ){
					double diff = diff( nodes.get( i ), nodes.get( j ) );
					if( diff < bestDiff ){
						bestDiff = diff;
						bestA = i;
						bestB = j;
					}
				}
			}
			
			Node<D> node = combine( nodes.remove( bestB ), nodes.remove( bestA ));
			nodes.add( node );
		}
		
		return nodes.get( 0 );
	}
	
	/**
	 * Creates a combination of <code>a</code> and <code>b</code>.
	 * @param a the first node
	 * @param b the second node
	 * @return a node which has <code>a</code> and <code>b</code> as children
	 */
	protected Node<D> combine( Node<D> a, Node<D> b ){
		double x = Math.min( a.x, b.x );
		double y = Math.min( a.y, b.y );
		double w = Math.max( a.x + a.width, b.x + b.width ) - x;
		double h = Math.max( a.y + a.height, b.y + b.height ) - y;
		
		double max = a.x + a.width/2;
		double may = a.y + a.height/2;
		double mbx = b.x + b.width/2;
		double mby = b.y + b.height/2;
		
		double dmx = (max - mbx) * h;
		double dmy = (may - mby) * w;
		
		Node<D> node = new Node<D>();
		
	
		if( Math.abs( dmx ) > Math.abs( dmy )){
			node.horizontal = true;
			if( dmx > 0 ){
				node.childA = b;
				node.childB = a;
			}
			else{
				node.childA = a;
				node.childB = b;
			}
			
			double split = ((node.childA.x + node.childA.width + node.childB.x) / 2.0 - x ) / w;
			Line line = bestFittingLine( x, y, w, h, false, split );
			if( line == null )
				node.divider = split;
			else
				node.divider = (line.alpha - x) / w;
		}
		else{
			node.horizontal = false;
			if( dmy > 0 ){
				node.childA = b;
				node.childB = a;
			}
			else{
				node.childA = a;
				node.childB = b;
			}
			
			double split = ((node.childA.y + node.childA.height + node.childB.y ) / 2.0 - y ) / h;
			Line line = bestFittingLine( x, y, w, h, true, split);
			if( line == null )
				node.divider = split;
			else
				node.divider = (line.alpha - y) / h;
		}
		
		node.x = x;
		node.y = y;
		node.width = w;
		node.height = h;
		return node;
	}
	
	/**
	 * Tells whether the two nodes could be merged or not.
	 * @param a the first node
	 * @param b the second node
	 * @return how likely the two nodes can be merged, a small result indicates
	 * that merging would be a good idea.
	 */
	protected double diff( Node<D> a, Node<D> b ){
		double x = Math.min( a.x, b.x );
		double y = Math.min( a.y, b.y );
		double w = Math.max( a.x + a.width, b.x + b.width ) - x;
		double h = Math.max( a.y + a.height, b.y + b.height ) - y;
		
		double sizeA = a.width * a.height;
		double sizeB = b.width * b.height;
		double size = w * h;
		
		double diff = (size - sizeA - sizeB) / size;
		
		for( Line line : lines ){
			diff += penalty( x, y, w, h, line );
		}
		
		return diff;
	}
	
	/**
	 * Searches the line that divides the rectangle <code>x, y, width, height</code>
	 * best.
	 * @param x the x-coordinate of the rectangle
	 * @param y the y-coordinate of the rectangle
	 * @param w the width of the rectangle
	 * @param h the height of the rectangle
	 * @param horizontal whether the line should be horizontal or not
	 * @param split the preferred value of {@link Line#alpha}.
	 * @return a line or <code>null</code>
	 */
	protected Line bestFittingLine( double x, double y, double w, double h, boolean horizontal, double split ){
		Line bestLine = null;
		double best = Double.MAX_VALUE;
		
		for( Line line : lines ){
			if( line.horizontal != horizontal )
				continue;
			
			double max, min, diff, penalty;
			
			if( line.horizontal ){
				if( y > line.alpha || y + h < line.alpha )
					continue;
				
				if( x + w < line.betaMin || x > line.betaMax )
					continue;
				
				min = Math.min( x, line.betaMin );
				max = Math.max( x+w, line.betaMax );
				diff = max - min - Math.min( line.betaMax - line.betaMin, w );
				penalty = diff / (max - min);
				penalty *= (1+Math.abs( split - line.alpha )/h);
			}
			else{
				if( x > line.alpha || x + w < line.alpha )
					continue;
				
				if( y + h < line.betaMin || y > line.betaMax )
					continue;
				
				min = Math.min( y, line.betaMin );
				max = Math.max( y+h, line.betaMax );
				diff = max - min - Math.min( line.betaMax - line.betaMin, h );
				penalty = diff / (max - min);
				penalty *= (1+Math.abs( split - line.alpha )/w);
			}
			
			if( penalty < 0.25 && penalty < best ){
				best = penalty;
				bestLine = line;
			}
		}
		
		return bestLine;
	}
	
	/**
	 * Used by {@link #diff(Node, Node) diff}
	 * to add a penalty if a line hits a rectangle. 
	 * @param x the x-coordinate of the rectangle
	 * @param y the y-coordinate of the rectangle
	 * @param w the width of the rectangle
	 * @param h the height of the rectangle
	 * @param line the line which may hit the rectangle
	 * @return the penalty, a value that will be added to the result of <code>diff</code>.
	 */
	protected double penalty( double x, double y, double w, double h, Line line ){
		double max, min, diff;
		
		if( line.horizontal ){
			if( y >= line.alpha || y + h <= line.alpha )
				return 0;
			
			if( x + w <= line.betaMin || x >= line.betaMax )
				return 0;
			
			min = Math.min( x, line.betaMin );
			max = Math.max( x+w, line.betaMax );
			diff = max - min - Math.min( line.betaMax - line.betaMin, w );
		}
		else{
			if( x >= line.alpha || x + w <= line.alpha )
				return 0;
			
			if( y + h <= line.betaMin || y >= line.betaMax )
				return 0;
			
			min = Math.min( y, line.betaMin );
			max = Math.max( y+h, line.betaMax );
			diff = max - min - Math.min( line.betaMax - line.betaMin, h );
		}
		
		return diff / (max - min);
	}
	
	/**
	 * Represents a dividing line in the grid.
	 * @author Benjamin Sigg
	 */
	protected static class Line{
		/** whether this line is horizontal or not */
		public boolean horizontal;
		/** the coordinate which is always the same on the line */
		public double alpha;
		/** the end with the smaller coordinate */
		public double betaMin;
		/** the end with the higher coordinate */
		public double betaMax;
	}
	
	/**
	 * Represents a node in the tree which will be built.
	 * @param <D> the kind of element that represents a {@link Dockable}
	 * @author Benjamin Sigg
	 */
	protected static class Node<D> implements GridNode<D>{
		/** the x-coordinate */
		public double x;
		/** the y-coordinate */
		public double y;
		/** the width of this rectangle */
		public double width;
		/** the height of this rectangle */
		public double height;
		/** the first child of this node */
		public Node<D> childA;
		/** the second child of this node */
		public Node<D> childB;
		/** the location of the divider */
		public double divider;
		/** whether the children of this node are laid out horizontally or not */
		public boolean horizontal;
		/** the elements represented by this leaf */
		public D[] dockables;
		/** the element that is selected */
		public D selected;
		/** all the placeholders associated with this location */
		public Path[] placeholders;
		/** a map containing placeholder information for a {@link DockStation} that could be placed
		 * as this location. */
		public PlaceholderMap placeholderMap;
		
		/**
		 * Writes the contents of this node into <code>tree</code>.
		 * @param tree the tree to write into
		 * @return the key of the node
		 */
		public SplitDockTree<D>.Key put( SplitDockTree<D> tree ){
			if( dockables != null || childA == null || childB == null ){
				return tree.put( dockables, selected, placeholders, placeholderMap, -1 );
			}
			else if( horizontal ){
				return tree.horizontal( childA.put( tree ), childB.put( tree ), divider, placeholders, placeholderMap, -1 );
			}
			else{
				return tree.vertical( childA.put( tree ), childB.put( tree ), divider, placeholders, placeholderMap, -1 );
			}
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public double getWidth() {
			return width;
		}

		public double getHeight() {
			return height;
		}

		public List<D> getDockables() {
			if( dockables == null ){
				return Collections.emptyList();
			}
			else{
				return Collections.unmodifiableList( Arrays.asList( dockables ) );
			}
		}

		public D getSelected() {
			return selected;
		}

		public List<Path> getPlaceholders() {
			if( placeholders == null ){
				return Collections.emptyList();
			}
			else{
				return Collections.unmodifiableList( Arrays.asList( placeholders ) );
			}
		}
	}
}
