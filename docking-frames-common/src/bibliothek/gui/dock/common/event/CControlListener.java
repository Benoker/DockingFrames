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
 * A listener to a {@link CControl}, gets informed when {@link CDockable}s
 * are added or removed, opened or closed.
 * @author Benjamin Sigg
 */
public interface CControlListener {
    /**
     * Called when <code>dockable</code> has been made public.
     * @param control the caller
     * @param dockable the element that is now known
     */
    public void added( CControl control, CDockable dockable );
    
    /**
     * Called when <code>dockable</code> has been removed.
     * @param control the caller
     * @param dockable the element that is no longer known
     */
    public void removed( CControl control, CDockable dockable );
    
    /**
     * Called when <code>dockable</code> has been made visible.
     * @param control the caller
     * @param dockable the element that is now visible
     * @see CDockable#isVisible()
     */
    public void opened( CControl control, CDockable dockable );
    
    /**
     * Called when <code>dockable</code> has been made invisible.
     * @param control the caller
     * @param dockable the element that is no longer visible
     * @see CDockable#isVisible()
     */
    public void closed( CControl control, CDockable dockable );
}
