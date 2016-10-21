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
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import bibliothek.util.Path;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XException;

/**
 * A data structure designed to store and retrieve placeholder information
 * persistently. This data structure basically is a map with a restricted set of types
 * that are allowed to be stored. This map uses arrays of {@link Path}s as {@link Key}s, there are
 * two modes how to use the keys:
 * <ul>
 * 	<li>If using shared keys, this map will use the <code>equals</code> method to compare keys</li>
 *  <li>If using non-shared keys, this map will use the <code>==</code> operator to compare keys</li>
 * </ul>
 * This data structure can work together with a {@link PlaceholderStrategy} to automatically delete
 * entries that are no longer valid. 
 * @author Benjamin Sigg
 */
public class PlaceholderMap {
	/** version of the format */
	private int version;
	/** what kind of data is stored in this map */
	private Path format;
	
	/** all the data that is stored in this map */
	private Map<Key, Map<String, Object>> data = new LinkedHashMap<Key, Map<String,Object>>();
	
	/** strategy observed for automatically removal of invalid placeholders */
	private PlaceholderStrategy strategy;
	
	/** listener to {@link #strategy} */
	private PlaceholderStrategyListener listener = new PlaceholderStrategyListener() {
		public void placeholderInvalidated( Set<Path> placeholders ){
			removeAll( placeholders, false );	
		}
	};
	
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
	 * @param strategy guard to identify the placeholders which are allowed to be stored, can be <code>null</code>
	 * @throws IOException in case of an I/O error
	 */
	public PlaceholderMap( DataInputStream in, PlaceholderStrategy strategy ) throws IOException{
		setPlaceholderStrategy( strategy );
		
		Version version = Version.read( in );
		if( Version.VERSION_1_1_1a.compareTo( version ) < 0 ){
			throw new IOException( "unknown version: " + version );
		}
		
		this.version = in.readInt();
		format = new Path( in.readUTF() );
	
		int size = in.readInt();
		
		for( int i = 0; i < size; i++ ){
			PlaceholderKey key = new PlaceholderKey( in, version );
			key = key.shrink( strategy );
			
			if( key != null ){
				add(key);
				Map<String, Object> map = data.get( key );
				int length = in.readInt();
				for( int j = 0; j < length; j++ ){
					String subkey = in.readUTF();
					Object value = read( in, strategy );
					map.put( subkey, value );
				}
			}
			else{
				int length = in.readInt();
				for( int j = 0; j < length; j++ ){
					in.readUTF();
					read( in, strategy );
				}
			}
		}
	}
	
	/**
	 * Creates a new map reading the content of the map directly from <code>in</code>.
	 * @param in the content to read
	 * @param strategy guard to identify the placeholders which are allowed to be stored, can be <code>null</code> 
	 */
	public PlaceholderMap( XElement in, PlaceholderStrategy strategy ){
		setPlaceholderStrategy( strategy );
		
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
				PlaceholderKey placeholder = new PlaceholderKey( xentry.getElement( "key" ) );
				placeholder = placeholder.shrink( strategy );
				
				if( placeholder != null ){
					add( placeholder );
					Map<String,Object> map = data.get( placeholder );
					for( int j = 0, m = xentry.getElementCount(); j<m; j++ ){
						XElement xitem = xentry.getElement( j );
						if( xitem.getName().equals( "item" )){
							String key = xitem.getString( "key" );
							Object value = read( xitem, strategy );
							map.put( key, value );
						}
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
		Version.write( out, Version.VERSION_1_1_1a );
		out.writeInt( version );
		out.writeUTF( format.toString() );
		
		out.writeInt( data.size() );
		for( Map.Entry<Key, Map<String, Object>> entry : data.entrySet() ){
			((PlaceholderKey)entry.getKey()).write( out );
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
		else if( value instanceof Path ){
			out.writeByte( 7 );
			out.writeUTF( ((Path)value).toString() );
		}
		else{
			throw new IOException( "unknown type: " + value.getClass() );
		}
	}

	private Object read( DataInputStream in, PlaceholderStrategy strategy ) throws IOException{
		byte kind = in.readByte();
		switch( kind ){
			case 0: return in.readUTF();
			case 1: return in.readInt();
			case 2: return in.readLong();
			case 3: return in.readDouble();
			case 4: return in.readBoolean();
			case 5: return new PlaceholderMap( in, strategy );
			case 6:
				int length = in.readInt();
				Object[] result = new Object[length];
				for( int i = 0; i < length; i++ ){
					result[i] = read( in, strategy );
				}
				return result;
			case 7: return new Path( in.readUTF() );
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
		
		for( Map.Entry<Key, Map<String, Object>> entry : data.entrySet() ){
			XElement xplaceholder = out.addElement( "entry" );
			((PlaceholderKey)entry.getKey()).write( xplaceholder.addElement( "key" ) );
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
		else if( value instanceof Path ){
			out.addString( "type", "t" );
			out.setString( ((Path)value).toString() );
		}
		else{
			throw new XException( "unknown type: " + value.getClass() );
		}
	}

	private Object read( XElement in, PlaceholderStrategy strategy ){
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
			return new PlaceholderMap( in, strategy );
		}
		if( "a".equals( type )){
			XElement[] xitems = in.getElements( "item" );
			Object[] result = new Object[xitems.length];
			for( int i = 0; i < xitems.length; i++ ){
				result[i] = read( xitems[i], strategy );
			}
			return result;
		}
		if( "t".equals( type )){
			return new Path( in.getString() );
		}
		else{
			throw new XException( "unknown type: " + type );
		}
	}
	
	/**
	 * Creates a deep copy of this map.
	 * @return the copy, not <code>null</code>
	 */
	public PlaceholderMap copy(){
		PlaceholderMap result = new PlaceholderMap( format, version );
		
		for( Map.Entry<Key, Map<String, Object>> entry : data.entrySet() ){
			Key newKey = result.copyKey( entry.getKey() );
			result.add( newKey );
			Map<String, Object> map = result.data.get( newKey );
			for( Map.Entry<String, Object> valueEntry : entry.getValue().entrySet() ){
				map.put( valueEntry.getKey(), copy( valueEntry.getValue() ) );
			}
		}
		
		return result;
	}
	
	/**
	 * May return this or a copy of this {@link PlaceholderMap} to which <code>strategy</code> was applied. The
	 * strategy of the result may or may not be set. This map is not modified by this method.
	 * @param strategy the strategy to apply
	 * @return either this or a copy of this map
	 */
	public PlaceholderMap filter( PlaceholderStrategy strategy ){
		if( strategy == null || this.strategy == strategy ){
			return this;
		}
		PlaceholderMap copy = copy();
		copy.setPlaceholderStrategy( strategy );
		copy.setPlaceholderStrategy( null );
		return copy;
	}
	
	private Object copy( Object value ){
		if( value instanceof String ){
			return value;
		}
		else if( value instanceof Integer ){
			return value;
		}
		else if( value instanceof Long ){
			return value;
		}
		else if( value instanceof Double ){
			return value;
		}
		else if( value instanceof Boolean ){
			return value;
		}
		else if( value instanceof PlaceholderMap ){
			return ((PlaceholderMap)value).copy();
		}
		else if( value instanceof Object[] ){
			Object[] array = (Object[])value;
			Object[] copy = new Object[ array.length ];
			for( int i = 0; i < copy.length; i++ ){
				copy[i] = copy( array[i] );
			}
			return copy;
		}
		else{
			throw new IllegalArgumentException( "unknown type: " + value.getClass() );
		}
	}
	
	/**
	 * Creates a new shared key for any set of placeholders. The new key will be 
	 * equal to any key that is generated by this method using the same arguments.
	 * @param placeholders the placeholders of the key
	 * @return the new key
	 */
	public Key newKey( Path... placeholders ){
		return newKey( null, placeholders );
	}

	/**
	 * Creates a new shared key for any set of placeholders. The new key will be 
	 * equal to any key that is generated by this method using the same arguments.
	 * @param anchor if the anchor is set, then this key cannot be automatically deleted. It is the clients responsibility
	 * to ensure that no two keys have the same anchor. If two keys have the same anchor, then one of them might override
	 * the other one leading to data loss. Can be <code>null</code>.
	 * @param placeholders the placeholders of the key
	 * @return the new key
	 */
	public Key newKey( String anchor, Path... placeholders ){
		return new PlaceholderKey( anchor, placeholders, true );
	}
	
	/**
	 * Creates a new non-shared key for any set of placeholders. The new key will
	 * not be equal to any other key but itself.
	 * @param placeholders the placeholders of the key
	 * @return the new key
	 */
	public Key newUniqueKey( Path... placeholders ){
		return newUniqueKey( null, placeholders );
	}

	/**
	 * Creates a new non-shared key for any set of placeholders. The new key will
	 * not be equal to any other key but itself.
	 * @param placeholders the placeholders of the key
	 * @param anchor if the anchor is set, then this key cannot be automatically deleted. It is the clients responsibility
	 * to ensure that no two keys have the same anchor. If two keys have the same anchor, then one of them might override
	 * the other one leading to data loss. Can be <code>null</code>.
	 * @return the new key
	 */
	public Key newUniqueKey( String anchor, Path... placeholders ){
		return new PlaceholderKey( anchor, placeholders, false );
	}
	
	/**
	 * Creates a copy of <code>key</code>.
	 * @param key the key to copy
	 * @return the new key
	 */
	public Key copyKey( Key key ){
		return new PlaceholderKey( key.getAnchor(), key.getPlaceholders(), key.isShared() );
	}
		
	/**
	 * Sets the strategy that is used to automatically remove invalid placeholders. This
	 * strategy is recursively applied to all other {@link PlaceholderMap}s that are
	 * stored within this map.
	 * @param strategy the new strategy, can be <code>null</code>
	 */
	public void setPlaceholderStrategy( PlaceholderStrategy strategy ){
		if( this.strategy != null ){
			this.strategy.removeListener( listener );
		}
		this.strategy = strategy;
		for( Map<?, Object> map : data.values() ){
			for( Object value : map.values() ){
				setPlaceholderStrategy( value, strategy );
			}
		}
		if( this.strategy != null ){
			validate( this.strategy, false );
			this.strategy.addListener( listener );
		}
	}
	
	private void setPlaceholderStrategy( Object value, PlaceholderStrategy strategy ){
		if( value instanceof PlaceholderMap ){
			((PlaceholderMap)value).setPlaceholderStrategy( strategy );
		}
		else if( value instanceof Object[] ){
			for( Object child : (Object[])value ){
				setPlaceholderStrategy( child, strategy );
			}
		}
	}
	
	/**
	 * Gets the strategy that is observed for removing invalid placeholders.
	 * @return the strategy, can be <code>null</code>
	 */
	public PlaceholderStrategy getPlaceholderStrategy(){
		return strategy;
	}
	
	/**
	 * Using <code>strategy</code> removes all placeholders that are invalid.
	 * @param strategy the strategy for checking the placeholders, a value or <code>null</code>
	 * means that all placeholders are valid
	 * @param recursive if <code>true</code> then {@link #validate(PlaceholderStrategy,boolean)} is also
	 * called on all sub-maps of this map
	 */
	public void validate( PlaceholderStrategy strategy, boolean recursive ){
		if( strategy == null ){
			return;
		}
		
		if( recursive ){
			for( Map<?, Object> map : data.values() ){
				for( Object value : map.values() ){
					validate( value, strategy );
				}
			}
		}
		
		Key[] keys = data.keySet().toArray( new Key[ data.size() ] );
		for( Key key : keys ){
			Key replacement = ((PlaceholderKey)key).shrink( strategy );
			if( replacement != key ){
				Map<String, Object> map = data.remove( key );
				if( replacement != null ){
					data.put( replacement, map );
				}
			}
		}
	}
	
	private void validate( Object value, PlaceholderStrategy strategy ){
		if( value instanceof PlaceholderMap ){
			((PlaceholderMap)value).validate( strategy, true );
		}
		else if( value instanceof Object[] ){
			for( Object child : (Object[])value ){
				validate( child, strategy );
			}
		}
	}

	/**
	 * Removes all occurrences of <code>placeholders</code>.
	 * @param placeholder the placeholder to remove
	 * @param recursive if <code>true</code>, this method is called recursively on
	 * every sub-map in this map
	 */
	public void removeAll( Path placeholder, boolean recursive ){
		Set<Path> placeholders = new HashSet<Path>();
		placeholders.add( placeholder );
		removeAll( placeholders, recursive );
	}
	
	/**
	 * Removes all occurrences of all elements of <code>placeholders</code>.
	 * @param placeholders the placeholders to remove
	 * @param recursive if <code>true</code>, this method is called recursively on
	 * every sub-map in this map
	 */
	public void removeAll( Set<Path> placeholders, boolean recursive ){
		if( placeholders.isEmpty() ){
			return;
		}
		
		if( recursive ){
			for( Map<?, Object> map : data.values() ){
				for( Object value : map.values() ){
					removeAll( value, placeholders );
				}
			}
		}
		
		Key[] keys = data.keySet().toArray( new Key[ data.size() ] );
		for( Key key : keys ){
			Key replacement = ((PlaceholderKey)key).shrink( placeholders );
			if( replacement != key ){
				Map<String, Object> map = data.remove( key );
				if( replacement != null ){
					data.put( replacement, map );
				}
			}
		}
	}
	
	private void removeAll( Object value, Set<Path> invalidated ){
		if( value instanceof PlaceholderMap ){
			((PlaceholderMap)value).removeAll( invalidated, true );
		}
		else if( value instanceof Object[] ){
			for( Object child : (Object[])value ){
				removeAll( child, invalidated );
			}
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
	public void add( Key placeholder ){
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
	public void remove( Key placeholder ){                                                
		data.remove( placeholder );
	}
	
	/**
	 * Gets all placeholders that are known to this map in the order
	 * they were {@link #add(Key) added} to this map.
	 * @return all placeholders
	 */
	public Key[] getPlaceholders(){
		Set<Key> set = data.keySet();
		return set.toArray( new Key[ set.size() ] );
	}
	
	/**
	 * Gets all keys that are in use for <code>placeholder</code> in the order
	 * they were first used to put an object into this map.
	 * @param placeholder some placeholder
	 * @return the associated keys, <code>null</code> if <code>placeholder</code> is not
	 * known to this map
	 */
	public String[] getKeys( Key placeholder ){
		Map<String,Object> map = data.get( placeholder );
		if( map == null ){
			return null;
		}
		Set<String> set = map.keySet();
		return set.toArray( new String[ set.size() ] );
	}
	
	/**
	 * Tells whether this map is completely empty.
	 * @return <code>true</code> if there are no data stored in this map
	 */
	public boolean isEmpty(){
		return data.isEmpty();
	}
	
	/**
	 * Stores the value <code>value</code> in this map, {@link #add(Key) adds}
	 * <code>placeholder</code> if necessary, overrides the value stored at
	 * <code>key</code> if existent.
	 * @param placeholder the placeholder for which <code>value</code> is stored
	 * @param key the unique identifier of the value
	 * @param value the new value, not <code>null</code>
	 */
	public void putString( Key placeholder, String key, String value ){
		put( placeholder, key, value );
	}

	/**
	 * Stores the value <code>value</code> in this map, {@link #add(Key) adds}
	 * <code>placeholder</code> if necessary, overrides the value stored at
	 * <code>key</code> if existent.
	 * @param placeholder the placeholder for which <code>value</code> is stored
	 * @param key the unique identifier of the value
	 * @param value the new value, not <code>null</code>
	 */
	public void putPath( Key placeholder, String key, Path value ){
		put( placeholder, key, value );
	}
	
	/**
	 * Stores the value <code>value</code> in this map, {@link #add(Key) adds}
	 * <code>placeholder</code> if necessary, overrides the value stored at
	 * <code>key</code> if existent.
	 * @param placeholder the placeholder for which <code>value</code> is stored
	 * @param key the unique identifier of the value
	 * @param value the new value
	 */
	public void putInt( Key placeholder, String key, int value ){
		put( placeholder, key, Integer.valueOf( value ) );
	}
	
	/**
	 * Stores the value <code>value</code> in this map, {@link #add(Key) adds}
	 * <code>placeholder</code> if necessary, overrides the value stored at
	 * <code>key</code> if existent.
	 * @param placeholder the placeholder for which <code>value</code> is stored
	 * @param key the unique identifier of the value
	 * @param value the new value
	 */
	public void putLong( Key placeholder, String key, long value ){
		put( placeholder, key, Long.valueOf( value ) );
	}
	
	/**
	 * Stores the value <code>value</code> in this map, {@link #add(Key) adds}
	 * <code>placeholder</code> if necessary, overrides the value stored at
	 * <code>key</code> if existent.
	 * @param placeholder the placeholder for which <code>value</code> is stored
	 * @param key the unique identifier of the value
	 * @param value the new value
	 */
	public void putBoolean( Key placeholder, String key, boolean value ){
		put( placeholder, key, Boolean.valueOf( value ) );
	}
	
	/**
	 * Stores the value <code>value</code> in this map, {@link #add(Key) adds}
	 * <code>placeholder</code> if necessary, overrides the value stored at
	 * <code>key</code> if existent.
	 * @param placeholder the placeholder for which <code>value</code> is stored
	 * @param key the unique identifier of the value
	 * @param value the new value
	 */
	public void putDouble( Key placeholder, String key, double value ){
		put( placeholder, key, Double.valueOf( value ) );
	}
	
	/**
	 * Stores the value <code>value</code> in this map, {@link #add(Key) adds}
	 * <code>placeholder</code> if necessary, overrides the value stored at
	 * <code>key</code> if existent.
	 * @param placeholder the placeholder for which <code>value</code> is stored
	 * @param key the unique identifier of the value
	 * @param value the new value, not <code>null</code>
	 */
	public void putMap( Key placeholder, String key, PlaceholderMap value ){
		put( placeholder, key, value );
	}
	
	/**
	 * Stores the value <code>value</code> in this map, {@link #add(Key) adds}
	 * <code>placeholder</code> if necessary, overrides the value stored at
	 * <code>key</code> if existent.
	 * @param placeholder the placeholder for which <code>value</code> is stored
	 * @param key the unique identifier of the value
	 * @param value the new value, not <code>null</code>. May contain any data type
	 * for which this map offers a put-method.
	 */
	public void putArray( Key placeholder, String key, Object[] value ){
		put( placeholder, key, value );
	}
	
	/**
	 * Stores the value <code>value</code> in this map, {@link #add(Key) adds}
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
	public void put( Key placeholder, String key, Object value ){
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
				value instanceof Path ||
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
	public Object remove( Key placeholder, String key ){
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
	public boolean contains( Key placeholder, String key ){
		return get( placeholder, key ) != null;
	}
	
	/**
	 * Gets the string that is stored under <code>key</code>
	 * @param placeholder the placeholder in whose realm to search
	 * @param key the unique identifier of the searched data
	 * @return the data, not <code>null</code>
	 * @throws IllegalArgumentException if either the value is of the wrong type or missing
	 */
	public String getString( Key placeholder, String key ){
		Object data = get( placeholder, key );
		if( data instanceof String ){
			return (String)data;
		}
		else{
			throw new IllegalArgumentException( "\"" + key + "\" is not a string" );
		}
	}

	/**
	 * Gets the {@link Path} that is stored under <code>key</code>
	 * @param placeholder the placeholder in whose realm to search
	 * @param key the unique identifier of the searched data
	 * @return the data, not <code>null</code>
	 * @throws IllegalArgumentException if either the value is of the wrong type or missing
	 */
	public Path getPath( Key placeholder, String key ){
		Object data = get( placeholder, key );
		if( data instanceof Path ){
			return (Path)data;
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
	public int getInt( Key placeholder, String key ){
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
	public long getLong( Key placeholder, String key ){
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
	public boolean getBoolean( Key placeholder, String key ){
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
	public double getDouble( Key placeholder, String key ){
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
	public PlaceholderMap getMap( Key placeholder, String key ){
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
	public Object[] getArray( Key placeholder, String key ){
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
	public Object get( Key placeholder, String key ){
		Map<String, Object> map = data.get( placeholder );
		if( map == null ){
			return null;
		}
		return map.get( key );
	}
	
	@Override
	public String toString(){
		return data.toString();
	}
	
	/**
	 * A key is a set of {@link Path}s, it is used to identify
	 * entries in a {@link PlaceholderMap}.
	 * @author Benjamin Sigg
	 */
	public static interface Key{
		/**
		 * Gets the placeholders which make up this key.
		 * @return the placeholders, this array may be empty but not <code>null</code>
		 */
		public Path[] getPlaceholders();
		
		/**
		 * Tells whether this key knows <code>placeholder</code>.
		 * @param placeholder the placeholder to search
		 * @return whether <code>placeholder</code> was found
		 */
		public boolean contains( Path placeholder );
		
		/**
		 * Gets the anchor. If the anchor is set, then this key cannot be deleted even if all
		 * {@link #getPlaceholders() placeholders} are no longer valid. No two keys may have the
		 * same anchor.
		 * @return the anchor, can be <code>null</code>
		 */
		public String getAnchor();
		
		/**
		 * Tells whether this key is shared. Two shared keys are equal if they
		 * have the same array of {@link #getPlaceholders() placeholders}, two non-shared
		 * keys are equal only if they are the same object, a shared and a non-shared key
		 * are never equal.
		 * @return whether this is a shared key
		 */
		public boolean isShared();
	}
	
	/**
	 * Standard implementation of {@link PlaceholderMap.Key}.
	 * @author Benjamin Sigg
	 *
	 */
	private class PlaceholderKey implements Key{
		private Path[] placeholders;
		private String anchor;
		private boolean shared;
		
		/**
		 * Creates a new key.
		 * @param anchor the anchor, makes this key accessible even if all placeholders are deleted, can be <code>null</code>
		 * @param placeholders the placeholders which make up this key, must not contain a <code>null</code> value
		 * @param shared how the <code>equals</code> method behaves
		 */
		public PlaceholderKey( String anchor, Path[] placeholders, boolean shared ){
			if( placeholders == null ){
				throw new IllegalArgumentException( "placeholders must not be null" );
			}
			
			for( Path placeholder : placeholders ){
				if( placeholder == null ){
					throw new IllegalArgumentException( "placeholders does contain a null value" );
				}
			}
			
			this.anchor = anchor;
			this.placeholders = placeholders;
			this.shared = shared;
		}
		
		public PlaceholderKey( DataInputStream in, Version version ) throws IOException{
			shared = in.readBoolean();
			
			if( Version.VERSION_1_1_1.compareTo( version ) <= 0 ){
				if( in.readBoolean() ){
					anchor = in.readUTF();
				}
			}
			
			placeholders = new Path[ in.readInt() ];
			for( int i = 0; i < placeholders.length; i++ ){
				placeholders[i] = new Path( in.readUTF() );
			}
		}
		
		public PlaceholderKey( XElement in ){
			shared = in.getBoolean( "shared" );
			
			XElement xanchor = in.getElement( "anchor" );
			if( xanchor != null ){
				anchor = xanchor.getString();
			}
			
			XElement[] xplaceholders = in.getElements( "placeholder" );
			placeholders = new Path[ xplaceholders.length ];
			for( int i = 0; i < xplaceholders.length; i++ ){
				placeholders[i] = new Path( xplaceholders[i].getString() );
			}
		}
		
		public void write( DataOutputStream out ) throws IOException{
			out.writeBoolean( shared );
			
			if( anchor != null ){
				out.writeBoolean( true );
				out.writeUTF( anchor );
			}
			else{
				out.writeBoolean( false );
			}
			
			out.writeInt( placeholders.length );
			for( Path path : placeholders ){
				out.writeUTF( path.toString() );
			}
		}
		
		public void write( XElement out ){
			out.addBoolean( "shared", shared );
			
			if( anchor != null ){
				out.addElement( "anchor" ).setString( anchor );
			}
			
			for( Path placeholder : placeholders ){
				out.addElement( "placeholder" ).setString( placeholder.toString() );
			}
		}
		
		/**
		 * Creates a new key by removing any invalid placeholder.
		 * @param strategy the strategy to apply, can be <code>null</code>
		 * @return the new key or <code>null</code> if there is nothing left from this key
		 */
		public PlaceholderKey shrink( PlaceholderStrategy strategy ){
			if( strategy == null ){
				return this;
			}
			
			boolean[] remain = new boolean[placeholders.length];
			int count = 0;
			for( int i = 0; i < remain.length; i++ ){
				remain[i] = strategy.isValidPlaceholder( placeholders[i] );
				if( remain[i] ){
					count++;
				}
			}
			
			if( count == placeholders.length ){
				return this;
			}
			if( count == 0 && anchor == null ){
				return null;
			}
			
			Path[] copy = new Path[ count ];
			int index = 0;
			for( int i = 0; i < placeholders.length; i++ ){
				if( remain[i] ){
					copy[ index++ ] = placeholders[i];
				}
			}
			
			return new PlaceholderKey( anchor, copy, shared );
		}
		
		/**
		 * Creates a new key removing all placeholders in <code>invalidated</code>
		 * from this key.
		 * @param invalidated the placeholders that are no longer valid
		 * @return the new key, may be <code>this</code> or <code>null</code> to indicate
		 * that this key is no longer valid
		 */
		public PlaceholderKey shrink( Set<Path> invalidated ){
			boolean[] remain = new boolean[placeholders.length];
			int count = 0;
			for( int i = 0; i < remain.length; i++ ){
				remain[i] = !invalidated.contains( placeholders[i] );
				if( remain[i] ){
					count++;
				}
			}
			
			if( count == placeholders.length ){
				return this;
			}
			if( count == 0 && anchor == null ){
				return null;
			}
			
			Path[] copy = new Path[ count ];
			int index = 0;
			for( int i = 0; i < placeholders.length; i++ ){
				if( remain[i] ){
					copy[ index++ ] = placeholders[i];
				}
			}
			
			return new PlaceholderKey( anchor, copy, shared );
		}
		
		public Path[] getPlaceholders(){
			return placeholders;
		}
		
		public boolean contains( Path placeholder ){
			for( Path item : placeholders ){
				if( item.equals( placeholder )){
					return true;
				}
			}
			return false;
		}
		
		public boolean isShared(){
			return shared;
		}
		
		public String getAnchor(){
			return anchor;
		}
		
		@Override
		public String toString(){
			StringBuilder builder = new StringBuilder();
			for( Path placeholder : placeholders ){
				builder.append( placeholder );
				builder.append( ", " );
			}
			if( anchor != null ){
				builder.append( ", anchor=" ).append( anchor ).append( ", " );
			}
			builder.append( "shared=" );
			builder.append( shared );
			return builder.toString();
		}
		
		@Override
		public int hashCode(){
			int result = Arrays.hashCode( placeholders );
			if( shared ){
				return result;
			}
			else{
				return -result;
			}
		}
		
		@Override
		public boolean equals( Object obj ){
			if( obj == this ){
				return true;
			}
			
			if( !isShared() ){
				return false;
			}
			
			if( obj.getClass() != this.getClass() ){
				return false;
			}
			
			PlaceholderKey that = (PlaceholderKey)obj;
			if( !that.isShared() ){
				return false;
			}
			
			if( anchor == null && that.getAnchor() != null ){
				return false;
			}
			
			if( anchor != null && !anchor.equals( that.getAnchor() )){
				return false;
			}
			
			return Arrays.equals( placeholders, that.placeholders );
		}
	}
}
