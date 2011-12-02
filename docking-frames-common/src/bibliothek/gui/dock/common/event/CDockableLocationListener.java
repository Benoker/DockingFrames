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
package bibliothek.gui.dock.common.event;

import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;

/**
 * This listeners allows clients to keep track of the location of a {@link CDockable}. Events are received
 * whenever the location changes, or when the visibility to the user changes.<br>
 * <b>Note:</b>
 * <ul>
 * 	<li>Many events can be fired in rapid succession, for example if the user resizes a {@link ScreenDockWindow} 
 * one event is fired whenever a movement of the mouse is detected.</li>
 * 	<li>This is a supporting listener and does not forward any information that could not be gathered by other
 * listeners.</li>
 *  <li>New events are created delayed in order to collect as many information as possible. 
 *  If a client uses different listeners, then events sent to {@link CDockableLocationListener} may seem to 
 *  arrive very late. However, at the moment when an event arrives, its data is current.</li>
 *  <li>Due to its delayed nature, this listener may not receive events that cancel each other.</li>
 * </ul>
 * 
 * @author Benjamin Sigg
 */
public interface CDockableLocationListener {
	/**
	 * Called if the visibility and/or the location of a {@link CDockable} changed.
	 * @param event detailed information about the event
	 */
	public void changed( CDockableLocationEvent event );
}
