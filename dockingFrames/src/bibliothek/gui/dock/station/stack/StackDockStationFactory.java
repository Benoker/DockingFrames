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

package bibliothek.gui.dock.station.stack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.layout.DockLayoutInfo;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A {@link DockFactory} that can read and write instances of {@link StackDockStation}.
 * This factory will create new instances of {@link StackDockStation} through
 * the method {@link #createStation()}.
 * @author Benjamin Sigg
 */
public class StackDockStationFactory implements DockFactory<StackDockStation, StackDockStationLayout> {
    /** The ID which is returned by {@link #getID()}*/
    public static final String ID = "StackDockStationFactory";
    
    public String getID() {
        return ID;
    }

    public StackDockStationLayout getLayout( StackDockStation station,
            Map<Dockable, Integer> children ) {
        
        List<Integer> list = new ArrayList<Integer>();
        Dockable selectedDockable = station.getFrontDockable();
        int selected = -1;
        
        for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
            Dockable dockable = station.getDockable( i );
            Integer id = children.get( dockable );
            if( id != null ){
                list.add( id );
                if( selectedDockable == dockable ){
                    selected = id;
                }
            }
        }
        
        int[] ids = new int[ list.size() ];
        for( int i = 0, n = list.size(); i<n; i++ )
            ids[i] = list.get( i ).intValue();
        
        return new StackDockStationLayout( selected, ids );
    }
    
    public void estimateLocations( StackDockStationLayout layout,
    		DockableProperty location, Map<Integer, DockLayoutInfo> children ) {
    	
    	for( int id : layout.getChildren() ){
    		DockLayoutInfo info = children.get( id );
    		if( info != null ){
    			StackDockProperty property = new StackDockProperty( id );
    			property.setSuccessor( location );
    			info.setLocation( property );
    		}
    	}
    }
    
    public void setLayout( StackDockStation station,
            StackDockStationLayout layout, Map<Integer, Dockable> children ) {
        
        for( int i = station.getDockableCount()-1; i >= 0; i-- )
            station.remove( i );
        
        for( int id : layout.getChildren() ){
        	Dockable dockable = children.get( id );
        	if( dockable != null ){
        		station.drop( dockable );
        	}
        }
        
        Dockable selected = children.get( layout.getSelected() );
        if( selected != null ){
    		station.setFrontDockable( selected );
        }
    }
    
    public void setLayout( StackDockStation element, StackDockStationLayout layout ) {
        // nothing to do
    }
    
    public StackDockStation layout( StackDockStationLayout layout,
            Map<Integer, Dockable> children ) {
        
        StackDockStation station = createStation();
        setLayout( station, layout, children );
        return station;
    }
    
    public StackDockStation layout( StackDockStationLayout layout ) {
        StackDockStation station = createStation();
        setLayout( station, layout );
        return station;
    }

    public void write( StackDockStationLayout layout, DataOutputStream out )
            throws IOException {
        
        Version.write( out, Version.VERSION_1_0_4 );
        out.writeInt( layout.getSelected() );
        out.writeInt( layout.getChildren().length );
        for( int c : layout.getChildren() )
            out.writeInt( c );
    }
    
    public StackDockStationLayout read( DataInputStream in ) throws IOException {
        Version version = Version.read( in );
        version.checkCurrent();
        
        int selected = in.readInt();
        int count = in.readInt();
        int[] ids = new int[ count ];
        for( int i = 0; i < count; i++ )
            ids[i] = in.readInt();
        return new StackDockStationLayout( selected, ids );
    }
    
    public void write( StackDockStationLayout layout, XElement element ) {
        if( layout.getSelected() >= 0 )
            element.addElement( "selected" ).setInt( layout.getSelected() );
        
        XElement xchildren = element.addElement( "children" );
        for( int i : layout.getChildren() ){
            xchildren.addElement( "child" ).addInt( "id", i );
        }
    }
    
    public StackDockStationLayout read( XElement element ) {
        XElement xselected = element.getElement( "selected" );
        int selected = -1;
        if( xselected != null )
            selected = xselected.getInt();
        
        XElement xchildren = element.getElement( "children" );
        int[] ids;
        
        if( xchildren != null ){
            XElement[] children = xchildren.getElements( "child" );
            ids = new int[ children.length ];
            for( int i = 0, n = children.length; i<n; i++ )
                ids[i] = children[i].getInt( "id" );
        }
        else{
            ids = new int[]{};
        }
        
        return new StackDockStationLayout( selected, ids );
    }
    
    /**
     * Called when a new {@link StackDockStation} is required.
     * @return a new station
     */
    protected StackDockStation createStation(){
        return new StackDockStation();
    }
}
