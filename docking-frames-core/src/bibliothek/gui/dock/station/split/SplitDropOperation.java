/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.gui.dock.station.split;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.StationDropItem;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.support.CombinerTarget;

/**
 * Implementation of {@link StationDropOperation}.
 * @author Benjamin Sigg
 */
public class SplitDropOperation implements StationDropOperation{
	private SplitDockAccess access;
	private PutInfo putInfo;
	private StationDropItem item;
	private boolean move;
	
	/**
	 * Creates a new operation.
	 * @param access access to the internal functions of a {@link SplitDockStation}
	 * @param putInfo the desired location of the dropped {@link Dockable}.
	 * @param item detailed information about the ongoing drag and drop operation
	 * @param move whether this operation is a move operation or not 
	 */
	public SplitDropOperation( SplitDockAccess access, PutInfo putInfo, StationDropItem item, boolean move ){
		this.access = access;
		this.putInfo = putInfo;
		this.item = item;
		this.move = move;
	}
	
	public boolean isMove(){
		return move;
	}
	
	public void draw(){
		access.setDropInfo( putInfo );
	}

	public void destroy( StationDropOperation next ){
		if( access.getOwner().getDropInfo() == putInfo ){
			access.setDropInfo( null );
			if( next == null || !(next instanceof SplitDropOperation) || next.getTarget() != getTarget() ){
				access.unsetDropInfo();
			}
		}
	}
	
	public DockStation getTarget(){
		return access.getOwner();
	}
	
	public Dockable getItem(){
		return putInfo.getDockable();
	}
	
	public CombinerTarget getCombination(){
		return putInfo.getCombinerTarget();
	}
	
	public DisplayerCombinerTarget getDisplayerCombination(){
		CombinerTarget target = getCombination();
		if( target == null ){
			return null;
		}
		return target.getDisplayerCombination();
	}
	
	public void execute(){
		access.unsetDropInfo();
		if( isMove() ){
			access.move( putInfo, item );
		}
		else{
			access.drop( null, putInfo, item );
		}
	}
	
}