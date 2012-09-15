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

import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.extension.css.CssType;

/**
 * A callback given to an {@link AnimatedCssProperty}.
 * @author Benjamin Sigg
 * @param <T> the type of value the property works with
 */
public interface AnimatedCssPropertyCallback<T> {
	/**
	 * Gets the scheme in whose realm this property works.
	 * @return the scheme, not <code>null</code>
	 */
	public CssScheme getScheme();
	
	/**
	 * To be called if the value of the property changed.
	 * @param value the new value
	 */
	public void set( T value );
	
	/**
	 * Can be used by the property to query other properties. Access to animated properties is
	 * only granted if{@link #addProperty(String, AnimatedCssProperty)} was called.
	 * @param type the expected type of the result
	 * @param key the key of the other property
	 * @return the value of the property <code>key</code>
	 */
	public <S> S get( CssType<S> type, String key );
	
	/**
	 * To be called if the property <code>key</code> needs to be animated as well. Only if this
	 * method is called {@link #get(String, CssType)} will return animated values. Circular
	 * dependencies will result in unspecified behavior.
	 * @param type the type of the property 
	 * @param key the name of a property that is animated as well
	 */
	public void addProperty( CssType<?> type, String key );
	
	/**
	 * To be called if the property <code>key</code> needs no longer to be animated.
	 * @param key the name of the property that is no longer animted
	 */
	public void removeProperty( String key );
	
	/**
	 * Triggers a call to {@link AnimatedCssProperty#step()} in the near future.
	 */
	public void step();
	
	/**
	 * Triggers a call to {@link AnimatedCssProperty#step()} within the next <code>delay</code>
	 * milliseconds. 
	 * @param delay the expected delay until <code>step</code> is called
	 */
	public void step( int delay );
}
