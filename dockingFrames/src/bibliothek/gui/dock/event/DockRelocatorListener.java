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

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.DockRelocator;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A listener used by a {@link DockRelocator}
 * to inform when a {@link bibliothek.gui.Dockable} is moved around.
 * @author Benjamin Sigg
 *
 */
public interface DockRelocatorListener {
    /**
     * Invoked before a {@link Dockable} is moved around. This method is called
     * after the user has released the mouse which was dragging a {@link DockTitle}
     * around.
     * @param controller the origin of the event
     * @param dockable the {@link Dockable} which was dragged
     * @param station the future parent of <code>dockable</code>
     * @see #dockablePut(DockController, Dockable, DockStation)
     */
    public void dockableDrag( DockController controller, Dockable dockable, DockStation station );
    
    /**
     * Invoked after a {@link Dockable} was moved.
     * @param controller the origin of the event
     * @param dockable the {@link Dockable} which was dragged
     * @param station the new parent of <code>dockable</code>
     * @see #dockableDrag(DockController, Dockable, DockStation)
     */
    public void dockablePut( DockController controller, Dockable dockable, DockStation station );
    
}
