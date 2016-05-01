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
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.FlapDockStation.Direction;
import bibliothek.gui.dock.layout.DockLayoutInfo;
import bibliothek.gui.dock.layout.LocationEstimationMap;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.Path;
import bibliothek.util.Version;
import bibliothek.util.xml.XAttribute;
import bibliothek.util.xml.XElement;

/**
 * A {@link DockFactory} which can handle {@link FlapDockStation}s.
 * @author Benjamin Sigg
 */
public class FlapDockStationFactory implements DockFactory<FlapDockStation, FlapDockPerspective, FlapDockStationLayout> {
	/** The default-id of this factory */
    public static final String ID = "flap dock";
    
    public String getID() {
        return ID;
    }
    
    public FlapDockStationLayout getLayout( FlapDockStation station, Map<Dockable, Integer> children ) {
        PlaceholderMap map = station.getPlaceholders( children );
        return new FlapDockStationLayout( station.isAutoDirection(), station.getDirection(), map );
    }
    
    public void setLayout( FlapDockStation station, FlapDockStationLayout layout, PlaceholderStrategy placeholders ) {
        station.setDirection( layout.getDirection() );
        station.setAutoDirection( layout.isAutoDirection() );
    }
    
    public void setLayout( FlapDockStation station, FlapDockStationLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ) {
    	DockController controller = station.getController();
    	try{
    		if( controller != null )
    			controller.freezeLayout();
    		
	        for( int i = station.getDockableCount()-1; i >= 0; i-- )
	            station.remove( i );
	        
	        station.setDirection( layout.getDirection() );
	        station.setAutoDirection( layout.isAutoDirection() );
	        
	        if( layout instanceof RetroFlapDockStationLayout ){
	        	RetroFlapDockStationLayout retroLayout = (RetroFlapDockStationLayout)layout;
	        	
	        	int[] ids = retroLayout.getChildren();
		        boolean[] holding = retroLayout.getHolds();
		        int[] sizes = retroLayout.getSizes();
		        
		        for( int i = 0, n = ids.length; i<n; i++ ){
		        	Dockable dockable = children.get( ids[i] );
		            
		        	if( dockable != null ){
		        		station.add( dockable );
		        		station.setHold( dockable, holding[i] );
		        		station.setWindowSize( dockable, sizes[i] );
		        	}
		        }	
	        }
	        else{
	        	station.setPlaceholders( layout.getPlaceholders().filter( placeholders ), children );
	        }
	        
    	}
    	finally{
    		if( controller != null )
    			controller.meltLayout();
    	}
    }
    
    public FlapDockPerspective layoutPerspective( FlapDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
    	FlapDockPerspective perspective = new FlapDockPerspective();
    	layoutPerspective( perspective, layout, children );
    	return perspective;
    }
    
    public void layoutPerspective( FlapDockPerspective perspective, FlapDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
	    perspective.read( layout.getPlaceholders(), children );	
    }

    public FlapDockStationLayout getPerspectiveLayout( FlapDockPerspective element, Map<PerspectiveDockable, Integer> children ){
    	boolean autoDirection = true;
    	Direction direction = Direction.SOUTH;
    	PlaceholderMap placeholders = element.toMap( children );
    	
	    return new FlapDockStationLayout( autoDirection, direction, placeholders );
    }
    
    public void estimateLocations( FlapDockStationLayout layout, final LocationEstimationMap children ){
    	if( layout instanceof RetroFlapDockStationLayout ){
    		RetroFlapDockStationLayout retroLayout = (RetroFlapDockStationLayout)layout;
	    	int[] ids = retroLayout.getChildren();
	    	boolean[] holding = retroLayout.getHolds();
	    	int[] sizes = retroLayout.getSizes();
	    	
	    	for( int i = 0, n = ids.length; i<n; i++ ){
	    		DockLayoutInfo info = children.getChild( ids[i] );
	    		if( info != null ){
	    			FlapDockProperty property = new FlapDockProperty( i, holding[i], sizes[i], info.getPlaceholder() );
	    			info.setLocation( property );
	    		}
	    	}
    	}
    	else{
    		DockablePlaceholderList.simulatedRead( layout.getPlaceholders(), new PlaceholderListItemAdapter<Dockable, PlaceholderListItem<Dockable>>() {
    			@Override
    			public PlaceholderListItem<Dockable> convert( ConvertedPlaceholderListItem item ){
    				int id = item.getInt( "id" );
    				int index = item.getInt( "index" );
    				boolean hold = item.getBoolean( "hold" );
    				int size = item.getInt( "size" );
    				Path placeholder = null;
    				if( item.contains( "placeholder" )){
    					placeholder = new Path( item.getString( "placeholder" ) );
    				}
    				FlapDockProperty property = new FlapDockProperty( index, hold, size, placeholder );
    				children.getChild( id ).setLocation( property );
    				
    				for( int i = 0, n = children.getSubChildCount( id ); i<n; i++ ){
    					DockLayoutInfo info = children.getSubChild( id, i );
    					info.setLocation( new FlapDockProperty( id, hold, size, info.getPlaceholder() ) );
    				}
    				
    				return null;
    			}
			});
    	}
    }
    
    public FlapDockStation layout( FlapDockStationLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ) {
        FlapDockStation station = createStation();
        setLayout( station, layout, children, placeholders );
        return station;
    }
    
    public FlapDockStation layout( FlapDockStationLayout layout, PlaceholderStrategy placeholders ) {
        FlapDockStation station = createStation();
        setLayout( station, layout, placeholders );
        return station;
    }

    public void write( FlapDockStationLayout layout, DataOutputStream out ) throws IOException {
    	if( layout instanceof RetroFlapDockStationLayout ){
    		RetroFlapDockStationLayout retroLayout = (RetroFlapDockStationLayout)layout;
    		Version.write( out, Version.VERSION_1_0_4 );

    		out.writeBoolean( layout.isAutoDirection() );
    		out.writeInt( layout.getDirection().ordinal() );

    		int count = retroLayout.getChildren().length;
    		out.writeInt( count );
    		for( int i = 0; i < count; i++ ){
    			out.writeInt( retroLayout.getChildren()[i] );
    			out.writeBoolean( retroLayout.getHolds()[i] );
    			out.writeInt( retroLayout.getSizes()[i] );

    		}
    	}
    	else{
    		Version.write( out, Version.VERSION_1_0_8 );

    		out.writeBoolean( layout.isAutoDirection() );
    		out.writeInt( layout.getDirection().ordinal() );

    		layout.getPlaceholders().write( out );
    	}
    }
    
    public FlapDockStationLayout read( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException{
        Version version = Version.read( in );
        version.checkCurrent();
        
        boolean version8 = Version.VERSION_1_0_8.compareTo( version ) <= 0;
        
        boolean auto = in.readBoolean();
        Direction direction = Direction.values()[ in.readInt() ];
        
        if( version8 ){
        	PlaceholderMap map = new PlaceholderMap( in, placeholders );
        	map.setPlaceholderStrategy( null );
        	
        	return new FlapDockStationLayout( auto, direction, map );
        }
        else{
	        int count = in.readInt();
	        
	        int[] ids = new int[ count ];
	        boolean[] holds = new boolean[ count ];
	        int[] sizes = new int[ count ];
	        
	        for( int i = 0; i < count; i++ ){
	            ids[i] = in.readInt();
	            holds[i] = in.readBoolean();
	            sizes[i] = in.readInt();
	        }
	        
	        return new RetroFlapDockStationLayout( ids, holds, sizes, auto, direction );
        }
    }

    public void write( FlapDockStationLayout layout, XElement element ) {
    	if( layout instanceof RetroFlapDockStationLayout ){
    		RetroFlapDockStationLayout retroLayout = (RetroFlapDockStationLayout)layout;
    		XElement window = element.addElement( "window" );
    		window.addBoolean( "auto", layout.isAutoDirection() );
    		window.addString( "direction", layout.getDirection().name() );

    		XElement children = element.addElement( "children" );
    		for( int i = 0, n = retroLayout.getChildren().length; i<n; i++ ){
    			XElement child = children.addElement( "child" );
    			child.addInt( "id", retroLayout.getChildren()[i] );
    			child.addBoolean( "hold", retroLayout.getHolds()[i] );
    			child.addInt( "size", retroLayout.getSizes()[i] );
    		}
    	}
    	else{
    		XElement window = element.addElement( "window" );
    		window.addBoolean( "auto", layout.isAutoDirection() );
    		window.addString( "direction", layout.getDirection().name() );

    		XElement children = element.addElement( "placeholders" );
    		layout.getPlaceholders().write( children );
    	}
    }
    
    public FlapDockStationLayout read( XElement element, PlaceholderStrategy placeholders ){
        XElement window = element.getElement( "window" );
        XElement xplaceholders = element.getElement( "placeholders" );
        if( xplaceholders != null ){
        	PlaceholderMap map = new PlaceholderMap( xplaceholders, placeholders );
        	map.setPlaceholderStrategy( null );
	        return new FlapDockStationLayout(  
	                window.getBoolean( "auto" ),
	                Direction.valueOf( window.getString( "direction" ) ),
	                map );
        }
        else{
	        XElement children = element.getElement( "children" );
	        XElement[] child = children.getElements( "child" );
	        
	        int[] ids = new int[ child.length ];
	        boolean[] holds = new boolean[ child.length ];
	        int[] sizes = new int[ child.length ];
	        
	        XAttribute sizeAttribute = window.getAttribute( "size" );
	        if( sizeAttribute != null ){
	            int size = sizeAttribute.getInt();
	            for( int i = 0; i < sizes.length; i++ )
	                sizes[i] = size;
	        }
	        else{
	            for( int i = 0, n = child.length; i<n; i++ ){
	                sizes[i] = child[i].getInt( "size" );
	            }
	        }
	        
	        for( int i = 0, n = child.length; i<n; i++ ){
	            ids[i] = child[i].getInt( "id" );
	            holds[i] = child[i].getBoolean( "hold" );
	        }
	        
	        return new RetroFlapDockStationLayout( ids, holds, 
	                sizes, window.getBoolean( "auto" ),
	                Direction.valueOf( window.getString( "direction" ) ));
        }
    }
    
    /**
     * Creates an instance of a {@link FlapDockStation}.
     * @return a new object 
     */
    protected FlapDockStation createStation(){
        return new FlapDockStation();
    }
}
