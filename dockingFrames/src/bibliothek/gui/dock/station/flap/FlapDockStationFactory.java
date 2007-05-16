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

package bibliothek.gui.dock.station.flap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.station.FlapDockStation;

/**
 * A {@link DockFactory} which can create instances of {@link FlapDockStation}.
 * @author Benjamin Sigg
 */
public class FlapDockStationFactory implements DockFactory<FlapDockStation> {
	/** The default-id of this factory */
    public static final String ID = "flap dock";
    
    public String getID() {
        return ID;
    }

    public void write( 
            FlapDockStation station,
            Map<Dockable, Integer> children,
            DataOutputStream out )
    
            throws IOException {

        out.writeBoolean( station.isAutoDirection() );
        out.writeInt( station.getDirection().ordinal() );
        out.writeInt( station.getWindowSize() );
        int count = station.getDockableCount();
        out.writeInt( count );
        for( int i = 0; i < count; i++ ){
            Dockable dockable = station.getDockable( i );
            out.writeInt( children.get( dockable ));
            out.writeBoolean( station.isHold( dockable ));
        }
    }

    public FlapDockStation read( 
            Map<Integer, Dockable> children,
            boolean ignore,
            DataInputStream in )
    
            throws IOException {
        
        FlapDockStation station = createStation();
        read( children, ignore, station, in );
        return station;
    }
    
    public void read(Map<Integer, Dockable> children, boolean ignore, FlapDockStation station, DataInputStream in) throws IOException {
        station.setAutoDirection( in.readBoolean() );
        station.setDirection( FlapDockStation.Direction.values()[ in.readInt() ] );
        station.setWindowSize( in.readInt() );
        int count = in.readInt();
        for( int i = 0; i < count; i++ ){
            int id = in.readInt();
            Dockable dockable = children.get( id );
            if( dockable != null ){
                station.add( dockable );
                station.setHold( dockable, in.readBoolean() );
            }
        }    	
    }

    /**
     * Creates an instance of a {@link FlapDockStation}.
     * @return the instance which will be returned 
     * by {@link #read(Map, boolean, DataInputStream) read}.
     */
    protected FlapDockStation createStation(){
        return new FlapDockStation();
    }
}
