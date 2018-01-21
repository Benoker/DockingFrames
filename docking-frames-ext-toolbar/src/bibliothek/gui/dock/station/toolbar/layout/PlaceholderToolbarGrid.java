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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderList.Filter;
import bibliothek.gui.dock.station.support.PlaceholderList.Level;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderMap.Key;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.support.PlaceholderStrategyListener;
import bibliothek.gui.dock.station.toolbar.layout.grid.Column;
import bibliothek.gui.dock.station.toolbar.layout.grid.ColumnItem;
import bibliothek.util.Path;

/**
 * A {@link PlaceholderToolbarGrid} behaves like a list of {@link PlaceholderList}s.
 * 
 * @author Benjamin Sigg
 * @param <D> the kind of object that should be treated as {@link Dockable}
 * @param <S> the kind of object that should be treated as {@link DockStation}
 * @param <P> the type of item which represents a {@link Dockable}
 */
public abstract class PlaceholderToolbarGrid<D, S, P extends PlaceholderListItem<D>> {
	/** defines the order of all columns (visible and invisible columns) */
	private GridPlaceholderList<D, S, P> columns;

	/** the currently used strategy to detect invalid placeholders */
	private PlaceholderStrategy strategy;

	/**
	 * this listener is added to {@link #strategy} if {@link #bound} is
	 * <code>true</code>
	 */
	private final PlaceholderStrategyListener strategyListener = new PlaceholderStrategyListener(){
		@Override
		public void placeholderInvalidated( Set<Path> placeholders ){
			purge();
		}
	};

	/** tells whether this grid is currently used */
	private boolean bound = false;

	/**
	 * Initializes all fields of this object.
	 */
	protected void init(){
		columns = createGrid();
	}

	/**
	 * Creates one column.
	 * 
	 * @return a new, empty list
	 */
	protected abstract PlaceholderList<D, S, P> createColumn();

	/**
	 * Creates a new {@link GridPlaceholderList}.
	 * 
	 * @return the new, empty grid
	 */
	protected abstract GridPlaceholderList<D, S, P> createGrid();

	/**
	 * Gets all placeholders that are associated with <code>dockable</code>.
	 * 
	 * @param dockable
	 *            some element used by this grid
	 * @return the placeholders
	 */
	protected abstract Set<Path> getPlaceholders( D dockable );
	
	/**
	 * Called if <code>item</code> was added to the column <code>column</code>.
	 * @param column a visible column
	 * @param columnIndex the index of the column
	 * @param item a new item of <code>column</code>
	 * @param itemIndex the index of the new item
	 */
	protected abstract void onInserted( PlaceholderList<D, S, P> column, int columnIndex, P item, int itemIndex );
	
	/**
	 * Called if <code>item</code> was removed to the column <code>column</code>.
	 * @param column a visible column
	 * @param columnIndex the index of the column
	 * @param item the removed item of <code>column</code>
	 * @param itemIndex the index the item had before removing
	 */
	protected abstract void onRemoved( PlaceholderList<D, S, P> column, int columnIndex, P item, int itemIndex );
	
	/**
	 * Called if a new column <code>column</code> was added to this grid. The column may not contain any elements when
	 * this method is called.
	 * @param column the new column
	 * @param index the index of the new column
	 */
	protected abstract void onInserted( PlaceholderList<D, S, P> column, int index );
	
	/**
	 * Called if the column <code>column</code> was removed from this grid. The column may still contain elements when it
	 * is removed.
	 * @param column the removed column
	 * @param index the index the column had before it was removed
	 */
	protected abstract void onRemoved( PlaceholderList<D, S, P> column, int index );
	
	/**
	 * Called if all columns were inserted at the same time. This means that the grid
	 * previously was completely empty.
	 */
	protected abstract void onInserted();
	
	/**
	 * Called if all columns were removed from this grid
	 */
	protected abstract void onRemoved();
	
	/**
	 * Removes all content from this grid.
	 */
	public void clear(){
		purge();
		for( final Column<D, S, P> column : columns.dockables() ) {
			column.getList().unbind();
			column.getList().setStrategy( null );
		}
		columns.clear();
		onRemoved();
	}

	/**
	 * Adds the item <code>item</code> to the non-empty column
	 * <code>column</code> into position <code>line</code>. This method may add
	 * a new column in order to store <code>item</code>.
	 * 
	 * @param column
	 *            the column in which to store <code>item</code>
	 * @param line
	 *            the index within <code>column</code>
	 * @param item
	 *            the item to store, not <code>null</code>
	 * @throws IllegalArgumentException
	 *             if <code>item</code> is <code>null</code>
	 * @throws IllegalStateException
	 *             if there is no {@link PlaceholderStrategy} set
	 */
	public void insert( int column, int line, P item ){
		if( item == null ) {
			throw new IllegalArgumentException( "item must not be null" );
		}

		final PlaceholderList<D, S, P> list = getColumn( column );
		if( list == null ) {
			insert( column, item, true );
		}
		else {
			int index = Math.min( line, list.dockables().size() );
			list.dockables().add( index, item );
			ensureRemoved( list, item );
			onInserted( list, column, item, index );
		}
	}

	/**
	 * Adds the item <code>item</code> to a new column, the new column will have
	 * the index <code>columnIndex</code>. If <code>columnIndex</code> is out of
	 * bounds, then the new column will be added as near as possible to the
	 * preferred position. This method will try to reuse an empty column, if one
	 * is available at the desired location.
	 * 
	 * @param columnIndex
	 *            the column to add
	 * @param item
	 *            the item to store, not <code>null</code>
	 */
	public void insert( int columnIndex, P item ){
		insert( columnIndex, item, true );
	}

	/**
	 * Adds the item <code>item</code> to a new column, the new column will have
	 * the index <code>columnIndex</code>. If <code>columnIndex</code> is out of
	 * bounds, then the new column will be added as near as possible to the
	 * preferred position.
	 * 
	 * @param columnIndex
	 *            the column to add
	 * @param item
	 *            the item to store, not <code>null</code>
	 * @param reuse
	 *            if <code>false</code> then a new column will be built in any
	 *            case, if <code>true</code> then this grid tries to reuse an
	 *            existing yet empty column if possible
	 */
	public void insert( int columnIndex, P item, boolean reuse ){
		final PlaceholderList<D, S, P> columnList = createColumn();
		final Column<D, S, P> column = columns.createColumn( columnList );
		boolean added = false;
		int addedColumnIndex = -1;
		
		if( reuse ) {
			int baseIndex;
			if( columns.dockables().size() > 0 ) {
				baseIndex = columns.levelToBase( Math.max( 0, Math.min( columns.dockables().size() - 1, columnIndex ) ), Level.DOCKABLE );
				baseIndex--;
			}
			else {
				baseIndex = 0;
			}

			if( (baseIndex >= 0) && (baseIndex < columns.list().size()) ) {
				final PlaceholderList<?, ?, Column<D, S, P>>.Item columnItem = columns.list().get( baseIndex );
				if( columnItem.getDockable() == null ) {
					final PlaceholderMap map = columnItem.getPlaceholderMap();
					if( map != null ) {
						columnList.read( map, columns.getConverter() );
					}
					columnItem.setDockable( column );
					addedColumnIndex = columns.dockables().indexOf( column );
					onInserted( column.getList(), addedColumnIndex );
					added = true;
				}
			}
		}

		columnList.dockables().add( item );
		if( added ){
			onInserted( columnList, addedColumnIndex, item, columnList.dockables().size()-1 );
		}
		else{
			int index = Math.max( 0, Math.min( columnIndex, columns.dockables().size() ) );
			columns.dockables().add( index, column );
			onInserted( columnList, index );
			onInserted( columnList, index, item, columnList.dockables().size()-1 );
		}

		if( bound ) {
			columnList.setStrategy( strategy );
		}

		ensureRemoved( columnList, item );
	}

	/**
	 * Moves the item at <code>sourceColumn/sourceLine</code> to
	 * <code>destinationColumn/destinationLine</code>. The operation behaves as
	 * if the item would first be removed from the source position, and
	 * afterwards inserted at the destination position.
	 * 
	 * @param sourceColumn
	 *            the column in which to find the item, only includes non-empty
	 *            columns
	 * @param sourceLine
	 *            the line in the column in which to find the item
	 * @param destinationColumn
	 *            the column in which to insert the item
	 * @param destinationLine
	 *            the line at which to insert the item
	 * @param destinationLevel
	 *            the level at which to find <code>destinationColumn</code>,
	 *            will be converted to an index from {@link Level#BASE}
	 * @throws IllegalArgumentException
	 *             if any index is out of bounds
	 */
	public void move( int sourceColumn, int sourceLine, int destinationColumn, int destinationLine, Level destinationLevel ){
		PlaceholderList<D, S, P> source = columns.dockables().get( sourceColumn ).getList();
		final Filter<P> sourceList = source.dockables();
		int destinationColumnIndex = -1;
		
		if( destinationColumn == columns.size( destinationLevel ) ) {
			destinationColumn = columns.size( Level.BASE );
		}
		else if( destinationColumn >= 0 ) {
			destinationColumn = columns.levelToBase( destinationColumn, destinationLevel );
		}

		if( (sourceLine < 0) || (sourceLine >= sourceList.size()) ) {
			throw new IllegalArgumentException( "sourceLine out of bounds: " + sourceLine );
		}

		if( (destinationColumn < -1) || (destinationColumn > columns.list().size()) ) {
			throw new IllegalArgumentException( "destinationColumn out of bounds: " + destinationColumn );
		}

		final P value = sourceList.get( sourceLine );

		PlaceholderList<D, S, P> list;
		if( (destinationColumn == -1) || (destinationColumn == columns.list().size()) ) {
			list = createColumn();
			if( destinationLine != 0 ) {
				throw new IllegalArgumentException( "destinationLine is out of bounds: " + destinationLine );
			}
			final PlaceholderList<ColumnItem<D, S, P>, ColumnItem<D, S, P>, Column<D, S, P>>.Item item = columns.new Item( columns.createColumn( list ) );
			if( destinationColumn == -1 ) {
				columns.list().add( 0, item );
			}
			else {
				columns.list().add( item );
			}
			destinationColumnIndex = columns.dockables().indexOf( item.getDockable() );
			onInserted( list, destinationColumnIndex );
		}
		else {
			final PlaceholderList<?, ?, Column<D, S, P>>.Item item = columns.list().get( destinationColumn );
			if( item.getDockable() == null ) {
				list = createColumn();
				if( destinationLine != 0 ) {
					throw new IllegalArgumentException( "destinationLine is out of bounds: " + destinationLine );
				}
				item.setDockable( columns.createColumn( list ) );
				destinationColumnIndex = columns.dockables().indexOf( item.getDockable() );
				onInserted( list, destinationColumnIndex );
			}
			else {
				list = item.getDockable().getList();
				if( (destinationLine < 0) || (destinationLine > list.dockables().size()) ) {
					throw new IllegalArgumentException( "destinationLine out of bounds: " + destinationLine );
				}
				destinationColumnIndex = columns.baseToLevel( destinationColumn, Level.DOCKABLE );
			}
			
		}

		P moved = sourceList.get( sourceLine );
		
		list.dockables().move( sourceList, sourceLine, destinationLine );
		ensureRemoved( list, value );
		
		onRemoved( source, sourceColumn, moved, sourceLine );
		onInserted( list, destinationColumnIndex, moved, destinationLine );
		
		purge();
	}

	/**
	 * Tries to put <code>item</code> into this list at location
	 * <code>placeholder</code>. If there is already an element at
	 * <code>placeholder</code>, then the old item is silently removed and the
	 * new item inserted. This method may create a new non-empty column if
	 * necessary.
	 * 
	 * @param placeholder
	 *            the name of the item
	 * @param item
	 *            the item to insert
	 * @return <code>true</code> if insertion was a success, <code>false</code>
	 *         otherwise
	 */
	public boolean put( Path placeholder, P item ){
		final int listIndex = columns.getListIndex( placeholder );
		if( listIndex == -1 ) {
			return false;
		}
		final PlaceholderList<?, ?, Column<D, S, P>>.Item listItem = columns.list().get( listIndex );

		Column<D, S, P> column = listItem.getDockable();
		if( column != null ) {
			int columnIndex = columns.dockables().indexOf( column );
			
			// int removedIndex = column.getList().getDockableIndex( placeholder );
			int replacingListIndex = column.getList().getListIndex( placeholder );
			if( replacingListIndex >= 0 ){
				P removed = column.getList().list().get( replacingListIndex ).getDockable();
				int size = column.getList().dockables().size();
				int removedIndex = column.getList().put( placeholder, item );
				if( removed != null ){
					onRemoved( column.getList(), columnIndex, removed, removedIndex );
					if( size == 0 && item == null ){
						onRemoved( column.getList(), columnIndex );
						listItem.setDockable( null );
					}
				}
				
				if( item != null ){
					if( size == 0 ){
						onInserted( column.getList(), columnIndex );
					}
					onInserted( column.getList(), columnIndex, item, column.getList().dockables().indexOf( item ) );
				}
				return true;
			}
		}
		final PlaceholderMap map = listItem.getPlaceholderMap();
		if( map != null ) {
			final PlaceholderList<D, S, P> list = createColumn();
			list.read( map, columns.getConverter() );
			column = columns.createColumn( list );

			listItem.setDockable( column );
			int columnIndex = columns.dockables().indexOf( column );
			
			onInserted( list, columnIndex );
			
			int insertIndex = column.getList().put( placeholder, item );
			if( insertIndex == -1 ) {
				listItem.setDockable( null );
				onRemoved( list, columnIndex );
				return false;
			}
			else {
				listItem.setPlaceholderMap( null );
				ensureRemoved( list, placeholder );
				onInserted( list, columnIndex, item, insertIndex );
			}
			return true;
		}
		return false;
	}

	/**
	 * Stores the placeholder <code>placeholder</code> in the designated column.
	 * 
	 * @param column
	 *            the column in which to add <code>placeholder</code>, only
	 *            includes non-empty columns
	 * @param line
	 *            the line in which to add <code>placeholder</code>
	 * @param placeholder
	 *            the placeholder to store
	 */
	public void addPlaceholder( int column, int line, Path placeholder ){
		columns.dockables().addPlaceholder( column, placeholder );
		final Column<D, S, P> item = columns.dockables().get( column );
		item.getList().dockables().addPlaceholder( line, placeholder );
		ensureRemoved( item.getList(), placeholder );
	}
	
	/**
	 * Inserts <code>placeholder</code> into column <code>column</code> at <code>line</code>. This
	 * method may create a new column if <code>column</code> is as big as the grid.
	 * @param column the column into which to insert <code>placeholder</code>, includes
	 * empty columns.
	 * @param line the line into which to insert <code>placeholder</code>
	 * @param placeholder the new placeholder, not <code>null</code>
	 */
	public void insertPlaceholder( int column, int line, Path placeholder ){
		if( column == columns.list().size() ){
			columns.list().insertPlaceholder( column, placeholder );
		}
		else{
			columns.list().addPlaceholder( column, placeholder );
		}
		PlaceholderList<ColumnItem<D, S, P>, ColumnItem<D, S, P>, Column<D, S, P>>.Item item = columns.list().get( column );
		if( item.getDockable() == null ){
			item.setDockable( columns.createColumn( createColumn() ));
		}
		Filter<PlaceholderList<D,S,P>.Item> lineList = item.getDockable().getList().list();
		
		if( line == lineList.size() ){
			lineList.insertPlaceholder( line, placeholder );
		}
		else{
			lineList.addPlaceholder( line, placeholder );
		}
	}

	/**
	 * Removes <code>item</code> from this grid, but leaves a placeholder for
	 * the item.
	 * @param item the item to remove
	 * @return <code>true</code> if <code>item</code> was found and removed
	 */
	public boolean remove( P item ){
		boolean result = false;
		int columnIndex = -1;
		for( final Column<D, S, P> column : columns.dockables() ) {
			columnIndex++;
			int index = column.getList().dockables().indexOf( item );
			if( index >= 0 ){
				column.getList().remove( item );
				onRemoved( column.getList(), columnIndex, item, index );
				result = true;
			}
		}
		purge();
		return result;
	}
	
	/**
	 * Removes all occurences of <code>placeholder</code>.
	 * @param placeholder the placeholder to remove
	 */
	public void removePlaceholder( Path placeholder ){
		Set<Path> set = new HashSet<Path>();
		set.add( placeholder );
		ensureRemoved( null, set );
	}

	private void ensureRemoved( PlaceholderList<D, S, P> ignore, P item ){
		final Set<Path> placeholders = getPlaceholders( item.asDockable() );
		ensureRemoved( ignore, placeholders );
	}

	private void ensureRemoved( PlaceholderList<D, S, P> ignore, Path placeholder ){
		final Set<Path> set = new HashSet<Path>();
		set.add( placeholder );
		ensureRemoved( ignore, set );
	}

	private void ensureRemoved( PlaceholderList<D, S, P> ignore, Set<Path> placeholders ){
		final Iterator<PlaceholderList<ColumnItem<D, S, P>, ColumnItem<D, S, P>, Column<D, S, P>>.Item> iter = columns.list().iterator();
		while( iter.hasNext() ) {
			final PlaceholderList<?, ?, Column<D, S, P>>.Item item = iter.next();
			if( (item.getDockable() == null) || (item.getDockable().getList() != ignore) ) {
				item.removeAll( placeholders );
				if( (item.getPlaceholderSet() == null) && item.isPlaceholder() ) {
					iter.remove();
				}
			}
		}

		for( final Column<D, S, P> column : columns.dockables() ) {
			if( column.getList() != ignore ) {
				column.getList().removeAll( placeholders );
			}
		}

		purge();
	}

	/**
	 * Tells in which non-empty column <code>dockable</code> is.
	 * 
	 * @param dockable
	 *            the item to search
	 * @return the column of the dockable or <code>-1</code> if not found
	 */
	public int getColumn( D dockable ){
		int index = 0;
		final Iterator<PlaceholderList<D, S, P>> columns = columns();
		while( columns.hasNext() ) {
			for( final P item : columns.next().dockables() ) {
				if( item.asDockable() == dockable ) {
					return index;
				}
			}
			index++;
		}
		return -1;
	}

	/**
	 * Gets the index of the first column that contains <code>placeholder</code>
	 * .
	 * 
	 * @param placeholder
	 *            the placeholder to search
	 * @return the first column with <code>placeholder</code> or -1 if not
	 *         found, this includes empty columns
	 */
	public int getColumn( Path placeholder ){
		int index = 0;
		for( final PlaceholderList<?, ?, Column<D, S, P>>.Item item : columns.list() ) {
			if( item.hasPlaceholder( placeholder ) ) {
				return index;
			}
			index++;
		}
		return -1;
	}

	/**
	 * Tells at which position <code>dockable</code> is within its column.
	 * 
	 * @param dockable
	 *            the item to search
	 * @return the location of <code>dockable</code>
	 */
	public int getLine( D dockable ){
		final int column = getColumn( dockable );
		if( column == -1 ) {
			return -1;
		}
		return getLine( column, dockable );
	}

	/**
	 * Tells at which position <code>dockable</code> is within the column
	 * <code>column</code>
	 * 
	 * @param column
	 *            the index of the non-empty column to search
	 * @param dockable
	 *            the item to search
	 * @return the location of <code>dockable</code>
	 */
	public int getLine( int column, D dockable ){
		final PlaceholderList<D, S, P> list = getColumn( column );
		int index = 0;
		for( final P item : list.dockables() ) {
			if( item.asDockable() == dockable ) {
				return index;
			}
			index++;
		}
		return -1;
	}

	/**
	 * Tells at which line <code>placeholder</code> appears in the first column
	 * that contains <code>placeholder</code>. This includes empty columns.
	 * 
	 * @param placeholder
	 *            the placeholder to search
	 * @return the line at which <code>placeholder</code> was found
	 */
	public int getLine( Path placeholder ){
		final int column = getColumn( placeholder );
		if( column == -1 ) {
			return -1;
		}
		return getLine( column, placeholder );
	}

	/**
	 * Tells at which line <code>placeholder</code> appears in the column
	 * <code>column</code>.
	 * 
	 * @param column
	 *            the index of the column, this includes empty columns
	 * @param placeholder
	 *            the placeholder to search
	 * @return the index the item would have or -1 if <code>placeholder</code>
	 *         was not found
	 */
	public int getLine( int column, Path placeholder ){
		final PlaceholderList<?, ?, Column<D, S, P>>.Item item = columns.list().get( column );
		if( item.getDockable() == null ) {
			if( item.hasPlaceholder( placeholder ) ) {
				return 0;
			}
			else {
				return -1;
			}
		}
		else {
			return item.getDockable().getList().list().indexOfPlaceholder( placeholder );
		}
	}

	/**
	 * Tells whether this {@link PlaceholderToolbarGrid} knows a column which
	 * contains the placeholder <code>placeholder</code>, this includes empty
	 * columns.
	 * 
	 * @param placeholder
	 *            the placeholder to search
	 * @return <code>true</code> if <code>placeholder</code> was found
	 */
	public boolean hasPlaceholder( Path placeholder ){
		final int listIndex = columns.getListIndex( placeholder );
		if( listIndex == -1 ) {
			return false;
		}
		final PlaceholderList<?, ?, Column<D, S, P>>.Item item = columns.list().get( listIndex );

		final Column<D, S, P> column = item.getDockable();
		if( column != null ) {
			return column.getList().hasPlaceholder( placeholder );
		}
		final PlaceholderMap map = item.getPlaceholderMap();
		if( map != null ) {
			for( final Key key : map.getPlaceholders() ) {
				if( key.contains( placeholder ) ) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the total count of items stored in this grid.
	 * 
	 * @return the total amount of items
	 */
	public int size(){
		int sum = 0;
		final Iterator<PlaceholderList<D, S, P>> iter = columns();
		while( iter.hasNext() ) {
			sum += iter.next().dockables().size();
		}
		return sum;
	}

	/**
	 * Gets the <code>index</code>'th item of this grid.
	 * 
	 * @param index
	 *            the index of the item
	 * @return the item
	 * @throws IllegalArgumentException
	 *             if <code>index</code> is not valid
	 */
	public P get( int index ){
		if( index < 0 ) {
			throw new IllegalArgumentException( "index must not be < 0" );
		}
		final Iterator<PlaceholderList<D, S, P>> iter = columns();
		while( iter.hasNext() ) {
			final Filter<P> dockables = iter.next().dockables();
			final int size = dockables.size();
			if( index < size ) {
				return dockables.get( index );
			}
			else {
				index -= size;
			}
		}

		throw new IllegalArgumentException( "index must not be >= size" );
	}

	/**
	 * Gets the item that represents <code>dockable</code>
	 * 
	 * @param dockable
	 *            the dockable to search
	 * @return the item that represents <code>dockable</code> or
	 *         <code>null</code> if not found
	 */
	public P get( D dockable ){
		final Iterator<P> iter = items();
		while( iter.hasNext() ) {
			final P next = iter.next();
			if( next.asDockable() == dockable ) {
				return next;
			}
		}
		return null;
	}

	/**
	 * Searches the item which is at the location of <code>placeholder</code>.
	 * 
	 * @param placeholder
	 *            some placeholder that may or may not be known to this grid
	 * @return the item at <code>placeholder</code> or <code>null</code> either
	 *         if <code>placeholder</code> was not found or if there is no item
	 *         stored
	 */
	public P get( Path placeholder ){
		final int listIndex = columns.getListIndex( placeholder );
		if( listIndex == -1 ) {
			return null;
		}
		final PlaceholderList<?, ?, Column<D, S, P>>.Item item = columns.list().get( listIndex );

		final Column<D, S, P> column = item.getDockable();
		if( column == null ) {
			return null;
		}
		return column.getList().getDockableAt( placeholder );
	}

	/**
	 * Gets an iterator over all columns, including the columns with no content.
	 * This does not include columns with no list (columns that consist only of
	 * placeholders).
	 * 
	 * @return all columns
	 */
	protected Iterator<PlaceholderList<D, S, P>> allColumns(){
		return new Iterator<PlaceholderList<D, S, P>>(){
			private final Iterator<Column<D, S, P>> items = columns.dockables().iterator();
			private PlaceholderList<D, S, P> current;
			private int currentIndex = -1;
			
			@Override
			public boolean hasNext(){
				return items.hasNext();
			};

			@Override
			public PlaceholderList<D, S, P> next(){
				current = items.next().getList();
				currentIndex++;
				return current;
			}

			@Override
			public void remove(){
				items.remove();
				onRemoved( current, currentIndex-- );
			}
		};
	}

	/**
	 * Gets an iterator over all non-empty columns. The iterator does not
	 * support modifications nor is it concurrent.
	 * 
	 * @return the iterator
	 */
	protected Iterator<PlaceholderList<D, S, P>> columns(){
		return new Iterator<PlaceholderList<D, S, P>>(){
			private final Iterator<Column<D, S, P>> items = columns.dockables().iterator();
			private PlaceholderList<D, S, P> next;

			private void forward(){
				next = null;
				while( (next == null) && items.hasNext() ) {
					final PlaceholderList<D, S, P> column = items.next().getList();
					if( column.dockables().size() > 0 ) {
						next = column;
					}
				}
			}

			@Override
			public boolean hasNext(){
				if( (next == null) && items.hasNext() ) {
					forward();
				}
				return next != null;
			}

			@Override
			public PlaceholderList<D, S, P> next(){
				final PlaceholderList<D, S, P> result = next;
				forward();
				return result;
			}

			@Override
			public void remove(){
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Gets an iterator that visits all items of this grid.
	 * 
	 * @return the iterator
	 */
	public Iterator<P> items(){
		return new Iterator<P>(){
			private final Iterator<PlaceholderList<D, S, P>> columns = columns();
			private Iterator<P> items = null;
			
			private PlaceholderList<D, S, P> currentList;
			private int currentListIndex = -1;
			private P currentItem;
			private int currentItemIndex = -1;
			private boolean requiresdPurge = false;
			
			private void validate(){
				while( ((items == null) || !items.hasNext()) && columns.hasNext() ) {
					currentList = columns.next();
					currentItemIndex = -1;
					currentListIndex++;
					items = currentList.dockables().iterator();
				}
			}

			@Override
			public boolean hasNext(){
				validate();
				boolean result = (items != null) && items.hasNext();
				if( !result ){
					if( requiresdPurge ){
						purge();
						requiresdPurge = false;
					}
				}
				return result;
			}

			@Override
			public P next(){
				validate();
				currentItem = items.next();
				return currentItem;
			}

			@Override
			public void remove(){
				if( items == null ) {
					throw new IllegalStateException( "no item selected" );
				}
				items.remove();
				onRemoved( currentList, currentListIndex, currentItem, currentItemIndex-- );
				requiresdPurge = true;
			}
		};
	}

	/**
	 * Gets the number of columns that are currently stored in this grid. Empty
	 * columns are excluded.
	 * 
	 * @return the total number of non-empty columns
	 */
	public int getColumnCount(){
		return columns.dockables().size();
	}

	/**
	 * Tells how many items are currently stored at the non-empty column with
	 * index <code>column</code>.
	 * 
	 * @param column
	 *            the index of a non-empty column
	 * @return the size of the column
	 */
	public int getLineCount( int column ){
		return columns.dockables().get( column ).getList().dockables().size();
	}

	/**
	 * Gets the total number of columns, this includes empty columns.
	 * 
	 * @return the total number of columns
	 */
	public int getTotalColumnCount(){
		return columns.list().size();
	}

	/**
	 * Gets an iterator over the contents of the <code>index</code>'th non-empty
	 * column.
	 * 
	 * @param index
	 *            the index of the non-empty column
	 * @return the content of the non-empty column
	 */
	public Iterator<P> getColumnContent( final int index ){
		final PlaceholderList<D, S, P> list = getColumn( index );
		if( list == null ) {
			throw new IllegalArgumentException( "index is out of bounds" );
		}
		return new Iterator<P>(){
			private Iterator<P> delegate = list.dockables().iterator();
			private P current;
			private int currentIndex = -1;
			private boolean requiresPurge = false;
			
			@Override
			public boolean hasNext(){
				boolean result = delegate.hasNext();
				if( !result ){
					if( requiresPurge ){
						purge();
						requiresPurge = false;
					}
				}
				return result;
			}

			@Override
			public P next(){
				current = delegate.next();
				currentIndex++;
				return current;
			}

			@Override
			public void remove(){
				delegate.remove();
				onRemoved( list, index, current, currentIndex-- );
			}
		};
	}

	/**
	 * Gets the non-empty column with index <code>index</code>. Subclasses should not modify the returned list.
	 * 
	 * @param index
	 *            the index of the column
	 * @return the non-empty column or <code>null</code> if no such column
	 *         exists
	 */
	protected PlaceholderList<D, S, P> getColumn( int index ){
		if( index < 0 ) {
			return null;
		}
		final Filter<Column<D, S, P>> dockables = columns.dockables();
		if( index >= dockables.size() ) {
			return null;
		}

		return dockables.get( index ).getList();
	}
	
	/**
	 * Informs this grid that it is actually used and that it should be allowed
	 * to add observers to various resources.
	 */
	public void bind(){
		columns.bind();
		final Iterator<PlaceholderList<D, S, P>> columns = allColumns();
		while( columns.hasNext() ) {
			columns.next().bind();
		}

		if( !bound ) {
			bound = true;
			if( strategy != null ) {
				strategy.addListener( strategyListener );
				purge();
			}
		}
	}

	/**
	 * Informs this grid that it is no longer used and that is should remove any
	 * observers.
	 */
	public void unbind(){
		columns.unbind();
		final Iterator<PlaceholderList<D, S, P>> columns = allColumns();
		while( columns.hasNext() ) {
			columns.next().unbind();
		}

		if( bound ) {
			bound = false;
			if( strategy != null ) {
				strategy.removeListener( strategyListener );
			}
		}
	}

	/**
	 * Sets the {@link PlaceholderStrategy} which is to be used by this grid.
	 * 
	 * @param strategy
	 *            the new strategy, can be <code>null</code>
	 */
	public void setStrategy( PlaceholderStrategy strategy ){
		if( (this.strategy != null) && bound ) {
			this.strategy.removeListener( strategyListener );
		}
		this.strategy = strategy;
		columns.setStrategy( strategy );
		final Iterator<PlaceholderList<D, S, P>> columns = allColumns();
		while( columns.hasNext() ) {
			columns.next().setStrategy( strategy );
		}

		if( (this.strategy != null) && bound ) {
			this.strategy.addListener( strategyListener );
			purge();
		}
	}

	/**
	 * Gets the {@link PlaceholderStrategy} that is currently used by this grid.
	 * 
	 * @return the strategy, can be <code>null</code>
	 */
	public PlaceholderStrategy getStrategy(){
		return strategy;
	}


	/**
	 * Removes any dead element from {@link #columns}.
	 */
	private void purge(){
		purge( false );
	}
	
	/**
	 * Removes any dead element from {@link #columns}.
	 * @param silent if <code>true</code> then no events are fired when a column is removed
	 */
	private void purge( boolean silent ){
		int index = -1;
		for( final PlaceholderList<ColumnItem<D, S, P>, ColumnItem<D, S, P>, Column<D, S, P>>.Item item : columns.list() ) {
			final Column<D, S, P> column = item.getDockable();
			if( column != null ) {
				index++;
				PlaceholderList<D, S, P> list = column.getList();
				if( list.dockables().size() == 0 ) {
					item.setPlaceholderMap( list.toMap( new PlaceholderListItemAdapter<D, PlaceholderListItem<D>>(){
						@Override
						public ConvertedPlaceholderListItem convert( int index, PlaceholderListItem<D> dockable ){
							throw new IllegalStateException( "the list is supposed to have no children, so this conversion method must never be called" );
						}
					} ) );
					item.setDockable( null );
					if( !silent ){
						onRemoved( list, index-- );
					}
				}
			}
		}
	}

	/**
	 * Called by {@link #toMap(Map)}, this method should read persistent data
	 * from <code>dockable</code> and write that data into <code>item</code>.
	 * 
	 * @param dockable
	 *            the dockable to read
	 * @param item
	 *            the item to write into
	 */
	protected abstract void fill( D dockable, ConvertedPlaceholderListItem item );

	/**
	 * Converts this grid into a {@link PlaceholderMap} using
	 * <code>identifiers</code> to remember which {@link Dockable} was a which
	 * position.
	 * 
	 * @param identifiers
	 *            identifiers for all children of the {@link DockStation} using
	 *            this grid
	 * @return the map that persistently stores all data of this grid
	 */
	public PlaceholderMap toMap( final Map<D, Integer> identifiers ){
		columns.setConverter( new PlaceholderListItemAdapter<D, P>(){
			@Override
			public ConvertedPlaceholderListItem convert( int index, P dockable ){
				final Integer id = identifiers.get( dockable.asDockable() );
				if( id == null ) {
					return null;
				}
				final ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
				item.putInt( "index", index );
				item.putInt( "id", id.intValue() );
				fill( dockable.asDockable(), item );
				return item;
			}
		} );

		try {
			return columns.toMap( new PlaceholderListItemAdapter<ColumnItem<D, S, P>, Column<D, S, P>>(){
				@Override
				public ConvertedPlaceholderListItem convert( int index, Column<D, S, P> dockable ){
					final ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
					item.putInt( "index", index );
					item.setPlaceholderMap( dockable.getPlaceholders() );
					return item;
				}
			} );
		}
		finally {
			columns.setConverter( null );
		}
	}

	public void fromMap( PlaceholderMap map, final Map<Integer, D> identifiers, final PlaceholderToolbarGridConverter<D, P> converter ){
		clear();
		columns.setConverter( new PlaceholderListItemAdapter<D, P>(){
			@Override
			public P convert( ConvertedPlaceholderListItem item ){
				final Integer id = item.getInt( "id" );
				final D dockable = identifiers.get( id );
				if( dockable == null ) {
					return null;
				}
				return converter.convert( dockable, item );
			}
		} );

		try {
			columns.read( map, new PlaceholderListItemAdapter<ColumnItem<D, S, P>, Column<D, S, P>>(){
				@Override
				public Column<D, S, P> convert( ConvertedPlaceholderListItem item ){
					final PlaceholderList<D, S, P> list = createColumn();
					final PlaceholderMap map = item.getPlaceholderMap();
					if( map == null ) {
						return null;
					}

					list.read( map, columns.getConverter() );
					return columns.createColumn( list );
				}

				@Override
				public void added( Column<D, S, P> dockable ){
					for( final P item : dockable.getList().dockables() ) {
						converter.added( item );
					}
				};
			} );
			purge(true);
		}
		finally {
			columns.setConverter( null );
			onInserted();
		}
	}

	/**
	 * Converts this grid into a {@link PlaceholderMap}, if possible any
	 * {@link Dockable} is converted into a placeholder.
	 * 
	 * @return the converted map
	 */
	public PlaceholderMap toMap(){
		columns.setConverter( new PlaceholderListItemAdapter<D, P>(){
			@Override
			public ConvertedPlaceholderListItem convert( int index, P dockable ){
				final ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
				
				fill( dockable.asDockable(), item );
				
				if( item.getPlaceholder() == null && item.getPlaceholderMap() == null ) {
					return null;
				}
				return item;
			}
		} );

		try {
			return columns.toMap( new PlaceholderListItemAdapter<ColumnItem<D, S, P>, Column<D, S, P>>(){
				@Override
				public ConvertedPlaceholderListItem convert( int index, Column<D, S, P> dockable ){
					final ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
					item.putInt( "index", index );
					item.setPlaceholderMap( dockable.getPlaceholders() );
					return item;
				}
			} );
		}
		finally {
			columns.setConverter( null );
		}
	}

	/**
	 * Replaces the content of this grid by a map that was written earlier using
	 * {@link #toMap()} or {@link #toMap(Map)}.
	 * 
	 * @param map
	 *            the map to read, not <code>null</code>
	 */
	public void fromMap( PlaceholderMap map ){
		clear();
		columns.read( map, new PlaceholderListItemAdapter<ColumnItem<D, S, P>, Column<D, S, P>>(){
			@Override
			public Column<D, S, P> convert( ConvertedPlaceholderListItem item ){
				final PlaceholderMap map = item.getPlaceholderMap();
				if( map == null ) {
					return null;
				}

				final PlaceholderList<D, S, P> content = createColumn();
				content.read( map, columns.getConverter() );
				return columns.createColumn( content );
			}
		} );
		purge(true);
	}
}
