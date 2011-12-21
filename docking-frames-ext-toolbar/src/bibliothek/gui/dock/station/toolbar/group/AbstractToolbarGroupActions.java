package bibliothek.gui.dock.station.toolbar.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.action.DockActionSource;

/**
 * A class that can create one {@link DockActionSource} for each {@link ToolbarColumn} a {@link ToolbarGroupDockStation} 
 * has. This class is built such that sublcasses can easily access or modify the {@link DockActionSource}s. 
 * 
 * @author Benjamin Sigg
 */
public abstract class AbstractToolbarGroupActions<C extends AbstractToolbarGroupActions<C>.Column> {
	/**
	 * The model that is currently observed 
	 */
	private ToolbarColumnModel model; 
	
	/**
	 * All the columns that are currently used
	 */
	private List<C> columns = new ArrayList<C>();
	
	/**
	 * This listener is added to the current {@link #model}
	 */
	private ToolbarColumnModelListener listener = new ToolbarColumnModelListener(){
		@Override
		public void removed( ToolbarColumnModel model, ToolbarColumn column, int index ){
			columns.remove( index ).destroy();
		}
		
		@Override
		public void inserted( ToolbarColumnModel model, ToolbarColumn column, int index ){
			C col = createColumn( column );
			columns.add( index, col );
			
			for( int i = 0, n = column.getDockableCount(); i<n; i++ ){
				col.inserted( i, column.getDockable( i ) );
			}
		}
	};
	
	/**
	 * Sets the model which should be observed by this {@link AbstractToolbarGroupActions}, all existing columns
	 * will be removed by this method.
	 * @param model the new model or <code>null</code>
	 */
	public void setModel( ToolbarColumnModel model ){
		if( this.model != model ){
			if( this.model != null ){
				this.model.removeListener( listener );
				for( Column column : columns ){
					column.destroy();
				}
				columns.clear();
			}
			
			this.model = model;
			
			if( model != null ){
				model.addListener( listener );
				for( int i = 0, n = model.getColumnCount(); i<n; i++ ){
					listener.inserted( model, model.getColumn( i ), i );
				}
			}
		}
	}
	
	/**
	 * Gets the model which is currently observed by this object
	 * @return the mode, can be <code>null</code>
	 */
	public ToolbarColumnModel getModel(){
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
	 * Creates a new, empty {@link Column} which will be filled with content later.
	 * @param column the column that is represented by the new object
	 * @return the new column, must not be <code>null</code>
	 */
	protected abstract C createColumn( ToolbarColumn column );
	
	/**
	 * Represents one column of the {@link ToolbarGroupDockStation}.
	 * @author Benjamin Sigg
	 */
	protected abstract class Column{
		private ToolbarColumn column;
		private boolean created = false;
		private DockActionSource source;
		private List<Dockable> items = new ArrayList<Dockable>();
		
		private ToolbarColumnListener listener = new ToolbarColumnListener(){
			@Override
			public void removed( ToolbarColumn column, Dockable item, int index ){
				items.remove( index );
				Column.this.removed( index, item );
			}
			
			@Override
			public void inserted( ToolbarColumn column, Dockable item, int index ){
				items.add( index, item );
				Column.this.inserted( index, item );
			}
		};
		
		/**
		 * Creates a new column.
		 * @param column the column that is represented by this object, not <code>null</code>
		 */
		public Column( ToolbarColumn column ){
			this.column = column;
			this.column.addListener( listener );
		}
		
		/**
		 * Gets the {@link DockActionSource} which is associated with this column.
		 * @return the source, can be <code>null</code>
		 */
		public DockActionSource getSource(){
			if( !created ){
				created = true;
				source = createSource();
			}
			return source;
		}
		
		/**
		 * Gets all the items of this column.
 		 * @return all the items, the list is not modifiable
		 */
		public List<Dockable> getDockables(){
			return Collections.unmodifiableList( items );
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
		 * @param dockable the item that was added
		 */
		protected abstract void inserted( int index, Dockable dockable );
		
		/**
		 * Called after an item was removed from this column
		 * @param index the index of the removed item
		 * @param dockable the item that was removed
		 */
		protected abstract void removed( int index, Dockable dockable );
		
		private void destroy(){
			column.removeListener( listener );
			removed();
		}
		
		/**
		 * Called if this column is no longer used, all resources should be released
		 */
		protected abstract void removed();
	}
}
