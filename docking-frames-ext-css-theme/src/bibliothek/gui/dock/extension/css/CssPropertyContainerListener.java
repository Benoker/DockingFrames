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
 * This listener is added to a {@link CssPropertyContainer} and is informed if
 * sub-properties are added or removed.
 * @author Benjamin Sigg
 */
public interface CssPropertyContainerListener {
	/**
	 * Called if a property has been added to <code>source</code>.
	 * @param source the source of the event
	 * @param key the key of the new property
	 * @param property the property that has been added
	 */
	public void propertyAdded( CssPropertyContainer source, String key, CssProperty<?> property );
	
	/**
	 * Called if a property has been removed from <code>source</code>.
	 * @param source the source of the event
	 * @param key the key of the removed property
	 * @param property the property that has been removed
	 */
	public void propertyRemoved( CssPropertyContainer source, String key, CssProperty<?> property );
}
