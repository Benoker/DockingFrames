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

import java.util.Map;

import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.PerspectivePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;

/**
 * This {@link PerspectiveStation} describes the layout of a {@link ToolbarContainerDockStation}.
 * @author Benjamin Sigg
 */
public class ToolbarContainerDockPerspective extends ListDockableStationPerspective{
	/**
	 * Creates a new, empty station.
	 */
	public ToolbarContainerDockPerspective(){
		setDockables( new PerspectivePlaceholderList<PerspectiveDockable>() );
	}
	
	/**
	 * Creates a new station.
	 * @param layout the layout of the station
	 * @param children the children of the station
	 */
	public ToolbarContainerDockPerspective( ToolbarContainerDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
		read( layout, children );
	}
	
	/**
	 * Updates the layout of this station.
	 * @param layout the new layout
	 * @param children the new children
	 */
	public void read(  ToolbarContainerDockStationLayout layout, final Map<Integer, PerspectiveDockable> children ){
		PlaceholderMap map = layout.getPlaceholders();
		if( !map.getFormat().equals( new Path( "dock.ToolbarContainerStation" ) ) ) {
			throw new IllegalArgumentException( "unknown format: " + map.getFormat() );
		}
		if( map.getVersion() != 0 ) {
			throw new IllegalArgumentException( "unknown version: " + map.getVersion() );
		}
		
		PlaceholderMap list = map.getMap( map.newKey( "content" ), "list" );
		
		PerspectivePlaceholderList<PerspectiveDockable> dockables = new PerspectivePlaceholderList<PerspectiveDockable>();
		dockables.read( list, new PlaceholderListItemAdapter<PerspectiveDockable, PerspectiveDockable>(){
			public PerspectiveDockable convert( ConvertedPlaceholderListItem item ){
				if( children == null ){
					return null;
				}
				int id = item.getInt( "id" );
				PerspectiveDockable dockable = children.get( id );
				dockable.setParent( ToolbarContainerDockPerspective.this );
				return dockable;
			}
		} );
		setDockables( dockables );
	}
	
	
	@Override
	public String getFactoryID(){
		return ToolbarContainerDockStationFactory.ID;
	}
	
	@Override
	protected DockableProperty getDockableProperty( int index, Path placeholder, PerspectiveDockable child, PerspectiveDockable target ){
		return new ToolbarContainerProperty( index, placeholder );
	}
}
