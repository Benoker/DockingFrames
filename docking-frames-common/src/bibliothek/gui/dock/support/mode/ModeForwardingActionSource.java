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
package bibliothek.gui.dock.support.mode;

import java.util.Iterator;
import java.util.NoSuchElementException;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.AbstractDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.event.DockActionSourceListener;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockStationListener;

/**
 * A {@link ModeForwardingActionSource} is attached to a {@link DockStation} and 
 * shows actions belonging to the selected {@link Dockable}. Only actions that
 * were approved by the {@link ModeManager} are actually shown. 
 * @author Benjamin Sigg
 */
public class ModeForwardingActionSource<H> extends AbstractDockActionSource{
	/** the station for which actions are to be shown */
	private DockStation station;
	/** the manager that creates the actions */
	private ModeManager<H,? extends Mode<H>> manager; 
	
	/** the current actions, can be <code>null</code> */
	private DockActionSource delegate;
	
	/** this listener is added to {@link #delegate} */
	private DockActionSourceListener delegateListener = new DockActionSourceListener(){
		public void actionsRemoved( DockActionSource source, int firstIndex, int lastIndex ){
			fireRemoved( firstIndex, lastIndex );
		}
		
		public void actionsAdded( DockActionSource source, int firstIndex, int lastIndex ){
			fireAdded( firstIndex, lastIndex );
		}
	};
	
	/** this listener is added to {@link #station} */
	private DockStationListener stationListener = new DockStationAdapter(){
		@Override
		public void dockableAdded( DockStation station, Dockable dockable ){
			rebuild();
		}
		
		@Override
		public void dockableRemoved( DockStation station, Dockable dockable ){
			rebuild();
		}
		
		@Override
		public void dockableSelected( DockStation station, Dockable oldSelection, Dockable newSelection ){
			rebuild();
		}
	};
	
	/**
	 * Creates a new action source
	 * @param station the station for which the actions are used
	 * @param manager the manager that has to be observed
	 */
	public ModeForwardingActionSource( DockStation station, ModeManager<H, ? extends Mode<H>> manager ){
		this.station = station;
		this.manager = manager;
		
		rebuild();
	}
	
	@Override
	public void addDockActionSourceListener( DockActionSourceListener listener ){
		boolean empty = listeners.isEmpty();
		super.addDockActionSourceListener( listener );
		if( empty ){
			if( delegate != null ){
				delegate.addDockActionSourceListener( delegateListener );
			}
			station.addDockStationListener( stationListener );
		}
	}
	
	@Override
	public void removeDockActionSourceListener( DockActionSourceListener listener ){
		super.removeDockActionSourceListener( listener );
		if( listeners.isEmpty() ){
			if( delegate != null ){
				delegate.removeDockActionSourceListener( delegateListener );
			}
			station.removeDockStationListener( stationListener );
		}
	}
	
	public DockAction getDockAction( int index ){
		return delegate.getDockAction( index );
	}

	public int getDockActionCount(){
		if( delegate == null ){
			return 0;
		}
		return delegate.getDockActionCount();
	}

	public LocationHint getLocationHint(){
		return new LocationHint( LocationHint.ACTION_GUARD, LocationHint.RIGHT );
	}

	public Iterator<DockAction> iterator(){
		if( delegate == null ){
			return new Iterator<DockAction>(){
				public boolean hasNext(){
					return false;
				}
				public DockAction next(){
					throw new NoSuchElementException();
				}
				public void remove(){
					throw new UnsupportedOperationException();
				}
			};
		}
		else{
			return delegate.iterator();
		}
	}

	private void rebuild(){
		if( delegate != null ){
			if( !listeners.isEmpty() ){
				delegate.removeDockActionSourceListener( delegateListener );
			}
			int size = getDockActionCount();
			delegate = null;
			if( size > 0 ){
				fireRemoved( 0, size-1 );
			}
		}
		
		delegate = manager.getSharedActions( station );
		
		if( delegate != null ){
			if( !listeners.isEmpty() ){
				delegate.addDockActionSourceListener( delegateListener );
			}
			int size = getDockActionCount();
			if( size > 0 ){
				fireAdded( 0, size-1 );
			}
		}
	}
}
