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
package bibliothek.gui.dock.control.relocator;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.station.StationDropOperation;

/**
 * A set of {@link Merger}s.
 * @author Benjamin Sigg
 */
public class MultiMerger implements Merger{
	private List<Merger> mergers = new ArrayList<Merger>();
	
	/**
	 * Adds a new algorithm to this merger.
	 * @param merger the new algorithm, not <code>null</code>
	 */
	public void add( Merger merger ){
		if( merger == null ){
			throw new IllegalArgumentException( "merger must not be null" );
		}
		mergers.add( merger );
	}
	
	/**
	 * Removes <code>merger</code> from this {@link MultiMerger}.
	 * @param merger the item to remove
	 */
	public void remove( Merger merger ){
		mergers.remove( merger );
	}
	
	/**
	 * Gets the total number of mergers in this set of mergers.
	 * @return the size
	 */
	public int size(){
		return mergers.size();
	}
	
	/**
	 * Gets the <code>index</code>'th merger in this list of mergers.
	 * @param index the location
	 * @return the merger, not <code>null</code>
	 */
	public Merger get( int index ){
		return mergers.get( index );
	}
	
	public boolean canMerge( StationDropOperation operation, DockStation parent, DockStation child ){
		for( Merger merger : mergers ){
			if( merger.canMerge( operation, parent, child )){
				return true;
			}
		}
		return false;
	}
	
	public void merge( StationDropOperation operation, DockStation parent, DockStation child ){
		for( Merger merger : mergers ){
			if( merger.canMerge( operation, parent, child )){
				merger.merge( operation, parent, child );
				return;
			}
		}
	}
}
