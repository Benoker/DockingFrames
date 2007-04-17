/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * A factory which can read and write the properties of a 
 * {@link DefaultDockable}. Note that this factory does not store any
 * information about the components added to the content pane of the
 * DefaultDockable.
 * @author Benjamin Sigg
 */
public class DefaultDockableFactory implements DockFactory<DefaultDockable> {
    /** The unique id of this factory */
    public static final String ID = "DefaultDockableFactory";
    
    public String getID() {
        return ID;
    }

    public void write( 
            DefaultDockable element,
            Map<Dockable, Integer> children,
            DataOutputStream out )
            throws IOException {
    	// do nothing
    }

    public DefaultDockable read( 
            Map<Integer, Dockable> children,
            boolean ignore,
            DataInputStream in ) throws IOException {
        
        DefaultDockable element = new DefaultDockable();
        return element;
    }
    
    public void read(Map<Integer, Dockable> children, boolean ignore, DefaultDockable preloaded, DataInputStream in) throws IOException {
    	// nothing to do
    }
}
