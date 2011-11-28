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
package bibliothek.gui.dock.control.relocator;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.dock.station.StationDropOperation;

/**
 * An {@link Inserter} which consists of several child {@link Inserter}s. In any method the first
 * result that is not <code>null</code> is the result of the entire {@link Inserter}.
 * @author Benjamin Sigg
 */
public class MultiInserter implements Inserter{
	/** all the inserters that were added to <code>this</code> */
	private List<Inserter> inserters = new ArrayList<Inserter>();
	
	/**
	 * Adds a new {@link Inserter} to the list of {@link Inserter}s.
	 * @param inserter the new item, not <code>null</code>
	 */
	public void add( Inserter inserter ){
		if( inserter == null ){
			throw new IllegalArgumentException( "inserter must not be null" );
		}
		inserters.add( inserter );
	}
	
	/**
	 * Removes <code>inserter</code> from the list of {@link Inserter}s.
	 * @param inserter the item to remove
	 */
	public void remove( Inserter inserter ){
		inserters.remove( inserter );
	}
	
	/**
	 * Gets the total number of {@link Inserter}s in this list of inserters.
	 * @return the list of inserters
	 */
	public int size(){
		return inserters.size();
	}
	
	/**
	 * Gets the <code>index</code>'th inserter in the list of inserters.
	 * @param index the location
	 * @return the inserter, not <code>null</code>
	 */
	public Inserter get( int index ){
		return inserters.get( index );
	}
	
	public StationDropOperation before( InserterSource source ){
		for( Inserter inserter : inserters ){
			StationDropOperation result = inserter.before( source );
			if( result != null ){
				return result;
			}
		}
		return null;
	}

	public StationDropOperation after( InserterSource source ){
		for( Inserter inserter : inserters ){
			StationDropOperation result = inserter.after( source );
			if( result != null ){
				return result;
			}
		}
		return null;
	}
}
