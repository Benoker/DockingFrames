/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.support.PlaceholderMap.Key;
import bibliothek.util.Path;

/**
 * Supporting class for {@link SplitDockStation} and {@link SplitDockPerspective}, allows to create
 * and read {@link PlaceholderMap}s.
 * @author Benjamin Sigg
 * @param <P> the kind of station or perspective using this converter
 * @param <N> the type of a single node
 * @param <D> the type representing a {@link Dockable}
 */
public abstract class AbstractSplitPlaceholderConverter<P, N, D> {
	/** the station for which this converter is used */
	private P station;
	
	/**
	 * Creates a new converter
	 * @param station the station for which the converter will be used
	 */
	public AbstractSplitPlaceholderConverter( P station ){
		this.station = station;
	}
	
	/**
	 * Gets the station for which this converter is used.
	 * @return the station
	 */
	public P getStation(){
		return station;
	}

	/**
	 * Converts the station of this converter into a {@link PlaceholderMap}.
	 * @return the map
	 * @see SplitDockStation#getPlaceholders()
	 */
	public PlaceholderMap getPlaceholders(){
		final PlaceholderMap result = new PlaceholderMap( new Path( "dock.SplitDockStation" ), 0 );
		handleChild( result, getRoot( station ), "" );
		return result;
	}
	
	/**
	 * Reads <code>map</code> and updates the contents of the {@link SplitDockStation} that is
	 * related to this converter.
	 * @param map the map to read
	 * @see SplitDockStation#setPlaceholders(PlaceholderMap)
	 */
	public void setPlaceholders( PlaceholderMap map ){
		map = map.filter( getPlaceholderStrategy( station ) );
		
		Key[] keys = map.getPlaceholders();
		BuildNode root = new BuildNode();
		for( Key key : keys ){
			handleEntry( key, map, root );
		}
	
		SplitDockTree<D> tree = createTree();
		root.collapse( tree );
		dropTree( station, tree );
	}
	
	/**
	 * Gets the root node of the tree that describes the layout of <code>station</code>.
	 * @param station the station whose root node is searched
	 * @return the root node
	 */
	protected abstract N getRoot( P station );
	
	/**
	 * Gets the {@link PlaceholderStrategy} that is used by <code>station</code> to filter
	 * its children.
	 * @param station the station whose {@link PlaceholderStrategy} is searched
	 * @return the strategy
	 */
	protected abstract PlaceholderStrategy getPlaceholderStrategy( P station );
	
	/**
	 * Tells whether <code>node</code> is a root node.
	 * @param node the node to check
	 * @return <code>true</code> if <code>node</code> is a root node
	 */
	protected abstract boolean isRoot( N node );
	
	/**
	 * Tells whether <code>node</code> is an intermediate node.
	 * @param node the node to check
	 * @return <code>true</code> if <code>node</code> is an intermediate node
	 */
	protected abstract boolean isNode( N node );
	
	/**
	 * Tells whether <code>node</code> is a leaf node.
	 * @param node the node to check
	 * @return <code>true</code> if <code>node</code> is a leaf node
	 */
	protected abstract boolean isLeaf( N node );
	
	/**
	 * Tells whether <code>node</code> is a placeholder node.
	 * @param node the node to check
	 * @return <code>true</code> if <code>node</code> is a placeholder node
	 */
	protected abstract boolean isPlaceholder( N node );
	
	/**
	 * Gets all placeholders that are associated with <code>node</code>.
	 * @param node the node whose placeholders are searched
	 * @return the placeholders
	 */
	protected abstract Path[] getPlaceholders( N node );
	
	/**
	 * Gets the {@link PlaceholderMap} which is associated with <code>node</code>.
	 * @param node the node whose {@link PlaceholderMap} is searched
	 * @return the map or <code>null</code>
	 */
	protected abstract PlaceholderMap getPlaceholderMap( N node );

	/**
	 * Gets the unique identifier that was assigned to <code>node</code>.
	 * @param node some node whose id is searched
	 * @return the unique identifier
	 */
	protected abstract long getId( N node );
	
	/**
	 * Gets the child of the {@link #isRoot(Object) root node} <code>root</code>.
	 * @param root a root node
	 * @return the only child of <code>root</code>
	 */
	protected abstract N getRootChild( N root );
	
	/**
	 * Gets the left child of the {@link #isNode(Object) intermediate node} <code>node</code>.
	 * @param node an intermediate node
	 * @return the left child of <code>node</code>
	 */
	protected abstract N getLeftChild( N node );
	
	/**
	 * Gets the right child of the {@link #isNode(Object) intermediate node} <code>node</code>.
	 * @param node an intermediate node
	 * @return the right child of <code>node</code>
	 */
	protected abstract N getRightChild( N node );
	
	/**
	 * Gets the divider location of the {@link #isNode(Object) intermediate node} <code>node</code>.
	 * @param node an intermediate node
	 * @return the divider location of <code>node</code>
	 */
	protected abstract double getDivider( N node );
	
	/**
	 * Gets the orientation of the {@link #isNode(Object) intermediate node} <code>node</code>.
	 * @param node an intermediate node
	 * @return the orientation of <code>node</code>
	 */
	protected abstract Orientation getOrientation( N node );
	
	/**
	 * Gets the dockable of the {@link #isLeaf(Object) leaf node} <code>leaf</code>.
	 * @param leaf a leaf node
	 * @return the dockable of <code>leaf</code>
	 */
	protected abstract D getDockable( N leaf );
	
	/**
	 * Gets a placeholder that is to be used for <code>dockable</code>.
	 * @param dockable some item whose placeholder is searched
	 * @return the placeholder, can be <code>null</code>
	 */
	protected abstract Path getPlaceholderFor( D dockable );
	
	/**
	 * Creates a new {@link SplitDockTree} which will be used for {@link #dropTree(Object, SplitDockTree)}.
	 * @return the new tree, not <code>null</code>
	 */
	protected abstract SplitDockTree<D> createTree();
	
	/**
	 * Updates <code>station</code> such that its layout looks as described by <code>tree</code>.
	 * @param station the station whose layout gets updated
	 * @param tree the new layout
	 */
	protected abstract void dropTree( P station, SplitDockTree<D> tree );
	
	private Set<Path> handleChild( PlaceholderMap result, N node, String path ){
		if( isRoot( node ) ){
			return handleRoot( result, node, path );
		}
		else if( isNode( node ) ){
			return handleNode( result, node, path );
		}
		else if( isLeaf( node ) ){
			return handleLeaf( result, node, path );
		}
		else if( isPlaceholder( node ) ){
			return handlePlaceholder( result, node, path );
		}
		else{
			return null;
		}
	}
	
	private Key handleBase( PlaceholderMap result, N node, String path, Set<Path> allPlaceholders, Set<Path> localPlaceholders ){
		Path[] array = getPlaceholders( node );
		PlaceholderMap map = getPlaceholderMap( node );
				
		if( array != null ){
			for( Path key : array ){
				allPlaceholders.add( key );
				if( localPlaceholders == null ){
					localPlaceholders = new HashSet<Path>();
				}
				localPlaceholders.add( key );
			}
		}
		
		if( !allPlaceholders.isEmpty() ){
			Key key = result.newKey( allPlaceholders.toArray( new Path[ allPlaceholders.size() ] ) );
			result.putLong( key, path + ".id", getId( node ) );
			if( map != null ){
				result.putMap( key, path + ".map", map.copy() );
			}
			if( localPlaceholders != null && !localPlaceholders.isEmpty() ){
				Key arrayKey = result.newKey( localPlaceholders.toArray( new Path[ localPlaceholders.size() ] ) );
				result.put( arrayKey, path + ".array", true );
			}
			return key;
		}
		return null;
	}
	
	private Set<Path> handleRoot( PlaceholderMap result, N root, String path ){
		Set<Path> set = handleChild( result, getRootChild( root ), path + "0" );
		if( set != null ){
			Key key = handleBase( result, root, path, set, null );
			if( key != null ){
				result.putString( key, path + ".type", "r" );
				return set;
			}
		}
		return null;
	}
	
	private Set<Path> handleNode( PlaceholderMap result, N node, String path ){
		Set<Path> left = handleChild( result, getLeftChild( node ), path + "0" );
		Set<Path> right = handleChild( result, getRightChild( node ), path + "1" );
		
		if( left == null && right == null ){
			return null;
		}
		
		Set<Path> all;
		if( left == null ){
			all = right;
		}
		else if( right == null ){
			all = left;
		}
		else{
			all = left;
			all.addAll( right );
		}
		
		Key key = handleBase( result, node, path, all, null );
		if( key != null ){
			result.putString( key, path + ".type", "n" );
			result.putDouble( key, path + ".divider", getDivider( node ) );
			result.putBoolean( key, path + ".orientation", getOrientation( node ) == Orientation.HORIZONTAL );
			return all;
		}
		return null;
	}
	
	private Set<Path> handlePlaceholder( PlaceholderMap result, N placeholder, String path ){
		Set<Path> set = new HashSet<Path>();
		Key key = handleBase( result, placeholder, path, set, null );
		if( key != null ){
			result.putString( key, path + ".type", "p" );
			return set;
		}
		return null;
	}
	
	private Set<Path> handleLeaf( PlaceholderMap result, N leaf, String path ){
		Set<Path> set = new HashSet<Path>();
		Set<Path> local = new HashSet<Path>();
		D dockable = getDockable( leaf );
		if( dockable != null ){
			Path key = getPlaceholderFor( dockable );
			if( key != null ){
				set.add( key );
				local.add( key );
			}
		}
		
		Key key = handleBase( result, leaf, path, set, local );
		if( key != null ){
			result.putString( key, path + ".type", "l" );
			return set;
		}
		return null;
	}
	
	
	private void handleEntry( Key key, PlaceholderMap map, BuildNode root ){
		for( String item : map.getKeys( key )){
			BuildNode node = root.get( path( item ));
			String itemKey = key( item );
			if( "type".equals( itemKey )){
				node.type = map.getString( key, item );
			}
			else if( "id".equals( itemKey )){
				node.id = map.getLong( key, item );
			}
			else if( "map".equals( itemKey )){
				node.map = map.getMap( key, item );
			}
			else if( "array".equals( itemKey )){
				node.placeholders = key.getPlaceholders();
			}
			else if( "divider".equals( itemKey )){
				node.divider = map.getDouble( key, item );
			}
			else if( "orientation".equals( itemKey )){
				node.orientation = map.getBoolean( key, item );
			}
		}
	}
	
	private String path( String item ){
		return item.substring( 0, item.indexOf( '.' ) );
	}
	
	private String key( String item ){
		return item.substring( item.indexOf( '.' )+1 );
	}

	/**
	 * A representation for a {@link SplitNode} helping to build up an initial {@link DockableSplitDockTree}.
	 * @author Benjamin Sigg
	 */
	private class BuildNode{
		private BuildNode[] children;
		private long id = -1;
		private String type;
		private Path[] placeholders;
		private PlaceholderMap map;
		private boolean orientation;
		private double divider = 0.5;
		
		public BuildNode get( String path ){
			return get( path, 0 );
		}
		
		private BuildNode get( String path, int index ){
			if( index == path.length() ){
				return this;
			}
			if( '0' == path.charAt( index )){
				return get(0).get( path, index+1 );
			}
			else{
				return get(1).get( path, index+1 );
			}
		}
		
		private BuildNode[] createArray( int length ){
			return (BuildNode[]) new AbstractSplitPlaceholderConverter.BuildNode[ length ];
		}
		
		private BuildNode get( int index ){
			if( children == null ){
				children = createArray( index+1 );
			}
			if( children.length <= index ){
				BuildNode[] temp = createArray( index+1 );
				System.arraycopy( children, 0, temp, 0, children.length );
				children = temp;
			}
			if( children[index] == null ){
				children[index] = new BuildNode();
			}
			return children[index];
		}
		
		public SplitDockTree<D>.Key collapse( SplitDockTree<D> tree ){
			if( "l".equals( type ) ||  "p".equals( type )){
				if( placeholders != null && placeholders.length > 0 ){
					return tree.put( placeholders, map, id );
				}
				return null;
			}
			else if( "n".equals( type )){
				SplitDockTree<D>.Key left = null;
				SplitDockTree<D>.Key right = null;
				
				if( children != null && children.length > 0 && children[0] != null ){
					left = children[0].collapse( tree );
				}
				if( children != null && children.length > 1 && children[1] != null ){
					right = children[1].collapse( tree );
				}
				if( left == null ){
					return right;
				}
				if( right == null ){
					return left;
				}
				
				if( orientation ){
					return tree.horizontal( left, right, divider, placeholders, map, id );
				}
				else{
					return tree.vertical( left, right, divider, placeholders, map, id );
				}
			}
			else if( "r".equals( type )){
				if( children != null && children.length > 0 && children[0] != null ){
					SplitDockTree<D>.Key child = children[0].collapse( tree );
					if( child != null ){
						tree.root( child );
						return child;
					}
				}
				return null;
			}
			else{
				return null;
			}
		}
	}
}
