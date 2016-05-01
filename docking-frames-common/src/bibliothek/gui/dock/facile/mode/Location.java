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
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.util.Path;

/**
 * Describes the location of a {@link Dockable} on some station.
 * @author Benjamin Sigg
 */
public class Location {
	/** the mode which is responsible for handling this location */
	private Path mode;
	/** the unique identifier of the root station */
	private String root;
	/** the location of the element */
	private DockableProperty location;
	/** whether this location was defined by the client application. */
	private boolean applicationDefined;
	
	/**
	 * Creates a new location.
	 * @param mode the mode which is responsible for handling this location
	 * @param root the identifier of the parent station, must not be <code>null</code>
	 * @param location the location on the station, may be <code>null</code>
	 */
	public Location( Path mode, String root, DockableProperty location ){
		this( mode, root, location, false );
	}
	
	/**
	 * Creates a new location.
	 * @param mode the mode which is responsible for handling this location
	 * @param root the identifier of the parent station, must not be <code>null</code>
	 * @param location the location on the station, may be <code>null</code>
	 * @param applicationDefined whether this location was defined by the client application
	 */
	public Location( Path mode, String root, DockableProperty location, boolean applicationDefined ){
		if( mode == null )
			throw new IllegalArgumentException( "mode must not be null" );
		
		if( root == null )
			throw new IllegalArgumentException( "root must not be null" );
		
		this.mode = mode;
		this.root = root;
		this.location = location;
		this.applicationDefined = applicationDefined;
	}

	/**
	 * Gets the mode which is responsible for this location.
	 * @return the mode
	 */
	public Path getMode(){
		return mode;
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
	
	/**
	 * Tells whether this location was defined by the client application. A location is defined by the client
	 * application if for example the method {@link CDockable#setLocation(bibliothek.gui.dock.common.CLocation)} is
	 * used to set the location of a {@link CDockable}. If possible application defined locations should
	 * not be automatically rewritten.<br>
	 * The state "application defined" is lost as soon as the owning {@link Dockable} has been put at this location.
	 * @return whether this location has been defined by a client application
	 */
	public boolean isApplicationDefined() {
		return applicationDefined;
	}
	
	/**
	 * Sets the property {@link #isApplicationDefined()} to <code>false</code>.
	 */
	public void resetApplicationDefined() {
		applicationDefined = false;
	}
	
	@Override
	public String toString(){
		return getClass().getName() + "[mode=" + String.valueOf( mode ) + ", root=" + root + ", location=" + location + ", applicationDefined=" + applicationDefined + "]";
	}
}
