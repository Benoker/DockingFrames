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
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.layout.DockLayoutInfo;
import bibliothek.gui.dock.layout.LocationEstimationMap;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.util.Path;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A factory for reading and writing {@link ToolbarDockStation}s.
 * 
 * @author Benjamin Sigg
 */
public class ToolbarDockStationFactory implements DockFactory<ToolbarDockStation, ToolbarDockPerspective, ToolbarDockStationLayout> {
	/** the unique, unmodifiable identifier of this factory */
	public static final String ID = "ToolbarDockStationFactory";

	@Override
	public String getID(){
		return ID;
	}

	@Override
	public ToolbarDockStationLayout getLayout( ToolbarDockStation element, Map<Dockable, Integer> children ){
		PlaceholderMap map = element.getPlaceholders(children);
		return new ToolbarDockStationLayout(map, element.getExpandedState());
	}

	@Override
	public ToolbarDockStationLayout getPerspectiveLayout( ToolbarDockPerspective element, Map<PerspectiveDockable, Integer> children ){
		PlaceholderMap map = element.getPlaceholders( children );
		return new ToolbarDockStationLayout( map, element.getExpandedState() );
	}

	@Override
	public void setLayout( ToolbarDockStation element, ToolbarDockStationLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ){
		element.setExpandedState( layout.getState(), false );
		element.setPlaceholders( layout.getPlaceholders(), children );
	}

	@Override
	public void setLayout( ToolbarDockStation element, ToolbarDockStationLayout layout, PlaceholderStrategy placeholders ){
		element.setExpandedState( layout.getState(), false );
	}

	@Override
	public void write( ToolbarDockStationLayout layout, DataOutputStream out ) throws IOException{
		Version.write( out, Version.VERSION_1_1_1 );
		layout.getPlaceholders().write( out );
		out.writeUTF( layout.getState().name() );
	}

	@Override
	public void write( ToolbarDockStationLayout layout, XElement element ){
		final XElement xplaceholders = element.addElement( "placeholders" );
		layout.getPlaceholders().write( xplaceholders );
		element.addElement( "expanded" ).setString( layout.getState().name() );
	}

	@Override
	public ToolbarDockStationLayout read( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException{
		final Version version = Version.read( in );
		version.checkCurrent();

		final PlaceholderMap map = new PlaceholderMap( in, placeholders );
		map.setPlaceholderStrategy( null );

		final ExpandedState state = ExpandedState.valueOf( in.readUTF() );

		return new ToolbarDockStationLayout( map, state );
	}

	@Override
	public ToolbarDockStationLayout read( XElement element, PlaceholderStrategy strategy ){
		final XElement xplaceholders = element.getElement( "placeholders" );
		final XElement xexpanded = element.getElement( "expanded" );

		final PlaceholderMap map = new PlaceholderMap( xplaceholders, strategy );
		map.setPlaceholderStrategy( null );

		ExpandedState state = ExpandedState.SHRUNK;
		if( xexpanded != null ) {
			state = ExpandedState.valueOf( xexpanded.getString() );
		}

		return new ToolbarDockStationLayout( map, state );
	}

	@Override
	public void estimateLocations( ToolbarDockStationLayout layout, final LocationEstimationMap children ){
		DockablePlaceholderList.simulatedRead( layout.getPlaceholders(), new PlaceholderListItemAdapter<Dockable, Dockable>(){
			@Override
			public Dockable convert( ConvertedPlaceholderListItem item ){
				int id = item.getInt( "id" );
				int index = item.getInt( "index" );
				Path placeholder = null;
				if( item.contains( "placeholder" )){
					placeholder = new Path( item.getString( "placeholder" ) );
				}
				children.setLocation( id, new ToolbarProperty( index, placeholder ) );
				for( int i = 0, n = children.getSubChildCount( id ); i<n; i++ ){
					DockLayoutInfo info = children.getSubChild( id, i );
					info.setLocation( new ToolbarProperty( index, info.getPlaceholder() ) );
				}
				return null;
			}
		});
	}

	@Override
	public ToolbarDockStation layout( ToolbarDockStationLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ){
		final ToolbarDockStation station = createStation();
		setLayout( station, layout, children, placeholders );
		return station;
	}

	@Override
	public ToolbarDockStation layout( ToolbarDockStationLayout layout, PlaceholderStrategy placeholders ){
		final ToolbarDockStation station = createStation();
		setLayout( station, layout, placeholders );
		return station;
	}

	@Override
	public ToolbarDockPerspective layoutPerspective( ToolbarDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
		return new ToolbarDockPerspective( layout, children );
	}

	@Override
	public void layoutPerspective( ToolbarDockPerspective perspective, ToolbarDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
		perspective.read( layout, children );
	}

	/**
	 * Creates a new {@link ToolbarDockStation}.
	 * 
	 * @return the new station, not <code>null</code>
	 */
	protected ToolbarDockStation createStation(){
		return new ToolbarDockStation();
	}
}
