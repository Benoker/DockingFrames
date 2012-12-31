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
package bibliothek.gui.dock.common.location;

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;

/**
 * This location represents an {@link ExtendedMode}. Using this location on a 
 * {@link CDockable} is equivalent of calling {@link CDockable#setExtendedMode(bibliothek.gui.dock.common.mode.ExtendedMode)}.
 * @author Benjamin Sigg
 */
public class CExtendedModeLocation extends CLocation{
	private ExtendedMode mode;
	
	/**
	 * Creates a new location. 
	 * @param mode the mode this location represents, not <code>null</code>
	 */
	public CExtendedModeLocation( ExtendedMode mode ){
		if( mode == null ){
			throw new IllegalArgumentException( "mode must not be null" );
		}
		this.mode = mode;
	}
	
	@Override
	public CLocation getParent(){
		return null;
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
	public CLocation expandProperty( DockableProperty property, CLocationExpandStrategy strategy ){
		return null;
	}

	public ExtendedMode findMode(){
		return mode;
	}

	public DockableProperty findProperty( DockableProperty successor ){
		return successor;
	}

	public String findRoot(){
		return null;
	}
}
