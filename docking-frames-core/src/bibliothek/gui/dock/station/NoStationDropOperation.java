/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.station;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.support.CombinerTarget;

/**
 * This {@link StationDropOperation} represent the no-op, and will not do anything.
 * @author Benjamin Sigg
 */
public class NoStationDropOperation implements StationDropOperation{
	private DockStation target;
	private Dockable item;
	
	/**
	 * Create a new operation
	 * @param target the station executing this no-operation 
	 * @param item the item wich is not moved
	 */
	public NoStationDropOperation( DockStation target, Dockable item ){
		this.target = target;
		this.item = item;
	}
	
	public void draw(){
		// ignore
	}

	public void destroy( StationDropOperation next ){
		// ignore		
	}

	public boolean isMove(){
		return true;
	}

	public void execute(){
		// ignore		
	}

	public DockStation getTarget(){
		return target;
	}

	public Dockable getItem(){
		return item;
	}

	public CombinerTarget getCombination(){
		return null;
	}

	public DisplayerCombinerTarget getDisplayerCombination(){
		return null;
	}
}
