/**
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

package bibliothek.gui.dock.station.stack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.station.StackDockStation;

/**
 * A {@link DockFactory} that can read and write instances of {@link StackDockStation}.
 * A call to {@link #read(Map, boolean, DataInputStream) read} is forwarded
 * to the method {@link StackDockStation}.{@link StackDockStation#read(Map, boolean, DataInputStream) read},
 * and a call to {@link #write(StackDockStation, Map, DataOutputStream) write}
 * uses the method {@link StackDockStation}.{@link StackDockStation#write(Map, DataOutputStream) write}.<br>
 * New instances are created by {@link #createStation()}.
 * @author Benjamin Sigg
 */
public class StackDockStationFactory implements DockFactory<StackDockStation> {
    /** The ID which is returned by {@link #getID()}*/
    public static final String ID = "StackDockStationFactory";
    
    public String getID() {
        return ID;
    }

    public void write( StackDockStation element,
            Map<Dockable, Integer> children,
            DataOutputStream out )
            throws IOException {
        
        element.write( children, out );
    }

    public StackDockStation read( Map<Integer, Dockable> children,
            boolean ignore,
            DataInputStream in ) throws IOException {
        
        StackDockStation station = createStation();
        station.read( children,ignore, in );
        return station;
    }

    public void read(Map<Integer, Dockable> children, boolean ignore, StackDockStation station, DataInputStream in) throws IOException {
    	station.read( children, ignore, in );
    }
    
    /**
     * Called by {@link #read(Map, boolean, DataInputStream) read} when
     * @return a new station
     */
    protected StackDockStation createStation(){
        return new StackDockStation();
    }
}
