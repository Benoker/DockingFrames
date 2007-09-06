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

package bibliothek.gui.dock.station;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * A <code>Combiner</code> merges two {@link Dockable Dockables} into
 * one {@link Dockable}. How this is done, is up to the Combiner, but
 * most Combiners will create a new {@link DockStation} and put the Dockables
 * onto this new station.
 */
public interface Combiner {
    /**
     * Merges the Dockable <code>old</code> and <code>drop</code> into 
     * a new Dockable.
     * @param old a Dockable which was sitting on the DockStation <code>parent</code>.
     * The parent of <code>old</code> is currently set to <code>null</code>.
     * @param drop a Dockable that has currently no parent, and that was
     * dragged over <code>old</code>
     * @param parent a DockStation which will become the parent of the
     * result of this method
     * @return The combination of <code>old</code> and <code>drop</code>
     */
    public Dockable combine( Dockable old, Dockable drop, DockStation parent );
}
