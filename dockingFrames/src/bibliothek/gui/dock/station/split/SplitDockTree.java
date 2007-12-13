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

import java.util.HashSet;
import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.SplitDockStation;

/**
 * Represents the internal tree of a {@link SplitDockStation}. Can be used
 * to exchange the tree of a {@link SplitDockStation}. Every node or leaf is
 * represented through a {@link Key}. Client code may use these keys to read
 * data, or create new branches of the tree.
 * @author Benjamin Sigg
 *
 */
public class SplitDockTree{
	/** the root of the tree */
	private Key root;
	
	/** the set of Dockables which already have a key */
	private Set<Dockable> dockables = new HashSet<Dockable>();
	
	/**
	 * Sets <code>dockable</code> as root, and returns a key to the root.
	 * @param dockable the new root
	 * @return the key to the root
	 */
	public Key root( Dockable dockable ){
		root = put( dockable );
		return root;
	}
	
	/**
	 * Removes the root of this tree.
	 * @return the old root;
	 */
	public Key unroot(){
		Key key = root;
		root = null;
		return key;
	}
	
	/**
	 * Sets <code>key</code> as the root of the tree. The root must not have
	 * a parent. 
	 * @param key the key which will be the root, <code>null</code> is not allowed.
	 * @return <code>this</code>
	 */
	public SplitDockTree root( Key key ){
		if( key == null )
			throw new IllegalArgumentException( "Key must not be null" );
		
		if( key.getTree() != this )
			throw new IllegalArgumentException( "Key is not created by this tree" );
		
		if( key.parent != null )
			throw new IllegalArgumentException( "Key has a parent, and can't be the root" );
		
		this.root = key;
		return this;
	}
	
	/**
	 * Creates a key for the set <code>dockables</code>.
	 * @param dockables the elements for which a key is requested
	 * @return the new key
	 */
	public Key put( Dockable... dockables ){
		if( dockables == null )
			throw new IllegalArgumentException( "Dockables must not be null" );
        
        if( dockables.length == 0 )
            throw new IllegalArgumentException( "At least one Dockable is required" );
		
        for( Dockable dockable : dockables ){
            if( dockable == null )
                throw new IllegalArgumentException( "Entries of array must not be null" );
            
    		if( !this.dockables.add( dockable ))
    			throw new IllegalArgumentException( "Dockable already known" );
    		
        }
		
		return new Leaf( dockables );
	}
	
	/**
	 * Adds two elements horizontally.
	 * @param left the left element
	 * @param right the right element
	 * @return a key of the combination of the two elements
	 */
	public Key horizontal( Dockable left, Dockable right ){
		return horizontal( put( left ), put( right ) );
	}

	/**
	 * Adds two elements horizontally.
	 * @param left the left element
	 * @param right the right element
	 * @param divider how much space the first element gets in respect
	 * to the second element. Must be between 0 and 1.
	 * @return a key of the combination of the two elements
	 */
	public Key horizontal( Dockable left, Dockable right, double divider ){
		return horizontal( put( left ), put( right ), divider );	
	}

	/**
	 * Adds two elements horizontally.
	 * @param left the left element
	 * @param right the right element
	 * @return a key of the combination of the two elements
	 */
	public Key horizontal( Key left, Key right ){
		return horizontal( left, right, 0.5 );
	}
	
	/**
	 * Adds two elements horizontally.
	 * @param left the left element
	 * @param right the right element
	 * @param divider how much space the first element gets in respect
	 * to the second element. Must be between 0 and 1.
	 * @return a key of the combination of the two elements
	 */
	public Key horizontal( Key left, Key right, double divider ){
		return new Node( left, right, divider, true );
	}
	
	/**
	 * Adds two elements vertically.
	 * @param top the top element
	 * @param bottom the bottom element
	 * @return a key of the combination of the two elements
	 */
	public Key vertical( Dockable top, Dockable bottom ){
		return vertical( put( top ), put( bottom ));
	}
	
	/**
	 * Adds two elements vertically.
	 * @param top the top element
	 * @param bottom the bottom element
	 * @param divider how much space the first element gets in respect
	 * to the second element. Must be between 0 and 1.
	 * @return a key of the combination of the two elements
	 */
	public Key vertical( Dockable top, Dockable bottom, double divider ){
		return vertical( put( top ), put( bottom ), divider );
	}
	
	/**
	 * Adds two elements vertically.
	 * @param top the top element
	 * @param bottom the bottom element
	 * @return a key of the combination of the two elements
	 */
	public Key vertical( Key top, Key bottom ){
		return vertical( top, bottom, 0.5 );
	}
	
	/**
	 * Adds two elements vertically.
	 * @param top the top element
	 * @param bottom the bottom element
	 * @param divider how much space the first element gets in respect
	 * to the second element. Must be between 0 and 1.
	 * @return a key of the combination of the two elements
	 */
	public Key vertical( Key top, Key bottom, double divider ){
		return new Node( top, bottom, divider, false );
	}
	
	/**
	 * Gets the root of the tree.
	 * @return the root, can be <code>null</code>
	 */
	public Key getRoot(){
		return root;
	}
	
	/**
	 * Tells whether <code>key</code> represents a leaf or not.
	 * @param key the key to test
	 * @return <code>true</code> if <code>key</code> is a leaf
	 */
	public boolean isDockable( Key key ){
		return key.asLeaf() != null;
	}
	
	/**
	 * Tells whether <code>key</code> represents a node or not.
	 * @param key the key to test
	 * @return <code>true</code> if <code>key</code> is a node
	 */
	public boolean isNode( Key key ){
		return key.asNode() != null;
	}
	
	/**
	 * Gets a list of all {@link Dockable}s that are known to this tree.
	 * @return the list of elements
	 */
	public Dockable[] getDockables(){
		return dockables.toArray( new Dockable[ dockables.size() ] );
	}
	
	/**
	 * Gets the elements that are represented by the leaf <code>key</code>.
	 * @param key the leaf
	 * @return the elements
	 */
	public Dockable[] getDockables( Key key ){
		if( !isDockable( key ))
			throw new IllegalArgumentException( "Not a Dockable" );
		return key.asLeaf().dockables;
	}
	
	/**
	 * Tells whether the node <code>key</code> represents a horizontal
	 * or a vertical node.
	 * @param key the node
	 * @return <code>true</code> if the elements are laid out horizontally,
	 * <code>false</code> if the are vertically
	 */
	public boolean isHorizontal( Key key ){
		if( !isNode( key ))
			throw new IllegalArgumentException( "Not a node" );
		return key.asNode().horizontal;
	}
	
	/**
	 * Gets the left element of the node <code>key</code>.
	 * @param key the node
	 * @return the left element
	 */
	public Key getLeft( Key key ){
		if( !isNode( key ))
			throw new IllegalArgumentException( "Not a node" );
		return key.asNode().keyA;
	}

	/**
	 * Gets the right element of the node <code>key</code>.
	 * @param key the node
	 * @return the right element
	 */
	public Key getRight( Key key ){
		if( !isNode( key ))
			throw new IllegalArgumentException( "Not a node" );
		return key.asNode().keyB;
	}
	
	/**
	 * Gets the top element of the node <code>key</code>.
	 * @param key the node
	 * @return the top element
	 */
	public Key getTop( Key key ){
		if( !isNode( key ))
			throw new IllegalArgumentException( "Not a node" );
		return key.asNode().keyA;
	}
	
	/**
	 * Gets the bottom element of the node <code>key</code>.
	 * @param key the node
	 * @return the bottom element
	 */
	public Key getBottom( Key key ){
		if( !isNode( key ))
			throw new IllegalArgumentException( "Not a node" );
		return key.asNode().keyB;
	}
	
	/**
	 * Gets the divider of the node <code>key</code>.
	 * @param key the node
	 * @return the divider, a number between 0 and 1
	 */
	public double getDivider( Key key ){
		if( !isNode( key ))
			throw new IllegalArgumentException( "Not a node" );
		return key.asNode().divider;
	}
	
	/**
	 * A key that represents either a node or a leaf. Clients should not
	 * subclass this class.
	 * @author Benjamin Sigg
	 */
	public abstract class Key{
		/** the parent of this node or leaf */
		private Key parent;
		
		/**
		 * Gets the tree which is the owner of this node or leaf.
		 * @return the owner
		 */
		public SplitDockTree getTree(){
			return SplitDockTree.this;
		}
		
		/**
		 * Gets the parent of this node or leaf.
		 * @return the parent, can be <code>null</code>
		 */
		public Key getParent(){
			return parent;
		}
		
		/**
		 * Sets the parent of this node or leaf.
		 * @param parent the parent
		 */
		private void setParent( Key parent ){
			this.parent = parent;
		}
		
		/**
		 * Gets this key as a leaf.
		 * @return this or <code>null</code>
		 */
		protected Leaf asLeaf(){
			return null;
		}
		
		/**
		 * Gets this key as a node.
		 * @return this or <code>null</code>
		 */
		protected Node asNode(){
			return null;
		}
	}
	
	/**
	 * A {@link Key} which represents a leaf.
	 * @author Benjamin Sigg
	 *
	 */
	private class Leaf extends Key{
		/** the Dockable that will replace this leaf */
		public Dockable[] dockables;
		
		/**
		 * Creates a new leaf.
		 * @param dockables the set of dockables which will replace this leaf
		 */
		public Leaf( Dockable[] dockables ){
            this.dockables = new Dockable[ dockables.length ];
            System.arraycopy( dockables, 0, this.dockables, 0, dockables.length );
		}
		
		@Override
        public Leaf asLeaf(){
			return this;
		}
	}
	
	/**
	 * A {@link Key} which represents a node in the tree.
	 * @author Benjamin Sigg
	 */
	private class Node extends Key{
		/** left or top child */
		public Key keyA;
		/** right or bottom child */
		public Key keyB;
		/** location of the divider */
		public double divider;
		/** whether the children are horizontal or vertical laid out */
		public boolean horizontal;
		
		/**
		 * Creates a new node.
		 * @param keyA the left or top child
		 * @param keyB the right or bottom child
		 * @param divider the location of the divider
		 * @param horizontal the orientation of this node
		 */
		public Node( Key keyA, Key keyB, double divider, boolean horizontal ){
			if( keyA.getTree() != getTree() )
				throw new IllegalArgumentException( "Key of first argument belongs not to this tree" );
			
			if( keyB.getTree() != getTree() )
				throw new IllegalArgumentException( "Key of second argument belongs not to this tree" );
			
			if( divider < 0 || divider > 1.0 )
				throw new IllegalArgumentException( "Divider out of bounds, must be between 0 and 1" );
			
			if( keyA.getParent() != null )
				throw new IllegalArgumentException( "First key already has a parent" );
			
			if( keyB.getParent() != null )
				throw new IllegalArgumentException( "Second key already has a parent" );
			
			if( keyA == keyB )
				throw new IllegalArgumentException( "The arguments must not be the same object" );
			
			if( keyA == root )
				throw new IllegalArgumentException( "First argument is the root, can't be a child of any other node" );
			
			if( keyB == root )
				throw new IllegalArgumentException( "Second argument is the root, can't be a child of any other node" );
			
			keyA.setParent( this );
			keyB.setParent( this );
			
			this.keyA = keyA;
			this.keyB = keyB;
			this.divider = divider;
			this.horizontal = horizontal;
		}
		
		@Override
		protected Node asNode(){
			return this;
		}
	}
}
