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
import bibliothek.gui.dock.extension.css.CssRuleListener;
import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.extension.css.animation.AnimatedCssRule;

/**
 * A {@link MatchedCssRule} is responsible for reading the properties of one {@link CssRule}
 * and forward them to a {@link CssItem}. Due to animations several {@link CssRule}s may be active
 * at the same time, it is the responsibility of this class to find out which {@link CssProperty}s
 * need to be set by an old rule, and which to be set by a new rule.
 * @author Benjamin Sigg
 */
public class MatchedCssRule {
	private static enum Mode{
		NEW, HEAD, OUTDATED, DESTROYED
	}
	
	/** all the properties that are currently monitored */
	private Map<CssPropertyKey, CssProperty<?>> properties = new HashMap<CssPropertyKey, CssProperty<?>>();
	
	/** the owner of this rule */
	private CssScheme scheme;
	
	/** the source of all the properties */
	private CssItem item;
	
	/** the values of all the properties */
	private AnimatedCssRule rule;
	
	/** the behavior of this {@link MatchedCssRule} */
	private Mode mode = Mode.NEW;
	
	private Listener listener = new Listener();
	
	/**
	 * Creates a new match
	 * @param scheme the scheme in whose realm this rule is used
	 * @param item the item whose properties are set
	 * @param rule the rule from which to read properties, can be <code>null</code>
	 */
	public MatchedCssRule( CssScheme scheme, CssItem item, AnimatedCssRule rule ){
		this.scheme = scheme;
		this.item = item;
		this.rule = rule;
		
		item.addPropertyContainerListener( listener );
		rule.addRuleListener( listener );
	}
	
	/**
	 * Install <code>this</code>, starts monitoring all {@link CssProperty}s of the {@link CssItem}.
	 * @param firstRule whether this is the first rule for the {@link CssItem}, the first rule also
	 * call {@link CssProperty#setScheme(CssScheme, String)}
	 */
	public void install( boolean firstRule ){
		if( mode != Mode.NEW ){
			throw new IllegalStateException( "already installed" );
		}
		mode = Mode.HEAD;
		
		for( String key : item.getPropertyKeys() ){
			addProperty( new CssPropertyKey( key ), item.getProperty( key ), firstRule );
		}
	}
	
	private boolean isInput( CssPropertyKey key ){
		if( rule == null ){
			return false;
		}
		return rule.isInput( key );
	}
	
	/**
	 * Informs this {@link MatchedCssRule} that it is no longer working with the most recent
	 * {@link CssRule}. The current {@link CssProperty}s will be copied and kept alive until
	 * {@link #destroy()} is called. 
	 */
	public void outdate(){
		if( mode != Mode.DESTROYED ){
			if( mode == Mode.HEAD ){
				// remove all static properties, keep dynamic properties until destruction
				item.removePropertyContainerListener( listener );
				
				CssPropertyKey[] keys = properties.keySet().toArray( new CssPropertyKey[ properties.size() ] );
				for( CssPropertyKey key : keys ){
					CssProperty<?> property = properties.get( key );
					if( !isInput(key) ){
						removeProperty( key, property, false );
					}
				}
			}
			mode = Mode.OUTDATED;
		}
	}
	
	/**
	 * Informs this {@link MatchedCssRule} that it is no longer in use, all resources are released.
	 */
	public void destroy(){
		if( mode != Mode.DESTROYED ){
			outdate();
			
			mode = Mode.DESTROYED;
			
			item.removePropertyContainerListener( listener );
			if( rule != null ){
				rule.removeRuleListener( listener );
			}
			
			for( CssProperty<?> property : properties.values() ){
				property.removePropertyContainerListener( listener );
			}
			
			for( CssProperty<?> property : properties.values() ){
				property.set( null );
				property.setScheme( null, null );
			}
			properties.clear();
		}
	}

	private CssPropertyKey combinedKey( CssPropertyContainer container, String key ){
		for( Map.Entry<CssPropertyKey, CssProperty<?>> entry : properties.entrySet() ){
			if( entry.getValue() == container ){
				return entry.getKey().append( key );
			}
		}
		return new CssPropertyKey( key );
	}

	private void removeProperty( CssPropertyKey key, CssProperty<?> property, boolean fullRemoval ){
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

	private <T> void addProperty( CssPropertyKey key, CssProperty<T> property, boolean firstRule ){
		if( properties.containsKey( key )){
			throw new IllegalStateException( "property with name '" + key + "' already exists" );
		}
		if( mode == Mode.OUTDATED && !isInput(key) ){
			throw new IllegalStateException( "attempt to register a static property after rule has been outdated" );
		}
		
		if( firstRule ){
			property.setScheme( scheme, key );
		}
		if( rule != null ){
			T value = rule.getProperty( property.getType( scheme ), key );
			if( value != null ){
				property.set( value );
			}
		}
		properties.put( key, property );
		for( String name : property.getPropertyKeys() ){
			addProperty( key.append( name ), property.getProperty( name ), true );
		}
		property.addPropertyContainerListener( listener );
	}
	
	
	private class Listener implements CssRuleListener, CssPropertyContainerListener{
		@Override
		public void selectorChanged( CssRule source ){
			// ignore	
		}
		
		@Override
		public void propertyChanged( CssRule source, CssPropertyKey key ){
			CssProperty<?> sink = properties.get( key );
			if( sink != null ){
				resetProperty( sink, key );
			}
		}
		
		@Override
		public void propertiesChanged( CssRule source ){
			for( Map.Entry<CssPropertyKey, CssProperty<?>> entry : properties.entrySet() ){
				resetProperty( entry.getValue(), entry.getKey() );
			}
		}
		
		private <T> void resetProperty( CssProperty<T> property, CssPropertyKey key ){
			T value;
			if( rule == null ){
				value = null;
			}
			else{
				value = rule.getProperty( property.getType( scheme ), key );
			}
			property.set( value );
		}

		@Override
		public void propertyAdded( CssPropertyContainer source, String key, CssProperty<?> property ){
			if( rule != null ){
				CssPropertyKey cssKey = combinedKey( source, key );
				addProperty( cssKey, property, true );
			}
		}
		@Override
		public void propertyRemoved( CssPropertyContainer source, String key, CssProperty<?> property ){
			if( rule != null ){
				CssPropertyKey cssKey = combinedKey( source, key );
				removeProperty( cssKey, property, true );
			}
		}
	}
}
