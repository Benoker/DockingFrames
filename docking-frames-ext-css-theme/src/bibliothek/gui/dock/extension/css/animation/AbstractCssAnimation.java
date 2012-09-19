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
package bibliothek.gui.dock.extension.css.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssRule;
import bibliothek.gui.dock.extension.css.CssRuleListener;
import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.extension.css.CssType;
import bibliothek.gui.dock.extension.css.property.AbstractCssPropertyContainer;
import bibliothek.util.Filter;

/**
 * This animation blends one type of item slowly into another type of item. The animation can observe one or many
 * properties of type <code>T</code>. If these properties have any sub-properties, then sub-animations can be used
 * to modify them as well.
 * @author Benjamin Sigg
 * @param <T> the type of item this animation handles
 */
public abstract class AbstractCssAnimation<T> extends AbstractCssPropertyContainer implements CssAnimation<T>{
	private CssAnimationCallback callback;
	private CssRule source;
	private CssRule target;
	private Filter<String> propertyFilter;
	
	private int duration = 1000;
	private int time = 0;
	
	private Map<String, AnimatedProperty<?>> properties = new HashMap<String, AnimatedProperty<?>>();
	private List<AnimatedProperty<?>> ordered = null;
	
	private CssType<T> type;
	
	private CssRuleListener listener = new CssRuleListener(){
		@Override
		public void selectorChanged( CssRule source ){
			// ignore
		}
		
		@Override
		public void propertiesChanged( CssRule source ){
			for( AnimatedProperty<?> property : properties.values() ){
				property.updateValues();
			}
		}
		
		@Override
		public void propertyChanged( CssRule source, String key ){
			AnimatedProperty<?> property = properties.get( key );
			if( property != null ){
				property.updateValues();
			}
		}
	};
	
	@Override
	public void setType( CssType<T> type ){
		if( type == null ){
			throw new IllegalArgumentException( "type must not be null" );
		}
		this.type = type;
	}
	
	@Override
	public String[] getPropertyKeys(){
		return new String[]{};
	}

	@Override
	public CssProperty<?> getProperty( String key ){
		return null;
	}
	
	@Override
	public void setPropertyFilter( Filter<String> propertyFilter ){
		this.propertyFilter = propertyFilter;
	}
	
	@Override
	public void init( CssRule current, CssAnimationCallback callback ){
		this.callback = callback;
		this.source = current;
		source.addRuleListener( listener );
		String[] keys = callback.getPropertiesOfType( type );
		for( String key : keys ){
			if( propertyFilter == null || propertyFilter.includes( key )){
				AnimatedProperty<T> property = new AnimatedProperty<T>( key, createProperty( type, key ), type );
				properties.put( key, property );
				property.stepNow();
				
			}
		}
	}
	
	@Override
	public void step( int delay ){
		if( delay != -1 ){
			time += delay;
		}
		if( time > duration ){
			for( AnimatedProperty<?> next : properties()){
				next.end();
			}
		}
		else{
			double progress = time / (double)duration;
			for( AnimatedProperty<?> next : properties()){
				next.updateProgress( progress );
			}
			callback.step();
		}
	}
	
	private Iterable<AnimatedProperty<?>> properties(){
		if( ordered == null ){
			ordered = new ArrayList<AnimatedProperty<?>>();
			Set<AnimatedProperty<?>> roots = new HashSet<AnimatedProperty<?>>();
			roots.addAll( properties.values() );
			for( AnimatedProperty<?> property : properties.values() ){
				if( property.observers != null ){
					for( AnimatedProperty<?> next : property.observers.values() ){
						roots.remove( next );
					}
				}
			}
			Set<AnimatedProperty<?>> visited = new HashSet<AnimatedProperty<?>>();
			Queue<AnimatedProperty<?>> queue = new LinkedList<AnimatedProperty<?>>();
			queue.addAll( roots );
			while( !queue.isEmpty() ){
				AnimatedProperty<?> head = queue.poll();
				ordered.add( head );
				if( head.observers != null ){
					for( AnimatedProperty<?> next : head.observers.values() ){
						if( visited.add( next )){
							queue.add( next );
						}
					}
				}
			}
		}
		return ordered;
	}

	@Override
	public void transition( CssRule destination ){
		target = destination;
		
		target.addRuleListener( listener );
		
		for( AnimatedProperty<?> property : properties() ){
			property.updateValues();
		}
		
		callback.step();
	}
	
	@SuppressWarnings("unchecked")
	private <S> AnimatedProperty<S> getOrCreateProperty( CssType<S> type, String key ){
		AnimatedProperty<S> property = (AnimatedProperty<S>)properties.get( key );
		if( property == null ){
			AnimatedCssProperty<S> sub = createSubProperty( type, key );
			if( sub == null ){
				sub = new NotAnimatedCssProperty<S>();
			}
			property = new AnimatedProperty<S>( key, sub, type );
			properties.put( key, property );
		}
		ordered = null;
		return property;
	}
	
	/**
	 * Creates a new animated property.
	 * @param type the type of the property
	 * @param key the name of the property
	 * @return the animation, not <code>null</code>
	 */
	protected abstract AnimatedCssProperty<T> createProperty( CssType<T> type, String key );
	
	/**
	 * Creates a new animated sub-property.
	 * @param type the type of the property
	 * @param key the key of the property
	 * @return the new animated property, can be <code>null</code>
	 */
	protected abstract <S> AnimatedCssProperty<S> createSubProperty( CssType<S> type, String key );
	
	private class AnimatedProperty<S> implements AnimatedCssPropertyCallback<S>{
		private AnimatedCssProperty<S> property;
		
		private Map<String, AnimatedProperty<?>> observers;
		private Map<String, Integer> dependencies;
		
		private String key;
		private CssType<S> type;
		
		public AnimatedProperty( String key, AnimatedCssProperty<S> property, CssType<S> type ){
			this.key = key;
			this.property = property;
			this.type = type;
			property.setCallback( this );
		}
		
		@Override
		public CssScheme getScheme(){
			return callback.getScheme();
		}
		
		public void end(){
			set( target.getProperty( type, key ));
		}
		
		public void updateValues(){
			property.setSource( source.getProperty( type, key ) );
			if( target != null ){
				property.setTarget( target.getProperty( type, key ) );
			}
		}
		
		public void updateProgress( double progress ){
			property.setTransition( progress );
		}
		
		private void addObserver( String key, AnimatedProperty<?> dependency ){
			if( observers == null ){
				observers = new HashMap<String, AnimatedProperty<?>>();
			}
			if( observers.containsKey( key )){
				throw new IllegalArgumentException( "property '" + key + "' has already been added" );
			}
			
			if( dependency == this ){
				throw new IllegalArgumentException( "cannot have a dependency to myself" );
			}
			Set<AnimatedProperty<?>> found = new HashSet<AnimatedProperty<?>>();
			check( dependency, found );
			observers.put( key, dependency );
		}
		
		private void removeObserver( String key ){
			observers.remove( key );
		}
		
		private void check( AnimatedProperty<?> dependency, Set<AnimatedProperty<?>> found ){
			if( dependency.observers != null ){
				for( AnimatedProperty<?> next : dependency.observers.values() ){
					if( next == this ){
						throw new IllegalArgumentException( "dependency cicle detected" );
					}
					if( !found.add( next )){
						check( next, found );
					}
				}
			}
		}

		@Override
		public void set( S value ){
			callback.setProperty( type, key, value );
			if( observers != null ){
				for( AnimatedProperty<?> next : observers.values() ){
					next.stepNow();
				}
			}
		}

		@Override
		public <R> R get( CssType<R> type, String key ){
			return callback.getProperty( type, key );
		}

		@Override
		public void addProperty( CssType<?> type, String key ){
			if( dependencies == null ){
				dependencies = new HashMap<String, Integer>();
			}
			Integer count = dependencies.get( key );
			if( count != null ){
				count = count+1;
				dependencies.put( key, count );
			}
			else{
				getOrCreateProperty( type, key ).addObserver( this.key, this );
				dependencies.put( key, 1 );
			}
		}

		@Override
		public void removeProperty( String key ){
			if( dependencies != null ){
				Integer count = dependencies.get( key );
				if( count != null ){
					if( count.intValue() == 1 ){
						getOrCreateProperty( null, key ).removeObserver( this.key );
						dependencies.remove( key );
					}
					else{
						count = count-1;
						dependencies.put( key, count );
					}
				}
			}
		}
		
		private void stepNow(){
			property.step();
		}

		@Override
		public void step(){
			// TODO Auto-generated method stub
			
		}

		@Override
		public void step( int delay ){
			// TODO Auto-generated method stub
			
		}
	}
}
