/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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

import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.StackDockStation;

/**
 * Information about the layout of a {@link StackDockStation} read from an old format.
 * This class only remains for backwards compatibility and should not be used by clients.
 * @author Benjamin Sigg
 */
public class RetroStackDockStationLayout extends StackDockStationLayout {
    /** the order of the children */
    private int[] children;
    
    /**
     * Creates a new layout
     * @param selected the name of the item that is selected
     * @param children the order of the children. The entries of the array
     * are the identifiers that a {@link DockFactory} gets in its method
     * {@link DockFactory#getLayout(bibliothek.gui.dock.DockElement, java.util.Map)}
     */
    public RetroStackDockStationLayout( int selected, int[] children ){
    	super( selected, null );
    	this.children = children;
    }
    
    /**
     * Gets the order of the children.
     * @return the order
     */
    public int[] getChildren() {
        return children;
    }
}
