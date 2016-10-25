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
package bibliothek.gui.dock.extension.css;


/**
 * A {@link CssProperty} is one property of a {@link CssItem}. A {@link CssProperty} itself
 * can have sub-properties, they are only active as long as this {@link CssProperty} is
 * attached to a {@link CssItem}.<br>
 * In nested properties, if the parent property has the key "x" and the child property has the
 * key "y", then inside the css file a property called "x-y" is searched.
 * 
 * @author Benjamin Sigg
 *
 * @param <T> the type of this property
 */
public interface CssProperty<T> extends CssPropertyContainer {
	/**
	 * Sets the value of this property.
	 * @param value the new value, can be <code>null</code>
	 */
	public void set( T value );
	
	/**
	 * Gest the type of this property.
	 * @param scheme the scheme in whose realm this property will be used
	 * @return the type, can be used to convert a {@link String} to
	 * a <code>T</code>
	 */
	public CssType<T> getType( CssScheme scheme );
	
	/**
	 * Called by <code>scheme</code> once it starts or stops monitoring this property.
	 * @param scheme the scheme which is responsible for setting the value of this property, or <code>null</code>
	 * @param key the key with which <code>scheme</code> will search for the value of this property, or <code>null</code>
	 * @throws IllegalStateException if this method is called twice in a row with non-<code>null</code> arguments
	 */
	public void setScheme( CssScheme scheme, CssPropertyKey key );
}
