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
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.layout.DockLayoutInfo;
import bibliothek.gui.dock.layout.LocationEstimationMap;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.Path;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A factory used to read and write the layout of a
 * {@link ToolbarContainerDockStation}.
 * 
 * @author Benjamin Sigg
 * @author Herve Guillaume
 */
public class ToolbarContainerDockStationFactory implements DockFactory<ToolbarContainerDockStation, ToolbarContainerDockPerspective, ToolbarContainerDockStationLayout> {
	/** the unique, unmodifiable identifier of this factory */
	public static final String ID = "ToolbarContainerDockStationFactory";

	@Override
	public String getID(){
		return ID;
	}

	/**
	 * Creates a {@link ToolbarContainerConverter} which will be used for one call.
	 * @return the new converter, not <code>null</code>
	 */
	protected ToolbarContainerConverter createConverter(){
		return new DefaultToolbarContainerConverter();
	}
	
	@Override
	public ToolbarContainerDockStationLayout getLayout( ToolbarContainerDockStation element, Map<Dockable, Integer> children ){
		return new ToolbarContainerDockStationLayout( element.getPlaceholders( children ) );
	}

	@Override
	public ToolbarContainerDockStationLayout getPerspectiveLayout( ToolbarContainerDockPerspective element, Map<PerspectiveDockable, Integer> children ){
		PlaceholderMap map = createConverter().getPlaceholders( element, children );
		return new ToolbarContainerDockStationLayout( map );
	}

	@Override
	public void setLayout( ToolbarContainerDockStation element, ToolbarContainerDockStationLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ){
		element.setPlaceholders( layout.getPlaceholders(), children );
	}

	@Override
	public void setLayout( ToolbarContainerDockStation element, ToolbarContainerDockStationLayout layout, PlaceholderStrategy placeholders ){
		// nothing to do
	}

	@Override
	public void write( ToolbarContainerDockStationLayout layout, DataOutputStream out ) throws IOException{
		Version.write( out, Version.VERSION_1_1_1 );
		layout.getPlaceholders().write( out );
	}

	@Override
	public void write( ToolbarContainerDockStationLayout layout, XElement element ){
		layout.getPlaceholders().write( element.addElement( "placeholders" ) );
	}

	@Override
	public ToolbarContainerDockStationLayout read( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException{
		final Version version = Version.read( in );
		version.checkCurrent();

		final PlaceholderMap map = new PlaceholderMap( in, placeholders );
		map.setPlaceholderStrategy( null );
		return new ToolbarContainerDockStationLayout( map );
	}

	@Override
	public ToolbarContainerDockStationLayout read( XElement element, PlaceholderStrategy placeholders ){
		final XElement xplaceholders = element.getElement( "placeholders" );

		final PlaceholderMap map = new PlaceholderMap( xplaceholders, placeholders );
		map.setPlaceholderStrategy( null );
		return new ToolbarContainerDockStationLayout( map );
	}

	@Override
	public void estimateLocations( ToolbarContainerDockStationLayout layout, final LocationEstimationMap children ){
		PlaceholderMap map = layout.getPlaceholders();
		
		if( !map.getFormat().equals( new Path("dock.ToolbarContainerStation") )){
			throw new IllegalArgumentException( "unknown type: " + map.getFormat() );
		}
		
		PlaceholderMap list = map.getMap( map.newKey( "content" ), "list" );
		DockablePlaceholderList.simulatedRead( list, new PlaceholderListItemAdapter<Dockable, Dockable>(){
			@Override
			public Dockable convert( ConvertedPlaceholderListItem item ){
				int id = item.getInt( "id" );
				int index = item.getInt( "index" );
				Path placeholder = null;
				if( item.contains( "placeholder" )){
					placeholder = new Path( item.getString( "placeholder" ) );
				}
				children.setLocation( id, new ToolbarContainerProperty( index, placeholder ) );
				for( int i = 0, n = children.getSubChildCount( id ); i<n; i++ ){
					DockLayoutInfo info = children.getSubChild( id, i );
					info.setLocation( new ToolbarContainerProperty( index, info.getPlaceholder() ) );
				}
				return null;
			}
		});
	}

	@Override
	public ToolbarContainerDockStation layout( ToolbarContainerDockStationLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ){
		final ToolbarContainerDockStation station = createStation();
		setLayout( station, layout, children, placeholders );
		return station;
	}

	@Override
	public ToolbarContainerDockStation layout( ToolbarContainerDockStationLayout layout, PlaceholderStrategy placeholders ){
		final ToolbarContainerDockStation station = createStation();
		setLayout( station, layout, placeholders );
		return station;
	}

	@Override
	public ToolbarContainerDockPerspective layoutPerspective( ToolbarContainerDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
		return new ToolbarContainerDockPerspective( layout, children );
	}

	@Override
	public void layoutPerspective( ToolbarContainerDockPerspective perspective, ToolbarContainerDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
		perspective.read( layout, children );
	}

	/**
	 * Creates a new {@link ToolbarContainerDockStation}.
	 * 
	 * @return the new station, not <code>null</code>
	 */
	protected ToolbarContainerDockStation createStation(){
		return new ToolbarContainerDockStation( ToolbarContainerDockStation.DEFAULT_ORIENTATION );
	}
}
