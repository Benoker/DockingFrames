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

package bibliothek.gui.dock.station.screen;

import java.awt.Rectangle;
import java.awt.Window;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.util.DirectWindowProvider;
import bibliothek.gui.dock.util.WindowProvider;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A {@link DockFactory} which writes and reads instances 
 * of {@link ScreenDockStation}. For every station, the bounds of all
 * dialogs are stored.
 * @author Benjamin Sigg
 */
public class ScreenDockStationFactory implements DockFactory<ScreenDockStation, ScreenDockStationLayout> {
    public static final String ID = "screen dock";

    private WindowProvider owner;
    
    /**
     * Constructs a factory
     * @param owner the window which will be used as owner for {@link ScreenDockDialog dialogs}
     */
    public ScreenDockStationFactory( Window owner ){
    	if( owner == null )
            throw new IllegalArgumentException( "Owner must not be null" );
        this.owner = new DirectWindowProvider( owner );
    }

    /**
     * Constructs a factory
     * @param owner the window which will be used as owner for {@link ScreenDockDialog dialogs}
     */
    public ScreenDockStationFactory( WindowProvider owner ){
        if( owner == null )
            throw new IllegalArgumentException( "Owner must not be null" );
        this.owner = owner;
    }
    
    /**
     * Gets the owner of all created dialogs.
     * @return the owner
     * @deprecated replaced by {@link #getProvider()}
     */
    @Deprecated
    public Window getOwner(){
        return owner.searchWindow();
    }
    
    /**
     * Gets the provider for windows, which will be used as owner for newly
     * created dialogs.
     * @return the owner
     */
    public WindowProvider getProvider(){
        return owner;
    }
    
    public String getID() {
        return ID;
    }
    
    public ScreenDockStationLayout getLayout( ScreenDockStation station,
            Map<Dockable, Integer> children ) {
        
        ScreenDockStationLayout layout = new ScreenDockStationLayout();
        for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
            Dockable dockable = station.getDockable( i );
            Integer id = children.get( dockable );
            if( id != null ){
                ScreenDockWindow window = station.getWindow( i );
                Rectangle bounds = window.getWindowBounds();
                
                layout.add( 
                        id,
                        bounds.x,
                        bounds.y, 
                        bounds.width,
                        bounds.height );
            }
        }
        
        return layout;
    }
    
    public void setLayout( ScreenDockStation element,
            ScreenDockStationLayout layout ) {
        // nothing to do
    }
    
    public void setLayout( ScreenDockStation station,
            ScreenDockStationLayout layout, Map<Integer, Dockable> children ) {
        
        for( int i = station.getDockableCount()-1; i >= 0; i-- )
            station.removeDockable( i );
        
        for( int i = 0, n = layout.size(); i<n; i++ ){
            Dockable dockable = children.get( layout.id( i ) );
            if( dockable != null ){
                station.addDockable(
                        dockable,
                        new Rectangle( layout.x( i ), layout.y( i ), layout.width( i ), layout.height( i )), 
                        true );
            }
        }
    }

    public ScreenDockStation layout( ScreenDockStationLayout layout ) {
        ScreenDockStation station = createStation();
        setLayout( station, layout );
        return station;
    }
    
    public ScreenDockStation layout( ScreenDockStationLayout layout,
            Map<Integer, Dockable> children ) {
        ScreenDockStation station = createStation();
        setLayout( station, layout, children );
        return station;
    }
    
    public void write( ScreenDockStationLayout layout, DataOutputStream out )
            throws IOException {
     
        Version.write( out, Version.VERSION_1_0_4 );
        
        out.writeInt( layout.size() );
        for( int i = 0, n = layout.size(); i<n; i++ ){
            out.writeInt( layout.id( i ) );
            out.writeInt( layout.x( i ) );
            out.writeInt( layout.y( i ) );
            out.writeInt( layout.width( i ) );
            out.writeInt( layout.height( i ) );
        }
    }
    
    public ScreenDockStationLayout read( DataInputStream in )
            throws IOException {
        
        Version version = Version.read( in );
        version.checkCurrent();
        
        ScreenDockStationLayout layout = new ScreenDockStationLayout();
        int count = in.readInt();
        for( int i = 0; i < count; i++ ){
            int id = in.readInt();
            int x = in.readInt();
            int y = in.readInt();
            int width = in.readInt();
            int height = in.readInt();
            layout.add( id, x, y, width, height );
        }
        return layout;
    }

    public void write( ScreenDockStationLayout layout, XElement element ) {
        for( int i = 0, n = layout.size(); i<n; i++ ){
            XElement child = element.addElement( "child" );
            child.addInt( "id", layout.id( i ) );
            child.addInt( "x", layout.x( i ) );
            child.addInt( "y", layout.y( i ) );
            child.addInt( "width", layout.width( i ) );
            child.addInt( "height", layout.height( i ) );
        }
    }
    
    public ScreenDockStationLayout read( XElement element ) {
        ScreenDockStationLayout layout = new ScreenDockStationLayout();
        for( XElement child : element.getElements( "child" )){
            layout.add( 
                    child.getInt( "id" ),
                    child.getInt( "x" ),
                    child.getInt( "y" ),
                    child.getInt( "width" ),
                    child.getInt( "height" ));
        }
        return layout;
    }
    
    /**
     * Creates a new {@link ScreenDockStation}.
     * @return the new station
     */
    protected ScreenDockStation createStation(){
        return new ScreenDockStation( owner );
    }
}
