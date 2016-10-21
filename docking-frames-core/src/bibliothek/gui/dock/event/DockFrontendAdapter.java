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

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;

/**
 * A class implementing all methods of {@link DockFrontendListener}, but
 * not doing anything inside these methods.
 * @author Benjamin Sigg
 *
 */
public abstract class DockFrontendAdapter implements DockFrontendListener {
    public void added( DockFrontend frontend, Dockable dockable ) {
        // empty
    }

    public void deleted( DockFrontend frontend, String name ) {
        // empty
    }

    public void hidden( DockFrontend fronend, Dockable dockable ) {
        // empty
    }

    public void hideable( DockFrontend frontend, Dockable dockable, boolean hideable ) {
        // empty
    }

    public void loaded( DockFrontend frontend, String name ) {
        // empty
    }

    public void read( DockFrontend frontend, String name ){
    	// empty
    }
    
    public void removed( DockFrontend frontend, Dockable dockable ) {
        // empty
    }

    public void saved( DockFrontend frontend, String name ) {
        // empty
    }

    public void shown( DockFrontend frontend, Dockable dockable ) {
        // empty
    }
}
