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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumn;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumnListener;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumnModel;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumnModelListener;

/**
 * A {@link PlaceholderToolbarGrid} which offers a {@link ToolbarColumnModel} to access the dockables.
 * @author Benjamin Sigg
 * @param <D> the kind of object that should be treated as {@link Dockable}
 * @param <S> the kind of object that should be treated as {@link DockStation}
 * @param <P> the type of item which represents a {@link Dockable}
 */
public abstract class ModeledPlaceholderToolbarGrid <D, S, P extends PlaceholderListItem<D>> extends PlaceholderToolbarGrid<D, S, P>{
	/** a facade to this grid, allowing easy access to all columns */
	private Model model = new Model();

	/**
	 * Gets access to a simplified view of this grid.
	 * @return the simplified view, never <code>null</code>
	 */
	public ToolbarColumnModel<D,P> getModel(){
		return model;
	}
	
	@Override
	protected void onInserted( PlaceholderList<D, S, P> column, int columnIndex, P item, int itemIndex ){
		model.getColumn( columnIndex ).onInserted( item, itemIndex );
	}

	@Override
	protected void onRemoved( PlaceholderList<D, S, P> column, int columnIndex, P item, int itemIndex ){
		model.getColumn( columnIndex ).onRemoved( item, itemIndex );
	}

	@Override
	protected void onInserted( PlaceholderList<D, S, P> column, int index ){
		model.onInserted( index );
	}

	@Override
	protected void onRemoved( PlaceholderList<D, S, P> column, int index ){
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
			Column column = model.getColumn( i );

			for( int j = column.getDockableCount() - 1; j >= 0; j-- ) {
				model.getColumn( i ).onRemoved( column.getItem( j ), j );
			}

			model.onRemoved( i );
		}
	}

	/**
	 * A facade simplifying access to this {@link DockablePlaceholderToolbarGrid}
	 * @author Benjamin Sigg
	 */
	private class Model implements ToolbarColumnModel<D,P> {
		/** all the observers of this {@link ToolbarColumnModel} */
		private List<ToolbarColumnModelListener<D,P>> modelListeners = new ArrayList<ToolbarColumnModelListener<D,P>>();

		/** all the columns that are currently shown in this grid */
		private List<Column> columns = new ArrayList<Column>();

		@Override
		public void addListener( ToolbarColumnModelListener<D,P> listener ){
			if( listener == null ) {
				throw new IllegalArgumentException( "listener must not be null" );
			}
			modelListeners.add( listener );
		}

		@Override
		public void removeListener( ToolbarColumnModelListener<D,P> listener ){
			modelListeners.remove( listener );
		}

		/**
		 * Gets all the {@link ToolbarColumnModelListener} that are currently registered at this model.
		 * @return all the listeners
		 */
		@SuppressWarnings("unchecked")
		protected ToolbarColumnModelListener<D,P>[] listeners(){
			return modelListeners.toArray( new ToolbarColumnModelListener[modelListeners.size()] );
		}

		@Override
		public Column getColumn( int index ){
			return columns.get( index );
		}
		
		@Override
		public Column getColumn( D dockable ){
			for( Column column : columns ){
				if( column.contains( dockable )){
					return column;
				}
			}
			return null;
		}

		@Override
		public int getColumnCount(){
			return columns.size();
		}

		public void onInserted( int index ){
			Column column = new Column();
			columns.add( index, column );
			for( ToolbarColumnModelListener<D,P> listener : listeners() ) {
				listener.inserted( this, column, index );
			}
		}

		public void onRemoved( int index ){
			Column column = columns.remove( index );
			for( ToolbarColumnModelListener<D,P> listener : listeners() ) {
				listener.removed( this, column, index );
			}
		}
	}

	/**
	 * A facade simplifying access to the visible columns of this {@link DockablePlaceholderToolbarGrid}.
	 * @author Benjamin Sigg
	 */
	private class Column implements ToolbarColumn<D,P> {
		private List<P> items = new ArrayList<P>();
		private List<ToolbarColumnListener<D,P>> listeners = new ArrayList<ToolbarColumnListener<D,P>>();

		public void onInserted( P item, int index ){
			items.add( index, item );
			for( ToolbarColumnListener<D,P> listener : listeners() ) {
				listener.inserted( this, item, item.asDockable(), index );
			}
		}

		public void onRemoved( P item, int index ){
			items.remove( index );
			for( ToolbarColumnListener<D,P> listener : listeners() ) {
				listener.removed( this, item, item.asDockable(), index );
			}
		}
		
		/**
		 * Tells whether this column contains <code>dockable</code>.
		 * @param dockable the item to search
		 * @return <code>true</code> if <code>dockable</code> was found
		 */
		public boolean contains( D dockable ){
			for( P item : items ){
				if( item.asDockable() == dockable ){
					return true;
				}
			}
			return false;
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
		public D getDockable( int index ){
			return items.get( index ).asDockable();
		}
		
		@Override
		public int indexOf( Dockable dockable ){
			for( int i = 0, n = getDockableCount(); i<n; i++ ){
				if( getDockable( i ) == dockable ){
					return i;
				}
			}
			return -1;
		}
		
		@Override
		public P getItem( int index ){
			return items.get( index );
		}

		@SuppressWarnings("unchecked")
		private ToolbarColumnListener<D,P>[] listeners(){
			return listeners.toArray( new ToolbarColumnListener[listeners.size()] );
		}

		@Override
		public void addListener( ToolbarColumnListener<D,P> listener ){
			if( listener == null ) {
				throw new IllegalArgumentException( "listener must not be null" );
			}
			listeners.add( listener );
		}

		@Override
		public void removeListener( ToolbarColumnListener<D,P> listener ){
			listeners.remove( listener );
		}
	}
}
