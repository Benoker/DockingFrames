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

/**
 * Used by a {@link PlaceholderList} to read and write to a {@link PlaceholderMap}.<br>
 * Implementations can assume that the list will both <code>convert</code> methods call with 
 * items in the order of which the items are found in the list (meaning ordered by their location). 
 * @author Benjamin Sigg
 *
 * @param <D> the kind of item representing a {@link Dockable}
 * @param <P> the kind of item this converter supports
 */
public interface PlaceholderListItemConverter<D, P extends PlaceholderListItem<D>> {
	/**
	 * Given some non-<code>null</code> dockable, this method converts the element into
	 * some data that can be stored persistently.
	 * @param index the index of <code>dockable</code> in the {@link PlaceholderList#dockables() dockables-list}
	 * @param dockable the element to store
	 * @return the converted item, can be <code>null</code> to indicate that this element
	 * should not be stored
	 */
	public ConvertedPlaceholderListItem convert( int index, P dockable );
	
	/**
	 * Given some data that was written by some {@link PlaceholderListItemConverter}, this
	 * method returns the dockable that belongs to that data. It is the clients responsibility
	 * to ensure that the same {@link PlaceholderListItemConverter} is used for reading
	 * and writing.<br>
	 * The {@link ConvertedPlaceholderListItem#getPlaceholder() placeholder property} of <code>item</code>
	 * will always be <code>null</code>, the {@link ConvertedPlaceholderListItem#getPlaceholderMap() map property} may
	 * not be the same as was associated when calling {@link #convert(int,PlaceholderListItem)}.
	 * @param item the item to read
	 * @return the corresponding element, can be <code>null</code>
	 */
	public P convert( ConvertedPlaceholderListItem item );
	
	/**
	 * Called as soon as the result of {@link #convert(ConvertedPlaceholderListItem)} has been added to the list.
	 * @param dockable the added element, not <code>null</code>
	 */
	public void added( P dockable );
}
