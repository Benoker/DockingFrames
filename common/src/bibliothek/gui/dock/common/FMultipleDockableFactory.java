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

import bibliothek.gui.dock.common.intern.FDockable;

/**
 * A factory that can create and store {@link FMultipleDockable}s. This factory
 * converts a {@link FMultipleDockable} in a {@link FMultipleDockableLayout} and
 * then writes the layout in various forms (like xml).
 * @author Benjamin Sigg
 *
 */
public interface FMultipleDockableFactory<F extends FMultipleDockable, L extends FMultipleDockableLayout> {
    /**
     * Collects all the properties of <code>dockable</code> and writes them
     * into a new {@link FMultipleDockableLayout}.
     * @param dockable the element whose properties should be collected
     * @return the layout that has been written
     */
    public L write( F dockable );
    
    /**
     * Creates a {@link FMultipleDockable} that gets its layout from <code>layout</code>.
     * @param layout the set of properties that can be used to create the new
     * {@link FDockable}.
     * @return the new dockable or <code>null</code> if the layout can't be read
     */
    public F read( L layout );
    
    /**
     * Creates a new, empty layout. The contents of the layout will be set
     * using one of the <code>read</code>-methods of {@link FMultipleDockableLayout}.
     * @return the new empty layout
     */
    public L create();
}
