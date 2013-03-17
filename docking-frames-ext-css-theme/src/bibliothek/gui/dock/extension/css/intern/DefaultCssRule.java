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
package bibliothek.gui.dock.extension.css.intern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.dock.extension.css.CssItem;
import bibliothek.gui.dock.extension.css.CssPropertyKey;
import bibliothek.gui.dock.extension.css.CssRule;
import bibliothek.gui.dock.extension.css.CssRuleContent;
import bibliothek.gui.dock.extension.css.CssRuleContentListener;
import bibliothek.gui.dock.extension.css.CssRuleListener;
import bibliothek.gui.dock.extension.css.CssSelector;
import bibliothek.gui.dock.extension.css.CssType;
import bibliothek.gui.dock.extension.css.CssDeclarationValue;

/**
 * The default {@link CssRule} is just a {@link Map} of properties.
 * @author Benjamin Sigg
 */
public class DefaultCssRule implements CssRule, CssRuleContent{
	/** tells which {@link CssItem} are affected by this rule */
	private CssSelector selector;
	
	/** all the listeners that have been added to this rule */
	private List<CssRuleListener> listeners = new ArrayList<CssRuleListener>( 2 );
	
	/** all the listeners that have been added to this rule-content */
	private List<CssRuleContentListener> contentListeners = new ArrayList<CssRuleContentListener>( 2 );
	
	/** all the properties of this rule */
	private Map<CssPropertyKey, CssDeclarationValue> properties = new HashMap<CssPropertyKey, CssDeclarationValue>( 5 );
	
	/**
	 * Creates a new rule
	 * @param selector tells which {@link CssItem}s are affected by this rule, not <code>null</code>
	 */
	public DefaultCssRule( CssSelector selector ){
		setSelector( selector );
	}
	
	@Override
	public CssSelector getSelector(){
		return selector;
	}
	
	/**
	 * Sets the selector, it tells which {@link CssItem}s are affected by this rule.
	 * @param selector the new selector, not <code>null</code>
	 */
	public void setSelector( CssSelector selector ){
		if( selector == null ){
			throw new IllegalArgumentException( "the selector must not be null" );
		}
		this.selector = selector;
		for( CssRuleListener listener : listeners.toArray( new CssRuleListener[ listeners.size() ] )){
			listener.selectorChanged( this );
		}
	}
	
	@Override
	public CssRuleContent getContent(){
		return this;
	}
	
	@Override
	public <T> T getProperty( CssType<T> type, CssPropertyKey property ){
		CssDeclarationValue value = properties.get( property );
		if( value == null ){
			return null;
		}
		return type.convert( properties.get( property ) );
	}

	/**
	 * Sets or removes a property of this rule.
	 * @param key the name of the property to set, will be forwarded to {@link CssPropertyKey#parse(String)} to 
	 * convert into a real key
	 * @param value the value of the property or <code>null</code>
	 */
	public void setProperty( String key, String value ){
		if( value == null ){
			setProperty( CssPropertyKey.parse( key ), null );
		}
		else{
			setProperty( CssPropertyKey.parse( key ), new CssDeclarationValue( value ) );
		}
	}
	
	/**
	 * Sets or removes a property of this rule.
	 * @param key the name of the property to set
	 * @param value the value of the property or <code>null</code>
	 */
	public void setProperty( CssPropertyKey key, CssDeclarationValue value ){
		if( value == null ){
			properties.remove( key );
		}
		else{
			properties.put( key, value );
		}
		for( CssRuleContentListener listener : contentListeners.toArray( new CssRuleContentListener[ contentListeners.size() ] )){
			listener.propertyChanged( this, key );
		}
	}

	@Override
	public void addRuleContentListener( CssRuleContentListener listener ){
		if( listener == null ){
			throw new IllegalArgumentException( "listener must not be null" );
		}
		contentListeners.add( listener );
	}
	
	@Override
	public void removeRuleContentListener( CssRuleContentListener listener ){
		contentListeners.remove( listener );	
	}
	
	@Override
	public void addRuleListener( CssRuleListener listener ){
		if( listener == null ){
			throw new IllegalArgumentException( "listener must not be null" );
		}
		listeners.add( listener );
	}

	@Override
	public void removeRuleListener( CssRuleListener listener ){
		listeners.remove( listener );
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append( selector );
		builder.append( "{" );
		for( Map.Entry<CssPropertyKey, CssDeclarationValue> entry : properties.entrySet() ){
			builder.append( "\n" );
			builder.append( entry.getKey() ).append( ": " ).append( entry.getValue() );
		}
		builder.append( "\n}" );
		return builder.toString();
	}
}
