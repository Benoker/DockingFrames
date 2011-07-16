/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
import bibliothek.util.Path;

/**
 * A {@link PlaceholderList} that works with real {@link Dockable}s.
 * @author Benjamin Sigg
 * @param <P> the kind of object that represents a {@link Dockable}
 */
public class DockablePlaceholderList<P extends PlaceholderListItem<Dockable>> extends PlaceholderList<Dockable, DockStation, P>{

	/**
	 * Creates a new and empty list.
	 */
	public DockablePlaceholderList(){
		// nothing
	}
	
	/**
	 * Creates a new list reading all the data that is stored in <code>map</code>. This
	 * constructor stores all placeholders that are described in <code>map</code>, obsolete
	 * placeholders may be deleted as soon as a {@link PlaceholderStrategy} is set.
	 * @param map the map to read, not <code>null</code>
	 * @throws IllegalArgumentException if <code>map</code> was not written by a {@link PlaceholderList}
	 */
	public DockablePlaceholderList( PlaceholderMap map ){
		this( map, new StrategyPlaceholderListItemConverter<P>( null ));
	}
	
	/**
	 * Creates a new list reading all the data that is stored in <code>map</code>. This
	 * constructor stores all placeholders that are described in <code>map</code>, obsolete
	 * placeholders may be deleted as soon as a {@link PlaceholderStrategy} is set.
	 * @param map the map to read, not <code>null</code>
	 * @param converter used to convert items back to dockables, not <code>null</code>
	 * @throws IllegalArgumentException if <code>map</code> was not written by a {@link PlaceholderList}
	 */
	public DockablePlaceholderList( PlaceholderMap map, PlaceholderListItemConverter<Dockable,P> converter ){
		super( map, converter );
	}

	/**
	 * Simulates a call to {@link #read(PlaceholderMap, PlaceholderListItemConverter)} and makes all calls to <code>converter</code>
	 * that would be made in a real read as well. 
	 * @param map the map to read
	 * @param converter used to convert items back to dockables, not <code>null</code>
	 * @param <P> the kind of data <code>converter</code> handles
	 */
	public static <P extends PlaceholderListItem<Dockable>> void simulatedRead( PlaceholderMap map, PlaceholderListItemConverter<Dockable,P> converter ){
		DockablePlaceholderList<P> list = new DockablePlaceholderList<P>();
		list.read( map, converter, true );
	}
	
	/**
	 * Converts this list into a {@link PlaceholderMap}, any remaining {@link Dockable} or
	 * {@link DockStation} will be converted into its placeholder using the currently installed
	 * {@link PlaceholderStrategy}. 
	 * @return the new map, not <code>null</code>
	 */
	public PlaceholderMap toMap(){
		return toMap( new StrategyPlaceholderListItemConverter<P>( getStrategy() ) );
	}
	
	@Override
	protected Path getPlaceholder( Dockable dockable ){
		PlaceholderStrategy strategy = getStrategy();
		if( strategy != null ){
			return strategy.getPlaceholderFor( dockable );
		}
		return null;
	}
	
	@Override
	protected DockStation toStation( Dockable dockable ){
		return dockable.asDockStation();
	}
	
	@Override
	protected Dockable[] getChildren( DockStation station ){
		Dockable[] children = new Dockable[ station.getDockableCount() ];
		for( int i = 0; i < children.length; i++ ){
			children[i] = station.getDockable( i );
		}
		return children;
	}
	
	@Override
	protected PlaceholderMap getPlaceholders( DockStation station ){
		return station.getPlaceholders();
	}
	
	@Override
	protected void setPlaceholders( DockStation station, PlaceholderMap map ){
		station.setPlaceholders( map );
	}
	
	@Override
	protected String toString( Dockable dockable ){
		return "'" + dockable.getTitleText() + "'";
	}
}
