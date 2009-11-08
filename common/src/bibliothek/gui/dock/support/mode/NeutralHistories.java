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
package bibliothek.gui.dock.support.mode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import bibliothek.gui.Dockable;

/**
 * Set of {@link NeutralHistory}s. This class provides methods to
 * {@link NeutralHistory#advance(NeutralModeCallback)} or 
 * {@link NeutralHistory#restore(NeutralModeCallback)} all of them
 * in parallel.
 * @author Benjamin Sigg
 */
public class NeutralHistories {
	/** the dockables and their histories */
	private Map<Dockable, NeutralHistory> histories = new HashMap<Dockable, NeutralHistory>();
	
	/** whether the methdod {@link #advance()} has been called more often than {@link #restore()} */
	private boolean advanced = false;
	
	/**
	 * Stores the history of <code>dockable</code>. If {@link #advance()}
	 * was already called the history is immediately updated, otherwise it
	 * remains empty until {@link #advance()} is called.
	 * @param dockable the element whose history should be managed
	 * @param mode the initial mode of <code>dockable</code>
	 */
	public void add( Dockable dockable, Mode mode ){
		NeutralHistory history = new NeutralHistory( dockable, mode );
		histories.put( dockable, history );
		if( advanced ){
			NeutralModeCallback callback = new NoCallback();
			while( history.advance( callback ));
		}
	}
	
	/**
	 * Removes <code>docakble</code> and its history.
	 * @param dockable the element to remove
	 */
	public void remove( Dockable dockable ){
		histories.remove( dockable );
	}
	
	/**
	 * Builds up the history for all dockables, moving them into
	 * their neutral state.
	 */
	public void advance(){
		AdvanceCallback callback = new AdvanceCallback();
		callback.run();
	}
	
	/**
	 * Restores the original state of all dockables.
	 */
	public void restore(){
		RestoreCallback callback = new RestoreCallback();
		callback.run();
	}
	
	private class NoCallback implements NeutralModeCallback{
		public void yield(){
			// ignore	
		}
	}
	
	/**
	 * A callback to advance the neutral histories.
	 * @author Benjamin Sigg
	 */
	private class AdvanceCallback implements NeutralModeCallback{
		private LinkedList<NeutralHistory> pending = new LinkedList<NeutralHistory>();
		private LinkedList<NeutralHistory> pendingNextLevel = new LinkedList<NeutralHistory>();
		
		public AdvanceCallback(){
			pending.addAll( histories.values() );
		}
		
		/**
		 * Builds up the history of all dockables.
		 */
		public void run(){
			while( true ){
				runLevel();
				if( pendingNextLevel.isEmpty() )
					return;
				pending = pendingNextLevel;
				pendingNextLevel = new LinkedList<NeutralHistory>();
			}
		}
		
		private void runLevel(){
			while( !pending.isEmpty() ){
				NeutralHistory head = pending.poll();
				if( head.advance( this ) ){
					pendingNextLevel.add( head );
				}
			}
		}
		
		public void yield(){
			runLevel();
		}
	}
	
	/**
	 * Callback to restore the original state of dockables.
	 * @author Benjamin Sigg
	 */
	private class RestoreCallback implements NeutralModeCallback{
		private LinkedList<NeutralHistory> level;
		
		public void run(){
			// max count
			int count = 0;
			for( NeutralHistory history : histories.values() ){
				count = Math.max( count, history.getSize() );
			}
			
			for( int now = count; now > 0; now-- ){
				// build level
				level = new LinkedList<NeutralHistory>();
				for( NeutralHistory history : histories.values() ){
					if( history.getSize() == now ){
						level.add( history );
					}
				}
				
				runLevel();
			}
		}
		
		private void runLevel(){
			while( !level.isEmpty() ){
				NeutralHistory history = level.poll();
				history.restore( this );
			}
		}
		
		public void yield(){
			runLevel();
		}
	}
}
