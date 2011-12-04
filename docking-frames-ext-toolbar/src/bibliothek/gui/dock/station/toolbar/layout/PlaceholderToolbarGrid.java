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
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderMap.Key;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.support.PlaceholderStrategyListener;
import bibliothek.gui.dock.station.toolbar.layout.GridPlaceholderList.Column;
import bibliothek.gui.dock.station.toolbar.layout.GridPlaceholderList.ColumnItem;
import bibliothek.util.Path;

/**
 * A {@link PlaceholderToolbarGrid} behaves like a list of {@link PlaceholderList}s. 
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

	/** this listener is added to {@link #strategy} if {@link #bound} is <code>true</code> */
	private PlaceholderStrategyListener strategyListener = new PlaceholderStrategyListener(){
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
	 * @return a new, empty list
	 */
	protected abstract PlaceholderList<D, S, P> createColumn();

	/**
	 * Creates a new {@link GridPlaceholderList}.
	 * @return the new, empty grid
	 */
	protected abstract GridPlaceholderList<D, S, P> createGrid();

	/**
	 * Gets all placeholders that are associated with <code>dockable</code>.
	 * @param dockable some element used by this grid
	 * @return the placeholders
	 */
	protected abstract Set<Path> getPlaceholders( D dockable );

	/**
	 * Removes all content from this grid.
	 */
	public void clear(){
		for( Column<D, S, P> column : columns.dockables() ) {
			column.getList().unbind();
			column.getList().setStrategy( null );
		}
		columns.clear();
	}

	/**
	 * Adds the item <code>item</code> to the non-empty column <code>column</code> into position
	 * <code>index</code>. This method may add a new column in order to store <code>item</code>.
	 * @param column the column in which to store <code>item</code>
	 * @param line the index within <code>column</code>
	 * @param item the item to store, not <code>null</code>
	 * @throws IllegalArgumentException if <code>item</code> is <code>null</code>
	 * @throws IllegalStateException if there is no {@link PlaceholderStrategy} set
	 */
	public void insert( int column, int line, P item ){
		if( item == null ) {
			throw new IllegalArgumentException( "item must not be null" );
		}

		PlaceholderList<D, S, P> list = getColumn( column );
		if( list == null ) {
			insert( column, item );
		}
		else {
			list.dockables().add( Math.min( line, list.dockables().size() ), item );
			ensureRemoved( list, item );
		}
	}

	/**
	 * Adds the item <code>item</code> to a new column, the new column will have the index
	 * <code>columnIndex</code>. If <code>columnIndex</code> is out of bounds, then the new column will
	 * be added as near as possible to the preferred position.
	 * @param columnIndex the column to add
	 * @param item the item to store, not <code>null</code>
	 */
	public void insert( int columnIndex, P item ){
		PlaceholderList<D, S, P> columnList = createColumn();
		columnList.dockables().add( item );

		Column<D, S, P> column = columns.createColumn( columnList );
		columns.dockables().add( Math.min( columnIndex, columns.dockables().size()), column );

		columnList.setStrategy( strategy );
		if( bound ) {
			columnList.setStrategy( strategy );
		}

		ensureRemoved( columnList, item );
	}

	/**
	 * Tries to put <code>item</code> into this list at location <code>placeholder</code>. If there
	 * is already an element at <code>placeholder</code>, then the old item is silenlty removed and
	 * the new item inserted. This method may create a new non-empty column if necessary.
	 * @param placeholder the name of the item
	 * @param item the item to insert
	 * @return <code>true</code> if insertion was a success, <code>false</code> otherwise
	 */
	public boolean put( Path placeholder, P item ){
		int listIndex = columns.getListIndex( placeholder );
		if( listIndex == -1 ) {
			return false;
		}
		PlaceholderList<?, ?, Column<D, S, P>>.Item listItem = columns.list().get( listIndex );

		Column<D, S, P> column = listItem.getDockable();
		if( column != null ) {
			return column.getList().put( placeholder, item ) != -1;
		}
		PlaceholderMap map = listItem.getPlaceholderMap();
		if( map != null ) {
			PlaceholderList<D, S, P> list = createColumn();
			list.read( map, columns.getConverter() );
			column = columns.createColumn( list );

			listItem.setDockable( column );

			if( column.getList().put( placeholder, item ) == -1 ) {
				listItem.setDockable( null );
				return false;
			}
			else {
				listItem.setPlaceholderMap( null );
				ensureRemoved( list, placeholder );
			}
			return true;
		}
		return false;
	}

	/**
	 * Stores the placeholder <code>placeholder</code> in the designated column.
	 * @param column the column in which to add <code>placeholder</code>
	 * @param line the line in which to add <code>placeholder</code>
	 * @param placeholder the placeholder to store
	 */
	public void insertPlaceholder( int column, int line, Path placeholder ){
		columns.dockables().addPlaceholder( column, placeholder );
		Column<D, S, P> item = columns.dockables().get( column );
		item.getList().dockables().addPlaceholder( line, placeholder );
		ensureRemoved( item.getList(), placeholder );
	}

	/**
	 * Removes <code>item</code> from this grid, but leaves a placeholder for the item.
	 * @param item the item to remove
	 */
	public void remove( P item ){
		for( Column<D, S, P> column : columns.dockables() ) {
			column.getList().remove( item );
		}
		purge();
	}

	private void ensureRemoved( PlaceholderList<D, S, P> ignore, P item ){
		Set<Path> placeholders = getPlaceholders( item.asDockable() );
		ensureRemoved( ignore, placeholders );
	}

	private void ensureRemoved( PlaceholderList<D, S, P> ignore, Path placeholder ){
		Set<Path> set = new HashSet<Path>();
		set.add( placeholder );
		ensureRemoved( ignore, set );
	}

	private void ensureRemoved( PlaceholderList<D, S, P> ignore, Set<Path> placeholders ){
		Iterator<PlaceholderList<ColumnItem<D, S, P>, ColumnItem<D, S, P>, Column<D, S, P>>.Item> iter = columns.list().iterator();
		while( iter.hasNext() ) {
			PlaceholderList<?, ?, Column<D, S, P>>.Item item = iter.next();
			if( item.getDockable().getList() != ignore ) {
				item.removeAll( placeholders );
				if( item.getPlaceholderSet() == null && item.isPlaceholder() ) {
					iter.remove();
				}
			}
		}

		for( Column<D, S, P> column : columns.dockables() ) {
			if( column.getList() != ignore ) {
				column.getList().removeAll( placeholders );
			}
		}

		purge();
	}

	/**
	 * Tells in which non-empty column <code>dockable</code> is.
	 * @param dockable the item to search
	 * @return the column of the dockable or <code>-1</code> if not found
	 */
	public int getColumn( D dockable ){
		int index = 0;
		Iterator<PlaceholderList<D, S, P>> columns = columns();
		while( columns.hasNext() ) {
			for( P item : columns.next().dockables() ) {
				if( item.asDockable() == dockable ) {
					return index;
				}
			}
			index++;
		}
		return -1;
	}

	/**
	 * Tells at which position <code>dockable</code> is within its column.
	 * @param dockable the item to search
	 * @return the location of <code>dockable</code>
	 */
	public int getLine( D dockable ){
		int column = getColumn( dockable );
		if( column == -1 ) {
			return -1;
		}
		return getLine( column, dockable );
	}

	/**
	 * Tells at which position <code>dockable</code> is within the column <code>column</code>
	 * @param column the index of the non-empty column to search
	 * @param dockable the item to search
	 * @return the location of <code>dockable</code>
	 */
	public int getLine( int column, D dockable ){
		PlaceholderList<D, S, P> list = getColumn( column );
		int index = 0;
		for( P item : list.dockables() ) {
			if( item.asDockable() == dockable ) {
				return index;
			}
			index++;
		}
		return -1;
	}

	/**
	 * Tells whether this {@link PlaceholderToolbarGrid} knows a column which contains
	 * the placeholder <code>placeholder</code>, this includes empty columns.
	 * @param placeholder the placeholder to search
	 * @return <code>true</code> if <code>placeholder</code> was found
	 */
	public boolean hasPlaceholder( Path placeholder ){
		int listIndex = columns.getListIndex( placeholder );
		if( listIndex == -1 ) {
			return false;
		}
		PlaceholderList<?, ?, Column<D, S, P>>.Item item = columns.list().get( listIndex );

		Column<D, S, P> column = item.getDockable();
		if( column != null ) {
			return column.getList().hasPlaceholder( placeholder );
		}
		PlaceholderMap map = item.getPlaceholderMap();
		if( map != null ) {
			for( Key key : map.getPlaceholders() ) {
				if( key.contains( placeholder ) ) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the total count of items stored in this grid.
	 * @return the total amount of items
	 */
	public int size(){
		int sum = 0;
		Iterator<PlaceholderList<D, S, P>> iter = columns();
		while( iter.hasNext() ) {
			sum += iter.next().dockables().size();
		}
		return sum;
	}

	/**
	 * Gets the <code>index</code>'th item of this grid.
	 * @param index the index of the item
	 * @return the item
	 * @throws IllegalArgumentException if <code>index</code> is not valid
	 */
	public P get( int index ){
		if( index < 0 ) {
			throw new IllegalArgumentException( "index must not be < 0" );
		}
		Iterator<PlaceholderList<D, S, P>> iter = columns();
		while( iter.hasNext() ) {
			Filter<P> dockables = iter.next().dockables();
			int size = dockables.size();
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
	 * @param dockable the dockable to search
	 * @return the item that represents <code>dockable</code> or <code>null</code> if not found
	 */
	public P get( D dockable ){
		Iterator<P> iter = items();
		while( iter.hasNext() ) {
			P next = iter.next();
			if( next.asDockable() == dockable ) {
				return next;
			}
		}
		return null;
	}

	/**
	 * Searches the item which is at the location of <code>placeholder</code>.
	 * @param placeholder some placeholder that may or may not be known to this grid
	 * @return the item at <code>placeholder</code> or <code>null</code> either if <code>placeholder</code>
	 * was not found or if there is no item stored
	 */
	public P get( Path placeholder ){
		int listIndex = columns.getListIndex( placeholder );
		if( listIndex == -1 ) {
			return null;
		}
		PlaceholderList<?, ?, Column<D, S, P>>.Item item = columns.list().get( listIndex );

		Column<D, S, P> column = item.getDockable();
		if( column == null ) {
			return null;
		}
		return column.getList().getDockableAt( placeholder );
	}

	/**
	 * Gets an iterator over all columns, including the columns with no content. This does not include columns 
	 * with no list (columns that consist only of placeholders).
	 * @return all columns
	 */
	protected Iterator<PlaceholderList<D, S, P>> allColumns(){
		return new Iterator<PlaceholderList<D, S, P>>(){
			private Iterator<GridPlaceholderList.Column<D, S, P>> items = columns.dockables().iterator();

			public boolean hasNext(){
				return items.hasNext();
			};

			@Override
			public PlaceholderList<D, S, P> next(){
				return items.next().getList();
			}

			@Override
			public void remove(){
				items.remove();
			}
		};
	}

	/**
	 * Gets an iterator over all non-empty columns. The iterator does not
	 * support modifications nor is it concurrent.
	 * @return the iterator
	 */
	protected Iterator<PlaceholderList<D, S, P>> columns(){
		return new Iterator<PlaceholderList<D, S, P>>(){
			private Iterator<GridPlaceholderList.Column<D, S, P>> items = columns.dockables().iterator();
			private PlaceholderList<D, S, P> next;

			private void forward(){
				next = null;
				while( next == null && items.hasNext() ) {
					PlaceholderList<D, S, P> column = items.next().getList();
					if( column.dockables().size() > 0 ) {
						next = column;
					}
				}
			}

			@Override
			public boolean hasNext(){
				if( next == null && items.hasNext() ) {
					forward();
				}
				return next != null;
			}

			@Override
			public PlaceholderList<D, S, P> next(){
				PlaceholderList<D, S, P> result = next;
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
	 * @return the iterator
	 */
	public Iterator<P> items(){
		return new Iterator<P>(){
			private Iterator<PlaceholderList<D, S, P>> columns = columns();
			private Iterator<P> items = null;

			private void validate(){
				while( (items == null || !items.hasNext()) && columns.hasNext() ) {
					items = columns.next().dockables().iterator();
				}
			}

			@Override
			public boolean hasNext(){
				validate();
				return items != null && items.hasNext();
			}

			@Override
			public P next(){
				validate();
				return items.next();
			}

			@Override
			public void remove(){
				if( items == null ) {
					throw new IllegalStateException( "no item selected" );
				}
				items.remove();
			}
		};
	}

	/**
	 * Gets the number of columns that are currently stored in this grid. Empty columns
	 * are excluded.
	 * @return the total number of non-empty columns
	 */
	public int getColumnCount(){
		return columns.dockables().size();
	}

	/**
	 * Gets an iterator over the contents of the <code>index</code>'th non-empty column.
	 * @param index the index of the non-empty column
	 * @return the content of the non-empty column
	 */
	public Iterator<P> getColumnContent( int index ){
		PlaceholderList<D, S, P> list = getColumn( index );
		if( list == null ) {
			throw new IllegalArgumentException( "index is out of bounds" );
		}
		return list.dockables().iterator();
	}

	/**
	 * Gets the non-empty column with index <code>index</code>.
	 * @param index the index of the column
	 * @return the non-empty column or <code>null</code> if no such column exists
	 */
	private PlaceholderList<D, S, P> getColumn( int index ){
		if( index < 0 ) {
			return null;
		}
		Filter<Column<D, S, P>> dockables = columns.dockables();
		if( index >= dockables.size() ) {
			return null;
		}

		return dockables.get( index ).getList();
	}

	/**
	 * Informs this grid that it is actually used and that it should is allowed to add observers
	 * to various resources. 
	 */
	public void bind(){
		columns.bind();
		Iterator<PlaceholderList<D, S, P>> columns = allColumns();
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
	 * Informs this grid that it is no longer used and that is should remove any observers.
	 */
	public void unbind(){
		columns.unbind();
		Iterator<PlaceholderList<D, S, P>> columns = allColumns();
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
	 * @param strategy the new strategy, can be <code>null</code>
	 */
	public void setStrategy( PlaceholderStrategy strategy ){
		if( this.strategy != null && bound ) {
			this.strategy.removeListener( strategyListener );
		}
		this.strategy = strategy;
		columns.setStrategy( strategy );
		Iterator<PlaceholderList<D, S, P>> columns = allColumns();
		while( columns.hasNext() ) {
			columns.next().setStrategy( strategy );
		}

		if( this.strategy != null && bound ) {
			this.strategy.addListener( strategyListener );
			purge();
		}
	}

	/**
	 * Gets the {@link PlaceholderStrategy} that is currently used by this grid.
	 * @return the strategy, can be <code>null</code>
	 */
	public PlaceholderStrategy getStrategy(){
		return strategy;
	}

	/**
	 * Removes any dead element from {@link #columns}.
	 */
	private void purge(){
		for( PlaceholderList<ColumnItem<D, S, P>, ColumnItem<D, S, P>, Column<D, S, P>>.Item item : columns.list() ) {
			Column<D, S, P> column = item.getDockable();
			if( column != null ) {
				if( column.getList().list().size() == 0 ) {
					item.setDockable( null );
				}
			}
		}
	}

	/**
	 * Called by {@link #toMap(Map)}, this method should read persistent data from <code>dockable</code> and
	 * write that data into <code>item</code>.
	 * @param dockable the dockable to read
	 * @param item the item to write into
	 */
	protected abstract void fill( D dockable, ConvertedPlaceholderListItem item );

	/**
	 * Converts this grid into a {@link PlaceholderMap} using <code>identifiers</code> to remember
	 * which {@link Dockable} was a which position.
	 * @param identifiers identifiers for all children of the {@link DockStation} using this grid
	 * @return the map that persistently stores all data of this grid
	 */
	public PlaceholderMap toMap( final Map<D, Integer> identifiers ){
		columns.setConverter( new PlaceholderListItemAdapter<D, P>(){
			public ConvertedPlaceholderListItem convert( int index, P dockable ){
				Integer id = identifiers.get( dockable.asDockable() );
				if( id == null ) {
					return null;
				}
				ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
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
					ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
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
		columns.setConverter( new PlaceholderListItemAdapter<D, P>(){
			public P convert( ConvertedPlaceholderListItem item ){
				Integer id = item.getInt( "id" );
				D dockable = identifiers.get( id );
				if( dockable == null ) {
					return null;
				}
				return converter.convert( dockable, item );
			}

			//public void added( P dockable ){
			// ignore
			//}
		} );

		try {
			columns.read( map, new PlaceholderListItemAdapter<GridPlaceholderList.ColumnItem<D, S, P>, GridPlaceholderList.Column<D, S, P>>(){
				public GridPlaceholderList.Column<D, S, P> convert( ConvertedPlaceholderListItem item ){
					PlaceholderList<D, S, P> list = createColumn();
					PlaceholderMap map = item.getPlaceholderMap();
					if( map == null ) {
						return null;
					}

					list.read( map, columns.getConverter() );
					return columns.createColumn( list );
				}

				public void added( GridPlaceholderList.Column<D, S, P> dockable ){
					for( P item : dockable.getList().dockables() ) {
						converter.added( item );
					}
				};
			} );
		}
		finally {
			columns.setConverter( null );
		}
	}

	/**
	 * Converts this grid into a {@link PlaceholderMap}, if possible any {@link Dockable} is converted into
	 * a placeholder.
	 * @return the converted map
	 */
	public PlaceholderMap toMap(){
		columns.setConverter( new PlaceholderListItemAdapter<D, P>(){
			public ConvertedPlaceholderListItem convert( int index, P dockable ){
				ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
				fill( dockable.asDockable(), item );
				if( item.getPlaceholder() == null ) {
					return null;
				}
				return item;
			}
		} );

		try {
			return columns.toMap( new PlaceholderListItemAdapter<ColumnItem<D, S, P>, Column<D, S, P>>(){
				@Override
				public ConvertedPlaceholderListItem convert( int index, Column<D, S, P> dockable ){
					ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
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
	 * Replaces the content of this grid by a map that was written earlier using {@link #toMap()}
	 * or {@link #toMap(Map)}.
	 * @param map the map to read, not <code>null</code>
	 */
	public void fromMap( PlaceholderMap map ){
		columns.clear();
		columns.read( map, new PlaceholderListItemAdapter<ColumnItem<D, S, P>, Column<D, S, P>>(){
			@Override
			public Column<D, S, P> convert( ConvertedPlaceholderListItem item ){
				PlaceholderMap map = item.getPlaceholderMap();
				if( map == null ) {
					return null;
				}

				PlaceholderList<D, S, P> content = createColumn();
				content.read( map, columns.getConverter() );
				return columns.createColumn( content );
			}
		} );
	}
}
