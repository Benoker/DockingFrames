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

import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.intern.CDockable;

/**
 * A class implementing all methods of {@link CDockableStateListener}
 * and {@link CDockablePropertyListener}.
 * The methods have an empty body and can be overridden by subclasses.
 * @author Benjamin Sigg
 */
public class CDockableAdapter implements CDockableStateListener, CDockablePropertyListener {
    public void closeableChanged( CDockable dockable ) {
        // empty
    }
    
    public void resizeLockedChanged( CDockable dockable ) {
        // empty
    }

    public void externalizableChanged( CDockable dockable ) {
        // empty
    }

    public void externalized( CDockable dockable ) {
        // empty
    }

    public void maximizableChanged( CDockable dockable ) {
        // empty
    }

    public void maximized( CDockable dockable ) {
        // empty
    }

    public void minimizableChanged( CDockable dockable ) {
        // empty
    }

    public void minimized( CDockable dockable ) {
        // empty
    }
    
    public void minimizedHoldChanged( CDockable dockable ) {
        // empty
    }
    
    public void minimizeSizeChanged( CDockable dockable ) {
        // empty
    }
    
    public void titleShownChanged( CDockable dockable ) {
        // empty
    }

    public void actionChanged( CDockable dockable, String key, CAction oldAction, CAction newAction ) {
        // empty
    }
    
    public void normalized( CDockable dockable ) {
        // empty
    }

    public void visibilityChanged( CDockable dockable ) {
        // empty
    }
}
