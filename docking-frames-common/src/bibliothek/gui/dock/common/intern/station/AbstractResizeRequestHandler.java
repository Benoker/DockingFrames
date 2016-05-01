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
import bibliothek.gui.dock.common.layout.RequestDimension;
import bibliothek.util.FrameworkOnly;

/**
 * A listener to a {@link CControl} that provides useful methods for subclasses.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public abstract class AbstractResizeRequestHandler implements ResizeRequestListener{
    
    /**
     * Searches the size request of <code>dockable</code>.
     * @param dockable some element
     * @return the size request or <code>null</code>
     */
    protected RequestDimension getAndClearResizeRequest( Dockable dockable ){
        if( dockable instanceof CommonDockable ){
            CDockable cdock = ((CommonDockable)dockable).getDockable();
            RequestDimension result = cdock.getAndClearResizeRequest();
            if( result == null )
                return null;
            
            return result.clone();
        }
        if( dockable instanceof StackDockStation ){
            RequestDimension max = new RequestDimension();
            StackDockStation station = (StackDockStation)dockable;
            
            for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
                RequestDimension check = getAndClearResizeRequest( station.getDockable( i ) );
                if( check != null ){
                    Dimension sizeDockable = station.getDockable( i ).getComponent().getSize();
                    Dimension sizeStation = station.getComponent().getSize();
                    
                    if( check.isWidthSet() ){
                        max.setWidth( Math.max( max.getWidth(), check.getWidth() + sizeStation.width - sizeDockable.width  ) );
                    }
                    if( check.isHeightSet() ){
                        max.setHeight( Math.max( max.getHeight(), check.getHeight() + sizeStation.height - sizeDockable.height ) );
                    }
                }
            }
            
            if( max.isHeightSet() || max.isWidthSet() )
                return max;
        }
        return null;
    }
}
