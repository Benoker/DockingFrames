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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.station.split.SplitDockPathProperty.Location;

/**
 * A layout storing the contents of a {@link SplitDockStation}.
 * @author Benjamin Sigg
 */
public class SplitDockStationLayout {
    /** the root of the tree, can be <code>null</code> */
    private Entry root;
    /** the id of the element that is put into fullscreen-mode */
    private int fullscreen;
    
    /**
     * Creates a new layout
     * @param root the root of the tree, can be <code>null</code>
     * @param fullscreen the id of the element which is in fullscreen-mode
     */
    public SplitDockStationLayout( Entry root, int fullscreen ){
        this.root = root;
        this.fullscreen = fullscreen;
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
     * An entry in a tree, either a node or a leaf.
     * @author Benjamin Sigg
     */
    public static abstract class Entry{
    	/** the parent element of this entry */
    	private Node parent;
    	/** the unique id of this node */
    	private long id;
    	
    	/**
    	 * Create a new entry
    	 * @param id the unique id of this node or -1
    	 */
    	public Entry( long id ){
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
        	return path;
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
         * @param id the id of a {@link Dockable}
         * @param nodeId the unique identifier of this node
         */
        public Leaf( int id, long nodeId ){
        	super( nodeId );
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
         * @param id the unique identifier of this node or -1
         */
        public Node( Orientation orientation, double divider, Entry childA, Entry childB, long id ){
        	super( id );
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
         * Creates a new path pointing to <code>child</code> which must be
         * a child of this node.
         * @param child some child of this node
         * @return a new path for <code>child</code>
         */
        public SplitDockPathProperty createPathProperty( Entry child ){
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
