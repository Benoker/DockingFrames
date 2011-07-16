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

import bibliothek.gui.dock.station.LayoutLocked;

/**
 * A listener that is added to a {@link bibliothek.gui.Dockable#addDockHierarchyListener(DockHierarchyListener) Dockable}
 * and is informed whenever the path of the element changes.
 * @author Benjamin Sigg
 */
@LayoutLocked
public interface DockHierarchyListener {
	/**
	 * Called when the path of the observed {@link bibliothek.gui.Dockable} has been changed.
	 * @param event the new path
	 */
	public void hierarchyChanged( DockHierarchyEvent event );
	
	/**
	 * Invoked whenever the controller of <code>dockable</code> has changed
	 * @param event the current path and controller
	 */
	public void controllerChanged( DockHierarchyEvent event );
}
