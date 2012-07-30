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
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.StackDockStation;
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
import bibliothek.util.xml.XElement;

/**
 * A {@link DockFactory} that can read and write instances of
 * {@link StackDockStation}. This factory will create new instances of
 * {@link StackDockStation} through the method {@link #createStation()}.
 * 
 * @author Benjamin Sigg
 */
public class StackDockStationFactory implements DockFactory<StackDockStation, StackDockPerspective, StackDockStationLayout> {
	/** The ID which is returned by {@link #getID()} */
	public static final String ID = "StackDockStationFactory";

	public String getID(){
		return ID;
	}

	public StackDockStationLayout getLayout( StackDockStation station, Map<Dockable, Integer> children ){
		Dockable selectedDockable = station.getFrontDockable();
		int selected = -1;

		if (selectedDockable != null) {
			selected = station.indexOf( selectedDockable );
		}

		PlaceholderMap map = station.getPlaceholders( children );

		return new StackDockStationLayout( selected, map );
	}

	public void estimateLocations( StackDockStationLayout layout, final LocationEstimationMap children ){
		if( layout instanceof RetroStackDockStationLayout ){
			RetroStackDockStationLayout retroLayout = (RetroStackDockStationLayout)layout;
			for (int id : retroLayout.getChildren()) {
				DockLayoutInfo info = children.getChild( id );
				if (info != null) {
					StackDockProperty property = new StackDockProperty( id, info.getPlaceholder() );
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
    				Path placeholder = null;
    				if( item.contains( "placeholder" )){
    					placeholder = new Path( item.getString( "placeholder" ) );
    				}
    				StackDockProperty property = new StackDockProperty( index, placeholder );
    				children.getChild( id ).setLocation( property );
    				
    				for( int i = 0, n = children.getSubChildCount( id ); i<n; i++ ){
    					DockLayoutInfo info = children.getSubChild( id, i );
    					info.setLocation( new StackDockProperty( index, info.getPlaceholder() ) );
    				}
    				return null;
    			}
			});
		}
	}

	public void setLayout( StackDockStation station, StackDockStationLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ){
		DockController controller = station.getController();
		try {
			if (controller != null)
				controller.freezeLayout();

			for (int i = station.getDockableCount() - 1; i >= 0; i--)
				station.remove( i );

			if( layout instanceof RetroStackDockStationLayout ){
				RetroStackDockStationLayout retroLayout = (RetroStackDockStationLayout)layout;
				for (int id : retroLayout.getChildren()) {
					Dockable dockable = children.get( id );
					if (dockable != null) {
						station.drop( dockable );
					}
				}
			}
			else{
				station.setPlaceholders( layout.getPlaceholders().filter( placeholders ), children );
			}

			Dockable selected = children.get( layout.getSelected() );
			if (selected != null) {
				station.setFrontDockable( selected );
			}
		} finally {
			if (controller != null)
				controller.meltLayout();
		}
	}

	public void setLayout( StackDockStation element, StackDockStationLayout layout, PlaceholderStrategy placeholders ){
		// nothing to do
	}
	
	public StackDockPerspective layoutPerspective( StackDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
		StackDockPerspective perspective = new StackDockPerspective();
		layoutPerspective( perspective, layout, children );
		return perspective;
	}
	
	public void layoutPerspective( StackDockPerspective perspective, StackDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
		perspective.read( layout.getPlaceholders(), children, layout.getSelected() );
	}

	public StackDockStation layout( StackDockStationLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ){
		StackDockStation station = createStation();
		setLayout( station, layout, children, placeholders );
		return station;
	}

	public StackDockStation layout( StackDockStationLayout layout, PlaceholderStrategy placeholders ){
		StackDockStation station = createStation();
		setLayout( station, layout, placeholders );
		return station;
	}
	
	public StackDockStationLayout getPerspectiveLayout( StackDockPerspective element, Map<PerspectiveDockable, Integer> children ){
		Integer selected = null;
		if( children != null ){
			selected = children.get( element.getSelection() );
		}
		return new StackDockStationLayout( selected == null ? -1 : selected.intValue(), element.toMap( children ) );
	}

	public void write( StackDockStationLayout layout, DataOutputStream out ) throws IOException{
		if( layout instanceof RetroStackDockStationLayout ){
			RetroStackDockStationLayout retroLayout = (RetroStackDockStationLayout)layout;
			Version.write( out, Version.VERSION_1_0_4 );
			out.writeInt( layout.getSelected() );

			out.writeInt( retroLayout.getChildren().length );
			for (int c : retroLayout.getChildren()){
				out.writeInt( c );
			}
		}
		else{
			Version.write( out, Version.VERSION_1_0_8 );
			out.writeInt( layout.getSelected() );
			layout.getPlaceholders().write( out );
		}
	}

	public StackDockStationLayout read( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException{
		Version version = Version.read( in );
		version.checkCurrent();

		boolean version8 = Version.VERSION_1_0_8.compareTo( version ) <= 0;
		if( version8 ){
			int selected = in.readInt();
			PlaceholderMap map = new PlaceholderMap( in, placeholders );
			map.setPlaceholderStrategy( null );
			return new StackDockStationLayout( selected, map );
		}
		else{
			int selected = in.readInt();
			int count = in.readInt();
			int[] ids = new int[count];
			for (int i = 0; i < count; i++)
				ids[i] = in.readInt();
			return new RetroStackDockStationLayout( selected, ids );
		}
	}

	public void write( StackDockStationLayout layout, XElement element ){
		if (layout.getSelected() >= 0)
			element.addElement( "selected" ).setInt( layout.getSelected() );

		if( layout instanceof RetroStackDockStationLayout ){
			RetroStackDockStationLayout retroLayout = (RetroStackDockStationLayout)layout;
			XElement xchildren = element.addElement( "children" );
			for (int i : retroLayout.getChildren()) {
				xchildren.addElement( "child" ).addInt( "id", i );
			}
		}
		else{
			layout.getPlaceholders().write( element.addElement( "placeholders" ) );
		}
	}

	public StackDockStationLayout read( XElement element, PlaceholderStrategy placeholders ){
		XElement xselected = element.getElement( "selected" );
		int selected = -1;
		if (xselected != null)
			selected = xselected.getInt();

		XElement xplaceholders = element.getElement( "placeholders" );
		if( xplaceholders == null ){
			XElement xchildren = element.getElement( "children" );
			int[] ids;
	
			if (xchildren != null) {
				XElement[] children = xchildren.getElements( "child" );
				ids = new int[children.length];
				for (int i = 0, n = children.length; i < n; i++)
					ids[i] = children[i].getInt( "id" );
			} else {
				ids = new int[] {};
			}
	
			return new RetroStackDockStationLayout( selected, ids );
		}
		else{
			PlaceholderMap map = new PlaceholderMap( xplaceholders, placeholders );
			return new StackDockStationLayout( selected, map );
		}
	}

	/**
	 * Called when a new {@link StackDockStation} is required.
	 * 
	 * @return a new station
	 */
	protected StackDockStation createStation(){
		return new StackDockStation();
	}
}
