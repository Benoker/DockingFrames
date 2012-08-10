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

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * A {@link DockRegisterListener} that contains only empty methods.
 * @author Benjamin Sigg
 */
public class DockRegisterAdapter implements DockRegisterListener{
	public void registerStalled( DockController controller ){
		// do nothing
	}
	
	public void registerUnstalled( DockController controller ){
		// do nothing	
	}
	
    public void dockStationRegistered( DockController controller, DockStation station ) {
        // do nothing
    }

    public void dockStationRegistering( DockController controller, DockStation station ) {
        // do nothing
    }

    public void dockStationUnregistered( DockController controller, DockStation station ) {
        // do nothing
    }

    public void dockableCycledRegister( DockController controller, Dockable dockable ) {
        // do nothing
    }

    public void dockableRegistered( DockController controller, Dockable dockable ) {
        // do nothing
    }

    public void dockableRegistering( DockController controller, Dockable dockable ) {
        // do nothing
    }

    public void dockableUnregistered( DockController controller, Dockable dockable ) {
        // do nothing
    }
}
