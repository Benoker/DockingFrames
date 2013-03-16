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


/**
 * Represents a single node in a {@link DocPath}.
 * @author Benjamin Sigg
 */
public class DocNode {
	/** the path to which this node belongs */
	private DocPath path;
	
	/** the original annotation */
	private CssDocPathNode node;
	
	/**
	 * Creates a new node.
	 * @param path the path to which this node belongs
	 * @param node the actual node
	 */
	public DocNode( DocPath path, CssDocPathNode node ){
		this.path = path;
		this.node = node;
	}
	
	/**
	 * Gets the root of the documentation.
	 * @return the root
	 */
	public DocRoot getRoot(){
		return path.getRoot();
	}
	
	/**
	 * Gets a description of this node.
	 * @return a description
	 */
	public DocText getDescription(){
		return new DocText( getRoot(), node.description() );
	}
	
	/**
	 * Gets the name of this node.
	 * @return the name
	 */
	public DocKey getName(){
		return DocKey.only( getRoot(), node.name() );
	}
	
	/**
	 * Gets the identifier of this node.
	 * @return the identifier
	 */
	public DocText getIdentifier(){
		return new DocText( getRoot(), node.identifier() );
	}
	
	/**
	 * Gets all the classes that may be applied to this node.
	 * @return all the classes
	 */
	public DocKey[] getClasses(){
		return DocKey.of( getRoot(), node.classes() );
	}
	
	/**
	 * Gets all the pseudo classes that may be applied to this node.
	 * @return all the pseudo classes
	 */
	public DocKey[] getPseudoClasses(){
		return DocKey.of( getRoot(), node.pseudoClasses() );
	}
	
	/**
	 * Gets all the properties that may be found in this node.
	 * @return all the properties
	 */
	public DocKey[] getProperties(){
		return DocKey.of( getRoot(), node.properties() );
	}
}
