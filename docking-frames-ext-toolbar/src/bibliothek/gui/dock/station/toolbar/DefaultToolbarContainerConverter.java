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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockHierarchyLock;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.PerspectivePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.Path;

/**
 * Default implementation of {@link ToolbarContainerConverter}. This converter
 * supports all features necessary to read and write {@link PlaceholderMap}s.
 * 
 * @author Benjamin Sigg
 * @author Herve Guillaume
 */
public class DefaultToolbarContainerConverter implements ToolbarContainerConverter {
	@Override
	public PlaceholderMap getPlaceholders( ToolbarContainerDockStation station ){
		final PlaceholderMap result = new PlaceholderMap( new Path( "dock.ToolbarContainerStation" ), 0 );
		result.put( result.newKey( "content" ), "list", station.getDockables().toMap() );
		return result;
	}

	@Override
	public PlaceholderMap getPlaceholders( ToolbarContainerDockStation station, Map<Dockable, Integer> children ){
		final PlaceholderMap result = new PlaceholderMap( new Path( "dock.ToolbarContainerStation" ), 0 );
		result.put( result.newKey( "content" ), "list", convert( station.getPlaceholderStrategy(), station.getDockables(), children ) );
		return result;
	}

	@Override
	public PlaceholderMap getPlaceholders( ToolbarContainerDockPerspective station, Map<PerspectiveDockable, Integer> children ){
		final PlaceholderMap result = new PlaceholderMap( new Path( "dock.ToolbarContainerStation" ), 0 );
		result.put( result.newKey( "content" ), "list", convert( station.getDockables(), children ) );
		return result;
	}
	
	private PlaceholderMap convert( final PlaceholderStrategy strategy, DockablePlaceholderList<StationChildHandle> list, final Map<Dockable, Integer> children ){
		return list.toMap( new PlaceholderListItemAdapter<Dockable, StationChildHandle>(){
			@Override
			public ConvertedPlaceholderListItem convert( int index, StationChildHandle handle ){
				final Dockable dockable = handle.getDockable();

				final ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
				final Integer id = children.get( dockable );
				if( id == null ) {
					return null;
				}

				item.putInt( "id", id );
				item.putInt( "index", index );
				if( strategy != null ) {
					final Path placeholder = strategy.getPlaceholderFor( dockable );
					if( placeholder != null ) {
						item.putString( "placeholder", placeholder.toString() );
						item.setPlaceholder( placeholder );
					}
				}
				return item;
			}
		} );
	}
	
	private PlaceholderMap convert( PerspectivePlaceholderList<PerspectiveDockable> list, final Map<PerspectiveDockable, Integer> children ){
		return list.toMap( new PlaceholderListItemAdapter<PerspectiveDockable, PerspectiveDockable>(){
			@Override
			public ConvertedPlaceholderListItem convert( int index, PerspectiveDockable dockable ){
				final ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
				final Integer id = children.get( dockable );
				if( id == null ) {
					return null;
				}

				item.putInt( "id", id );
				item.putInt( "index", index );
				Path placeholder = dockable.getPlaceholder();
				if( placeholder != null ) {
					item.putString( "placeholder", placeholder.toString() );
					item.setPlaceholder( placeholder );
				}
				return item;
			}
		} );
	}

	@Override
	public void setPlaceholders( ToolbarContainerDockStation station, PlaceholderMap map ){
		if( !map.getFormat().equals( new Path( "dock.ToolbarContainerStation" ) ) ) {
			throw new IllegalArgumentException( "unknown format: " + map.getFormat() );
		}
		if( map.getVersion() != 0 ) {
			throw new IllegalArgumentException( "unknown version: " + map.getVersion() );
		}

		station.setPlaceholders( map.getMap( map.newKey( "content" ), "list" ) );
	}

	@Override
	public void setPlaceholders( ToolbarContainerDockStation station, ToolbarContainerConverterCallback callback, PlaceholderMap map, Map<Integer, Dockable> children ){
		convert( station, callback, map.getMap( map.newKey( "content" ), "list" ), children );
	}

	private void convert( final ToolbarContainerDockStation station, final ToolbarContainerConverterCallback callback, PlaceholderMap map, final Map<Integer, Dockable> children ){
		final DockablePlaceholderList<StationChildHandle> list = new DockablePlaceholderList<StationChildHandle>();
		callback.setDockables( list );
		list.read( map, new PlaceholderListItemAdapter<Dockable, StationChildHandle>(){
			private DockHierarchyLock.Token token;

			@Override
			public StationChildHandle convert( ConvertedPlaceholderListItem item ){
				final int id = item.getInt( "id" );
				final Dockable dockable = children.get( id );
				if( dockable == null ) {
					return null;
				}

				DockUtilities.ensureTreeValidity( station, dockable );
				token = DockHierarchyLock.acquireLinking( station, dockable );

				final StationChildHandle handle = callback.wrap( dockable );
				callback.adding( handle );
				return handle;
			}

			@Override
			public void added( StationChildHandle handle ){
				try {
					callback.added( handle );
				}
				finally {
					token.release();
				}
			}
		} );
		callback.finished( list );
	}
}
