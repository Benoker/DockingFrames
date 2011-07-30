/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.common;

import java.awt.Component;

/**
 * A {@link CStationContainer} is a set of root {@link CStation}s that are somehow
 * combined and ordered on some kind of {@link Component}. A container may or may not be mutable.
 * @author Benjamin Sigg
 */
public interface CStationContainer {
	/**
	 * Adds the observer <code>listener</code> to this container. The listener is to be informed
	 * whenever a {@link CStation} is added or removed from this container.
	 * @param listener the new listener, not <code>null</code>
	 */
	public void addStationContainerListener( CStationContainerListener listener );
	
	/**
	 * Removes the observer <code>listener</code> from this container.
	 * @param listener the listener to remove
	 */
	public void removeStationContainerListener( CStationContainerListener listener );
	
	/**
	 * Gets a unique identifier that is used by only this {@link CStationContainer}.
	 * @return the unique identifier, not <code>null</code>
	 */
	public String getUniqueId();
	
	/**
	 * Gets a {@link Component} whose children are all the {@link CStation}s of this
	 * {@link CStationContainer}.
	 * @return the parent of all {@link CStation}s, not <code>null</code>
	 */
	public Component getComponent();
	
	/**
	 * Gets the number of {@link CStation}s that are currently in this container.
	 * @return the number of stations, at least 0
	 */
	public int getStationCount();
	
	/**
	 * Gets the index'th child of this container.
	 * @param index the index of the child, between 0 and {@link #getStationCount()}
	 * @return the child, not <code>null</code>
	 */
	public CStation<?> getStation( int index );
	
	/**
	 * Gets the preferred default {@link CStation} of this container. Children with no location
	 * are usually made visible on such a default station.
	 * @return the default station or <code>null</code>
	 */
	public CStation<?> getDefaultStation();
}
