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
package bibliothek.util.filter;

import java.util.HashSet;
import java.util.Set;

import bibliothek.util.Filter;

/**
 * This filter keeps a set of items, an item is included if it is contained in that set.
 * @author Benjamin Sigg
 * @param <T> the kind of item this filter handles
 */
public class PresetFilter<T> implements Filter<T>{
	/** the items to include */
	private Set<T> items = new HashSet<T>();
	
	/**
	 * Creates a new filter
	 * @param items all the items to include
	 */
	public PresetFilter( T... items ){
		for( T item : items ){
			this.items.add( item );
		}
	}
	
	public boolean includes( T item ){
		return items.contains( item );
	}
}
