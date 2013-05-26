/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.station.CSplitDockStation;
import bibliothek.gui.dock.common.intern.station.CommonDockStation;
import bibliothek.gui.dock.common.mode.CExternalizedModeArea;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.ModeAreaListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * The representation of a {@link SplitDockStation} which is a child of a {@link ScreenDockStation},
 * meaning the children are part of a {@link CExternalizedModeArea}.
 * @author Benjamin Sigg
 */
public class ExternalizedCSplitDockStationHandler extends CSplitDockStationHandle {
	/** what mode to use when unmaximizing children */
	private LocationMode externalMode;
	
	/** the actual {@link CExternalizedModeArea} */
	private Externalized external;
	
	/**
	 * Creates a new handler
	 * @param station the station that will be represented by <code>this</code>
	 * @param manager manager handling all the modes created by <code>this</code>
	 */
	public ExternalizedCSplitDockStationHandler( CStation<CSplitDockStation> station, CLocationModeManager manager ){
		super( station, manager );
		external = new Externalized();
	}
	
	@Override
	protected LocationMode getNormalMode(){
		return externalMode;
	}
	
	/**
	 * Gets a representation of the {@link SplitDockStation} as {@link CExternalizedModeArea}.
	 * @return the representation, always the same object
	 */
	public CExternalizedModeArea asExternalized(){
		return external;
	}
	
	private CStation<?> getBaseStation(){
		DockStation parent = getStation().getDockParent();
		while( parent != null ){
			if( parent instanceof CommonDockStation<?, ?> ){
				return ((CommonDockStation<?, ?>)parent).getStation();
			}
		}
		throw new IllegalStateException( "missing parent station" );
	}
	
	/***
	 * Represents a {@link SplitDockStation} which is a child of a {@link ScreenDockStation} as
	 * a {@link CExternalizedModeArea}.
	 * @author Benjamin Sigg
	 */
	protected class Externalized implements CExternalizedModeArea{
		public DockableProperty getLocation( Dockable child ){
			return asNormalModeArea().getLocation( child );
		}
	
		public boolean setLocation( Dockable dockable, DockableProperty location, AffectedSet set ){
			return asNormalModeArea().setLocation( dockable, location, set );
		}
	
		public String getUniqueId(){
			return getCStation().getUniqueId();
		}
	
		public boolean autoDefaultArea(){
			return false;
		}
		
		public boolean isLocationRoot(){
			return false;
		}
	
		public boolean isChild( Dockable dockable ){
			return asNormalModeArea().isChild( dockable );
		}
	
		public DockStation getStation(){
			return ExternalizedCSplitDockStationHandler.this.getStation();
		}
	
		public void setController( DockController controller ){
			// ignore
		}
	
		public void setMode( LocationMode mode ){
			externalMode = mode;
		}
	
		public void addModeAreaListener( ModeAreaListener listener ){
			add( new ModeAreaListenerWrapper( this, listener ));
		}
	
		public void removeModeAreaListener( ModeAreaListener listener ){
			remove( new ModeAreaListenerWrapper( this, listener ));
		}
		
		public CLocation getCLocation( Dockable dockable ){
			CStation<?> base = getBaseStation();
			DockStation baseStation = base.getStation();
			DockableProperty property = DockUtilities.getPropertyChain( baseStation, dockable );
			return base.getStationLocation().expandProperty( baseStation.getController(), property );
		}
	
		public CLocation getCLocation( Dockable dockable, Location location ){
			CStation<?> base = getBaseStation();
			DockableProperty property = location.getLocation();
			if( property == null ){
				return base.getStationLocation();
			}
			
			return base.getStationLocation().expandProperty( base.getStation().getController(), property );
		}
	
		public boolean respectWorkingAreas(){
			return false;
		}
	}
}
