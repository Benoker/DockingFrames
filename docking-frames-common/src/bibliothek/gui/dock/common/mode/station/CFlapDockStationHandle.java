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
package bibliothek.gui.dock.common.mode.station;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.station.CFlapDockStation;
import bibliothek.gui.dock.common.mode.CMinimizedModeArea;
import bibliothek.gui.dock.common.mode.CModeArea;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.station.FlapDockStationHandle;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * Interface between {@link FlapDockStation} and {@link CModeArea}.
 * @author Benjamin Sigg
 */
public class CFlapDockStationHandle extends FlapDockStationHandle implements CMinimizedModeArea{
	/** base location */
	private CLocation location;
	
	/**
	 * Creates a new handle
	 * @param station the station which is handled by this handle
	 */
	public CFlapDockStationHandle( CStation<CFlapDockStation> station ){
		this( station.getUniqueId(), station.getStation(), station.getStationLocation() );
	}
	
	/**
	 * Creates a new handle
	 * @param id the unique identifier of this station
	 * @param station the station to handle
	 * @param location the location which represents <code>station</code>
	 */
	public CFlapDockStationHandle( String id, CFlapDockStation station, CLocation location ){
		super( id, station );
		if( location == null ){
			throw new IllegalArgumentException( "location must not be null" );
		}
		
		this.location = location;
	}
	

	public CLocation getCLocation( Dockable dockable ){
		DockableProperty property = DockUtilities.getPropertyChain( getStation(), dockable );
		return location.expandProperty( getStation().getController(), property );
	}
	
	public CLocation getCLocation( Dockable dockable, Location location ){
		DockableProperty property = location.getLocation();
		if( property == null )
			return this.location;
		
		return this.location.expandProperty( getStation().getController(), property );
	}
}
