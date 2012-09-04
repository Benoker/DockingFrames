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
 * A part of a {@link CssPath}. A {@link CssNode} is mutable, its parent
 * {@link CssPath}s should monitor it with a {@link CssNodeListener}.<br>
 * In general nodes have to be optimized for use with attached {@link CssNodeListener}s.
 * @author Benjamin Sigg
 */
public interface CssNode {
	
	/**
	 * Gets the name of this node.
	 * @return the name, not <code>null</code>
	 */
	public String getName();
	
	/**
	 * Gets the identifier of this node.
	 * @return the identifier, may be <code>null</code>
	 */
	public String getIdentifier();

	/**
	 * Tells whether this node has the class <code>className</code>
	 * @param className the name of a class
	 * @return whether this node has <code>className</code>
	 */
	public boolean hasClass( String className );
	
	/**
	 * Tells whether this node has the pseudo-class <code>pseudoClass</code>.
	 * @param pseudoClass the name of a pseudo-class
	 * @return whether this node has <code>pseudoClass</code>
	 */
	public boolean hasPseudoClass( String className );
	
	/**
	 * Gets a property of this element that can be used by the {@link CssSelector}
	 * to filter items. The node is completely free when choosing which properties
	 * exist and what values they have. The properties of {@link CssItem#getProperty(String)} may 
	 * or may not have a representation as property.
	 * @param key the key of a property
	 * @return the value of the property or <code>null</code> if not present
	 */
	public String getProperty( String key );
	
	/**
	 * Adds the observer <code>listener</code> to this node.
	 * @param listener the new listener, not <code>null</code>
	 */
	public void addNodeListener( CssNodeListener listener );
	
	/**
	 * Removes the observer <code>listener</code> from this node.
	 * @param listener the listener to remove
	 */
	public void removeNodeListener( CssNodeListener listener );
}
