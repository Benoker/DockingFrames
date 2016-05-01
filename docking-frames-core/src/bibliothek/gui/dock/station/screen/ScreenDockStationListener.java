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
package bibliothek.gui.dock.station.screen;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;

/**
 * A listener that can be added to a {@link ScreenDockStation}.
 * @author Benjamin
 */
public interface ScreenDockStationListener {
	/**
	 * Called if the fullscreen-mode of <code>dockable</code> changed.
	 * @param station the caller
	 * @param dockable the element whose mode changed
	 */
	public void fullscreenChanged( ScreenDockStation station, Dockable dockable );
	
	/**
	 * Called when <code>window</code> was added to <code>station</code>.
	 * @param station the caller 
	 * @param dockable the element that is shown on <code>window</code>
	 * @param window the new window
	 */
	public void windowRegistering( ScreenDockStation station, Dockable dockable, ScreenDockWindow window );
	
	/**
	 * Called when <code>window</code> was removed from <code>station</code>.
	 * @param station the element that was shown on <code>window</code>
	 * @param dockable the element that was shown on <code>window</code>
	 * @param window the removed window
	 */
	public void windowDeregistering( ScreenDockStation station, Dockable dockable, ScreenDockWindow window );
}
