/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
import bibliothek.gui.Dockable;

/**
 * An event describing the focus transfer from one {@link Dockable} to another.
 * @author Benjamin Sigg
 */
public class DockableFocusEvent {
    private DockController controller;
    private Dockable oldFocus;
    private Dockable newFocus;
    
    /**
     * Creates a new event.
     * @param controller the controller on which the change occurred
     * @param oldFocus the {@link Dockable} that was focused earlier, can be <code>null</code>
     * @param newFocus the {@link Dockable} that is focused now, can be <code>null</code>
     */
    public DockableFocusEvent( DockController controller, Dockable oldFocus, Dockable newFocus ){
        this.controller = controller;
        this.oldFocus = oldFocus;
        this.newFocus = newFocus;
    }
    
    /**
     * Gets the controller on which the focus change occurred.
     * @return the source
     */
    public DockController getController() {
        return controller;
    }
    
    /**
     * Gets the {@link Dockable} which had the focus before the change.
     * @return the old focus owner, can be <code>null</code>
     */
    public Dockable getOldFocusOwner() {
        return oldFocus;
    }
    
    /**
     * Gets the {@link Dockable} which has the focus after the change.
     * @return the new focus owner, can be <code>null</code>
     */
    public Dockable getNewFocusOwner() {
        return newFocus;
    }
}
