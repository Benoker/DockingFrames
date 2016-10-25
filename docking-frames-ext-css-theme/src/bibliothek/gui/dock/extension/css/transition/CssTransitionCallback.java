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
import bibliothek.gui.dock.extension.css.CssPropertyKey;
import bibliothek.gui.dock.extension.css.CssRule;
import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.extension.css.CssType;

/**
 * Given to a {@link CssTransition}, allows the transition to access and modify properties.
 * @author Benjamin Sigg
 */
public interface CssTransitionCallback {
	/**
	 * Will call {@link CssTransition#step()} after a short delay.
	 * @see #step(int)
	 */
	public void step();
	
	/**
	 * Will call {@link CssTransition#step()} in about <code>delay</code> milliseconds. The call
	 * may happen a bit later because of other code that needs to executed first.
	 * @param delay the delay in milliseconds, at least <code>0</code>. To save CPU time the delay
	 * should be at least 20 milliseconds.
	 */
	public void step( int delay );
	
	/**
	 * Called by a {@link CssTransition} if it is no longer in use, immediately releases all resources
	 * that were ever used by the transition.
	 */
	public void destroyed();
	
	/**
	 * Gets the scheme in whose realm the transition is working.
	 * @return the scheme, not <code>null</code>
	 */
	public CssScheme getScheme();
	
	/**
	 * Gets the unique identifiers of all the properties that are using <code>type</code> to convert
	 * {@link String}s into their values.
	 * @param type the type of the properties
	 * @return the unique identifiers of all properties using <code>type</code>
	 * @see CssProperty#getType(CssScheme)
	 */
	public <T> CssPropertyKey[] getPropertiesOfType( CssType<T> type );
	
	/**
	 * Sets the value of some property. If using the wrong type an exception is thrown.
	 * @param type the type of the property
	 * @param key the unique identifier of the property
	 * @param value the new value of the property, may be <code>null</code> depending on the property
	 * @throws IllegalArgumentException if <code>type</code> does not match the type of the property, or if
	 * <code>key</code> does not point to a property at all
	 */
	public <T> void setProperty( CssType<T> type, CssPropertyKey key, T value );
	
	/**
	 * Gets the value of some property, the value from the last call to {@link #setProperty(CssType, String, Object)}
	 * or from the root {@link CssRule} is returned.
	 * @param type the expected type of the value
	 * @param key the name of the property
	 * @return the value of the property
	 */
	public <T> T getProperty( CssType<T> type, CssPropertyKey key );
	
	/**
	 * Adds <code>property</code> as dependency, the property will behave as if it would be long to the
	 * parent of the {@link CssTransition}. As source dependency the value of the property will be set using
	 * the old (source) {@link CssRule}.
	 * @param key the key of the property
	 * @param property the property to automatically set
	 */
	public void addSourceDependency( String key, CssProperty<?> property );
	
	/**
	 * Removes the source dependency <code>key</code>.
	 * @param key the name of the property to remove
	 */
	public void removeSourceDependency( String key );
	

	/**
	 * Adds <code>property</code> as dependency, the property will behave as if it would be long to the
	 * parent of the {@link CssTransition}. As target dependency the value of the property will be set using
	 * the new (target) {@link CssRule}.
	 * @param key the key of the property
	 * @param property the property to automatically set
	 */
	public void addTargetDependency( String key, CssProperty<?> property );
	
	/**
	 * Removes the target dependency <code>key</code>.
	 * @param key the name of the property to remove
	 */
	public void removeTargetDependency( String key );
}
