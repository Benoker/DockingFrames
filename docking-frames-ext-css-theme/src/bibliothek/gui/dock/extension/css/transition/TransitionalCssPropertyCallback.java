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

import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssScheme;

/**
 * A callback given to an {@link TransitionalCssProperty}.
 * @author Benjamin Sigg
 * @param <T> the type of value the property works with
 */
public interface TransitionalCssPropertyCallback<T> {
	/**
	 * Gets the scheme in whose realm this property works.
	 * @return the scheme, not <code>null</code>
	 */
	public CssScheme getScheme();
	
	/**
	 * To be called if the value of the property changed.<br>
	 * Please read about the behavior of animated properties with sub-properties in {@link TransitionalCssProperty}.
	 * @param value the new value
	 */
	public void set( T value );
	
	/**
	 * Triggers a call to {@link TransitionalCssProperty#step()} in the near future.
	 */
	public void step();
	
	/**
	 * Triggers a call to {@link TransitionalCssProperty#step()} within the next <code>delay</code>
	 * milliseconds. 
	 * @param delay the expected delay until <code>step</code> is called
	 */
	public void step( int delay );
	
	/**
	 * Informs the callback that the source property depends on another property with name <code>key</code>. This
	 * method will ensure that {@link CssProperty#set(Object) the value} of <code>property</code> is set
	 * and updated.<br>
	 * Please note that no two transitions may depend on the same properties.
	 * @param key the key of the sub-property, relative to the parent property (the name of the parent is not included
	 * in the key)
	 * @param property the sub-property itself, it's value will be update automatically
	 */
	public void addSourceDependency( String key, CssProperty<?> property );
	
	/**
	 * Informs this callback that the source property does no longer depend on the property <code>key</code>.
	 * @param key the key of the sub-property, relative to the parent property (the name of the parent is not included
	 * in the key)
	 */
	public void removeSourceDependency( String key );
	
	/**
	 * Informs the callback that the target property depends on another property with name <code>key</code>. This
	 * method will ensure that {@link CssProperty#set(Object) the value} of <code>property</code> is set
	 * and updated.<br>
	 * Please note that no two transitions may depend on the same properties.
	 * @param key the key of the sub-property, relative to the parent property (the name of the parent is not included
	 * in the key)
	 * @param property the sub-property itself, it's value will be update automatically
	 */
	public void addTargetDependency( String key, CssProperty<?> property );
	
	/**
	 * Informs this callback that the target property does no longer depend on the property <code>key</code>.
	 * @param key the key of the sub-property, relative to the parent property (the name of the parent is not included
	 * in the key)
	 */
	public void removeTargetDependency( String key );
}
