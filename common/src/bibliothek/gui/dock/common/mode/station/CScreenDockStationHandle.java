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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.mode.CExternalizedModeArea;
import bibliothek.gui.dock.common.mode.CMaximizedModeArea;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.LocationModeEvent;
import bibliothek.gui.dock.facile.mode.ModeArea;
import bibliothek.gui.dock.facile.mode.ModeAreaListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.screen.ScreenDockStationListener;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * Connection between a {@link ScreenDockStation} and the {@link CExternalizedModeArea} interface.
 * @author Benjamin Sigg
 */
public class CScreenDockStationHandle {
	/** base location */
	private CLocation location;
	
	/** the station handled by this handle */
	private CStation<ScreenDockStation> station;
	
	/** Representation of this as {@link CExternalizedModeArea} */
	private External external = new External();
	
	/** Representation of this as {@link CMaximizedModeArea} */
	private Maximal maximal = new Maximal();
	
	/** the mode owning {@link #external} */
	private LocationMode externalMode;
	
	/**
	 * Creates a new handle
	 * @param station the station which is handled by this handle
	 */
	public CScreenDockStationHandle( CStation<ScreenDockStation> station ){
		this.station = station;
		station.getStation().addScreenDockStationListener( new ScreenDockStationListener() {
			public void windowRegistering( ScreenDockStation station, Dockable dockable, ScreenDockWindow window ) {
				// ignore
			}
			
			public void windowDeregistering( ScreenDockStation station, Dockable dockable, ScreenDockWindow window ) {
				// ignore
			}
			
			public void fullscreenChanged( ScreenDockStation station, Dockable dockable ) {
				ModeAreaListener[] listeners;
				ModeArea source;
				
				if( station.isFullscreen( dockable )){
					source = maximal;
					listeners = maximal.listeners();
				}
				else{
					source = external;
					listeners = external.listeners();
				}
				
				Set<Dockable> dockables = new HashSet<Dockable>();
				dockables.add( dockable );
				for( ModeAreaListener listener : listeners ){
					listener.internalLocationChange( source, dockables );
				}
			}
		});
	}

	/**
	 * Gets the representation of this as {@link CExternalizedModeArea}.
	 * @return the representation
	 */
	public CExternalizedModeArea getExternalizedModeArea(){
		return external;
	}
	
	/**
	 * Gets the representation of this as {@link CMaximizedModeArea}.
	 * @return the representation
	 */
	public CMaximizedModeArea getMaximizedModeArea(){
		return maximal;
	}
	
	/**
	 * Gets a {@link CLocation} describing the location of <code>dockable</code> on this handles station.
	 * @param dockable some child
	 * @return the location
	 */
	public CLocation getCLocation( Dockable dockable ){
		DockableProperty property = DockUtilities.getPropertyChain( station.getStation(), dockable );
		return location.expandProperty( property );
	}

	/**
	 * Assuming dockable would be at location location if it would be a child of this station, 
	 * returns the CLocation that matches location.
	 * @param dockable some element which may or may not be a child of this station
	 * @param location the location dockable would have if it would be a child of this station
	 * @return the location
	 */
	public CLocation getCLocation( Dockable dockable, Location location ){
		DockableProperty property = location.getLocation();
		if( property == null )
			return this.location;
		
		return this.location.expandProperty( property );
	}
	
	/**
	 * Represents a {@link CScreenDockStationHandle} as {@link CExternalizedModeArea}.
	 * @author Benjamin Sigg
	 */
	protected class External implements CExternalizedModeArea{
		private List<ModeAreaListener> listeners = new ArrayList<ModeAreaListener>();
		
		public DockableProperty getLocation( Dockable child ) {
			return DockUtilities.getPropertyChain( getStation(), child );
		}

		public void setLocation( Dockable dockable, DockableProperty location, AffectedSet set ) {
			set.add( dockable );
			
			if( isChild( dockable )){
				if( location != null ){
					station.getStation().move( dockable, location );
				}
			}
			else{
				if( location != null ){
					if( !station.getStation().drop( dockable, location ))
						location = null;
				}
				if( location == null )
					station.getStation().drop( dockable );
			}
		}

		public void addModeAreaListener( ModeAreaListener listener ) {
			listeners.add( listener );
		}

		public void removeModeAreaListener( ModeAreaListener listener ) {
			listeners.remove( listener );
		}
		
		/**
		 * Gets all the listeners that are registered at this area.
		 * @return all listeners
		 */
		public ModeAreaListener[] listeners(){
			return listeners.toArray( new ModeAreaListener[ listeners.size() ] );
		}

		public DockStation getStation() {
			return station.getStation();
		}

		public String getUniqueId() {
			return station.getUniqueId();
		}

		public boolean isChild( Dockable dockable ) {
			return dockable.getDockParent() == station.getStation() && !station.getStation().isFullscreen( dockable );
		}

		public boolean respectWorkingAreas() {
			return false;
		}

		public void setController( DockController controller ) {
			// ignore
		}
		
		public void setMode( LocationMode mode ){
			externalMode = mode;	
		}

		public CLocation getCLocation( Dockable dockable ) {
			return CScreenDockStationHandle.this.getCLocation( dockable );
		}

		public CLocation getCLocation( Dockable dockable, Location location ) {
			return CScreenDockStationHandle.this.getCLocation( dockable, location );
		}
	}
	
	/**
	 * A representation of {@link CScreenDockStationHandle} as {@link CMaximizedModeArea}.
	 * @author Benjamin Sigg
	 */
	protected class Maximal implements CMaximizedModeArea{
		private List<ModeAreaListener> listeners = new ArrayList<ModeAreaListener>();

		public Dockable[] getMaximized() {
			return station.getStation().getFullscreenChildren();
		}

		public boolean isRepresenting( DockStation station ) {
			return CScreenDockStationHandle.this.station.getStation() == station;
		}

		public Runnable onApply( LocationModeEvent event ) {
			return null;
		}

		public Runnable onApply( LocationModeEvent event, Dockable replacement ) {
			return null;
		}

		public void prepareApply( Dockable dockable, AffectedSet affected ) {
			// ignore
		}
		
		public LocationMode getUnmaximizedMode(){
			return externalMode;
		}

		public void setMaximized( Dockable dockable, boolean maximized, AffectedSet set ) {
			ScreenDockStation station = getStation();
			DockStation parent = dockable.getDockParent();
			while( parent != station && parent != null ){
				dockable = parent.asDockable();
				parent = dockable == null ? null : dockable.getDockParent();
			}
			if( parent == null ){
				throw new IllegalArgumentException( "dockable was not a child of this station" );
			}
			
			set.add( dockable );
			getStation().setFullscreen( dockable, maximized );
		}

		public void addModeAreaListener( ModeAreaListener listener ) {
			listeners.add( listener );
		}

		public void removeModeAreaListener( ModeAreaListener listener ) {
			listeners.remove( listener );
		}
		
		/**
		 * Gets all the listeners that are registered at this area.
		 * @return all listeners
		 */
		public ModeAreaListener[] listeners(){
			return listeners.toArray( new ModeAreaListener[ listeners.size() ] );
		}
		
		public ScreenDockStation getStation() {
			return station.getStation();
		}

		public String getUniqueId() {
			return station.getUniqueId();
		}

		public boolean isChild( Dockable dockable ) {
			ScreenDockStation station = getStation();
			return dockable.getDockParent() == station && station.isFullscreen( dockable );
		}

		public boolean respectWorkingAreas() {
			return false;
		}

		public void setController( DockController controller ) {
			// ignore
		}
		
		public void setMode( LocationMode mode ){
			// ignore	
		}

		public CLocation getCLocation( Dockable dockable ) {
			return CScreenDockStationHandle.this.getCLocation( dockable );
		}

		public CLocation getCLocation( Dockable dockable, Location location ) {
			return CScreenDockStationHandle.this.getCLocation( dockable, location );
		}
		
	}
}
