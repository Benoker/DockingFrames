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
package bibliothek.util;

/**
 * A filter is used to split up a set of items to a set of included and a set of excluded items.
 * @author Benjamin Sigg
 * @param <T> the kind of items this filter handles
 */
public interface Filter<T> {
	/**
	 * Tells whether the item <code>item</code> should be included or not.
	 * @param item the item to include, depending on the user of this filter the item may or may not be <code>null</code>
	 * @return <code>true</code> if <code>item</code> is included, <code>false</code> if <code>item</code> is excluded
	 */
	public boolean includes( T item );
}
