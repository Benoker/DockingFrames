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

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.DirectRemoteRelocator;
import bibliothek.gui.dock.control.DockRelocator;
import bibliothek.gui.dock.control.RemoteRelocator;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.event.DockRegisterListener;

/**
 * This listener can be added to a {@link DockRelocator} and will receive an event
 * whenever the user, a {@link RemoteRelocator} or a {@link DirectRemoteRelocator} moves
 * around a {@link Dockable}. <br>
 * <b>Note:</b> this listener is intended to monitor the users actions. This listener will never be informed about events
 * that are triggered indirectly or by the client itself. It must not be used to keep track of the layout
 * of the entire application, {@link DockRegisterListener} and {@link DockHierarchyListener}s are much better fit for 
 * that job.
 * <br>
 * A successful drag and drop operation of a {@link Dockable} will cause these events:
 * <ol>
 * 	<li> {@link #grabbing(DockRelocatorEvent)} right before the operation starts.</li>
 *  <li> {@link #grabbed(DockRelocatorEvent)} once the operation started.</li>
 *  <li> {@link #searched(DockRelocatorEvent)} every time when the user moves the mouse. </li>
 *  <li> {@link #dropping(DockRelocatorEvent)} once the user released the mouse and the {@link Dockable} is about to
 *  change its position. </li>
 *  <li> optional: {@link #dragging(DockRelocatorEvent)} before the {@link Dockable} is removed from its current parent. </li>
 *  <li> optional: {@link #dragged(DockRelocatorEvent)} if the {@link Dockable} was removed from its parent. </li>
 *  <li> {@link #dropped(DockRelocatorEvent)} once the operation completed. </li>
 * </ol>
 * 
 * Other combinations of these event may happen in the future.
 * 
 * The event {@link #canceled(DockRelocatorEvent)} may be called at any time.
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
	 * Called after the mouse has moved, the {@link Dockable} may have a new target {@link DockStation}.
	 * @param event further description
	 */
	public void searched( DockRelocatorEvent event );
	
	/**
	 * Called when the user released the mouse, but the {@link Dockable} was not
	 * yet moved. After the drop operation completed the {@link Dockable} will either be a child
	 * of {@link DockRelocatorEvent#getTarget() the target station}, of its current parent or has no parent at all.
	 * @param event further description of the event
	 */
	public void dropping( DockRelocatorEvent event );

	/**
	 * Called before the {@link Dockable} is removed from its parent. This event may even be called, if the future
	 * parent of the {@link Dockable} is identical to the current parent.
	 * @param event further description of the event
	 */
	public void dragging( DockRelocatorEvent event );
	
	/**
	 * Called after the {@link Dockable} was removed from its parent. It is possible that the {@link Dockable}
	 * already has a new parent. It is even possible that the new parent is the old parent. This event can neither be canceled nor forbidden.
	 * @param event further description
	 */
	public void dragged( DockRelocatorEvent event );
	
	/**
	 * Called after a drag and drop operation finished. The {@link Dockable} was moved
	 * by the operation. Please note that the actual parent of the {@link Dockable} does not have to be
	 * {@link DockRelocatorEvent#getTarget() the target station}. This event can neither be canceled nor forbidden.
	 * @param event further description of the event
	 */
	public void dropped( DockRelocatorEvent event );
	
	/**
	 * Called if a drag and drop operation was canceled due to any reason.
	 * @param event further description of the event 
	 */
	public void canceled( DockRelocatorEvent event );
}
