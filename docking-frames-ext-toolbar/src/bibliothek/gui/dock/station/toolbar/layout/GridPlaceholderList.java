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
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderListItemConverter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.toolbar.layout.grid.Column;
import bibliothek.gui.dock.station.toolbar.layout.grid.ColumnItem;
import bibliothek.util.Path;

/**
 * This {@link PlaceholderList} stores {@link Column}s as items, where one
 * {@link Column} is just another {@link PlaceholderList}.
 * 
 * @author Benjamin Sigg
 * @param <D>
 *            the kind of object that should be treated as {@link Dockable}
 * @param <S>
 *            the kind of object that should be treated as {@link DockStation}
 * @param <P>
 *            the type of item which represents a {@link Dockable}
 */
public abstract class GridPlaceholderList<D, S, P extends PlaceholderListItem<D>> extends PlaceholderList<ColumnItem<D, S, P>, ColumnItem<D, S, P>, Column<D, S, P>> {
	/**
	 * This {@link PlaceholderListItemConverter} is used to read and write
	 * {@link Column}s, the default setting just ignores any reference to any
	 * {@link Dockable}.
	 */
	private PlaceholderListItemConverter<D, P> converter;

	public GridPlaceholderList(){
		setConverter( null );
	}

	/**
	 * Factory method creating an new {@link Column} that can be added to this
	 * list.
	 * 
	 * @param content
	 *            the content of the column, not <code>null</code>
	 * @return a new, empty column that is yet unknown to this list
	 */
	public Column<D, S, P> createColumn( PlaceholderList<D, S, P> content ){
		if( content == null ) {
			throw new IllegalArgumentException( "content must not be null" );
		}
		return new ColumnList( content );
	}

	/**
	 * Sets the {@link PlaceholderListItemConverter} which should be used to
	 * convert the internal lists of this grid.
	 * 
	 * @param converter
	 *            the converter to use, can be <code>null</code>
	 */
	public void setConverter( PlaceholderListItemConverter<D, P> converter ){
		if( converter == null ) {
			converter = new PlaceholderListItemConverter<D, P>(){
				@Override
				public ConvertedPlaceholderListItem convert( int index, P dockable ){
					return null;
				}

				@Override
				public P convert( ConvertedPlaceholderListItem item ){
					return null;
				}

				@Override
				public void added( P dockable ){
					// ignore
				}
			};
		}
		this.converter = converter;
	}

	/**
	 * Gets the converter that is used to read and write {@link Column}s.
	 * 
	 * @return the converter, never <code>null</code>
	 */
	public PlaceholderListItemConverter<D, P> getConverter(){
		return converter;
	}

	@Override
	protected Path getPlaceholder( ColumnItem<D, S, P> dockable ){
		return dockable.getPlaceholder();
	}

	@Override
	protected String toString( ColumnItem<D, S, P> dockable ){
		return dockable.toString();
	}

	@Override
	protected ColumnItem<D, S, P> toStation( ColumnItem<D, S, P> dockable ){
		return dockable.asStation();
	}

	@Override
	protected PlaceholderMap getPlaceholders( ColumnItem<D, S, P> station ){
		return station.getPlaceholders();
	}

	@Override
	protected void setPlaceholders( ColumnItem<D, S, P> station, PlaceholderMap map ){
		station.setPlaceholders( map );
	}

	@Override
	protected ColumnItem<D, S, P>[] getChildren( ColumnItem<D, S, P> station ){
		return station.getChildren();
	}

	/**
	 * Converts the item <code>dockable</code> to a station.
	 * 
	 * @param dockable
	 *            the item to convert
	 * @return the converted item or <code>null</code>
	 */
	protected abstract S itemToStation( D dockable );

	/**
	 * Gets all the children of <code>station</code>
	 * 
	 * @param station
	 *            the station whose children are searched
	 * @return all the children
	 */
	protected abstract D[] getItemChildren( S station );

	/**
	 * Gest the placeholder of <code>dockable</code>.
	 * 
	 * @param dockable
	 *            some element of this grid
	 * @return the placeholder or <code>null</code>
	 */
	protected abstract Path getItemPlaceholder( D dockable );

	/**
	 * Gets all the placeholders of <code>station</code>
	 * 
	 * @param station
	 *            some station
	 * @return all the placeholders, can be <code>null</code>
	 */
	protected abstract PlaceholderMap getItemPlaceholders( S station );

	/**
	 * Sets the placeholders that are to be used by <code>station</code>
	 * 
	 * @param station
	 *            the station whose layout is to be updated
	 * @param map
	 *            the new placeholders, never <code>null</code>
	 */
	protected abstract void setItemPlaceholders( S station, PlaceholderMap map );

	/**
	 * Represents some kind of {@link Dockable}.
	 * 
	 * @author Benjamin Sigg
	 */
	private class DockableItem implements ColumnItem<D, S, P> {
		private final D item;

		public DockableItem( D item ){
			this.item = item;
		}

		@Override
		public String toString(){
			return item.toString();
		}

		/**
		 * Gets <code>this</code> if {@link #item} is some kind of station.
		 * 
		 * @return
		 */
		@Override
		public ColumnItem<D, S, P> asStation(){
			final S station = itemToStation( item );
			if( station == null ) {
				return null;
			}
			return new StationItem( station );
		}

		@Override
		public Path getPlaceholder(){
			return getItemPlaceholder( item );
		}

		@Override
		public ColumnItem<D, S, P>[] getChildren(){
			throw new IllegalStateException( "a " + getClass().getSimpleName() + " must not be asked for its children" );
		}

		@Override
		public PlaceholderMap getPlaceholders(){
			return null;
		}

		@Override
		public void setPlaceholders( PlaceholderMap map ){
			// ignore
		}
	}

	/**
	 * Represents some kind of {@link DockStation}.
	 * 
	 * @author Benjamin Sigg
	 */
	private class StationItem implements ColumnItem<D, S, P> {
		private final S item;

		public StationItem( S item ){
			this.item = item;
		}

		@Override
		public ColumnItem<D, S, P> asStation(){
			return this;
		}

		@Override
		public Path getPlaceholder(){
			throw new IllegalStateException( "a " + getClass().getSimpleName() + " must not be asked for its placeholder" );
		}

		@SuppressWarnings("unchecked")
		@Override
		public ColumnItem<D, S, P>[] getChildren(){
			final D[] children = getItemChildren( item );
			final ColumnItem<D, S, P>[] result = new ColumnItem[children.length];
			for( int i = 0; i < result.length; i++ ) {
				result[i] = new DockableItem( children[i] );
			}
			return result;
		}

		@Override
		public PlaceholderMap getPlaceholders(){
			return getItemPlaceholders( item );
		}

		@Override
		public void setPlaceholders( PlaceholderMap map ){
			setItemPlaceholders( item, map );
		}
	}

	private class ColumnList implements Column<D, S, P> {
		private final PlaceholderList<D, S, P> list;

		public ColumnList( PlaceholderList<D, S, P> list ){
			this.list = list;
		}

		@Override
		public ColumnItem<D, S, P> asDockable(){
			return this;
		}

		@Override
		public ColumnItem<D, S, P> asStation(){
			return this;
		}

		@SuppressWarnings("unchecked")
		@Override
		public ColumnItem<D, S, P>[] getChildren(){
			final ColumnItem<D, S, P>[] result = new ColumnItem[list.dockables().size()];
			int index = 0;
			for( final P item : list.dockables() ) {
				result[index++] = new DockableItem( item.asDockable() );
			}
			return result;
		}

		@Override
		public PlaceholderMap getPlaceholders(){
			return list.toMap( converter );
		}

		@Override
		public void setPlaceholders( PlaceholderMap map ){
			list.clear();
			list.read( map, converter );
		}

		/**
		 * Gets the list of {@link Dockable}s of this column.
		 * 
		 * @return the list of items
		 */
		@Override
		public PlaceholderList<D, S, P> getList(){
			return list;
		}

		@Override
		public Path getPlaceholder(){
			return null;
		}
	}
}
