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
package bibliothek.gui.dock.common.intern;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.common.FControl;

/**
 * A {@link DockAcceptance} ensuring that the {@link FDockable#getExtendedMode() extended mode} property
 * of {@link FDockable} is respected on drag and drop operations.
 * @author Benjamin Sigg
 */
public class ExtendedModeAcceptance implements DockAcceptance {
    /** access to the {@link FControl} */
    private FControlAccess control;
    
    /**
     * Creates a new acceptance.
     * @param control access to the {@link FControl}
     */
    public ExtendedModeAcceptance( FControlAccess control ){
        this.control = control;
    }
    
    public boolean accept( DockStation parent, Dockable child ) {
        if( control.getStateManager().isOnTransition() )
            return true;
        
        if( child instanceof FacileDockable ){
            FDockable fdockable = ((FacileDockable)child).getDockable();
            FDockable.ExtendedMode mode = control.getStateManager().childsExtendedMode( parent );
            
            if( mode == null )
                return false;
            
            switch( mode ){
                case MINIMIZED:
                    return fdockable.isMinimizable();
                case EXTERNALIZED:
                    return fdockable.isExternalizable();
            }
        }
        
        return true;
    }

    public boolean accept( DockStation parent, Dockable child, Dockable next ) {
        return accept( parent, next );
    }
}
