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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;

/**
 * A listener added to a {@link CDockable}, this listener will get informed
 * about state changes of {@link CDockable}.
 * @author Benjamin Sigg
 * @see CDockablePropertyListener
 * @see CDockableLocationListener
 */
public interface CDockableStateListener {
    /**
     * Called when the {@link CDockable#isVisible() visibility}-property
     * changed. Please read the notes of {@link CDockable#isVisible()} to
     * learn more about the exact meaning of visibility in the context of a 
     * {@link CDockable}.<br>
     * Clients interested to know whether the user can see the {@link Dockable} or not should use
     * a {@link CDockableLocationListener}.
     * @param dockable the source of the event
     * @see CDockable#isVisible()
     * @see CDockable#isShowing()
     */
    public void visibilityChanged( CDockable dockable );
    
    /**
     * Called if the {@link CDockable#getExtendedMode() extended mode} of <code>dockable</code>
     * changed.
     * @param dockable the element whose mode changed
     * @param mode the new mode
     */
    public void extendedModeChanged( CDockable dockable, ExtendedMode mode );
}
