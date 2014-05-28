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
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.common.layout.RequestDimension;
import bibliothek.gui.dock.facile.station.split.ConflictResolver;
import bibliothek.gui.dock.facile.station.split.LockedResizeLayoutManager;
import bibliothek.gui.dock.facile.station.split.ResizeRequest;
import bibliothek.gui.dock.station.split.Leaf;
import bibliothek.gui.dock.station.split.Root;
import bibliothek.gui.dock.station.split.SplitLayoutManager;
import bibliothek.util.FrameworkOnly;

/**
 * A {@link LockedResizeLayoutManager} that looks out for 
 * {@link CDockable#isResizeLockedVertically()} and {@link CDockable#isResizeLockedHorizontally()}.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class CLockedResizeLayoutManager extends LockedResizeLayoutManager<RequestDimension> {
    /** the control in whose realm this manager works */
    private CControl control;
    
    /**
     * Creates a new layout manager
     */
    public CLockedResizeLayoutManager(){
        // nothing to do
    }

    /**
     * Creates a new layout manager
     * @param control the control in whose realm this manager works
     */
    public CLockedResizeLayoutManager( CControl control ){
        this.control = control;
    }
    
    /**
     * Creates a new layout manager using <code>delegate</code> as 
     * delegate for all tasks that have to be carried out.
     * @param delegate the delegate used for standard tasks to carry out. 
     */
    public CLockedResizeLayoutManager( SplitLayoutManager delegate ){
        super( delegate );
    }
    
    /**
     * Sets the control in whose realm this manager should work.
     * @param control the control, can be <code>null</code>
     */
    public void setControl( CControl control ){
        this.control = control;
    }
    
    @Override
    public ConflictResolver<RequestDimension> getConflictResolver() {
        if( control != null )
            return control.getProperty( CControl.RESIZE_LOCK_CONFLICT_RESOLVER );
        
        return super.getConflictResolver();
    }
    
    @Override
    public ResizeRequest getRequest( RequestDimension size, Leaf leaf ) {
        if( size != null ){
            Rectangle modified = leaf.getCurrentBounds();
            
            // +0.1: to work against a later integer conversion that might round down
            double deltaWidth = size.getWidth() + 0.001 - modified.width;
            double deltaHeight = size.getHeight() + 0.001 - modified.height;
            
            Root root = leaf.getRoot();
            deltaWidth /= root.getWidthFactor();
            deltaHeight /= root.getHeightFactor();
            
            return new ResizeRequest(
                    deltaWidth, 
                    deltaHeight,
                    size.isWidthSet() ? 1 : -1,
                    size.isHeightSet() ? 1 : -1 );
        }
        return null;
    }

    @Override
    public RequestDimension prepareResize( Leaf leaf ) {
        boolean lockedWidth = isLockedHorizontally( leaf.getDockable() );
        boolean lockedHeight = isLockedVertically( leaf.getDockable() );
        
        if( !lockedWidth && !lockedHeight )
            return null;
        
        RequestDimension request = new RequestDimension();
        Rectangle bounds = leaf.getCurrentBounds();

        if( lockedWidth ){
            double width = leaf.getWidth();
            if( width > 0 ){
                request.setWidth( bounds.width );
            }
        }
        
        if( lockedHeight ){
            double height = leaf.getHeight();
            if( height > 0 ){
                request.setHeight( bounds.height );
            }
        }
        
        return request;
    }

    /**
     * Checks whether <code>dockable</code>s height is locked.
     * @param dockable the element to test
     * @return <code>true</code> if the height is locked
     */
    private boolean isLockedVertically( Dockable dockable ){
        if( dockable instanceof CommonDockable ){
            CDockable cdock = ((CommonDockable)dockable).getDockable();
            return cdock.isResizeLockedVertically();
        }
        if( dockable != null && dockable.asDockStation() instanceof StackDockStation ){
            StackDockStation station = (StackDockStation)dockable.asDockStation();
            for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
                if( isLockedVertically( station.getDockable( i ) ))
                    return true;
            }
        }
        return false;
    }
    

    /**
     * Checks whether <code>dockable</code>s width is locked.
     * @param dockable the element to test
     * @return <code>true</code> if the width is locked
     */
    private boolean isLockedHorizontally( Dockable dockable ){
        if( dockable instanceof CommonDockable ){
            CDockable cdock = ((CommonDockable)dockable).getDockable();
            return cdock.isResizeLockedHorizontally();
        }
        if( dockable != null && dockable.asDockStation() instanceof StackDockStation ){
            StackDockStation station = (StackDockStation)dockable.asDockStation();
            for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
                if( isLockedHorizontally( station.getDockable( i ) ))
                    return true;
            }
        }
        return false;
    }
}
