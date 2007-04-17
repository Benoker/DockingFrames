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

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.title.DockTitle;

/**
 * An abstract implementation of {@link DockableListener}, {@link DockStationListener}
 * and {@link DockControllerListener}. All methods of this class are empty.
 * This class can be used if one or more of the mentioned interfaces should be implemented,
 * but only a few methods have actually to do something
 * @author Benjamin Sigg
 *
 */
public abstract class DockAdapter implements DockableListener, DockControllerListener, DockStationListener{

    public void titleBinded( Dockable dockable, DockTitle title ) {
        // do nothing
    }

    public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ) {
        // do nothing        
    }

    public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ) {
        // do nothing        
    }

    public void titleUnbinded( Dockable dockable, DockTitle title ) {
        // do nothing
    }

    public void dockStationUnregistered( DockController controller, DockStation station ) {
        // do nothing
    }

    public void dockStationRegistered( DockController controller, DockStation station ) {
        // do nothing
    }

    public void dockStationRegistering( DockController controller, DockStation station ) {
        // do nothing
    }

    public void dockableUnregistered( DockController controller, Dockable dockable ) {
        // do nothing
    }

    public void dockableDrag( DockController controller, Dockable dockable, DockStation station ) {
        // do nothing
    }

    public void dockableFocused( DockController controller, Dockable dockable ) {
        // do nothing   
    }

    public void dockablePut( DockController controller, Dockable dockable, DockStation station ) {
        // do nothing
    }

    public void dockableRegistered( DockController controller, Dockable dockable ) {
        // do nothing
    }

    public void dockableRegistering( DockController controller, Dockable dockable ) {
        // do nothing   
    }

    public void titleBinded( DockController controller, DockTitle title, Dockable dockable ) {
        // do nothing
    }

    public void titleUnbinded( DockController controller, DockTitle title, Dockable dockable ) {
        // do nothing
    }

    public void dockableAdded( DockStation station, Dockable dockable ) {
        // do nothing        
    }

    public void dockableAdding( DockStation station, Dockable dockable ) {
        // do nothing
    }

    public void dockableRemoved( DockStation station, Dockable dockable ) {
        // do nothing
    }

    public void dockableRemoving( DockStation station, Dockable dockable ) {
        // do nothing   
    }

    public void dockableVisibiltySet( DockStation station, Dockable dockable, boolean visible ) {
        // do nothing
    }
}
