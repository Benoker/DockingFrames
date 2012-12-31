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
import bibliothek.gui.dock.station.toolbar.ToolbarProperty;

/**
 * A location pointing to one item of a toolbar.
 * @author Benjamin Sigg
 */
public class CToolbarItemLocation extends CLocation{
	private CLocation parent;
	private int index;
	
	/**
	 * Creates a new location.
	 * @param parent a pointer to the toolbar
	 * @param index the index of the item to which this location points
	 */
	public CToolbarItemLocation( CLocation parent, int index ){
		if( parent == null ){
			throw new IllegalArgumentException( "parent must not be null" );
		}
		this.parent = parent;
		this.index = index;
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
		ToolbarProperty property = new ToolbarProperty( index, null );
		property.setSuccessor( successor );
		return parent.findProperty( property );
	}

	/**
	 * @deprecated see {@link CLocation#aside()} for an explanation.
	 */
	@Deprecated
	@Override
	public CLocation aside(){
		return new CToolbarItemLocation( parent, index+1 );
	}
	
	@Override
	public String toString(){
		return String.valueOf( parent ) + " [index " + index + "]";
	}
}
