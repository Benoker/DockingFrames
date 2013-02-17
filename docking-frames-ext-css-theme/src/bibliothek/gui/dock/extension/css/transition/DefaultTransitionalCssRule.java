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
package bibliothek.gui.dock.extension.css.transition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.dock.extension.css.CssItem;
import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyContainer;
import bibliothek.gui.dock.extension.css.CssPropertyKey;
import bibliothek.gui.dock.extension.css.CssRule;
import bibliothek.gui.dock.extension.css.CssRuleContent;
import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.extension.css.CssType;
import bibliothek.gui.dock.extension.css.property.AbstractCssPropertyContainer;
import bibliothek.gui.dock.extension.css.scheme.PropertyForwarder;
import bibliothek.gui.dock.extension.css.transition.scheduler.CssSchedulable;
import bibliothek.gui.dock.extension.css.transition.scheduler.CssScheduler;

/**
 * The default implementation of {@link TransitionalCssRuleContent} makes use of one {@link CssRule} and a list
 * of {@link CssTransition}s to perform transitions. 
 * @author Benjamin Sigg
 */
public class DefaultTransitionalCssRule extends AbstractTransitionalCssRule {
	private CssRuleContent root;
	private WrappedCssRuleContent source;
	private List<Transition> transitions = new ArrayList<Transition>();
	
	private WrappedCssRuleContent target;
	private boolean transition = false;
	
	/**
	 * Creates a new animated rule.
	 * @param root the root rule, the source of all properties, can be <code>null</code>
	 */
	public DefaultTransitionalCssRule( CssRuleContent root ){
		this.root = root;
		source = new WrappedCssRuleContent( root );
		target = new WrappedCssRuleContent( null );
	}
	
	@Override
	protected void setPrevious( TransitionalCssRuleContent previous ){
		super.setPrevious( previous );
		if( previous == null ){
			source.setRule( root );
		}
		else{
			source.setRule( previous );
		}
	}
	
	@Override
	public void animate( CssPropertyKey transitionKey, CssTransition<?> transition ){
		Transition callback = new Transition( transitionKey, transition );
		transitions.add( callback );
		transition.init( source, callback );
		if( this.transition ){
			transition.transition( target );
		}
	}
	
	@Override
	public void transition( CssRuleContent root ){
		target.setRule( root );
		transition = true;
		if( transitions.isEmpty() ){
			getLink().remove();
		}
		else{
			for( Transition transition : transitions ){
				transition.transition.transition( root );
			}
		}
	}
	
	@Override
	public boolean isInput( CssPropertyKey property ){
		for( Transition transition : transitions ){
			if( transition.transition.isInput( property )){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isAnimated( CssPropertyKey property ){
		for( Transition transition : transitions ){
			if( transition.overridenProperties.containsKey( property )){
				return true;
			}
		}
		TransitionalCssRuleContent previous = getPrevious();
		if( previous == null ){
			return false;
		}
		return previous.isAnimated( property );
	}
	
	@Override
	public CssRuleContent getRoot(){
		return root;
	}
	
	@Override
	public String toString(){
		return "animated: " + root.toString();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty( CssType<T> type, CssPropertyKey property ){
		TransitionalProperty<?> transitional = null;
		for( Transition transition : transitions ){
			transitional = transition.overridenProperties.get( property );
			if( transitional != null ){
				break;
			}
		}
		
		if( transitional == null ){
			TransitionalCssRuleContent previous = getPrevious();
			if( previous == null || !previous.isAnimated( property )){
				return root.getProperty( type, property );
			}
			return previous.getProperty( type, property );
		}
		if( !transitional.type.equals( type )){
			throw new IllegalArgumentException( "type conflict, expected " + type + ", but found " + transitional.type + " for property " + property );
		}
		return (T)transitional.value;
	}
	
	private static class TransitionalProperty<T>{
		private CssType<T> type;
		private T value;
		
		public TransitionalProperty( CssType<T> type, T value ){
			this.type = type;
			this.value = value;
		}
	}
	
	private class TargetDependencies extends AbstractCssPropertyContainer{
		private Map<String, CssProperty<?>> dependencies = new HashMap<String, CssProperty<?>>();
		private PropertyForwarder targetForwarder;
		
		private TargetDependencies( final Transition transition ){
			targetForwarder = new PropertyForwarder( target, this, transition.getScheme() ){
				@Override
				protected CssPropertyKey combinedKey( CssPropertyContainer container, String key ){
					CssPropertyKey result = super.combinedKey( container, key );
					if( result.length() == 1 ){
						result = transition.transitionalKey.append( key );
					}
					return result;
				}
			};
		}
		
		@Override
		public String[] getPropertyKeys(){
			return dependencies.keySet().toArray( new String[ dependencies.size() ] );
		}

		@Override
		public CssProperty<?> getProperty( String key ){
			return dependencies.get( key );
		}
		
		public void addTargetDependency( String key, CssProperty<?> property ){
			if( dependencies.containsKey( key )){
				throw new IllegalArgumentException( "key '" + key + "' is already asigned to another property" );
			}
			dependencies.put( key, property );
			firePropertyAdded( key, property );
		}
		
		public void removeTargetDependency( String key ){
			CssProperty<?> property = dependencies.remove( key );
			if( property != null ){
				firePropertyRemoved( key, property );
			}
		}
		
		public void destroy(){
			targetForwarder.destroy();
		}

		@Override
		protected void bind(){
			// ignore
		}

		@Override
		protected void unbind(){
			// ignore
		}
	}
	
	private class Transition extends AbstractCssPropertyContainer implements CssTransitionCallback, CssSchedulable{
		private CssPropertyKey transitionalKey;
		private CssTransition<?> transition;
		private Map<CssPropertyKey, TransitionalProperty<?>> overridenProperties = new HashMap<CssPropertyKey, TransitionalProperty<?>>();
		private Map<String, CssProperty<?>> sourceDependencies = new HashMap<String, CssProperty<?>>();
		private TargetDependencies targetDependencies;
		
		private PropertyForwarder sourceForwarder;
		
		public Transition( CssPropertyKey transitionKey, CssTransition<?> transition ){
			this.transitionalKey = transitionKey;
			this.transition = transition;
			targetDependencies = new TargetDependencies( this );
			sourceForwarder = new PropertyForwarder( DefaultTransitionalCssRule.this, this, getScheme() ){
				@Override
				protected CssPropertyKey combinedKey( CssPropertyContainer container, String key ){
					CssPropertyKey result = super.combinedKey( container, key );
					if( result.length() == 1 ){
						result = Transition.this.transitionalKey.append( key );
					}
					return result;
				}
			};
		}
		
		@Override
		public CssScheme getScheme(){
			return getLink().getChain().getScheme();
		}
		
		public CssItem getItem(){
			return getLink().getChain().getItem();
		}

		@Override
		public void step( CssScheduler scheduler, int delay ){
			if( transition != null ){	
				transition.step( delay );
			}
		}
		
		@Override
		public <T> CssPropertyKey[] getPropertiesOfType( CssType<T> type ){
			CssItem item = getItem();
			List<CssPropertyKey> result = new ArrayList<CssPropertyKey>();
			for( String key : item.getPropertyKeys() ){
				if( item.getProperty( key ).getType( getScheme() ).equals( type )){
					result.add( new CssPropertyKey( key ));
				}
			}
			return result.toArray( new CssPropertyKey[ result.size() ] );
		}

		@Override
		public <T> void setProperty( CssType<T> type, CssPropertyKey key, T value ){
			overridenProperties.put( key, new TransitionalProperty<T>( type, value ) );
			fireChanged( key );
		}
		
		@Override
		public <T> T getProperty( CssType<T> type, CssPropertyKey key ){
			return DefaultTransitionalCssRule.this.getProperty( type, key );
		}
		
		@Override
		public void addSourceDependency( String key, CssProperty<?> property ){
			if( sourceDependencies.containsKey( key )){
				throw new IllegalArgumentException( "key '" + key + "' is already asigned to another property" );
			}
			sourceDependencies.put( key, property );
			firePropertyAdded( key, property );
		}
		
		@Override
		public void removeSourceDependency( String key ){
			CssProperty<?> property = sourceDependencies.remove( key );
			if( property != null ){
				firePropertyRemoved( key, property );
			}
		}
		
		@Override
		public void addTargetDependency( String key, CssProperty<?> property ){
			targetDependencies.addTargetDependency( key, property );	
		}
		
		@Override
		public void removeTargetDependency( String key ){
			targetDependencies.removeTargetDependency( key );	
		}
		
		@Override
		public String[] getPropertyKeys(){
			return sourceDependencies.keySet().toArray( new String[ sourceDependencies.size() ] );
		}
		
		@Override
		public CssProperty<?> getProperty( String key ){
			return sourceDependencies.get( key );
		}

		@Override
		protected void bind(){
			// TODO Auto-generated method stub
			
		}
		
		@Override
		protected void unbind(){
				// TODO Auto-generated method stub
				
		}
		
		@Override
		public void step(){
			getLink().getChain().getScheme().getScheduler().step( this );
		}

		@Override
		public void step( int delay ){
			getLink().getChain().getScheme().getScheduler().step( this, delay );
		}
		
		@Override
		public void destroyed(){
			sourceForwarder.destroy();
			targetDependencies.destroy();
			transitions.remove( this );
			transition = null;
			for( CssPropertyKey key : overridenProperties.keySet() ){
				fireChanged( key );
			}
			if( transitions.isEmpty() ){
				RuleChainLink link = getLink();
				if( link != null ){
					link.remove();
				}
			}
		}
	}
}
