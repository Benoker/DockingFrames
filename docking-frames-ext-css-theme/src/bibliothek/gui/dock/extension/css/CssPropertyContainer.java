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
 * A map of {@link CssProperty}s, can modify itself and needs to be monitored
 * with some {@link CssPropertyContainerListener}s.
 * @author Benjamin Sigg
 */
public interface CssPropertyContainer {

	/**
	 * Gets a list of all the keys that can be used for {@link #getProperty(String)}.
	 * @return the list of all currently available values
	 */
	public String[] getPropertyKeys();
	
	/**
	 * Gets a changeable property of this container. Each property has a type, only properties
	 * that have any meaning to this container can be set, others will be ignored (the result
	 * of this method is <code>null</code> for them).
	 * @param key the key of the property
	 * @return the property or <code>null</code> if not existing
	 */
	public CssProperty<?> getProperty( String key );
	
	/**
	 * Adds a listener to this property, will be informed if a sub-property is
	 * added or removed.
	 * @param listener the observer to add, not <code>null</code>
	 */
	public void addPropertyContainerListener( CssPropertyContainerListener listener );
	
	/**
	 * Removes a listener from this property.
	 * @param listener the listener to remove
	 */
	public void removePropertyContainerListener( CssPropertyContainerListener listener );

}
