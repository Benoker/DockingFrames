/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.toolbar.location;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.ModeAreaListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.toolbar.CToolbarArea;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A connection between a {@link CToolbarArea} and the {@link CToolbarModeArea} interface.
 * @author Benjamin Sigg
 */
public class CToolbarAreaHandle implements CToolbarModeArea{
	private CToolbarArea station;
	
	public CToolbarAreaHandle( CToolbarArea area ){
		this.station = area;
	}

	@Override
	public CLocation getCLocation( Dockable dockable ){
		DockableProperty property = DockUtilities.getPropertyChain( getStation(), dockable );
		CLocation location = station.getStationLocation();
		return location.expandProperty( station.getStation().getController(), property );
	}

	@Override
	public CLocation getCLocation( Dockable dockable, Location location ){
		DockableProperty property = location.getLocation();
		CLocation base = station.getStationLocation();
		if( property == null )
			return base;
		
		return base.expandProperty( station.getStation().getController(), property );
	}
	
	@Override
	public DockableProperty getLocation( Dockable child ){
		return DockUtilities.getPropertyChain( station.getStation(), child );
	}
	
	@Override
	public boolean setLocation( Dockable dockable, DockableProperty location, AffectedSet set ){
		set.add( dockable );
		
		ToolbarContainerDockStation station = this.station.getStation();
		
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

	@Override
	public boolean respectWorkingAreas(){
		return true;
	}

	@Override
	public String getUniqueId(){
		return station.getUniqueId();
	}

	@Override
	public boolean autoDefaultArea(){
		return true;
	}
	
	@Override
	public boolean isLocationRoot(){
		return true;
	}

	@Override
	public boolean isChild( Dockable dockable ){
		return dockable.asDockStation() != station.getStation() && DockUtilities.isAncestor( station.getStation(), dockable );
	}

	@Override
	public DockStation getStation(){
		return station.getStation();
	}

	@Override
	public void setController( DockController controller ){
		// ignore
	}

	@Override
	public void setMode( LocationMode mode ){
		//ignore
	}

	@Override
	public void addModeAreaListener( ModeAreaListener listener ){
		// ignore
	}

	@Override
	public void removeModeAreaListener( ModeAreaListener listener ){
		// ignore
	}
}
