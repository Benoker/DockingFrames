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

package bibliothek.gui.dock.toolbar.perspective;

import bibliothek.gui.dock.common.perspective.CDockablePerspective;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.gui.dock.common.perspective.CStationPerspective;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.station.toolbar.ToolbarGroupDockPerspective;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.toolbar.CToolbarArea;
import bibliothek.util.Path;

/**
 * Represents a {@link CToolbarArea} as perspective.
 * @author Benjamin Sigg
 */
public class CToolbarAreaPerspective implements CStationPerspective{
	private boolean root = true;
	private String id;
	private CommonToolbarContainerDockPerspective delegate;
	private CPerspective perspective;
	
	/**
	 * Creates a new perspective.
	 * @param id the unique identifier of this station
	 */
	public CToolbarAreaPerspective( String id ){
		this.id = id;
		delegate = new CommonToolbarContainerDockPerspective( this );
	}
	
	/**
	 * Gets the number of {@link CToolbarGroupPerspective groups} this station currently has. This method
	 * assumes that the client did not modify the {@link ToolbarStrategy}.
	 * @return the number of groups
	 */
	public int getGroupCount(){
		return delegate.getDockableCount();
	}
	
	/**
	 * Gets or creates a group of toolbars at location <code>index</code>.
	 * @param index the index of an existing group, <code>-1</code> or {@link #getGroupCount()}
	 * @return the group at <code>index</code> or <code>null</code> if the child at <code>index</code> has
	 * the wrong type. A result of <code>null</code> can only happen if the client modified the {@link ToolbarStrategy}.
	 */
	public CToolbarGroupPerspective group( int index ){
		if( index < 0 ){
			return insert( 0 );
		}
		else if( index >= getGroupCount() ){
			return insert( getGroupCount() );
		}
		
		PerspectiveElement child = delegate.getDockable( index );
		if( child instanceof ToolbarGroupDockPerspective ){
			return new CToolbarGroupPerspective( (ToolbarGroupDockPerspective)child );
		}
		else{
			return null;
		}
	}
	
	/**
	 * Creates a new {@link CToolbarGroupPerspective} and inserts the new group at <code>index</code>.
	 * @param index the location of the new group
	 * @return the new group
	 */
	public CToolbarGroupPerspective insert( int index ){
		ToolbarGroupDockPerspective group = new ToolbarGroupDockPerspective();
		delegate.add( index, group );
		return new CToolbarGroupPerspective( group );
	}
	
	/**
	 * Removes the group at location <code>index</code> from this station.
	 * @param index the index of the group to remove
	 */
	public void remove( int index ){
		delegate.remove( index );
	}
	
	/**
	 * Removes <code>group</code> from this station.
	 * @param group the group to remove, not <code>null</code>
	 */
	public void remove( CToolbarGroupPerspective group ){
		delegate.remove( group.getDelegate() );
	}
	
	@Override
	public CommonToolbarContainerDockPerspective intern(){
		return delegate;
	}

	@Override
	public CDockablePerspective asDockable(){
		return null;
	}

	@Override
	public CStationPerspective asStation(){
		return this;
	}

	@Override
	public String getUniqueId(){
		return id;
	}

	@Override
	public Path getTypeId(){
		return CToolbarArea.TYPE_ID;
	}

	@Override
	public void setPerspective( CPerspective perspective ){
		this.perspective = perspective;
	}
	
	@Override
	public CPerspective getPerspective(){
		return perspective;
	}

	@Override
	public boolean isWorkingArea(){
		return false;
	}

	@Override
	public boolean isRoot(){
		return root;
	}

	@Override
	public void setRoot( boolean root ){
		this.root = root;
	}
}
