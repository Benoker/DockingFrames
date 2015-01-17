/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2015 Benjamin Sigg
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
package bibliothek.gui.dock.station.support;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.PlaceholderMapping;
import bibliothek.util.Path;

/**
 * A generic {@link PlaceholderMapping} that operates by accessing a {@link PlaceholderList}.
 * @author Benjamin Sigg
 */
public abstract class PlaceholderListMapping implements PlaceholderMapping{
	/** the owner and creator of this mapping */
	private DockStation station;

	/** a generic list of placeholders, that represent the layout of {@link #station} */
	private DockablePlaceholderList<?> placeholders;
	
	/**
	 * Creates a new {@link PlaceholderListMapping}. This constructor should not be called by clients
	 * directly, instead they should call {@link DockStation#getPlaceholderMapping()}
	 * @param station the station that created and owns this mapping
	 * @param placeholders the placeholders that are used by {@link #station}
	 */
	public PlaceholderListMapping( DockStation station, DockablePlaceholderList<?> placeholders ){
		this.station = station;
		this.placeholders = placeholders;
	}
	
	public DockStation getStation() {
		return station;
	}

	public void addPlaceholder( Dockable dockable, Path placeholder ) {
		placeholders.addPlaceholder( dockable, placeholder );
	}

	public void removePlaceholder( Path placeholder ) {
		placeholders.removeAll( placeholder );
	}
	
	public boolean hasPlaceholder( Path placeholder ) {
		return placeholders.hasPlaceholder( placeholder );
	}
	
	public Dockable getDockableAt( Path placeholder ) {
		PlaceholderListItem<Dockable> item = placeholders.getDockableAt( placeholder );
		if( item == null ){
			return null;
		}
		else{
			return item.asDockable();
		}
	}
}
