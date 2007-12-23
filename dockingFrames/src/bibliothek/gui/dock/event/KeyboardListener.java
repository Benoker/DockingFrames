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

package bibliothek.gui.dock.event;

import java.awt.event.KeyEvent;

import bibliothek.gui.dock.DockElement;

/**
 * A listener added to the {@link bibliothek.gui.dock.control.KeyboardController},
 * this listener receives a notification whenever a keyevent is dispatched
 * in a {@link bibliothek.gui.dock.DockElement} below the location of this
 * listener.
 * @author Benjamin Sigg
 */
public interface KeyboardListener extends LocatedListener {
	/**
	 * Called when a key has been pressed.
	 * @param element the source of the event
	 * @param event the description of the event
	 * @return <code>true</code> if this listener processed the event, and
	 * the event has not to be forwarded any further.
	 */
	public boolean keyPressed( DockElement element, KeyEvent event );
	
	/**
	 * Called when a key has been released.
	 * @param element the source of the event
	 * @param event the description of the event
	 * @return <code>true</code> if this listener processed the event, and
	 * the event has not to be forwarded any further.
	 */
	public boolean keyReleased( DockElement element, KeyEvent event );
	
	/**
	 * Called when a key has been types.
	 * @param element the source of the event
	 * @param event the description of the event
	 * @return <code>true</code> if this listener processed the event, and
	 * the event has not to be forwarded any further.
	 */
	public boolean keyTyped( DockElement element, KeyEvent event );
}
