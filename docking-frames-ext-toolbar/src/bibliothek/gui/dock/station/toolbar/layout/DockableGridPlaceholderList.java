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

package bibliothek.gui.dock.station.toolbar.layout;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.Path;

/**
 * Represents a list of lists of {@link Dockable}s and placeholders.<br>
 * Note: this class does not validate its content in any way, it is the clients
 * responsibility to keep the list clean. A good wrapper is
 * {@link PlaceholderToolbarGrid}, which adds several convenient methods and
 * ensures that the list of lists always is cleaned up correctly.
 * 
 * @author Benjamin Sigg
 * @param <P>
 *            the kind of object that represents a {@link Dockable}
 */
public class DockableGridPlaceholderList<P extends PlaceholderListItem<Dockable>> extends GridPlaceholderList<Dockable, DockStation, P> {
	@Override
	protected DockStation itemToStation( Dockable dockable ){
		return dockable.asDockStation();
	}

	@Override
	protected Dockable[] getItemChildren( DockStation station ){
		final Dockable[] result = new Dockable[station.getDockableCount()];
		for( int i = 0; i < result.length; i++ ) {
			result[i] = station.getDockable( i );
		}
		return result;
	}

	@Override
	protected Path getItemPlaceholder( Dockable dockable ){
		final PlaceholderStrategy strategy = getStrategy();
		if( strategy == null ) {
			return null;
		}
		return strategy.getPlaceholderFor( dockable );
	}

	@Override
	protected PlaceholderMap getItemPlaceholders( DockStation station ){
		return station.getPlaceholders();
	}

	@Override
	protected void setItemPlaceholders( DockStation station, PlaceholderMap map ){
		station.setPlaceholders( map );
	}
}
