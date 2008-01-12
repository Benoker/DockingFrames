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
package bibliothek.gui.dock.common.event;

import bibliothek.gui.dock.common.intern.FDockable;

/**
 * A class implementing all methods of {@link FDockableListener}. The methods
 * have an empty body and can be overriden by subclasses.
 * @author Benjamin Sigg
 *
 */
public class FDockableAdapter implements FDockableListener {
    public void closeableChanged( FDockable dockable ) {
        // empty
    }

    public void externalizableChanged( FDockable dockable ) {
        // empty
    }

    public void externalized( FDockable dockable ) {
        // empty
    }

    public void maximizableChanged( FDockable dockable ) {
        // empty
    }

    public void maximized( FDockable dockable ) {
        // empty
    }

    public void minimizableChanged( FDockable dockable ) {
        // empty
    }

    public void minimized( FDockable dockable ) {
        // empty
    }

    public void normalized( FDockable dockable ) {
        // empty
    }

    public void visibilityChanged( FDockable dockable ) {
        // empty
    }
}
