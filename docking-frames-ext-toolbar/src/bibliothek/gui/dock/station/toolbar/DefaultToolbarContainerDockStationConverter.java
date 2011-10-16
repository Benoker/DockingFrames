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

import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.layout.DockLayout;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;

/**
 * Default implementation of {@link ToolbarContainerDockStationConverter}. This converter supports all features
 * necessary to read and write {@link PlaceholderMap}s.
 * @author Benjamin Sigg
 */
public class DefaultToolbarContainerDockStationConverter implements ToolbarContainerDockStationConverter {
	@Override
	public PlaceholderMap getPlaceholders( ToolbarContainerDockStation station ){
		PlaceholderMap result = new PlaceholderMap( new Path( "dock.ToolbarContainerStation" ), 0 );
		result.put( result.newKey( "north" ), "list", station.getDockables( Position.NORTH ).toMap() );
		result.put( result.newKey( "south" ), "list", station.getDockables( Position.SOUTH ).toMap() );
		result.put( result.newKey( "east" ), "list", station.getDockables( Position.EAST ).toMap() );
		result.put( result.newKey( "west" ), "list", station.getDockables( Position.WEST ).toMap() );
		result.put( result.newKey( "center" ), "list", station.getDockables( Position.CENTER ).toMap() );
		return result;
	}

	@Override
	public PlaceholderMap getPlaceholders( ToolbarContainerDockStation station, Map<Dockable, Integer> children ){
		PlaceholderMap result = new PlaceholderMap( new Path( "dock.ToolbarContainerStation" ), 0 );
		result.put( result.newKey( "north" ), "list", convert( station.getDockables( Position.NORTH ), children ) );
		result.put( result.newKey( "south" ), "list", convert( station.getDockables( Position.SOUTH ), children ) );
		result.put( result.newKey( "east" ), "list", convert( station.getDockables( Position.EAST ), children ) );
		result.put( result.newKey( "west" ), "list", convert( station.getDockables( Position.WEST ), children ) );
		result.put( result.newKey( "center" ), "list", convert( station.getDockables( Position.CENTER ), children ) );
		return result;
	}
	
	private PlaceholderMap convert( DockablePlaceholderList<Dockable> list, final Map<Dockable, Integer> children ){
		list.toMap( new PlaceholderListItemAdapter<Dockable, Dockable>(){
			@Override
			public ConvertedPlaceholderListItem convert( int index, Dockable dockable ){
				
				
				return super.convert( index, dockable );
			}
		});
	}

	@Override
	public void setPlaceholders( ToolbarContainerDockStation station, PlaceholderMap map ){
		if( !map.getFormat().equals( new Path( "dock.ToolbarContainerStation" ) ) ) {
			throw new IllegalArgumentException( "unknown format: " + map.getFormat() );
		}
		if( map.getVersion() != 0 ) {
			throw new IllegalArgumentException( "unknown version: " + map.getVersion() );
		}
		
		station.setPlaceholders( Position.NORTH, map.getMap( map.newKey( "north" ), "list" ) );
		station.setPlaceholders( Position.SOUTH, map.getMap( map.newKey( "south" ), "list" ) );
		station.setPlaceholders( Position.EAST, map.getMap( map.newKey( "east" ), "list" ) );
		station.setPlaceholders( Position.WEST, map.getMap( map.newKey( "west" ), "list" ) );
		station.setPlaceholders( Position.CENTER, map.getMap( map.newKey( "center" ), "list" ) );
	}

	@Override
	public void setPlaceholders( ToolbarContainerDockStation station, PlaceholderMap map, Map<Integer, Dockable> children ){
		// TODO Auto-generated method stub

	}
}
