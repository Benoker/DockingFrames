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

import java.awt.event.KeyEvent;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.CDockable;

/**
 * A {@link CKeyboardListener} gets informed about {@link KeyEvent}s which
 * happen on certain {@link CDockable}s. The events can fall through several
 * {@link Dockable}s until they are processed. Once they are processed, no
 * other listener is informed about them. 
 * @author Benjamin Sigg
 * @see CDockable#addKeyboardListener(CKeyboardListener)
 * @see CDockable#removeKeyboardListener(CKeyboardListener)
 * @see CControl#addKeyboardListener(CKeyboardListener)
 * @see CControl#removeKeyboardListener(CKeyboardListener)
 */
public interface CKeyboardListener {
    /**
     * Called when a key was pressed on a child or on <code>source</code>.
     * @param source the source of the event
     * @param event the event
     * @return <code>true</code> if this method processed the event and the
     * event must not be forwarded to other listeners, <code>false</code>
     * if this listener did not process the event
     */
    public boolean keyPressed( CDockable source, KeyEvent event );
    
    /**
     * Called when a key was released on a child or on <code>source</code>.
     * @param source the source of the event
     * @param event the event
     * @return <code>true</code> if this method processed the event and the
     * event must not be forwarded to other listeners, <code>false</code>
     * if this listener did not process the event
     */
    public boolean keyReleased( CDockable source, KeyEvent event );
    
    /**
     * Called when a key was typed on a child or on <code>source</code>.
     * @param source the source of the event
     * @param event the event
     * @return <code>true</code> if this method processed the event and the
     * event must not be forwarded to other listeners, <code>false</code>
     * if this listener did not process the event
     */
    public boolean keyTyped( CDockable source, KeyEvent event );
}
