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
package bibliothek.gui.dock.control.relocator;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.DirectRemoteRelocator;
import bibliothek.gui.dock.control.DockRelocator;
import bibliothek.gui.dock.control.RemoteRelocator;

/**
 * This listener can be added to a {@link DockRelocator} and will receive an event
 * whenever the user, a {@link RemoteRelocator} or a {@link DirectRemoteRelocator} moves
 * around a {@link Dockable}.
 * @author Benjamin Sigg
 */
public interface VetoableDockRelocatorListener {
	/**
	 * Called before the drag and drop operations starts, the user is already pressing the mouse,
	 * but there are no visible indicators of the operation yet.
	 * @param event further description of the event
	 */
	public void grabbing( DockRelocatorEvent event );
	
	/**
	 * Called when the drag and drop operation started, after this method was invoked visible
	 * indicators for the user will start to appear.
	 * @param event further description of the event
	 */
	public void grabbed( DockRelocatorEvent event );

	/**
	 * Called whenever the user moves the mouse during a drag and drop operation.
	 * @param event further description of the event
	 */
	public void dragged( DockRelocatorEvent event );
	
	/**
	 * Called when the user released the mouse, but the {@link Dockable} was not
	 * yet moved.
	 * @param event further description of the event
	 */
	public void dropping( DockRelocatorEvent event );
	
	/**
	 * Called after a drag and drop operation finished. The {@link Dockable} was moved
	 * by the operation.
	 * @param event further description of the event
	 */
	public void dropped( DockRelocatorEvent event );
	
	/**
	 * Called if a drag and drop operation was canceled due to any reason.
	 * @param event further description of the event 
	 */
	public void canceled( DockRelocatorEvent event );
}
