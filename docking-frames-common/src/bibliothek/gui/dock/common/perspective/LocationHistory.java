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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.util.Path;

/**
 * An ordered map of {@link ExtendedMode}s and {@link Location}s, ordered by
 * the time when the location was recorded. 
 * @author Benjamin Sigg
 */
public class LocationHistory {
	private Map<ExtendedMode, Location> locations = new HashMap<ExtendedMode, Location>();
	private List<ExtendedMode> order = new LinkedList<ExtendedMode>();
	
	/**
	 * Sets the <code>location</code> that is to be used for <code>mode</code>.
	 * @param mode the mode of the location
	 * @param location the location to store
	 */
	public void add( ExtendedMode mode, Location location ){
		if( !mode.getModeIdentifier().equals( location.getMode() )){
			throw new IllegalArgumentException( "mode and location do not fit together" );
		}
		locations.put( mode, location );
		order.remove( mode );
		order.add( mode );
	}
	
	/**
	 * Sets the <code>location</code> that is to be used for <code>mode</code>.
	 * @param index the location of the mode, where 0 represents the oldest entry
	 * @param mode the mode of the location
	 * @param location the location to store
	 */
	public void insert( int index, ExtendedMode mode, Location location ){
		if( !mode.getModeIdentifier().equals( location.getMode() )){
			throw new IllegalArgumentException( "mode and location do not fit together" );
		}
		locations.put( mode, location );
		int old = order.indexOf( mode );
		if( old > 0 ){
			if( old < index ){
				index--;
			}
			order.remove( old );
		}
		
		order.add( index, mode );
	}
	
	/**
	 * Gets the location that is to be used for <code>mode</code>.
	 * @param mode the mode whose location is searched
	 * @return the location or <code>null</code> if not found
	 */
	public Location get( ExtendedMode mode ){
		return locations.get( mode );
	}
	
	/**
	 * Removes any entries related to <code>mode</code>.
	 * @param mode the mode to remove
	 */
	public void remove( ExtendedMode mode ){
		locations.remove( mode );
		order.remove( mode );
	}
	
	/**
	 * Gets the number of entries this history has.
	 * @return the number of entries
	 */
	public int getSize(){
		return order.size();
	}
	
	/**
	 * Gets the <code>index</code>'th entry of this history, where an
	 * index of 0 represents the oldest entry.
	 * @param index the location of the entry
	 * @return the mode at <code>index</code>
	 */
	public ExtendedMode getMode( int index ){
		return order.get( index );
	}
	
	/**
	 * Gets the newest entry of this history.
	 * @return the newest mode or <code>null</code>
	 */
	public ExtendedMode getLastMode(){
		int size = getSize();
		if( size == 0 ){
			return null;
		}
		return getMode( size-1 );
	}
	
	/**
	 * Gets all the identifiers of the ordered {@link ExtendedMode}s.
	 * @return the order of modes
	 */
	public List<Path> getOrder(){
		List<Path> result = new ArrayList<Path>();
		for( ExtendedMode mode : order ){
			result.add( mode.getModeIdentifier() );
		}
		return result;
	}
	
	/**
	 * Gets all the locations that are stored in this history.
	 * @return the locations
	 */
	public Map<Path, Location> getLocations(){
		Map<Path, Location> result = new HashMap<Path, Location>();
		for( Location location : locations.values() ){
			result.put( location.getMode(), location );
		}
		return result;
	}
}
