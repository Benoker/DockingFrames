package bibliothek.gui.dock.station.toolbar.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumn;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumnListener;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumnModel;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumnModelListener;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.DockUtilities.DockVisitor;
import bibliothek.util.Path;

/**
 * An implementation of {@link PlaceholderToolbarGrid} that uses {@link Dockable}s and {@link DockStation}s.
 * 
 * @author Benjamin Sigg
 * @param <P> the kind of object that represents a {@link Dockable}
 */
public class DockablePlaceholderToolbarGrid<P extends PlaceholderListItem<Dockable>> extends PlaceholderToolbarGrid<Dockable, DockStation, P> {
	/** a facade to this grid, allowing easy access to all columns */
	private Model model = new Model();

	/**
	 * Creates and initializes a new grid
	 */
	public DockablePlaceholderToolbarGrid(){
		init();
	}

	/**
	 * Gets access to a simplified view of this grid.
	 * @return the simplified view, never <code>null</code>
	 */
	public Model getModel(){
		return model;
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
	}

	@Override
	protected void onInserted( PlaceholderList<Dockable, DockStation, P> column, int columnIndex, P item, int itemIndex ){
		model.getColumn( columnIndex ).onInserted( item, itemIndex );
	}

	@Override
	protected void onRemoved( PlaceholderList<Dockable, DockStation, P> column, int columnIndex, P item, int itemIndex ){
		model.getColumn( columnIndex ).onRemoved( item, itemIndex );
	}

	@Override
	protected void onInserted( PlaceholderList<Dockable, DockStation, P> column, int index ){
		model.onInserted( index );
	}

	@Override
	protected void onRemoved( PlaceholderList<Dockable, DockStation, P> column, int index ){
		for( int i = column.dockables().size() - 1; i >= 0; i-- ) {
			model.getColumn( index ).onRemoved( column.dockables().get( i ), i );
		}
		model.onRemoved( index );
	}

	@Override
	protected void onInserted(){
		for( int i = 0, n = getColumnCount(); i < n; i++ ) {
			model.onInserted( i );

			int index = 0;
			Iterator<P> content = getColumnContent( i );
			while( content.hasNext() ) {
				model.getColumn( i ).onInserted( content.next(), index++ );
			}
		}
	}

	@Override
	protected void onRemoved(){
		for( int i = model.getColumnCount() - 1; i >= 0; i++ ) {
			PlaceholderList<?, ?, P> column = getColumn( i );

			for( int j = column.dockables().size() - 1; j >= 0; j-- ) {
				model.getColumn( i ).onRemoved( column.dockables().get( j ), j );
			}

			model.onRemoved( i );
		}
	}

	/**
	 * A facade simplifying access to this {@link DockablePlaceholderToolbarGrid}
	 * @author Benjamin Sigg
	 */
	private class Model implements ToolbarColumnModel<P> {
		/** all the observers of this {@link ToolbarColumnModel} */
		private List<ToolbarColumnModelListener<P>> modelListeners = new ArrayList<ToolbarColumnModelListener<P>>();

		/** all the columns that are currently shown in this grid */
		private List<Column> columns = new ArrayList<Column>();

		@Override
		public void addListener( ToolbarColumnModelListener<P> listener ){
			if( listener == null ) {
				throw new IllegalArgumentException( "listener must not be null" );
			}
			modelListeners.add( listener );
		}

		@Override
		public void removeListener( ToolbarColumnModelListener<P> listener ){
			modelListeners.remove( listener );
		}

		/**
		 * Gets all the {@link ToolbarColumnModelListener} that are currently registered at this model.
		 * @return all the listeners
		 */
		@SuppressWarnings("unchecked")
		protected ToolbarColumnModelListener<P>[] listeners(){
			return modelListeners.toArray( new ToolbarColumnModelListener[modelListeners.size()] );
		}

		@Override
		public Column getColumn( int index ){
			return columns.get( index );
		}

		@Override
		public int getColumnCount(){
			return columns.size();
		}

		public void onInserted( int index ){
			Column column = new Column();
			columns.add( index, column );
			for( ToolbarColumnModelListener<P> listener : listeners() ) {
				listener.inserted( this, column, index );
			}
		}

		public void onRemoved( int index ){
			Column column = columns.remove( index );
			for( ToolbarColumnModelListener<P> listener : listeners() ) {
				listener.removed( this, column, index );
			}
		}
	}

	/**
	 * A facade simplifying access to the visible columns of this {@link DockablePlaceholderToolbarGrid}.
	 * @author Benjamin Sigg
	 */
	private class Column implements ToolbarColumn<P> {
		private List<P> items = new ArrayList<P>();
		private List<ToolbarColumnListener<P>> listeners = new ArrayList<ToolbarColumnListener<P>>();

		public void onInserted( P item, int index ){
			items.add( index, item );
			for( ToolbarColumnListener<P> listener : listeners() ) {
				listener.inserted( this, item.asDockable(), index );
			}
		}

		public void onRemoved( P item, int index ){
			items.remove( index );
			for( ToolbarColumnListener<P> listener : listeners() ) {
				listener.removed( this, item.asDockable(), index );
			}
		}

		@Override
		public int getColumnIndex(){
			return model.columns.indexOf( this );
		}

		@Override
		public int getDockableCount(){
			return items.size();
		}

		@Override
		public Dockable getDockable( int index ){
			return items.get( index ).asDockable();
		}
		
		@Override
		public P getItem( int index ){
			return items.get( index );
		}

		@SuppressWarnings("unchecked")
		private ToolbarColumnListener<P>[] listeners(){
			return listeners.toArray( new ToolbarColumnListener[listeners.size()] );
		}

		@Override
		public void addListener( ToolbarColumnListener<P> listener ){
			if( listener == null ) {
				throw new IllegalArgumentException( "listener must not be null" );
			}
			listeners.add( listener );
		}

		@Override
		public void removeListener( ToolbarColumnListener<P> listener ){
			listeners.remove( listener );
		}
	}
}
