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
import java.awt.Insets;

import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.layout.RequestDimension;
import bibliothek.gui.dock.station.split.Leaf;
import bibliothek.gui.dock.station.split.Root;
import bibliothek.gui.dock.station.split.SplitLayoutManager;

/**
 * A handle that can be used to change the layout of a {@link SplitDockStation}
 * such that the {@link Dimension} of {@link CDockable#getAndClearResizeRequest()}
 * is more or less respected.
 * @author Benjamin Sigg
 */
public class SplitResizeRequestHandler extends AbstractResizeRequestHandler{
    /** the station which will be updated by this handler */
    private SplitDockStation station;
    
    /**
     * Creates a new handler.
     * @param station the station which will be updated by this handler
     */
    public SplitResizeRequestHandler( SplitDockStation station ){
        this.station = station;
    }
    
    public void handleResizeRequest( CControl control ) {
        SplitLayoutManager oldManager = station.getSplitLayoutManager();
        LayoutManager layout = new LayoutManager();
        try{
            layout.setControl( control );
            station.setSplitLayoutManager( layout );
            station.updateBounds();
        }
        finally{
            layout.setControl( null );
            station.setSplitLayoutManager( oldManager );
        }
    }
    
    /**
     * A layout manager that respects the result of {@link CDockable#getAndClearResizeRequest()}.
     * @author Benjamin Sigg
     */
    private class LayoutManager extends CLockedResizeLayoutManager{
        @Override
        public void updateBounds( Root root, double x, double y, double factorW, double factorH ) {
            updateBoundsLocked( root, x, y, factorW, factorH );
        }
        @Override
        public RequestDimension prepareResize( Leaf leaf ) {
            RequestDimension request = getAndClearResizeRequest( leaf.getDockable() );
            if( request != null ){
                Insets insets = leaf.getDisplayer().getDockableInsets();
                
                if( request.isWidthSet() )
                    request.setWidth( request.getWidth() + insets.left + insets.right );
                
                if( request.isHeightSet() )
                    request.setHeight( request.getHeight() + insets.top + insets.bottom );
                
                return request;
            }
            
            return super.prepareResize( leaf );
        }
    }
}
