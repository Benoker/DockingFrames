/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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

package bibliothek.gui.dock.title;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * A factory for instances of {@link DockTitle}. The factory distinguishes
 * between titles for pure {@link Dockable Dockables} and titles for
 * Dockables which are also {@link DockStation DockStations}.
 * @author Benjamin Sigg
 */
public interface DockTitleFactory {
    /**
     * Creates a {@link DockTitle} for the pure <code>dockable</code>. 
     * @param dockable the owner of the title
     * @param version the version which uses this factory, might be <code>null</code>
     * @return the new title, can be <code>null</code> if no title should
     * be shown for <code>dockable</code>. 
     */
    public DockTitle createDockableTitle( Dockable dockable, DockTitleVersion version );
    
    /**
     * Creates a {@link DockTitle} for <code>dockable</code> which is
     * also a {@link DockStation}.
     * @param dockable the owner of the title.
     * @param version the version which uses this factory, might be <code>null</code>
     * @param <D> the type of <code>dockable</code>.
     * @return the new title, can be <code>null</code> if no title
     * should be shown for <code>dockable</code>.
     */
    public <D extends Dockable & DockStation> DockTitle createStationTitle( D dockable, DockTitleVersion version );
}
