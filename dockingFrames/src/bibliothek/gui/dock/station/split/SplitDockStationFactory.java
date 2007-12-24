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

package bibliothek.gui.dock.station.split;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.SplitDockStation;

/**
 * A factory that creates {@link SplitDockStation SplitDockStations}.
 * @author Benjamin Sigg
 */
public class SplitDockStationFactory implements DockFactory<SplitDockStation> {
	/** The id which is normally used for this type of factory*/
    public static final String ID = "SplitDockStationFactory";
    
    public String getID() {
        return ID;
    }

    public void write( SplitDockStation element,
            Map<Dockable, Integer> children,
            DataOutputStream out )
            throws IOException {
        
        element.write( children, out );
    }

    public SplitDockStation read( 
            Map<Integer, Dockable> children,
            boolean ignore,
            DataInputStream in ) throws IOException {
        
        SplitDockStation station = createStation();
        station.read( children, ignore, in );
        return station;
    }
    
    public void read(Map<Integer, Dockable> children, boolean ignore, SplitDockStation station, DataInputStream in) throws IOException {
    	station.read( children, ignore, in );
    }

    /**
     * Creates an instance of a {@link SplitDockStation}. This instance
     * will be returned from {@link #read(Map, boolean, DataInputStream) read}.
     * @return the new instance
     */
    protected SplitDockStation createStation(){
        return new SplitDockStation();
    }
}
