/**
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

package bibliothek.gui.dock.station.support;

import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.StackDockStation;

/**
 * A simple implementation of {@link Combiner}, which merges two {@link Dockable Dockables}
 * by creating a {@link StackDockStation}, and putting the children onto this
 * station.<br>
 * If the argument <code>parent</code> in {@link #combine(Dockable, Dockable, DockStation) combiner}
 * is a {@link Dockable}, then it's title and icon are copied onto the new
 * station.
 * @author Benjamin Sigg
 */
public class DefaultCombiner implements Combiner {
	public Dockable combine( Dockable old, Dockable drop, DockStation parent ) {
        StackDockStation stack = new StackDockStation( parent.getTheme() );
        
        stack.drop( old );
        stack.drop( drop );
        
        return stack;
    }
}
