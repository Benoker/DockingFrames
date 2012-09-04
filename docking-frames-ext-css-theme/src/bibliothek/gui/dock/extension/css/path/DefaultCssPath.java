/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.path;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.dock.extension.css.CssNode;
import bibliothek.gui.dock.extension.css.CssNodeListener;
import bibliothek.gui.dock.extension.css.CssPath;

/**
 * This {@link CssPath} offers methods to add and remove {@link CssNode}s.
 * @author Benjamin Sigg
 */
public class DefaultCssPath extends AbstractCssPath{
	private List<CssNode> nodes = new ArrayList<CssNode>();
	private CssNodeListener nodeListener = new CssNodeListener(){
		@Override
		public void nodeChanged( CssNode node ){
			firePathChanged();	
		}
	};
	
	/**
	 * Creates a new path.
	 * @param nodes the nodes of this path
	 */
	public DefaultCssPath( CssNode... nodes ){
		for( CssNode node : nodes ){
			addNode( node );
		}
	}
	
	@Override
	protected void bind(){
		for( CssNode node : nodes ){
			node.addNodeListener( nodeListener );
		}
	}
	
	@Override
	protected void unbind(){
		for( CssNode node : nodes ){
			node.removeNodeListener( nodeListener );
		}
	}
	
	public int getSize(){
		return getNodeCount();
	}

	/**
	 * Gets the number of nodes of this path.
	 * @return the number of parts
	 */
	public int getNodeCount(){
		return nodes.size();
	}
	
	public CssNode getNode( int index ){
		return nodes.get( index );
	}
	
	/**
	 * Adds <code>node</code> at the end of this list of nodes.
	 * @param node the node to add, not <code>null</code>
	 */
	public void addNode( CssNode node ){
		addNode( getSize(), node );
	}
	
	/**
	 * Inserts <code>node</code> at the <code>index</code>'th location of
	 * this list of nodes.
	 * @param index the index of the new node
	 * @param node the new node, not <code>null</code>
	 */
	public void addNode( int index, CssNode node ){
		if( node == null ){
			throw new IllegalArgumentException( "node must not be null" );
		}
		nodes.add( index, node );
		if( isBound() ){
			node.addNodeListener( nodeListener );
			firePathChanged();
		}
	}
	
	/**
	 * Gets the location of <code>node</code> in this list of nodes.
	 * @param node the node to search
	 * @return the location or <code>-1</code> if not found
	 */
	public int indexOf( CssNode node ){
		return nodes.indexOf( node );
	}
	

	/**
	 * Removes the <code>index</code>'th node of this path.
	 * @param index the index of the node to remove
	 * @return the node that was removed
	 */
	public CssNode removeNode( int index ){
		CssNode node = nodes.remove( index );
		if( isBound() ){
			node.removeNodeListener( nodeListener );
			firePathChanged();
		}
		return node;
	}
	
	/**
	 * Removes <code>node</code> from this list of nodes.
	 * @param node the node to remove
	 */
	public void remove( CssNode node ){
		int index = indexOf( node );
		if( index >= 0 ){
			removeNode( index );
		}
	}
}
