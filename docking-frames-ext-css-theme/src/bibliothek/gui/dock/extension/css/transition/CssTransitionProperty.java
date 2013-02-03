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
import bibliothek.gui.dock.extension.css.CssPropertyKey;
import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.extension.css.CssType;
import bibliothek.gui.dock.extension.css.property.AbstractContainerCssProperty;
import bibliothek.gui.dock.extension.css.property.AbstractCssPropertyContainer;
import bibliothek.gui.dock.extension.css.transition.types.InstantCssTransition;
import bibliothek.util.filter.PresetFilter;

/**
 * This property adds a transition to a property. There will be an additional sub-property "transition" telling
 * how the transition should look like.<br>
 * Note that if <code>T</code> is a {@link CssPropertyContainer}, using {@link CssContainerTransitionProperty} is
 * more appropriate. 
 * @author Benjamin Sigg
 * @param <T> the type of value this property enhances with an transition
 */
public abstract class CssTransitionProperty<T> extends AbstractCssPropertyContainer implements CssProperty<T>{
	private CssScheme scheme;
	private CssItem item;
	private CssPropertyKey propertyKey;
	private CssTransition<T> currentTransition;
	
	private AbstractContainerCssProperty<CssTransition<T>> transition = new AbstractContainerCssProperty<CssTransition<T>>(){
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public CssType<CssTransition<T>> getType( CssScheme scheme ){
			Class<CssTransition<T>> clazz = (Class<CssTransition<T>>)(Class)CssTransition.class;
			return scheme.getConverter( clazz );
		}
		
		@Override
		public void setScheme( CssScheme scheme, CssPropertyKey key ){
			// ignore
		}
		
		@Override
		protected void propertyChanged( CssTransition<T> value ){
			if( propertyKey == null && value != null ){
				throw new IllegalStateException( "the value of this property is set, but the property is not yet in use" );
			}
			
			if( value == null ){
				// inform the system that this property *may* be involved in an transition
				value = new InstantCssTransition<T>();
					
			}
			if( value != currentTransition ){
				value.setType( CssTransitionProperty.this.getType( scheme ) );
				value.setPropertyFilter( new PresetFilter<CssPropertyKey>( propertyKey ) );
				scheme.animate( item, propertyKey, value );
			}
			
			currentTransition = value;
		}
	};
	
	/**
	 * Creates a new transition property
	 * @param scheme the scheme which managed the transitions
	 * @param item the item that is animated
	 */
	public CssTransitionProperty( CssScheme scheme, CssItem item ){
		this.scheme = scheme;
		this.item = item;
	}
	
	@Override
	public void setScheme( CssScheme scheme, CssPropertyKey key ){
		if( propertyKey != null && key != null ){	
			throw new IllegalStateException( "this property is already in use, it cannot be used at two places at the same time" );
		}
		this.propertyKey = key;
	}
	
	@Override
	public String[] getPropertyKeys(){
		return new String[]{ "transition" };
	}
	
	@Override
	public CssProperty<?> getProperty( String key ){
		if( "transition".equals( key )){
			return transition;
		}
		return null;
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
