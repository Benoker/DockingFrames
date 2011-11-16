/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.station.toolbar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ContainerLineStation;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.layout.LocationEstimationMap;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A factory used to read and write the layout of a {@link ContainerLineStation}.
 * @author Benjamin Sigg
 * @author Herve Guillaume
 */
public class ContainerLineStationFactory implements DockFactory<ContainerLineStation, PerspectiveElement, ContainerLineStationLayout>{
	/** the unique, unmodifiable identifier of this factory */
	public static final String ID = "ContainerLineStationFactory";
	
	@Override
	public String getID(){
		return ID;
	}

	@Override
	public ContainerLineStationLayout getLayout( ContainerLineStation element, Map<Dockable, Integer> children ){
		return new ContainerLineStationLayout( element.getPlaceholders( children ) );
	}

	@Override
	public ContainerLineStationLayout getPerspectiveLayout( PerspectiveElement element, Map<PerspectiveDockable, Integer> children ){
		return null;
	}

	@Override
	public void setLayout( ContainerLineStation element, ContainerLineStationLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ){
		element.setPlaceholders( layout.getPlaceholders(), children );
	}

	@Override
	public void setLayout( ContainerLineStation element, ContainerLineStationLayout layout, PlaceholderStrategy placeholders ){
		// nothing to do
	}

	@Override
	public void write( ContainerLineStationLayout layout, DataOutputStream out ) throws IOException{
		Version.write( out, Version.VERSION_1_1_1 );
		layout.getPlaceholders().write( out );
	}

	@Override
	public void write( ContainerLineStationLayout layout, XElement element ){
		layout.getPlaceholders().write( element.addElement( "placeholders" ) );
	}

	@Override
	public ContainerLineStationLayout read( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException{
		Version version = Version.read( in );
		version.checkCurrent();
		
		PlaceholderMap map = new PlaceholderMap( in, placeholders );
		map.setPlaceholderStrategy( null );
		return new ContainerLineStationLayout( map );
	}

	@Override
	public ContainerLineStationLayout read( XElement element, PlaceholderStrategy placeholders ){
		XElement xplaceholders = element.getElement( "placeholders" );
		
		PlaceholderMap map = new PlaceholderMap( xplaceholders, placeholders );
		map.setPlaceholderStrategy( null );
		return new ContainerLineStationLayout( map );
	}

	@Override
	public void estimateLocations( ContainerLineStationLayout layout, LocationEstimationMap children ){
		// TODO pending
	}

	@Override
	public ContainerLineStation layout( ContainerLineStationLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ){
		ContainerLineStation station = createStation();
		setLayout( station, layout, children, placeholders );
		return station;
	}

	@Override
	public ContainerLineStation layout( ContainerLineStationLayout layout, PlaceholderStrategy placeholders ){
		ContainerLineStation station = createStation();
		setLayout( station, layout, placeholders );
		return station;
	}

	@Override
	public PerspectiveElement layoutPerspective( ContainerLineStationLayout layout, Map<Integer, PerspectiveDockable> children ){
		return null;
	}

	@Override
	public void layoutPerspective( PerspectiveElement perspective, ContainerLineStationLayout layout, Map<Integer, PerspectiveDockable> children ){
		
	}
	
	/**
	 * Creates a new {@link ContainerLineStation}.
	 * @return the new station, not <code>null</code>
	 */
	protected ContainerLineStation createStation(){
		return new ContainerLineStation();
	}
}
