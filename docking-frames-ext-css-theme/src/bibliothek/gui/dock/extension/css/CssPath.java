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
package bibliothek.gui.dock.extension.css;


/**
 * Represents the path of a {@link CssItem}.
 * @author Benjamin Sigg
 */
public class CssPath {
	private CssPathNode[] nodes;
	
	/**
	 * Creates a new path
	 * @param nodes the elements of the path
	 */
	public CssPath( CssPathNode[] nodes ){
		if( nodes == null || nodes.length == 0 ){
			throw new IllegalArgumentException( "nodes must not be null and contain at least one item" );
		}
		this.nodes = nodes;
	}
	
	/**
	 * Gets the number of elements in this path.
	 * @return the number of elements
	 */
	public int getSize(){
		return nodes.length;
	}
	
	/**
	 * Gets the <code>index</code>'th element on this path.
	 * @param index the index of the element
	 * @return the element
	 */
	public CssPathNode getNode( int index ){
		return nodes[ index ];
	}
}
