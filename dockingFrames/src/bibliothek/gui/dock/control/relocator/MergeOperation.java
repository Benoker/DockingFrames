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

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * Uses a {@link Merger} to merge two {@link DockStation}s.
 * @author Benjamin Sigg
 */
public class MergeOperation implements RelocateOperation{
	private DockController controller;
	private Merger merger;
	private DockStation station;
	
	/**
	 * Creates a new operation.
	 * @param controller the controller in whose realm this operation works
	 * @param merger the merger that will be used to merge the stations
	 * @param station the target of this operation
	 */
	public MergeOperation( DockController controller, Merger merger, DockStation station ){
		this.controller = controller;
		this.merger = merger;
		this.station = station;
	}
	
	public DockStation getStation(){
		return station;
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
			DefaultDockRelocatorEvent event = new DefaultDockRelocatorEvent( controller, selection, children, station );
			listener.dragging( event );
			if( event.isCanceled() || event.isForbidden() ){
				return false;
			}
		}
		
		merger.merge( station, child );
		
		parent = selection.getDockParent();
		if( parent != null && parent != station ){
			parent.drag( selection );
		}
		
		return true;
	}
}
