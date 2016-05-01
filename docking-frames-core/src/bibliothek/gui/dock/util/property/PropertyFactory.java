/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.util.property;

import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyKey;

/**
 * A strategy to create or store the default value of a {@link PropertyKey}.
 * @author Benjamin Sigg
 *
 * @param <A> the kind of value this strategy handles
 */
public interface PropertyFactory<A> {
	/**
	 * Gets the default value for <code>key</code>. This method is called
	 * only once for the combination of <code>key</code> and <code>properties</code>,
	 * it can either always return the same object or create a new one every 
	 * time. The results gets stored as default value in <code>properties</code>.
	 * @param key the key for which to get the default
	 * @param properties the map in which the default will be stored
	 * @return the new default value, <code>null</code> is valid
	 */
	public A getDefault( PropertyKey<A> key, DockProperties properties );
	
	/**
	 * Asks for the default value that should be used if no {@link DockProperties}
	 * are available.
	 * @param key the key of the property
	 * @return the default value, can be <code>null</code>
	 */
	public A getDefault( PropertyKey<A> key );
}
