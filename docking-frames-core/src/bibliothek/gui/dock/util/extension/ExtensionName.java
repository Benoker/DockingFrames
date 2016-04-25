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
package bibliothek.gui.dock.util.extension;

import java.util.HashMap;
import java.util.Map;

import bibliothek.util.Path;

/**
 * The name of some extension that can be loaded through {@link Extension}. There is no clear
 * definition of which class uses extension at which time. At the moment the best way to find 
 * out is to search all occurences of this class in the code.<br>
 * New extensions will be added to the framework when needed or upon request. 
 * @author Benjamin Sigg
 * @param <E> the type of this extension
 */
public class ExtensionName<E> {
	private Map<String, Object> parameters;
	private Path name;
	private Class<E> type;
	
	/**
	 * Creates a new name.
	 * @param name the unique identifier of this extension
	 * @param type the type of this extension
	 */
	public ExtensionName( Path name, Class<E> type ){
		this( name, type, null );
	}
	
	/**
	 * Creates a new name.
	 * @param name the unique identifier of this extension
	 * @param type the type of this extension
	 * @param parameters all the parameters for this extension, can be <code>null</code>
	 */
	public ExtensionName( Path name, Class<E> type, Map<String, Object> parameters ){
		if( name == null ){
			throw new IllegalArgumentException( "name must not be null" );
		}
		if( type == null ){
			throw new IllegalArgumentException( "type must not be null" );
		}
		this.name = name;
		this.type = type;
		this.parameters = parameters;
	}
	
	/**
	 * Creates a new name.
	 * @param name the unique identifier of this extension
	 * @param type the type of this extension
	 * @param parameterKey the key of the only parameter of this {@link ExtensionName}
	 * @param parameterValue the value of the only parameter of this {@link ExtensionName}
	 */
	public ExtensionName( Path name, Class<E> type, String parameterKey, Object parameterValue ){
		this( name, type, null );
		
		parameters = new HashMap<String, Object>();
		parameters.put( parameterKey, parameterValue );
	}
		
	/**
	 * Gets the unique name of this extension.
	 * @return the name, not <code>null</code>
	 */
	public Path getName(){
		return name;
	}
	
	/**
	 * Gets the type of this extension.
	 * @return the type, not <code>null</code>
	 */
	public Class<E> getType(){
		return type;
	}
	
	/**
	 * Gets an additional parameter of this extension. The name, type and meaning of the
	 * parameter depends on the extensions name.
	 * @param parameter the name of the parameter
	 * @return the value, can be <code>null</code>
	 */
	public Object get( String parameter ){
		return parameter == null ? null : parameters.get( parameter );
	}

	@Override
	public String toString(){
		return "ExtensionName[name=" + name + ", type=" + type + ", parameters=" + parameters + "]";
	}
	
	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals( Object obj ){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExtensionName other = (ExtensionName) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals( other.name ))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals( other.parameters ))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals( other.type ))
			return false;
		return true;
	}
	
	
}
