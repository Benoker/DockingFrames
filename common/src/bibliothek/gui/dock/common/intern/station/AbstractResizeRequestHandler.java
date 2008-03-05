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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.event.ResizeRequestListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;

/**
 * A listener to a {@link CControl} that provides useful methods for subclasses.
 * @author Benjamin Sigg
 */
public abstract class AbstractResizeRequestHandler implements ResizeRequestListener{
    
    /**
     * Searches the size request of <code>dockable</code>.
     * @param dockable some element
     * @return the size request or <code>null</code>
     */
    protected Dimension getAndClearResizeRequest( Dockable dockable ){
        if( dockable instanceof CommonDockable ){
            CDockable cdock = ((CommonDockable)dockable).getDockable();
            Dimension result = cdock.getAndClearResizeRequest();
            if( result == null )
                return null;
            
            return new Dimension( result );
        }
        if( dockable instanceof StackDockStation ){
            Dimension max = new Dimension( -1, -1 );
            StackDockStation station = (StackDockStation)dockable;
            
            for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
                Dimension check = getAndClearResizeRequest( station.getDockable( i ) );
                if( check != null ){
                    Dimension sizeDockable = station.getDockable( i ).getComponent().getSize();
                    Dimension sizeStation = station.getComponent().getSize();
                    
                    check = new Dimension( check );
                    check.width += sizeStation.width - sizeDockable.width;
                    check.height += sizeStation.height - sizeDockable.height;
                    
                    max.width = Math.max( max.width, check.width );
                    max.height = Math.max( max.height, check.height );
                }
            }
            
            if( max.width != -1 && max.height != -1 )
                return max;
        }
        return null;
    }
}
