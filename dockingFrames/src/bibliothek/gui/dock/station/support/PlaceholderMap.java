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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XException;

/**
 * A data structure designed to store and retrieve placeholder information
 * persistently.
 * @author Benjamin Sigg
 */
public class PlaceholderMap {
	/** version of the format */
	private int version;
	/** what kind of data is stored in this map */
	private Path format;
	
	/** all the data that is stored in this map */
	private Map<Path, Map<String, Object>> data = new LinkedHashMap<Path, Map<String,Object>>();
	
	/**
	 * Creates a new map.
	 * @param format the kind of data stored in this map, the exact meaning depends on the client
	 * that is using this map
	 * @param version the version of the format, the exact meaning depends on the client
	 * that is using this map
	 */
	public PlaceholderMap( Path format, int version ){
		if( format == null ){
			throw new IllegalArgumentException( "format must not be null" );
		}
		this.format = format;
		this.version = version;
	}
	
	/**
	 * Creates a new map reading the content of the map directly from <code>in</code>.
	 * @param in the content
	 * @throws IOException in case of an I/O error
	 */
	public PlaceholderMap( DataInputStream in ) throws IOException{
		Version version = Version.read( in );
		if( version.compareTo( Version.VERSION_1_0_8 ) != 0 ){
			throw new IOException( "unknown version: " + version );
		}
		
		this.version = in.readInt();
		format = new Path( in.readUTF() );
	
		int size = in.readInt();
		
		for( int i = 0; i < size; i++ ){
			Path placeholder = new Path( in.readUTF() );
			add(placeholder);
			Map<String, Object> map = data.get( placeholder );
			int length = in.readInt();
			for( int j = 0; j < length; j++ ){
				String key = in.readUTF();
				Object value = read( in );
				map.put( key, value );
			}
		}
	}
	
	/**
	 * Creates a new map reading the content of the map directly from <code>in</code>.
	 * @param in the content to read
	 */
	public PlaceholderMap( XElement in ){
		XElement xversion = in.getElement( "version" );
		if( xversion == null ){
			throw new XException( "missing element 'version'" );
		}
		version = xversion.getInt();
		
		XElement xformat = in.getElement( "format" );
		if( xformat == null ){
			throw new XException( "missing element 'format'" );
		}
		format = new Path( xformat.getString() );
		
		for( int i = 0, n = in.getElementCount(); i<n; i++ ){
			XElement xentry = in.getElement( i );
			if( xentry.getName().equals( "entry" )){
				Path placeholder = new Path( xentry.getString( "placeholder" ));
				add( placeholder );
				Map<String,Object> map = data.get( placeholder );
				for( int j = 0, m = xentry.getElementCount(); j<m; j++ ){
					XElement xitem = xentry.getElement( j );
					if( xitem.getName().equals( "item" )){
						String key = xitem.getString( "key" );
						Object value = read( xitem );
						map.put( key, value );
					}
				}
			}
		}
	}
	
	/**
	 * Writes the contents of this map into <code>out</code>.
	 * @param out the stream to write into
	 * @throws IOException in case of an I/O error
	 */
	public void write( DataOutputStream out ) throws IOException{
		Version.write( out, Version.VERSION_1_0_8 );
		out.writeInt( version );
		out.writeUTF( format.toString() );
		
		out.writeInt( data.size() );
		for( Map.Entry<Path, Map<String, Object>> entry : data.entrySet() ){
			out.writeUTF( entry.getKey().toString() );
			Map<String, Object> map = entry.getValue();
			out.writeInt( map.size() );
			for( Map.Entry<String, Object> mapEntry : map.entrySet() ){
				out.writeUTF( mapEntry.getKey() );
				write( mapEntry.getValue(), out );
			}
		}
	}

	private void write( Object value, DataOutputStream out ) throws IOException{
		if( value instanceof String ){
			out.writeByte( 0 );
			out.writeUTF( (String)value );
		}
		else if( value instanceof Integer ){
			out.writeByte( 1 );
			out.writeInt( (Integer)value );
		}
		else if( value instanceof Long ){
			out.writeByte( 2 );
			out.writeLong( (Long)value );
		}
		else if( value instanceof Double ){
			out.writeByte( 3 );
			out.writeDouble( (Double)value );
		}
		else if( value instanceof Boolean ){
			out.writeByte( 4 );
			out.writeBoolean( (Boolean)value );
		}
		else if( value instanceof PlaceholderMap ){
			out.writeByte( 5 );
			((PlaceholderMap)value).write( out );
		}
		else if( value instanceof Object[] ){
			out.writeByte( 6 );
			Object[] array = (Object[])value;
			out.writeInt( array.length );
			for( Object item : array ){
				write( item, out );
			}
		}
		else{
			throw new IOException( "unknown type: " + value.getClass() );
		}
	}

	private Object read( DataInputStream in ) throws IOException{
		byte kind = in.readByte();
		switch( kind ){
			case 0: return in.readUTF();
			case 1: return in.readInt();
			case 2: return in.readLong();
			case 3: return in.readDouble();
			case 4: return in.readBoolean();
			case 5: return new PlaceholderMap( in );
			case 6:
				int length = in.readInt();
				Object[] result = new Object[length];
				for( int i = 0; i < length; i++ ){
					result[i] = read(in);
				}
				return result;
		}
		throw new IOException( "illegal format" );
	}
	
	/**
	 * Writes the contents of this map into <code>out</code>.
	 * @param out the element to fill, its attributes will not be modified
	 */
	public void write( XElement out ){
		out.addElement( "version" ).setInt( version );
		out.addElement( "format" ).setString( format.toString() );
		
		for( Map.Entry<Path, Map<String, Object>> entry : data.entrySet() ){
			XElement xplaceholder = out.addElement( "entry" );
			xplaceholder.addString( "placeholder", entry.getKey().toString() );
			Map<String, Object> map = entry.getValue();
			for( Map.Entry<String, Object> mapEntry : map.entrySet() ){
				XElement xitem = xplaceholder.addElement( "item" );
				xitem.addString( "key", mapEntry.getKey() );
				write( mapEntry.getValue(), xitem );
			}
		}
	}
	
	private void write( Object value, XElement out ){
		if( value instanceof String ){
			out.addString( "type", "s" );
			out.setString( (String)value );
		}
		else if( value instanceof Integer ){
			out.addString( "type", "i" );
			out.setInt( (Integer)value );
		}
		else if( value instanceof Long ){
			out.addString( "type", "l" );
			out.setLong( (Long)value );
		}
		else if( value instanceof Double ){
			out.addString( "type", "d" );
			out.setDouble( (Double)value );
		}
		else if( value instanceof Boolean ){
			out.addString( "type", "b" );
			out.setBoolean( (Boolean)value );
		}
		else if( value instanceof PlaceholderMap ){
			out.addString( "type", "p" );
			((PlaceholderMap)value).write( out );
		}
		else if( value instanceof Object[] ){
			out.addString( "type", "a" );
			Object[] array = (Object[])value;
			for( Object item : array ){
				write( item, out.addElement( "item" ) );
			}
		}
		else{
			throw new XException( "unknown type: " + value.getClass() );
		}
	}

	private Object read( XElement in ){
		String type = in.getString( "type" );
		if( "s".equals( type )){
			return in.getString();
		}
		if( "i".equals( type )){
			return in.getInt();
		}
		if( "l".equals( type )){
			return in.getLong();
		}
		if( "d".equals( type )){
			return in.getDouble();
		}
		if( "b".equals( type )){
			return in.getBoolean();
		}
		if( "p".equals( type )){
			return new PlaceholderMap( in );
		}
		if( "a".equals( type )){
			XElement[] xitems = in.getElements( "item" );
			Object[] result = new Object[xitems.length];
			for( int i = 0; i < xitems.length; i++ ){
				result[i] = read( xitems[i] );
			}
			return result;
		}
		else{
			throw new XException( "unknown type: " + type );
		}
	}
		
	/**
	 * Gets the version of the format used in this map.
	 * @return the version, its meaning depends on {@link #getFormat() the format}
	 */
	public int getVersion(){
		return version;
	}
	
	/**
	 * Gets the format of this map, the meaning of the format depends on the client.
	 * @return the format, not <code>null</code>
	 */
	public Path getFormat(){
		return format;
	}
	
	/**
	 * Adds the placeholder <code>placeholder</code> to this map. Nothing happens
	 * if <code>placeholder</code> is already in this map.
	 * @param placeholder the new placeholder, not <code>null</code>
	 */
	public void add( Path placeholder ){
		if( placeholder == null ){
			throw new IllegalArgumentException( "placeholder must not be null" );
		}
		Map<String, Object> map = data.get( placeholder );
		if( map == null ){
			map = new LinkedHashMap<String, Object>();
			data.put( placeholder, map );
		}
	}
	
	/**
	 * Removes <code>placeholder</code> and all information that is associated with
	 * <code>placeholder</code> from this map.
	 * @param placeholder the placeholder to clear
	 */
	public void remove( Path placeholder ){                                                
		data.remove( placeholder );
	}
	
	/**
	 * Gets all placeholders that are known to this map in the order
	 * they were {@link #add(Path) added} to this map.
	 * @return all placeholders
	 */
	public Path[] getPlaceholders(){
		Set<Path> set = data.keySet();
		return set.toArray( new Path[ set.size() ] );
	}
	
	/**
	 * Gets all keys that are in use for <code>placeholder</code> in the order
	 * they were first used to put an object into this map.
	 * @param placeholder some placeholder
	 * @return the associated keys, <code>null</code> if <code>placeholder</code> is not
	 * known to this map
	 */
	public String[] getKeys( Path placeholder ){
		Map<String,Object> map = data.get( placeholder );
		if( map == null ){
			return null;
		}
		Set<String> set = map.keySet();
		return set.toArray( new String[ set.size() ] );
	}
	
	/**
	 * Stores the value <code>value</code> in this map, {@link #add(Path) adds}
	 * <code>placeholder</code> if necessary, overrides the value stored at
	 * <code>key</code> if existent.
	 * @param placeholder the placeholder for which <code>value</code> is stored
	 * @param key the unique identifier of the value
	 * @param value the new value, not <code>null</code>
	 */
	public void putString( Path placeholder, String key, String value ){
		put( placeholder, key, value );
	}
	
	/**
	 * Stores the value <code>value</code> in this map, {@link #add(Path) adds}
	 * <code>placeholder</code> if necessary, overrides the value stored at
	 * <code>key</code> if existent.
	 * @param placeholder the placeholder for which <code>value</code> is stored
	 * @param key the unique identifier of the value
	 * @param value the new value
	 */
	public void putInt( Path placeholder, String key, int value ){
		put( placeholder, key, Integer.valueOf( value ) );
	}
	
	/**
	 * Stores the value <code>value</code> in this map, {@link #add(Path) adds}
	 * <code>placeholder</code> if necessary, overrides the value stored at
	 * <code>key</code> if existent.
	 * @param placeholder the placeholder for which <code>value</code> is stored
	 * @param key the unique identifier of the value
	 * @param value the new value
	 */
	public void putLong( Path placeholder, String key, long value ){
		put( placeholder, key, Long.valueOf( value ) );
	}
	
	/**
	 * Stores the value <code>value</code> in this map, {@link #add(Path) adds}
	 * <code>placeholder</code> if necessary, overrides the value stored at
	 * <code>key</code> if existent.
	 * @param placeholder the placeholder for which <code>value</code> is stored
	 * @param key the unique identifier of the value
	 * @param value the new value
	 */
	public void putBoolean( Path placeholder, String key, boolean value ){
		put( placeholder, key, Boolean.valueOf( value ) );
	}
	
	/**
	 * Stores the value <code>value</code> in this map, {@link #add(Path) adds}
	 * <code>placeholder</code> if necessary, overrides the value stored at
	 * <code>key</code> if existent.
	 * @param placeholder the placeholder for which <code>value</code> is stored
	 * @param key the unique identifier of the value
	 * @param value the new value
	 */
	public void putDouble( Path placeholder, String key, double value ){
		put( placeholder, key, Double.valueOf( value ) );
	}
	
	/**
	 * Stores the value <code>value</code> in this map, {@link #add(Path) adds}
	 * <code>placeholder</code> if necessary, overrides the value stored at
	 * <code>key</code> if existent.
	 * @param placeholder the placeholder for which <code>value</code> is stored
	 * @param key the unique identifier of the value
	 * @param value the new value, not <code>null</code>
	 */
	public void putMap( Path placeholder, String key, PlaceholderMap value ){
		put( placeholder, key, value );
	}
	
	/**
	 * Stores the value <code>value</code> in this map, {@link #add(Path) adds}
	 * <code>placeholder</code> if necessary, overrides the value stored at
	 * <code>key</code> if existent.
	 * @param placeholder the placeholder for which <code>value</code> is stored
	 * @param key the unique identifier of the value
	 * @param value the new value, not <code>null</code>. May contain any data type
	 * for which this map offers a put-method.
	 */
	public void putArray( Path placeholder, String key, Object[] value ){
		put( placeholder, key, value );
	}
	
	/**
	 * Stores the value <code>value</code> in this map, {@link #add(Path) adds}
	 * <code>placeholder</code> if necessary, overrides the value stored at
	 * <code>key</code> if existent.<br>
	 * It is the clients responsibility not to introduce cycles of object references.
	 * @param placeholder the placeholder for which <code>value</code> is stored
	 * @param key the unique identifier of the value
	 * @param value the new value, not <code>null</code>
	 * @throws IllegalArgumentException if <code>value</code> is neither
	 * {@link String}, {@link Integer}, {@link Long}, {@link Double}, {@link Boolean}
	 * {@link PlaceholderMap} nor an array of {@link Object}s containing the mentioned data types
	 */	
	public void put( Path placeholder, String key, Object value ){
		Object invalid = invalidType( value );
		
		if( invalid == null ){
			add( placeholder );
			data.get( placeholder ).put( key, value );
		}
		else{
			throw new IllegalArgumentException( "value of illegal type: " + (invalid instanceof String ? invalid : invalid.getClass() ));
		}
	}
	
	private Object invalidType( Object value ){
		if( value instanceof String ||
				value instanceof Integer ||
				value instanceof Long ||
				value instanceof Double ||
				value instanceof Boolean ||
				value instanceof PlaceholderMap ){
			return null;
		}
		if( value instanceof Object[] ){
			for( Object item : (Object[])value ){
				Object result = invalidType( item );
				if( result != null ){
					return result;
				}
			}
		}
		if( value == null ){
			return "null";
		}
		return null;
	}
	
	/**
	 * Removes the value that is stored at <code>key</code>. Note that this
	 * method will not remove <code>placeholder</code> from this map even
	 * if there is no data left associated with <code>placeholder</code>.
	 * @param placeholder the placeholder in whose realm the data is deleted
	 * @param key the unique identifier of the removed data
	 * @return the data that was removed, may be <code>null</code>
	 */
	public Object remove( Path placeholder, String key ){
		Map<String, Object> map = data.get( placeholder );
		if( map == null ){
			return null;
		}
		return map.remove( key );
	}
	
	/**
	 * Tells whether <code>key</code> exists for <code>placeholder</code>.
	 * @param placeholder the placeholder in whose realm <code>key</code> should exist
	 * @param key the key to search
	 * @return <code>true</code> if there is some data stored
	 */
	public boolean contains( Path placeholder, String key ){
		return get( placeholder, key ) != null;
	}
	
	/**
	 * Gets the string that is stored under <code>key</code>
	 * @param placeholder the placeholder in whose realm to search
	 * @param key the unique identifier of the searched data
	 * @return the data, not <code>null</code>
	 * @throws IllegalArgumentException if either the value is of the wrong type or missing
	 */
	public String getString( Path placeholder, String key ){
		Object data = get( placeholder, key );
		if( data instanceof String ){
			return (String)data;
		}
		else{
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Gets the integer that is stored under <code>key</code>
	 * @param placeholder the placeholder in whose realm to search
	 * @param key the unique identifier of the searched data
	 * @return the data, not <code>null</code>
	 * @throws IllegalArgumentException if either the value is of the wrong type or missing
	 */
	public int getInt( Path placeholder, String key ){
		Object data = get( placeholder, key );
		if( data instanceof Integer ){
			return (Integer)data;
		}
		else{
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Gets the long that is stored under <code>key</code>
	 * @param placeholder the placeholder in whose realm to search
	 * @param key the unique identifier of the searched data
	 * @return the data, not <code>null</code>
	 * @throws IllegalArgumentException if either the value is of the wrong type or missing
	 */
	public long getLong( Path placeholder, String key ){
		Object data = get( placeholder, key );
		if( data instanceof Long ){
			return (Long)data;
		}
		else{
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Gets the boolean that is stored under <code>key</code>
	 * @param placeholder the placeholder in whose realm to search
	 * @param key the unique identifier of the searched data
	 * @return the data, not <code>null</code>
	 * @throws IllegalArgumentException if either the value is of the wrong type or missing
	 */
	public boolean getBoolean( Path placeholder, String key ){
		Object data = get( placeholder, key );
		if( data instanceof Boolean ){
			return (Boolean)data;
		}
		else{
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Gets the double that is stored under <code>key</code>
	 * @param placeholder the placeholder in whose realm to search
	 * @param key the unique identifier of the searched data
	 * @return the data, not <code>null</code>
	 * @throws IllegalArgumentException if either the value is of the wrong type or missing
	 */
	public double getDouble( Path placeholder, String key ){
		Object data = get( placeholder, key );
		if( data instanceof Double ){
			return (Double)data;
		}
		else{
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Gets the map that is stored under <code>key</code>
	 * @param placeholder the placeholder in whose realm to search
	 * @param key the unique identifier of the searched data
	 * @return the data, not <code>null</code>
	 * @throws IllegalArgumentException if either the value is of the wrong type or missing
	 */
	public PlaceholderMap getMap( Path placeholder, String key ){
		Object data = get( placeholder, key );
		if( data instanceof PlaceholderMap ){
			return (PlaceholderMap)data;
		}
		else{
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Gets the map that is stored under <code>key</code>
	 * @param placeholder the placeholder in whose realm to search
	 * @param key the unique identifier of the searched data
	 * @return the data, not <code>null</code>
	 * @throws IllegalArgumentException if either the value is of the wrong type or missing
	 */
	public Object[] getArray( Path placeholder, String key ){
		Object data = get( placeholder, key );
		if( data instanceof Object[] ){
			return (Object[])data;
		}
		else{
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Gets the data that is stored under <code>key</code>.
	 * @param placeholder the realm in which to search
	 * @param key the key of the data
	 * @return the data, may be <code>null</code>
	 */
	public Object get( Path placeholder, String key ){
		Map<String, Object> map = data.get( placeholder );
		if( map == null ){
			return null;
		}
		return map.get( key );
	}
}
