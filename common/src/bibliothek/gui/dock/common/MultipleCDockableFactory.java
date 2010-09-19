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

import bibliothek.gui.dock.common.event.CVetoClosingListener;
import bibliothek.gui.dock.common.intern.CDockable;

/**
 * A factory that can create and store {@link MultipleCDockable}s. This factory
 * converts a {@link MultipleCDockable} in a {@link MultipleCDockableLayout} and
 * then writes the layout in various forms (like xml).
 * @author Benjamin Sigg
 * @param <F> the kind of dockable that is managed by this factory
 * @param <L> the kind of meta-data this factory reads and writes about <code>F</code>
 */
public interface MultipleCDockableFactory<F extends MultipleCDockable, L extends MultipleCDockableLayout> {
    /**
     * Collects all the properties of <code>dockable</code> and writes them
     * into a new {@link MultipleCDockableLayout}.
     * @param dockable the element whose properties should be collected
     * @return the layout that has been written
     */
    public L write( F dockable );
    
    /**
     * Creates a {@link MultipleCDockable} that gets its layout from <code>layout</code>.
     * @param layout the set of properties that can be used to create the new
     * {@link CDockable}.
     * @return the new dockable or <code>null</code> if the layout can't be read
     */
    public F read( L layout );
    
    /**
     * Tells whether the meta-data <code>layout</code> belongs to <code>dockable</code>, meaning
     * <code>write( dockable )</code> would produce <code>layout</code> and <code>read( layout )</code>
     * would produce <code>dockable</code>.<br>
     * This method is used to create a pairing of dockables and meta-data. Dockables without partner are
     * most likely deleted. If a dockable or some meta-data has more than one potential partner, then one
     * pair is randomly chosen.<br>
     * This method is primarily used for optimization: assume <code>dockable</code> is shown or known
     * to the view and <code>layout</code> has been read from a file. Normally all dockables produced
     * by this factory would be removed and replaced by newly created dockables. If however this method
     * finds a match between a layout and a dockable, then the dockable can be reused.<br>
     * The second goal of this method is to help prevent unnecessary events to the {@link CVetoClosingListener}
     * for dockables that just get replaced by a "clone".<br>
     * @param dockable some element that is shown or known to the view
     * @param layout some layout that will be applied
     * @return <code>true</code> if <code>dockable</code> would be produced by {@link #read(MultipleCDockableLayout) read(layout)}.
     */
    public boolean match( F dockable, L layout );
    
    /**
     * Creates a new, empty layout. The contents of the layout will be set
     * using one of the <code>read</code>-methods of {@link MultipleCDockableLayout}.
     * @return the new empty layout
     */
    public L create();
}
