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
package bibliothek.gui.dock.common.intern.station;

import java.awt.Rectangle;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.facile.station.split.LockedResizeLayoutManager;
import bibliothek.gui.dock.station.split.Leaf;
import bibliothek.gui.dock.station.split.Root;

/**
 * A {@link LockedResizeLayoutManager} that looks out for {@link CDockable#isResizeLocked()}.
 * @author Benjamin Sigg
 */
public class CLockedResizeLayoutManager extends LockedResizeLayoutManager<Rectangle> {
    @Override
    protected ResizeRequest getRequest( Rectangle bounds, Leaf leaf ) {
        if( bounds != null ){
            Rectangle modified = leaf.getCurrentBounds();
            
            double deltaWidth = bounds.width - modified.width;
            double deltaHeight = bounds.height - modified.height;
            
            Root root = leaf.getRoot();
            deltaWidth /= root.getWidthFactor();
            deltaHeight /= root.getHeightFactor();
            
            return new ResizeRequest( deltaWidth, deltaHeight );
        }
        return null;
    }

    @Override
    protected Rectangle prepareResize( Leaf leaf ) {
        if( isLocked( leaf.getDockable() ))
            return leaf.getCurrentBounds();
        
        return null;
    }

    /**
     * Checks whether <code>dockable</code>s size is locked.
     * @param dockable the element to test
     * @return <code>true</code> if the size is locked
     */
    private boolean isLocked( Dockable dockable ){
        if( dockable instanceof CommonDockable ){
            CDockable cdock = ((CommonDockable)dockable).getDockable();
            return cdock.isResizeLocked();
        }
        if( dockable.asDockStation() instanceof StackDockStation ){
            StackDockStation station = (StackDockStation)dockable.asDockStation();
            for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
                if( isLocked( station.getDockable( i ) ))
                    return true;
            }
        }
        return false;
    }
}
