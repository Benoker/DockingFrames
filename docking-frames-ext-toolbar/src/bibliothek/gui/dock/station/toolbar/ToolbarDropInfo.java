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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.support.CombinerTarget;

/**
 * This class contains and computes information about a drag and drop action of
 * a {@link ToolbarDockStation}.
 * 
 * @author Herve Guillaume
 */
public abstract class ToolbarDropInfo implements StationDropOperation{
	/** The {@link Dockable} which is inserted */
	private Dockable dockable;
	
	/** The new parent of {@link #dockable}. */
	private ToolbarDockStation station;
	
	/** The index {@link #dockable} will have after insertion */
	private int index;

	/**
	 * Creates new drop information.
	 * @param dockable the item that is dropped
	 * @param station the new parent of {@link #dockable}
	 * @param index where to insert {@link #dockable}
	 */
	public ToolbarDropInfo( Dockable dockable, ToolbarDockStation station, int index ){
		this.dockable = dockable;
		this.station = station;
		this.index = index;
	}

	@Override
	public Dockable getItem(){
		return dockable;
	}

	@Override
	public ToolbarDockStation getTarget(){
		return station;
	}

	/**
	 * Gets the location the {@link #getItem() item} would have after inserting
	 * into the {@link #getTarget() target}.
	 * @return the new position
	 */
	public int getIndex(){
		return index;
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
	
	public boolean hasNoEffect(){
		if( !isMove() ){
			return false;
		}
		int oldIndex = station.indexOf( dockable );
		int index = this.index;
		if( oldIndex < index ){
			index--;
		}
		return oldIndex == index;
	}
}
