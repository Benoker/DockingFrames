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

package bibliothek.gui.dock.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bibliothek.gui.dock.event.DockActionSourceListener;

/**
 * A {@link DockActionSource} which shows a selection of {@link DockAction DockActions}
 * fetched from another <code>DockActionSource</code> 
 * @author Benjamin Sigg
 *
 */
public abstract class FilteredDockActionSource extends AbstractDockActionSource{
	/** the source from which DockActions are fetched */
	private DockActionSource source;
	
	/**
	 * A list telling for every action of {@link #source} whether it is
	 * selected or not.<br>
	 * The list can be <code>null</code> to show, that actions have to be
	 * read directly from the source. 
	 */
	private List<Boolean> actions = null;
	
	/** a listener to {@link #source}, used only if this source is observed itself */
	private Listener listener = new Listener();
	
	/**
	 * Creates a new filtered DockActionSource.
	 * @param source the source from which {@link DockAction DockActions} are fetched,
	 * must not be <code>null</code>
	 */
	public FilteredDockActionSource( DockActionSource source ){
		if( source == null )
			throw new IllegalArgumentException( "source must not be null" );
		this.source = source;
	}
	
	public Iterator<DockAction> iterator(){
		return new Iterator<DockAction>(){
			private Iterator<DockAction> iterator = source.iterator();
			private DockAction next;
			
			{
				advance();
			}
			
			public boolean hasNext(){
				return next != null;
			}

			public DockAction next(){
				DockAction result = next;
				advance();
				return result;
			}
			
			private void advance(){
				while( iterator.hasNext() ){
					DockAction check = iterator.next();
					if( include( check )){
						next = check;
						return;
					}
				}
				next = null;
			}

			public void remove(){
				iterator.remove();
			}
			
		};
	}
	
	@Override
	public void addDockActionSourceListener( DockActionSourceListener listener ){
		if( listeners.isEmpty() ){
			source.addDockActionSourceListener( this.listener );
			actions = new ArrayList<Boolean>();
			for( DockAction action : source ){
				actions.add( include( action ));
			}
		}
		super.addDockActionSourceListener( listener );
	}
	
	@Override
	public void removeDockActionSourceListener( DockActionSourceListener listener ){
		super.removeDockActionSourceListener( listener );
		if( listeners.isEmpty() ){
			source.removeDockActionSourceListener( this.listener );
			actions = null;
		}
	}

	public DockAction getDockAction( int index ){
		if( index < 0 )
			throw new IllegalArgumentException( "Index out of bounds: " + index );
		
		int count = -1;
		if( actions == null ){
			for( DockAction action : source ){
				if( include( action )){
					count++;
					if( index == count )
						return action;
				}
			}	
		}
		else{
			for( int i = 0, n = source.getDockActionCount(); i<n; i++ ){
				if( actions.get( i )){
					count++;
					if( index == count )
						return source.getDockAction( i );
				}
			}
		}
		
		throw new IllegalArgumentException( "Index out of bounds: " + index );
	}
	
	public int getDockActionCount(){
		int count = 0;
		if( actions == null ){
			for( DockAction action : source ){
				if( include( action ))
					count++;
			}
		}
		else{
			for( int i = 0, n = source.getDockActionCount(); i<n; i++ ){
				if( actions.get( i ))
					count++;
			}
				
		}
		
		return count;
	}

	public LocationHint getLocationHint(){
		return source.getLocationHint();
	}
	
	/**
	 * Recalculates the visibility of all actions and fires events to the registered {@link DockActionSourceListener}
	 * if the state of an action changed.
	 */
	public void refresh(){
		if( actions != null ){
			int index = 0;
			
			for( int i = 0, n = source.getDockActionCount(); i<n; i++ ){
				boolean include = include( source.getDockAction( i ) );
				if( include != actions.get( i ).booleanValue() ){
					actions.set( i, include );
					if( include ){
						// action has been added
						fireAdded( index, index );
					}
					else{
						// action was removed
						fireRemoved( index, index );
					}
				}
				if( include ){
					index++;
				}
			}
		}
	}
	
	/**
	 * Tells whether <code>action</code> should be included in the list of
	 * actions of this source, or not.
	 * @param action the action to test
	 * @return <code>true</code> if <code>action</code> should be shown
	 */
	protected abstract boolean include( DockAction action );
	
	/**
	 * A listener added to {@link FilteredDockActionSource#source} in order
	 * to change the enclosing {@link FilteredDockActionSource} when necessary. 
	 * @author Benjamin Sigg
	 */
	private class Listener implements DockActionSourceListener{
		public void actionsAdded( DockActionSource source, int firstIndex, int lastIndex ){
			// find location to insert new actions
			int insert = 0;
			
			for( int i = 0; i < firstIndex; i++ ){
				if( actions.get( i )){
					insert++;
				}
			}
			
			// filter new actions
			int count = 0;
			for( int i = firstIndex; i <= lastIndex; i++ ){
				DockAction action = source.getDockAction( i );
				boolean include = include( action );
				actions.add( i, include );
				if( include ){
					count++;
				}
			}
			
			// fire events
			if( count > 0 ){
				fireAdded( insert, insert+count-1 );
			}
		}

		public void actionsRemoved( DockActionSource source, int firstIndex, int lastIndex ){
			// find location to remove old actions
			int remove = 0;
			
			for( int i = 0; i < firstIndex; i++ ){
				if( actions.get( i )){
					remove++;
				}
			}
			
			// remove old actions
			int count = 0;
			for( int i = lastIndex; i >= firstIndex; i-- ){
				if( actions.remove( i ))
					count++;
			}
			
			// fire events
			if( count > 0 ){
				fireRemoved( remove, remove+count-1 );
			}
		}
	}
}
