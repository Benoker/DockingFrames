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

package bibliothek.gui.dock;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * The {@link DockHierarchyLock} allows {@link DockStation}s to defend
 * themselfs against concurrent modifications of the hierarchy. At any time only
 * one {@link DockStation} in the realm of a {@link DockController} can acquire
 * the lock.
 * @author Benjamin Sigg
 */
public class DockHierarchyLock {
	/** the current lock */
	private volatile Token token = null;
	
	/** whether to throw an exception or just print one */
	private boolean hardExceptions = false;
	
	/** if greater than 0, no exception is ever thrown */
	private volatile int concurrent = 0;
	
	/**
	 * Sets whether exceptions should be thrown or only printed.
	 * @param hardExceptions <code>true</code> if the exceptions should be thrown
	 */
	public void setHardExceptions( boolean hardExceptions ){
		this.hardExceptions = hardExceptions;
	}
	
	/**
	 * Tells whether hard exceptions should be thrown or only printed.
	 * @return <code>true</code> if exceptions should be thrown
	 * @see #setHardExceptions(boolean)
	 */
	public boolean isHardExceptions(){
		return hardExceptions;
	}
	
	/**
	 * Tells this lock whether concurrent modifications are allowed or not. If allowed no exception will be thrown
	 * due to a lock being acquired while a lock is already held. 
	 * @param concurrent whether to allow concurrent modification or not
	 */
	public synchronized void setConcurrent( boolean concurrent ){
		if( concurrent ){
			this.concurrent++;
		}
		else{
			this.concurrent--;
		}
	}
	
	/**
	 * Whether this lock throws exceptions or is silent.
	 * @return if <code>true</code>, then this lock is silent
	 * @see #isConcurrent()
	 */
	public boolean isConcurrent(){
		return concurrent > 0;
	}
	
	/**
	 * The same as calling {@link #acquireLink(DockStation, Dockable)} with the {@link DockHierarchyLock} of
	 * the {@link DockController} of <code>station</code>. Returns a fake {@link Token} if <code>station</code> has
	 * no {@link DockController}.
	 * @param station the station which wants to be the new parent of <code>dockable</code>
	 * @param dockable a dockable with no parent
	 * @return the acquired token to release the lock
	 * @throws IllegalStateException if <code>dockable</code> has a parent or <code>station</code>
	 * thinks that <code>dockable</code> is one of its children
	 */
	public static Token acquireLinking( DockStation station, Dockable dockable ){
		DockController controller = station.getController();
		if( controller == null ){
			return new Token( null, station, dockable, true );
		}
		else{
			return controller.getHierarchyLock().acquireLink( station, dockable );
		}
	}
	
	/**
	 * The same as calling {@link #acquireUnlink(DockStation, Dockable)} with the {@link DockHierarchyLock} of
	 * the {@link DockController} of <code>station</code>. Returns a fake {@link Token} if <code>station</code> has
	 * no {@link DockController}.
	 * @param station the current parent of <code>dockable</code>
	 * @param dockable a dockable with <code>station</code> as parent
	 * @return the acquired token to release the lock
	 * @throws IllegalStateException if <code>dockable</code> is not a child of
	 * <code>station</code>
	 */
	public static Token acquireUnlinking( DockStation station, Dockable dockable ){
		DockController controller = station.getController();
		if( controller == null ){
			return new Token( null, station, dockable, true );
		}
		else{
			return controller.getHierarchyLock().acquireUnlink( station, dockable );
		}
	}
	
	/**
	 * Acquires a fake token which does not lock anything. This method never throws an exception.
	 * @return the fake token
	 */
	public static Token acquireFake(){
		return new Token( null, null, null, false );
	}
	
	/**
	 * Allows <code>station</code> to become the new parent of <code>dockable</code>.  
	 * @param station the station which wants to be the new parent of <code>dockable</code>
	 * @param dockable a dockable with no parent
	 * @return the acquired token to release the lock
	 * @throws IllegalStateException if <code>dockable</code> has a parent or <code>station</code>
	 * thinks that <code>dockable</code> is one of its children
	 */
	public synchronized Token acquireLink( DockStation station, Dockable dockable ){
		if( station == null ){
			throw new IllegalArgumentException( "station is null" );
		}
		if( dockable == null ){
			throw new IllegalArgumentException( "dockable is null" );
		}
		ensureUnlinked( station, dockable );
		
		if( isConcurrent() ){
			return new Token( this, station, dockable, true );
		}
		
		if( token != null ){
			throwException( new IllegalStateException( "the lock has already been acquired" ) );
		}
		token = new Token( this, station, dockable, true );
		return token;
	}
	
	/**
	 * Allows <code>station</code> to remove itself as parent from <code>dockable</code>.  
	 * @param station the current parent of <code>dockable</code>
	 * @param dockable a dockable with <code>station</code> as parent
	 * @return the acquired token to release the lock
	 * @throws IllegalStateException if <code>dockable</code> is not a child of
	 * <code>station</code>
	 */
	public synchronized Token acquireUnlink( DockStation station, Dockable dockable ){
		if( station == null ){
			throw new IllegalArgumentException( "station is null" );
		}
		if( dockable == null ){
			throw new IllegalArgumentException( "dockable is null" );
		}
		ensureLinked( station, dockable );
		
		if( isConcurrent() ){
			return new Token( this, station, dockable, false );
		}
		
		if( token != null ){
			throwException( new IllegalStateException( "the lock has already been acquired" ) );
		}
		token = new Token( this, station, dockable, false );
		return token;
	}
	
	private void ensureLinked( DockStation station, Dockable dockable ){
		if( dockable.getDockParent() != station ){
			throwException( new IllegalStateException( "the parent of '" + dockable + "' is not '" + station + "' but '" + dockable.getDockParent() + "'" ) );
			return;
		}
		boolean found = false;
		for( int i = 0, n = station.getDockableCount(); i<n && !found; i++ ){
			if( station.getDockable( i ) == dockable ){
				found = true;
			}
		}
		if( !found ){
			throwException( new IllegalStateException( "the station '" + station + "' does not know '" + dockable + "'" ) );
			return;
		}	
	}
	
	private void ensureUnlinked( DockStation station, Dockable dockable ){
		if( dockable.getDockParent() != null ){
			throwException( new IllegalStateException( "The parent of '" + dockable + "' is not null but '" + dockable.getDockParent() + "'" ) );
			return;
		}
		for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
			if( station.getDockable( i ) == dockable ){
				throwException( new IllegalStateException( "The station '" + station + "' knows of '" + dockable + "'" ) );
				return;
			}
		}
	}
	
	private void throwException( RuntimeException exception ){
		if( hardExceptions ){
			throw exception;
		}
		else{
			exception.printStackTrace();
		}
	}
	
	/**
	 * Is acquired from a {@link DockHierarchyLock} and releases the lock.
	 */
	public static class Token{
		private DockHierarchyLock lock;
		private DockStation station;
		private Dockable dockable;
		private boolean link;
		
		private Token( DockHierarchyLock lock, DockStation station, Dockable dockable, boolean link ){
			this.lock = lock;
			this.station = station;
			this.dockable = dockable;
			this.link = link;
		}
		
		/**
		 * Releases the lock.
		 * @throws IllegalStateException if the state is not as suggested by the
		 * acquierer when acquiering the lock
		 */
		public void release(){
			synchronized( this ){
				if( lock != null ){
					lock.token = null;
					if( link ){
						lock.ensureLinked( station, dockable );
					}
					else{
						lock.ensureUnlinked( station, dockable );
					}
				}
			}
		}
		
		/**
		 * Releases this lock without doing the usual checks.
		 */
		public void releaseNoCheck(){
			synchronized( this ){
				if( lock != null ){
					lock.token = null;
				}
			}
		}
	}
}
