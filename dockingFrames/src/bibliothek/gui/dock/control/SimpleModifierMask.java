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

/**
 * This mask uses two integers <code>on</code> and <code>off</code>
 * to check a modifier. A modifier is accepted if <code>(modifier & (on | off))== on</code>
 * is <code>true</code>.
 * @author Benjamin Sigg
 */
public class SimpleModifierMask implements ModifierMask{
    private int onmask;
    private int offmask;
    
    /**
     * Creates a new mask.
     * @param on the keys that must be pressed
     * @param off the keys that must not be pressed
     */
    public SimpleModifierMask( int on, int off ){
        this.onmask = on;
        this.offmask = off;
    }
    
    public boolean matches( int modifiers ) {
        return (modifiers & (onmask | offmask)) == onmask;
    }
}
