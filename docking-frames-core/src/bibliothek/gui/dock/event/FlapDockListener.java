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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;

/**
 * A listener that is added to a {@link FlapDockStation}.
 * @author Benjamin Sigg
 */
public interface FlapDockListener {
    /**
     * Invoked when the hold-property of a {@link Dockable} has been changed. 
     * @param station the station invoking this listener
     * @param dockable the {@link Dockable} whose property has been changed
     * @param hold the new value of the property
     */
    public void holdChanged( FlapDockStation station, Dockable dockable, boolean hold );
}
