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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * One element in a {@link CssPath}.
 * @author Benjamin Sigg
 */
public class CssPathNode {
	/**
	 * Returns a new {@link Builder} for creating a new {@link CssPathNode}.
	 * @param name the name of the element
	 * @return the new builder
	 */
	public static Builder node( String name ){
		return new Builder( name );
	}
	
	private String name;
	
	private String identifier;
	
	private Set<String> classes;
	
	private Set<String> pseudoClasses;
	
	private Map<String, String> properties;
	
	private CssPathNode( String name, String identifier, Set<String> classes, Set<String> pseudoClasses, Map<String, String> properties ){
		this.name = name;
		this.identifier = identifier;
		this.classes = classes;
		this.pseudoClasses = pseudoClasses;
		this.properties = properties;
	}
	
	/**
	 * Gets the name of this element.
	 * @return the name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Gets the identifier of this element.
	 * @return the identifier, can be <code>null</code>
	 */
	public String getIdentifier(){
		return identifier;
	}
	
	/**
	 * Tells whether this node has the class <code>className</code>
	 * @param className the name of a class
	 * @return whether this node has <code>className</code>
	 */
	public boolean hasClass( String className ){
		if( classes == null ){
			return false;
		}
		return classes.contains( className );
	}
	
	/**
	 * Tells whether this node has the pseudo-class <code>pseudoClass</code>.
	 * @param pseudoClass the name of a pseudo-class
	 * @return whether this node has <code>pseudoClass</code>
	 */
	public boolean hasPseudoClass( String pseudoClass ){
		if( pseudoClasses == null ){
			return false;
		}
		return pseudoClass.contains( pseudoClass );
	}

	/**
	 * Gets a property of this element that can be used by the {@link CssSelector}
	 * to filter items. The item is completely free when choosing which properties
	 * exist and what values they have. The properties of {@link CssItem#getProperty(String)} may 
	 * or may not have a representation as property.
	 * @param key the key of a property
	 * @return the value of the property or <code>null</code> if not present
	 */
	public String getProperty( String key ){
		if( properties == null ){
			return null;
		}
		return properties.get( key );
	}
	
	public static class Builder{
		private String name;
		private String identifier;
		private Set<String> classes;
		private Set<String> pseudoClasses;
		private Map<String, String> properties;
		
		private Builder( String name ){
			this.name = name;
		}
		
		/**
		 * Sets the identifier of the element.
		 * @param identifier the identifier
		 * @return <code>this</code>
		 */
		public Builder identifier( String identifier ){
			this.identifier = identifier;
			return this;
		}
		
		/**
		 * Adds a class to the element.
		 * @param className the name of the class
		 * @return <code>this</code>
		 */
		public Builder clazz( String className ){
			if( className == null ){
				throw new IllegalArgumentException( "className must not be null" );
			}
			if( classes == null ){
				classes = new HashSet<String>();
			}
			classes.add( className );
			return this;
		}
		
		/**
		 * Adds a pseudo-class to the element.
		 * @param className the name of the class
		 * @return <code>this</code>
		 */
		public Builder pseudo( String className ){
			if( className == null ){
				throw new IllegalArgumentException( "className must not be null" );
			}
			if( pseudoClasses == null ){
				pseudoClasses = new HashSet<String>();
			}
			pseudoClasses.add( className );
			return this;
		}
		
		/**
		 * Adds a property to the element.
		 * @param key the name of the property
		 * @param value the value of the property
		 * @return <code>this</code>
		 */
		public Builder property( String key, String value ){
			if( key == null ){
				throw new IllegalArgumentException( "key must not be null" );
			}
			if( properties == null ){
				properties = new HashMap<String, String>();
			}
			properties.put( key, value );
			return this;
		}
		
		/**
		 * Creates the new {@link CssPathNode}.
		 * @return a new node
		 */
		public CssPathNode build(){
			return new CssPathNode( name, identifier, classes, pseudoClasses, properties );
		}
	}
}
