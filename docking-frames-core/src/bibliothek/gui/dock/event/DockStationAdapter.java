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

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * An abstract implementation of {@link DockStationListener}.
 * All methods of this class are empty. The class can be used instead of
 * {@link DockStationListener} if only a few methods have to be implemented.
 * @author Benjamin Sigg
 *
 */
public abstract class DockStationAdapter implements DockStationListener {

    public void dockableAdding( DockStation station, Dockable dockable ) {
        // do nothing
    }

    public void dockableRemoving( DockStation station, Dockable dockable ) {
        // do nothing
    }

    public void dockableAdded( DockStation station, Dockable dockable ) {
        // do nothing
    }

    public void dockableRemoved( DockStation station, Dockable dockable ) {
        // do nothing
    }
    
    public void dockableShowingChanged( DockStation station, Dockable dockable, boolean visible ) {
        // do nothing
    }
    
    public void dockableSelected( DockStation station, Dockable oldSelection, Dockable newSelection ) {
        // do nothing
    }
    
    public void dockablesRepositioned( DockStation station, Dockable[] dockables ){
    	// do nothing
    }
}
