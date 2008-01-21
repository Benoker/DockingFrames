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

package bibliothek.gui.dock.event;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * A listener added to a {@link DockController}, this listener gets informed
 * when the focused {@link Dockable} is exchanged.
 * @author Benjamin Sigg
 */
public interface DockableFocusListener {
    /**
     * Invoked when <code>dockable</code> has gained the focus. 
     * @param controller the origin of the event
     * @param dockable the {@link Dockable} which is now focused, can be <code>null</code>
     */
    public void dockableFocused( DockController controller, Dockable dockable );
    
    /**
     * Called when <code>station</code> changes its selected <code>dockable</code>.
     * @param controller the controller in whose realm the event occurred
     * @param station some {@link DockStation}
     * @param dockable the currently selected element on <code>station</code>,
     * can be <code>null</code>
     * @see DockStation#getFrontDockable()
     */
    public void dockableSelected( DockController controller, DockStation station, Dockable dockable );
}
