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

import java.awt.Dimension;
import java.awt.Rectangle;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.facile.station.split.LockedResizeLayoutManager;
import bibliothek.gui.dock.station.split.Leaf;
import bibliothek.gui.dock.station.split.Root;
import bibliothek.gui.dock.station.split.SplitLayoutManager;

/**
 * A {@link LockedResizeLayoutManager} that looks out for {@link CDockable#isResizeLocked()}.
 * @author Benjamin Sigg
 */
public class CLockedResizeLayoutManager extends LockedResizeLayoutManager<Dimension> {
    /**
     * Creates a new layout manager
     */
    public CLockedResizeLayoutManager(){
        // nothing to do
    }
    
    /**
     * Creates a new layout manager using <code>delegate</code> as 
     * delegate for all tasks that have to be carried out.
     * @param delegate the delegate used for standard tasks to carry out. 
     */
    public CLockedResizeLayoutManager( SplitLayoutManager delegate ){
        super( delegate );
    }
    
    @Override
    protected ResizeRequest getRequest( Dimension size, Leaf leaf ) {
        if( size != null ){
            Rectangle modified = leaf.getCurrentBounds();
            
            // +0.1: to work against a later integer conversion that might round down
            double deltaWidth = size.width + 0.001 - modified.width;
            double deltaHeight = size.height + 0.001 - modified.height;
            
            Root root = leaf.getRoot();
            deltaWidth /= root.getWidthFactor();
            deltaHeight /= root.getHeightFactor();
            
            return new ResizeRequest( deltaWidth, deltaHeight );
        }
        return null;
    }

    @Override
    protected Dimension prepareResize( Leaf leaf ) {
        if( isLocked( leaf.getDockable() )){
            if( leaf.getWidth() > 0 && leaf.getHeight() > 0 ){
                return leaf.getCurrentBounds().getSize();
            }
        }
        
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
