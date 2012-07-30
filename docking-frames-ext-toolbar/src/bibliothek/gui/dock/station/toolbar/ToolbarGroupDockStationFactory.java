/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.station.toolbar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.layout.DockLayoutInfo;
import bibliothek.gui.dock.layout.LocationEstimationMap;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupProperty;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.util.Path;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A factory used to store and load {@link ToolbarGroupDockStation}s.
 * 
 * @author Benjamin Sigg
 */
public class ToolbarGroupDockStationFactory implements DockFactory<ToolbarGroupDockStation, ToolbarGroupDockPerspective, ToolbarGroupDockStationLayout> {
	/** the unique, unmodifiable identifier of this factory */
	public static final String ID = "ToolbarGroupDockStationFactory";

	@Override
	public String getID(){
		return ID;
	}

	@Override
	public ToolbarGroupDockStationLayout getLayout( ToolbarGroupDockStation element, Map<Dockable, Integer> children ){
		final PlaceholderMap map = element.getPlaceholders( children );
		return new ToolbarGroupDockStationLayout( map, element.getExpandedState() );
	}

	@Override
	public ToolbarGroupDockStationLayout getPerspectiveLayout( ToolbarGroupDockPerspective element, Map<PerspectiveDockable, Integer> children ){
		return new ToolbarGroupDockStationLayout( element.toMap( children ), element.getExpandedState() );
	}

	@Override
	public void setLayout( ToolbarGroupDockStation element, ToolbarGroupDockStationLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ){
		element.setExpandedState( layout.getState(), false );
		element.setPlaceholders( layout.getPlaceholders(), children );
	}

	@Override
	public void setLayout( ToolbarGroupDockStation element, ToolbarGroupDockStationLayout layout, PlaceholderStrategy placeholders ){
		element.setExpandedState( layout.getState(), false );
	}

	@Override
	public void write( ToolbarGroupDockStationLayout layout, DataOutputStream out ) throws IOException{
		Version.write( out, Version.VERSION_1_1_1 );
		layout.getPlaceholders().write( out );
		out.writeUTF( layout.getState().name() );
	}

	@Override
	public void write( ToolbarGroupDockStationLayout layout, XElement element ){
		final XElement xplaceholders = element.addElement( "placeholders" );
		layout.getPlaceholders().write( xplaceholders );
		element.addElement( "expanded" ).setString( layout.getState().name() );
	}

	@Override
	public ToolbarGroupDockStationLayout read( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException{
		final Version version = Version.read( in );
		version.checkCurrent();

		final PlaceholderMap map = new PlaceholderMap( in, placeholders );
		map.setPlaceholderStrategy( null );

		final ExpandedState state = ExpandedState.valueOf( in.readUTF() );

		return new ToolbarGroupDockStationLayout( map, state );
	}

	@Override
	public ToolbarGroupDockStationLayout read( XElement element, PlaceholderStrategy strategy ){
		final XElement xplaceholders = element.getElement( "placeholders" );
		final XElement xexpanded = element.getElement( "expanded" );

		final PlaceholderMap map = new PlaceholderMap( xplaceholders, strategy );
		map.setPlaceholderStrategy( null );

		ExpandedState state = ExpandedState.SHRUNK;
		if( xexpanded != null ) {
			state = ExpandedState.valueOf( xexpanded.getString() );
		}

		return new ToolbarGroupDockStationLayout( map, state );
	}

	@Override
	public void estimateLocations( ToolbarGroupDockStationLayout layout, final LocationEstimationMap children ){
		DockablePlaceholderList.simulatedRead( layout.getPlaceholders(), new PlaceholderListItemAdapter<Dockable, Dockable>(){
			@Override
			public Dockable convert( ConvertedPlaceholderListItem item ){
				final int column = item.getInt( "index" );
			
				DockablePlaceholderList.simulatedRead( item.getPlaceholderMap(), new PlaceholderListItemAdapter<Dockable, Dockable>(){
					@Override
					public Dockable convert( ConvertedPlaceholderListItem item ){
						int id = item.getInt( "id" );
						int line = item.getInt( "index" );
						Path placeholder = null;
						if( item.contains( "placeholder" )){
							placeholder = new Path( item.getString( "placeholder" ) );
						}
						children.setLocation( id, new ToolbarGroupProperty( column, line, placeholder ) );
						for( int i = 0, n = children.getSubChildCount( id ); i<n; i++ ){
							DockLayoutInfo info = children.getSubChild( id, i );
							info.setLocation( new ToolbarGroupProperty( column, line, info.getPlaceholder() ) );
						}
						return null;
					}
				});
				return null;
			}
		});
	}

	@Override
	public ToolbarGroupDockStation layout( ToolbarGroupDockStationLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ){
		final ToolbarGroupDockStation station = createStation();
		setLayout( station, layout, children, placeholders );
		return station;
	}

	@Override
	public ToolbarGroupDockStation layout( ToolbarGroupDockStationLayout layout, PlaceholderStrategy placeholders ){
		final ToolbarGroupDockStation station = createStation();
		setLayout( station, layout, placeholders );
		return station;
	}

	@Override
	public ToolbarGroupDockPerspective layoutPerspective( ToolbarGroupDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
		return new ToolbarGroupDockPerspective( layout, children );
	}

	@Override
	public void layoutPerspective( ToolbarGroupDockPerspective perspective, ToolbarGroupDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
		perspective.read( layout, children );
	}

	/**
	 * Creates a new {@link ToolbarGroupDockStation}.
	 * 
	 * @return the new station, not <code>null</code>
	 */
	protected ToolbarGroupDockStation createStation(){
		return new ToolbarGroupDockStation();
	}
}
