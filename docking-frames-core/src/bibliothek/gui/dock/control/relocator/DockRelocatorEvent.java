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

import java.awt.Point;
import java.awt.event.MouseEvent;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.DirectRemoteRelocator;
import bibliothek.gui.dock.control.DockRelocator;
import bibliothek.gui.dock.control.RemoteRelocator;

/**
 * An event created by the {@link DockRelocator} and forwarded to the {@link VetoableDockRelocatorListener}. This event
 * represents what the users sees on the screen, internally the drag and drop operation may trigger additional events.
 * @author Benjamin Sigg
 */
public interface DockRelocatorEvent {
	/**
	 * Gets the controller in whose realm this event was created.
	 * @return the controller, not <code>null</code>
	 */
	public DockController getController();
	
	/**
	 * Gets the {@link DockRelocator} which created this event.
	 * @return the relocator, not <code>null</code>
	 */
	public DockRelocator getSource();
	
	/**
	 * Gets the {@link Dockable} which is or will be dragged.
	 * @return the dragged item
	 */
	public Dockable getDockable();
	
	/**
	 * Gets a set of {@link Dockable}s that will also change their parent due to this
	 * event. This list contains only the set of {@link Dockable}s that are <i>directly</i> affected by this event.
	 * Elements that are affected indirectly, e.g. because a {@link DockStation} remains that has only one child and
	 * thus gets removed, are not included. 
	 * @return the items whose position is about to change too, can be empty but not <code>null</code>
	 */
	public Dockable[] getImplicitDockables();
	
	/**
	 * Gets the current target of the drag and drop operation, if the operation would finish
	 * now, {@link #getDockable() the dockable} would be dropped onto this station.<br>
	 * Please note that this field does not take into consideration, that the <code>dockable</code> may
	 * be forced onto another parent. Clients should always ask the {@link Dockable} directly for its
	 * real parent.
	 * @return the current target or <code>null</code> if either the drag and drop operation is
	 * not yet started or if there is no target selected
	 */
	public DockStation getTarget();
	
	/**
	 * Gets the location of the mouse on the screen. This includes imaginary points created by calls
	 * to a {@link DirectRemoteRelocator} or a {@link RemoteRelocator}. The property may not be
	 * set if either the location of the mouse is not important for the current event, or simply
	 * if the location of the mouse is not known.
	 * @return the location of the mouse, may be <code>null</code>
	 */
	public Point getMouseLocation();
	
	/**
	 * Cancels the entire operation, the {@link Dockable} remains at its current place and
	 * all visible indicators are removed. 
	 */
	public void cancel();
	
	/**
	 * Tells whether this event is already canceled. This can either be due to a call to
	 * {@link #cancel()} or because the {@link DockRelocator} already decided that the
	 * operation has to be canceled.
	 * @return whether the operation is canceled
	 * @see #cancel()
	 */
	public boolean isCanceled();
	
	/**
	 * Forbids the current event to complete, depending on the state of the operation this is 
	 * equivalent to calling {@link #cancel()}.
	 */
	public void forbid();
	
	/**
	 * Tells whether this event is forbidden. This can either be due to a call to {@link #forbid()}
	 * or because the {@link DockRelocator} already decided that this operation should not 
	 * have an effect.
	 * @return whether the operation is forbidden
	 * @see #forbid()
	 */
	public boolean isForbidden();
	
	/**
	 * Advices to complete the drag and drop operation right now, this operation at least 
	 * requires {@link #getTarget()} to be not <code>null</code>, otherwise this operation
	 * is equal to calling {@link #cancel()}.
	 */
	public void drop();
	
	/**
	 * Tells whether this event will result in a drop operation. This can be either due to
	 * a call to {@link #drop()} or because the {@link DockRelocator} already decided that
	 * a the operation has to finish.
	 * @return whether {@link #getDockable() the dockable} will be dropped
	 * @see #drop()
	 */
	public boolean isDropping();
	
	/**
	 * The {@link DockRelocator} is to behave as if this event never happened. Usually this means that
	 * the {@link MouseEvent} is not {@link MouseEvent#consume() consumed} and that no action takes place.
	 * Note however that some events cannot be ignored, for example if the operation is 
	 * {@link #isCanceled() canceled}.
	 */
	public void ignore();
	
	/**
	 * Tells whether the {@link DockRelocator} behaves as if an event did not happen.
	 * @return whether to ignore the event
	 */
	public boolean isIgnored();
	
	/**
	 * Tells whether the event really is a move event (the parent of the {@link Dockable} does not change).
	 * @return <code>true</code> if the event describes a move event, <code>false</code> if not or if the event
	 * does not describe a drop operation.
	 */
	public boolean isMove();
}
