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
import bibliothek.gui.dock.layout.DockLayoutInfo;
import bibliothek.gui.dock.layout.LocationEstimationMap;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.station.screen.window.ScreenDockDialog;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.util.DirectWindowProvider;
import bibliothek.gui.dock.util.WindowProvider;
import bibliothek.util.Path;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A {@link DockFactory} which writes and reads instances 
 * of {@link ScreenDockStation}. For every station, the bounds of all
 * dialogs are stored.
 * @author Benjamin Sigg
 */
public class ScreenDockStationFactory implements DockFactory<ScreenDockStation, ScreenDockPerspective, ScreenDockStationLayout> {
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
    
    public void estimateLocations( ScreenDockStationLayout layout, final LocationEstimationMap children ){
    	if( layout instanceof RetroScreenDockStationLayout ){
    		RetroScreenDockStationLayout retro = (RetroScreenDockStationLayout)layout;
    		
	    	for( int i = 0, n = retro.size(); i<n; i++ ){
	    		DockLayoutInfo info = children.getChild( retro.id( i ));
	    		if( info != null ){
	    			ScreenDockProperty property = new ScreenDockProperty( retro.x( i ), retro.y( i ), retro.width( i ), retro.height( i ), null );
	    			info.setLocation( property );
	    		}
	    	}
    	}
    	else{
    		DockablePlaceholderList.simulatedRead( layout.getPlaceholders(), new PlaceholderListItemAdapter<Dockable, PlaceholderListItem<Dockable>>() {
    			@Override
    			public PlaceholderListItem<Dockable> convert( ConvertedPlaceholderListItem item ) {
    				int id = item.getInt( "id" );
    				
    				int x = item.getInt( "x" );
    				int y = item.getInt( "y" );
    				int width = item.getInt( "width" );
    				int height = item.getInt( "height" );
    				boolean fullscreen = item.getBoolean( "fullscreen" );
    				Path placeholder = null;
    				if( item.contains( "placeholder" )){
    					placeholder = new Path( item.getString( "placeholder" ) );
    				}
    				
    				ScreenDockProperty property = new ScreenDockProperty( x, y, width, height, placeholder, fullscreen );
    				children.getChild( id ).setLocation( property );
    				
    				for( int i = 0, n = children.getSubChildCount( id ); i<n; i++ ){
    					DockLayoutInfo info = children.getSubChild( id, i );
    					info.setLocation( new ScreenDockProperty( x, y, width, height, info.getPlaceholder(), fullscreen ) );
    				}

    				return null;
    			}
			});
    	}
    }
    
    public ScreenDockStationLayout getLayout( ScreenDockStation station, Map<Dockable, Integer> children ) {
        return new ScreenDockStationLayout( station.getPlaceholders( children ) );
    }
    
    public void setLayout( ScreenDockStation element, ScreenDockStationLayout layout, PlaceholderStrategy placeholders ) {
        // nothing to do
    }
    
    public void setLayout( ScreenDockStation station, ScreenDockStationLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ) {
        for( int i = station.getDockableCount()-1; i >= 0; i-- )
            station.removeDockable( i );
        
        if( layout instanceof RetroScreenDockStationLayout ){
        	RetroScreenDockStationLayout retro = (RetroScreenDockStationLayout) layout;
        	
	        for( int i = 0, n = retro.size(); i<n; i++ ){
	            Dockable dockable = children.get( retro.id( i ) );
	        	if( dockable != null ){
	             	Rectangle location = new Rectangle( retro.x( i ), retro.y( i ), retro.width( i ), retro.height( i ));
	        		station.addDockable(
	        				dockable,
	        				location, 
	        				true );
	        	}
	        }
        }
        else{
        	station.setPlaceholders( layout.getPlaceholders().filter( placeholders ), children );
        }
    }

    public ScreenDockStation layout( ScreenDockStationLayout layout, PlaceholderStrategy placeholders ) {
        ScreenDockStation station = createStation();
        setLayout( station, layout, placeholders );
        return station;
    }
    
    public ScreenDockStation layout( ScreenDockStationLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ) {
        ScreenDockStation station = createStation();
        setLayout( station, layout, children, placeholders );
        return station;
    }
    
    public ScreenDockStationLayout getPerspectiveLayout( ScreenDockPerspective element, Map<PerspectiveDockable, Integer> children ){
    	return new ScreenDockStationLayout( element.toMap( children ) );
    }
    
    public ScreenDockPerspective layoutPerspective( ScreenDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
	    ScreenDockPerspective result = new ScreenDockPerspective();
	    layoutPerspective( result, layout, children );
	    return result;
    }
    
    public void layoutPerspective( ScreenDockPerspective perspective, ScreenDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
    	perspective.read( layout.getPlaceholders(), children );
    }
    
    public void write( ScreenDockStationLayout layout, DataOutputStream out ) throws IOException {
     	if( layout instanceof RetroScreenDockStationLayout ){
    		RetroScreenDockStationLayout retro = (RetroScreenDockStationLayout)layout;
    	
	        Version.write( out, Version.VERSION_1_0_4 );
	        
	        out.writeInt( retro.size() );
	        for( int i = 0, n = retro.size(); i<n; i++ ){
	            out.writeInt( retro.id( i ) );
	            out.writeInt( retro.x( i ) );
	            out.writeInt( retro.y( i ) );
	            out.writeInt( retro.width( i ) );
	            out.writeInt( retro.height( i ) );
	        }
    	}
    	else{
    		PlaceholderMap map = layout.getPlaceholders();
    		Version.write( out, Version.VERSION_1_0_8 );
    		map.write( out );
    	}
    }
    
    public ScreenDockStationLayout read( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException{
        Version version = Version.read( in );
        version.checkCurrent();
        boolean version8 = version.compareTo( Version.VERSION_1_0_8 ) >= 0;
        
        if( version8 ){
        	PlaceholderMap map = new PlaceholderMap( in, placeholders );
        	return new ScreenDockStationLayout( map );
        }
        else{
	        RetroScreenDockStationLayout layout = new RetroScreenDockStationLayout();
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
    }

    public void write( ScreenDockStationLayout layout, XElement element ) {
    	if( layout instanceof RetroScreenDockStationLayout ){
    		RetroScreenDockStationLayout retro = (RetroScreenDockStationLayout)layout;
    	
	        for( int i = 0, n = retro.size(); i<n; i++ ){
	            XElement child = element.addElement( "child" );
	            child.addInt( "id", retro.id( i ) );
	            child.addInt( "x", retro.x( i ) );
	            child.addInt( "y", retro.y( i ) );
	            child.addInt( "width", retro.width( i ) );
	            child.addInt( "height", retro.height( i ) );
	        }
    	}
    	else{
    		layout.getPlaceholders().write( element.addElement( "placeholders" ) );
    	}
    }
    
    public ScreenDockStationLayout read( XElement element, PlaceholderStrategy placeholders ){
    	XElement xplaceholders = element.getElement( "placeholders" );
    	if( xplaceholders != null ){
    		return new ScreenDockStationLayout( new PlaceholderMap( xplaceholders, placeholders ) );
    	}
    	else{
	        RetroScreenDockStationLayout layout = new RetroScreenDockStationLayout();
	        for( XElement child : element.getElements( "child" )){
	            layout.add( 
	                    child.getInt( "id" ),
	                    child.getInt( "x" ),
	                    child.getInt( "y" ),
	                    child.getInt( "width" ),
	                    child.getInt( "height" ) );
	        }
	        return layout;
    	}
    }
    
    /**
     * Creates a new {@link ScreenDockStation}.
     * @return the new station
     */
    protected ScreenDockStation createStation(){
        return new ScreenDockStation( owner );
    }
}
