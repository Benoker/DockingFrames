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
package bibliothek.gui.dock.station.support;

import java.util.HashMap;
import java.util.Map;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.Dockable;

/**
 * Data about a {@link Dockable} that was stored in a {@link PlaceholderList}.
 * @author Benjamin Sigg
 */
public class ConvertedPlaceholderListItem {
	/** a placeholder to use for this item */
	private Path placeholder;
	/** additional information about this item */
	private PlaceholderMap map;
	/** additional information about this item */
	private Map<String, Object> data;
	
	/**
	 * Associates a placeholder with this item. A {@link PlaceholderList} will insert
	 * this placeholder into its set of placeholders.
	 * @param placeholder the placeholder, can be <code>null</code>
	 */
	public void setPlaceholder( Path placeholder ){
		this.placeholder = placeholder;
	}
	
	/**
	 * Gets the placeholder that is associated with this item.
	 * @return the placeholder, can be <code>null</code>
	 * @see #setPlaceholder(Path)
	 */
	public Path getPlaceholder(){
		return placeholder;
	}
	
	/**
	 * Associates a map of data with this item. Notice that keys for this
	 * map must be valid placeholders. Clients may need to use the empty key
	 * (calling {@link PlaceholderMap#newKey(Path...)} with no arguments).<br>
	 * The map will be ignored if there is already a map associated with this item
	 * by the list itself.
	 * @param map the data, can be <code>null</code>
	 */
	public void setPlaceholderMap( PlaceholderMap map ){
		this.map = map;
	}
	
	/**
	 * Gets additional information about this item.
	 * @return additional information, can be <code>null</code>
	 * @see #setPlaceholderMap(PlaceholderMap)
	 */
	public PlaceholderMap getPlaceholderMap(){
		return map;
	}

	/**
	 * Stores an additional key-value pair.
	 * @param key the key
	 * @param value the value
	 */
	public void putInteger( String key, int value ){
		put( key, value );
	}
	
	/**
	 * Stores an additional key-value pair.
	 * @param key the key
	 * @param value the value
	 */	
	public void putLong( String key, long value ){
		put( key, value );
	}
	
	/**
	 * Stores an additional key-value pair.
	 * @param key the key
	 * @param value the value
	 */
	public void putValue( String key, double value ){
		put( key, value );
	}
	
	/**
	 * Stores an additional key-value pair.
	 * @param key the key
	 * @param value the value
	 */
	public void putBoolean( String key, boolean value ){
		put( key, value );
	}
	
	/**
	 * Stores an additional key-value pair.
	 * @param key the key
	 * @param value the value
	 */
	public void putString( String key, String value ){
		put( key, value );
	}

	/**
	 * Stores an additional key-value pair.
	 * @param key the key
	 * @param value the value
	 */
	public void put( String key, Object value ){
		if( data == null ){
			data = new HashMap<String, Object>();
		}
		if( value == null ){
			throw new IllegalArgumentException( "value must not be null" );
		}
		
		if( value instanceof Integer ||
				value instanceof Long ||
				value instanceof Double ||
				value instanceof Boolean ||
				value instanceof String ){
		
			data.put( key, value );
		}
		else{
			throw new IllegalArgumentException( "not a valid type to put: " + value.getClass() );
		}
	}

	/**
	 * Gets the keys of all the data that is stored.
	 * @return the keys, not <code>null</code>
	 */
	public String[] keys(){
		if( data == null ){
			return new String[]{};
		}
		
		return data.keySet().toArray( new String[data.size()] );
	}

	/**
	 * Gets the data that is stored for key <code>key</code>.
	 * @param key the key of some entry
	 * @return the data
	 * @throws IllegalArgumentException if there is nothing stored for <code>key</code> or if the
	 * stored object has the wrong type
	 */
	public String getString( String key ){
		Object data = get( key );
		if( !(data instanceof String)){
			throw new IllegalArgumentException( "not a string: " + key );
		}
		return (String)data;
	}
	
	/**
	 * Gets the data that is stored for key <code>key</code>.
	 * @param key the key of some entry
	 * @return the data
	 * @throws IllegalArgumentException if there is nothing stored for <code>key</code> or if the
	 * stored object has the wrong type
	 */
	public int getInt( String key ){
		Object data = get( key );
		if( !(data instanceof Integer)){
			throw new IllegalArgumentException( "not an integer: " + key );
		}
		return (Integer)data;
	}
	
	/**
	 * Gets the data that is stored for key <code>key</code>.
	 * @param key the key of some entry
	 * @return the data
	 * @throws IllegalArgumentException if there is nothing stored for <code>key</code> or if the
	 * stored object has the wrong type
	 */
	public long getLong( String key ){
		Object data = get( key );
		if( !(data instanceof Long)){
			throw new IllegalArgumentException( "not a long: " + key );
		}
		return (Long)data;
	}
	
	/**
	 * Gets the data that is stored for key <code>key</code>.
	 * @param key the key of some entry
	 * @return the data
	 * @throws IllegalArgumentException if there is nothing stored for <code>key</code> or if the
	 * stored object has the wrong type
	 */
	public double getDouble( String key ){
		Object data = get( key );
		if( !(data instanceof Double)){
			throw new IllegalArgumentException( "not a double: " + key );
		}
		return (Double)data;
	}
	
	/**
	 * Gets the data that is stored for key <code>key</code>.
	 * @param key the key of some entry
	 * @return the data
	 * @throws IllegalArgumentException if there is nothing stored for <code>key</code> or if the
	 * stored object has the wrong type
	 */
	public boolean getBoolean( String key ){
		Object data = get( key );
		if( !(data instanceof Boolean)){
			throw new IllegalArgumentException( "not a boolean: " + key );
		}
		return (Boolean)data;
	}
	
	/**
	 * Gets the data that is stored for key <code>key</code>.
	 * @param key the key of some entry
	 * @return the data, can be <code>null</code>
	 */
	public Object get( String key ){
		if( data == null ){
			return null;
		}
		return data.get( key );
	}
	
	/**
	 * Tells whether some data is stored for <code>key</code>.
	 * @param key the key to search
	 * @return <code>true</code> if data exists, <code>false</code> otherwise
	 */
	public boolean contains( String key ){
		return get( key ) != null;
	}
}
