/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.themes;

import bibliothek.gui.DockStation;
import bibliothek.util.ClientOnly;

/**
 * A default implementation of {@link StationThemeItem} where clients can directly
 * set the value.
 * @author Benjamin Sigg
 * @param <T> the kind of value this item represents
 */
@ClientOnly
public class DefaultStationThemeItem<T> implements StationThemeItem<T> {
	private T value;

	/**
	 * Creates a new item
	 */
	public DefaultStationThemeItem(){
		// nothing
	}
	
	/**
	 * Creates a new item
	 * @param value the value of this item, can be <code>null</code>
	 */
	public DefaultStationThemeItem( T value ){
		this.value = value;
	}
	
	/**
	 * Sets the value of this item.
	 * @param value the new value, whether <code>null</code> is allowed or not
	 * depends on the usage of this item
	 */
	public void setValue( T value ){
		this.value = value;
	}
	
	public T get( DockStation station ){
		return value;
	}
}
