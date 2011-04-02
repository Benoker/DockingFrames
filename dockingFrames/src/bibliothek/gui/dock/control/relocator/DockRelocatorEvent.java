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

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.DockRelocator;

/**
 * An event created by the {@link DockRelocator} and forwarded to the {@link VetoableDockRelocatorListener}.
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
	 * Gets the current target of the drag and drop operation, if the operation would finish
	 * now, {@link #getDockable() the dockable} would be dropped onto this station.
	 * @return the current target or <code>null</code> if either the drag and drop operation is
	 * not yet started or if there is no target selected
	 */
	public DockStation getTarget();
	
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
}
