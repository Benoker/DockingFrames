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

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
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

    public void titleBound( Dockable dockable, DockTitle title ) {
        // do nothing
    }

    public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ) {
        // do nothing        
    }

    public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ) {
        // do nothing        
    }

    public void titleUnbound( Dockable dockable, DockTitle title ) {
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
    
    public void dockableCycledRegister( DockController controller, Dockable dockable ) {
        // ignore 
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

    public void titleBound( DockController controller, DockTitle title, Dockable dockable ) {
        // do nothing
    }

    public void titleUnbound( DockController controller, DockTitle title, Dockable dockable ) {
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
