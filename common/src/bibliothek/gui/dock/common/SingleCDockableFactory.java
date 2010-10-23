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
package bibliothek.gui.dock.common;


/**
 * A backup factory is used by a {@link CControl} to create {@link SingleCDockable}s
 * if a dockable is missing in the cache, but needed because some layout is loaded
 * from a file.
 * @author Benjamin Sigg
 */
public interface SingleCDockableFactory {
    /**
     * Creates a backup of a {@link SingleCDockable}.
     * @param id the unique id that the result must have
     * @return the backup dockable or <code>null</code> if no dockable can
     * be created
     */
    public SingleCDockable createBackup( String id );
}
