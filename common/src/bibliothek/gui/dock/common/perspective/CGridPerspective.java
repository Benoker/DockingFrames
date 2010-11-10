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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CGridArea;
import bibliothek.gui.dock.common.intern.CPlaceholderStrategy;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.station.split.PerspectiveSplitDockGrid;
import bibliothek.gui.dock.station.split.SplitDockPerspective;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;
import bibliothek.util.Todo;

/**
 * A representation of a {@link CGridArea}.
 * @author Benjamin Sigg
 */
@Todo(description="nested dockable/stations will not work due to ClassCastException")
public class CGridPerspective extends SingleCDockablePerspective implements CStationPerspective{
	/** the intern representation of this perspective */
	private CommonSplitDockPerspective delegate;
	
	/** helper class to build up this perspective */
	private PerspectiveSplitDockGrid grid;
	
	/** whether {@link #gridDeploy()} is called automatically */
	private boolean autoDeploy = true;
	
	/** whether there are changes on {@link #grid} */
	private boolean gridChanges = false;
	
	/**
	 * Creates a new, empty perspective.
	 * @param id the unique identifier of this perspective
	 */
	public CGridPerspective( String id ){
		super( id );
		delegate = new CommonSplitDockPerspective();
		gridClear();
	}
	
	@Override
	protected CommonSplitDockPerspective create(){
		return delegate;
	}
	
	@Override
	public CommonSplitDockPerspective intern(){
		return (CommonSplitDockPerspective)super.intern();
	}
	
	/**
	 * Calls {@link #gridDeploy()}, but only if {@link #isAutoDeploy()} returns <code>true</code> and
	 * if {@link #grid() the grid} was accessed.
	 */
	protected void maybeDeploy(){
		if( isAutoDeploy() && gridChanges ){
			gridDeploy();
		}
	}
	
	private PerspectiveDockable[] convert( CDockablePerspective[] dockables ){
		PerspectiveDockable[] result = new PerspectiveDockable[ dockables.length ];
		for( int i = 0; i < result.length; i++ ){
			result[i] = dockables[i].intern().asDockable();
		}
		return result;
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
		grid.addDockable( x, y, width, height, convert( dockables ));
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
	 * @see #gridAdd(double, double, double, double, PerspectiveDockable...)
	 * @see #gridSelect(double, double, double, double, PerspectiveDockable)
	 * @see #gridHorizontal(double, double, double)
	 * @see #gridVertical(double, double, double)
	 * @see #gridClear()  
	 */
	public void gridDeploy(){
		if( gridChanges ){
			gridChanges = false;
			delegate.read( grid.toTree(), null );
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
	public SplitDockPerspective.Entry getRoot(){
		return delegate.getRoot();
	}

	@Override
	public CStationPerspective asStation(){
		return this;
	}
	
	public String getFactoryID(){
		return delegate.getFactoryID();
	}
	
	public PlaceholderMap getPlaceholders(){
		return delegate.getPlaceholders();
	}
	
	public void setPlaceholders( PlaceholderMap placeholders ){
		delegate.setPlaceholders( placeholders );	
	}
	
	/**
	 * The type of object that is used by a {@link CGridPerspective} as intern representation.
	 * @author Benjamin Sigg
	 */
	public class CommonSplitDockPerspective extends SplitDockPerspective implements CommonElementPerspective{
		public CElementPerspective getElement(){
			return CGridPerspective.this;
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
