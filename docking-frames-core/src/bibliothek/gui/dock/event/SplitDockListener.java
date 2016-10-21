/**
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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;

/**
 * A listener which is added to a {@link SplitDockStation}. The listener
 * is informed when the fullscreen-property is changed.
 * @author Benjamin Sigg
 *
 */
public interface SplitDockListener {
	/**
	 * Invoked when a new {@link Dockable} was made fullscreen, or 
	 * no {@link Dockable} at all is fullscreen.
	 * @param station the station on which the event happened
	 * @param oldFullScreen the {@link Dockable} that was fullscreen, may be <code>null</code>
	 * @param newFullScreen the {@link Dockable} which is now fullscreen, may be <code>null</code>
	 * @see SplitDockStation#setFullScreen(Dockable)
	 * @see SplitDockStation#getFullScreen()
	 */
    public void fullScreenDockableChanged( SplitDockStation station, Dockable oldFullScreen, Dockable newFullScreen );
}
