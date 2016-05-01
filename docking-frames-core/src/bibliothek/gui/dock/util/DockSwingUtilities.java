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

import java.awt.Component;
import java.awt.Container;

import javax.swing.JComponent;

/**
 * A set of methods useful for working with Swing.
 * @author Benjamin Sigg
 */
public final class DockSwingUtilities {
    private DockSwingUtilities(){
        // nothing
    }

    /**
     * Checks whether the tree of components, starting with <code>component</code>,
     * contains elements that are not from Swing but from AWT.
     * @param component the top of the tree
     * @return <code>true</code> if at least one AWT component was found
     */
    public static boolean containsAWTComponents( Component component ){
        if( component instanceof Container ){
            Container container = (Container)component;
            for( int i = 0, n = container.getComponentCount(); i<n; i++ ){
                if( containsAWTComponents( container.getComponent( i ) ))
                    return true;
            }
        }

        return !(component instanceof JComponent);
    }   
}
