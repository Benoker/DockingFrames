/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.gui.dock.common;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.FlapDockStation.Direction;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.location.CBaseLocation;
import bibliothek.gui.dock.common.location.CMinimizedLocation;
import bibliothek.gui.dock.common.location.Side;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.util.Path;

/**
 * A component that is normally set into the center of the
 * main- {@link JFrame}. This component can display
 * and manage some {@link CDockable}s.<br>
 * This component contains in the center a {@link SplitDockStation} allowing
 * to show several {@link CDockable}s at the same time. At each border a
 * {@link FlapDockStation} allows to show "minimized" {@link CDockable}s.<br>
 * Note: clients should not create {@link CContentArea}s directly, they should 
 * use {@link CControl#getContentArea()} to get the default content area, or
 * {@link CControl#createContentArea(String)} to create a new content area. 
 * @author Benjamin Sigg
 */
public class CContentArea extends JPanel implements CStationContainer{
	
	/** The result of {@link CStation#getTypeId()} for the center station */
	public static final Path TYPE_ID_CENTER = new Path( "dock", "CContentArea", "center" );
	
	/** The result of {@link CStation#getTypeId()} for the minimize stations */
	public static final Path TYPE_ID_MINIMIZE = new Path( "dock", "CContentArea", "minimize" );
	
	/**
	 * References a corner of a panel.
	 * @author Benjamin Sigg
	 */
	public static enum Corner{
		/** the lower right corner */
		SOUTH_EAST,
		/** the lower left corner */
		SOUTH_WEST,
		/** the higher right corner */
		NORTH_EAST,
		/** the higher left corner */
		NORTH_WEST;
	}

	/** the child in the center */
	private CenterStation center;

	/** the child at the north border */
	private MinimizeStation north;
	/** the child at the south border */
	private MinimizeStation south;
	/** the child at the east border */
	private MinimizeStation east;
	/** the child at the west border */
	private MinimizeStation west;

	/** the components in the corners */
	private Component[] cornerComponents = new Component[8];

	/** an identifier for this center */
	private String uniqueId;

	/** access to the controller which uses this area */
	private CControl control;

	/** the set of stations on this content area */
	private CStation<?>[] stations;

	/**
	 * Creates a new content area.
	 * @param control the control for which this area will be used
	 * @param uniqueId a unique identifier of this center
	 */
	public CContentArea( CControl control, String uniqueId ){
		this.control = control;
		this.uniqueId = uniqueId;
		

		CBaseLocation base = new CBaseLocation( this );

		center = new CenterStation( getCenterIdentifier(), base.normal() );
		north = new MinimizeStation( getNorthIdentifier(), new CMinimizedLocation( base, Side.NORTH ) );
		south = new MinimizeStation( getSouthIdentifier(), new CMinimizedLocation( base, Side.SOUTH ) );
		east = new MinimizeStation( getEastIdentifier(), new CMinimizedLocation( base, Side.EAST ) );
		west = new MinimizeStation( getWestIdentifier(), new CMinimizedLocation( base, Side.WEST ) );
		
		center.getStation().setExpandOnDoubleclick( false );

		north.setDirection( Direction.SOUTH );

		south.setDirection( Direction.NORTH );

		east.setDirection( Direction.WEST );

		west.setDirection( Direction.EAST );

		setLayout( new BorderLayout() );
		add( center.getStation(), BorderLayout.CENTER );
		add( north, BorderLayout.NORTH );
		add( south, BorderLayout.SOUTH );
		add( east, BorderLayout.EAST );
		add( west, BorderLayout.WEST );

		stations = new CStation[]{ north, south, east, west, center };
	}
	
	/**
	 * Adds additional stations to the {@link #getStations() array} of {@link CStation}. This method
	 * should only be called by subclasses. 
	 * @param stations the additional stations to store
	 */
	protected void addStations( CStation<?>... stations ){
		CStation<?>[] temp = new CStation<?>[ this.stations.length + stations.length ];
		System.arraycopy( this.stations, 0, temp, 0, this.stations.length );
		System.arraycopy( stations, 0, temp, this.stations.length, stations.length );
		this.stations = temp;
	}

	/**
	 * Gets the unique id of this center.
	 * @return the unique id
	 */
	public String getUniqueId(){
		return uniqueId;
	}
	
	public Component getComponent(){
		return this;
	}

	public void addStationContainerListener( CStationContainerListener listener ){
		// ignore, the CContentArea is not mutable hence the listener will never be called
	}
	
	public void removeStationContainerListener( CStationContainerListener listener ){
		// ignore
	}
	
	/**
	 * Gets the {@link CControl} for which this content area was created.
	 * @return the owner of this area
	 */
	public CControl getControl(){
		return control;
	}

	/**
	 * Gets an independent array of all stations that are used on this
	 * {@link CContentArea}.
	 * @return the list of stations
	 */
	public CStation<?>[] getStations(){
		CStation<?>[] copy = new CStation[ stations.length ];
		System.arraycopy( stations, 0, copy, 0, stations.length );
		return copy;
	}
	
	public int getStationCount(){
		return stations.length;
	}
	
	public CStation<?> getStation( int index ){
		return stations[index];
	}
	
	/**
	 * Gets the index of <code>child</code>.
	 * @param child a child {@link CStation} of this area.
	 * @return the index of <code>child</code> or -1 if not found
	 */
	public int indexOf( CStation<?> child ){
		for( int i = 0; i < stations.length; i++ ){
			if( stations[i] == child ){
				return i;
			}
		}
		return -1;
	}
	
	public CStation<?> getDefaultStation(){
		return center;
	}
	
	public CStation<?> getDefaultStation( ExtendedMode mode ){
		if( mode == ExtendedMode.MINIMIZED ){
			return north;
		}
		if( mode == ExtendedMode.NORMALIZED ){
			return center;
		}
		if( mode == ExtendedMode.MAXIMIZED ){
			return center;
		}
		return null;
	}
	
	/**
	 * Exchanges all the {@link CDockable}s on the center panel by
	 * the elements of <code>grid</code>.
	 * @param grid a grid containing some new {@link Dockable}s
	 */
	public void deploy( CGrid grid ){
		getCenter().dropTree( grid.toTree() );
	}

	/**
	 * Puts <code>component</code> in one corner of this area.
	 * @param component the component, can be <code>null</code>
	 * @param corner the corner into which to put <code>component</code>
	 * @param horizontal whether <code>component</code> should be horizontally
	 * or vertically.
	 */
	public void setCornerComponent( Component component, Corner corner, boolean horizontal ){
		int index = corner.ordinal() * 2;
		if( horizontal )
			index++;

		if( cornerComponents[ index ] != null ){
			switch( corner ){
				case NORTH_WEST:
					if( horizontal ){
						north.remove( cornerComponents[ index ] );
					}
					else{
						west.remove( cornerComponents[ index ] );
					}
					break;
				case NORTH_EAST:
					if( horizontal ){
						north.remove( cornerComponents[ index ] );
					}
					else{
						east.remove( cornerComponents[ index ] );
					}
					break;
				case SOUTH_WEST:
					if( horizontal ){
						south.remove( cornerComponents[ index ] );
					}
					else{
						west.remove( cornerComponents[ index ] );
					}
					break;
				case SOUTH_EAST:
					if( horizontal ){
						south.remove( cornerComponents[ index ] );
					}
					else{
						east.remove( cornerComponents[ index ] );
					}
					break;
			}
		}

		cornerComponents[ index ] = component;
		if( component != null ){
			switch( corner ){
				case NORTH_WEST:
					if( horizontal ){
						north.add( component, BorderLayout.WEST );
					}
					else{
						west.add( component, BorderLayout.NORTH );
					}
					break;
				case NORTH_EAST:
					if( horizontal ){
						north.add( component, BorderLayout.EAST );
					}
					else{
						east.add( component, BorderLayout.NORTH );
					}
					break;
				case SOUTH_WEST:
					if( horizontal ){
						south.add( component, BorderLayout.WEST );
					}
					else{
						west.add( component, BorderLayout.SOUTH );
					}
					break;
				case SOUTH_EAST:
					if( horizontal ){
						south.add( component, BorderLayout.EAST );
					}
					else{
						east.add( component, BorderLayout.SOUTH );
					}
					break;
			}
		}
	}

	/**
	 * Gets the component of a corner.
	 * @param corner the corner in which to search
	 * @param horizontal whether the component is horizontally or vertically
	 * @return the component or <code>null</code>
	 */
	public Component getCornerComponent( Corner corner, boolean horizontal ){
		int index = corner.ordinal() * 2;
		if( horizontal )
			index++;
		return cornerComponents[ index ];
	}

	/**
	 * Sets the minimum size of the four areas in which minimized {@link Dockable}s
	 * are shown. Clients could also call <code>get'Side'().setMinimumSize( size )</code>.<br>
	 * There is no method <code>getMinimumAreaSize</code> because the result might
	 * not be the same for all stations.
	 * @param size the new minimum size or <code>null</code> to revert to the default
	 * value.
	 * @see FlapDockStation#setMinimumSize(Dimension)
	 * @see FlapDockStation#MINIMUM_SIZE
	 */
	public void setMinimumAreaSize( Dimension size ){
		north.getStation().setMinimumSize( size );
		south.getStation().setMinimumSize( size );
		west.getStation().setMinimumSize( size );
		east.getStation().setMinimumSize( size );
	}
	
	/**
	 * Gets the station in the center of this {@link CContentArea}.
	 * @return the central station
	 */
	public SplitDockStation getCenter(){
		return center.getStation();
	}
	
	/**
	 * Gets the station in the center of this {@link CContentArea}.
	 * @return the central station
	 */
	public CGridArea getCenterArea(){
		return center;
	}
	
	/**
	 * Gets the station in the north of this {@link CContentArea}
	 * @return the station in the north
	 */
	public FlapDockStation getNorth(){
		return north.getStation();
	}

	/**
	 * Gets the station in the north of this {@link CContentArea}
	 * @return the station in the north
	 */
	public CMinimizeArea getNorthArea(){
		return north;
	}

	/**
	 * Gets the station in the south of this {@link CContentArea}
	 * @return the station in the south
	 */
	public FlapDockStation getSouth(){
		return south.getStation();
	}

	/**
	 * Gets the station in the south of this {@link CContentArea}
	 * @return the station in the south
	 */
	public CMinimizeArea getSouthArea(){
		return south;
	}

	/**
	 * Gets the station in the east of this {@link CContentArea}
	 * @return the station in the east
	 */
	public FlapDockStation getEast(){
		return east.getStation();
	}
	
	/**
	 * Gets the station in the east of this {@link CContentArea}
	 * @return the station in the east
	 */
	public CMinimizeArea getEastArea(){
		return east;
	}

	/**
	 * Gets the station in the west of this {@link CContentArea}
	 * @return the station in the west
	 */
	public FlapDockStation getWest(){
		return west.getStation();
	}

	/**
	 * Gets the station in the west of this {@link CContentArea}
	 * @return the station in the west
	 */
	public CMinimizeArea getWestArea(){
		return west;
	}

	/**
	 * Gets the global identifier for the panel in the center.
	 * @return the identifier
	 */
	public String getCenterIdentifier(){
		return getCenterIdentifier( uniqueId );
	}

	/**
	 * Creates the global identifier of a panel in the center.
	 * @param uniqueCenterId the unique if of the owning {@link CContentArea}.
	 * @return the global identifier
	 */
	public static String getCenterIdentifier( String uniqueCenterId ){
		return uniqueCenterId + " center";
	}

	/**
	 * Gets the global identifier for the panel in the north.
	 * @return the identifier
	 */
	public String getNorthIdentifier(){
		return getNorthIdentifier( uniqueId );
	}


	/**
	 * Creates the global identifier of a panel in the north.
	 * @param uniqueCenterId the unique id of the owning {@link CContentArea}.
	 * @return the global identifier
	 */
	public static String getNorthIdentifier( String uniqueCenterId ){
		return uniqueCenterId + " north";
	}


	/**
	 * Gets the global identifier for the panel in the south.
	 * @return the identifier
	 */
	public String getSouthIdentifier(){
		return getSouthIdentifier( uniqueId );
	}


	/**
	 * Creates the global identifier of a panel in the south.
	 * @param uniqueCenterId the unique id of the owning {@link CContentArea}.
	 * @return the global identifier
	 */
	public static String getSouthIdentifier( String uniqueCenterId ){
		return uniqueCenterId + " south";
	}

	/**
	 * Gets the global identifier for the panel in the east.
	 * @return the identifier
	 */
	public String getEastIdentifier(){
		return getEastIdentifier( uniqueId );
	}

	/**
	 * Creates the global identifier of a panel in the east.
	 * @param uniqueCenterId the unique id of the owning {@link CContentArea}.
	 * @return the global identifier
	 */
	public static String getEastIdentifier( String uniqueCenterId ){
		return uniqueCenterId + " east";
	}

	/**
	 * Gets the global identifier for the panel in the west.
	 * @return the identifier
	 */
	public String getWestIdentifier(){
		return getWestIdentifier( uniqueId );
	}

	/**
	 * Creates the global identifier of a panel in the west.
	 * @param uniqueCenterId the unique id of the owning {@link CContentArea}.
	 * @return the global identifier
	 */
	public static String getWestIdentifier( String uniqueCenterId ){
		return uniqueCenterId + " west";
	}
	
	public CStation<?> getMatchingStation( CStationContainer container, CStation<?> station ){
		if( container == this ){
			return station;
		}
		
		if( container instanceof CContentArea ){
			CContentArea other = (CContentArea)container;
			if( other.getStationCount() == getStationCount() ){
				int index = other.indexOf( station );
				if( index != -1 ){
					return getStation( index );
				}
			}
		}
		return null;
	}

	/**
	 * A wrapper around the {@link FlapDockStation}s which represent the minimize
	 * areas.
	 * @author Benjamin Sigg
	 */
	private class MinimizeStation extends CMinimizeArea{
		private CLocation location;
		
		public MinimizeStation( String id, CLocation location ){
			this.location = location;
			init( control, id );
		}
		
		public Path getTypeId(){
			return TYPE_ID_MINIMIZE;
		}
		
		@Override
		public CLocation getStationLocation(){
			return location;
		}
	}

	/**
	 * A wrapper around the {@link SplitDockStation} that sits in the middle
	 * of the area.
	 * @author Benjamin Sigg
	 */
	private class CenterStation extends CGridArea {
		private CLocation location;

		public CenterStation( String id, CLocation location ){
			super( control, id );
			this.location = location;
		}

		public Path getTypeId(){
			return TYPE_ID_CENTER;
		}
		
		@Override
		public CLocation getStationLocation(){
			return location;
		}
	}
}
