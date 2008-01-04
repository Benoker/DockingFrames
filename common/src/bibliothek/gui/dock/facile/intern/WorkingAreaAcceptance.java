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

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.facile.FWorkingArea;

/**
 * A {@link DockAcceptance} ensuring that the {@link FDockable#getWorkingArea()}
 * property is respected.
 * @author Benjamin Sigg
 *
 */
public class WorkingAreaAcceptance implements DockAcceptance {
    public boolean accept( DockStation parent, Dockable child ) {
        FWorkingArea area = searchArea( parent );
        return match( area, child );
    }

    public boolean accept( DockStation parent, Dockable child, Dockable next ) {
        FWorkingArea area = searchArea( parent );
        return match( area, next );
    }
    
    /**
     * Searches the first {@link FWorkingArea} in the path to the root.
     * @param element some element
     * @return the first {@link FWorkingArea} that occurs on the path from
     * <code>element</code> to the root.
     */
    private FWorkingArea searchArea( DockElement element ){
        Dockable dockable = element.asDockable();
        while( dockable != null ){
            if( dockable instanceof FacileDockable ){
                FDockable fdock = ((FacileDockable)dockable).getDockable();
                if( fdock instanceof FWorkingArea )
                    return (FWorkingArea)fdock;
            }
            DockStation station = dockable.getDockParent();
            dockable = station == null ? null : station.asDockable();
        }
        return null;
    }
    
    /**
     * Checks all {@link FDockable}s and compares their
     * {@link FDockable#getWorkingArea() working area}
     * with <code>area</code>.
     * @param area a possible new parent
     * @param dockable the root of the tree of elements to test
     * @return <code>true</code> if all elements have <code>area</code> as
     * preferred parent, <code>false</code> otherwise
     */
    private boolean match( FWorkingArea area, Dockable dockable ){
        if( dockable instanceof FacileDockable ){
            FDockable fdockable = ((FacileDockable)dockable).getDockable();
            FWorkingArea request = fdockable.getWorkingArea();
            if( request != area )
                return false;
            
            if( fdockable instanceof FWorkingArea )
                return true;
        }
        
        DockStation station = dockable.asDockStation();
        if( station != null )
            return match( area, station );
        else
            return true;
    }
    
    /**
     * Checks all {@link FDockable}s and compares their
     * {@link FDockable#getWorkingArea() working area}
     * with <code>area</code>.
     * @param area a possible new parent
     * @param station the root of the tree of elements to test
     * @return <code>true</code> if all elements have <code>area</code> as
     * preferred parent, <code>false</code> otherwise
     */
    private boolean match( FWorkingArea area, DockStation station ){
        for( int i = 0, n = station.getDockableCount(); i < n; i++ ){
            boolean result = match( area, station.getDockable( i ));
            if( !result )
                return false;
        }
        return true;
    }
}
