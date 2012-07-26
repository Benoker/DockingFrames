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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderMap.Key;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.DockUtilities.DockVisitor;
import bibliothek.util.Path;

/**
 * An implementation of {@link PlaceholderToolbarGrid} that uses {@link Dockable}s and {@link DockStation}s.
 * 
 * @author Benjamin Sigg
 * @param <P> the kind of object that represents a {@link Dockable}
 */
public class DockablePlaceholderToolbarGrid<P extends PlaceholderListItem<Dockable>> extends ModeledPlaceholderToolbarGrid<Dockable, DockStation, P> {
	/**
	 * Creates and initializes a new grid
	 */
	public DockablePlaceholderToolbarGrid(){
		init();
	}

	@Override
	protected PlaceholderList<Dockable, DockStation, P> createColumn(){
		return new DockablePlaceholderList<P>();
	}

	@Override
	protected GridPlaceholderList<Dockable, DockStation, P> createGrid(){
		return new DockableGridPlaceholderList<P>();
	}

	@Override
	protected Set<Path> getPlaceholders( Dockable dockable ){
		final PlaceholderStrategy strategy = getStrategy();
		if( strategy == null ) {
			return Collections.emptySet();
		}
		final Set<Path> placeholders = new HashSet<Path>();
		DockUtilities.visit( dockable, new DockVisitor(){
			@Override
			public void handleDockable( Dockable dockable ){
				final Path placeholder = strategy.getPlaceholderFor( dockable );
				if( placeholder != null ) {
					placeholders.add( placeholder );
				}
			}

			@Override
			public void handleDockStation( DockStation station ){
				final PlaceholderMap map = station.getPlaceholders();
				if( map != null ) {
					for( final Key key : map.getPlaceholders() ) {
						for( final Path placeholder : key.getPlaceholders() ) {
							placeholders.add( placeholder );
						}
					}
				}
			}
		} );
		return placeholders;
	}

	@Override
	protected void fill( Dockable dockable, ConvertedPlaceholderListItem item ){
		final PlaceholderStrategy strategy = getStrategy();
		if( strategy != null ) {
			final Path placeholder = strategy.getPlaceholderFor( dockable );
			if( placeholder != null ) {
				item.putString( "placeholder", placeholder.toString() );
				item.setPlaceholder( placeholder );
			}
		}
		DockStation station = dockable.asDockStation();
		if( station != null ){
			item.setPlaceholderMap( station.getPlaceholders() );
		}
	}

}
