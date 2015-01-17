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
package bibliothek.gui.dock.station.toolbar.group;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.PlaceholderMapping;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.toolbar.layout.DockablePlaceholderToolbarGrid;
import bibliothek.util.Path;

/**
 * A {@link PlaceholderMapping} that represents the placeholders of a {@link ToolbarGroupDockStation}.
 * @author Benjamin Sigg
 */
public class ToolbarGroupPlaceholderMapping implements PlaceholderMapping{
	/** the creator and owner of this mapping */
	private ToolbarGroupDockStation station;
	
	/** the internal representation of the layout of {@link #station} */
	private DockablePlaceholderToolbarGrid<?> grid;
	
	/**
	 * Creates a new mapping. This constructor should not be called by clients, instead they should call
	 * {@link ToolbarGroupDockStation#getplaceho}
	 * @param station
	 * @param grid
	 */
	public ToolbarGroupPlaceholderMapping( ToolbarGroupDockStation station, DockablePlaceholderToolbarGrid<?> grid ){
		this.station = station;
		this.grid = grid;
	}

	@Override
	public ToolbarGroupDockStation getStation() {
		return station;
	}
	
	@Override
	public void addPlaceholder( Dockable dockable, Path placeholder ) {
		if( placeholder == null ){
			throw new IllegalArgumentException( "placeholder must not be null" );
		}
		
		int column = grid.getColumn( dockable );
		if( column == -1 ){
			throw new IllegalArgumentException( "unable to find column of dockable" );
		}
		
		int line = grid.getLine( column, dockable );
		if( line == -1 ){
			throw new IllegalArgumentException( "unable to find line of dockable" );
		}
		
		grid.addPlaceholder( column, line, placeholder );
	}
	
	@Override
	public boolean hasPlaceholder( Path placeholder ) {
		return grid.hasPlaceholder( placeholder );
	}
	
	@Override
	public void removePlaceholder( Path placeholder ) {
		grid.removePlaceholder( placeholder );
	}
	
	@Override
	public Dockable getDockableAt( Path placeholder ) {
		PlaceholderListItem<Dockable> item = grid.get( placeholder );
		if( item == null ){
			return null;
		}
		else{
			return item.asDockable();
		}
	}
	
	@Override
	public DockableProperty getLocationAt( Path placeholder ) {
		int column = grid.getColumn( placeholder );
		if( column == -1 ){
			return null;
		}
		
		int line = grid.getLine( placeholder );
		if( line == -1 ){
			return null;
		}
		
		return new ToolbarGroupProperty( column, line, placeholder );
	}
}
