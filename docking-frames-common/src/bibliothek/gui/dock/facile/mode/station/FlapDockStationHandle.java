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
package bibliothek.gui.dock.facile.mode.station;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.MinimizedModeArea;
import bibliothek.gui.dock.facile.mode.ModeAreaListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A connection between a {@link FlapDockStation} and the
 * {@link MinimizedModeArea} interface.
 * @author Benjamin Sigg
 */
public class FlapDockStationHandle implements MinimizedModeArea{
	/** unique id of this handle */
	private String id;
	
	/** the station handled by this area */
	private FlapDockStation station;

	/**
	 * Creates a new handle.
	 * @param id unique id of this handle
	 * @param station the station managed by this handle
	 */
	public FlapDockStationHandle( String id, FlapDockStation station ){
		if( id == null )
			throw new IllegalArgumentException( "id must not be null" );
		if( station == null )
			throw new IllegalArgumentException( "station must not be null" );
		
		this.id = id;
		this.station = station;
	}
	
	public boolean autoDefaultArea() {
		return true;
	}
	
	public boolean isLocationRoot(){
		return true;
	}
	
	public void addModeAreaListener( ModeAreaListener listener ){
		// ignore
	}
	
	public void removeModeAreaListener( ModeAreaListener listener ){
		// ignore	
	}
	
	public void setController( DockController controller ){
		// ignore	
	}
	
	public void setMode( LocationMode mode ){
		// ignore	
	}
	
	/**
	 * Gets the station which is managed by this handle.
	 * @return the station
	 */
	public FlapDockStation getStation(){
		return station;
	}
	
	public DockableProperty getLocation( Dockable child ){
		return DockUtilities.getPropertyChain( station, child );
	}

	public String getUniqueId(){
		return id;
	}

	public boolean isChild( Dockable dockable ){
		return dockable.getDockParent() == station;
	}
	
	public boolean respectWorkingAreas(){
		return false;
	}

	public boolean setLocation( Dockable dockable, DockableProperty location, AffectedSet set ){
		set.add( dockable );
		
		if( isChild( dockable )){
			if( location != null ){
				station.move( dockable, location );
			}
			return true;
		}
		else{
			boolean acceptable = DockUtilities.acceptable( getStation(), dockable );
			if( !acceptable ){
				return false;
			}
			
			if( location != null ){
				if( !station.drop( dockable, location )){
					location = null;
				}
			}
			if( location == null ){
				station.drop( dockable );
			}
			return true;
		}
	}
}
