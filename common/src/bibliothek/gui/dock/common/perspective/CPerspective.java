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
package bibliothek.gui.dock.common.perspective;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.perspective.PerspectiveStation;

/**
 * A {@link CPerspective} is a lightweight, modifiable representation of all {@link Dockable}s and {@link DockStation}s
 * handled by a {@link CControl}.<br>
 * When using a {@link CPerspective} clients have to be aware of:
 * <ul>
 * 	<li>Neither single- nor multiple-dockables need to be registered.</li>
 *  <li>Any root-{@link CStation} used by a {@link CControl} needs to be registered using {@link #addRoot(CStationPerspective)}</li> 
 * </ul> 
 * @author Benjamin Sigg
 */
public class CPerspective {
	/** All the root stations of this perspective */
	private Map<String, CStationPerspective> roots = new HashMap<String, CStationPerspective>();
	
	/**
	 * Adds a new root station to this perspective. If a station with name <code>id</code> is
	 * already registered, then this station gets replaced.
	 * @param station the new station
	 */
	public void addRoot( CStationPerspective station ){
		if( station == null ){
			throw new IllegalArgumentException( "station must not be null" );
		}
		roots.put( station.getUniqueId(), station );
	}
	
	/**
	 * Gets the station which was registered with the unique identifier <code>id</code>.
	 * @param id some unique identifier
	 * @return the station associated with <code>id</code>, can be <code>null</code>
	 */
	public CStationPerspective getRoot( String id ){
		return roots.get( id );
	}
	
	/**
	 * Gets the names of all the stations that were registered as root station
	 * @return the names, not <code>null</code>
	 */
	public String[] getRootKeys(){
		return roots.keySet().toArray( new String[ roots.size() ] );
	}
	
	/**
	 * Gets an iterator that will visit all the {@link PerspectiveElement}s of this {@link CPerspective}. The
	 * iterator does not check whether this perspective is modified while it is in use.
	 * @return the iterator over all elements
	 */
	public Iterator<PerspectiveElement> elements(){
		return new ElementIterator();
	}
	
	private static class ElementFrame{
		public PerspectiveElement[] items;
		public int offset;
		
		public ElementFrame( PerspectiveElement[] items ){
			this.items = items;
		}
	}
	
	/**
	 * An iterator over all the {@link PerspectiveElement}s that are currently stored in this
	 * perspective. 
	 * @author Benjamin Sigg
	 */
	private class ElementIterator implements Iterator<PerspectiveElement>{
		private LinkedList<ElementFrame> stack = new LinkedList<ElementFrame>();
		
		public ElementIterator(){
			List<PerspectiveElement> items = new ArrayList<PerspectiveElement>();
			for( CStationPerspective station : roots.values() ){
				if( station.asDockable() == null || station.asDockable().getParent() == null ){
					items.add( station );
				}
			}
			
			stack.push( new ElementFrame( items.toArray( new PerspectiveElement[ items.size() ] ) ) );
		}
		
		public boolean hasNext(){
			for( ElementFrame frame : stack ){
				if( frame.offset < frame.items.length ){
					return true;
				}
			}
			return false;
		}
		
		public PerspectiveElement next(){
			while( stack.size() > 0 ){
				ElementFrame top = stack.peek();
				if( top.offset < top.items.length ){
					PerspectiveElement result = top.items[top.offset++];
					
					PerspectiveStation station = result.asStation();
					if( station != null ){
						PerspectiveElement[] children = new PerspectiveElement[ station.getDockableCount() ];
						for( int i = 0; i < children.length; i++ ){
							children[ i ] = station.getDockable( i );
						}
						stack.push( new ElementFrame( children ) );
					}
					
					return result;
				}
				stack.poll();
			}
			throw new NoSuchElementException();
		}
		
		public void remove(){
			throw new UnsupportedOperationException();
		}
	}
}
