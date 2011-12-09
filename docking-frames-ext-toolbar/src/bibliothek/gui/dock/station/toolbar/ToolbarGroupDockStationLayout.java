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

import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;

/**
 * A layout object describing the contents of a {@link ToolbarGroupDockStation}.
 * 
 * @author Benjamin Sigg
 */
public class ToolbarGroupDockStationLayout{
	/** the encoded layout of a {@link ToolbarGroupDockStation} */
	private final PlaceholderMap placeholders;

	/** whether the children are big or small */
	private final ExpandedState state;

	/**
	 * Creates a new layout object.
	 * 
	 * @param map
	 *            the encoded layout of a {@link ToolbarGroupDockStation}, not
	 *            <code>null</code>
	 * @param state
	 *            whether the children are big or small
	 */
	public ToolbarGroupDockStationLayout( PlaceholderMap map,
			ExpandedState state ){
		placeholders = map;
		this.state = state;
	}

	/**
	 * Gets the encoded layout of a {@link ToolbarGroupDockStation}.
	 * 
	 * @return the encoded layout
	 */
	public PlaceholderMap getPlaceholders(){
		return placeholders;
	}

	/**
	 * Tells whether the children are big or small.
	 * 
	 * @return the size of the children
	 */
	public ExpandedState getState(){
		return state;
	}
}
