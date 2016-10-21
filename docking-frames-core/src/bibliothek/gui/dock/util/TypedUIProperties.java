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
package bibliothek.gui.dock.util;

import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.util.Path;

/**
 * A wrapper around an {@link UIProperties} enhancing the properties with type safety.
 * @author Benjamin Sigg
 */
public class TypedUIProperties {
	/**
	 * A class describing a type, different {@link Type} objects may share the same {@link Class} object
	 * but with different or even equal generic parameters. 
	 * @author Benjamin Sigg
	 * @param <T> the actual type
	 */
	public static class Type<T>{
		private final String key;
		
		/**
		 * Creates a new type.
		 * @param key the key of this type, must not be <code>null</code>, must at least contain
		 * one character, must not contain the character ".", must pass {@link Path#isValidPath(String)} 
		 */
		public Type( String key ){
			if( key == null ){
				throw new IllegalArgumentException( "key must not be null" );
			}
			if( key.length() == 0 ){
				throw new IllegalArgumentException( "key must contain at least one character" );
			}
			if( key.contains( "." )){
				throw new IllegalArgumentException( "key must not contain the character '.'" );
			}
			if( !Path.isValidPath( key )){
				throw new IllegalArgumentException( "key not a valid path" );
			}
			
			this.key = key;
		}
		
		/**
		 * Gets the key of this type.
		 * @return the key, not <code>null</code>
		 */
		public String getKey(){
			return key;
		}
		
		/**
		 * Gets the identifier that should be used as replacement for a resource with identifier <code>id</code>
		 * and with type <code>this</code>.
		 * @param id the id of some identifier
		 * @return the combined identifier of <code>this</code> and <code>id</code>
		 */
		public String getKey( String id ){
			return key + "." + id;
		}
		
		/**
		 * Gets the identifier for the kind of {@link UIValue} that reads an {@link UIValue} of this type.
		 * @param kind the kind of {@link UIValue}
		 * @return the identifier to use in a map
		 */
		public Path getKind( Path kind ){
			return new Path( key ).append( kind );
		}
	}
	
	/** all the properties of this manager */
	private UIProperties<Object, UIValue<Object>, UIBridge<Object, UIValue<Object>>> properties;
	
	/** all the types available to this properties */
	private Map<String, Type<?>> types = new HashMap<String, Type<?>>();
	
	/** all the wrappers for {@link UIBridge}, used for hiding the internal naming scheme */
	private Map<UIBridge<?, ?>, ThemeBridge<?>> bridges = new HashMap<UIBridge<?,?>, ThemeBridge<?>>();
	
	/**
	 * Creates a new map.
	 * @param controller the controller that uses this map
	 */
	public TypedUIProperties( DockController controller ){
		properties = new UIProperties<Object, UIValue<Object>, UIBridge<Object,UIValue<Object>>>( controller );
	}
	
	/**
	 * Registers <code>type</code> at this properties. Only after <code>type</code> has been registered it
	 * can be used to call the methods of this object. Note that <code>type</code> cannot be removed nor can
	 * it be altered. An attempt to use another {@link Type} with the same key will always result in an
	 * exception.
	 * @param <T> the <code>type</code>
	 * @param type the new type, not <code>null</code>
	 */
	public <T> void registerType( Type<T> type ){
		if( types.get( type.getKey() ) != null ){
			throw new IllegalArgumentException( "the key '" + type.getKey() + "' is already in use" );
		}
		types.put( type.getKey(), type );
	}
	
	private <T> void check( Type<T> type ){
		if( types.get( type.getKey() ) != type ){
			throw new IllegalArgumentException( "type '" + type.getKey() + "=" + type + " is not registered" );
		}
	}
	
	/**
	 * Sets a scheme that is used to fill missing entries.
	 * @param priority the level on which the scheme will operate
	 * @param scheme the new scheme or <code>null</code>
	 */
	public void setScheme( Priority priority, UIScheme<Object, UIValue<Object>, UIBridge<Object,UIValue<Object>>> scheme ){
		properties.setScheme( priority, scheme );
	}
	
	/**
	 * Gets the scheme which fills missing entries on the level <code>priority</code>
	 * @param priority the level to question
	 * @return the scheme or <code>null</code>
	 */
	public UIScheme<Object, UIValue<Object>, UIBridge<Object,UIValue<Object>>> getScheme( Priority priority ){
		return properties.getScheme( priority );
	}
	
	/**
	 * Adds the listener <code>value</code> to this manager.
	 * @param <V> the type of object <code>value</code> supports.
	 * @param id the unique identifier of the property to observe
	 * @param kind what kind of object <code>value</code> actually is
	 * @param type <code>V</code> in a form that can be tested by this manager
	 * @param value the new observer
	 * @throws IllegalArgumentException if <code>type</code> is not known to this manager or if either
	 * of the arguments is <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public <V> void add( String id, Path kind, Type<V> type, UIValue<V> value ){
		check( type );
		properties.add( type.getKey( id ), type.getKind( kind ), (UIValue<Object>)value );
	}
	
	/**
	 * Removes the observer <code>value</code> from this manager.
	 * @param value the observer to remove
	 */
	@SuppressWarnings("unchecked")
	public void remove( UIValue<?> value ){
		properties.remove( (UIValue<Object>)value );
	}
	
	/**
	 * Adds an {@link UIBridge} to this manager, the bridge will be responsible for {@link UIValue}s of kind
	 * <code>kind</code>. Please note that {@link UIValue}s of a sub-kind of <code>kind</code> might be handled
	 * by <code>bridge</code> as well, unless a bridge was installed for that sub-kind.
	 * @param <V> the <code>type</code>
	 * @param priority the importance of <code>bridge</code>
	 * @param kind the kind of {@link UIValue}s <code>bridge</code> has to handle
	 * @param type The type of objects handled by <code>bridge</code>
	 * @param bridge the new bridge
	 */
	@SuppressWarnings("unchecked")
	public <V> void publish( Priority priority, Path kind, Type<V> type, UIBridge<V, UIValue<V>> bridge ){
		check( type );
		kind = type.getKind( kind );
		
		ThemeBridge<?> theme = bridges.get( bridge );
		if( theme == null ){
			theme = new ThemeBridge<V>( type.getKey().length()+1, bridge );
			bridges.put( bridge, theme );
		}
		
		properties.publish( priority, kind, (UIBridge)theme );
	}
	
	/**
	 * Removes the {@link UIBridge} that was responsible for handling {@link UIValue}s of kind <code>kind</code>
	 * and wrapping type <code>type</code>
	 * @param <V> the <code>type</code>
	 * @param priority the level on which the bridge worked
	 * @param kind the kind of {@link UIValue} the bridge handled
	 * @param type the type of value the {@link UIValue}s handle 
	 */
	@SuppressWarnings("unchecked")
	public <V> void unpublish( Priority priority, Path kind, Type<V> type ){
		check( type );
		kind = type.getKind( kind );
		
		UIBridge<V, UIValue<V>> bridge = (UIBridge)properties.getBridge( priority, kind );
		if( bridge != null ){
			properties.unpublish( priority, kind );
			ThemeBridge<V> theme = (ThemeBridge<V>)bridges.get( bridge );
			if( theme != null ){
				checkUsed( theme );
			}
		}
	}
	
	/**
	 * Removes any occurrence of <code>bridge</code> from this manager.
	 * @param <V> the type handled by the {@link UIValue}s
	 * @param priority the level on which to search the bridge
	 * @param bridge the bridge to remove everywhere
	 */
	@SuppressWarnings("unchecked")
	public <V> void unpublish( Priority priority, UIBridge<V, UIValue<V>> bridge ){
	    ThemeBridge<V> theme = (ThemeBridge<V>)bridges.get( bridge );
		if( theme != null ){
			properties.unpublish( priority, (UIBridge)theme );
			checkUsed( theme );
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void checkUsed( ThemeBridge<?> bridge ){
		if( !properties.isStored( (UIBridge)bridge )){
			bridges.remove( bridge.getDelegate() );
		}
	}
	
	/**
	 * Sets a value of this manager.
	 * @param <V> the <code>type</code>
	 * @param priority the level on which to store <code>value</code>
	 * @param id the unique identifier of <code>value</code>
	 * @param type the type of <code>value</code>
	 * @param value the resources itself, can be <code>null</code>
	 */
	public <V> void put( Priority priority, String id, Type<V> type, V value ){
		check( type );
		id = type.getKey( id );
		properties.put( priority, id, value );
	}
	
	/**
	 * Gets the current value of the resource <code>id</code> with type <code>type</code>.
	 * @param <V> the <code>type</code>
	 * @param id the identifier of some resource
	 * @param type the type of the resource
	 * @return the resource or <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public <V> V get( String id, Type<V> type ){
		check( type );
		id = type.getKey( id );
		return (V)properties.get( id );
	}
	
	/**
	 * Removes all resources that were stored on the level <code>priority</code>.
	 * @param priority the priority of the level to remove
	 */
	public void clear( Priority priority ){
		properties.clear( priority );
	}
	
	/**
	 * A wrapper around an {@link UIBridge}, hiding the internal naming scheme of {@link ThemeManager}.
	 * @author Benjamin Sigg
	 * @param <V> the kind of value the bridge handles
	 */
	private static class ThemeBridge<V> implements UIBridge<V, UIValue<V>>{
		private int offset;
		private UIBridge<V, UIValue<V>> delegate;
		
		/**
		 * Creates a new bridge.
		 * @param offset how many characters to remove from an identifier to hide the internal naming scheme
		 * @param delegate the algorithm that should be used
		 */
		public ThemeBridge( int offset, UIBridge<V, UIValue<V>> delegate ){
			this.offset = offset;
			this.delegate = delegate;
		}
		
		/**
		 * Gets the algorithm that is hidden by this bridge.
		 * @return the algorithm, not <code>null</code>
		 */
		public UIBridge<V, UIValue<V>> getDelegate(){
			return delegate;
		}

		public void add( String id, UIValue<V> uiValue ){
			delegate.add( id.substring( offset ), uiValue );
		}

		public void remove( String id, UIValue<V> uiValue ){
			delegate.remove( id.substring( offset ), uiValue );
		}

		public void set( String id, V value, UIValue<V> uiValue ){
			delegate.set( id.substring( offset ), value, uiValue );
		}
	}
}
