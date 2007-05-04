/**
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

package bibliothek.gui.dock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link DockSituation} that does not load or store all {@link DockElement DockElements}.
 * All elements which are registered by {@link #put(DockElement)} are stored in an
 * internal list. On writing, just a unique id is written to the stream. 
 * A {@link DockFactory} is still necessary for these elements, but the factory may
 * just do nothing.
 * @author Benjamin Sigg
 */
public class PredefinedDockSituation extends DockSituation {
	/** A mapping from ids to a list of elements which must not be created by a factory */
	private Map<String, DockElement> stringToElement = new HashMap<String, DockElement>();
	/** A mapping from a list of elements to their ids */
	private Map<DockElement, String> elementToString = new HashMap<DockElement, String>();
	
	private static final String KNOWN = "known - ";
	private static final String UNKNOWN = "unknown - ";
	
	private final PreloadFactory factory = new PreloadFactory();
	
	/**
	 * Registers an element at this situation. When a stream is read, this
	 * element will be returned instead a newly created element (assuming
	 * that the element was written into the stream). The key for
	 * the element is generated automatically
	 * @param element the element
	 */
	public void put( DockElement element ){
		put( String.valueOf( stringToElement.size() ), element );
	}
	
	/**
	 * Registers an element at this situation. When a stream is read, this
	 * element will be returned instead a newly created element (assuming
	 * that the element was written into the stream).
	 * @param key the key of the element
	 * @param element the element
	 * @throws IllegalArgumentException if the key is already used
	 */
	public void put( String key, DockElement element ){
		if( stringToElement.containsKey( key ))
			throw new IllegalArgumentException( "Key does already exist: " + key );
		
		stringToElement.put( key, element );
		elementToString.put( element, key );
	}

	@Override
	protected String getID(DockElement dockable) {
		String key = elementToString.get( dockable );
		if( key == null )
			return UNKNOWN + super.getID( dockable );
		else
			return KNOWN;
	}
	
	@Override
	protected String getID(DockFactory<?> factory) {
		if( factory == this.factory )
			return KNOWN;
		else
			return UNKNOWN + super.getID(factory);
	}
	
	@Override
	protected DockFactory<? extends DockElement> getFactory(String id) {
		if( KNOWN.equals( id ))
			return factory;
		else
			return super.getFactory( id );
	}
	
	/**
	 * A factory which uses other factories as delegate. This factory does
	 * not always use the delegates, sometimes it does just read an element
	 * which was predefined in {@link PredefinedDockSituation}.
	 * @author Benjamin Sigg
	 */
	private class PreloadFactory implements DockFactory<DockElement>{
		public String getID() {
			return KNOWN;
		}

		@SuppressWarnings( "unchecked" )
		public DockElement read(Map<Integer, Dockable> children, boolean ignore, DataInputStream in) throws IOException {
			String key = in.readUTF();
			String factory = in.readUTF();
			DockElement preloaded = stringToElement.get( key );
			if( preloaded == null )
				return null;
			
			DockFactory<DockElement> delegate = (DockFactory<DockElement>)getFactory( factory );
			if( delegate == null )
				return null;
			
			delegate.read( children, ignore, preloaded, in );
			
			return preloaded;
		}

		@SuppressWarnings( "unchecked" )
		public void read(Map<Integer, Dockable> children, boolean ignore, DockElement preloaded, DataInputStream in) throws IOException {
			in.readUTF();
			String factory = in.readUTF();
			DockFactory<DockElement> delegate = (DockFactory<DockElement>)getFactory( factory );
			if( delegate != null ){
				delegate.read( children, ignore, preloaded, in );
			}
		}

		@SuppressWarnings( "unchecked" )
		public void write(DockElement element, Map<Dockable, Integer> children, DataOutputStream out) throws IOException {
			String factory = UNKNOWN + PredefinedDockSituation.super.getID( element );
			out.writeUTF( elementToString.get( element ));
			out.writeUTF( factory );
			DockFactory<DockElement> delegate = (DockFactory<DockElement>)getFactory( factory );
			if( delegate == null )
				throw new IllegalStateException( "Missing factory: " + factory );
				
			delegate.write( element, children, out );
		}
	}
}
