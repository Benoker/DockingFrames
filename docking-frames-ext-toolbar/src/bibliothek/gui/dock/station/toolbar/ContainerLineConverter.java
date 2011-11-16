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
package bibliothek.gui.dock.station.toolbar;

import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ContainerLineStation;
import bibliothek.gui.dock.station.support.PlaceholderMap;

/**
 * A helper interface used by the {@link ContainerLineStation} to read and write
 * {@link PlaceholderMap}s.
 * @author Benjamin Sigg
 * @Herve Guillaume
 */
public interface ContainerLineConverter {
	/**
	 * Called by {@link ContainerLineStation#getPlaceholders()}
	 * @param station the calling station
	 * @return the placeholders
	 */
	public PlaceholderMap getPlaceholders( ContainerLineStation station );
	
	/**
	 * Called by {@link ContainerLineStation#getPlaceholders(Map)}
	 * @param station  the calling station
	 * @param children identifiers for the children of the station
	 * @return the placeholders
	 */
	public PlaceholderMap getPlaceholders( ContainerLineStation station, Map<Dockable, Integer> children );
	
	/**
	 * Called by {@link ContainerLineStation#setPlaceholders(PlaceholderMap)}
	 * @param station the calling station
	 * @param map the placeholders to read
	 */
	public void setPlaceholders( ContainerLineStation station, PlaceholderMap map );
	
	/**
	 * Called by {@link ContainerLineStation#setPlaceholders(PlaceholderMap, Map)}
	 * @param station the calling station
	 * @param callback offers methods to call private methods of <code>station</code>
	 * @param children the new children of <code>station</code>
	 * @param map the placeholders to read
	 */
	public void setPlaceholders( ContainerLineStation station, ContainerLineConverterCallback callback, PlaceholderMap map, Map<Integer, Dockable> children );
}
