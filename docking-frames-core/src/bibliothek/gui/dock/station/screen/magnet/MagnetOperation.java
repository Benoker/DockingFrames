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
package bibliothek.gui.dock.station.screen.magnet;

import bibliothek.gui.dock.station.screen.ScreenDockWindow;

/**
 * Creates by a {@link MagnetStrategy}, this operation is created once movement or resizing of a 
 * {@link ScreenDockWindow} started and exists until this operation stopped. 
 * @author Benjamin Sigg
 */
public interface MagnetOperation {
	/**
	 * Called by <code>controller</code> after a {@link ScreenDockWindow} moved or changed
	 * its size (or both). This method has to find out whether the window is attracted to another
	 * window and if so it must call one of the <code>attract</code> methods of <code>request</code>.
	 * @param controller the caller of this method, may be used to find other {@link ScreenDockWindow}s
	 * @param request detailed information about the event
	 */
	public void attract( MagnetController controller, MagnetRequest request );
	
	/**
	 * Called once the operation is no longer needed. 
	 */
	public void destroy();
}
