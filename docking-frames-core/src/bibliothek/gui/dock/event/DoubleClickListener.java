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

import java.awt.event.MouseEvent;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.DoubleClickController;

/**
 * A {@link DoubleClickListener} is added to the {@link DoubleClickController}
 * and may receive an event if a {@link Dockable}, which is child or equal
 * to {@link #getTreeLocation()}, was clicked twice. The event is only received
 * if no other <code>DoubleClickObserver</code> processed the event.
 * @author Benjamin Sigg
 *
 */
public interface DoubleClickListener extends LocatedListener {
    /**
     * Called when the user has clicked twice on <code>dockable</code> or
     * on one of the titles of <code>dockable</code>.
     * @param dockable the clicked element
     * @param event the cause of the invocation of this method
     * @return <code>true</code> if this observer processed the event (and
     * the event must not be forwarded to any other observer), <code>false</code>
     * if this observer is not interested in the event
     */
    public boolean process( Dockable dockable, MouseEvent event );
}
