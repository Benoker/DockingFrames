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
package bibliothek.gui.dock.facile.intern;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.action.StateManager;
import bibliothek.gui.dock.facile.FDockable;

/**
 * A manager that can change the extended-state of {@link FDockable}s
 * @author Benjamin Sigg
 *
 */
public class FStateManager extends StateManager {
    /**
     * Creates a new manager
     * @param controller the controller to observe
     */
    public FStateManager( DockController controller ){
        super( controller );
    }
    
    /**
     * Changes the mode of <code>dockable</code>.
     * @param dockable an element whose mode will be changed
     * @param mode the new mode
     */
    public void setMode( Dockable dockable, FDockable.ExtendedMode mode ) {
        switch( mode ){
            case EXTERNALIZED:
                setMode( dockable, EXTERNALIZED );
                break;
            case MAXIMIZED:
                setMode( dockable, MAXIMIZED );
                break;
            case MINIMIZED:
                setMode( dockable, MINIMIZED );
                break;
            case NORMALIZED:
                setMode( dockable, NORMALIZED );
                break;
        }
    }
    
    /**
     * Gets the mode <code>dockable</code> is currently into.
     * @param dockable the questioned element
     * @return the mode of <code>dockable</code>
     */
    public FDockable.ExtendedMode getMode( Dockable dockable ){
        String mode = currentMode( dockable );
        
        if( EXTERNALIZED.equals( mode ))
            return FDockable.ExtendedMode.EXTERNALIZED;
        else if( MINIMIZED.equals( mode ))
            return FDockable.ExtendedMode.MINIMIZED;
        else if( MAXIMIZED.equals( mode ))
            return FDockable.ExtendedMode.MAXIMIZED;
        else if( NORMALIZED.equals( mode ))
            return FDockable.ExtendedMode.NORMALIZED;
        
        return null;
    }
    
    @Override
    public void rebuild( Dockable dockable ) {
        super.rebuild( dockable );
    }
}
