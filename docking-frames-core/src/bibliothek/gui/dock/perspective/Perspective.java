/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.perspective;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.layout.AdjacentDockFactory;
import bibliothek.gui.dock.layout.DockLayout;
import bibliothek.gui.dock.layout.DockLayoutComposition;
import bibliothek.gui.dock.layout.DockLayoutInfo;
import bibliothek.gui.dock.layout.DockSituation;
import bibliothek.gui.dock.layout.DockSituationIgnore;
import bibliothek.util.xml.XElement;

/**
 * A {@link Perspective} is a helper class that allows clients to create a layout without the need to
 * create {@link DockStation}s or {@link Dockable}s.<br>
 * Perspectives cannot be created directly, clients must first set up the {@link DockSituation} that
 * would load the layout and then call the method {@link DockSituation#createPerspective()} to create
 * a {@link Perspective} which in return is able to write a file that matches the need of its 
 * owner {@link DockSituation}.
 * @author Benjamin Sigg
 */
public abstract class Perspective {
	/** Provides  algorithms used to convert the {@link PerspectiveElement}s */
	private DockSituation situation;

	/**
	 * Creates a new {@link Perspective} using <code>situation</code> to read and write
	 * items.
	 * @param situation the set of factories to use
	 */
	public Perspective( DockSituation situation ){
		this.situation = situation;
	}
	
	/**
	 * Reads the contents of <code>root</code> and returns them in a map.
	 * @param root the data to read
	 * @return the content of <code>root</code>
	 */
	public Map<String, PerspectiveStation> readXML( XElement root ){
		Map<String, DockLayoutComposition> map = situation.readCompositionsXML( root );
		Map<String, PerspectiveStation> result = new HashMap<String, PerspectiveStation>();
		for( Map.Entry<String, DockLayoutComposition> entry : map.entrySet() ){
			PerspectiveElement element = convert( entry.getValue() );
			PerspectiveStation station = element == null ? null : element.asStation();
			if( station != null ){
				result.put( entry.getKey(), station );
			}
		}
		return result;
	}

	/**
	 * Converts the content of <code>stations</code> to XML.
	 * @param stations the items to write
	 * @param element the element to write into
	 */
	public void writeXML( Map<String, PerspectiveStation> stations, XElement element ){
		Map<String, DockLayoutComposition> map = new HashMap<String, DockLayoutComposition>();
		for( Map.Entry<String, PerspectiveStation> entry : stations.entrySet() ){
			DockLayoutComposition composition = convert( entry.getValue() );
			if( composition != null ){
				map.put( entry.getKey(), composition );
			}
		}

		situation.writeCompositionsXML( map, element );
	}

	/**
	 * Reads the contents of <code>in</code> and returns them in a map.
	 * @param in the stream to read from
	 * @return the content of <code>in</code>
	 * @throws IOException if an I/O-error occurs
	 */
	public Map<String, PerspectiveStation> read( DataInputStream in ) throws IOException {
		Map<String, DockLayoutComposition> map = situation.readCompositions( in );
		Map<String, PerspectiveStation> result = new HashMap<String, PerspectiveStation>();
		for( Map.Entry<String, DockLayoutComposition> entry : map.entrySet() ){
			PerspectiveElement element = convert( entry.getValue() );
			PerspectiveStation station = element == null ? null : element.asStation();
			if( station != null ){
				result.put( entry.getKey(), station );
			}
		}
		return result;
	}

	/**
	 * Writes the layout created by <code>stations</code> to <code>out</code>. The data written
	 * by this method can be read by {@link DockSituation#read(DataInputStream)}
	 * @param stations the root-stations to store
	 * @param out the stream to write into
	 * @throws IOException if an I/O-error occurred
	 */
	public void write( Map<String, PerspectiveStation> stations, DataOutputStream out ) throws IOException {
		Map<String, DockLayoutComposition> map = new HashMap<String, DockLayoutComposition>();
		for( Map.Entry<String, PerspectiveStation> entry : stations.entrySet() ){
			DockLayoutComposition composition = convert( entry.getValue() );
			if( composition != null ){
				map.put( entry.getKey(), composition );
			}
		}

		situation.writeCompositions( map, out );
	}

	/**
	 * Converts <code>element</code> using the {@link DockFactory}s that are registered at this
	 * perspective.
	 * @param element the element to convert, not <code>null</code>
	 * @return the converted element
	 * @throws IllegalArgumentException if a factory is missing
	 */
	@SuppressWarnings("unchecked")
	public DockLayoutComposition convert( PerspectiveElement element ){
		if( element == null ){
			throw new IllegalArgumentException( "element must not be null" );
		}
		
		DockSituationIgnore ignore = situation.getIgnore();
		if( ignore != null && ignore.ignoreElement( element )){
			return null;
		}
		
		String id = getID( element );
		DockFactory<DockElement, PerspectiveElement, Object> factory = (DockFactory<DockElement, PerspectiveElement, Object>)getFactory( id );
		if( factory == null ){
			throw new IllegalArgumentException( "missing factory: " + element.getFactoryID() );
		}

		Map<PerspectiveDockable, Integer> ids = new HashMap<PerspectiveDockable, Integer>();
		List<DockLayoutComposition> children = new ArrayList<DockLayoutComposition>();

		boolean ignoreChildren = false;
		PerspectiveStation station = element.asStation();
		if( station != null ){
			if( ignore == null || !ignore.ignoreChildren( station )){
				int index = 0;
				for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
					PerspectiveDockable dockable = station.getDockable( i );
					DockLayoutComposition composition = convert( dockable );
					if( composition != null ){
						children.add( composition );
						ids.put( dockable, index++ );
					}
				}
			}
			else{
				ignoreChildren = true;
			}
		}

		Object data = factory.getPerspectiveLayout( element, ids );
		DockLayout<Object> layout = new DockLayout<Object>( id, data );
		DockLayoutInfo info = new DockLayoutInfo( layout );
		PerspectiveDockable dockable = element.asDockable();
		if( dockable != null ){
			info.setPlaceholder( dockable.getPlaceholder() );
		}
		
		List<DockLayout<?>> adjacentLayouts = null;
		for( AdjacentDockFactory<?> adjacent : situation.getAdjacentFactorys().values() ){
			if( adjacent.interested( element )){
				data = adjacent.getPerspectiveLayout( element, ids );
				if( data != null ){
					layout = new DockLayout<Object>( adjacent.getID(), data );
					if( adjacentLayouts == null ){
						adjacentLayouts = new ArrayList<DockLayout<?>>();
					}
					adjacentLayouts.add( layout );
				}
			}
		}
		
		return new DockLayoutComposition( info, adjacentLayouts, children, ignoreChildren );
	}

	/**
	 * Converts <code>composition</code> using the {@link DockFactory}s that are registered at
	 * this perspective.
	 * @param composition the element to convert, not <code>null</code>
	 * @return the converted element
	 */
	@SuppressWarnings("unchecked")
	public PerspectiveElement convert( DockLayoutComposition composition ){
		DockLayoutInfo info = composition.getLayout();
		if( info == null )
			return null;

		DockLayout<Object> layout = (DockLayout<Object>)info.getDataLayout();
		if( layout == null || layout.getData() == null )
			return null;

		DockFactory<DockElement, PerspectiveElement, Object> factory = (DockFactory<DockElement, PerspectiveElement, Object>)getFactory( layout.getFactoryID() );
		if( factory == null )
			return null;

		PerspectiveElement result = null;
		
		if( composition.isIgnoreChildren() ){
			for( DockLayoutComposition childComposition : composition.getChildren() ){
				convert( childComposition );
			}

			result = factory.layoutPerspective( layout.getData(), null );
		}
		else{
			Map<Integer, PerspectiveDockable> children = new HashMap<Integer, PerspectiveDockable>();
			int index = 0;

			for( DockLayoutComposition childComposition : composition.getChildren() ){
				PerspectiveElement child = convert( childComposition );
				if( child != null ){
					PerspectiveDockable dockable = child.asDockable();
					if( dockable != null ){
						children.put( index, dockable );
					}
				}

				index++;
			}

			result = factory.layoutPerspective( layout.getData(), children );
		}

		return result;
	}
	
	/**
	 * Gets the {@link DockSituation} which is used to convert {@link PerspectiveElement}s 
	 * and {@link DockLayoutComposition}s.
	 * @return the situation, not <code>null</code>
	 */
	public DockSituation getSituation(){
		return situation;
	}

	/**
	 * Gets the identifier of the factory that is responsible for <code>element</code>.
	 * @param element the element to store
	 * @return the factory that is responsible for <code>element</code>
	 */
	protected abstract String getID( PerspectiveElement element );

	/**
	 * Gets the factory which is responsible to store an element whose id is <code>id</code>.
	 * @param id the identifier of the element to store or read
	 * @return the factory, can be <code>null</code>
	 */
	protected abstract DockFactory<?, ?, ?> getFactory( String id );
}
