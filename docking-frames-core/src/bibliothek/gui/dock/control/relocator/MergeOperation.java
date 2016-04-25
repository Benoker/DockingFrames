/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.control.relocator;

import java.awt.Point;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.StationDropItem;
import bibliothek.gui.dock.station.StationDropOperation;

/**
 * Uses a {@link Merger} to merge two {@link DockStation}s.
 * @author Benjamin Sigg
 */
public class MergeOperation implements RelocateOperation{
	private DockController controller;
	private Merger merger;
	private DockStation station;
	private StationDropOperation operation;
	private StationDropItem item;
	
	/**
	 * Creates a new operation.
	 * @param controller the controller in whose realm this operation works
	 * @param merger the merger that will be used to merge the stations
	 * @param station the target of this operation
	 * @param operation the operation that would be executed by the station
	 * @param item information about the source of the operation
	 */
	public MergeOperation( DockController controller, Merger merger, DockStation station, StationDropOperation operation, StationDropItem item ){
		this.controller = controller;
		this.merger = merger;
		this.station = station;
		this.operation = operation;
		this.item = item;
	}
	
	public DockStation getStation(){
		return station;
	}
	
	public StationDropOperation getOperation(){
		return operation;
	}
	
	public void destroy( RelocateOperation next ){
		if( next == null ){
			operation.destroy( null );
		}
		else{
			operation.destroy( next.getOperation() );
		}
	}
	
	public Dockable[] getImplicit( Dockable selection ){
		DockStation child = selection.asDockStation();
		Dockable[] children = new Dockable[ child.getDockableCount() ];
		for( int i = 0; i < children.length; i++ ){
			children[i] = child.getDockable( i );
		}
		return children;
	}
	
	public boolean execute( Dockable selection, VetoableDockRelocatorListener listener ){
		DockStation child = selection.asDockStation();
		DockStation parent = selection.getDockParent();

		Dockable[] children = getImplicit( selection );
		
		if( parent != null && parent != station ){
			Point mouse = new Point( item.getMouseX(), item.getMouseY() );
			DefaultDockRelocatorEvent event = new DefaultDockRelocatorEvent( controller, selection, children, station, mouse, getOperation().isMove() );
			listener.dragging( event );
			if( event.isCanceled() || event.isForbidden() ){
				return false;
			}
		}
		
		merger.merge( operation, station, child );
		
		parent = selection.getDockParent();
		if( parent != null && parent != station ){
			parent.drag( selection );
		}
		
		return true;
	}
}
