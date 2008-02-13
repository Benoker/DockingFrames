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
package bibliothek.gui.dock.station.stack;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.layout.DockLayout;

/**
 * A layout of a {@link StackDockStation}, stores the order of all {@link Dockable}s
 * on the station.
 * @author Benjamin Sigg
 */
public class StackDockStationLayout implements DockLayout {
    /** the order of the chilren */
    private int[] children;
    
    /** the name of the selected child */
    private int selected;
    
    /** the id of the factory of this layout */
    private String factory;
    
    /**
     * Creates a new layout.
     * @param selected the name of the selected child
     * @param children the order of the children. The entries of the array
     * are the identifiers that a {@link DockFactory} gets in its method
     * {@link DockFactory#getLayout(bibliothek.gui.dock.DockElement, java.util.Map)}
     */
    public StackDockStationLayout( int selected, int[] children ){
        this.selected = selected;
        this.children = children;
    }
    
    public String getFactoryID() {
        return factory;
    }
    
    public void setFactoryID( String id ) {
        factory = id;
    }
    
    /**
     * Gets the order of the children.
     * @return the order
     */
    public int[] getChildren() {
        return children;
    }
    
    /**
     * Gets the name of the selected child.
     * @return the selected child
     */
    public int getSelected() {
        return selected;
    }
}
