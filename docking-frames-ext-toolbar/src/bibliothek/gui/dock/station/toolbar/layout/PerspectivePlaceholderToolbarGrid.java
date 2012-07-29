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

import java.util.HashSet;
import java.util.Set;

import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.PerspectivePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.util.Path;

/**
 * An implementation of {@link PlaceholderToolbarGrid} that uses {@link PerspectiveDockable}s
 * and {@link PerspectiveStation}s, thus is ideal to be used in the perspective API.
 * @author Benjamin Sigg
 */
public class PerspectivePlaceholderToolbarGrid extends ModeledPlaceholderToolbarGrid<PerspectiveDockable, PerspectiveStation, PerspectiveDockable>{
	/**
	 * Creates a new, empty grid.
	 */
	public PerspectivePlaceholderToolbarGrid(){
		init();
	}
	
	@Override
	protected PlaceholderList<PerspectiveDockable, PerspectiveStation, PerspectiveDockable> createColumn(){
		return new PerspectivePlaceholderList<PerspectiveDockable>();
	}

	@Override
	protected GridPlaceholderList<PerspectiveDockable, PerspectiveStation, PerspectiveDockable> createGrid(){
		return new PerspectiveGridPlaceholderList();
	}

	@Override
	protected Set<Path> getPlaceholders( PerspectiveDockable dockable ){
		Set<Path> result = new HashSet<Path>();
		fill( result, dockable );
		return result;
	}
	
	private void fill( Set<Path> placeholders, PerspectiveDockable dockable ){
		Path placeholder = dockable.getPlaceholder();
		if( placeholder != null ){
			placeholders.add( placeholder );
		}
		PerspectiveStation station = dockable.asStation();
		if( station != null ){
			for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
				fill( placeholders, station.getDockable( i ));
			}
		}
	}
	
	/**
	 * Replaces <code>oldDockable</code> with <code>newDockable</code>.
	 * @param oldDockable a child of this grid
	 * @param newDockable the replacement of <code>oldDockable</code>
	 */
	public void replace( PerspectiveDockable oldDockable, PerspectiveDockable newDockable ){
		int column = getColumn( oldDockable );
		if( column == -1 ){
			throw new IllegalArgumentException( "oldDockable is not known to this grid" );
		}
		PlaceholderList<PerspectiveDockable, PerspectiveStation, PerspectiveDockable> list = getColumn( column );
		int index = list.dockables().indexOf( oldDockable );
		list.remove( index );
		list.dockables().add( index, newDockable );
	}

	@Override
	protected void fill( PerspectiveDockable dockable, ConvertedPlaceholderListItem item ){
		Path placeholder = dockable.getPlaceholder();
		if( placeholder != null ) {
			item.putString( "placeholder", placeholder.toString() );
			item.setPlaceholder( placeholder );
		}
		
		PerspectiveStation station = dockable.asStation();
		if( station != null ){
			item.setPlaceholderMap( station.getPlaceholders() );
		}
	}
	
	
}
