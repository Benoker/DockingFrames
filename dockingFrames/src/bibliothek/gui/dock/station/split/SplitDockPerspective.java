/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
import java.util.List;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.perspective.Perspective;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.station.stack.StackDockPerspective;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * Represents a {@link SplitDockStation} in a {@link Perspective}.
 * @author Benjamin Sigg
 */
public class SplitDockPerspective implements PerspectiveDockable, PerspectiveStation{
	private PerspectiveStation parent;

	/** the child that is currently in fullscreen mode */
	private PerspectiveDockable fullscreen;
	
	/** the root of the tree that represents the layout */
	private Entry root;
	
	/** all the children of this station */
	private List<PerspectiveDockable> children = new ArrayList<PerspectiveDockable>();
	
	/**
	 * Reads the contents of <code>tree</code> and replaces any content of this perspective
	 * @param tree the tree that represents this perspective
	 * @param fullscreen the one child that is currently in fullscreen-mode, can be <code>null</code>
	 */
	public void read( PerspectiveSplitDockTree tree, PerspectiveDockable fullscreen ){
		root = convert( tree.getRoot() );
		if( fullscreen != null && !children.contains( fullscreen )){
			throw new IllegalArgumentException( "fullscreen is not a child of this station" );
		}
		this.fullscreen = fullscreen;
	}
	
	private Entry convert( PerspectiveSplitDockTree.Key key ){
		SplitDockTree<PerspectiveDockable> tree = key.getTree();
		if( tree.isDockable( key )){
			PerspectiveDockable[] dockables = tree.getDockables( key );
			PerspectiveDockable dockable = null;
			
			if( dockables.length == 1 ){
				dockable = dockables[0];
			}
			else if( dockables.length > 1 ){
				dockable = new StackDockPerspective( dockables, tree.getSelected( key ) );
			}
			
			if( dockable.getParent() != null ){
				throw new IllegalArgumentException( "dockable already has a parent" );
			}
			dockable.setParent( this );
			children.add( dockable );
			
			return new Leaf( dockable, tree.getPlaceholders( key ), tree.getPlaceholderMap( key ), key.getNodeId() );
		}
		if( tree.isPlaceholder( key )){
			return new Leaf( null, tree.getPlaceholders( key ), tree.getPlaceholderMap( key ), key.getNodeId() );
		}
		if( tree.isNode( key )){
			Entry childA = convert( tree.getLeft( key ));
			Entry childB = convert( tree.getRight( key ));
			Orientation orientation;
			if( tree.isHorizontal( key )){
				orientation = Orientation.HORIZONTAL;
			}
			else{
				orientation = Orientation.VERTICAL;
			}
			
			return new Node( orientation, tree.getDivider( key ), childA, childB, tree.getPlaceholders( key ), tree.getPlaceholderMap( key ), key.getNodeId() );
		}
		throw new IllegalStateException( "key does not represent any known kind of element" );
	}
	
	/**
	 * Gets the element which is in fullscreen-mode
	 * @return the maximized element, can be <code>null</code>
	 */
	public PerspectiveDockable getFullScreen(){
		return fullscreen;
	}
	
	/**
	 * Gets the root of the tree that is the layout of this station.
	 * @return the root of the tree, not <code>null</code>
	 */
	public Entry getRoot(){
		return root;
	}
	
	public PerspectiveStation getParent(){
		return parent;
	}

	@Todo
	public Path getPlaceholder(){
		return null;
	}

	public void setParent( PerspectiveStation parent ){
		this.parent = parent;	
	}

	public PerspectiveDockable asDockable(){
		return this;
	}

	public PerspectiveStation asStation(){
		return this;
	}

	public String getFactoryID(){
		return SplitDockStationFactory.ID;
	}

	public PerspectiveDockable getDockable( int index ){
		return children.get( index );
	}

	public int getDockableCount(){
		return children.size();
	}
	
	@Todo( compatibility=Compatibility.COMPATIBLE, priority=Priority.ENHANCEMENT, target=Version.VERSION_1_1_0,
			description="implementation pending")
	public void setPlaceholders( PlaceholderMap placeholders ){
		// ignore, SplitDockStation does not support placeholder maps
	}
	
	@Todo( compatibility=Compatibility.COMPATIBLE, priority=Priority.ENHANCEMENT, target=Version.VERSION_1_1_0,
			description="implementation pending")
	public PlaceholderMap getPlaceholders(){
		return null;
	}

    /**
     * An entry in a tree, either a node or a leaf.
     * @author Benjamin Sigg
     */
    public static abstract class Entry{
    	/** the parent element of this entry */
    	private Node parent;
    	/** the unique id of this node */
    	private long id;
    	/** placeholders that are associated with this entry */
    	private Path[] placeholders;
    	/** placeholder information of a child {@link DockStation} */
    	private PlaceholderMap placeholderMap;
    	
    	/**
    	 * Create a new entry
    	 * @param placeholders the placeholders associated with this node or leaf
    	 * @param placeholderMap placeholder information of a child {@link DockStation}
    	 * @param id the unique id of this node or -1
    	 */
    	public Entry( Path[] placeholders, PlaceholderMap placeholderMap, long id ){
    		this.placeholders = placeholders;
    		this.placeholderMap = placeholderMap;
    		this.id = id;
    	}
    	
    	/**
    	 * Sets the parent of this entry.
    	 * @param parent the parent
    	 */
    	protected void setParent( Node parent ){
			this.parent = parent;
		}
    	
    	/**
    	 * Gets the parent of this entry, is <code>null</code> for the
    	 * root entry.
    	 * @return the parent.
    	 */
    	public Node getParent() {
			return parent;
		}
    	
    	/**
    	 * Gets the unique id of this node.
    	 * @return the unique id or -1
    	 */
    	public long getNodeId(){
			return id;
		}
    	
        /**
         * Returns <code>this</code> as leaf or <code>null</code>.
         * @return <code>this</code> or <code>null</code>
         */
        public Leaf asLeaf(){
            return null;
        }
        
        /**
         * Returns <code>this</code> as node or <code>null</code>.
         * @return <code>this</code> or <code>null</code>
         */
        public Node asNode(){
            return null;
        }
        
        /**
         * Gets all the placeholders that are associated with this entry.
         * @return the placeholders
         */
        public Path[] getPlaceholders(){
			return placeholders;
		}
        
        /**
         * Gets the placeholder information of a potential child {@link DockStation}.
         * @return the placeholder map, can be <code>null</code>
         */
        public PlaceholderMap getPlaceholderMap(){
			return placeholderMap;
		}
    }
    
    /**
     * A leaf in a tree, describes one {@link Dockable}.
     * @author Benjamin Sigg
     */
    public static class Leaf extends Entry{
        /** the element represented by this leaf */
        private PerspectiveDockable dockable;
        
        /**
         * Creates a new leaf
         * @param dockable the element that is represented by this leaf
         * @param placeholders placeholders associated with this leaf
         * @param placeholderMap placeholder information of a child {@link DockStation}
         * @param nodeId the unique identifier of this node, can be -1
         */
        public Leaf( PerspectiveDockable dockable, Path[] placeholders, PlaceholderMap placeholderMap, long nodeId ){
        	super( placeholders, placeholderMap, nodeId );
            this.dockable = dockable;
        }
        
        @Override
        public Leaf asLeaf() {
            return this;
        }
        
        /**
         * Gets the element which is represented by this leaf.
         * @return the element
         */
        public PerspectiveDockable getDockable(){
			return dockable;
		}
    }
    
    /**
     * A node in a tree.
     * @author Benjamin Sigg
     */
    public static class Node extends Entry{
        /** whether the node is horizontal or vertical */
        private Orientation orientation;
        /** the location of the divider */
        private double divider;
        /** the top or left child */
        private Entry childA;
        /** the bottom or right child */
        private Entry childB;
        
        /**
         * Creates a new node.
         * @param orientation whether this node is horizontal or vertical
         * @param divider the location of the divider
         * @param childA the left or top child
         * @param childB the right or bottom child
         * @param placeholders placeholders associated with this node
         * @param placeholderMap placeholder information of a child {@link DockStation}
         * @param id the unique identifier of this node or -1
         */
        public Node( Orientation orientation, double divider, Entry childA, Entry childB, Path[] placeholders, PlaceholderMap placeholderMap, long id ){
        	super( placeholders, placeholderMap, id );
            this.orientation = orientation;
            this.divider = divider;
            this.childA = childA;
            this.childB = childB;
            
            if( childA != null )
            	childA.setParent( this );
            if( childB != null )
            	childB.setParent( this );
        }
        
        @Override
        public Node asNode() {
            return this;
        }
        
        /**
         * Tells whether this node is horizontal or vertical.
         * @return the orientation
         */
        public Orientation getOrientation() {
            return orientation;
        }
        
        /**
         * The location of the divider.
         * @return a value between 0 and 1
         */
        public double getDivider() {
            return divider;
        }
        
        /**
         * Gets the left or top child.
         * @return the left or top child
         */
        public Entry getChildA() {
            return childA;
        }
        
        /**
         * Gets the right or bottom child.
         * @return the right or bottom child
         */
        public Entry getChildB() {
            return childB;
        }
    }
}
