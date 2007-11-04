/**
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

/**
 * A set of properties that are used at different places all over the framework.
 * No component should expect that there are any entries in this map.
 * @author Benjamin Sigg
 *
 */
public class DockProperties {
	/** the map of values */
	private Map<PropertyKey<?>, Entry<?>> map = new HashMap<PropertyKey<?>, Entry<?>>();
	
	/**
	 * Sets a value.
	 * @param <A> the type of the value
	 * @param key the key to access the value
	 * @param value the value, can be <code>null</code>
	 */
	public <A> void set( PropertyKey<A> key, A value ){
		Entry<A> entry = getEntry( key, true );
		entry.setValue( value );
		check( entry );
	}
	
	/**
	 * Gets the value accessed by <code>key</code>. If the value in the
	 * properties is <code>null</code>, then the {@link PropertyKey#getDefault() default}
	 * value is returned.
	 * @param <A> the type of the value
	 * @param key the key to search
	 * @return the value or <code>null</code>
	 */
	public <A> A get( PropertyKey<A> key ){
		Entry<A> entry = getEntry( key, false );
		A result;
		if( entry == null )
			result = null;
		else
			result = entry.getValue();
		
		if( result == null )
		    result = key.getDefault();
		
		return result;
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
	 * @param secure <code>true</code> if <code>null</code> is not a valid 
	 * result. 
	 * @return the entry of <code>null</code>, but only if <code>secure</code>
	 * is <code>false</code>
	 */
	@SuppressWarnings( "unchecked" )
	private <A> Entry<A> getEntry( PropertyKey<A> key, boolean secure ){
		Entry<?> entry = map.get( key );
		if( entry == null && secure ){
			entry = new Entry<A>();
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
		private A value;

		/**
		 * Sets the new value of this entry.
		 * @param value the new value
		 */
		@SuppressWarnings( "unchecked" )
		public void setValue( A value ){
			A oldValue = this.value;
			this.value = value;
			
			if( (oldValue == null && value != null) ||
				(oldValue != null && value == null) ||
				(oldValue != null && !oldValue.equals( value ))){
			
				for( DockPropertyListener<A> listener : (DockPropertyListener<A>[])listeners.toArray( new DockPropertyListener<?>[ listeners.size() ] ))
					listener.propertyChanged( DockProperties.this, key, oldValue, this.value );
			}
		}
		
		/**
		 * Gets the value of this entry.
		 * @return the value
		 */
		public A getValue(){
			return value;
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
			return value == null && listeners.isEmpty();
		}
	}
}
