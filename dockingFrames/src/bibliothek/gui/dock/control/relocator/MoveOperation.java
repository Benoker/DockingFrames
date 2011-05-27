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
package bibliothek.gui.dock.control.relocator;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * This operation calls {@link DockStation#move()}.
 * @author Benjamin Sigg
 *
 */
public class MoveOperation implements RelocateOperation{
	private DockController controller;
	private DockStation station;
	
	/**
	 * Creates a new operation
	 * @param controller the controller in whose realm this operation works
	 * @param station the target of this operation
	 */
	public MoveOperation( DockController controller, DockStation station ){
		this.controller = controller;
		this.station = station;
	}
	
	public DockStation getStation(){
		return station;
	}
	
	public Dockable[] getImplicit( Dockable selection ){
		return new Dockable[]{};
	}
	
	public void execute( Dockable selection, VetoableDockRelocatorListener listener ){
		DefaultDockRelocatorEvent event = new DefaultDockRelocatorEvent( controller, selection, station );
		listener.dropping( event );
		if( event.isCanceled() || event.isForbidden() ){
			event = new DefaultDockRelocatorEvent( controller, selection, station ); 
			event.cancel();
			listener.canceled( event );
			return;
		}
		event = new DefaultDockRelocatorEvent( controller, selection, station );
		listener.dragging( event );
		if( event.isCanceled() || event.isForbidden() ){
			event = new DefaultDockRelocatorEvent( controller, selection, station ); 
			event.cancel();
			listener.canceled( event );
			return;
		}
		station.move();
		listener.dragged( new DefaultDockRelocatorEvent( controller, selection, station ) );
        listener.dropped( new DefaultDockRelocatorEvent( controller, selection, station ) );
	}
}
