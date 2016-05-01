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
package bibliothek.gui.dock.common.event;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.CDockable;

/**
 * A {@link CFocusListener} gets informed when a {@link CDockable} gains or
 * looses the focus, or is selected or deselected by its parent.
 * @author Benjamin Sigg
 * @see CDockable#addFocusListener(CFocusListener)
 * @see CDockable#removeFocusListener(CFocusListener)
 * @see CControl#addFocusListener(CFocusListener)
 * @see CControl#removeFocusListener(CFocusListener)
 */
public interface CFocusListener {
    /**
     * Called when <code>dockable</code> gains the focus.
     * @param dockable the element which just gained the focus
     */
    public void focusGained( CDockable dockable );
    
    /**
     * Called when <code>dockable</code> lost its focus.
     * @param dockable the element which just lost the focus
     */
    public void focusLost( CDockable dockable );
}
