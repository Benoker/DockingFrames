/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.common.intern.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bibliothek.gui.dock.action.AbstractDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.common.action.CAction;

/**
 * This {@link DockActionSource} handles {@link CAction}s.
 * @author Benjamin Sigg
 *
 */
public class CActionSource extends AbstractDockActionSource{
	/** the list of managed actions */
	private List<CAction> actions = new ArrayList<CAction>();
	
	/** the location of this source */
	private LocationHint hint;
	
	/**
	 * Creates a new source.
	 * @param hint the location of this source
	 */
	public CActionSource( LocationHint hint ){
		if( hint == null )
			throw new IllegalArgumentException( "hint must not be null" );
		this.hint = hint;
	}
	
	/**
	 * Adds an action to this source.
	 * @param action the new action, not <code>null</code>
	 */
	public void add( CAction action ){
		insert( getDockActionCount(), action );
	}
	
	/**
	 * Inserts an action at <code>index</code> of this source.
	 * @param index an index between 0 and {@link #getDockActionCount()} (incl.)
	 * @param action the new action, not <code>null</code>
	 */
	public void insert( int index, CAction action ){
		if( action == null )
			throw new IllegalArgumentException( "action must not be null" );
		actions.add( index, action );
		fireAdded( index, index );
	}
	
	/**
	 * Replaces the action at <code>index</code> with <code>index</code>.
	 * @param index the index of the new action
	 * @param action the new action, not <code>null</code>
	 * @return the old action, may be <code>null</code>
	 */
	public CAction set( int index, CAction action ){
		if( action == null )
			throw new IllegalArgumentException( "action must not be null" );
		
		if( index == actions.size() ){
			add( action );
			return null;
		}
		CAction result = remove( index );
		insert( index, action );
		return result;
	}
	
	/**
	 * Removes the action at <code>index</code>.
	 * @param index the index of the action to remove
	 * @return the removed action
	 */
	public CAction remove( int index ){
		CAction action = actions.remove( index );
		fireRemoved( index, index );
		return action;
	}
	
	/**
	 * Removes <code>action</code> from this source.
	 * @param action the action to remove
	 * @return <code>true</code> if the action was removed
	 */
	public boolean remove( CAction action ){
		int index = actions.indexOf( action );
		if( index == -1 ){
			return false;
		}
		remove( index );
		return true;
	}
	
	/**
	 * Gets the <code>index</code>'th action of this source.
	 * @param index the index of some action
	 * @return the action, not <code>null</code>
	 */
	public CAction getAction( int index ){
		return actions.get( index );
	}
	
	public DockAction getDockAction( int index ){
		return getAction( index ).intern();
	}

	public int getDockActionCount(){
		return actions.size();
	}

	public LocationHint getLocationHint(){
		return hint;
	}

	public Iterator<DockAction> iterator(){
		return new Iterator<DockAction>(){
			private int index = 0;
			private boolean removed = false;
			
			public boolean hasNext(){
				return index < getDockActionCount();
			}
			
			public DockAction next(){
				removed = false;
				return getDockAction( index++ );
			}
			
			public void remove(){
				if( removed )
					throw new IllegalStateException( "item already removed" );
				removed = true;
				CActionSource.this.remove( index-- );
			}
		};
	}
}
