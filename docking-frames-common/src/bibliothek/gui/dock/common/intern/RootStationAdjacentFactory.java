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
package bibliothek.gui.dock.common.intern;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.station.CommonDockStation;
import bibliothek.gui.dock.common.perspective.CStationPerspective;
import bibliothek.gui.dock.common.perspective.CommonElementPerspective;
import bibliothek.gui.dock.layout.AdjacentDockFactory;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.Path;
import bibliothek.util.xml.XElement;

/**
 * This factory stores the property {@link CStation#getTypeId()} for any
 * {@link CStation} that is found.
 * @author Benjamin Sigg
 */
public class RootStationAdjacentFactory implements AdjacentDockFactory<Path>{
	/** The unique identifier of this factory */
	public static final String FACTORY_ID = "dock.RootStationAdjacentFactory";
	
	public boolean interested( DockElement element ){
		return element instanceof CommonDockStation<?,?>;
	}
	
	public boolean interested( PerspectiveElement element ){
		if( element instanceof CommonElementPerspective ){
			CStationPerspective station = ((CommonElementPerspective)element).getElement().asStation();
			return station != null;
		}
		return false;
	}

	public String getID(){
		return FACTORY_ID;
	}

	public Path getLayout( DockElement element, Map<Dockable, Integer> children ){
		return ((CommonDockStation<?,?>)element).getStation().getTypeId();
	}
	
	public Path getPerspectiveLayout( PerspectiveElement element, Map<PerspectiveDockable, Integer> children ){
		CStationPerspective station = ((CommonElementPerspective)element).getElement().asStation();
		return station.getTypeId();
	}

	public Path read( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException{
		byte version = in.readByte();
		if( version != 0 ){
			throw new IOException( "unknown version: " + version );
		}
		if( in.readBoolean() ){
			return new Path( in.readUTF() );
		}
		else{
			return null;
		}
	}

	public Path read( XElement element, PlaceholderStrategy placeholders ){
		XElement xtype = element.getElement( "type" );
		if( xtype == null ){
			return null;
		}
		return new Path( xtype.getString() );
	}

	public void setLayout( DockElement element, Path layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ){
		if( !(element instanceof CommonDockStation<?, ?>)){
			throw new IllegalArgumentException( "expected the 'element' to be a '" + CommonDockStation.class.getSimpleName() + "', but instead it was a '" + element.getClass().getName() + "'.\n" + 
					"The reason for this exception may be be:\n - a DockElement that was registered with the wrong unique identifier\n - the type of a DockElement was changed\n - a SingleCDockableFactory creating the wrong type of items.\n"+
					"Type and toString of 'element': " + element.getClass().getName() + ",\n" + element);
		}
		CStation<?> station = ((CommonDockStation<?, ?>)element).getStation();
		Path typeId = station.getTypeId();
		if( typeId != null && !typeId.equals( layout )){
			throw new IllegalArgumentException( "expected the 'element' to have type id '" + layout + "', but instead it was '" + typeId + "'.\n" +
					"The reason for this exception may be be:\n - a DockElement that was registered with the wrong unique identifier\n - the type of a DockElement was changed\n - a SingleCDockableFactory creating the wrong type of items.\n"+
					"Type and toString of 'element': " + station.getClass().getName() + ",\n" + station);
		}
	}

	public void setLayout( DockElement element, Path layout, PlaceholderStrategy placeholders ){
		setLayout( element, layout, null, placeholders );
	}

	public void write( Path layout, DataOutputStream out ) throws IOException{
		out.writeByte( 0 );
		if( layout == null ){
			out.writeBoolean( false );
		}
		else{
			out.writeBoolean( true );
			out.writeUTF( layout.toString() );
		}
	}

	public void write( Path layout, XElement element ){
		if( layout != null ){
			element.addElement( "type" ).setString( layout.toString() );
		}
	}
}
