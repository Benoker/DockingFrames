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

import bibliothek.gui.dock.extension.css.path.CssPathListener;
import bibliothek.gui.dock.extension.css.property.BooleanType;
import bibliothek.gui.dock.extension.css.property.CssTransitionType;
import bibliothek.gui.dock.extension.css.property.IntegerType;
import bibliothek.gui.dock.extension.css.property.font.CssFontModifier;
import bibliothek.gui.dock.extension.css.property.font.CssFontModifierType;
import bibliothek.gui.dock.extension.css.property.font.FontModifyType;
import bibliothek.gui.dock.extension.css.property.paint.ColorType;
import bibliothek.gui.dock.extension.css.property.paint.CssPaint;
import bibliothek.gui.dock.extension.css.property.paint.CssPaintType;
import bibliothek.gui.dock.extension.css.property.shape.CssShape;
import bibliothek.gui.dock.extension.css.property.shape.CssShapeType;
import bibliothek.gui.dock.extension.css.scheme.MatchedCssRule;
import bibliothek.gui.dock.extension.css.transition.CssTransition;
import bibliothek.gui.dock.extension.css.transition.DefaultAnimatedCssRuleChain;
import bibliothek.gui.dock.extension.css.transition.TransitionalCssRuleContent;
import bibliothek.gui.dock.extension.css.transition.TransitionalCssRuleChain;
import bibliothek.gui.dock.extension.css.transition.scheduler.CssScheduler;
import bibliothek.gui.dock.extension.css.transition.scheduler.DefaultCssScheduler;
import bibliothek.gui.dock.extension.css.tree.CssTree;
import bibliothek.gui.dock.util.font.GenericFontModifier.Modify;

/**
 * Represents the contents of some css files. It is a map allowing 
 * the framework to access values read from a css file.
 * @author Benjamin Sigg
 */
public class CssScheme {
	private final Object RULES_LOCK = new Object();
	
	private List<CssRule> rules = new ArrayList<CssRule>();
	
	private Map<CssItem, Match> items = new HashMap<CssItem, CssScheme.Match>();
	private Map<Class<?>, CssType<?>> types = new HashMap<Class<?>, CssType<?>>();
	
	private boolean rulesAreSorted = false;
	private boolean rematchPending = false;
	
	private CssTree tree;
	private CssScheduler scheduler = new DefaultCssScheduler();
	
	private CssRuleListener selectorChangedListener = new CssRuleListener(){
		@Override
		public void selectorChanged( CssRule source ){
			rulesAreSorted = false;
			rematch();
		}
	};
	
	/**
	 * Creates a new scheme
	 */
	public CssScheme(){
		initDefaultTypes();
	}
	
	private void initDefaultTypes(){
		setConverter( CssPaint.class, new CssPaintType() );
		setConverter( CssShape.class, new CssShapeType() );
		setConverter( CssFontModifier.class, new CssFontModifierType() );
		
		setConverter( Color.class, new ColorType() );
		setConverter( Integer.class, new IntegerType() );
		setConverter( Modify.class, new FontModifyType() );
		setConverter( Boolean.class, new BooleanType() );
		
		types.put( CssTransition.class, new CssTransitionType() );
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
		items.put( item, match );
		
		match.searchRule();
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
	 * Searches a {@link CssRule}s whose {@link CssSelector} matches
	 * {@link CssItem}. Then collects the properties of the rule and returns them.
	 * @param item the item for which a rule is searched
	 * @return the properties of the rule, <code>null</code> if nothing was found
	 */
	public CssRuleContent search( CssItem item ){
		synchronized( RULES_LOCK ){
			ensureRulesSorted();
			CssRuleContentUnion result = null;
			
			for( CssRule rule : rules ){
				if( rule.getSelector().matches( item.getPath() )){
					if( result == null ){
						result = new CssRuleContentUnion();
					}
					result.add( rule.getContent() );
				}
			}
			return result;
		}
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
		synchronized( RULES_LOCK ){
			rules.add( rule );
		}
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
		synchronized( RULES_LOCK ){
			rules.remove( rule );
		}
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
		synchronized( RULES_LOCK ){
			this.rules.clear();
		}
		addRules( rules );
	}
	
	/**
	 * Adds all the {@link CssRule}s from <code>rules</code> to this scheme.
	 * @param rules a new set of {@link CssRule}s
	 */
	public void addRules( Collection<CssRule> rules ){
		for( CssRule rule : rules ){
			synchronized( RULES_LOCK ){
				this.rules.add( rule );
			}
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
			synchronized( RULES_LOCK ){
				Collections.sort( rules, new Comparator<CssRule>(){
					public int compare( CssRule a, CssRule b){
						return a.getSelector().getSpecificity().compareTo( b.getSelector().getSpecificity() );
					}
				} );
			}
			rulesAreSorted = true;
		}
	}
	
	/**
	 * Gets the {@link CssScheduler} which is responsible for asynchronous calls to the transitions. 
	 * @return the scheduler, not <code>null</code>
	 */
	public CssScheduler getScheduler(){
		return scheduler;
	}
	
	/**
	 * Sets the scheduler for asynchronous execution of transitions.
	 * @param scheduler the scheduler, not <code>null</code>
	 */
	public void setScheduler( CssScheduler scheduler ){
		if( scheduler == null ){
			throw new IllegalArgumentException( "scheduler must not be null" );
		}
		this.scheduler = scheduler;
	}
		
	/**
	 * Creates a new {@link TransitionalCssRuleChain} which will animate the properties of <code>item</code>.
	 * @param item the item to animate
	 * @return the new set of transitions
	 */
	protected TransitionalCssRuleChain createTransition( CssItem item ){
		return new DefaultAnimatedCssRuleChain( this, item );
	}
	
	/**
	 * Starts a new transition on top of the current transitions of <code>item</code>.
	 * @param item the item to animate
	 * @param transitionKey the name of the {@link CssProperty} describing <code>transition</code>
	 * @param transition the additional transition, will run alongside other currently
	 * running transition
	 * @throws IllegalArgumentException if <code>item</code> cannot be found, or
	 * if <code>transition</code> is <code>null</code>
	 */
	public void animate( CssItem item, CssPropertyKey transitionKey, CssTransition<?> transition ){
		if( transition == null ){
			throw new IllegalArgumentException( "transition must not be null" );
		}
		
		Match match = items.get( item );
		if( match == null ){
			throw new IllegalArgumentException( "item not found" );
		}
		
		match.animate( transitionKey, transition );
	}
	
	/**
	 * Represents a match between a {@link CssRule} and a {@link CssItem}, this
	 * class ensures the transfer of the values from the rule ot the item.
	 * @author Benjamin Sigg
	 */
	private class Match implements CssItemListener, CssPathListener{
		private TransitionalCssRuleChain chain;
		private TransitionalCssRuleContent rule;
		private CssItem item;
		private CssPath path;
		
		private MatchedCssRule currentMatch;
		
		/**
		 * Creates a new match
		 * @param item the item to which to write properties
		 */
		public Match( CssItem item ){
			this.item = item;
			item.addItemListener( this );
			path = item.getPath();
			path.addPathListener( this );
			chain = createTransition( item );
		}
		
		public void destroy(){
			item.removeItemListener( this );
			item.getPath().removePathListener( this );
			chain.destroy();
		}
		
		private void searchRule(){
			setRule( search( item ) );
		}
		
		private void animate( CssPropertyKey transitionKey, CssTransition<?> transition ){
			TransitionalCssRuleContent nextRule = chain.animate( transitionKey, transition );
			if( nextRule != rule ){
				replaceRule( nextRule );
			}
		}
		
		private void setRule( CssRuleContent nextRule ){
			if( rule == null || nextRule != rule.getRoot() ){
				TransitionalCssRuleContent nextAnimatedRule = chain.transition( nextRule );
				if( nextAnimatedRule != rule ){
					replaceRule( nextAnimatedRule );
				}
			}
		}
		
		private void replaceRule( TransitionalCssRuleContent nextRule ){
			boolean firstRule = currentMatch == null;
			
			if( currentMatch != null ){
				currentMatch.outdate();
			}
			rule = nextRule;
			
			currentMatch = new MatchedCssRule( CssScheme.this, item, nextRule );
			rule.onDestroyed( new Destroy( currentMatch ) );
			currentMatch.install( firstRule );
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
	}
	
	private static class Destroy implements Runnable{
		private MatchedCssRule rule;
		
		public Destroy( MatchedCssRule rule ){
			this.rule = rule;
		}
		
		@Override
		public void run(){
			rule.destroy();
		}
	}
}
