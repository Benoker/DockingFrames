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

import bibliothek.gui.dock.extension.css.CssItem;
import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyContainer;
import bibliothek.gui.dock.extension.css.CssPropertyContainerListener;
import bibliothek.gui.dock.extension.css.CssScheme;

/**
 * This {@link CssTransitionProperty} handles values that are {@link CssPropertyContainer}s, and thus
 * have sub-properties that must be made visible as well.<br>
 * Please read about the behavior of transitional properties with sub-properties in {@link TransitionalCssProperty}. 
 * @author Benjamin Sigg
 * @param <T> the kind of value handled by this class
 */
public abstract class CssContainerTransitionProperty<T extends CssPropertyContainer> extends CssTransitionProperty<T> {
	private T value;
	private CssPropertyContainerListener listener = new CssPropertyContainerListener(){
		@Override
		public void propertyRemoved( CssPropertyContainer source, String key, CssProperty<?> property ){
			firePropertyRemoved( key, property );
		}
		
		@Override
		public void propertyAdded( CssPropertyContainer source, String key, CssProperty<?> property ){
			firePropertyAdded( key, property );
		}
	};
	
	/**
	 * Creates a new property
	 * @param scheme the scheme which manages the transition
	 * @param item the item that is animated
	 */
	public CssContainerTransitionProperty( CssScheme scheme, CssItem item ){
		super( scheme, item );
	}

	@Override
	public void set( T value ){
		if( this.value != value ){
			if( isBound() ){
				if( this.value != null ){
					this.value.removePropertyContainerListener( listener );
					for( String key : this.value.getPropertyKeys() ){
						firePropertyRemoved( key, getProperty( key ) );
					}
				}
				this.value = value;
				if( this.value != null ){
					this.value.addPropertyContainerListener( listener );
					for( String key : this.value.getPropertyKeys() ){
						CssProperty<?> property = getProperty( key );
						if( property != null ){
							firePropertyAdded( key, property );
						}
					}
				}
			}
			else{
				this.value = value;
			}
			propertyChanged( this.value );
		}
	}
	
	/**
	 * Called if the value of this property changed.
	 * @param value the new value, may be <code>null</code>
	 */
	protected abstract void propertyChanged( T value );
	
	@Override
	protected void bind(){
		super.bind();
		if( value != null ){
			value.addPropertyContainerListener( listener );
		}
	}
	
	@Override
	protected void unbind(){
		super.unbind();
		if( value != null ){
			value.removePropertyContainerListener( listener );
		}
	}
	
	@Override
	public CssProperty<?> getProperty( String key ){
		CssProperty<?> result = super.getProperty( key );
		if( result == null && value != null ){
			result = value.getProperty( key );
		}
		return result;
	}
	
	@Override
	public String[] getPropertyKeys(){
		String[] keys = super.getPropertyKeys();
		if( value != null ){
			String[] sub = value.getPropertyKeys();
			if( sub.length > 0 ){
				String[] copy = new String[ keys.length + sub.length ];
				System.arraycopy( keys, 0, copy, 0, keys.length );
				System.arraycopy( sub, 0, copy, keys.length, sub.length );
				keys = copy;
			}
		}
		return keys;
	}
}
