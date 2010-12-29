/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.DockController;

/**
 * A set of properties that are used at different places all over the framework.
 * The map uses a {@link Priority} based system, allowing clients to override
 * behavior of themes or set default values in case a theme does not set one.
 * @author Benjamin Sigg
 */
public class DockProperties {
	/** the map of values */
	private Map<PropertyKey<?>, Entry<?>> map = new HashMap<PropertyKey<?>, Entry<?>>();

	/** the owner of this map */
	private DockController controller;
	
	/**
	 * Creates a new map.
	 * @param controller the owner of this map
	 */
	public DockProperties( DockController controller ){
		if( controller == null ){
			throw new IllegalArgumentException( "controller must not be null" );
		}
		this.controller = controller;
	}
	
	/**
	 * Gets the owner of this {@link DockProperties}.
	 * @return the owner, not <code>null</code>
	 */
	public DockController getController(){
		return controller;
	}
	
	/**
	 * Sets a value. This is equivalent to calling <code>set( key, value, Priority.CLIENT )</code>.
	 * @param <A> the type of the value
	 * @param key the key to access the value
	 * @param value the value, can be <code>null</code>
	 */
	public <A> void set( PropertyKey<A> key, A value ){
		set( key, value, Priority.CLIENT );
	}
	
	/**
	 * Sets a value.
	 * @param <A> the type of the value
	 * @param key the key to access the value
	 * @param value the value, can be <code>null</code>
	 * @param priority the priority of the new value
	 */
	public <A> void set( PropertyKey<A> key, A value, Priority priority ){
		Entry<A> entry = getEntry( key, true );
		entry.setValue( value, priority );
		check( entry );
	}
	
	/**
	 * Ensures that the value behind <code>key</code> will never be
	 * changed. Should be used with care: any attempt to set the value afterwards
	 * will be responded with an {@link IllegalStateException}. Most times it
	 * is much better to just use {@link Priority#CLIENT} to mark some setting
	 * as important.
	 * @param <A> the type of the value
	 * @param key the key to protect
	 */
	public <A> void finalize( PropertyKey<A> key ){
		Entry<A> entry = getEntry( key, true );
		entry.lock();
	}
	
	/**
	 * Either sets the property <code>key</code> to <code>value</code> or
	 * set the default value for <code>key</code>.
	 * @param <A> the type of the value
	 * @param key the key to access the value
	 * @param value the new value, if <code>null</code> then the default
	 * value will be set
	 * @param priority the priority of the value to remove
	 */
	public <A> void setOrRemove( PropertyKey<A> key, A value, Priority priority ){
		if( value == null )
			unset( key, priority );
		else
			set( key, value, priority );
	}
	
	/**
	 * Tells the entry <code>key</code> that the user has never set its value.
	 * This is equivalent to calling <code>unset( key, Priority.CLIENT )</code>.
	 * @param key the key to access the entry
	 */
	public void unset( PropertyKey<?> key ){
		unset( key, Priority.CLIENT );
	}
	
	/**
	 * Tells the entry <code>key</code> that the user has never set its value.
	 * Also removes the old value of the entry.
	 * @param key the key to access the entry
	 * @param priority the priority for which to remove the value
	 */
	public void unset( PropertyKey<?> key, Priority priority ){
	    Entry<?> entry = getEntry( key, true );
	    entry.unsetValue( priority );
        check( entry );
	}
	
	/**
	 * Gets the value accessed by <code>key</code>. If the value in the
	 * properties is not set, then the {@link PropertyKey#getDefault(DockProperties) default}
	 * value is returned.
	 * @param <A> the type of the value
	 * @param key the key to search
	 * @return the value or <code>null</code>
	 */
	public <A> A get( PropertyKey<A> key ){
		Entry<A> entry = getEntry( key, true );
		return entry.getValue();
	}
	
	/**
	 * Gets the value of <code>key</code> for the given <code>priority</code>.
	 * @param <A> the kind of value
	 * @param key some key, not <code>null</code>
	 * @param priority the priority, not <code>null</code>
	 * @return the value, might be <code>null</code> even if {@link #get(PropertyKey)}
	 * returns a non-<code>null</code> value
	 */
	public <A> A get( PropertyKey<A> key, Priority priority ){
		Entry<A> entry = getEntry( key, false );
		if( entry == null )
			return null;
		return entry.getValue( priority );
	}
	
	/**
	 * Tells whether there is value set for <code>key</code> with <code>priority</code>. 
	 * @param <A> the kind of value
	 * @param key the key to check
	 * @param priority the priority for which something might be set
	 * @return <code>true</code> if there is a value set
	 */
	public <A> boolean isSet( PropertyKey<A> key, Priority priority ){
	    Entry<A> entry = getEntry( key, false );
	    if( entry == null )
	        return false;

	    return entry.value.isSet( priority );
	}
	
	/**
	 * Tells whether there is value set for <code>key</code>.
	 * @param <A> the kind of value
	 * @param key the key to check
	 * @return <code>true</code> if there is a value set
	 */
	public <A> boolean isSet( PropertyKey<A> key ){
	    Entry<A> entry = getEntry( key, false );
	    if( entry == null )
	        return false;

	    return entry.value.isSomethingSet();
	}
	
	
	
	/**
	 * Adds a listener that will be informed whenever the value accessed
	 * through <code>key</code> changes.
	 * @param <A> the type of the value
	 * @param key the key that accesses the value
	 * @param listener the new listener
	 */
	public <A> void addListener( PropertyKey<A> key, DockPropertyListener<A> listener ){
		if( listener == null )
			throw new IllegalArgumentException( "Listener must not be null" );
		getEntry( key, true ).addListener( listener );
	}
	
	/**
	 * Removes an earlier added listener.
	 * @param <A> the type of value observed by the listener
	 * @param key the key to access the observed entry
	 * @param listener the listener to remove
	 */
	public <A> void removeListener( PropertyKey<A> key, DockPropertyListener<A> listener ){
		Entry<A> entry = getEntry( key, false );
		if( entry != null ){
			entry.removeListener( listener );
			check( entry );
		}
	}
	
	/**
	 * Gets the entry for <code>key</code>.
	 * @param <A> the type of the entry
	 * @param key the name of the entry
	 * @param createIfNull <code>true</code> if <code>null</code> is not a valid 
	 * result. 
	 * @return the entry or <code>null</code>, but only if <code>createIfNull</code>
	 * is <code>false</code>
	 */
	@SuppressWarnings( "unchecked" )
	private <A> Entry<A> getEntry( PropertyKey<A> key, boolean createIfNull ){
		Entry<?> entry = map.get( key );
		if( entry == null && createIfNull ){
			entry = new Entry<A>( key );
			map.put( key, entry );
		}
		return (Entry<A>)entry;
	}
	
	/**
	 * Checks whether <code>entry</code> has to be stored any longer.
	 * @param entry the entry that may be deleted
	 */
	private void check( Entry<?> entry ){
		if( entry.removeable() ){
			map.remove( entry.getKey() );
		}
	}
	
	/**
	 * An entry that contains key, listeners and a value.
	 * @author Benjamin Sigg
	 *
	 * @param <A> the type of the value
	 */
	private class Entry<A>{
		/** the name of this entry */
		private PropertyKey<A> key;
		/** listeners to this entry */
		private List<DockPropertyListener<A>> listeners = new ArrayList<DockPropertyListener<A>>();
		/** the value stored in this entry */
		private NullPriorityValue<A> value = new NullPriorityValue<A>();
		
		/** default value of this entry */
		private A defaultValue;
		/** whether the default value was ever needed and has been set */
		private boolean defaultValueSet = false;

		/** whether changes of this entry are allowed */
		private boolean locked = false;
		
		/**
		 * Creates a new entry.
		 * @param key the name of this entry
		 */
		public Entry( PropertyKey<A> key ){
		    this.key = key;
		}
		
		/**
		 * If called makes this entry immutable.
		 */
		public void lock(){
			locked = true;
		}
		
		/**
		 * Sets the new value of this entry.
		 * @param value the new value
		 * @param priority the priority of the new value
		 */
		@SuppressWarnings( "unchecked" )
		public void setValue( A value, Priority priority ){
			if( locked ){
				throw new IllegalStateException( "this entry is immutable" );
			}
			
			A oldValue = getValue();
			this.value.set( priority, value );
			A newValue = getValue();
			
			if( (oldValue == null && newValue != null) ||
				(oldValue != null && newValue == null) ||
				(oldValue != null && !oldValue.equals( newValue ))){
			
				for( DockPropertyListener<A> listener : (DockPropertyListener<A>[])listeners.toArray( new DockPropertyListener<?>[ listeners.size() ] ))
					listener.propertyChanged( DockProperties.this, key, oldValue, newValue );
			}
		}
		
		/**
		 * Removes a value from this entry
		 * @param priority the priority of the value to unset
		 */
		@SuppressWarnings("unchecked")
		public void unsetValue( Priority priority ){
			if( locked ){
				throw new IllegalStateException( "this entry is immutable" );
			}
			
			A oldValue = getValue();
			this.value.unset( priority );
			A newValue = getValue();
			
			if( (oldValue == null && newValue != null) ||
				(oldValue != null && newValue == null) ||
				(oldValue != null && !oldValue.equals( newValue ))){
			
				for( DockPropertyListener<A> listener : (DockPropertyListener<A>[])listeners.toArray( new DockPropertyListener<?>[ listeners.size() ] ))
					listener.propertyChanged( DockProperties.this, key, oldValue, newValue );
			}
		}
		
		/**
		 * Gets the value of this entry.
		 * @return the value
		 */
		public A getValue(){
			A value = this.value.get();
			
			if( value == null && (!this.value.isSomethingSet() || key.isNullValueReplacedByDefault()) ){
				return getDefault();
			}
			return value;
		}
		
		/**
		 * Gets the value stored for <code>priority</code>.
		 * @param priority the priority, not <code>null</code>
		 * @return the value, might be <code>null</code>
		 */
		public A getValue( Priority priority ){
			return value.get( priority );
		}
		
		/**
		 * Gets the default value of this property.
		 * @return the default value, may be <code>null</code>
		 */
		public A getDefault(){
			if( !defaultValueSet ){
				defaultValue = key.getDefault( DockProperties.this );
				defaultValueSet = true;
			}
			return defaultValue;
		}
		
		/**
		 * Gets the name of this entry.
		 * @return the name
		 */
		public PropertyKey<A> getKey(){
			return key;
		}
		
		/**
		 * Adds a new listener to this entry.
		 * @param listener the new listener
		 */
		public void addListener( DockPropertyListener<A> listener ){
			listeners.add( listener );
		}
		
		/**
		 * Removes a listener from this entry.
		 * @param listener the listener to remove
		 */
		public void removeListener( DockPropertyListener<A> listener ){
			listeners.remove( listener );
		}
		
		/**
		 * Tells whether this entry is needed any longer or not.
		 * @return <code>true</code> if this entry can be deleted safely.
		 */
		public boolean removeable(){
			if( locked )
				return false;
			
			if( !listeners.isEmpty() )
				return false;
			
			if( defaultValueSet || value.isSomethingSet() )
				return false;
			
			return true;
		}
	}
}
