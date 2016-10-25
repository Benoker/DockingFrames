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
package bibliothek.gui.dock.station.toolbar;

import java.util.Map;

import bibliothek.gui.DockStation;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumnModel;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupProperty;
import bibliothek.gui.dock.station.toolbar.layout.PerspectivePlaceholderToolbarGrid;
import bibliothek.gui.dock.station.toolbar.layout.PlaceholderToolbarGridConverter;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.Path;

/**
 * A {@link PerspectiveStation} representing a {@link ToolbarGroupDockStation}.
 * @author Benjamin Sigg
 */
public class ToolbarGroupDockPerspective implements PerspectiveStation, PerspectiveDockable{
	/** the children of this station */
	private PerspectivePlaceholderToolbarGrid dockables;
	
	/** the parent station of this dockable */
	private PerspectiveStation parent;

	/** the default size of this station */
	private ExpandedState state = ExpandedState.SHRUNK;

	/** the orientation of the children */
	private Orientation orientation = Orientation.HORIZONTAL;
	
	/**
	 * Creates a new, empty station.
	 */
	public ToolbarGroupDockPerspective(){
		dockables = new PerspectivePlaceholderToolbarGrid();
	}
	
	/**
	 * Creates a new station.
	 * @param layout the layout of the station
	 * @param children the unique identifiers of the children
	 */
	public ToolbarGroupDockPerspective( ToolbarGroupDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
		dockables = new PerspectivePlaceholderToolbarGrid();
		read( layout, children );
	}
	
	/**
	 * Updates the layout of this station by reading <code>layout</code>.
	 * @param layout the new layout
	 * @param children the new children of this station
	 */
	public void read( ToolbarGroupDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
		state = layout.getState();
		dockables.fromMap( layout.getPlaceholders(), children, new PlaceholderToolbarGridConverter<PerspectiveDockable, PerspectiveDockable>(){
			@Override
			public PerspectiveDockable convert( PerspectiveDockable dockable, ConvertedPlaceholderListItem item ){
				return dockable;
			}
			
			@Override
			public void added( PerspectiveDockable item ){
				item.setParent( ToolbarGroupDockPerspective.this );
			}
		});
		orientation = ToolbarGroupDockStationLayout.readOrientation( layout.getPlaceholders() );
	}
	
	/**
	 * Allows access to the internal data structure of this station. Clients should be very
	 * carefull when using this structure, as there are no options to ensure the validity of the structure. If
	 * possible, clients should only read data from the structure, and should not execute any write
	 * operations.
	 * @return the internal data structure
	 */
	public PerspectivePlaceholderToolbarGrid getDockables(){
		return dockables;
	}
	
	/**
	 * Converts this station into a map of {@link PerspectiveDockable}s.
	 * @param children the unique identifiers of the children
	 * @return the map of dockables
	 */
	public PlaceholderMap toMap( Map<PerspectiveDockable, Integer> children ){
		PlaceholderMap map = dockables.toMap( children );
		ToolbarGroupDockStationLayout.writeOrientation( map, orientation );
		return map;
	}
	
	/***
	 * Sets the default size of this station
	 * @param state the size, not <code>null</code>
	 */
	public void setExpandedtState( ExpandedState state ){
		if( state == null ){
			throw new IllegalArgumentException( "state must not be null" );
		}
		this.state = state;
	}
	
	/**
	 * Gets the default size of this station.
	 * @return the size, not <code>null</code>
	 */
	public ExpandedState getExpandedState(){
		return state;
	}
	
	/**
	 * Sets the orientation of this station. Please note that the orientation can be overridden
	 * by the parent {@link DockStation}.
	 * @param orientation the orientation, must not be <code>null</code>
	 */
	public void setOrientation( Orientation orientation ){
		if( orientation == null ){
			throw new IllegalArgumentException( "orientation must not be null" );
		}
		this.orientation = orientation;
	}
	
	/**
	 * Gets the orientation of this station.
	 * @return the orientation, not <code>null</code>
	 */
	public Orientation getOrientation(){
		return orientation;
	}
	
	/**
	 * Gets access to an abstraction of the model of this station. This is the same class that is used
	 * by the {@link ToolbarGroupDockStation}, hence it's behavior is identical.
	 * @return the model, can be monitored to receive information about changes
	 */
	public ToolbarColumnModel<PerspectiveDockable, PerspectiveDockable> getModel(){
		return dockables.getModel();
	}
	
	/**
	 * Gets the item that is stored in column <code>column</code> at line <code>line</code>.
	 * @param column the column in which to search
	 * @param line the line in which the dockable should be found
	 * @return the item at <code>column/line</code>
	 */
	public PerspectiveDockable getDockable( int column, int line ){
		return getModel().getColumn( column ).getDockable( line );
	}
	
	/**
	 * Gets the number of columns, only includes columns that contain at least one child.
	 * @return the number of non-empty columns
	 */
	public int getColumnCount(){
		return dockables.getColumnCount();
	}
	
	/**
	 * Gets the number of items in <code>column</code>.
	 * @param column the column whose size is asked
	 * @return the number of children in <code>column</code>
	 */
	public int getLineCount( int column ){
		return dockables.getLineCount( column );
	}
	
	/**
	 * Adds <code>dockable</code> at the end of column <code>column</code>. This method
	 * may create a new column. 
	 * @param column the index of a column
	 * @param dockable the item to add to <code>column</code>
	 */
	public void add( int column, PerspectiveDockable dockable ){
		int line = 0;
		if( column >= 0 && column < getColumnCount() ){
			line = getLineCount( column );
		}
		insert( column, line, dockable );
	}
	
	/**
	 * Adds <code>dockable</code> to column <code>column</code> at position <code>line</code>.
	 * This method may create a new column.
	 * @param column the column in which to insert the new item
	 * @param line the position in the column
	 * @param dockable the new item
	 */
	public void insert( int column, int line, PerspectiveDockable dockable ){
		DockUtilities.ensureTreeValidity( this, dockable );
		dockables.insert( column, line, dockable );
		dockable.setParent( this );
	}
	
	/**
	 * Adds a new column to the grid and inserts <code>dockable</code> as first child. This method
	 * will try to reuse existing but empty columns if possible.
	 * @param column The index of the new column, can be <code>-1</code> to create a column at the
	 * beginning of the list
	 * @param dockable the new item
	 */
	public void insert( int column, PerspectiveDockable dockable ){
		DockUtilities.ensureTreeValidity( this, dockable );
		dockables.insert( column, dockable );
		dockable.setParent( this );
	}
	
	/**
	 * Removes the item at <code>column/line</code>.
	 * @param column the column from which the dockable should be removed
	 * @param line the line in which the dockable is currently stored
	 */
	public void remove( int column, int line ){
		remove( getDockable( column, line ) );
	}
	
	@Override
	public String getFactoryID(){
		return ToolbarGroupDockStationFactory.ID;
	}

	@Override
	public PerspectiveStation asStation(){
		return this;
	}

	@Override
	public PerspectiveDockable asDockable(){
		return this;
	}

	@Override
	public Path getPlaceholder(){
		return null;
	}

	@Override
	public PerspectiveStation getParent(){
		return parent;
	}

	@Override
	public void setParent( PerspectiveStation parent ){
		this.parent = parent;
	}

	@Override
	public int getDockableCount(){
		return dockables.size();
	}

	@Override
	public PerspectiveDockable getDockable( int index ){
		return dockables.get( index );
	}

	@Override
	public DockableProperty getDockableProperty( PerspectiveDockable child, PerspectiveDockable target ){
		int column = dockables.getColumn( child );
		int line = dockables.getLine( column, child );
		Path placeholder;
		if( target == null ){
			placeholder = child.getPlaceholder();
		}
		else{
			placeholder = target.getPlaceholder();
		}
		return new ToolbarGroupProperty( column, line, placeholder );
	}

	@Override
	public PlaceholderMap getPlaceholders(){
		PlaceholderMap map = dockables.toMap();
		ToolbarGroupDockStationLayout.writeOrientation( map, orientation );
		return map;
	}

	@Override
	public void setPlaceholders( PlaceholderMap placeholders ){
		dockables.fromMap( placeholders );
	}

	@Override
	public boolean remove( PerspectiveDockable dockable ){
		return dockables.remove( dockable );
	}

	@Override
	public void replace( PerspectiveDockable oldDockable, PerspectiveDockable newDockable ){
		dockables.replace( oldDockable, newDockable );
	}
}
