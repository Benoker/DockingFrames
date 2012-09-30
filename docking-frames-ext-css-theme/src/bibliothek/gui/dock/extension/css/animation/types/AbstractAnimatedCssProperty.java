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
package bibliothek.gui.dock.extension.css.animation.types;

import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyContainer;
import bibliothek.gui.dock.extension.css.CssPropertyKey;
import bibliothek.gui.dock.extension.css.animation.AnimatedCssProperty;
import bibliothek.gui.dock.extension.css.animation.AnimatedCssPropertyCallback;
import bibliothek.gui.dock.extension.css.util.CssPropertyTreeObserver;

/**
 * This {@link AnimatedCssProperty} handles objects of type {@link CssPropertyContainer}, it automatically requests and
 * handles the sub-properties. 
 * @author Benjamin Sigg
 * @param <T> the type of object animated by this property
 */
public abstract class AbstractAnimatedCssProperty<T extends CssPropertyContainer> implements AnimatedCssProperty<T> {
	private AnimatedCssPropertyCallback<T> callback;
	
	private T source;
	private T target;
	
	private double transition = 0;
	
	private Listener listener;
	
	@Override
	public void setCallback( AnimatedCssPropertyCallback<T> callback ){
		this.callback = callback;
	}
	
	/**
	 * Gets the current callback.
	 * @return the callback used to forward new values
	 */
	public AnimatedCssPropertyCallback<T> getCallback(){
		return callback;
	}

	@Override
	public void setSource( T source ){
		if( this.source != null ){
			listener.setListening( false );
			listener = null;
		}
		this.source = source;
		if( source != null ){
			listener = new Listener( source );
			listener.setListening( true );
		}
		update();
	}

	/**
	 * Gets the current source value.
	 * @return the last value from {@link #setSource(CssPropertyContainer)}
	 */
	public T getSource(){
		return source;
	}
	
	@Override
	public void setTarget( T target ){
		this.target = target;
		update();
	}
	
	/**
	 * Gets the current target value.
	 * @return the last value from {@link #setTarget(CssPropertyContainer)}
	 */
	public T getTarget(){
		return target;
	}
	
	@Override
	public void setTransition( double transition ){
		this.transition = transition;
		update();
	}
	
	/**
	 * Gets the current progress of the transition.
	 * @return a value between <code>0</code> and <code>1</code>
	 */
	public double getTransition(){
		return transition;
	}

	@Override
	public void step( int delay ){
		update();
	}
	
	/**
	 * Called every time one of the properties of this {@link AbstractAnimatedCssProperty} is changed.
	 */
	protected abstract void update();
	

	private class Listener extends CssPropertyTreeObserver{
		public Listener( CssPropertyContainer root ){
			super( root );
		}

		@Override
		protected void onAdded( CssPropertyKey key, CssProperty<?> property ){
			callback.addDependency( key );	
		}
		
		@Override
		protected void onRemoved( CssPropertyKey key ){
			callback.removeDependency( key );
		}
	}
}
