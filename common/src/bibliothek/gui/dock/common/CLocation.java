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
package bibliothek.gui.dock.common;

import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.location.CBaseLocation;
import bibliothek.gui.dock.common.location.CExternalizedLocation;
import bibliothek.gui.dock.common.location.CMaximizedLocation;
import bibliothek.gui.dock.common.location.CWorkingAreaLocation;
import bibliothek.gui.dock.layout.DockableProperty;

/**
 * A class describing the location of a {@link CDockable}. {@link CLocation}s
 * are combined, and a whole path of <code>CLocation</code>s describes an actual
 * location. Some examples how to create a path of locations:<br>
 * <pre>
 * 	// an externalized element
 * 	CLocation location = CLocation.external( 20, 20, 400, 300 );
 * 
 * 	// a minimized element at the left side of the default panel
 * 	CLocation location = CLocation.base().minimalWest();
 *  
 * 	// an element in the lower right corner on a stack (tapped pane):
 * 	CLocation location = CLocation.base().normalSouth( 0.5 ).east( 0.5 ).stack( 2 );
 *  
 * 	// an element in the middle on a specific CContentArea
 * 	CContentArea area = ...
 * 	CLocation location = CLocation.base( area ).normalRectangle( 0.25, 0.25, 0.5, 0.5 );
 * </pre>
 * @author Benjamin Sigg
 */
public abstract class CLocation {
	/**
	 * Creates a new location that tells all children to use a station
	 * defined on <code>center</code>.
	 * @param center the base of all new locations, can be <code>null</code>
	 * @return the representation of <code>center</code>
	 */
	public static CBaseLocation base( CContentArea center ){
		return new CBaseLocation( center );
	}
	
	/**
	 * Creates a new location that tells all children to use the default
	 * center.
	 * @return the new base
	 */
	public static CBaseLocation base(){
		return new CBaseLocation();
	}
	
	/**
	 * Creates a new location representing the given {@link CWorkingArea}.
	 * @param area an area
	 * @return the representation of <code>area</code>
	 */
	public static CWorkingAreaLocation working( CWorkingArea area ){
	    return new CWorkingAreaLocation( area );
	}
	
	/**
	 * Creates a new location representing the externalized mode.
	 * @param x the x-coordinate in pixels
	 * @param y the y-coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 * @return the new location
	 */
	public static CExternalizedLocation external( int x, int y, int width, int height ){
		return new CExternalizedLocation( x, y, width, height );
	}
	
	/**
	 * Creates a location representing the maximized mode.
	 * @return the maximized mode
	 */
	public static CMaximizedLocation maximized(){
		return new CMaximizedLocation();
	}
	
	/**
	 * Gets the name of the station this location belongs to.
	 * @return the name of the station or <code>null</code> if this location
	 * does not have enough information to find the root
	 */
	public abstract String findRoot();
	
	/**
	 * Gets the mode this location represents.
	 * @return the mode or <code>null</code>
	 */
	public abstract CDockable.ExtendedMode findMode();
	
	/**
	 * Gets a path describing this location in terms of the 
	 * DockingFrames. The method is ignoring any children of this location.
	 * @return the path to this location or <code>null</code>
	 */
	public DockableProperty findProperty(){
		return findProperty( null );
	}
	
	/**
	 * Gets a path describing this location in terms of the 
	 * DockingFrames.
	 * @param successor the path of the elements above this location or <code>null</code>
	 * @return the path to this location or <code>null</code>
	 */
	public abstract DockableProperty findProperty( DockableProperty successor );
	
	/**
	 * Returns a {@link CLocation} that describes the location of an element
	 * that should be inserted next to this location.
	 * @return the new location
	 */
	public abstract CLocation aside();
}
