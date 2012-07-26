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

import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;

/**
 * A layout object describing the contents of a {@link ToolbarGroupDockStation}.
 * 
 * @author Benjamin Sigg
 */
public class ToolbarGroupDockStationLayout {
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
	public ToolbarGroupDockStationLayout( PlaceholderMap map, ExpandedState state ){
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
	
	/**
	 * Stores <code>orientation</code> in <code>map</code>.
	 * @param map the map to modify
	 * @param orientation the orientation to save
	 * @see #readOrientation(PlaceholderMap)
	 */
	public static void writeOrientation( PlaceholderMap map, Orientation orientation ){
		PlaceholderMap.Key key = map.newKey( "group" );
		switch( orientation ){
			case HORIZONTAL:
				map.putString( key, "orientation", "horizontal" );
				break;
			case VERTICAL:
				map.putString( key, "orientation", "vertical" );
				break;
		}
	}

	/**
	 * Reads an {@link Orientation} from <code>map</code>, this method assumes that the
	 * orientation was written with {@link #writeOrientation(PlaceholderMap, Orientation)}.
	 * @param map the map to read from
	 * @return the orientation or <code>null</code> if not found
	 * @see #writeOrientation(PlaceholderMap, Orientation)
	 */
	public static Orientation readOrientation( PlaceholderMap map ){
		PlaceholderMap.Key key = map.newKey( "group" );
		String orientation = map.getString( key, "orientation" );
		if( "horizontal".equals( orientation ) ) {
			return Orientation.HORIZONTAL;
		}
		else if( "vertical".equals( orientation ) ) {
			return Orientation.VERTICAL;
		}
		else{
			return null;
		}
	}
}
