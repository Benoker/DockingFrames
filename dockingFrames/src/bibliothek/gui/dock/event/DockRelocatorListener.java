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

/**
 * A listener used by a {@link DockRelocator}
 * to inform when a {@link bibliothek.gui.Dockable} is moved around.
 * @author Benjamin Sigg
 *
 */
public interface DockRelocatorListener {
    /**
     * Invoked when a drag and drop operation is initiated. This method is
     * called when the user made the gesture of dragging, but not yet of dropping.
     * @param controller the origin of the event
     * @param dockable the element that has been grabbed
     */
    public void init( DockController controller, Dockable dockable );
    
    /**
     * Invoked when a drag and drop operation was initiated, but canceld. This
     * method can be called any time after {@link #init(DockController, Dockable)}.
     * was called. 
     * @param controller the origin of the event
     * @param dockable the element that has been grabbed
     */
    public void cancel( DockController controller, Dockable dockable );
    
    /**
     * Invoked when a drag and drop operation has been confirmed. This method
     * is called after the user made a drop-gesture, but before <code>dockable</code>
     * is dragged from its old parent.
     * @param controller the origin of the event
     * @param dockable the {@link Dockable} which was dragged
     * @param station the future parent of <code>dockable</code>
     * @see #drop(DockController, Dockable, DockStation)
     */
    public void drag( DockController controller, Dockable dockable, DockStation station );
    
    /**
     * Invoked after a {@link Dockable} was dropped.
     * @param controller the origin of the event
     * @param dockable the {@link Dockable} which was dragged
     * @param station the new parent of <code>dockable</code>
     * @see #drag(DockController, Dockable, DockStation)
     */
    public void drop( DockController controller, Dockable dockable, DockStation station );
    
}
