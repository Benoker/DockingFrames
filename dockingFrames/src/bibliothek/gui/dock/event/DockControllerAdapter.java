/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.event;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.title.DockTitle;

/**
 * An abstract implementation of {@link DockControllerListener}. All
 * methods of this class are empty. This class can be used instead of
 * {@link DockControllerListener} when only a few selected methods
 * have to be implemented.
 * @author Benjamin Sigg
 *
 */
public abstract class DockControllerAdapter implements DockControllerListener {

    public void dockableRegistered( DockController controller, Dockable dockable ) {
        // do nothing
    }

    public void dockStationRegistered( DockController controller,
            DockStation station ) {
        // do nothing
    }

    public void dockableRegistering( DockController controller,
            Dockable dockable ) {
        // do nothing
    }

    public void dockStationRegistering( DockController controller,
            DockStation station ) {
        // do nothing
    }

    public void dockableUnregistered( DockController controller,
            Dockable dockable ) {
        // do nothing
    }

    public void dockStationUnregistered( DockController controller,
            DockStation station ) {
        // do nothing
    }

    public void dockableDrag( DockController controller, Dockable dockable, DockStation station ) {
        // do nothing
    }
    
    public void dockablePut( DockController controller, Dockable dockable,
            DockStation station ) {
        // do nothing
    }

    public void titleBinded( DockController controller, DockTitle title,
            Dockable dockable ) {
        // do nothing
    }

    public void titleUnbinded( DockController controller, DockTitle title,
            Dockable dockable ) {
        // do nothing
    }
    
    public void dockableFocused( DockController controller, Dockable dockable ) {
        // do nothing
    }
}
