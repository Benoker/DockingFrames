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
import bibliothek.gui.dock.toolbar.CToolbarArea;

/**
 * A {@link CLocation} pointing to a {@link CToolbarArea}.
 * @author Benjamin Sigg
 */
public class CToolbarAreaLocation extends CLocation{
	private CToolbarArea root;
	
	/**
	 * Creates a new location.
	 * @param root the area to which this location points
	 */
	public CToolbarAreaLocation( CToolbarArea root ){
		this.root = root;
	}
	
	/**
	 * Gets a location that points to a specific group of toolbars on
	 * a {@link CToolbarArea}.
	 * @param group the index of the group, a value of <code>-1</code> points to the 
	 * not yet existing group at the beginning of the area
	 * @return the location pointing to <code>group</code>
	 */
	public CToolbarGroupLocation group( int group ){
		return new CToolbarGroupLocation( this, group );
	}
	
	@Override
	public CLocation getParent(){
		return null;
	}

	@Override
	public String findRoot(){
		return root.getUniqueId();
	}

	@Override
	public ExtendedMode findMode(){
		return CToolbarMode.TOOLBAR;
	}

	@Override
	public DockableProperty findProperty( DockableProperty successor ){
		if( successor == null ){
			return new ToolbarContainerProperty( 0, null );
		}
		else{
			return successor;
		}
	}

	/**
	 * @deprecated see {@link CLocation#aside()} for an explanation.
	 */
	@Deprecated
	@Override
	public CLocation aside(){
		return this;
	}
	
	@Override
	public String toString(){
		return "[toolbar-area " + root.getUniqueId() + "]";
	}
}
