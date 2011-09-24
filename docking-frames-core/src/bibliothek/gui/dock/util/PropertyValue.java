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

import bibliothek.gui.DockController;

/**
 * A wrapper for a value which is either read from {@link DockProperties},
 * or can be set by the client directly.
 * @author Benjamin Sigg
 *
 * @param <A> the type of wrapper value
 */
public abstract class PropertyValue<A> {
	/** the access to the value */
	private PropertyKey<A> key;
	/** the observed properties, can be <code>null</code> */
	private DockProperties properties;
	/** the value set by the client */
	private A value;
	/** a listener to {@link #properties} */
	private DockPropertyListener<A> listener;
	
	/**
	 * Creates a new value.
	 * @param key the key used to access the value in {@link DockProperties}
	 */
	public PropertyValue( PropertyKey<A> key ){
	    this( key, null );
	}
	
	/**
     * Creates a new value.
     * @param key the key used to access the value in {@link DockProperties}
     * @param controller the controller from which properties are to be read
     */
	public PropertyValue( PropertyKey<A> key, DockController controller ){
		if( key == null )
			throw new IllegalArgumentException( "Key must not be null" );
		this.key = key;
		
		listener = new DockPropertyListener<A>(){
			public void propertyChanged( DockProperties properties, PropertyKey<A> property, A oldValue, A newValue ){
				if( value == null )
					valueChanged( oldValue, newValue );
			}
		};
		
		setProperties( controller );
	}
	
	/**
	 * Sets the {@link DockProperties} that are read from
	 * <code>controller</code>.
	 * @param controller the properties, can be <code>null</code>
	 * @see #setProperties(DockProperties)
	 */
	public void setProperties( DockController controller ){
		if( controller == null )
			setProperties( (DockProperties)null );
		else
			setProperties( controller.getProperties() );
	}
	
	/**
	 * Sets the {@link DockProperties} which should be observed.
	 * @param properties the new properties, can be <code>null</code>
	 */
	public void setProperties( DockProperties properties ){
		if( value != null )
			this.properties = properties;
		else{
			A oldValue = getValue();
			
			if( this.properties != null )
				this.properties.removeListener( key, listener );
			
			this.properties = properties;
			
			if( properties != null )
				properties.addListener( key, listener );
			
			A newValue = getValue();
			if( (oldValue == null && newValue != null) ||
				(oldValue != null && newValue == null) ||
				(oldValue != null && !oldValue.equals( newValue ))){
				
				valueChanged( oldValue, newValue );
			}
		}
	}
	
	/**
	 * Gets the currently observed properties.
	 * @return the map, or <code>null</code>
	 */
	public DockProperties getProperties(){
		return properties;
	}
	
	/**
	 * Gets the key which is used to access the value in {@link DockProperties}.
	 * @return the key
	 */
	public PropertyKey<A> getKey(){
		return key;
	}
	
	/**
	 * Sets the key which is used to access the value in {@link DockProperties}.
	 * @param key the new key
	 */
	public void setKey( PropertyKey<A> key ) {
		if( key == null )
			throw new IllegalArgumentException( "key must not be null" );
		
		if( properties == null ){
			this.key = key;
		}
		else{
			A oldValue = getValue();
			properties.removeListener( this.key, listener );
			this.key = key;
			properties.addListener( this.key, listener );

			A newValue = getValue();
			if( (oldValue == null && newValue != null) ||
				(oldValue != null && newValue == null) ||
				(oldValue != null && !oldValue.equals( newValue ))){
				
				valueChanged( oldValue, newValue );
			}
		}
	}
	
	/**
	 * Gets the current value. The result is the argument of {@link #setValue(Object)} if
	 * the argument was not <code>null</code>, or else the value read from
	 * the {@link #setProperties(DockProperties) properties}.<br>
	 * Note that this method can return <code>null</code> even if the
	 * {@link PropertyKey} has a non-<code>null</code> default value.
	 * @return the value or <code>null</code> if no value was found at all
	 */
	public A getValue(){
		if( value != null )
			return value;
		
		if( properties != null )
			return properties.get( key );
		
		return key.getDefault( null );
	}
	
	/**
	 * Gets the value that was set through {@link #setValue(Object)}.
	 * @return the value, might be <code>null</code>
	 */
	public A getOwnValue(){
	    return value;
	}
	
	/**
	 * Tells whether any value is set.
	 * @return <code>true</code> if not the default value of the key
	 * would be returned
	 */
	public boolean isAnyValueSet(){
	    if( value != null )
	        return true;
	    
	    if( properties != null ){
	        if( properties.isSet( key ))
	            return true;
	    }
	    
	    return false;
	}
	
	/**
	 * Sets the current value.
	 * @param value the value, <code>null</code> if the value should be read
	 * from the {@link #setProperties(DockProperties) properties}
	 */
	public void setValue( A value ){
		if( properties != null ){
			if( this.value == null && value != null )
				properties.removeListener( key, listener );
			else if( this.value != null && value == null )
				properties.addListener( key, listener );
		}
		
		A oldValue = getValue();
		this.value = value;
		A newValue = getValue();
		
		if( (oldValue == null && newValue != null) ||
			(oldValue != null && newValue == null) ||
			(oldValue != null && !oldValue.equals( newValue ))){
				
			valueChanged( oldValue, newValue );
		}
	}
	
	/**
	 * Invoked when the value has been changed.
	 * @param oldValue the new value
	 * @param newValue the old value
	 */
	protected abstract void valueChanged( A oldValue, A newValue );
	
	@Override
	public String toString(){
		return getClass().getName() + "[" + key.toString() + " -> " + getValue() + "]";
	}
}
