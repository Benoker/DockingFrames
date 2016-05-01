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

package bibliothek.gui.dock;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.component.DockComponentRoot;


/**
 * An element in the hierarchy of dockables and stations. Classes implementing this interface
 * must either be a {@link Dockable}, a {@link DockStation} or both of them.
 * @author Benjamin Sigg
 */
public interface DockElement extends DockComponentRoot{
	/**
     * Returns <code>this</code> if <code>this</code> is an instance of 
     * {@link Dockable}. Otherwise <code>null</code> is returned.
     * @return <code>this</code> or <code>null</code>. Must not return <code>null</code> if {@link #asDockable()}
     * already returns <code>null</code>.
     */
    public Dockable asDockable();

    /**
     * Returns <code>this</code> if <code>this</code> is an instance of 
     * {@link DockStation}. Otherwise <code>null</code> is returned.
     * @return <code>this</code> or <code>null</code>. Must not return <code>null</code> if {@link #asDockable()}
     * already returns <code>null</code>
     */
    public DockStation asDockStation();
    
    /**
     * Gets the controller that currently is associated with this {@link DockElement}.
     * @return the controller or <code>null</code>
     */
    public DockController getController();
    
    /**
     * Gets the unique name of the {@link DockFactory} which can read
     * and write elements of this type.
     * @return the id of the factory
     */
    public String getFactoryID();
}
