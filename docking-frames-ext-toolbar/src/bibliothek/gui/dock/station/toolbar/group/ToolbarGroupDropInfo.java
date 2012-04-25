/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Hervé Guillaume, Benjamin Sigg
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
 * Hervé Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.station.toolbar.group;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.support.CombinerTarget;

/**
 * This class contains and computes information about a drag and drop action.
 * Especially, where the {@link Dockable} should be inserted into which
 * {@link DockStation}
 * 
 * @author Herve Guillaume
 * @param <S>
 *            the kind of station using this {@link ToolbarGroupDropInfo}
 */
public abstract class ToolbarGroupDropInfo implements StationDropOperation{
	/** The {@link Dockable} which is inserted */
	private final Dockable dragDockable;
	/**
	 * The {@link Dockable} which received the dockbale (WARNING: this can be
	 * different to his original dock parent!)
	 */
	private final ToolbarGroupDockStation stationHost;
	
	/** the column into which to insert the {@link #dragDockable} */
	private int column;
	
	/** the row into which to insert the {@link #dragDockable} or -1 */
	private int line;

	/**
	 * Creates a new drop info for a {@link Dockable} that is inserted into a new column.
	 * @param dockable the item that is dropped
	 * @param station the station onto which <code>dockable</code> is dropped
	 * @param column the index of the new column
	 */
	public ToolbarGroupDropInfo( Dockable dockable, ToolbarGroupDockStation station, int column ){
		this( dockable, station, column, -1 );
	}
	
	/**
	 * Creates a new drop info.
	 * @param dockable the item that is dropped
	 * @param station the station onto which <code>dockable</code> is dropped
	 * @param column the column into which <code>dockable</code> is dropped, this may be an existing
	 * column or a new column
	 * @param line the row in which <code>dockable</code> will appear, a value of -1 indiciates that
	 * the item will appear in a new column
	 */
	public ToolbarGroupDropInfo( Dockable dockable, ToolbarGroupDockStation station, int column, int line ){
		this.dragDockable = dockable;
		this.stationHost = station;
		this.column = column;
		this.line = line;
	}

	@Override
	public Dockable getItem(){
		return dragDockable;
	}

	@Override
	public ToolbarGroupDockStation getTarget(){
		return stationHost;
	}

	/**
	 * Gets the column into which {@link #getItem() the item} is inserted.
	 * @return the index of the column, this may be an existing column or a new column
	 */
	public int getColumn(){
		return column;
	}
	
	/**
	 * Gets the line into which {@link #getItem() the item} is inserted. A value of -1 indicates
	 * that a new column is to be created.
	 * @return the line or -1
	 */
	public int getLine(){
		return line;
	}
	
	@Override
	public CombinerTarget getCombination(){
		// not supported by this kind of station
		return null;
	}

	@Override
	public DisplayerCombinerTarget getDisplayerCombination(){
		// not supported by this kind of station
		return null;
	}

	@Override
	public boolean isMove(){
		return getItem().getDockParent() == getTarget();
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName() + '@'
				+ Integer.toHexString(hashCode());
	}
}