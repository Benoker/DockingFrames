/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.facile.mode;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.layout.DockableProperty;

/**
 * Describes the location of a {@link Dockable} on some station.
 * @author Benjamin Sigg
 */
public class Location {
	/** the unique identifier of the root station */
	private String root;
	/** the location of the element */
	private DockableProperty location;
	
	/**
	 * Creates a new location.
	 * @param root the identifier of the parent station, must not be <code>null</code>
	 * @param location the location on the station, may be <code>null</code>
	 */
	public Location( String root, DockableProperty location ){
		if( root == null )
			throw new IllegalArgumentException( "root must not be null" );
		
		this.root = root;
		this.location = location;
	}

	/**
	 * Gets the unique identifier of the parent station.  
	 * @return the identifier, not <code>null</code>
	 */
	public String getRoot(){
		return root;
	}

	/**
	 * Gets the location on the parent station.
	 * @return the location, may be <code>null</code>
	 */
	public DockableProperty getLocation(){
		return location;
	}
}
