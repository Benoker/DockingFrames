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

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.station.split.SplitDockPathProperty.Location;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;

/**
 * A layout storing the contents of a {@link SplitDockStation}.
 * @author Benjamin Sigg
 */
public class SplitDockStationLayout {
    /** the root of the tree, can be <code>null</code> */
    private Entry root;
    /** the id of the element that is put into fullscreen-mode */
    private int fullscreen;
    /** whether the station showed a fullscreen action */
    private boolean hasFullscreenAction;
    
    /**
     * Creates a new layout
     * @param root the root of the tree, can be <code>null</code>
     * @param fullscreen the id of the element which is in fullscreen-mode
     * @deprecated please use {@link #SplitDockStationLayout(Entry, int, boolean)} instead
     */
    @Deprecated
    public SplitDockStationLayout( Entry root, int fullscreen ){
    	this( root, fullscreen, true );
    }
    
    /**
     * Creates a new layout
     * @param root the root of the tree, can be <code>null</code>
     * @param fullscreen the id of the element which is in fullscreen-mode
     * @param hasFullscreenAction whether the {@link SplitDockStation} did show a fullscreen-action
     */
    public SplitDockStationLayout( Entry root, int fullscreen, boolean hasFullscreenAction ){
        this.root = root;
        this.fullscreen = fullscreen;
        this.hasFullscreenAction = hasFullscreenAction;
    }
    
    /**
     * Gets the root of the tree.
     * @return the root, can be <code>null</code>
     */
    public Entry getRoot() {
        return root;
    }
    
    /**
     * Gets the id of the element which is in fullscreen-mode.
     * @return the id of the element, -1 means that no element is set to
     * fullscreen
     */
    public int getFullscreen() {
        return fullscreen;
    }
    
    /**
     * Tells whether the {@link SplitDockStation} did show a fullscreen-action or not. This property
     * is only applied if a new {@link SplitDockStation} is created during loading.
     * @return whether to show an action or not
     */
    public boolean hasFullscreenAction(){
		return hasFullscreenAction;
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
         * Whether this node or leaf is visible to the user.
         * @return <code>true</code> if this represents some graphical element or has a visible child
         */
        public abstract boolean isVisible();
        
        /**
         * Creates a new path property which describes the location of
         * this element.
         * @return the new path property
         */
        public SplitDockPathProperty createPathProperty() {
        	SplitDockPathProperty path = null;
        	if( parent != null ){
        	 	path = parent.createPathProperty( this );
        	}
        	else{
        		path = new SplitDockPathProperty();
        	}
        	path.setLeafId( getNodeId() );
        	return path;
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
        /** the id of the element */
        private int id;
        
        /**
         * Creates a new leaf
         * @param id the id of a {@link Dockable} or -1
         * @param placeholders placeholders associated with this leaf
         * @param placeholderMap placeholder information of a child {@link DockStation}
         * @param nodeId the unique identifier of this node
         */
        public Leaf( int id, Path[] placeholders, PlaceholderMap placeholderMap, long nodeId ){
        	super( placeholders, placeholderMap, nodeId );
            this.id = id;
        }
        
        /**
         * Gets the id of a {@link Dockable}.
         * @return the id
         */
        public int getId() {
            return id;
        }
        
        @Override
        public Leaf asLeaf() {
            return this;
        }
        
        @Override
        public boolean isVisible(){
        	return id != -1;
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
        
        @Override
        public boolean isVisible(){
	        return childA.isVisible() && childB.isVisible();
        }
        
        /**
         * Creates a new path pointing to <code>child</code> which must be
         * a child of this node.
         * @param child some child of this node
         * @return a new path for <code>child</code>
         */
        public SplitDockPathProperty createPathProperty( Entry child ){
        	boolean childAvisible = childA.isVisible();
        	boolean childBvisible = childB.isVisible();
        	
        	if( childAvisible && childBvisible ){
	        	SplitDockPathProperty property = createPathProperty();
	        	if( child == childA ){
	        		if( orientation == Orientation.HORIZONTAL ){
	        			property.add( Location.LEFT, divider, child.getNodeId() );
	        		}
	        		else{
	        			property.add( Location.TOP, divider, child.getNodeId() );
	        		}
	        	}
	        	else if( child == childB ){
	        		if( orientation == Orientation.HORIZONTAL ){
	        			property.add( Location.RIGHT, 1-divider, child.getNodeId() );
	        		}
	        		else{
	        			property.add( Location.BOTTOM, 1-divider, child.getNodeId() );
	        		}
	        	}
	        	return property;
        	}

        	Node parent = getParent();
        	if( parent != null ){
        		return parent.createPathProperty( this );
        	}
        	else{
        		return new SplitDockPathProperty();
        	}
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
