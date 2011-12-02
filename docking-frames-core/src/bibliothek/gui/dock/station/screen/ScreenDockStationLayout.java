/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.gui.dock.station.screen;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.support.PlaceholderMap;

/**
 * A layout that holds the contents of a {@link ScreenDockStation}.
 * @author Benjamin Sigg
 */
public class ScreenDockStationLayout {
	/** all the items of this layout */
	private PlaceholderMap placeholders;
	
	/**
	 * Default constructor not setting the {@link #placeholders} of
	 * this layout. Kept for backwards compatibility, should not be used
	 * by clients.
	 */
	protected ScreenDockStationLayout(){
		// nothing
	}
	
	/**
	 * Creates a new layout.
	 * @param placeholders all the items of this layout
	 */
	public ScreenDockStationLayout( PlaceholderMap placeholders ){
		this.placeholders = placeholders;
	}
	
    /**
     * Gets all the items of this layout, including the encoded {@link Dockable}s.
     * @return the placeholders
     */
	public PlaceholderMap getPlaceholders() {
		return placeholders;
	}
}
