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
package bibliothek.gui.dock.common.perspective;

import java.util.List;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGridArea;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.CPlaceholderStrategy;
import bibliothek.gui.dock.common.intern.station.CommonDockStationFactory;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.common.perspective.mode.CMaximizedModeAreaPerspective;
import bibliothek.gui.dock.common.perspective.mode.CMaximizedModePerspective;
import bibliothek.gui.dock.common.perspective.mode.CModeAreaPerspective;
import bibliothek.gui.dock.common.perspective.mode.CNormalModePerspective;
import bibliothek.gui.dock.common.perspective.mode.LocationModeManagerPerspective;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.station.split.GridNode;
import bibliothek.gui.dock.station.split.PerspectiveSplitDockGrid;
import bibliothek.gui.dock.station.split.PerspectiveSplitDockTree;
import bibliothek.gui.dock.station.split.SplitDockFullScreenProperty;
import bibliothek.gui.dock.station.split.SplitDockPathProperty;
import bibliothek.gui.dock.station.split.SplitDockPerspective;
import bibliothek.gui.dock.station.split.SplitDockPlaceholderProperty;
import bibliothek.gui.dock.station.split.SplitDockProperty;
import bibliothek.gui.dock.station.split.SplitDockPerspective.Entry;
import bibliothek.gui.dock.station.split.SplitDockPerspective.Node;
import bibliothek.gui.dock.station.split.SplitDockPerspective.Root;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;

/**
 * A representation of a {@link CGridArea}. If this perspective is loaded with content, then all the coordinates
 * are in a range between 0 and 100.
 * @author Benjamin Sigg
 */
public class CGridPerspective extends SingleCDockablePerspective implements CStationPerspective {
	/** the intern representation of this perspective */
	private CommonSplitDockPerspective delegate;

	/** helper class to build up this perspective */
	private PerspectiveSplitDockGrid grid;

	/** whether {@link #gridDeploy()} is called automatically */
	private boolean autoDeploy = true;

	/** whether there are changes on {@link #grid} */
	private boolean gridChanges = false;

	/** whether {@link #gridDeploy()} currently is executed */
	private boolean onDeploy = false;

	/** the owner of this object */
	private CPerspective perspective;

	/** the mode the currently maximized element had before maximization, can be <code>null</code> */
	private Path unmaximizeMode;

	/** the location the currently maximized element had before maximization, can be <code>null</code> */
	private Location unmaximizeLocation;
	
	/** whether this perspective acts as working area */
	private boolean workingArea;
	
	/** The type of this perspective */
	private Path typeId;

	/** Whether this is a root station */
	private boolean root = true;
	
	/** identifiers children that are in normal mode */
	private CModeAreaPerspective normalMode = new CModeAreaPerspective(){
		public String getUniqueId(){
			return CGridPerspective.this.getUniqueId();
		}

		public boolean isChild( PerspectiveDockable dockable ){
			if( dockable.getParent() == intern() ) {
				return delegate().getFullscreen() != dockable;
			}
			return false;
		}
		
		public boolean isChildLocation( DockableProperty location ){
			return location instanceof SplitDockProperty 
					|| location instanceof SplitDockPathProperty
					|| location instanceof SplitDockPlaceholderProperty;
		}
	};

	/** identifies children that are in maximized mode */
	private CMaximizedModeAreaPerspective maximalMode = new CMaximizedModeAreaPerspective(){
		public String getUniqueId(){
			return CGridPerspective.this.getUniqueId();
		}

		public boolean isChild( PerspectiveDockable dockable ){
			if( dockable.getParent() == intern() ) {
				return delegate().getFullscreen() == dockable;
			}
			return false;
		}

		public void setUnmaximize( Path mode, Location location ){
			unmaximizeLocation = location;
			unmaximizeMode = mode;
		}

		public Location getUnmaximizeLocation(){
			return unmaximizeLocation;
		}

		public Path getUnmaximizeMode(){
			return unmaximizeMode;
		}
		
		public boolean isChildLocation( DockableProperty location ){
			return location instanceof SplitDockFullScreenProperty;
		}
	};
	
	/**
	 * Creates a new, empty perspective.
	 * @param id the unique identifier of this perspective
	 */
	public CGridPerspective( String id ){
		this( id, null );
	}
	
	/**
	 * Creates a new, empty perspective.
	 * @param id the unique identifier of this perspective
	 * @param typeId the type of this station, can be <code>null</code>
	 */
	public CGridPerspective( String id, Path typeId ){
		this( id, typeId, false );
	}

	/**
	 * Creates a new, empty perspective.
	 * @param id the unique identifier of this perspective
	 * @param typeId the type of this station, can be <code>null</code>
	 * @param workingArea whether this station should be treated as {@link CStation#isWorkingArea() working area} or not.
	 */
	public CGridPerspective( String id, Path typeId, boolean workingArea ){
		super( id );
		delegate = new CommonSplitDockPerspective();
		delegate.setHasFullscreenAction( false );
		setWorkingArea( workingArea );
		gridClear();
		if( typeId == null ){
			typeId = CGridArea.TYPE_ID;
		}
		this.typeId = typeId;
	}
	
	public boolean isWorkingArea(){
		return workingArea;
	}
	
	public Path getTypeId(){
		return typeId;
	}
	
	public boolean isRoot(){
		return root;
	}
	
	public void setRoot( boolean root ){
		this.root = root;
	}
	
	/**
	 * Sets whether this station should be regarded as a {@link CStation#isWorkingArea() working area} or not. This
	 * setting is not stored, it is the clients responsibility to make sure that the matching {@link CStation} is
	 * or is not a working area.
	 * @param workingArea whether this station is to be treated like a working area or not
	 */
	public void setWorkingArea( boolean workingArea ){
		this.workingArea = workingArea;
	}

	@Override
	protected CommonSplitDockPerspective create(){
		return delegate;
	}
	
	private CommonSplitDockPerspective delegate() {
		return intern();
	}

	@Override
	public CommonSplitDockPerspective intern(){
		return (CommonSplitDockPerspective) super.intern();
	}

	public void setPerspective( CPerspective perspective ){
		if( this.perspective != null ) {
			((CNormalModePerspective) this.perspective.getLocationManager().getMode( ExtendedMode.NORMALIZED )).remove( normalMode );
			((CMaximizedModePerspective) this.perspective.getLocationManager().getMode( ExtendedMode.MAXIMIZED )).remove( maximalMode );
		}
		this.perspective = perspective;
		if( this.perspective != null ) {
			((CNormalModePerspective) this.perspective.getLocationManager().getMode( ExtendedMode.NORMALIZED )).add( normalMode );
			((CMaximizedModePerspective) this.perspective.getLocationManager().getMode( ExtendedMode.MAXIMIZED )).add( maximalMode );
		}
	}
	
	public CPerspective getPerspective(){
		return perspective;
	}

	/**
	 * Calls {@link #gridDeploy()}, but only if {@link #isAutoDeploy()} returns <code>true</code> and
	 * if {@link #grid() the grid} was accessed.
	 */
	protected void maybeDeploy(){
		if( isAutoDeploy() && gridChanges ) {
			gridDeploy();
		}
	}

	private PerspectiveDockable[] convert( CDockablePerspective[] dockables ){
		PerspectiveDockable[] result = new PerspectiveDockable[dockables.length];
		for( int i = 0; i < result.length; i++ ) {
			result[i] = dockables[i].intern().asDockable();
		}
		return result;
	}

	/**
	 * Unpacks the stations (e.g. a stack) that is stored at <code>x,y,width,height</code>. The result
	 * is like removing all children and add them again with {@link #gridAdd(double, double, double, double, CDockablePerspective...)}.
	 * @param x the x-coordinate of a set of {@link CDockablePerspective}, can be any number
	 * @param y the y-coordinate of a set of {@link CDockablePerspective}, can be any number
	 * @param width the width of a set of {@link CDockablePerspective}, can be any number greater than 0
	 * @param height the height of a set of {@link CDockablePerspective}, can be any number greater than 0
	 */
	public void unpack( double x, double y, double width, double height ){
		gridChanges = true;
		grid.unpack( x, y, width, height );
	}
	
	/**
	 * Adds <code>dockables</code> at location <code>x/y</code> with size <code>width/height</code> to an internal 
	 * list of pending commands to execute. This method does not change the layout of this area, but a call
	 * to {@link #gridDeploy()} will.<br>
	 * Calling this method several times with the same location and size has the same effect as calling it once,
	 * but with a bigger array that contains all the dockables that would otherwise be added through many calls. 
	 * @param x the x-coordinate of <code>dockables</code>, can be any number
	 * @param y the y-coordinate of <code>dockables</code>, can be any number
	 * @param width the width of <code>dockables</code>, can be any number greater than 0
	 * @param height the height of <code>dockables</code>, can be any number greater than 0
	 * @param dockables the elements to add, should contain at least one item
	 * @see #gridClear()
	 * @see #gridDeploy()
	 */
	public void gridAdd( double x, double y, double width, double height, CDockablePerspective... dockables ){
		gridChanges = true;
		grid.addDockable( x, y, width, height, convert( dockables ) );
	}
	
	/**
	 * Adds <code>dockables</code> as placeholder at location <code>x/y</code> with size <code>width/height</code> to 
	 * an internal list of pending commands to execute. This method does not change the layout of this area, but a call
	 * to {@link #gridDeploy()} will.<br>
	 * Calling this method several times with the same location and size has the same effect as calling it once,
	 * but with a bigger array that contains all the dockables that would otherwise be added through many calls.
	 * @param x the x-coordinate of <code>dockables</code>, can be any number
	 * @param y the y-coordinate of <code>dockables</code>, can be any number
	 * @param width the width of <code>dockables</code>, can be any number greater than 0
	 * @param height the height of <code>dockables</code>, can be any number greater than 0
	 * @param dockables the elements whose placeholders to add, should contain at least one item
	 * @see #gridClear()
	 * @see #gridDeploy()
	 * @throws IllegalArgumentException if not all dockables have a placeholder
	 */
	public void gridPlaceholder( double x, double y, double width, double height, CDockablePerspective... dockables ){
		gridChanges = true;
		Path[] placeholders = new Path[ dockables.length ];
		for( int i = 0; i < dockables.length; i++ ){
			placeholders[i] = dockables[i].intern().asDockable().getPlaceholder();
			if( placeholders[i] == null ){
				throw new IllegalArgumentException( "dockable '" + i + "' does not have a placeholder: " + dockables[i] );
			}
		}
		grid.addPlaceholders( x, y, width, height, placeholders );
	}
	
	/**
	 * Adds placeholders at location <code>x/y</code> with size <code>width/height</code> to 
	 * an internal list of pending commands to execute. This method does not change the layout of this area, but a call
	 * to {@link #gridDeploy()} will.<br>
	 * Calling this method several times with the same location and size has the same effect as calling it once,
	 * but with a bigger array that contains all the dockables that would otherwise be added through many calls.
	 * @param x the x-coordinate of <code>dockables</code>, can be any number
	 * @param y the y-coordinate of <code>dockables</code>, can be any number
	 * @param width the width of <code>dockables</code>, can be any number greater than 0
	 * @param height the height of <code>dockables</code>, can be any number greater than 0
	 * @param placeholders the placeholders to add, should contain at least one element and no <code>null</code> elements
	 * @see #gridClear()
	 * @see #gridDeploy()
	 * @throws IllegalArgumentException if not all dockables have a placeholder
	 */
	public void gridPlaceholder( double x, double y, double width, double height, Path... placeholders ){
		gridChanges = true;
		grid.addPlaceholders( x, y, width, height, placeholders );
	}

	/**
	 * Using location <code>x/y</code> and size <code>width/height</code> as key, this method set the selection
	 * in a group of dockables. This method does not change the layout directly, but a call to {@link #gridDeploy()} will.
	 * @param x the x-coordinate of <code>dockables</code>, can be any number
	 * @param y the y-coordinate of <code>dockables</code>, can be any number
	 * @param width the width of <code>dockables</code>, can be any number greater than 0
	 * @param height the height of <code>dockables</code>, can be any number greater than 0
	 * @param selection the element that should be selected, must already be in the group
	 * @see #gridClear()
	 * @see #gridDeploy()
	 */
	public void gridSelect( double x, double y, double width, double height, CDockablePerspective selection ){
		gridChanges = true;
		grid.setSelected( x, y, width, height, selection == null ? null : selection.intern().asDockable() );
	}

	/**
	 * Adds a constraint to the algorithm that is executed by {@link #gridDeploy()}, the constraint tells that
	 * there should be a horizontal divider from <code>x1/y</code> to <code>x2/y</code>.
	 * @param x1 the beginning of the divider
	 * @param x2 the end of the divider
	 * @param y the vertical position of the divider
	 */
	public void gridHorizontal( double x1, double x2, double y ){
		gridChanges = true;
		grid.addHorizontalDivider( x1, x2, y );
	}

	/**
	 * Adds a constraint to the algorithm that is executed by {@link #gridDeploy()}, the constraint tells that
	 * there should be a vertical divider from <code>x/y1</code> to <code>x/y2</code>.
	 * @param x the horizontal position of the divider
	 * @param y1 the beginning of the divider
	 * @param y2 the end of the divider
	 */
	public void gridVertical( double x, double y1, double y2 ){
		gridChanges = true;
		grid.addVerticalDivider( x, y1, y2 );
	}

	/**
	 * Deletes all pending commands that were collected by the <code>grid*</code> methods. A call to this
	 * method does not change the current layout of this area, but a call to {@link #gridDeploy()} will.
	 * @see #gridDeploy()
	 */
	public void gridClear(){
		grid = new PerspectiveSplitDockGrid();
	}

	/**
	 * Removes all children of this area, then executes pending commands that add dockables at specified locations.<br>
	 * In particular this method analyzes all the commands that were generated by calls to the <code>grid*</code> methods 
	 * and merges them into a layout that fits the locations and sizes the client specified as good as possible.<br>
	 * If {@link #isAutoDeploy()} returns <code>true</code>, then this method is called automatically before storing 
	 * the layout of this area.<br>
	 * This method will silently return if the list of pending commands was never accessed directly or indirectly
	 * by the client.
	 * @see #isAutoDeploy() 
	 * @see #gridAdd(double, double, double, double, CDockablePerspective...)
	 * @see #gridSelect(double, double, double, double, CDockablePerspective)
	 * @see #gridHorizontal(double, double, double)
	 * @see #gridVertical(double, double, double)
	 * @see #gridClear()  
	 */
	public void gridDeploy(){
		if( gridChanges ) {
			gridChanges = false;
			try {
				onDeploy = true;
				delegate().read( grid.toTree(), null );
			}
			finally {
				onDeploy = false;
			}
		}
	}

	/**
	 * Reads the contents of the {@link #getRoot() root} and resets the {@link #grid() grid} to reflect that
	 * root. This method is called once during construction of this perspective, it can later be called
	 * to reset the perspective.
	 */
	public void gridPrepare(){
		gridChanges = false;
		gridClear();
		handle( delegate().getRoot().getChild(), 0, 0, 100, 100 );
	}

	private void handle( Entry entry, double x, double y, double width, double height ){
		if( entry != null ){
			if( entry.asLeaf() != null ) {
				PerspectiveDockable dockable = entry.asLeaf().getDockable();
				if( dockable != null ) {
					grid.addDockable( x, y, width, height, dockable );
				}
			}
			else{
				Node node = entry.asNode();
				double divider = node.getDivider();
				if( node.getOrientation() == Orientation.HORIZONTAL ){
					handle( node.getChildA(), x, y, width*divider, height );
					handle( node.getChildB(), x+width*divider, y, width*(1-divider), height );
				}
				else{
					handle( node.getChildA(), x, y, width, height*divider );
					handle( node.getChildB(), x, y+height*divider, width, height*(1-divider) );
				}
			}
		}
	}

	/**
	 * Allows access to the internal representation of this area as grid. Changes to the returned object will stored
	 * but not change the layout of this area directly, a call to {@link #gridDeploy()} will change the layout however.
	 * @return the internal grid
	 * @see #gridDeploy()
	 */
	public PerspectiveSplitDockGrid grid(){
		gridChanges = true;
		return grid;
	}
	
	/**
	 * Gets all the nodes of the grid. Each node is a set of {@link PerspectiveDockable}s and their location and size.
	 * @return the nodes, may be empty, is unmodifiable 
	 */
	public List<GridNode<PerspectiveDockable>> getGridNodes(){
		return grid.getGridNodes();
	}

	/**
	 * Sets whether {@link #gridDeploy()} is called automatically by this area before accessing the tree
	 * of {@link Dockable}s. The default value for this property is <code>true</code>.<br>
	 * Clients have to call {@link #gridDeploy()} if this property is <code>false</code> in order to execute commands
	 * that were collected with the <code>grid*</code> methods. 
	 * @param autoDeploy whether {@link #gridDeploy()} is called automatically
	 */
	public void setAutoDeploy( boolean autoDeploy ){
		this.autoDeploy = autoDeploy;
	}

	/**
	 * Tells whether {@link #gridDeploy()} will be called automatically before accessing the tree of {@link Dockable}s. 
	 * @return whether automatic deployment is active
	 * @see #setAutoDeploy(boolean)
	 */
	public boolean isAutoDeploy(){
		return autoDeploy;
	}

	/**
	 * Gets access to the intern tree that represents the layout of this area. Clients may alter this tree in any
	 * way they like. Please note that if {@link #isAutoDeploy() automatic deployment} is active, {@link #gridDeploy()}
	 * can be triggered by invoking this method. 
	 * @return the root of the intern tree of dockables, <code>null</code> if this area does not have any children
	 */
	public SplitDockPerspective.Root getRoot(){
		return delegate().getRoot();
	}

	/**
	 * Maximized <code>dockable</code> on this station. Please read about the side effects in {@link #maximize(PerspectiveDockable)}.
	 * @param dockable the element to maximize, not <code>null</code>
	 */
	public void maximize( CDockablePerspective dockable ){
		maximize( dockable.intern().asDockable() );
	}

	/**
	 * Maximized <code>dockable</code> on this station. Note that maximized elements will be de-maximized by a 
	 * {@link CControl} unless {@link CControl#setRevertToBasicModes(boolean)} was switched to <code>false</code>.
	 * A call to this method has several side effects that must be cared for:
	 * <ul>
	 * 	<li>If necessary and if auto-deploy is set, {@link #gridDeploy()} is called.</li>
	 *  <li>If the parent of <code>dockable</code> is not this station, then <code>dockable</code> 
	 *  is removed from the parent and added to this. The location <code>dockable</code> has on its
	 *  parent is stored and can be used during un-maximization.</li>
	 *  <li>If <code>dockable</code> has no parent, then it is added to this station. No location
	 *  information is stored. This is no problem for {@link CDockablePerspective} as they usually have a 
	 *  history of legal locations associated, but for any other dockable the missing location can lead to
	 *  strange behavior when un-maximizing.</li>
	 * </ul>
	 * @param dockable the element to maximize
	 */
	public void maximize( PerspectiveDockable dockable ){
		maybeDeploy();

		// find current location
		LocationModeManagerPerspective manager = perspective.getLocationManager();
		Location location = manager.getLocation( dockable );
		Path mode = null;
		if( location == null ) {
			ExtendedMode eMode = manager.getMode( dockable );
			if( eMode != null ) {
				mode = eMode.getModeIdentifier();
			}
		}
		else {
			mode = location.getMode();
		}

		// reparent if necessary
		if( dockable.getParent() != intern() ) {
			if( dockable.getParent() != null ) {
				dockable.getParent().remove( dockable );
			}

			Root root = getRoot();
			SplitDockPerspective.Leaf leaf = new SplitDockPerspective.Leaf( dockable, null, null, -1 );

			if( root.getChild() == null ) {
				root.setChild( leaf );
			}
			else {
				root.setChild( new SplitDockPerspective.Node( Orientation.HORIZONTAL, 0.5, leaf, root.getChild(), null, null, -1 ) );
			}
		}

		// store
		delegate.setFullscreen( dockable );

		unmaximizeLocation = location;
		unmaximizeMode = mode;
	}

	/**
	 * Gets the element that is maximized.
	 * @return the maximized child or <code>null</code>
	 */
	public PerspectiveDockable getMaximized(){
		return delegate().getFullscreen();
	}

	@Override
	public CStationPerspective asStation(){
		return this;
	}

	public String getFactoryID(){
		return delegate().getFactoryID();
	}

	public PlaceholderMap getPlaceholders(){
		return delegate().getPlaceholders();
	}

	public void setPlaceholders( PlaceholderMap placeholders ){
		delegate().setPlaceholders( placeholders );
	}

	/**
	 * The type of object that is used by a {@link CGridPerspective} as intern representation.
	 * @author Benjamin Sigg
	 */
	public class CommonSplitDockPerspective extends SplitDockPerspective implements CommonDockStationPerspective {
		public CElementPerspective getElement(){
			return CGridPerspective.this;
		}
		
		@Override
		public String getFactoryID(){
			return CommonDockStationFactory.FACTORY_ID;
		}
		
		public String getConverterID(){
			return super.getFactoryID();
		}
		
		@Override
		public void read( PerspectiveSplitDockTree tree, PerspectiveDockable fullscreen ){
			super.read( tree, fullscreen );
			if( !onDeploy ) {
				gridPrepare();
			}
		}

		@Override
		protected PerspectiveDockable combine( PerspectiveDockable[] dockables, PerspectiveDockable selection ){
			return new CStackPerspective( dockables, selection );
		}

		@Override
		public Path getPlaceholder(){
			return CPlaceholderStrategy.getSingleDockablePlaceholder( getUniqueId() );
		}

		@Override
		public Root getRoot(){
			maybeDeploy();
			return super.getRoot();
		}

		@Override
		public int getDockableCount(){
			maybeDeploy();
			return super.getDockableCount();
		}

		@Override
		public PerspectiveDockable getDockable( int index ){
			maybeDeploy();
			return super.getDockable( index );
		}
	}
}
