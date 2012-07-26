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

import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;

/**
 * Represents a list of lists of {@link PerspectiveDockable}s and placeholders.
 * @author Benjamin Sigg
 */
public class PerspectiveGridPlaceholderList extends GridPlaceholderList<PerspectiveDockable, PerspectiveStation, PerspectiveDockable>{
	@Override
	protected PerspectiveStation itemToStation( PerspectiveDockable dockable ){
		return dockable.asStation();
	}

	@Override
	protected PerspectiveDockable[] getItemChildren( PerspectiveStation station ){
		int count = station.getDockableCount();
		PerspectiveDockable[] result = new PerspectiveDockable[ count ];
		for( int i = 0; i < count; i++ ){
			result[i] = station.getDockable( i );
		}
		return result;
	}

	@Override
	protected Path getItemPlaceholder( PerspectiveDockable dockable ){
		return dockable.getPlaceholder();
	}

	@Override
	protected PlaceholderMap getItemPlaceholders( PerspectiveStation station ){
		return station.getPlaceholders();
	}

	@Override
	protected void setItemPlaceholders( PerspectiveStation station, PlaceholderMap map ){
		station.setPlaceholders( map );
	}
}
