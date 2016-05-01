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
package bibliothek.gui.dock.station.support;

import bibliothek.gui.Dockable;
import bibliothek.util.Path;

/**
 * Data about a {@link Dockable} that was stored in a {@link PlaceholderList}.
 * @author Benjamin Sigg
 */
public class ConvertedPlaceholderListItem extends PlaceholderMetaMap{
	/** a placeholder to use for this item */
	private Path placeholder;
	/** additional information about this item */
	private PlaceholderMap map;
	
	
	/**
	 * Associates a placeholder with this item. A {@link PlaceholderList} will insert
	 * this placeholder into its set of placeholders.
	 * @param placeholder the placeholder, can be <code>null</code>
	 */
	public void setPlaceholder( Path placeholder ){
		this.placeholder = placeholder;
	}
	
	/**
	 * Gets the placeholder that is associated with this item.
	 * @return the placeholder, can be <code>null</code>
	 * @see #setPlaceholder(Path)
	 */
	public Path getPlaceholder(){
		return placeholder;
	}
	
	/**
	 * Associates a map of data with this item. Notice that keys for this
	 * map must be valid placeholders. Clients may need to use the empty key
	 * (calling {@link PlaceholderMap#newKey(Path...)} with no arguments).<br>
	 * The map will be ignored if there is already a map associated with this item
	 * by the list itself.
	 * @param map the data, can be <code>null</code>
	 */
	public void setPlaceholderMap( PlaceholderMap map ){
		this.map = map;
	}
	
	/**
	 * Gets additional information about this item. This is the map that was actually written,
	 * not necessarily the map that was set by {@link #setPlaceholderMap(PlaceholderMap)}.
	 * @return additional information, can be <code>null</code>
	 * @see #setPlaceholderMap(PlaceholderMap)
	 */
	public PlaceholderMap getPlaceholderMap(){
		return map;
	}
}
