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

package bibliothek.gui.dock.station.toolbar.group;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.station.toolbar.title.ColumnDockActionSource;
import bibliothek.gui.dock.station.toolbar.title.ColumnDockActionSourceListener;

/**
 * A class that can create one {@link DockActionSource} for each {@link ToolbarColumn} a {@link ToolbarGroupDockStation} 
 * has. This class is built such that subclasses can easily access or modify the {@link DockActionSource}s.
 * 
 * @author Benjamin Sigg
 * @param <P> the type of object that represents a {@link Dockable}
 * @param <C> the type of this subclass
 */
public abstract class AbstractToolbarGroupActions<P, C extends AbstractToolbarGroupActions<P, C>.Column> implements ColumnDockActionSource {
	/**
	 * The model that is currently observed 
	 */
	private ToolbarColumnModel<Dockable,P> model;

	/**
	 * All the columns that are currently used
	 */
	private List<C> columns = new ArrayList<C>();
	
	/** all the listeners that were added to this {@link ColumnDockActionSource} */
	private List<ColumnDockActionSourceListener> listeners = new ArrayList<ColumnDockActionSourceListener>();

	/** the station using this object */
	private ToolbarGroupDockStation station;
	
	/**
	 * This listener is added to the current {@link #model}
	 */
	private ToolbarColumnModelListener<Dockable,P> modelListener = new ToolbarColumnModelListener<Dockable,P>(){
		@Override
		public void removed( ToolbarColumnModel<Dockable,P> model, ToolbarColumn<Dockable,P> column, int index ){
			Column col = columns.remove( index );
			for( ColumnDockActionSourceListener listener : listeners() ){
				listener.removed( AbstractToolbarGroupActions.this, col.getSource(), index );
			}
			col.destroy();
		}

		@Override
		public void inserted( ToolbarColumnModel<Dockable,P> model, ToolbarColumn<Dockable,P> column, int index ){
			C col = createColumn( column );
			columns.add( index, col );

			for( ColumnDockActionSourceListener listener : listeners() ){
				listener.inserted( AbstractToolbarGroupActions.this, col.getSource(), index );
			}
			
			for( int i = 0, n = column.getDockableCount(); i < n; i++ ) {
				col.inserted( i, column.getItem( i ) );
			}
		}
	};

	/**
	 * This listener is added to {@link Component}s on which the boundaries of the columns depend.
	 */
	private ComponentListener componentListener = new ComponentListener(){
		@Override
		public void componentShown( ComponentEvent e ){
			// ignore	
		}
		
		@Override
		public void componentHidden( ComponentEvent e ){
			// ignore
		}

		@Override
		public void componentResized( ComponentEvent e ){
			for( ColumnDockActionSourceListener listener : listeners() ){
				listener.reshaped( AbstractToolbarGroupActions.this );
			}
		}
		
		@Override
		public void componentMoved( ComponentEvent e ){
			for( ColumnDockActionSourceListener listener : listeners() ){
				listener.reshaped( AbstractToolbarGroupActions.this );
			}
		}
	};

	/**
	 * Creates a new object
	 * @param station the station which uses this set of actions
	 */
	public AbstractToolbarGroupActions( ToolbarGroupDockStation station ){
		this.station = station;
	}
	
	@Override
	public void addListener( ColumnDockActionSourceListener listener ){
		if( listener == null ){
			throw new IllegalArgumentException( "listener must not be null" );
		}
		listeners.add( listener );
	}
	
	@Override
	public void removeListener( ColumnDockActionSourceListener listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Gets all the listeners that are currently registered.
	 * @return all the listeners
	 */
	protected ColumnDockActionSourceListener[] listeners(){
		return listeners.toArray( new ColumnDockActionSourceListener[ listeners.size() ] );
	}
	
	/**
	 * Sets the model which should be observed by this {@link AbstractToolbarGroupActions}, all existing columns
	 * will be removed by this method.
	 * @param model the new model or <code>null</code>
	 */
	public void setModel( ToolbarColumnModel<Dockable,P> model ){
		if( this.model != model ) {
			if( this.model != null ) {
				this.model.removeListener( modelListener );
				for( int i = columns.size()-1; i >= 0; i-- ){
					Column column = columns.get( i );
					modelListener.removed( model, column.getColumn(), i );
				}
			}

			this.model = model;

			if( model != null ) {
				model.addListener( modelListener );
				for( int i = 0, n = model.getColumnCount(); i < n; i++ ) {
					modelListener.inserted( model, model.getColumn( i ), i );
				}
			}
		}
	}

	/**
	 * Gets the model which is currently observed by this object
	 * @return the mode, can be <code>null</code>
	 */
	public ToolbarColumnModel<Dockable,P> getModel(){
		return model;
	}

	/**
	 * Gets all the column that are currently known to this object.
	 * @return all the columns, the list is not modifiable
	 */
	protected List<C> getColumns(){
		return Collections.unmodifiableList( columns );
	}
	
	/**
	 * Gets the current number of columns.
	 * @return the number of columns
	 */
	public int getColumnCount(){
		return columns.size();
	}
	
	@Override
	public int getSourceCount(){
		return getColumnCount();
	}
	
	@Override
	public DockActionSource getSource( int index ){
		return getColumn( index ).getSource();
	}
	
	@Override
	public Orientation getOrientation(){
		return station.getOrientation();
	}
	
	/**
	 * Gets the <code>index</code>'th column.
	 * @param index the location of the column
	 * @return the column, never <code>null</code>
	 */
	public C getColumn( int index ){
		return columns.get( index );
	}
	
	/**
	 * Searches the column which contains <code>dockable</code>.
	 * @param dockable the element to search
	 * @return the column with <code>dockable</code> or <code>null</code> if not found
	 */
	protected C getColumn( Dockable dockable ){
		for( C column : columns ){
			if( column.getDockables().contains( dockable )){
				return column;
			}
		}
		return null;
	}

	@Override
	public int getSourceOffset( int index ){
		Rectangle boundaries = getColumn( index ).getColumnBoundaries();
		if( boundaries == null ){
			return -1;
		}
		if( getOrientation() == Orientation.VERTICAL ){
			return boundaries.x;
		}
		else{
			return boundaries.y;
		}
	}

	@Override
	public int getSourceLength( int index ){
		Rectangle boundaries = getColumn( index ).getColumnBoundaries();
		if( boundaries == null ){
			return 0;
		}
		if( getOrientation() == Orientation.VERTICAL ){
			return boundaries.width;
		}
		else{
			return boundaries.height;
		}
	}
	
	/**
	 * Creates a new, empty {@link Column} which will be filled with content later.
	 * @param column the column that is represented by the new object
	 * @return the new column, must not be <code>null</code>
	 */
	protected abstract C createColumn( ToolbarColumn<Dockable,P> column );
	
	/**
	 * Gets the bounds of the {@link Component} <code>item</code>.
	 * @param item the item whose boundaries are required
	 * @return the boundaries
	 */
	protected abstract Rectangle getBoundaries( P item );
	
	/**
	 * Installs the {@link ComponentListener} <code>listener</code> such that changes on <code>item</code> that lead to
	 * columns shifting position or size are recognized.
	 * @param item the item which gets a new listener
	 * @param listener the new listener
	 */
	protected abstract void installListener( P item, ComponentListener listener );
	
	/**
	 * Removes the listener <code>listener</code> from <code>item</code>.
	 * @param item the item where the listener is to be removed
	 * @param listener the listener to remove
	 */
	protected abstract void uninstallListener( P item, ComponentListener listener );
	
	/**
	 * Represents one column of the {@link ToolbarGroupDockStation}.
	 * @author Benjamin Sigg
	 */
	protected abstract class Column {
		private ToolbarColumn<Dockable,P> column;
		private boolean created = false;
		private DockActionSource source;
		
		private List<P> items = new ArrayList<P>();
		private List<Dockable> dockables = new ArrayList<Dockable>();

		private ToolbarColumnListener<Dockable,P> listener = new ToolbarColumnListener<Dockable,P>(){
			@Override
			public void removed( ToolbarColumn<Dockable,P> column, P item, Dockable dockable, int index ){
				items.remove( index );
				dockables.remove( index );
				uninstallListener( item, componentListener );
				Column.this.removed( index, item );
			}

			@Override
			public void inserted( ToolbarColumn<Dockable,P> column, P item, Dockable dockable, int index ){
				items.add( index, item );
				dockables.add( index, dockable );
				installListener( item, componentListener );
				Column.this.inserted( index, item );
			}
		};

		/**
		 * Creates a new column.
		 * @param column the column that is represented by this object. Subclasses can
		 * set this argument to <code>null</code>, but have to call {@link #init(ToolbarColumn)} later.
		 */
		public Column( ToolbarColumn<Dockable,P> column ){
			if( column != null ){
				init( column );
			}
		}
		
		/**
		 * Initializes all fields of this object
		 * @param column the column that is represented by this object, not <code>null</code>
		 */
		protected void init( ToolbarColumn<Dockable,P> column ){
			this.column = column;
			this.column.addListener( listener );
			for( int i = 0, n = column.getDockableCount(); i<n; i++ ){
				listener.inserted( column, column.getItem( i ), column.getDockable( i ), i );
			}
		}

		/**
		 * Gets the {@link DockActionSource} which is associated with this column.
		 * @return the source, can be <code>null</code>
		 */
		public DockActionSource getSource(){
			if( !created ) {
				created = true;
				source = createSource();
			}
			return source;
		}

		/**
		 * Gets all the items of this column.
		 * @return all the items, the list is not modifiable
		 */
		public List<P> getItems(){
			return Collections.unmodifiableList( items );
		}
		
		/**
		 * Gets all the items of this column.
		 * @return all the items, the list is not modifiable
		 */
		public List<Dockable> getDockables(){
			return Collections.unmodifiableList( dockables );
		}

		/**
		 * Gets the column which is represented by this object
		 * @return the underlying data structure
		 */
		public ToolbarColumn<Dockable,P> getColumn(){
			return column;
		}
		
		
		/**
		 * Gets the current boundaries of this column.
		 * @return the boundaries in respect to the {@link ToolbarGroupDockStation}, <code>null</code> if there
		 * are no items in this column
		 */
		public Rectangle getColumnBoundaries(){
			Rectangle result = null;
			
			for( P item : getItems() ){
				Rectangle next = getBoundaries( item );
				if( result == null ){
					result = new Rectangle( next );
				}
				else{
					result = result.union( next );
				}
			}
			
			return result;
		}
		
		/**
		 * Creates a {@link DockActionSource} that will be stored in this {@link Column}. This method is called
		 * lazily, the first time when {@link #getSource()} is executed. 
		 * @return the new source or <code>null</code>
		 */
		protected abstract DockActionSource createSource();

		/**
		 * Called after an item was added to this column
		 * @param index the index of the new item
		 * @param item the item that was added
		 */
		protected abstract void inserted( int index, P item );

		/**
		 * Called after an item was removed from this column
		 * @param index the index of the removed item
		 * @param item the item that was removed
		 */
		protected abstract void removed( int index, P item );

		private void destroy(){
			column.removeListener( listener );
			for( P item : getItems() ){
				uninstallListener( item, componentListener );
			}
			removed();
		}

		/**
		 * Called if this column is no longer used, all resources should be released
		 */
		protected abstract void removed();
	}
}
