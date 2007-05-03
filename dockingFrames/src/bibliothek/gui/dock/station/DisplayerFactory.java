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

import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A factory that creates instances of {@link DockableDisplayer}.
 * @author Benjamin Sigg
 *
 */
public interface DisplayerFactory {
    /**
     * Creates a new {@link DockableDisplayer} which will be shown
     * on <code>station</code>, will have <code>dockable</code> as
     * child and will display the <code>title</code>.
     * @param station the parent of the created displayer
     * @param dockable the child of the created displayer
     * @param title the title for the displayer
     * @return the newly created displayer
     */
    public DockableDisplayer create( DockStation station, Dockable dockable, DockTitle title );
}
