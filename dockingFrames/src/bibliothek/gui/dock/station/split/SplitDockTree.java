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

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;

/**
 * Represents the internal tree of a {@link SplitDockStation}. Can be used
 * to exchange the tree of a {@link SplitDockStation}. Every node or leaf is
 * represented through a {@link Key}. Client code may use these keys to read
 * data, or create new branches of the tree.<br>
 * Each node in a {@link SplitDockStation} has a unique identifier. This {@link SplitDockTree} 
 * class allows to set this identifier either through {@link Key#setNodeId(long)} or
 * through the non-complex methods (the methods that create only one new key).
 * @author Benjamin Sigg
 * @param <D> the kind of object representing a {@link Dockable}
 */
public abstract class SplitDockTree<D>{
	/** the root of the tree */
	private Key root;
	
	/** the set of Dockables which already have a key */
	private Set<D> dockables = new HashSet<D>();
	
	/**
	 * Creates a new array of size <code>size</code> for objects of type <code>D</code>.
	 * @param size the size of the array
	 * @return the new array
	 */
	public abstract D[] array( int size );
	
	/**
	 * Creates an array around <code>dockable</code>.
	 * @param dockable the element that should be put into an array
	 * @return the array of length 1
	 */
	public D[] array( D dockable ){
		D[] array = array( 1 );
		array[0] = dockable;
		return array;
	}
	
	/**
	 * Sets <code>dockable</code> as root, and returns a key to the root.
	 * @param dockable the new root
	 * @return the key to the root
	 */
	public Key root( D dockable ){
		root = put( array( dockable ));
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
	public SplitDockTree<D> root( Key key ){
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
	 * Creates a key for the leaf <code>dockable</code>.
	 * @param dockable the element for which a key is requested
	 * @param nodeId the unique identifier for this node, can be -1
	 * @return the new key
	 */
	public Key put( D dockable, long nodeId ){
		D[] array = array( 1 );
		array[0] = dockable;
		return put( array, null, nodeId );
	}
	
	/**
	 * Creates a key for the set <code>dockables</code>.
	 * @param dockables the elements for which a key is requested
	 * @return the new key
	 */
	public Key put( D... dockables ){
		return put( dockables, null );
	}

	/**
	 * Creates a key for the set <code>dockables</code>.
	 * @param dockables the elements for which a key is requested
	 * @param selected the element that should be selected, can be <code>null</code>
	 * @return the new key
	 */
	public Key put( D[] dockables, D selected ){
		return put( dockables, selected, -1 );
	}
	
	/**
	 * Creates a key for the set <code>dockables</code>.
	 * @param dockables the elements for which a key is requested
	 * @param selected the element that should be selected, can be <code>null</code>
	 * @param nodeId a unique identifier for this node, may be -1
	 * @return the new key
	 */
	public Key put( D[] dockables, D selected, long nodeId ){
		return put( dockables, selected, null, null, nodeId );
	}
	
	/**
	 * Creates a key for a placeholder leaf.
	 * @param placeholders the placeholders to store
	 * @param placeholderMap placeholder information of a child {@link DockStation}
	 * @return the new key
	 */
	public Key put( Path[] placeholders, PlaceholderMap placeholderMap ){
		return put( placeholders, placeholderMap, -1 );
	}
	
	/**
	 * Creates a key for a placeholder leaf.
	 * @param placeholders the placeholders to store
	 * @param placeholderMap placeholder information of a child {@link DockStation}
	 * @param nodeId the unique identifier of the new node, can be -1
	 * @return the new key
	 */
	public Key put( Path[] placeholders, PlaceholderMap placeholderMap, long nodeId ){
		return put( null, null, placeholders, placeholderMap, nodeId );
	}
	
	/**
	 * Creates a key for the set of <code>dockables</code> or the set of
	 * <code>placeholders</code>.
	 * @param dockables the elements for which a key is requested
	 * @param selected the element that should be selected, can be <code>null</code>
	 * @param placeholders the placeholders for which a key is requested
	 * @param placeholderMap placeholder information of a child {@link DockStation}
	 * @param nodeId a unique identifier for this node, may be -1
	 * @return the new key
	 */
	public Key put( D[] dockables, D selected, Path[] placeholders, PlaceholderMap placeholderMap, long nodeId ){
		if( placeholders == null || placeholders.length == 0 ){
			if( dockables == null )
				throw new IllegalArgumentException( "Dockables must not be null" );
	        
	        if( dockables.length == 0 )
	            throw new IllegalArgumentException( "At least one Dockable is required" );
		}
		
		if( dockables != null ){
	        for( D dockable : dockables ){
	            if( dockable == null )
	                throw new IllegalArgumentException( "Entries of array must not be null" );
	            
	    		if( !this.dockables.add( dockable ))
	    			throw new IllegalArgumentException( "Dockable already known" );
	        }	
		}
		
		return new Leaf( dockables, selected, placeholders, placeholderMap, nodeId );
	}
	
	/**
	 * Adds two elements horizontally.
	 * @param left the left element
	 * @param right the right element
	 * @return a key of the combination of the two elements
	 */
	public Key horizontal( D left, D right ){
		return horizontal( put( array( left ) ), put( array( right ) ) );
	}

	/**
	 * Adds two elements horizontally.
	 * @param left the left element
	 * @param right the right element
	 * @param divider how much space the first element gets in respect
	 * to the second element. Must be between 0 and 1.
	 * @return a key of the combination of the two elements
	 */
	public Key horizontal( D left, D right, double divider ){
		return horizontal( put( array( left ) ), put( array( right ) ), divider );	
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
		return horizontal( left, right, divider, -1 );
	}
	
	/**
	 * Adds two elements horizontally.
	 * @param left the left element
	 * @param right the right element
	 * @param divider how much space the first element gets in respect
	 * to the second element. Must be between 0 and 1.
	 * @param nodeId a unique identifier for this node, may be -1
	 * @return a key of the combination of the two elements
	 */
	public Key horizontal( Key left, Key right, double divider, long nodeId ){
		return horizontal( left, right, divider, null, null, nodeId );
	}

	/**
	 * Adds two elements horizontally.
	 * @param left the left element
	 * @param right the right element
	 * @param divider how much space the first element gets in respect
	 * to the second element. Must be between 0 and 1.
	 * @param placeholders placeholders that are associated with this nodes
	 * @param placeholderMap placeholder information of a child {@link DockStation}
	 * @param nodeId a unique identifier for this node, may be -1
	 * @return a key of the combination of the two elements
	 */
	public Key horizontal( Key left, Key right, double divider, Path[] placeholders, PlaceholderMap placeholderMap, long nodeId ){
		return new Node( left, right, divider, true, placeholders, placeholderMap, nodeId );
	}
	
	/**
	 * Adds two elements vertically.
	 * @param top the top element
	 * @param bottom the bottom element
	 * @return a key of the combination of the two elements
	 */
	public Key vertical( D top, D bottom ){
		return vertical( put( array( top ) ), put( array( bottom ) ));
	}
	
	/**
	 * Adds two elements vertically.
	 * @param top the top element
	 * @param bottom the bottom element
	 * @param divider how much space the first element gets in respect
	 * to the second element. Must be between 0 and 1.
	 * @return a key of the combination of the two elements
	 */
	public Key vertical( D top, D bottom, double divider ){
		return vertical( put( array( top ) ), put( array( bottom ) ), divider );
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
		return vertical( top, bottom, divider, -1 );
	}
	
	/**
	 * Adds two elements vertically.
	 * @param top the top element
	 * @param bottom the bottom element
	 * @param divider how much space the first element gets in respect
	 * to the second element. Must be between 0 and 1.
	 * @param nodeId a unique identifier for this node, may be -1
	 * @return a key of the combination of the two elements
	 */
	public Key vertical( Key top, Key bottom, double divider, long nodeId ){
		return vertical( top, bottom, divider, null, null, nodeId );
	}
	
	/**
	 * Adds two elements vertically.
	 * @param top the top element
	 * @param bottom the bottom element
	 * @param divider how much space the first element gets in respect
	 * to the second element. Must be between 0 and 1.
	 * @param placeholders placeholders that are associated with this node
	 * @param placeholderMap placeholder information of a child {@link DockStation}
	 * @param nodeId a unique identifier for this node, may be -1
	 * @return a key of the combination of the two elements
	 */
	public Key vertical( Key top, Key bottom, double divider, Path[] placeholders, PlaceholderMap placeholderMap, long nodeId ){
		return new Node( top, bottom, divider, false, placeholders, placeholderMap, nodeId );
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
	 * Tells whether <code>key</code> contains placeholders
	 * @param key some node or leaf
	 * @return <code>true</code> if there are placeholders
	 */
	public boolean isPlaceholder( Key key ){
		return key.placeholders != null && key.placeholders.length > 0;
	}
	
	/**
	 * Gets the placeholders which are associated with <code>key</code>
	 * @param key some node or leaf
	 * @return the placeholders, can be <code>null</code>
	 */
	public Path[] getPlaceholders( Key key ){
		return key.placeholders;
	}
	
	/**
	 * Gets the placeholder information of the child {@link DockStation} of <code>key</code>.
	 * @param key some node or leaf
	 * @return the placeholder information, can be <code>null</code>
	 */
	public PlaceholderMap getPlaceholderMap( Key key ){
		return key.placeholderMap;
	}
	
	/**
	 * Gets a list of all {@link Dockable}s that are known to this tree.
	 * @return the list of elements
	 */
	public D[] getDockables(){
		return dockables.toArray( array( dockables.size() ) );
	}
	
	/**
	 * Gets the elements that are represented by the leaf <code>key</code>.
	 * @param key the leaf
	 * @return the elements, can be <code>null</code>
	 */
	public D[] getDockables( Key key ){
		if( !isDockable( key ))
			throw new IllegalArgumentException( "Not a Dockable" );
		return key.asLeaf().dockables;
	}
	
	/**
	 * Gets the element that is selected in this leaf.
	 * @param key the leaf
	 * @return the selected element, can be <code>null</code>
	 */
	public D getSelected( Key key ){
		if( !isDockable( key ))
			throw new IllegalArgumentException( "Not a Dockable" );
		return key.asLeaf().selected;
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
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append( getClass().getName() + "[root=\n" );
		if( root == null ){
			builder.append( "null" );
		}
		else{
			root.toString( builder, 1 );
		}
		builder.append( "\n]" );
		return builder.toString();
	}
	
	/**
	 * A key that represents either a node or a leaf. Clients should not
	 * subclass this class.
	 * @author Benjamin Sigg
	 */
	public abstract class Key{
		/** the parent of this node or leaf */
		private Key parent;
		
		/** the unique id of this node */
		private long id;
		
		/** the placeholders associated with this node */
		private Path[] placeholders;
		
		/** placeholder information about a child {@link DockStation} */
		private PlaceholderMap placeholderMap;
		
		/**
		 * Creates a new key
		 * @param placeholders the placeholders that are associated with this node
		 * @param placeholderMap placeholder information about a child {@link DockStation}
		 * @param id the unique id of this node 
		 */
		public Key( Path[] placeholders, PlaceholderMap placeholderMap, long id ){
			this.id = id;
			if( placeholders != null ){
				this.placeholders = placeholders.clone();
			}
			if( placeholderMap != null ){
				this.placeholderMap = placeholderMap.copy();
			}
		}
		
		/**
		 * Converts this key and all its children into a {@link String}.
		 * @param builder the builder to which the content of this key is to be added
		 * @param depth the depth of this key (number of parents)
		 */
		protected abstract void toString( StringBuilder builder, int depth );
		
		/**
		 * Gets the tree which is the owner of this node or leaf.
		 * @return the owner
		 */
		public SplitDockTree<D> getTree(){
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
		 * Sets the unique identifier of this node
		 * @param id the id or -1
		 */
		public void setNodeId( long id ){
			this.id = id;
		}
		
		/**
		 * Gets the unique id of this node.
		 * @return the identifier or -1
		 */
		public long getNodeId(){
			return id;
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
		public D[] dockables;
		/** the element that is selected, can be <code>null</code> */
		public D selected;
		
		/**
		 * Creates a new leaf.
		 * @param dockables the set of dockables which will replace this leaf
		 * @param selected the selected element
		 * @param placeholders the placeholders that are associated with this node
		 * @param placeholderMap placeholder information for a child {@link DockStation}
		 * @param id the unique identifier of this node or -1
		 */
		public Leaf( D[] dockables, D selected, Path[] placeholders, PlaceholderMap placeholderMap, long id ){
			super( placeholders, placeholderMap, id );
			if( dockables != null ){
				this.dockables = dockables.clone();
			}
            this.selected = selected;
		}
		
		@Override
        public Leaf asLeaf(){
			return this;
		}
		
		@Override
		protected void toString( StringBuilder builder, int depth ){
			for( int i = 0; i < depth; i++ ){
				builder.append( "\t" );
			}
			builder.append( "Leaf[dockables: ").append( dockables == null ? 0 : dockables.length );
			Path[] placeholders = getPlaceholders( this );
			if( placeholders != null ){
				builder.append( ", placeholders: " );
				for( Path placeholder : placeholders ){
					builder.append( placeholder );
				}
			}
			builder.append( "]" );
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
		 * @param placeholders placeholders that are associated with this node
		 * @param placeholderMap placeholder information of a child {@link DockStation}
		 * @param id the unique identifier of this node or -1
		 */
		public Node( Key keyA, Key keyB, double divider, boolean horizontal, Path[] placeholders, PlaceholderMap placeholderMap, long id ){
			super( placeholders, placeholderMap, id );
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
		
		protected void toString( StringBuilder builder, int depth ){
			for( int i = 0; i < depth; i++ ){
				builder.append( "\t" );
			}
			builder.append( "Node[divider: ").append( divider );
			Path[] placeholders = getPlaceholders( this );
			if( placeholders != null ){
				builder.append( ", placeholders: " );
				for( Path placeholder : placeholders ){
					builder.append( placeholder );
				}
			}
			builder.append( "]\n" );
			keyA.toString( builder, depth+1 );
			builder.append( "\n" );
			keyB.toString( builder, depth+1 );
		}
	}
}
