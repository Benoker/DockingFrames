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

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the contents of some css files. It is a map allowing 
 * the framework to access values read from a css file.
 * @author Benjamin Sigg
 */
public class CssScheme {
	private List<CssRule> rules = new ArrayList<CssRule>();
	
	private Map<CssItem, Match> items = new HashMap<CssItem, CssScheme.Match>();
	
	private boolean rulesAreSorted = false;
	private boolean rematchPending = false;
	
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
		
		if( !rulesAreSorted ){
			Collections.sort( rules, new Comparator<CssRule>(){
				public int compare( CssRule a, CssRule b){
					return a.getSelector().getSpecificity().compareTo( b.getSelector().getSpecificity() );
				}
			} );
			rulesAreSorted = true;
		}
		
		for( Match match : items.values() ){
			match.searchRule();
		}
	}
	
	private <T> void write( String value, CssProperty<T> property ){
		if( "null".equals( value )){
			property.set( null );
		}
		else{
			T converted = property.getType().convert( value );
			if( converted == null ){
				throw new IllegalArgumentException( "'" + value + "' cannot be converted to '" + property.getType() + "'" );
			}
			else{
				property.set( converted );
			}
		}
	}
	
	/**
	 * Represents a match between a {@link CssRule} and a {@link CssItem}, this
	 * class ensures the transfer of the values from the rule ot the item.
	 * @author Benjamin Sigg
	 */
	private class Match implements CssItemListener, CssRuleListener{
		private CssRule rule;
		private CssItem item;
		
		/**
		 * Creates a new match
		 * @param item the item to which to write properties
		 */
		public Match( CssItem item ){
			this.item = item;
			item.addItemListener( this );
		}
		
		public void destroy(){
			item.removeItemListener( this );
			if( rule != null ){
				rule.removeRuleListener( this );
			}
			for( String key : item.getPropertyKeys()){
				item.getProperty( key ).set( null );
			}
		}
		
		private void searchRule(){
			setRule( search( item ) );
		}
		
		private void setRule( CssRule nextRule ){
			if( nextRule != rule ){
				if( rule != null ){
					rule.removeRuleListener( this );
					for( String key : item.getPropertyKeys() ){
						item.getProperty( key ).set( null );
					}
				}
				rule = nextRule;
				if( rule != null ){
					rule.addRuleListener( this );
					for( String key : item.getPropertyKeys() ){
						String value = rule.getProperty( key );
						if( value != null ){
							CssProperty<?> sink = item.getProperty( key );
							write( value, sink );
						}
					}
				}
			}
		}
		
		@Override
		public void propertyChanged( CssRule source, String key ){
			CssProperty<?> sink = item.getProperty( key );
			String value = rule.getProperty( key );
			if( value != null && sink != null ){
				write( value, sink );
			}
		}
		
		@Override
		public void selectorChanged( CssRule source ){
			// ignore	
		}
		
		@Override
		public void pathChanged( CssItem source ){
			searchRule();
		}
		
		@Override
		public void valueAdded( CssItem source, String key ){
			CssProperty<?> sink = item.getProperty( key );
			String value = rule.getProperty( key );
			if( value != null ){
				write( value, sink );
			}
		}
		@Override
		public void valueRemoved( CssItem source, String key ){
			// ignore
		}
	}
}
