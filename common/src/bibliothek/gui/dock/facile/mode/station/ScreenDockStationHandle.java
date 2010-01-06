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
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.facile.mode.ExternalizedModeArea;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A link between a {@link ScreenDockStation} and the
 * {@link ExternalizedModeArea} interface.
 * @author Benjamin Sigg
 *
 */
public class ScreenDockStationHandle implements ExternalizedModeArea{
	/** the unique id of this handle */
	private String id;
	
	/** the wrapped station */
	private ScreenDockStation station;
	
	/**
	 * Creates a new handle.
	 * @param id the unique identifier of this handle
	 * @param station the wrapped station
	 */
	public ScreenDockStationHandle( String id, ScreenDockStation station ){
		if( id == null )
			throw new IllegalArgumentException( "id must not be null" );
		if( station == null )
			throw new IllegalArgumentException( "station must not be null" );
		
		this.id = id;
		this.station = station;
	}
	
	public void setController( DockController controller ){
		// ignore	
	}
	
	/**
	 * Gets the station which is managed by this handle.
	 * @return the station
	 */
	public ScreenDockStation getStation(){
		return station;
	}

	public boolean isRepresentant( DockStation station ){
		return this.station == station;
	}
	
	public boolean respectWorkingAreas(){
		return false;
	}
	
	public DockableProperty getLocation( Dockable child ){
		return DockUtilities.getPropertyChain( station, child );
	}

	public void setLocation( Dockable dockable, DockableProperty location, AffectedSet set ){
		set.add( dockable );
		
		if( isChild( dockable )){
			if( location != null ){
				station.move( dockable, location );
			}
		}
		else{
			if( location != null ){
				if( !station.drop( dockable, location ))
					location = null;
			}
			if( location == null )
				station.drop( dockable );
		}
	}

	public String getUniqueId(){
		return id;
	}

	public boolean isChild( Dockable dockable ){
		return dockable.getDockParent() == station;
	}

}
