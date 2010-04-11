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
package bibliothek.gui.dock.station.stack.tab;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.station.stack.StackDockStationFilterListener;
import bibliothek.gui.dock.station.stack.TabContent;

/**
 * An abstract implementation of {@link TabContentFilter}, knows which {@link StackDockStation}s
 * and which {@link Dockable}s are currently filtered.<br>
 * Subclasses may override {@link #added(StackDockStation, Dockable) added}, {@link #removed(StackDockStation, Dockable) removed},
 * {@link #deselected(StackDockStation, Dockable) deselected} and {@link #selected(StackDockStation, Dockable) selected} to be informed
 * if the contents of a {@link StackDockStation} changed.
 * @author Benjamin Sigg
 */
public abstract class AbstractTabContentFilter implements TabContentFilter{
	/** all listeners known to this filter */
	private List<StackDockStationFilterListener> listeners = new ArrayList<StackDockStationFilterListener>();
	
	/** all stations that are currently installed */
	protected List<StackDockStation> stations = new ArrayList<StackDockStation>();
	
	/** a listener added to all {@link StackDockStation}s */
	private DockStationListener stationListener = new DockStationAdapter() {
		public void dockableAdding( DockStation station, Dockable dockable ){
			added( (StackDockStation)station, dockable );
		}
		
		public void dockableRemoved( DockStation station, Dockable dockable ){
			removed( (StackDockStation)station, dockable );
		}
		
		public void dockableSelected( DockStation station, Dockable oldSelection, Dockable newSelection ){
			if( oldSelection != newSelection ){
				if( oldSelection != null ){
					deselected( (StackDockStation)station, oldSelection );
				}
				if( newSelection != null ){
					selected( (StackDockStation)station, newSelection );
				}
			}
		}
	};
	
	public void addListener( StackDockStationFilterListener listener ){
		if( listener == null ){
			throw new IllegalArgumentException( "listener must not be null" );
		}
		listeners.add( listener );
	}

	public void removeListener( StackDockStationFilterListener listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Gets all listeners that are currently observing this filter.
	 * @return the new listener
	 */
	protected StackDockStationFilterListener[] listeners(){
		return listeners.toArray( new StackDockStationFilterListener[ listeners.size() ] );
	}
	
	/**
	 * Calls {@link StackDockStationFilterListener#contentChanged()} on all 
	 * listeners that are currently installed.
	 */
	protected void fireChanged(){
		for( StackDockStationFilterListener listener : listeners() ){
			listener.contentChanged();
		}
	}
	
	/**
	 * Calls {@link StackDockStationFilterListener#contentChanged(Dockable)} on all 
	 * listeners that are currently installed.
	 * @param dockable the element whose content changed
	 */
	protected void fireChanged( Dockable dockable ){
		for( StackDockStationFilterListener listener : listeners() ){
			listener.contentChanged( dockable );
		}
	}
	
	/**
	 * Calls {@link StackDockStationFilterListener#contentChanged(StackDockStation)} on all 
	 * listeners that are currently installed.
	 * @param station the station whose content changed
	 */
	protected void fireChanged( StackDockStation station ){
		for( StackDockStationFilterListener listener : listeners() ){
			listener.contentChanged( station );
		}
	}
	
	public void install( StackDockStation station ){
		stations.add( station );
		station.addDockStationListener( stationListener );
	}
	
	public void uninstall( StackDockStation station ){
		stations.remove( station );
		station.removeDockStationListener( stationListener );
	}
	
	/**
	 * This implementation just returns <code>content</code>.
	 */
	public TabContent filter( TabContent content, StackDockStation station, Dockable dockable ){
		return content;
	}
	
	/**
	 * Called when <code>dockable</code> is added to <code>station</code>, this method is called before 
	 * the {@link Dockable#getDockParent() dock parent} of <code>dockable</code> is set.
	 * @param station the new parent of <code>dockable</code>
	 * @param dockable the new child
	 */
	protected void added( StackDockStation station, Dockable dockable ){
		// ignore
	}
	
	/**
	 * Called when <code>dockable</code> has been removed from <code>station</code>.
	 * @param station the old parent of <code>dockable</code>
	 * @param dockable the removed element
	 */
	protected void removed( StackDockStation station, Dockable dockable ){
		// ignore		
	}
	
	/**
	 * Called when the selection of <code>station</code> changed to <code>newSelection</code>.
	 * @param station the owner of <code>newSelection</code>
	 * @param dockable the new selection, not <code>null</code>
	 */
	protected void selected( StackDockStation station, Dockable dockable ){
		// ignore
	}

	/**
	 * Called when the selection of <code>station</code> changed to another dockable than <code>oldSelection</code>.
	 * @param station the owner of <code>oldSelection</code>
	 * @param dockable the old selection, not <code>null</code>
	 */
	protected void deselected( StackDockStation station, Dockable dockable ){
		// ignore		
	}

}