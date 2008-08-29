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

package bibliothek.gui.dock.dockable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.layout.DockLayoutInfo;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A factory which can read and write the properties of a 
 * {@link DefaultDockable}. Note that this factory does not store any
 * information about the components added to the content pane of the
 * DefaultDockable.
 * @author Benjamin Sigg
 */
public class DefaultDockableFactory implements DockFactory<DefaultDockable, Object> {
    /** The unique id of this factory */
    public static final String ID = "DefaultDockableFactory";
    
    public String getID() {
        return ID;
    }

    public void estimateLocations(Object layout, DockableProperty location, Map<Integer, DockLayoutInfo> children) {
    	// nothing to do
    }
    
    public Object getLayout( DefaultDockable element,
            Map<Dockable, Integer> children ) {
        
        return new Object();
    }
    
    public void setLayout( DefaultDockable element, Object layout, Map<Integer, Dockable> children ) {
        // nothing to do
    }
    
    public void setLayout( DefaultDockable element, Object layout ) {
        // nothing to do
    }

    public DefaultDockable layout( Object layout, Map<Integer, Dockable> children ) {
        return new DefaultDockable();
    }
    
    public DefaultDockable layout( Object layout ) {
        return new DefaultDockable();
    }

    public Object read( DataInputStream in ) throws IOException {
        Version version = Version.read( in );
        version.checkCurrent();
        return new Object();
    }

    public Object read( XElement element ) {
        return new Object();
    }


    public void write( Object layout, DataOutputStream out )
            throws IOException {
        Version.write( out, Version.VERSION_1_0_4 );
        
        // nothing to do
    }

    public void write( Object layout, XElement element ) {
        // nothing to do
    }
}
