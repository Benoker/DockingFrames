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
package bibliothek.gui.dock.extension.css;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.dock.extension.css.animation.AnimatedCssRule;
import bibliothek.gui.dock.extension.css.animation.AnimatedCssRuleChain;
import bibliothek.gui.dock.extension.css.animation.CssAnimation;
import bibliothek.gui.dock.extension.css.animation.DefaultAnimatedCssRuleChain;
import bibliothek.gui.dock.extension.css.paint.CssPaint;
import bibliothek.gui.dock.extension.css.path.CssPathListener;
import bibliothek.gui.dock.extension.css.shape.CssShape;
import bibliothek.gui.dock.extension.css.tree.CssTree;
import bibliothek.gui.dock.extension.css.type.ColorType;
import bibliothek.gui.dock.extension.css.type.CssAnimationType;
import bibliothek.gui.dock.extension.css.type.CssPaintType;
import bibliothek.gui.dock.extension.css.type.CssShapeType;

/**
 * Represents the contents of some css files. It is a map allowing 
 * the framework to access values read from a css file.
 * @author Benjamin Sigg
 */
public class CssScheme {
	/** The separator to combine keys of {@link CssProperty}s to longer keys */
	private static final String SEPARATOR = "-";
	
	private List<CssRule> rules = new ArrayList<CssRule>();
	
	private Map<CssItem, Match> items = new HashMap<CssItem, CssScheme.Match>();
	private Map<Class<?>, CssType<?>> types = new HashMap<Class<?>, CssType<?>>();
	
	private boolean rulesAreSorted = false;
	private boolean rematchPending = false;
	
	private CssTree tree;
	
	private CssRuleListener selectorChangedListener = new CssRuleListener(){
		@Override
		public void selectorChanged( CssRule source ){
			rulesAreSorted = false;
			rematch();
		}
		
		@Override
		public void propertyChanged( CssRule source, String key ){
			// ignore
		}
	};
	
	/**
	 * Creates a new scheme
	 */
	public CssScheme(){
		initDefaultTypes();
	}
	
	private void initDefaultTypes(){
		setConverter( Color.class, new ColorType() );
		setConverter( CssPaint.class, new CssPaintType() );
		setConverter( CssShape.class, new CssShapeType() );
		types.put( CssAnimation.class, new CssAnimationType() );
	}
	
	/**
	 * Sets the document.
	 * @param tree the elements for which this scheme is used
	 */
	public void setTree( CssTree tree ){
		this.tree = tree;
	}
	
	/**
	 * Gets the document (internal cache and factory of paths) for which this scheme
	 * is used.
	 * @return the document, can be <code>null</code>
	 */
	public CssTree getTree(){
		return tree;
	}
	
	/**
	 * Stores a converter for converting {@link String}s into {@link Object}s of
	 * type <code>type</code>. 
	 * @param type the type of the created objects
	 * @param converter the converter to use
	 */
	public <T> void setConverter( Class<T> type, CssType<T> converter ){
		if( converter == null ){
			types.remove( type );
		}
		else{
			types.put( type, converter );
		}
	}
	
	/**
	 * Gets a converter for creating {@link Object}s of type <code>type</code>
	 * @param type the type of the created objects
	 * @return the converter, not <code>null</code>
	 * @throws IllegalStateException if there is no converter registered for <code>type</code>
	 */
	@SuppressWarnings("unchecked")
	public <T> CssType<T> getConverter( Class<T> type ){
		CssType<T> result = (CssType<T>)types.get( type );
		if( result == null ){
			throw new IllegalStateException( "missing converter for type: " + type );
		}
		return result;
	}
	
	/**
	 * Adds <code>item</code> as observer to this map of properties. The properties of
	 * <code>item</code> will be set using the best matching {@link CssRule}.
	 * @param item the new item, not <code>null</code>
	 * @throws IllegalArgumentException if <code>item</code> has already been added
	 */
	public void add( CssItem item ){
		if( items.containsKey( item )){
			throw new IllegalArgumentException( "the item is already added" );
		}
		
		Match match = new Match( item );
		match.searchRule();
		
		items.put( item, match );
	}
	
	/**
	 * Removes <code>item</code> from this map of properties. All properties of
	 * <code>item</code> will be reset to <code>null</code>.
	 * @param item the item to remove, not <code>null</code>
	 */
	public void remove( CssItem item ){
		Match match = items.remove( item );	
		if( match != null ){
			match.destroy();
		}
	}
	
	/**
	 * Searches a {@link CssRule} whose {@link CssSelector} matches
	 * {@link CssItem}.
	 * @param item the item for which a rule is searched
	 * @return the rule or <code>null</code> if nothing was found
	 */
	public CssRule search( CssItem item ){
		ensureRulesSorted();
		for( CssRule rule : rules ){
			if( rule.getSelector().matches( item.getPath() )){
				return rule;
			}
		}
		return null;
	}
	
	/**
	 * Adds <code>rule</code> to this scheme. This method calls {@link #rematch()}, meaning the changes
	 * will be propagated to the {@link CssItem}s later.
	 * @param rule the rule to add, not <code>null</code>
	 */
	public void addRule( CssRule rule ){
		if( rule == null ){
			throw new IllegalArgumentException( "rule must not be null" );
		}
		rules.add( rule );
		rulesAreSorted = false;
		rule.addRuleListener( selectorChangedListener );
		rematch();
	}
	
	/**
	 * Removes <code>rule</code> from this scheme. This method calls {@link #rematch()}, meaning the changes
	 * will be propagated to the {@link CssItem}s later.
	 * @param rule the rule to remove
	 */
	public void removeRule( CssRule rule ){
		rules.remove( rule );
		rule.removeRuleListener( selectorChangedListener );
		rulesAreSorted = false;
		rematch();
	}
	
	/**
	 * Replaces all the current rules with the {@link CssRule}s from <code>rules</code>.
	 * @param rules the new set of {@link CssRule}s
	 */
	public void setRules( Collection<CssRule> rules ){
		for( CssRule rule : this.rules ){
			rule.removeRuleListener( selectorChangedListener );
		}
		this.rules.clear();
		addRules( rules );
	}
	
	/**
	 * Adds all the {@link CssRule}s from <code>rules</code> to this scheme.
	 * @param rules a new set of {@link CssRule}s
	 */
	public void addRules( Collection<CssRule> rules ){
		for( CssRule rule : rules ){
			this.rules.add( rule );
			rule.addRuleListener( selectorChangedListener );
		}
		rulesAreSorted = false;
		rematch();
	}
	
	/**
	 * Schedules a call to {@link #match()}, the call will be executed later in the EDT.
	 */
	public void rematch(){
		if( !rematchPending ){
			EventQueue.invokeLater( new Runnable(){
				@Override
				public void run(){
					match();
				}
			} );
		}
	}
	
	/**
	 * Goes through all currently registered {@link CssItem}s and ensures they are matched with the correct
	 * {@link CssRule}.
	 */
	public void match(){
		rematchPending = false;
		ensureRulesSorted();
		
		for( Match match : items.values() ){
			match.searchRule();
		}
	}
	
	private void ensureRulesSorted(){
		if( !rulesAreSorted ){
			Collections.sort( rules, new Comparator<CssRule>(){
				public int compare( CssRule a, CssRule b){
					return a.getSelector().getSpecificity().compareTo( b.getSelector().getSpecificity() );
				}
			} );
			rulesAreSorted = true;
		}
	}
		
	/**
	 * Creates a new {@link AnimatedCssRuleChain} which will animate the properties of <code>item</code>.
	 * @param item the item to animate
	 * @return the new set of animations
	 */
	protected AnimatedCssRuleChain createAnimation( CssItem item ){
		return new DefaultAnimatedCssRuleChain( this, item );
	}
	
	/**
	 * Starts a new animation on top of the current animations of <code>item</code>.
	 * @param item the item to animate
	 * @param animation the additional animation, will run alongside other currently
	 * running animation
	 * @throws IllegalArgumentException if <code>item</code> cannot be found, or
	 * if <code>animation</code> is <code>null</code>
	 */
	public void animate( CssItem item, CssAnimation<?> animation ){
		if( animation == null ){
			throw new IllegalArgumentException( "animation must not be null" );
		}
		
		Match match = items.get( item );
		if( match == null ){
			throw new IllegalArgumentException( "item not found" );
		}
		
		match.animate( animation );
	}
	
	/**
	 * Represents a match between a {@link CssRule} and a {@link CssItem}, this
	 * class ensures the transfer of the values from the rule ot the item.
	 * @author Benjamin Sigg
	 */
	private class Match implements CssItemListener, CssPathListener, CssRuleListener, CssPropertyContainerListener{
		private AnimatedCssRuleChain chain;
		private AnimatedCssRule rule;
		private CssItem item;
		private CssPath path;
		private Map<String, CssProperty<?>> properties = new HashMap<String, CssProperty<?>>();
		
		/**
		 * Creates a new match
		 * @param item the item to which to write properties
		 */
		public Match( CssItem item ){
			this.item = item;
			item.addItemListener( this );
			path = item.getPath();
			path.addPathListener( this );
			chain = createAnimation( item );
		}
		
		public void destroy(){
			item.removeItemListener( this );
			item.getPath().removePathListener( this );
			if( rule != null ){
				rule.removeRuleListener( this );
			}
			for( String key : item.getPropertyKeys()){
				propertyRemoved( item, key, item.getProperty( key ) );
			}
		}
		
		private void searchRule(){
			setRule( search( item ) );
		}
		
		private void animate( CssAnimation<?> animation ){
			uninstall();
			rule = chain.animate( animation );
			install();
		}
		
		private void setRule( CssRule nextRule ){
			if( rule == null || nextRule != rule.getRoot() ){
				uninstall();
				rule = chain.transition( nextRule );
				install();
			}
		}
		
		private void uninstall(){
			item.removePropertyContainerListener( this );
			if( rule != null ){
				rule.removeRuleListener( this );
			}
			for( String key : item.getPropertyKeys()){
				propertyRemoved( key, item.getProperty( key ) );
			}
		}
		
		private void install(){
			item.addPropertyContainerListener( this );
			rule.addRuleListener( this );
			for( String key : item.getPropertyKeys()){
				propertyAdded( key, item.getProperty( key ) );
			}			
		}
		
		@Override
		public void propertyChanged( CssRule source, String key ){
			CssProperty<?> sink = properties.get( key );
			if( sink != null ){
				resetProperty( sink, key );
			}
		}
		
		private <T> void resetProperty( CssProperty<T> property, String key ){
			T value = rule.getProperty( property.getType( CssScheme.this ), key );
			property.set( value );
		}
		
		@Override
		public void selectorChanged( CssRule source ){
			// ignore	
		}
		
		@Override
		public void pathChanged( CssItem source ){
			path.removePathListener( this );
			path = item.getPath();
			path.addPathListener( this );
			searchRule();
		}
		
		@Override
		public void pathChanged( CssPath path ){
			searchRule();	
		}
		
		private String combinedKey( CssPropertyContainer container, String key ){
			for( Map.Entry<String, CssProperty<?>> entry : properties.entrySet() ){
				if( entry.getValue() == container ){
					return entry.getKey() + SEPARATOR + key;
				}
			}
			return key;
		}
		
		@Override
		public void propertyAdded( CssPropertyContainer source, String key, CssProperty<?> property ){
			if( rule != null ){
				key = combinedKey( source, key );
				propertyAdded( key, property );
			}
		}
		
		private <T> void propertyAdded( String key, CssProperty<T> property ){
			T value = rule.getProperty( property.getType( CssScheme.this ), key );
			if( value != null ){
				property.set( value );
			}
			if( properties.containsKey( key )){
				throw new IllegalStateException( "property with name '" + key + "' already exists" );
			}
			properties.put( key, property );
			property.addPropertyContainerListener( this );
			for( String name : property.getPropertyKeys() ){
				propertyAdded( key + SEPARATOR + name, property.getProperty( name ) );
			}
		}
		
		@Override
		public void propertyRemoved( CssPropertyContainer source, String key, CssProperty<?> property ){
			if( rule != null ){
				key = combinedKey( source, key );
				propertyRemoved( key, property );
			}
		}
		
		private void propertyRemoved( String key, CssProperty<?> property ){
			property.removePropertyContainerListener( this );
			for( String name : property.getPropertyKeys() ){
				propertyRemoved( key + SEPARATOR + name, property.getProperty( name ) );
			}
			properties.remove( key );
			property.set( null );
		}
	}
}
