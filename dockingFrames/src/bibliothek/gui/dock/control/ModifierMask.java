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
package bibliothek.gui.dock.control;

import java.awt.event.InputEvent;

/**
 * A {@link ModifierMask} represents a pattern that tells what modifier keys 
 * are currently pressed. Modifier keys are keys like ctrl, shift or alt.<br>
 * This interface will be used to check the result of {@link InputEvent#getModifiersEx()}.
 * @author Benjamin Sigg
 */
public interface ModifierMask {
    /**
     * Tells whether this {@link ModifierMask} relates to <code>modifiers</code>
     * or not. The <code>modifiers</code> have the same form as 
     * {@link InputEvent#getModifiersEx()} would have.
     * @param modifiers the modifiers from an <code>InputEvent</code>
     * @return <code>true</code> if this mask matches the modifiers
     */
    public boolean matches( int modifiers );
}
