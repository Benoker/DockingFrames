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

import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.PerspectivePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.util.Path;

/**
 * A {@link PerspectiveStation} representing a {@link ToolbarDockStation}.
 * @author Benjamin Sigg
 */
public class ToolbarDockPerspective extends ListDockableStationPerspective{
	/** the preferred size of this toolbar */
	private ExpandedState state = ExpandedState.SHRUNK;
	
	/**
	 * Creates a new, empty station.
	 */
	public ToolbarDockPerspective(){
		setDockables( new PerspectivePlaceholderList<PerspectiveDockable>() );
	}
	
	/**
	 * Creates a new station.
	 * @param layout the layout of the station
	 * @param children the children of the station
	 */
	public ToolbarDockPerspective( ToolbarDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
		read( layout, children );
	}
	
	/**
	 * Updates the layout of this station.
	 * @param layout the new layout
	 * @param children the new children
	 */
	public void read(  ToolbarDockStationLayout layout, final Map<Integer, PerspectiveDockable> children ){
		PerspectivePlaceholderList<PerspectiveDockable> dockables = new PerspectivePlaceholderList<PerspectiveDockable>();
		setExpandedState( layout.getState() );
		dockables.read( layout.getPlaceholders(), new PlaceholderListItemAdapter<PerspectiveDockable, PerspectiveDockable>(){
			public PerspectiveDockable convert( ConvertedPlaceholderListItem item ){
				if( children == null ){
					return null;
				}
				int id = item.getInt( "id" );
				PerspectiveDockable dockable = children.get( id );
				dockable.setParent( ToolbarDockPerspective.this );
				return dockable;
			}
		} );
		setDockables( dockables );
	}
	
	/**
	 * Converts the list of children of this perspective into a {@link PlaceholderMap}.
	 * @param children the unique identifier of each child
	 * @return the map of children
	 */
	public PlaceholderMap getPlaceholders( final Map<PerspectiveDockable, Integer> children ){
		return getDockables().toMap( new PlaceholderListItemAdapter<PerspectiveDockable, PerspectiveDockable>(){
			@Override
			public ConvertedPlaceholderListItem convert( int index, PerspectiveDockable dockable ){
				Integer id = children.get( dockable );
				if( id == null ){
					return null;
				}
				
				ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
				item.putInt( "id", id );
				item.putInt( "index", index );
				Path placeholder = dockable.getPlaceholder();
				if( placeholder != null ){
					item.putString( "placeholder", placeholder.toString() );
					item.setPlaceholder( placeholder );
				}
				return item;
			}
		});
	}
	
	@Override
	public String getFactoryID(){
		return ToolbarDockStationFactory.ID;
	}
	
	/**
	 * Sets the preferred size of this station.
	 * @param state the preferred size, not <code>null</code>
	 */
	public void setExpandedState( ExpandedState state ){
		if( state == null ){
			throw new IllegalArgumentException( "state must not be null" );
		}
		this.state = state;
	}
	
	/**
	 * Gets the preferred size of this station.
	 * @return the preferred size, not <code>null</code>
	 */
	public ExpandedState getExpandedState(){
		return state;
	}
	
	@Override
	protected DockableProperty getDockableProperty( int index, Path placeholder, PerspectiveDockable child, PerspectiveDockable target ){
		return new ToolbarProperty( index, placeholder );
	}
}
