/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.station.toolbar;

import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.station.support.PlaceholderMap;

/**
 * A helper interface used by the {@link ToolbarContainerDockStation} to read
 * and write {@link PlaceholderMap}s.
 * 
 * @author Benjamin Sigg
 * @author Herve Guillaume
 */
public interface ToolbarContainerConverter {
	/**
	 * Called by {@link ToolbarContainerDockStation#getPlaceholders()}
	 * 
	 * @param station
	 *            the calling station
	 * @return the placeholders
	 */
	public PlaceholderMap getPlaceholders( ToolbarContainerDockStation station );

	/**
	 * Called by {@link ToolbarContainerDockStation#getPlaceholders(Map)}
	 * @param station the calling station
	 * @param children identifiers for the children of the station
	 * @return the placeholders
	 */
	public PlaceholderMap getPlaceholders( ToolbarContainerDockStation station, Map<Dockable, Integer> children );

	/**
	 * Called by {@link ToolbarDockStationFactory#getPerspectiveLayout(bibliothek.gui.dock.perspective.PerspectiveElement, Map)}
	 * @param station the calling station
	 * @param children identifiers for the children of the station
	 * @return the placeholders
	 */
	public PlaceholderMap getPlaceholders( ToolbarContainerDockPerspective station, Map<PerspectiveDockable, Integer> children );
	
	/**
	 * Called by
	 * {@link ToolbarContainerDockStation#setPlaceholders(PlaceholderMap)}
	 * 
	 * @param station
	 *            the calling station
	 * @param map
	 *            the placeholders to read
	 */
	public void setPlaceholders( ToolbarContainerDockStation station, PlaceholderMap map );

	/**
	 * Called by
	 * {@link ToolbarContainerDockStation#setPlaceholders(PlaceholderMap, Map)}
	 * 
	 * @param station
	 *            the calling station
	 * @param callback
	 *            offers methods to call private methods of <code>station</code>
	 * @param children
	 *            the new children of <code>station</code>
	 * @param map
	 *            the placeholders to read
	 */
	public void setPlaceholders( ToolbarContainerDockStation station, ToolbarContainerConverterCallback callback, PlaceholderMap map, Map<Integer, Dockable> children );
}
