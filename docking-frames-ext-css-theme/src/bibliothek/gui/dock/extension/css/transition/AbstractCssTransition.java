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

import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyKey;
import bibliothek.gui.dock.extension.css.CssRuleContent;
import bibliothek.gui.dock.extension.css.CssRuleContentListener;
import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.extension.css.CssType;
import bibliothek.gui.dock.extension.css.property.AbstractCssPropertyContainer;
import bibliothek.gui.dock.extension.css.transition.scheduler.CssSchedulable;
import bibliothek.gui.dock.extension.css.transition.scheduler.CssScheduler;
import bibliothek.util.Filter;

/**
 * This transition offers methods that can help to blend one type of item slowly into another type of item. 
 * The {@link AbstractCssTransition} can observe one or many properties of type <code>T</code>. 
 * If these properties have any sub-properties, then sub-s can be used to modify them as well.
 * @author Benjamin Sigg
 * @param <T> the type of item this  handles
 */
public abstract class AbstractCssTransition<T> extends AbstractCssPropertyContainer implements CssTransition<T>{
	private CssTransitionCallback callback;
	private CssRuleContent source;
	private CssRuleContent target;
	private Filter<CssPropertyKey> propertyFilter;
	
	/** the list of properties which either are animated or required for the  */
	private Map<CssPropertyKey, AnimatedProperty<?>> properties = new HashMap<CssPropertyKey, AnimatedProperty<?>>();
	
	private CssType<T> type;

	private CssRuleContentListener listener = new CssRuleContentListener(){
		@Override
		public void propertiesChanged( CssRuleContent source ){
			for( AnimatedProperty<?> property : properties.values() ){
				property.updateValues();
			}
		}
		
		@Override
		public void propertyChanged( CssRuleContent source, CssPropertyKey key ){
			AnimatedProperty<?> animatedProperty = properties.get( key );
			if( animatedProperty != null ){
				animatedProperty.updateValues();
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
	public boolean isInput( CssPropertyKey key ){
		for( AnimatedProperty<?> value : properties.values() ){
			if( value.dependencies.containsKey( key )){
				return true;
			}
		}
		return false;
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
	public void setPropertyFilter( Filter<CssPropertyKey> propertyFilter ){
		this.propertyFilter = propertyFilter;
	}
	
	@Override
	public void init( CssRuleContent current, CssTransitionCallback callback ){
		this.callback = callback;
		this.source = current;
		source.addRuleContentListener( listener );
		CssPropertyKey[] keys = callback.getPropertiesOfType( type );
		for( CssPropertyKey key : keys ){
			if( propertyFilter == null || propertyFilter.includes( key )){
				AnimatedProperty<T> property = new AnimatedProperty<T>( key, createProperty( type, key ), type );
				properties.put( key, property );
				property.updateValues();
			}
		}
	}
	
	/**
	 * Sets the progress of the animation and schedules a new call to {@link #step(int)}. 
	 * @param progress the current progress, between <code>0</code> and <code>1</code>
	 */
	protected void updateProgress( double progress ){
		for( AnimatedProperty<?> next : properties.values()){
			next.updateProgress( progress );
		}
		callback.step();		
	}
	
	/**
	 * Informs all properties that the animation is over.
	 */
	protected void endAnimation(){
		for( AnimatedProperty<?> next : properties.values()){
			next.end();
		}		
	}
	
	@Override
	public void transition( CssRuleContent destination ){
		target = destination;
		
		target.addRuleContentListener( listener );
		
		for( AnimatedProperty<?> property : properties.values() ){
			property.updateValues();
		}
		
		callback.step();
	}
	
	/**
	 * Creates a new animated property.
	 * @param type the type of the property
	 * @param key the name of the property
	 * @return the , not <code>null</code>
	 */
	protected abstract TransitionalCssProperty<T> createProperty( CssType<T> type, CssPropertyKey key );
	
	/**
	 * A wrapper around an {@link TransitionalCssProperty}, knows which other properties are required
	 * for the {@link TransitionalCssProperty} (the dependencies), and automatically updates the
	 * list of dependencies by creating sub-{@link AnimatedProperty}s.
	 * @author Benjamin Sigg
	 * @param <S> the kind of object handled by the wrapped {@link TransitionalCssProperty} 
	 */
	private class AnimatedProperty<S> implements TransitionalCssPropertyCallback<S>, CssSchedulable{
		private TransitionalCssProperty<S> property;
		
		private CssPropertyKey key;
		private CssType<S> type;
		
		private Map<CssPropertyKey, CssProperty<?>> dependencies = new HashMap<CssPropertyKey,CssProperty<?>>();
		
		public AnimatedProperty( CssPropertyKey key, TransitionalCssProperty<S> property, CssType<S> type ){
			this.key = key;
			this.property = property;
			this.type = type;
			property.setCallback( this );
		}
		
		@Override
		public void addSourceDependency( String key, CssProperty<?> property ){
			callback.addSourceDependency( key, property );
			dependencies.put( this.key.append( key ), property );
		}
		
		@Override
		public void removeSourceDependency( String key ){
			dependencies.remove( this.key.append( key ) );
			callback.removeSourceDependency( key );
		}
		
		@Override
		public void addTargetDependency( String key, CssProperty<?> property ){
			callback.addTargetDependency( key, property );	
		}
		
		@Override
		public void removeTargetDependency( String key ){
			callback.removeTargetDependency( key );	
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

		@Override
		public void set( S value ){
			callback.setProperty( type, key, value );
		}

		@Override
		public void step( CssScheduler scheduler, int delay ){
			property.step( delay );
		}
		
		@Override
		public void step(){
			getScheme().getScheduler().step( this );
		}

		@Override
		public void step( int delay ){
			getScheme().getScheduler().step( this, delay );
		}
	}
}
