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
package bibliothek.gui.dock.station.screen;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;

/**
 * This filter can be added to the {@link ScreenDockStation}, it allows to filter {@link Dockable}s which
 * are not allowed to go into fullscreen mode. These {@link Dockable}s will not react on a double click nor
 * will have the fullscreen action.
 * @author Benjamin Sigg
 */
public interface ScreenDockFullscreenFilter {
	/**
	 * Tells whether <code>dockable</code> can be in fullscreen mode. All filters have to agree
	 * in order to allow fullscreen mode.
	 * @param dockable the element to check
	 * @return whether fullscreen mode should be enabled
	 */
	public boolean isFullscreenEnabled( Dockable dockable );
}
