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

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.location.CBaseLocation;
import bibliothek.gui.dock.common.location.CExternalizedLocation;
import bibliothek.gui.dock.common.location.CFlapIndexLocation;
import bibliothek.gui.dock.common.location.CGridAreaLocation;
import bibliothek.gui.dock.common.location.CLocationExpandStrategy;
import bibliothek.gui.dock.common.location.CMaximalExternalizedLocation;
import bibliothek.gui.dock.common.location.CMaximizedLocation;
import bibliothek.gui.dock.common.location.CMinimizeAreaLocation;
import bibliothek.gui.dock.common.location.CWorkingAreaLocation;
import bibliothek.gui.dock.common.location.DefaultExpandStrategy;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;

/**
 * A class describing the current location of a {@link CDockable}. {@link CLocation}s
 * are combined, and a whole path of <code>CLocation</code>s describes an actual location. 
 * <br>
 * Some warnings:
 * <ul>
 * <li>A {@link CLocation} is a very short living object: it does not have any ties to the
 * actual layout of the application. Meaning that any change in the layout may invalidate a {@link CLocation}. For
 * this reason clients are strongly encouraged not to store {@link CLocation}s in any kind of collection for later use.</li>
 * <li>There is no code available for storing {@link CLocation} persistently. This is deliberate, as {@link CLocation}s
 * are only good for a momentary snapshot of the location. The framework itself provides facilities to persistently store
 * the location of a {@link Dockable} for a long time. You may have a look at {@link CControl#writeXML(java.io.File)} and
 * {@link CControl#setMissingStrategy(MissingCDockableStrategy)}.</li>
 * <li>While {@link CLocation} and {@link DockableProperty} both store the location of {@link CDockable} or
 * a {@link Dockable}, they are not exactly the same thing. A {@link CLocation} expresses the current location of a 
 * {@link CDockable} independent from the <code>dockable</code> itself. A {@link DockableProperty} however may also
 * store information that is specific to a {@link Dockable}, namely the placeholder. This means that while every
 * {@link CLocation} can be converted to a {@link DockableProperty}, not every {@link DockableProperty} can be
 * converted to a {@link CLocation} without loosing some information.</li>
 * </ul>
 * Some examples showing how to create a path of locations:<br>
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
 * Two {@link CLocation}s are to be considered equal if {@link #findRoot()}, {@link #findMode()} and
 * {@link #findProperty()} return the same value.<br>
 *    
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
	 * Creates a new location representing a minimize area.
	 * @param area the area to which the location refers
	 * @return the new location
	 */
	public static CMinimizeAreaLocation minimized( CMinimizeArea area ){
	    return new CMinimizeAreaLocation( area );
	}

	/**
     * Creates a new location representing a minimize area.
     * @param area the area to which the location refers
     * @param index the exact location on <code>area</code>
     * @return the new location
     */
	public static CFlapIndexLocation minimized( CMinimizeArea area, int index ){
        return new CMinimizeAreaLocation( area ).insert( index );
    }
	
	/**
	 * Creates a new location representing a grid area.
	 * @param area the area that is represented
	 * @return the new location
	 */
	public static CGridAreaLocation normalized( CGridArea area ){
	    return new CGridAreaLocation( area );
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
	 * Creates a location representing the maximized mode on <code>area</code>. It is the clients
	 * responsibility to ensure that <code>area</code> actually allows maximization.
	 * @param area the area on which an element will be maximized
	 * @return the new maximization location
	 */
	public static CMaximizedLocation maximized( CGridArea area ){
		return new CMaximizedLocation( area.getUniqueId() );
	}

	/**
	 * Creates a location representing the maximized mode on <code>area</code>. It is the clients
	 * responsibility to ensure that <code>area</code> actually allows maximization.
	 * @param area the area on which an element will be maximized
	 * @return the new maximization location
	 */
	public static CMaximizedLocation maximized( CContentArea area ){
		return new CMaximizedLocation( area.getCenterIdentifier() );
	}
	
	/**
	 * Creates a location representing an element that is externalized and maximized. The coordinates
	 * are used once the {@link CDockable} is no longer maximized.
	 * @param x the x-coordinate in pixels
	 * @param y the y-coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 * @return the new location
	 */
	public static CMaximalExternalizedLocation maximized( int x, int y, int width, int height ){
		return new CMaximalExternalizedLocation( x, y, width, height );
	}
	
	/**
	 * Gets the parent location of this location. Some root-locations may never have a parent.
	 * @return the parent location, can be <code>null</code>
	 */
	public abstract CLocation getParent();
	
	/**
	 * Gets the name of the station this location belongs to. This can be the id
	 * of any {@link CStation}.
	 * @return the name of the station or <code>null</code> if this location
	 * does not have enough information to find the root
	 */
	public abstract String findRoot();

	/**
	 * Gets the mode this location represents.
	 * @return the mode or <code>null</code>
	 */
	public abstract ExtendedMode findMode();
	
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
	 * @return the path to this location or <code>null</code>, can also be <code>successor</code>
	 */
	public abstract DockableProperty findProperty( DockableProperty successor );
	
	/**
	 * Tries to create a location that resembles <code>property</code>.
	 * @param controller the controller in whose realm this method is called, may be used to load 
	 * extensions
	 * @param property some location
	 * @return a location whose {@link #findProperty()} would create 
	 * <code>property</code> again, or <code>null</code> in case that <code>property</code>
	 * can't be used
	 */
	public final CLocation expandProperty( DockController controller, DockableProperty property ){
		return expandProperty( property, new DefaultExpandStrategy( controller ) );
	}
	
	/**
	 * Tries to create a location that resembles <code>property</code>.
	 * @param property some location
	 * @param strategy a strategy helping to convert the properties
	 * @return a location whose {@link #findProperty()} would create 
	 * <code>property</code> again, or <code>null</code> in case that <code>property</code>
	 * can't be used
	 */
	public CLocation expandProperty( DockableProperty property, CLocationExpandStrategy strategy ){
		CLocation location = strategy.expand( this, property );
		if( location == null ){
			return null;
		}
		property = property.getSuccessor();
		if( property == null ){
			return location;
		}
		return location.expandProperty( property, strategy );
	}
	
	/**
	 * Returns a {@link CLocation} that describes the location of an element
	 * that should be inserted next to this location.
	 * @return the new location
	 * @deprecated Clients should make use of {@link CDockable#setLocationsAside(CDockable)} and
	 * {@link CDockable#setLocationsAsideFocused()}, because these methods can directly modify {@link DockStation}s
	 * and insert placeholders when necessary. 
	 */
	@Deprecated
	public abstract CLocation aside();
	
	@Override
	public boolean equals( Object obj ){
		if( obj instanceof CLocation ){
			CLocation that = (CLocation)obj;
			return equals( findRoot(), that.findRoot() ) &&
				equals( findMode(), that.findMode() ) &&
				equals( findProperty(), that.findProperty() );
		}
		return false;
	}
	
	private boolean equals( Object a, Object b ){
		if( a == b ){
			return true;
		}
		if( a == null && b != null ){
			return false;
		}
		return a != null && a.equals( b );
	}

	@Override
	public int hashCode(){
		Object root = findRoot();
		Object mode = findMode();
		Object property = findProperty();
		
		int result = 0;
		if( root != null ){
			result = root.hashCode();
		}
		result *= 31;
		if( mode != null ){
			result += mode.hashCode();
		}
		result *= 31;
		if( property != null ){
			result += property.hashCode();
		}
		return result;
	}
}
