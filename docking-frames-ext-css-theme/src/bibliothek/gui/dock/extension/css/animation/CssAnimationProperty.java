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

import bibliothek.gui.dock.extension.css.CssItem;
import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.extension.css.CssType;
import bibliothek.gui.dock.extension.css.property.AbstractContainerCssProperty;
import bibliothek.gui.dock.extension.css.property.AbstractCssPropertyContainer;
import bibliothek.util.filter.PresetFilter;

/**
 * This property adds an animation to some kind of value.
 * @author Benjamin Sigg
 * @param <T> the type of value this property enhances with an animation
 */
public abstract class CssAnimationProperty<T> extends AbstractCssPropertyContainer implements CssProperty<T>{
	private CssScheme scheme;
	private CssItem item;
	private String propertyKey;
	
	private AbstractContainerCssProperty<CssAnimation<T>> animation = new AbstractContainerCssProperty<CssAnimation<T>>(){
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public CssType<CssAnimation<T>> getType( CssScheme scheme ){
			Class<CssAnimation<T>> clazz = (Class<CssAnimation<T>>)(Class)CssAnimation.class;
			return scheme.getConverter( clazz );
		}
		
		@Override
		protected void propertyChanged( CssAnimation<T> value ){
			if( value != null ){
				value.setType( CssAnimationProperty.this.getType( scheme ) );
				value.setPropertyFilter( new PresetFilter<String>( propertyKey ) );
				scheme.animate( item, value );
			}
		}
	};
	
	/**
	 * Creates a new animation property
	 * @param scheme the scheme which managed the animations
	 * @param item the item that is animated
	 * @param propertyKey the name of the property that should be animated
	 */
	public CssAnimationProperty( CssScheme scheme, CssItem item, String propertyKey ){
		this.scheme = scheme;
		this.item = item;
		this.propertyKey = propertyKey;
	}
	
	@Override
	public String[] getPropertyKeys(){
		return new String[]{ "animation" };
	}
	
	@Override
	public CssProperty<?> getProperty( String key ){
		if( "animation".equals( key )){
			return animation;
		}
		return null;
	}
}
