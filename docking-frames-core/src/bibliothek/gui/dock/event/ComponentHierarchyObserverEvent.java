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

import java.awt.Component;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.control.ComponentHierarchyObserver;

/**
 * An event telling that {@link Component}s have been added or removed from
 * a {@link ComponentHierarchyObserver}.
 * @author Benjamin Sigg
 *
 */
public class ComponentHierarchyObserverEvent {
    private DockController source;
    private List<Component> components;
    
    /**
     * Creates a new event
     * @param source the source of the event
     * @param components the components that have been added or removed
     */
    public ComponentHierarchyObserverEvent( DockController source, List<Component> components ){
        this.source = source;
        this.components = components;
    }
    
    /**
     * Gets the source of the event.
     * @return the source
     */
    public DockController getSource() {
        return source;
    }
    
    /**
     * Gets a list that contains all the components that were added or removed.
     * @return the new or lost components
     */
    public List<Component> getComponents() {
        return components;
    }
}
