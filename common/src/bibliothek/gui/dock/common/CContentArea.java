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

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.FlapDockStation.Direction;
import bibliothek.gui.dock.common.event.ResizeRequestListener;
import bibliothek.gui.dock.common.intern.AbstractCStation;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.station.FlapResizeRequestHandler;
import bibliothek.gui.dock.common.intern.station.SplitResizeRequestHandler;
import bibliothek.gui.dock.common.location.CBaseLocation;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.common.mode.station.CFlapDockStationHandle;
import bibliothek.gui.dock.common.mode.station.CSplitDockStationHandle;
import bibliothek.gui.dock.common.perspective.CGridPerspective;
import bibliothek.gui.dock.common.perspective.CMinimizePerspective;
import bibliothek.gui.dock.common.perspective.CStationPerspective;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

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
@Todo( priority=Priority.MINOR, compatibility=Compatibility.COMPATIBLE, target=Version.VERSION_1_1_0, 
		description="Introduce some interface 'MultiCStation' or similar: a set of CStations, CContentArea would implement this interface" )
public class CContentArea extends JPanel{
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
	private SplitDockStation center;

	/** the child at the north border */
	private FlapDockStation north;
	/** the child at the south border */
	private FlapDockStation south;
	/** the child at the east border */
	private FlapDockStation east;
	/** the child at the west border */
	private FlapDockStation west;

	/** the component at the north side */
	private JComponent northComponent;
	/** the component at the south side */
	private JComponent southComponent;
	/** the component at the east side */
	private JComponent eastComponent;
	/** the component at the west side */
	private JComponent westComponent;

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
		center = control.getFactory().createSplitDockStation();
		center.setExpandOnDoubleclick( false );

		northComponent = new JPanel( new BorderLayout() );
		southComponent = new JPanel( new BorderLayout() );
		eastComponent = new JPanel( new BorderLayout() );
		westComponent = new JPanel( new BorderLayout() );

		north = control.getFactory().createFlapDockStation( northComponent );
		south = control.getFactory().createFlapDockStation( southComponent );
		east = control.getFactory().createFlapDockStation( eastComponent );
		west = control.getFactory().createFlapDockStation( westComponent );

		north.setAutoDirection( false );
		north.setDirection( Direction.SOUTH );

		south.setAutoDirection( false );
		south.setDirection( Direction.NORTH );

		east.setAutoDirection( false );
		east.setDirection( Direction.WEST );

		west.setAutoDirection( false );
		west.setDirection( Direction.EAST );

		setLayout( new BorderLayout() );
		northComponent.add( north.getComponent(), BorderLayout.CENTER );
		southComponent.add( south.getComponent(), BorderLayout.CENTER );
		eastComponent.add( east.getComponent(), BorderLayout.CENTER );
		westComponent.add( west.getComponent(), BorderLayout.CENTER );
		add( center, BorderLayout.CENTER );
		add( northComponent, BorderLayout.NORTH );
		add( southComponent, BorderLayout.SOUTH );
		add( eastComponent, BorderLayout.EAST );
		add( westComponent, BorderLayout.WEST );

		CBaseLocation base = new CBaseLocation( this );

		stations = new CStation[]{
				new CenterStation( center, getCenterIdentifier(), base.normal() ),
				new MinimizeStation( north, getNorthIdentifier(), base.minimalNorth() ),
				new MinimizeStation( south, getSouthIdentifier(), base.minimalSouth() ),
				new MinimizeStation( east, getEastIdentifier(), base.minimalEast() ),
				new MinimizeStation( west, getWestIdentifier(), base.minimalWest() )
		};
	}

	/**
	 * Gets the unique id of this center.
	 * @return the unique id
	 */
	public String getUniqueId(){
		return uniqueId;
	}

	/**
	 * Gets the {@link CControl} for which this content area was created.
	 * @return the owner of this area
	 */
	public CControl getControl(){
		return control;
	}

	/**
	 * Gets an independant array of all stations that are used on this
	 * {@link CContentArea}.
	 * @return the list of stations
	 */
	public CStation<?>[] getStations(){
		CStation<?>[] copy = new CStation[ stations.length ];
		System.arraycopy( stations, 0, copy, 0, stations.length );
		return copy;
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
						northComponent.remove( cornerComponents[ index ] );
					}
					else{
						westComponent.remove( cornerComponents[ index ] );
					}
					break;
				case NORTH_EAST:
					if( horizontal ){
						northComponent.remove( cornerComponents[ index ] );
					}
					else{
						eastComponent.remove( cornerComponents[ index ] );
					}
					break;
				case SOUTH_WEST:
					if( horizontal ){
						southComponent.remove( cornerComponents[ index ] );
					}
					else{
						westComponent.remove( cornerComponents[ index ] );
					}
					break;
				case SOUTH_EAST:
					if( horizontal ){
						southComponent.remove( cornerComponents[ index ] );
					}
					else{
						eastComponent.remove( cornerComponents[ index ] );
					}
					break;
			}
		}

		cornerComponents[ index ] = component;
		if( component != null ){
			switch( corner ){
				case NORTH_WEST:
					if( horizontal ){
						northComponent.add( component, BorderLayout.WEST );
					}
					else{
						westComponent.add( component, BorderLayout.NORTH );
					}
					break;
				case NORTH_EAST:
					if( horizontal ){
						northComponent.add( component, BorderLayout.EAST );
					}
					else{
						eastComponent.add( component, BorderLayout.NORTH );
					}
					break;
				case SOUTH_WEST:
					if( horizontal ){
						southComponent.add( component, BorderLayout.WEST );
					}
					else{
						westComponent.add( component, BorderLayout.SOUTH );
					}
					break;
				case SOUTH_EAST:
					if( horizontal ){
						southComponent.add( component, BorderLayout.EAST );
					}
					else{
						eastComponent.add( component, BorderLayout.SOUTH );
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
		north.setMinimumSize( size );
		south.setMinimumSize( size );
		west.setMinimumSize( size );
		east.setMinimumSize( size );
	}
	
	/**
	 * Gets the station in the center of this {@link CContentArea}.
	 * @return the central station
	 */
	public SplitDockStation getCenter(){
		return center;
	}

	/**
	 * Gets the station in the north of this {@link CContentArea}
	 * @return the station in the north
	 */
	public FlapDockStation getNorth(){
		return north;
	}

	/**
	 * Gets the station in the south of this {@link CContentArea}
	 * @return the station in the south
	 */
	public FlapDockStation getSouth(){
		return south;
	}

	/**
	 * Gets the station in the east of this {@link CContentArea}
	 * @return the station in the east
	 */
	public FlapDockStation getEast(){
		return east;
	}

	/**
	 * Gets the station in the west of this {@link CContentArea}
	 * @return the station in the west
	 */
	public FlapDockStation getWest(){
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

	/**
	 * A wrapper around the {@link FlapDockStation}s which represent the minimize
	 * areas.
	 * @author Benjamin Sigg
	 */
	private static class MinimizeStation extends AbstractCStation<FlapDockStation>{
		private ResizeRequestListener handler;
		private CFlapDockStationHandle modeManagerHandle;
		
		public MinimizeStation( FlapDockStation station, String id, CLocation location ){
			super( station, id, location );
			handler = new FlapResizeRequestHandler( station );
			modeManagerHandle = new CFlapDockStationHandle( this );
		}

		@Override
		protected void install( CControlAccess access ) {
			access.getOwner().addResizeRequestListener( handler );
			access.getLocationManager().getMinimizedMode().add( modeManagerHandle );
		}
		@Override
		protected void uninstall( CControlAccess access ) {
			access.getOwner().removeResizeRequestListener( handler );
			access.getLocationManager().getMinimizedMode().remove( modeManagerHandle.getUniqueId() );
		}
		
		public CStationPerspective createPerspective(){
			return new CMinimizePerspective( getUniqueId() );
		}
	}

	/**
	 * A wrapper around the {@link SplitDockStation} that sits in the middle
	 * of the area.
	 * @author Benjamin Sigg
	 */
	private class CenterStation extends AbstractCStation<SplitDockStation>{
		private ResizeRequestListener handler;
		private CSplitDockStationHandle modeManagerHandle;

		public CenterStation( SplitDockStation station, String id, CLocation location ){
			super( station, id, location );
			handler = new SplitResizeRequestHandler( station );
			modeManagerHandle = new CSplitDockStationHandle( this, control.getLocationManager() );
		}

		@Override
		protected void install( CControlAccess access ) {
			access.getOwner().addResizeRequestListener( handler );
			CLocationModeManager manager = access.getLocationManager();
			manager.getNormalMode().add( modeManagerHandle.asNormalModeArea() );
			manager.getMaximizedMode().add( modeManagerHandle.asMaximziedModeArea() );
		}
		@Override
		protected void uninstall( CControlAccess access ) {
			access.getOwner().removeResizeRequestListener( handler );
			CLocationModeManager manager = access.getLocationManager();
			manager.getNormalMode().remove( modeManagerHandle.asNormalModeArea().getUniqueId() );
			manager.getMaximizedMode().remove( modeManagerHandle.asMaximziedModeArea().getUniqueId() );
		}
		
		public CStationPerspective createPerspective(){
			return new CGridPerspective( getUniqueId() );
		}
	}
}
