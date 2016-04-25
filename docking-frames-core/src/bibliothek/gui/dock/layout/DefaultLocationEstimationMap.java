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
package bibliothek.gui.dock.layout;

import java.util.ArrayList;
import java.util.List;

/**
 * This default implementation of a {@link LocationEstimationMap} works directly with the tree built
 * by {@link DockLayoutComposition}s. It is to be used as follows:<br>
 * <ul>
 * 	<li>Create an initial map with the root {@link DockLayoutComposition}.</li>
 * 	<li>Recursively traverse the map down using {@link #subMap(int)} until a leaf is reached.</li>
 * 	<li>Call the various <code>setLocation</code>-methods.</li>
 * 	<li>Use {@link #finish()} once all locations for a node is set.</li>
 * 	<li>Set the location of the next level... until there are no levels left.</li>
 * </ul>
 * @author Benjamin Sigg
 */
public class DefaultLocationEstimationMap implements LocationEstimationMap {
	/** the root */
	private Node root;
	
	/**
	 * Creates a new map
	 * @param root the representation of this station
	 */
	public DefaultLocationEstimationMap( DockLayoutComposition root ){
		this( new Node( root, false ) );
	}
	
	private DefaultLocationEstimationMap( Node root ){
		this.root = root;
	}
	
	/**
	 * Prepares this map for estimating locations. Preparation of the map means that all current locations
	 * are stored and can be used for comparison when calling {@link #finish()}.
	 */
	public void prepare(){
		root.prepare();
	}
	
	/**
	 * Returns a newly created map that centers around the child with index <code>childIndex</code>.
	 * @param childIndex the index of the child
	 * @return the new map
	 */
	public DefaultLocationEstimationMap subMap( int childIndex ){
		return new DefaultLocationEstimationMap( root.children.get( childIndex ) );
	}
	
	/**
	 * Gets the element which is represented by this map.
	 * @return the element
	 */
	public DockLayoutComposition getRoot(){
		return root.composition;
	}
	
	/**
	 * Closes up this node. If the location of a leaf changed, its previous location is set as successor. 
	 * Otherwise if the location of a child changed, set the new location and use the old location as successor.
	 * Otherwise invalidate the leaf as its location will never be completed.
	 * @see #prepare()
	 */
	public void finish(){
		/*
		 * If location of leaf changed: set old location as successor
		 * If location of parent changed: set current location as successor and replace
		 * If no location changed: leafs gets invalidated
		 */
		for( Node child : root.children ){
			DockableProperty newParentLocation = child.validLocation();
			for( Node leaf : child.leafs ){
				leaf.finish( newParentLocation );
			}
			for( Node grandchild : child.children ){
				grandchild.finish( newParentLocation );
			}
		}
	}
	
	public int getChildCount(){
		return root.children.size();
	}
	
	public DockLayoutInfo getChild( int childIndex ){
		if( childIndex < 0 || childIndex >= getChildCount() ){
			return null;
		}
		return root.children.get( childIndex ).composition.getLayout();
	}
	
	public int getSubChildCount( int childIndex ){
		return root.children.get( childIndex ).leafs.size();
	}
	
	public DockLayoutInfo getSubChild( int childIndex, int subChildIndex ){
		return root.children.get( childIndex ).leafs.get( subChildIndex ).composition.getLayout();
	}
	
	public void setLocation( int childIndex, DockableProperty location ){
		if( location != null ){	
			getChild( childIndex ).setLocation( location );
		}
	}
	
	public void setLocation( int childIndex, int subChildIndex, DockableProperty location ){
		if( location != null ){
			getSubChild( childIndex, subChildIndex ).setLocation( location );
		}
	}
	
	/**
	 * A wrapper around a {@link DockLayoutInfo} and its children and leafs.
	 * @author Benjamin Sigg
	 */
	private static class Node{
		/** the content of this leaf */
		public final DockLayoutComposition composition;
		
		private DockableProperty oldLocation;
		private boolean valid = true;
		
		public final List<Node> children = new ArrayList<Node>();
		public final List<Node> leafs;
		
		public Node( DockLayoutComposition composition, boolean collectLeafs ){
			if( collectLeafs ){
				leafs = new ArrayList<Node>();
			}
			else{
				leafs = null;
			}
			
			this.composition = composition;
			for( DockLayoutComposition child : composition.getChildren() ){
				Node childNode = new Node( child, true );
				children.add( childNode );
				if( collectLeafs ){
					if( childNode.isLeaf() ){
						leafs.add( childNode );
					}
					else{
						leafs.addAll( childNode.leafs );
					}
				}
			}
		}
		
		public boolean isLeaf(){
			return children.isEmpty();
		}
		
		public void prepare(){
			if( valid ){
				oldLocation = composition.getLayout().getLocation();
				for( Node child : children ){
					child.prepare();
				}
			}
		}
		
		public void finish( DockableProperty newParentLocation ){
			if( valid ){
				DockableProperty newLeafLocation = validLocation();
				if( newLeafLocation == null && newParentLocation != null ){
					newLeafLocation = newParentLocation.copy();
				}
				if( newLeafLocation != null ){
					newLeafLocation.setSuccessor( oldLocation );
					composition.getLayout().setLocation( newLeafLocation );
				}
				else{
					invalidate();
				}
			}
		}
		
		public void invalidate(){
			valid = false;
		}
		
		public DockableProperty validLocation(){
			DockableProperty location = composition.getLayout().getLocation();
			if( location != oldLocation ){
				return location;
			}
			return null;
		}
	}
}
