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


package bibliothek.gui.dock.themes.flat;

import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.StackDockStation;
import bibliothek.gui.dock.themes.FlatTheme;

/**
 * A {@link Combiner} normally used by the {@link FlatTheme} to replace the
 * default combiner.
 * @author Benjamin Sigg
 */
public class FlatCombiner implements Combiner{
    public Dockable combine( Dockable old, Dockable drop, DockStation parent ) {
        StackDockStation stack = createStackDockStation( parent.getTheme() );
        
        stack.setStackComponent( new FlatTab());
        stack.drop( old );
        stack.drop( drop );
        
        return stack;
    }
    
    /**
     * Creates a new {@link StackDockStation} which will be populated
     * with two {@link Dockable Dockables}.
     * @param theme The theme that the station will have, might be <code>null</code>
     * @return the new station
     */
    protected StackDockStation createStackDockStation( DockTheme theme ){
        return new StackDockStation( theme );
    }
}
