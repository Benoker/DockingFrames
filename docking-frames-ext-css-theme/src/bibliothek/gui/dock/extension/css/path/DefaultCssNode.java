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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.dock.extension.css.CssNode;

/**
 * This {@link CssNode} offers the possibility to set any property
 * from outside at any time.
 * @author Benjamin Sigg
 */
public class DefaultCssNode extends AbstractCssNode{
	private String name;
	private String identifier;
	private Map<String, String> properties;
	private Set<String> classes;
	private Set<String> pseudoClasses;

	/**
	 * Creates a new node
	 * @param name the name of this node, must not be <code>null</code>
	 */
	public DefaultCssNode( String name ){
		setName( name );
	}
	
	@Override
	public String getName(){
		return name;
	}
	
	@Override
	protected void bind(){
		// ignore
	}
	
	@Override
	protected void unbind(){
		// ignore	
	}
	
	/**
	 * Changes the name of this node.
	 * @param name the new name, not <code>null</code>
	 */
	public void setName( String name ){
		if( name == null || name.length() == 0 ){
			throw new IllegalArgumentException( "name must not be null nor must it have a length of 0" );
		}
		this.name = name;
		fireNodeChanged();
	}
	
	@Override
	public String getIdentifier(){
		return identifier;
	}
	
	/**
	 * Changes the identifier of this node.
	 * @param identifier the new identifier, can be <code>null</code>
	 */
	public void setIdentifier( String identifier ){
		this.identifier = identifier;
		fireNodeChanged();
	}
	
	@Override
	public String getProperty( String key ){
		if( properties == null ){
			return null;
		}
		return properties.get( key );
	}
	
	/**
	 * Changes a property of this node.
	 * @param key the name of the property
	 * @param value the new value, can be <code>null</code>
	 */
	public void putProperty( String key, String value ){
		if( value == null ){
			if( properties != null ){
				properties.remove( key );
				if( properties.isEmpty() ){
					properties = null;
				}
			}
		}
		else{
			if( properties == null ){
				properties = new HashMap<String, String>();
			}
			properties.put( key, value );
		}
		fireNodeChanged();
	}
	
	@Override
	public boolean hasClass( String className ){
		if( classes == null ){
			return false;
		}
		return classes.contains( className );
	}
	
	/**
	 * Adds a class to this node.
	 * @param className the name of the class
	 */
	public void addClass( String className ){
		if( classes == null ){
			classes = new HashSet<String>();
		}
		classes.add( className );
		fireNodeChanged();
	}
	
	/**
	 * Removes a class of this node.
	 * @param className the name of the class to remove
	 */
	public void removeClass( String className ){
		if( classes != null ){
			classes.remove( className );
			if( classes.isEmpty() ){
				classes = null;
			}
			fireNodeChanged();
		}
	}
	
	@Override
	public boolean hasPseudoClass( String className ){
		if( pseudoClasses == null ){
			return false;
		}
		return pseudoClasses.contains( className );
	}

	/**
	 * Adds a pseudo-class to this node.
	 * @param className the name of the class
	 */
	public void addPseudoClass( String className ){
		if( pseudoClasses == null ){
			pseudoClasses = new HashSet<String>();
		}
		pseudoClasses.add( className );
		fireNodeChanged();
	}
	
	/**
	 * Removes a pseudo-class of this node.
	 * @param className the name of the class to remove
	 */
	public void removePseudoClass( String className ){
		if( pseudoClasses != null ){
			pseudoClasses.remove( className );
			if( pseudoClasses.isEmpty() ){
				pseudoClasses = null;
			}
			fireNodeChanged();
		}
	}
}
