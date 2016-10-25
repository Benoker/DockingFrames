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
package bibliothek.gui.dock.toolbar.perspective;

import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.station.toolbar.ToolbarDockPerspective;
import bibliothek.gui.dock.station.toolbar.ToolbarGroupDockPerspective;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumn;

/**
 * A wrapper around a {@link ToolbarGroupDockPerspective}, offers features that are
 * useful in the Common project only.<br>
 * A group of toolbars is organized similar to a table: there are columns of toolbars, and each
 * column has its own size.<br>
 * <b>Important note:</b> "columns" is an abstract idea, they do not exist as {@link DockStation}s. The {@link CToolbarGroupPerspective}
 * tries to keep its abstract representation of columns valid, if however two {@link CToolbarGroupPerspective}s point
 * to the same {@link ToolbarGroupDockPerspective} one perspective can modify the underlying data structure without
 * the other perspective noticing the changes immediately.
 * @author Benjamin Sigg
 */
public class CToolbarGroupPerspective {
	private ToolbarGroupDockPerspective delegate;

	/** all the columns that are currently alive */
	private Map<ToolbarColumn<PerspectiveDockable, PerspectiveDockable>, Column> columns = new HashMap<ToolbarColumn<PerspectiveDockable, PerspectiveDockable>, CToolbarGroupPerspective.Column>();

	/**
	 * Creates a new perspective wrapping around <code>delegate</code>.
	 * @param delegate the internal representation, not <code>null</code>
	 */
	public CToolbarGroupPerspective( ToolbarGroupDockPerspective delegate ){
		if( delegate == null ) {
			throw new IllegalArgumentException( "delegate must not be null" );
		}
		this.delegate = delegate;
	}

	@Override
	public boolean equals( Object obj ){
		if( obj.getClass() == getClass() ) {
			return ((CToolbarGroupPerspective) obj).delegate == delegate;
		}
		return false;
	}

	/**
	 * Allows access to the internal representation of this perspective.
	 * @return the internal representation, not <code>null</code>
	 */
	public ToolbarGroupDockPerspective getDelegate(){
		return delegate;
	}

	/**
	 * Gets the total number of toolbars in this group. This method assumes that the
	 * client did not modify the {@link ToolbarStrategy}.
	 * @return the total number of toolbars
	 */
	public int getToolbarCount(){
		return delegate.getDockableCount();
	}

	/**
	 * Gets the toolbar at location <code>index</code>.
	 * @param index the location of the toolbar
	 * @return the toolbar or <code>null</code> if the child at <code>index</code> has the wrong
	 * type. A result of <code>null</code> is only to be expected if the client modified the
	 * {@link ToolbarStrategy}.
	 */
	public CToolbarPerspective getToolbar( int index ){
		PerspectiveElement child = delegate.getDockable( index );
		if( child instanceof ToolbarDockPerspective ) {
			return new CToolbarPerspective( (ToolbarDockPerspective) child );
		}
		else {
			return null;
		}
	}

	/**
	 * Gets the total number of columns.
	 * @return the number of columns
	 */
	public int getColumnCount(){
		return delegate.getColumnCount();
	}

	/**
	 * Gets or creates the {@link Column} at <code>index</code>. Note that a {@link Column} requires
	 * at least one child, a new {@link Column} will not appear until one child has been added. 
	 * @param index the index of an existing column, <code>-1</code> or {@link #getColumnCount()}
	 * @return the column, note that this method may create a new {@link Column} object every time
	 * it is called
	 * @see #insert(int)
	 * @see Column
	 */
	public Column column( int index ){
		if( index < 0 ) {
			return insert( 0 );
		}
		else if( index >= getColumnCount() ) {
			return insert( getColumnCount() );
		}
		ToolbarColumn<PerspectiveDockable, PerspectiveDockable> column = delegate.getModel().getColumn( index );
		Column result = columns.get( column );
		if( result == null ) {
			result = new Column( index, column );
		}
		result.validate();
		return result;
	}

	/**
	 * Creates a new, empty column. Only when the client adds a child to the {@link Column} the
	 * column will insert itself at <code>index</code>. Creating a list of empty columns, and adding
	 * children afterwards, will actually change the order of the columns to the order in which
	 * the children were added. 
	 * @param index the location where to insert the new column
	 * @return the new empty column
	 * @see Column
	 */
	public Column insert( int index ){
		return new Column( index );
	}
	
	/**
	 * Gets or creates a toolbar in column <code>column</code> at location <code>index</code>.
	 * @param column the column in which to search or add the toolbar
	 * @param index the location of the toolbar
	 * @return the toolbar or <code>null</code> if a child of this station has the wrong type
	 */
	public CToolbarPerspective toolbar( int column, int index ){
		return column( column ).toolbar( index );
	}
	
	private void validateAll(){
		for( Column column : columns.values().toArray( new Column[ columns.size() ] )){
			column.validate();
		}
	}

	/**
	 * Represents a single column of toolbars in a group of toolbars. A column is a concept of the
	 * user interface, as a result a column must contain at least one child. If a column does not
	 * have children, it is not part of the group. It will however store its last index and can
	 * insert itself at that last index again if new children are added. If several columns have
	 * no children, then their order can change depending on the order in which new children are
	 * added.
	 * @author Benjamin Sigg
	 */
	public class Column {
		private int index;
		private ToolbarColumn<PerspectiveDockable, PerspectiveDockable> column;

		private Column( int index, ToolbarColumn<PerspectiveDockable, PerspectiveDockable> column ){
			this.index = index;
			this.column = column;

			if( column != null ) {
				columns.put( column, this );
			}
		}

		private Column( int index ){
			this.index = index;
		}

		private void validate(){
			if( column != null ){
				int index = column.getColumnIndex();
				if( index == -1 ){
					columns.remove( column );
					column = null;
					validateAll();
				}
				else{
					this.index = index;
				}
			}
		}
		
		/**
		 * Gets the number of {@link CToolbarPerspective toolbars} in this column.
		 * @return the number of toolbars
		 */
		public int getToolbarCount(){
			validate();
			if( column == null ) {
				return 0;
			}
			else {
				return column.getDockableCount();
			}
		}

		/**
		 * Gets or creates the toolbar at <code>index</code>.
		 * @param index the location of the toolbar, <code>-1</code> or {@link #getToolbarCount()}
		 * @return the toolbar or <code>null</code>. A value of <code>null</code> is only returned
		 * if the child has a wrong type, this happens only if the client changed the {@link ToolbarStrategy}
		 */
		public CToolbarPerspective toolbar( int index ){
			validate();
			if( index < 0 ) {
				return insert( 0 );
			}
			else if( index >= getToolbarCount() ) {
				return insert( getToolbarCount() );
			}
			PerspectiveElement child = column.getDockable( index );
			if( child instanceof ToolbarDockPerspective ) {
				return new CToolbarPerspective( (ToolbarDockPerspective) child );
			}
			else {
				return null;
			}
		}

		/**
		 * Creates a new toolbar at <code>index</code>.
		 * @param index the location of the new toolbar
		 * @return the new toolbar
		 */
		public CToolbarPerspective insert( int index ){
			validate();
			ToolbarDockPerspective toolbar = new ToolbarDockPerspective();
			if( column == null ) {
				delegate.insert( this.index, toolbar );
				column = delegate.getModel().getColumn( toolbar );
				columns.put( column, this );
			}
			else {
				delegate.insert( column.getColumnIndex(), index, toolbar );
			}
			return new CToolbarPerspective( toolbar );
		}

		/**
		 * Removes the toolbar at location <code>index</code> from this column.
		 * @param index the index of the toolbar to remove
		 */
		public void remove( int index ){
			validate();
			if( column != null ){
				delegate.remove( column.getDockable( index ) );
			}
		}

		/**
		 * Removes <code>toolbar</code> from this column.
		 * @param toolbar the toolbar to remove
		 */
		public void remove( CToolbarPerspective toolbar ){
			validate();
			delegate.remove( toolbar.getDelegate() );
		}
	}
}
