/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.util;

import java.awt.Window;

/**
 * A window provider where the window can be set directly.
 * @author Benjamin Sigg
 */
public class DirectWindowProvider extends AbstractWindowProvider {
    private Window window;
    
    /**
     * Creates a new window provider.
     */
    public DirectWindowProvider(){
        // ignore
    }
    
    /**
     * Creates a new window provider.
     * @param window the window which should be provided by this provider,
     * can be <code>null</code>
     */
    public DirectWindowProvider( Window window ){
        setWindow( window );
    }
    
    /**
     * Sets the window which will be provided by this provider.
     * @param window the new window, can be <code>null</code>
     */
    public void setWindow( Window window ) {
        if( this.window != window ){
            this.window = window;
            fireWindowChanged( window );
        }
    }
    
    public Window searchWindow() {
        return window;
    }

}
