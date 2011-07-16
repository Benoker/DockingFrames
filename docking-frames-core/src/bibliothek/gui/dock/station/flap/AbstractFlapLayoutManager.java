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
package bibliothek.gui.dock.station.flap;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;

/**
 * Abstract implementation of {@link FlapLayoutManager}, providing support for listeners.
 * @author Benjamin Sigg
 */
public abstract class AbstractFlapLayoutManager implements FlapLayoutManager{
	/** all the listeners that are currently registered */
	private List<FlapLayoutManagerListener> listeners = new ArrayList<FlapLayoutManagerListener>();
	
	public void addListener( FlapLayoutManagerListener listener ){
		if( listener == null ){
			throw new IllegalArgumentException( "listener must not be null" );
		}
		listeners.add( listener );
	}
	
	public void removeListener( FlapLayoutManagerListener listener ){
		listeners.remove( listener );	
	}
	
	/**
	 * Invokes the method {@link FlapLayoutManagerListener#holdSwitchableChanged(FlapLayoutManager, FlapDockStation, Dockable)}
	 * on all listeners that are currently registered.
	 * @param station the station to which the event belongs or <code>null</code>
	 * @param dockable the affected dockable or <code>null</code>
	 */
	protected void fireHoldSwitchableChanged( FlapDockStation station, Dockable dockable ){
		for( FlapLayoutManagerListener listener : listeners.toArray( new FlapLayoutManagerListener[ listeners.size() ] ) ){
			listener.holdSwitchableChanged( this, station, dockable );
		}
	}
}
