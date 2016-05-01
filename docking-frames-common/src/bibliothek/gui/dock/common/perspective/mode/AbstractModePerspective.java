/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.common.perspective.mode;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.dock.common.perspective.CDockablePerspective;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.support.mode.ModeSetting;

/**
 * This abstract implementation of a {@link LocationModeManagerPerspective} just uses
 * a collection of {@link LocationModePerspective}s to find out whether the mode of
 * a {@link CDockablePerspective} matches this mode or not.
 * @param <A> the kind of {@link CModeAreaPerspective} that can be registered
 * @author Benjamin Sigg
 */
public abstract class AbstractModePerspective<A extends CModeAreaPerspective> implements LocationModePerspective{
	/** all the stations that contribute to this perspective */
	private List<A> locations = new ArrayList<A>();

	/** the perspective that uses this mode */
	private CPerspective perspective;
	
	public void setPerspective( CPerspective perspective ){
		this.perspective = perspective;	
	}
	
	/**
	 * Gets the perspective that uses this mode.
	 * @return the perspective
	 */
	public CPerspective getPerspective(){
		return perspective;
	}
	
	/**
	 * Adds <code>location</code> to the list of possible parents.
	 * @param location the new location, not <code>null</code>
	 */
	public void add( A location ){
		if( location == null ){
			throw new IllegalArgumentException( "location is null" );
		}
		locations.add( location );
	}
	
	/**
	 * Removes <code>location</code> from this perspective.
	 * @param location the station to remove
	 */
	public void remove( A location ){
		locations.remove( location );
	}
	
	/**
	 * Gets the number of {@link CModeAreaPerspective}s stored in this object.
	 * @return the number of areas
	 */
	public int getAreaCount(){
		return locations.size();
	}
	
	/**
	 * Gets the <code>index</code>'th area of this {@link AbstractModePerspective}
	 * @param index the index of some area
	 * @return the area
	 */
	public A getArea( int index ){
		return locations.get( index );
	}
	
	public boolean isCurrentMode( PerspectiveDockable dockable ){
		for( CModeAreaPerspective location : locations ){
			if( location.isChild( dockable )){
				return true;
			}
		}
		return false;
	}
	
	public boolean isCurrentMode( String root, DockableProperty location ){
		for( CModeAreaPerspective area : locations ){
			if( area.getUniqueId().equals( root )){
				if( area.isChildLocation( location )){
					return true;
				}
			}
		}
		return false;
	}
	
	public void readSetting( ModeSetting<Location> setting ){
		// ignore
	}
	
	public void writeSetting( ModeSetting<Location> setting ){
		// ignore
	}
}
