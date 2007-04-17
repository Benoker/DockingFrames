/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.themes.nostack;

import bibliothek.gui.dock.DockAcceptance;
import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.accept.AbstractAcceptance;
import bibliothek.gui.dock.station.StackDockStation;

/**
 * A {@link DockAcceptance} which permits the user to set a  
 * {@link StackDockStation} into another <code>StackDockStation</code>.
 * @author Benjamin Sigg
 */
public class NoStackAcceptance extends AbstractAcceptance{
    @Override
    public boolean accept( DockStation parent, Dockable child ) {
        if( parent instanceof StackDockStation )
            return !(child.asDockStation() instanceof StackDockStation);
        
        return true;
    }

    @Override
    public boolean accept( DockStation parent, Dockable child, Dockable next ) {
        if( parent instanceof StackDockStation )
            return false;
        
        if( child instanceof StackDockStation )
            return false;
        
        if( next instanceof StackDockStation )
            return false;
        
        return true;
    }
}
