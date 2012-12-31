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
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupProperty;

/**
 * A location pointing to a toolbar that is part of a group of toolbars. 
 * @author Benjamin Sigg
 */
public class CToolbarLocation extends CLocation{
	private int column;
	private int line;
	private CLocation parent;
	
	/**
	 * Creates a new location.
	 * @param parent the location defining the group of toolbars
	 * @param column the column to which this location is pointing
	 * @param line the line in <code>column</code> to which this location is pointing
	 */
	public CToolbarLocation( CLocation parent, int column, int line ){
		if( parent == null ){
			throw new IllegalArgumentException( "parent must not be null" );
		}
		this.parent = parent;
		this.column = column;
		this.line = line;
	}

	/**
	 * Gets the location of an item of this toolbar.
	 * @param index the index of the item
	 * @return the new location
	 */
	public CToolbarItemLocation item( int index ){
		return new CToolbarItemLocation( this, index );
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
		ToolbarGroupProperty property = new ToolbarGroupProperty( column, line, null );
		property.setSuccessor( successor );
		return parent.findProperty( property );
	}

	/**
	 * @deprecated see {@link CLocation#aside()} for an explanation.
	 */
	@Deprecated
	@Override
	public CLocation aside(){
		return new CToolbarLocation( parent, column, line+1 );
	}
	
	@Override
	public String toString(){
		return String.valueOf( parent ) + " [column " + column + ", line " + line + "]";
	}
}
