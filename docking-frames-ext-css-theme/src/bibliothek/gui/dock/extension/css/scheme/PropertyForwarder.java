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
package bibliothek.gui.dock.extension.css.scheme;

import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.dock.extension.css.CssItem;
import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyContainer;
import bibliothek.gui.dock.extension.css.CssPropertyContainerListener;
import bibliothek.gui.dock.extension.css.CssPropertyKey;
import bibliothek.gui.dock.extension.css.CssRule;
import bibliothek.gui.dock.extension.css.CssRuleContent;
import bibliothek.gui.dock.extension.css.CssRuleContentListener;
import bibliothek.gui.dock.extension.css.CssScheme;

/**
 * This class monitors a {@link CssPropertyContainer} and sets the values of its
 * {@link CssProperty}s using values from a {@link CssRuleContent}.
 * @author Benjamin Sigg
 */
public class PropertyForwarder {
	/** the source of all values */
	private CssRuleContent source;
	
	/** where to insert all the values */
	private CssPropertyContainer target;
	
	/** additional information required to convert data from {@link #source} to {@link #target} */
	private CssScheme scheme;
	
	/** monitors {@link CssRule} and target {@link CssPropertyContainer}s */
	private Listener listener = new Listener();
	
	/** all the properties that are currently monitored */
	private Map<CssPropertyKey, CssProperty<?>> properties = new HashMap<CssPropertyKey, CssProperty<?>>();

	/**
	 * Creates a new forwarder.
	 * @param source the source of all values
	 * @param target the target for all values
	 * @param scheme conversion information for values
	 */
	public PropertyForwarder( CssRuleContent source, CssPropertyContainer target, CssScheme scheme ){
		this.source = source;
		this.target = target;
		this.scheme = scheme;
		
		target.addPropertyContainerListener( listener );
		source.addRuleContentListener( listener );
	}
	
	/**
	 * Finds out which key <code>container</code> and <code>key</code> form.
	 * @param container the container, which may or may not be a {@link CssProperty} whose
	 * key is stored.
	 * @param key the appendix to the key of <code>container</code>
	 * @return the new key representing <code>key</code> as child of <code>container</code>
	 */
	protected CssPropertyKey combinedKey( CssPropertyContainer container, String key ){
		for( Map.Entry<CssPropertyKey, CssProperty<?>> entry : properties.entrySet() ){
			if( entry.getValue() == container ){
				return entry.getKey().append( key );
			}
		}
		return new CssPropertyKey( key );
	}
	
	/**
	 * Takes all the known {@link CssProperty}s from the <code>source</code> and adds
	 * them for monitoring.
	 * @param firstRule whether this is the first rule forwarder for a {@link CssItem}, the first
	 * forwarder has to call {@link CssProperty#setScheme(CssScheme, CssPropertyKey)}
	 */
	public void install( boolean firstRule ){
		for( String key : target.getPropertyKeys() ){
			addProperty( new CssPropertyKey( key ), target.getProperty( key ), firstRule );
		}
	}
	
	protected void ignoreTarget(){
		target.removePropertyContainerListener( listener );
	}

	/**
	 * This forwarded stops listening to <code>source</code> or <code>target</code> and
	 * sets all properties back to <code>null</code>
	 */
	public void destroy(){
		target.removePropertyContainerListener( listener );
		source.removeRuleContentListener( listener );
		
		for( CssProperty<?> property : properties.values() ){
			property.removePropertyContainerListener( listener );
		}
		
		for( CssProperty<?> property : properties.values() ){
			property.set( null );
			property.setScheme( null, null );
		}
		properties.clear();
	}

	/**
	 * Called if a <code>property</code> is to be monitored.
	 * @param key the name of <code>property</code>
	 * @param property the additional property to monitor
	 * @param firstRule whether this is the first rule forwarder for a {@link CssItem}, the first
	 * forwarder has to call {@link CssProperty#setScheme(CssScheme, CssPropertyKey)}
	 */
	protected <T> void addProperty( CssPropertyKey key, CssProperty<T> property, boolean firstRule ){
		if( properties.containsKey( key )){
			throw new IllegalStateException( "property with name '" + key + "' already exists" );
		}
		
		if( firstRule ){
			property.setScheme( scheme, key );
		}
		if( source != null ){
			T value = source.getProperty( property.getType( scheme ), key );
			property.set( value );
		}
		properties.put( key, property );
		property.addPropertyContainerListener( listener );
		for( String name : property.getPropertyKeys() ){
			CssProperty<?> subProperty = property.getProperty( name );
			if( subProperty != null ){
				CssPropertyKey subKey = key.append( name );
				if( !properties.containsKey( subKey )){
					addProperty( subKey, subProperty, true );
				}
			}
		}
	}
	

	/**
	 * Removes <code>property</code> from this forwarder.
	 * @param key the name of the property
	 * @param property the property to remove
	 * @param fullRemoval whether the value and {@link CssScheme} should be set to <code>null</code>
	 */
	protected void removeProperty( CssPropertyKey key, CssProperty<?> property, boolean fullRemoval ){
		property.removePropertyContainerListener( listener );
		if( fullRemoval ){
			for( String name : property.getPropertyKeys() ){
				removeProperty( key.append( name ), property.getProperty( name ), fullRemoval );
			}
		}
		properties.remove( key );
		if( fullRemoval ){
			property.set( null );
			property.setScheme( null, null );
		}
	}
	
	protected CssPropertyKey[] getKeys(){
		return properties.keySet().toArray( new CssPropertyKey[ properties.size() ] );
	}
	
	protected CssProperty<?> getProperty( CssPropertyKey key ){
		return properties.get( key );
	}
	
	private class Listener implements CssRuleContentListener, CssPropertyContainerListener{
		@Override
		public void propertyChanged( CssRuleContent source, CssPropertyKey key ){
			CssProperty<?> sink = properties.get( key );
			if( sink != null ){
				resetProperty( sink, key );
			}
		}
		
		@Override
		public void propertiesChanged( CssRuleContent source ){
			for( Map.Entry<CssPropertyKey, CssProperty<?>> entry : properties.entrySet() ){
				resetProperty( entry.getValue(), entry.getKey() );
			}
		}
		
		private <T> void resetProperty( CssProperty<T> property, CssPropertyKey key ){
			T value;
			if( source == null ){
				value = null;
			}
			else{
				value = source.getProperty( property.getType( scheme ), key );
			}
			property.set( value );
		}

		@Override
		public void propertyAdded( CssPropertyContainer source, String key, CssProperty<?> property ){
			if( source != null ){
				CssPropertyKey cssKey = combinedKey( source, key );
				addProperty( cssKey, property, true );
			}
		}
		@Override
		public void propertyRemoved( CssPropertyContainer source, String key, CssProperty<?> property ){
			if( source != null ){
				CssPropertyKey cssKey = combinedKey( source, key );
				removeProperty( cssKey, property, true );
			}
		}
	}
}
