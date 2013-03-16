/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.doc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Describes the location of a {@link DocProperty}.
 * @author Benjamin Sigg
 */
public class DocPath {
	/** the property whose path <code>this</code> is */
	private DocProperty property;
	
	/** all the nodes of this path */
	private List<DocNode> nodes = new ArrayList<DocNode>();
	
	/** the unsorted nodes of this path */
	private List<DocNode> unordered = new ArrayList<DocNode>();
	
	/** the actual path */
	private CssDocPath path;
	
	/** the parent path of <code>this</code> */
	private DocPath parent;
	
	/**
	 * Calculates the path of <code>property</code>.
	 * @param property the property whose path <code>this</code> represents.
	 * @param path the actual path
	 */
	public DocPath( DocProperty property, CssDocPath path ){
		this.property = property;
		add( path );
	}
	
	/**
	 * Gets the parent path of <code>this</code>.
	 * @return the parent path, can be <code>null</code>
	 */
	public DocPath getParent(){
		return parent;
	}
	
	/**
	 * Gets the root of the documentation.
	 * @return the root
	 */
	public DocRoot getRoot(){
		return property.getRoot();
	}
	
	/**
	 * Gets the documentation of this path.
	 * @return the documentation
	 */
	public DocText getDescription(){
		return new DocText( getRoot(), path.description() ); 
	}
	
	/**
	 * Gets the sorted nodes, that appear exactly one time on the path.
	 * @return the sorted nodes
	 */
	public Iterable<DocNode> getNodes(){
		return nodes;
	}
	
	/**
	 * Gets the nodes whose order and number of appearance is unknown.
	 * @return the unordered nodes
	 */
	public Iterable<DocNode> getUnordered(){
		return unordered;
	}
	
	private void add( CssDocPath path ){
		String refId = path.referenceId();
		if( !refId.isEmpty() ){
			add( getPathFor( refId, path.referencePath() ));
		}
		else{
			unpack( path );
		}
	}
	
	private void unpack( CssDocPath path ){
		this.path = path;
		
		for( CssDocPathNode node : path.nodes() ){
			add( nodes, node );
		}
		for( CssDocPathNode node : path.unordered() ){
			add( unordered, node );
		}
		
		String parentId = path.parentId();
		if( !parentId.isEmpty() ){
			parent = new DocPath( property, getPathFor( parentId, path.parentClass() ));
		}
	}
	
	private CssDocPath getPathFor( String refId, Class<?> refClass ){
		CssDocPath path;
		
		if( refClass == Object.class ){
			path = property.getClazz().getPath( refId );
		}
		else{
			path = property.getRoot().get( refClass ).getPath( refId );
		}
		if( path == null ){
			throw new IllegalArgumentException( "unknown ref: " + refId + " " + refClass );
		}
		return path;
	}
	
	private void add( Collection<DocNode> target, CssDocPathNode node ){
		Class<?> refClass = node.reference();
		if( refClass == Object.class ){
			target.add( new DocNode( this, node ));
		}
		else{
			CssDocPathNode replacement = refClass.getAnnotation( CssDocPathNode.class );
			if( replacement == null ){
				throw new IllegalArgumentException( "referenced " + refClass + " does not have any css-doc-path-nodes" );
			}
			add( target, replacement );
		}
	}
}
