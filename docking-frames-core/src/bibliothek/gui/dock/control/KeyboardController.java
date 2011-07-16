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

package bibliothek.gui.dock.control;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.event.KeyboardListener;
import bibliothek.util.FrameworkOnly;

/**
 * An observer of all {@link KeyEvent}, forwarding them to registered listeners. The
 * listeners are only informed about events that occurred in a part of the dock-tree below them.
 * @author Benjamin Sigg
 *
 */
public interface KeyboardController {
	/**
	 * Adds a global key listener to this controller. Global {@link KeyListener}s
	 * will receive a notification for every event that this controller handles.
	 * The listeners will also be informed about events that are consumed.
	 * @param listener the new listener
	 */
	public void addGlobalListener( KeyListener listener );
	
	/**
	 * Removes a listener from this controller.
	 * @param listener the listener to remove
	 */
	public void removeGlobalListener( KeyListener listener );
	
	/**
	 * Adds a listener to this controller. The listener will be invoked
	 * when a {@link java.awt.event.KeyEvent} occurs in the subtree below
	 * the listeners {@link bibliothek.gui.dock.DockElement}.
	 * @param listener the new listener
	 */
	public void addListener( KeyboardListener listener );
	
	/**
	 * Removes a listener from this controller.
	 * @param listener the listener to remove
	 */
	public void removeListener( KeyboardListener listener );
	
	/**
	 * Gets the {@link DockController} in whose realm this {@link KeyboardController} works.
	 * @return the owner of this controller
	 */
	public DockController getController();
	
	/**
	 * Stops this controller. This controller has to remove any resources
	 * it uses and has not to be useful any further.<br>
	 * This method should no be called by clients
	 */
	@FrameworkOnly
	public abstract void kill();
}
