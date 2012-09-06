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
package bibliothek.gui.dock.extension.css.tree;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.extension.css.CssNode;
import bibliothek.gui.dock.extension.css.CssNodeListener;
import bibliothek.gui.dock.extension.css.CssPath;
import bibliothek.gui.dock.extension.css.path.AbstractCssPath;
import bibliothek.gui.dock.extension.css.path.CssPathListener;

/**
 * This path describes the entire path from root {@link DockStation} to
 * a specific {@link DockElement}.
 * @author Benjamin Sigg
 */
public class DockElementPath extends AbstractCssPath{
	/** The tree to ask for paths for parent {@link DockElement}s */
	private CssTree tree;
	
	/** The element described by <code>this</code> path */
	private DockElement element;
	
	/** The path to the parent {@link DockStation} or <code>null</code> */
	private CssPath parent;
	
	/** The node describing the relation between {@link #element} and its parent */
	private CssNode relationNode;
	
	/** The node for {@link #element} itself */
	private CssNode selfNode;
	
	private DockHierarchyListener hierarchyListener = new DockHierarchyListener(){
		@Override
		public void hierarchyChanged( DockHierarchyEvent event ){
			unbind();
			bind();
			firePathChanged();
		}
		
		@Override
		public void controllerChanged( DockHierarchyEvent event ){
			// ignore
		}
	};
	
	private CssPathListener parentListener = new CssPathListener(){
		public void pathChanged( CssPath path ){
			firePathChanged();
		}
	};
	
	private CssNodeListener nodeListener = new CssNodeListener(){
		@Override
		public void nodeChanged( CssNode node ){
			firePathChanged();	
		}
	};
	
	/**
	 * Creates a new path to <code>element</code>.
	 * @param tree the tree required to access the paths of parent elements
	 * @param element the element whose path <code>this</code> will be
	 */
	public DockElementPath( CssTree tree, DockElement element ){
		if( tree == null ){
			throw new IllegalArgumentException( "tree must not be null" );
		}
		if( element == null ){
			throw new IllegalArgumentException( "element must not be null" );
		}
		this.tree = tree;
		this.element = element;
	}
	
	@Override
	public int getSize(){
		CssPath parent;
		int size = 0;
		
		if( isBound() ){
			parent = this.parent;
			if( relationNode != null ){
				size = 2;
			}
			else{
				size = 1;
			}
		}
		else{
			parent = getParent();
			if( getRelationNode() != null ){
				size = 2;
			}
			else{
				size = 1;
			}
		}
		
		if( parent != null ){
			size += parent.getSize();
		}
		return size;
	}
	
	@Override
	public CssNode getNode( int index ){
		CssPath parent;
		if( isBound() ){
			parent = this.parent;
		}
		else{
			parent = getParent();
		}
		
		if( parent != null ){
			int parentSize = parent.getSize();
			if( index < parentSize ){
				return parent.getNode( index );
			}
			index -= parentSize;
		}
		
		if( isBound() ){
			if( index == 0 ){
				if( relationNode != null ){
					return relationNode;
				}
				return selfNode;
			}
			if( index == 1 ){
				if( relationNode != null ){
					return selfNode;
				}
			}
		}
		else{
			CssNode relationNode = getRelationNode();
			
			if( index == 0 ){
				if( relationNode !=  null ){
					return relationNode;
				}
				return getSelfNode();
			}
			if( index == 1 ){
				if( relationNode != null ){
					return getSelfNode();
				}
			}
		}
		throw new IllegalArgumentException( "index out of bounds" );
	}
	
	private CssPath getParent(){
		Dockable dockable = element.asDockable();
		if( dockable == null ){
			return null;
		}
		DockStation parent = dockable.getDockParent();
		if( parent == null ){
			return null;
		}
		return tree.getPathFor( dockable.getDockParent() );
	}
	
	private CssNode getSelfNode(){
		return tree.getSelfNode( element );
	}
	
	private CssNode getRelationNode(){
		return tree.getRelationNode( element );
	}
	
	@Override
	protected void bind(){
		parent = getParent();
		relationNode = getRelationNode();
		selfNode = getSelfNode();
		
		Dockable dockable = element.asDockable();
		if( dockable != null ){
			dockable.addDockHierarchyListener( hierarchyListener );
		}
		if( parent != null ){
			parent.addPathListener( parentListener );
		}
		if( relationNode != null ){
			relationNode.addNodeListener( nodeListener );
		}
		if( selfNode != null ){
			selfNode.addNodeListener( nodeListener );
		}
	}
	
	@Override
	protected void unbind(){
		Dockable dockable = element.asDockable();
		if( dockable != null ){
			dockable.removeDockHierarchyListener( hierarchyListener );
		}
		if( parent != null ){
			parent.removePathListener( parentListener );
			parent = null;
		}
		if( selfNode != null ){
			selfNode.removeNodeListener( nodeListener );
			selfNode = null;
		}
		if( relationNode != null ){
			relationNode.removeNodeListener( nodeListener );
			relationNode = null;
		}
	}
}
