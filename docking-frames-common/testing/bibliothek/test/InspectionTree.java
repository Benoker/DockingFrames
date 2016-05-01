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
package bibliothek.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class InspectionTree implements TreeModel {
	private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
	private InspectionGraph graph;
	
	private Node root;
	
	public InspectionTree( InspectionNode root, InspectionGraph graph ){
		this.root = new Node( null, root );
		this.graph = graph;
	}
	
	public void addTreeModelListener( TreeModelListener l ){
		listeners.add( l );
	}

	public void removeTreeModelListener( TreeModelListener l ){
		listeners.remove( l );
	}
	
	protected TreeModelListener[] listeners(){
		return listeners.toArray( new TreeModelListener[ listeners.size() ] );
	}
	
	protected void fireNodesChanged( TreeModelEvent event ){
		for( TreeModelListener listener : listeners() ){
			listener.treeNodesChanged( event );
		}
	}
	
	protected void fireNodesRemoved( TreeModelEvent event ){
		for( TreeModelListener listener : listeners() ){
			listener.treeNodesRemoved( event );
		}
	}
	
	protected void fireNodesInserted( TreeModelEvent event ){
		for( TreeModelListener listener : listeners() ){
			listener.treeNodesInserted( event );
		}
	}
	
	public Object getChild( Object parent, int index ){
		return ((Node)parent).getChild( index );
	}

	public int getChildCount( Object parent ){
		return ((Node)parent).getChildrenCount();
	}

	public int getIndexOfChild( Object parent, Object child ){
		return ((Node)parent).indexOf( (Node)child );
	}

	public Object getRoot(){
		return root;
	}

	public boolean isLeaf( Object node ){
		return getChildCount( node ) == 0;
	}

	public void valueForPathChanged( TreePath path, Object newValue ){
		// ignore
	}
	
	public void update( boolean cleanse ){
		graph.updateAll();
		if( cleanse ){
			Set<InspectionNode> set = new HashSet<InspectionNode>();
			root.collect( set );
			graph.retainAll( set );
		}
	}
	
	public String toString( Object node ){
		Object value = ((Node)node).content.getInspect().getValue();
		return graph.toString( value );
	}
	
	private class Node implements InspectionNodeListener{
		private Node parent;
		private Node[] children;
		private InspectionNode content;
		
		public Node( Node parent, InspectionNode content ){
			this.parent = parent;
			this.content = content;
			content.addListener( this );
		}
		
		@Override
		public String toString(){
			return content.toString();
		}
		
		public void collect( Set<InspectionNode> nodes ){
			if( nodes.add( content )){
				if( children != null ){
					for( Node child : children ){
						child.collect( nodes );
					}
				}
			}
		}
		
		public void destroy(){
			content.removeListener( this );
			if( children != null ){
				for( Node child : children ){
					child.destroy();
				}
			}
		}
		
		public TreePath getPath(){
			Node current = this;
			int count = 0;
			while( current != null ){
				count++;
				current = current.parent;
			}
			Object[] path = new Object[count];
			current = this;
			int index = count-1;
			while( current != null ){
				path[index--] = current;
				current = current.parent;
			}
			return new TreePath( path );
		}
		
		public void updated(){
			TreeModelEvent event = new TreeModelEvent( this, getPath() );
			fireNodesChanged( event );
		}
		
		public void updated( InspectionNode[] oldChildren, InspectionNode[] newChildren ){
			if( children == null ){
				updated();
			}
			else{
				updated();
				
				if( oldChildren == null ){
					oldChildren = new InspectionNode[]{};
				}
				if( newChildren == null ){
					newChildren = new InspectionNode[]{};
				}
				
				handleRemoved( oldChildren, newChildren );
				handleAdded( oldChildren, newChildren );
			}
		}
		
		private void handleRemoved( InspectionNode[] oldChildren, InspectionNode[] newChildren ){
			boolean[] removed = new boolean[oldChildren.length];
			int offset = 0;
			int count = 0;
			
			for( int i = 0; i < oldChildren.length; i++ ){
				int index = indexOf( newChildren, oldChildren[i], offset );
				if( index == -1 ){
					removed[i] = true;
					count++;
				}
				else{
					removed[i] = false;
					offset = index+1;
				}
			}
			
			if( count > 0 ){
				int[] indices = new int[count];
				Object[] selection = new Object[count];
				Node[] updated = new Node[ children.length - count ];
				offset = 0;
				int index = 0;
				for( int i = 0; i < children.length; i++ ){
					if( removed[ i ] ){
						indices[ offset ] = i;
						selection[ offset ] = children[i];
						children[i].destroy();
						offset++;
					}
					else{
						updated[index++] = children[i];
					}
				}
				children = updated;
				TreeModelEvent event = new TreeModelEvent( InspectionTree.this, getPath(), indices, selection );
				fireNodesRemoved( event );
			}
		}
		
		private void handleAdded( InspectionNode[] oldChildren, InspectionNode[] newChildren ){
			boolean[] added = new boolean[ newChildren.length ];
			int count = 0;
			int offset = 0;
			
			for( int i = 0; i < newChildren.length; i++ ){
				int index = indexOf( oldChildren, newChildren[i], offset );
				if( index == -1 ){
					added[i] = true;
					count++;
				}
				else{
					added[i] = false;
					offset = index+1;
				}
			}
			
			if( count > 0 ){
				int[] indices = new int[count];
				Object[] selection = new Object[count];
				Node[] updated = new Node[ children.length + count ];
				offset = 0;
				int index = 0;
				for( int i = 0; i < newChildren.length; i++ ){
					if( added[ i ] ){
						indices[ offset ] = i;
						updated[i] = new Node( this, newChildren[i] );
						selection[ offset ] = updated[i];
						offset++;
					}
					else{
						updated[i] = children[index++];
					}
				}
				children = updated;
				TreeModelEvent event = new TreeModelEvent( InspectionTree.this, getPath(), indices, selection );
				fireNodesInserted( event );
			}
		}
		
		private int indexOf( InspectionNode[] nodes, InspectionNode search, int offset ){
			for( int i = offset; i < nodes.length; i++ ){
				if( nodes[i] == search ){
					return i;
				}
			}
			return -1;
		}
		
		private void children(){
			if( children == null ){
				InspectionNode[] contentChildren = content.getChildren();
				children = new Node[ contentChildren.length ];
				for( int i = 0; i < children.length; i++ ){
					children[i] = new Node( this ,contentChildren[i] );
				}
			}
		}
		
		public int getChildrenCount(){
			children();
			return children.length;
		}
		
		public Node getChild( int index ){
			children();
			return children[index];
		}
		
		public int indexOf( Node child ){
			for( int i = 0; i < children.length; i++ ){
				if( children[i] == child ){
					return i;
				}
			}
			return -1;
		}
	}
}
