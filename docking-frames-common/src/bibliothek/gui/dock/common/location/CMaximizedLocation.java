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
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.split.SplitDockFullScreenProperty;

/**
 * A location representing the maximized state. If no root-station is set,
 * then the location of a maximized element depends on its location before
 * maximization.
 * @author Benjamin Sigg
 */
public class CMaximizedLocation extends AbstractStackholdingLocation {
	private String root;
	
	/**
	 * Creates a new location
	 */
	public CMaximizedLocation(){
		// ignore
	}
	
	/**
	 * Creates a new location.
	 * @param root the station which represents the maximize area, can be <code>null</code>
	 */
	public CMaximizedLocation( String root ){
		this.root = root;
	}
	
	@Override
	public CLocation getParent(){
		return null;
	}
	
	@Override
	public ExtendedMode findMode(){
		return ExtendedMode.MAXIMIZED;
	}

	@Override
	public DockableProperty findProperty( DockableProperty successor ){
		SplitDockFullScreenProperty property = new SplitDockFullScreenProperty();
		property.setSuccessor( successor );
		
		CLocation parent = getParent();
		if( parent != null ){
			return parent.findProperty( property );
		}
		
		return property;
	}
	
	@Override
	public String findRoot(){
		return root;
	}
	
	@Override
    public String toString() {
        return "[maximized]";
    }

	/**
	 * @deprecated see {@link CLocation#aside()} for an explanation.
	 */
	@Deprecated
	@Override
	public CLocation aside(){
		return stack( 1 );
	}
}
