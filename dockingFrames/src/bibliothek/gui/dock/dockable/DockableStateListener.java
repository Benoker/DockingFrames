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
package bibliothek.gui.dock.dockable;

import bibliothek.gui.Dockable;

/**
 * This listener is invoked when the location of one or many {@link Dockable}s changed. The listener is
 * intended to keep track of a {@link Dockable} while its hierarchy remains the same. If the hierarchy 
 * changes an event is sent too, but applications should be aware that all flags but the "visibility" will
 * be ignored.
 * @author Benjamin Sigg
 */
public interface DockableStateListener {
	/**
	 * Called if the location changed. Many events can be merged into one event, it may
	 * happen that two events cancel each other out, but are still reported here.
	 * @param event detailed information about the change
	 */
	public void changed( DockableStateEvent event );
}
