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
package bibliothek.gui.dock.focus;

import bibliothek.gui.Dockable;

/**
 * This listener is added to a {@link DockableSelection} and gets informed
 * when the user selects an element or cancels the selection.
 * @author Benjamin Sigg
 */
public interface DockableSelectionListener {
    /**
     * Called when an element is selected, but the selection is not yet confirmed.
     * @param dockable the currently selected element, can be <code>null</code>
     */
    public void considering( Dockable dockable );
    
    /**
     * Called when an element is selected and the selection confirmed.
     * @param dockable the selected element, not <code>null</code>
     */
    public void selected( Dockable dockable );
    
    /**
     * Called when the user cancels the operation.
     */
    public void canceled();
}
