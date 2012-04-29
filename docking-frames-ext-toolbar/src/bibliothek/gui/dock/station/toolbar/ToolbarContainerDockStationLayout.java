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

import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.station.support.PlaceholderMap;

/**
 * Describes the layout of a {@link ToolbarContainerDockStation}
 * 
 * @author Benjamin Sigg
 * @author Herve Guillaume
 */
public class ToolbarContainerDockStationLayout{
	/** the encoded layout of the {@link ToolbarContainerDockStation} */
	private final PlaceholderMap placeholders;

	/**
	 * Creates a new layout object
	 * 
	 * @param map
	 *            the encoded layout, not <code>null</code>
	 */
	public ToolbarContainerDockStationLayout( PlaceholderMap map ){
		placeholders = map;
	}

	/**
	 * Gets the encoded layout of the {@link ToolbarContainerDockStation}.
	 * 
	 * @return the encoded layout, not <code>null</code>
	 */
	public PlaceholderMap getPlaceholders(){
		return placeholders;
	}
}
