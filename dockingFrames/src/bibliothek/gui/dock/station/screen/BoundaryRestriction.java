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
package bibliothek.gui.dock.station.screen;

import java.awt.*;

/**
 * Restricts the boundaries of a {@link ScreenDockDialog}, a restriction might be,
 * that the dialog can't be placed outside the screen.
 * @author Benjamin Sigg
 */
public interface BoundaryRestriction {
    /** A restriction that allows all boundaries. */
    public BoundaryRestriction FREE = new BoundaryRestriction(){
        public Rectangle check( ScreenDockDialog dialog ) {
            return null;
        }
        public Rectangle check( ScreenDockDialog dialog, Rectangle target ) {
            return null;
        }
    };
    
    /** 
     * A restriction that will ensure that every dialog is always visible, even
     * if that means that in a multi screen environment some screens can't be 
     * used. 
     */
    public BoundaryRestriction HARD = new HardBoundaryRestriction();
    
    /**
     * Calculates the bounds which <code>dialog</code> can have. 
     * @param dialog the dialog whose bounds should be checked.
     * @return the new boundaries of <code>dialog</code>, can be <code>null</code>
     * to indicate that the current boundaries are valid. 
     */
    public Rectangle check( ScreenDockDialog dialog );
    
    /**
     * Calculates the bounds which <code>dialog</code> can have.
     * @param dialog the dialog whose future bounds should be checked.
     * @param target the bounds <code>dialog</code> should have, this method
     * should not write into <code>target</code>.
     * @return the new boundaries, <code>null</code> to indicate that 
     * <code>target</code> is valid. 
     */
    public Rectangle check( ScreenDockDialog dialog, Rectangle target );
}
