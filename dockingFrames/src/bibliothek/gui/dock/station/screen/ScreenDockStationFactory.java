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

package bibliothek.gui.dock.station.screen;

import java.awt.Window;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.ScreenDockStation;

/**
 * A {@link DockFactory} which writes and reads instances 
 * of {@link ScreenDockStation}. For every station, the bounds of all
 * dialogs are stored.
 * @author Benjamin Sigg
 */
public class ScreenDockStationFactory implements DockFactory<ScreenDockStation> {
    public static final String ID = "screen dock";

    private Window owner;
    
    /**
     * Constructs a factory
     * @param owner the window which will be used as owner for {@link ScreenDockDialog dialogs}
     */
    public ScreenDockStationFactory( Window owner ){
    	if( owner == null )
            throw new IllegalArgumentException( "Owner must not be null" );
        this.owner = owner;
    }
    
    /**
     * Gets the owner of all created dialogs.
     * @return the owner
     */
    public Window getOwner(){
        return owner;
    }
    
    public String getID() {
        return ID;
    }

    public void write( 
            ScreenDockStation station,
            Map<Dockable, Integer> children,
            DataOutputStream out )
    
            throws IOException {
        
        int count = station.getDockableCount();
        out.writeInt( count );
        
        for( int i = 0; i < count; i++ ){
            Dockable dockable = station.getDockable( i );
            ScreenDockDialog dialog = station.getDialog( i );
            
            out.writeInt( dialog.getX() );
            out.writeInt( dialog.getY() );
            out.writeInt( dialog.getWidth() );
            out.writeInt( dialog.getHeight() );
            out.writeInt( children.get( dockable ) );
        }
    }

    public ScreenDockStation read( 
            Map<Integer, Dockable> children,
            boolean ignore,
            DataInputStream in )
            throws IOException {
        
        ScreenDockStation station = createStation();
        read( children, ignore, station, in );
        return station;
    }
    
    public void read(Map<Integer, Dockable> children, boolean ignore, ScreenDockStation station, DataInputStream in) throws IOException {
    	int count = in.readInt();
        
        for( int i = 0; i < count; i++ ){
            int x = in.readInt();
            int y = in.readInt();
            int width = in.readInt();
            int height = in.readInt();
            int child = in.readInt();
            
            Dockable dockable = children.get( child );
            if( dockable != null ){
                station.drop( dockable );
                ScreenDockDialog dialog = station.getDialog( dockable );
                dialog.setBounds( x, y, width, height );
                dialog.validate();
            }
        }
    }

    /**
     * Creates a new {@link ScreenDockStation} which will be returned
     * by {@link #read(Map, boolean, DataInputStream) read}.
     * @return the new station
     */
    protected ScreenDockStation createStation(){
        return new ScreenDockStation( owner );
    }
}
