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

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerProperty;

/**
 * This location points to a group of toolbars.
 * @author Benjamin Sigg
 */
public class CToolbarGroupLocation extends CLocation{
	private CLocation parent;
	private int group;
	
	/**
	 * Creates a new location.
	 * @param parent the location defined by the root station, must not be <code>null</code>
	 * @param group the index of the group, at least 0
	 */
	public CToolbarGroupLocation( CLocation parent, int group ){
		if( parent == null ){
			throw new IllegalArgumentException( "parent must not be null" );
		}
		this.parent = parent;
		this.group = group;
	}
	
	/**
	 * Creates a new location pointing to one toolbar of this group of toolbars.
	 * @param column the column in which to find the toolbar, a value of <code>-1</code> is pointing to a new, 
	 * not yet existing column at the beginning of the group
	 * @param line the line in <code>column</code> where the toolbar is to be found, a value of <code>-1</code>
	 * is pointing to a new, not yet existing line at the beginning of the column
	 * @return the new location
	 */
	public CToolbarLocation toolbar( int column, int line ){
		return new CToolbarLocation( this, column, line );
	}
	
	@Override
	public CLocation getParent(){
		return parent;
	}

	@Override
	public String findRoot(){
		return parent.findRoot();
	}

	@Override
	public ExtendedMode findMode(){
		return parent.findMode();
	}

	@Override
	public DockableProperty findProperty( DockableProperty successor ){
		ToolbarContainerProperty location = new ToolbarContainerProperty( group, null );
		location.setSuccessor( successor );
		return parent.findProperty( location );
	}

	/**
	 * @deprecated see {@link CLocation#aside()} for an explanation.
	 */
	@Deprecated
	@Override
	public CLocation aside(){
		return new CToolbarGroupLocation( parent, group+1 );
	}
	
	@Override
	public String toString(){
		return String.valueOf( parent ) + " [group " + group + "]";
	}
}
