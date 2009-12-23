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
package bibliothek.gui.dock.common.mode.station;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.common.mode.MaximizedMode;
import bibliothek.gui.dock.common.mode.MaximizedModeArea;
import bibliothek.gui.dock.common.mode.NormalModeArea;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A link between {@link SplitDockStation}, {@link NormalModeArea} and
 * {@link MaximizedModeArea}.
 * @author Benjamin Sigg
 */
public class SplitDockStationHandle {
	/** unique id of this handle */
	private String id; 
	/** station managed by this handle */
	private SplitDockStation station;
	
	/** normal-mode */
	private Normal normal = new Normal();
	/** maximized-mode */
	private Maximal maximal = new Maximal();
	
	private MaximizedMode maximizedMode;
	
	/**
	 * Creates a new handle.
	 * @param id the unique id of this handle
	 * @param station the station to be managed
	 */
	public SplitDockStationHandle( String id, SplitDockStation station ){
		if( id == null )
			throw new IllegalArgumentException( "id must not be null" );
		if( station == null )
			throw new IllegalArgumentException( "station must not be null" );
		
		this.id = id;
		this.station = station;
	}
	
	/**
	 * Gets the station which is managed by this handle.
	 * @return the station
	 */
	public SplitDockStation getStation(){
		return station;
	}
	
	/**
	 * Returns this as {@link NormalModeArea}
	 * @return a representation of <code>this</code>
	 */
	public NormalModeArea asNormalModeArea(){
		return normal;
	}
	
	/**
	 * Returns this as {@link MaximizedModeArea}
	 * @return a representation of <code>this</code>
	 */
	public MaximizedModeArea asMaximziedModeArea(){
		return maximal;
	}
	
	private class Normal implements NormalModeArea{
		public boolean isNormalModeChild( Dockable dockable ){
			return isChild( dockable ) && station.getFullScreen() != dockable;
		}

		public DockableProperty getLocation( Dockable child ){
			return DockUtilities.getPropertyChain( station, child );
		}

		public String getUniqueId(){
			return id;
		}

		public boolean isChild( Dockable dockable ){
			return dockable.getDockParent() == station && !maximal.isChild( dockable );
		}

		public void setLocation( Dockable dockable, DockableProperty location ){
			maximal.setMaximized( null );
			
			if( dockable.getDockParent() == station ){
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
	}
	
	private class Maximal implements MaximizedModeArea{
		public void connect( MaximizedMode mode ){
			if( maximizedMode != null )
				throw new IllegalStateException( "handle already in use" );
			maximizedMode = mode;
		}
		
		public String getUniqueId(){
			return id; 
		}
		
		public boolean isChild( Dockable dockable ){
			return getMaximized() == dockable;
		}
		
		public Dockable getMaximized(){
			return station.getFullScreen();
		}

		public void setMaximized( Dockable dockable ){
			
			
			// TODO Auto-generated method stub
			
		}
		
	}
}
