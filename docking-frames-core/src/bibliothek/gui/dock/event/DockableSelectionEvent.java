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
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * An event that tells which {@link Dockable} was selected on wich {@link DockStation}
 * @author Benjamin Sigg
 *
 */
public class DockableSelectionEvent {
    private DockController controller;
    private DockStation station;
    private Dockable oldSelected;
    private Dockable newSelected;
    
    /**
     * Creates a new event.
     * @param controller the controller in whose realm the change occurred
     * @param station the station on which the change occurred
     * @param oldSelected the {@link Dockable} that was selected before the change
     * @param newSelected the {@link Dockable} that is selected after the change
     */
    public DockableSelectionEvent( DockController controller, DockStation station, Dockable oldSelected, Dockable newSelected ){
        this.controller = controller;
        this.station = station;
        this.oldSelected = oldSelected;
        this.newSelected = newSelected;
    }
    
    /**
     * Gets the controller in whose realm the selection change occurred.
     * @return the controller
     */
    public DockController getController() {
        return controller;
    }
    
    /**
     * Gets the station whose selected {@link Dockable} changed.
     * @return the parent of the old and new selected {@link Dockable}
     */
    public DockStation getStation() {
        return station;
    }
    
    /**
     * Gets the element that was selected before the change.
     * @return the old selected element, can be <code>null</code>
     */
    public Dockable getOldSelected() {
        return oldSelected;
    }
    
    /**
     * Gets the element that is selected after the change. 
     * @return the newly selected element, can be <code>null</code>
     */
    public Dockable getNewSelected() {
        return newSelected;
    }
}
