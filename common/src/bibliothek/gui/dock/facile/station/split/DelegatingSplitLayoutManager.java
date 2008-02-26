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
package bibliothek.gui.dock.facile.station.split;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.split.*;

/**
 * A {@link SplitLayoutManager} which delegates all its work to another manager.
 * This class is intended to be subclassed in order to override some methods.
 * @author Benjamin Sigg
 */
public class DelegatingSplitLayoutManager implements SplitLayoutManager {
    private SplitLayoutManager delegate;
    
    /**
     * Creates a new manager
     * @param delegate the manager whose methods will be called unless a
     * subclass has overridden them.
     */
    public DelegatingSplitLayoutManager( SplitLayoutManager delegate ){
        if( delegate == null )
            throw new NullPointerException( "delegate must not be null" );
        this.delegate = delegate;
    }
    
    public void calculateDivider( SplitDockStation station, PutInfo putInfo, Leaf origin ) {
        delegate.calculateDivider( station, putInfo, origin );
    }

    public void install( SplitDockStation station ) {
        delegate.install( station );
    }

    public PutInfo prepareDrop( SplitDockStation station, int x, int y, int titleX, int titleY, boolean checkOverrideZone, Dockable dockable ) {
        return delegate.prepareDrop( station, x, y, titleX, titleY, checkOverrideZone, dockable );
    }

    public PutInfo prepareMove( SplitDockStation station, int x, int y, int titleX, int titleY, boolean checkOverrideZone, Dockable dockable ) {
        return delegate.prepareMove( station, x, y, titleX, titleY, checkOverrideZone, dockable );
    }

    public void uninstall( SplitDockStation station ) {
        delegate.uninstall( station );
    }

    public void updateBounds( Root root, double x, double y, double width, double height, double factorW, double factorH ) {
        delegate.updateBounds( root, x, y, width, height, factorW, factorH );
    }

    public double validateDivider( SplitDockStation station, double divider, Node node ) {
        return delegate.validateDivider( station, divider, node );
    }

    public PutInfo validatePutInfo( SplitDockStation station, PutInfo info ) {
        return delegate.validatePutInfo( station, info );
    }

    public Dockable willMakeFullscreen( SplitDockStation station, Dockable dockable ) {
        return delegate.willMakeFullscreen( station, dockable );
    }

}
