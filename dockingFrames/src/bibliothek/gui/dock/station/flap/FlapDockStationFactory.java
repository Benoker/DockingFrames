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

package bibliothek.gui.dock.station.flap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.FlapDockStation.Direction;
import bibliothek.util.xml.XElement;

/**
 * A {@link DockFactory} which can handle {@link FlapDockStation}s.
 * @author Benjamin Sigg
 */
public class FlapDockStationFactory implements DockFactory<FlapDockStation, FlapDockStationLayout> {
	/** The default-id of this factory */
    public static final String ID = "flap dock";
    
    public String getID() {
        return ID;
    }
    
    public FlapDockStationLayout getLayout( FlapDockStation station,
            Map<Dockable, Integer> children ) {
        
        List<Integer> ids = new ArrayList<Integer>();
        List<Boolean> holding = new ArrayList<Boolean>();
        
        for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
            Dockable dockable = station.getDockable( i );
            Integer id = children.get( dockable );
            if( id != null ){
                ids.add( id );
                holding.add( station.isHold( dockable ));
            }
        }
        
        int[] idArray = new int[ ids.size() ];
        boolean[] holdingArray = new boolean[ ids.size() ];
        for( int i = 0, n = ids.size(); i<n; i++ ){
            idArray[i] = ids.get( i );
            holdingArray[i] = holding.get( i );
        }
        
        return new FlapDockStationLayout( idArray, holdingArray, 
                station.getWindowSize(), station.isAutoDirection(),
                station.getDirection() );
    }
    
    public void setLayout( FlapDockStation station, FlapDockStationLayout layout ) {
        station.setDirection( layout.getDirection() );
        station.setAutoDirection( layout.isAutoDirection() );
        station.setWindowSize( layout.getSize() );
    }
    
    public void setLayout( FlapDockStation station,
            FlapDockStationLayout layout, Map<Integer, Dockable> children ) {
     
        for( int i = station.getDockableCount()-1; i >= 0; i-- )
            station.remove( i );
        
        station.setDirection( layout.getDirection() );
        station.setAutoDirection( layout.isAutoDirection() );
        station.setWindowSize( layout.getSize() );
        
        int[] ids = layout.getChildren();
        boolean[] holding = layout.getHolds();
        
        for( int i = 0, n = ids.length; i<n; i++ ){
            Dockable dockable = children.get( ids[i] );
            if( dockable != null ){
                station.add( dockable );
                station.setHold( dockable, holding[i] );
            }
        }
    }
    
    public FlapDockStation layout( FlapDockStationLayout layout,
            Map<Integer, Dockable> children ) {
        
        FlapDockStation station = createStation();
        setLayout( station, layout, children );
        return station;
    }
    
    public FlapDockStation layout( FlapDockStationLayout layout ) {
        FlapDockStation station = createStation();
        setLayout( station, layout );
        return station;
    }
    
    public void write( FlapDockStationLayout layout, DataOutputStream out )
            throws IOException {
     
        out.writeBoolean( layout.isAutoDirection() );
        out.writeInt( layout.getDirection().ordinal() );
        out.writeInt( layout.getSize() );
        
        int count = layout.getChildren().length;
        out.writeInt( count );
        for( int i = 0; i < count; i++ ){
            out.writeInt( layout.getChildren()[i] );
            out.writeBoolean( layout.getHolds()[i] );
        }
    }

    public FlapDockStationLayout read( DataInputStream in ) throws IOException {
        boolean auto = in.readBoolean();
        Direction direction = Direction.values()[ in.readInt() ];
        int size = in.readInt();
        int count = in.readInt();
        
        int[] ids = new int[ count ];
        boolean[] holds = new boolean[ count ];
        
        for( int i = 0; i < count; i++ ){
            ids[i] = in.readInt();
            holds[i] = in.readBoolean();
        }
        
        return new FlapDockStationLayout( ids, holds, size, auto, direction );
    }

    public void write( FlapDockStationLayout layout, XElement element ) {
        XElement window = element.addElement( "window" );
        window.addBoolean( "auto", layout.isAutoDirection() );
        window.addInt( "size", layout.getSize() );
        window.addString( "direction", layout.getDirection().name() );
        
        XElement children = element.addElement( "children" );
        for( int i = 0, n = layout.getChildren().length; i<n; i++ ){
            XElement child = children.addElement( "child" );
            child.addInt( "id", layout.getChildren()[i] );
            child.addBoolean( "hold", layout.getHolds()[i] );
        }
    }
    
    public FlapDockStationLayout read( XElement element ) {
        XElement window = element.getElement( "window" );
        XElement children = element.getElement( "children" );
        XElement[] child = children.getElements( "child" );
        
        int[] ids = new int[ child.length ];
        boolean[] holds = new boolean[ child.length ];
        
        for( int i = 0, n = child.length; i<n; i++ ){
            ids[i] = child[i].getInt( "id" );
            holds[i] = child[i].getBoolean( "hold" );
        }
        
        return new FlapDockStationLayout( ids, holds, 
                window.getInt( "size" ), window.getBoolean( "auto" ),
                Direction.valueOf( window.getString( "direction" ) ));
    }
    
    /**
     * Creates an instance of a {@link FlapDockStation}.
     * @return a new object 
     */
    protected FlapDockStation createStation(){
        return new FlapDockStation();
    }
}
