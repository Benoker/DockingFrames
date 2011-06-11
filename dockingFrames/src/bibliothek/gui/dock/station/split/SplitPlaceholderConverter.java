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
import bibliothek.gui.dock.station.support.PlaceholderMap.Key;
import bibliothek.util.Path;

/**
 * Supporting class for {@link SplitDockStation}, provides the implementation of {@link SplitDockStation#getPlaceholders()}
 * and {@link SplitDockStation#setPlaceholders(PlaceholderMap)}
 * @author Benjamin Sigg
 */
public class SplitPlaceholderConverter {
	/** the station for which this converter is used */
	private SplitDockStation station;
	
	/**
	 * Creates a new converter
	 * @param station the station for which the converter will be used
	 */
	public SplitPlaceholderConverter( SplitDockStation station ){
		this.station = station;
	}
	
	/**
	 * Converts the {@link SplitDockStation} of this converter into a {@link PlaceholderMap}.
	 * @return the map
	 * @see SplitDockStation#getPlaceholders()
	 */
	public PlaceholderMap getPlaceholders(){
		final PlaceholderMap result = new PlaceholderMap( new Path( "dock.SplitDockStation" ), 0 );
		handleChild( result, station.getRoot(), "" );
		return result;
	}
	
	private Set<Path> handleChild( PlaceholderMap result, SplitNode node, String path ){
		if( node instanceof Root ){
			return handleRoot( result, (Root)node, path );
		}
		else if( node instanceof Node ){
			return handleNode( result, (Node)node, path );
		}
		else if( node instanceof Leaf ){
			return handleLeaf( result, (Leaf)node, path );
		}
		else if( node instanceof Placeholder ){
			return handlePlaceholder( result, (Placeholder)node, path );
		}
		else{
			return null;
		}
	}
	
	private Key handleBase( PlaceholderMap result, SplitNode node, String path, Set<Path> allPlaceholders, Set<Path> localPlaceholders ){
		Path[] array = node.getPlaceholders();
		PlaceholderMap map = node.getPlaceholderMap();
				
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
			result.putLong( key, path + ".id", node.getId() );
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
	
	private Set<Path> handleRoot( PlaceholderMap result, Root root, String path ){
		Set<Path> set = handleChild( result, root.getChild(), path + "0" );
		if( set != null ){
			Key key = handleBase( result, root, path, set, null );
			if( key != null ){
				result.putString( key, path + ".type", "r" );
				return set;
			}
		}
		return null;
	}
	
	private Set<Path> handleNode( PlaceholderMap result, Node node, String path ){
		Set<Path> left = handleChild( result, node.getLeft(), path + "0" );
		Set<Path> right = handleChild( result, node.getRight(), path + "1" );
		
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
			result.putDouble( key, path + ".divider", node.getDivider() );
			result.putBoolean( key, path + ".orientation", node.getOrientation() == Orientation.HORIZONTAL );
			return all;
		}
		return null;
	}
	
	private Set<Path> handlePlaceholder( PlaceholderMap result, Placeholder placeholder, String path ){
		Set<Path> set = new HashSet<Path>();
		Key key = handleBase( result, placeholder, path, set, null );
		if( key != null ){
			result.putString( key, path + ".type", "p" );
			return set;
		}
		return null;
	}
	
	private Set<Path> handleLeaf( PlaceholderMap result, Leaf leaf, String path ){
		Set<Path> set = new HashSet<Path>();
		Set<Path> local = new HashSet<Path>();
		Dockable dockable = leaf.getDockable();
		if( dockable != null ){
			Path key = station.getPlaceholderStrategy().getPlaceholderFor( dockable );
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
	
	
	/**
	 * Reads <code>map</code> and updates the contents of the {@link SplitDockStation} that is
	 * related to this converter.
	 * @param map the map to read
	 * @see SplitDockStation#setPlaceholders(PlaceholderMap)
	 */
	public void setPlaceholders( PlaceholderMap map ){
		map = map.filter( station.getPlaceholderStrategy() );
		
		Key[] keys = map.getPlaceholders();
		BuildNode root = new BuildNode();
		for( Key key : keys ){
			handleEntry( key, map, root );
		}
		
		DockableSplitDockTree tree = new DockableSplitDockTree();
		root.collapse( tree );
		station.dropTree( tree );
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
	private static class BuildNode{
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
		
		private BuildNode get( int index ){
			if( children == null ){
				children = new BuildNode[ index+1 ];
			}
			if( children.length <= index ){
				BuildNode[] temp = new BuildNode[ index+1 ];
				System.arraycopy( children, 0, temp, 0, children.length );
				children = temp;
			}
			if( children[index] == null ){
				children[index] = new BuildNode();
			}
			return children[index];
		}
		
		public SplitDockTree<Dockable>.Key collapse( DockableSplitDockTree tree ){
			if( "l".equals( type ) ||  "p".equals( type )){
				if( placeholders != null && placeholders.length > 0 ){
					return tree.put( placeholders, map, id );
				}
				return null;
			}
			else if( "n".equals( type )){
				SplitDockTree<Dockable>.Key left = null;
				SplitDockTree<Dockable>.Key right = null;
				
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
					SplitDockTree<Dockable>.Key child = children[0].collapse( tree );
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
