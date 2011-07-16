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
package bibliothek.gui.dock.util;

import bibliothek.util.Path;

/**
 * An algorithm that can create missing entries for an {@link UIProperties}s.
 * @author Benjamin Sigg
 * @param <V> The kind of values this map contains
 * @param <U> The kind of observers used to read values from this map
 * @param <B> The kind of bridges used to transfer values <code>V</code> to observers <code>U</code>
 */
public interface UIScheme<V, U extends UIValue<V>, B extends UIBridge<V, U>> {
	/**
	 * Informs this scheme that it is now used by <code>properties</code>.
	 * @param properties the owner of this scheme
	 */
	public void install( UIProperties<V, U, B> properties );
	
	/**
	 * Informs this scheme that it is no longer used by <code>properties</code>.
	 * @param properties an old client of this scheme
	 */
	public void uninstall( UIProperties<V, U, B> properties );
	
	/**
	 * Creates the resource with name <code>name</code>. This method may be called often
	 * and should complete quickly.
	 * @param name the name of some resource
	 * @param properties the map that will use the resource
	 * @return the resource or <code>null</code> if this scheme does not know <code>name</code>
	 */
	public V getResource( String name, UIProperties<V, U, B> properties );
	
	/**
	 * Creates the {@link UIBridge} that handles resources of type <code>name</code>.
	 * @param name the name of the bridge
	 * @param properties the map that will use the resource
	 * @return the new bridge or <code>null</code> if this scheme does not know <code>name</code>
	 */
	public B getBridge( Path name, UIProperties<V, U, B> properties );
	
	/**
	 * Adds a listener to this scheme.
	 * @param listener the new listener, not <code>null</code>
	 */
	public void addListener( UISchemeListener<V,U,B> listener );
	
	/**
	 * Removes a listener from this scheme.
	 * @param listener the listener to remove
	 */
	public void removeListener( UISchemeListener<V,U,B> listener );
}
