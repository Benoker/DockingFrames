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
package bibliothek.gui.dock.common.intern.ui;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.util.FrameworkOnly;

/**
 * A {@link DockAcceptance} ensuring that the {@link CDockable#isStackable()}
 * property is respected.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class StackableAcceptance implements DockAcceptance {
    
    public boolean accept( DockStation parent, Dockable child ) {
        if( parent instanceof StackDockStation ){
            if( child instanceof CommonDockable ){
                if( !((CommonDockable)child).getDockable().isStackable() )
                    return false;
            }
        }
        
        return true;
    }
    
    public boolean accept( DockStation parent, Dockable child, Dockable next ) {
        if( child instanceof CommonDockable ){
            if( !((CommonDockable)child).getDockable().isStackable() )
                return false;
        }
        if( next instanceof CommonDockable ){
            if( !((CommonDockable)next).getDockable().isStackable() )
                return false;
        }
        return true;
    }
}
