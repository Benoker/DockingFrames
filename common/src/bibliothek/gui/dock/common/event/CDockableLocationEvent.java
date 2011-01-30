/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.common.event;

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.intern.CDockable;

/**
 * This event object is given to a {@link CDockableLocationListener} if the location or the visibility to the
 * user of a {@link CDockable} changed.<br>
 * Please note that any location can be <code>null</code>.
 * @author Benjamin Sigg
 */
public class CDockableLocationEvent {
	private CDockable dockable;
	
	private boolean oldVisible;
	private boolean newVisible;
	
	private boolean locationChanged;
	private CLocation oldLocation;
	private CLocation newLocation;
	
	/**
	 * Creates a new event.
	 * @param dockable the source of the event
	 * @param oldVisible the old visibility state
	 * @param newVisible the new visibility state
	 * @param oldLocation the old location, may be <code>null</code>
	 * @param newLocation the new location, may be <code>null</code>
	 */
	public CDockableLocationEvent( CDockable dockable, boolean oldVisible, boolean newVisible, CLocation oldLocation, CLocation newLocation ){
		this.dockable = dockable;
		this.oldVisible = oldVisible;
		this.newVisible = newVisible;
		this.oldLocation = oldLocation;
		this.newLocation = newLocation;
		
		if( oldLocation == null && newLocation != null ){
			locationChanged = true;
		}
		else if( oldLocation != null && !oldLocation.equals( newLocation )){
			locationChanged = true;
		}
	}

	/**
	 * Gets the dockable whose state changed.
	 * @return the element whose state changed
	 */
	public CDockable getDockable(){
		return dockable;
	}
	
	/**
	 * Tells whether the location of the dockable changed.
	 * @return whether the location changed
	 */
	public boolean isLocationChanged(){
		return locationChanged;
	}
	
	/**
	 * Gets the location of the dockable before this event
	 * @return the old location, can be <code>null</code>
	 */
	public CLocation getOldLocation(){
		return oldLocation;
	}
	
	/**
	 * Gets the location of the dockable after this event
	 * @return the new location, can be <code>null</code>
	 */
	public CLocation getNewLocation(){
		return newLocation;
	}
	
	/**
	 * Tells whether the visibility state of the dockable changed
	 * @return whether the visibility state changed
	 */
	public boolean isVisibleChanged(){
		return oldVisible != newVisible;
	}
	
	/**
	 * Tells whether the user could see the dockable before this event
	 * @return the old visibility state
	 */
	public boolean getOldVisible(){
		return oldVisible;
	}
	
	/**
	 * Tells whether the user can see the dockable after this event
	 * @return the new visibility state
	 */
	public boolean getNewVisible(){
		return newVisible;
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append( getClass().getSimpleName() ).append( "[" );
		if( isVisibleChanged() ){
			builder.append( "VISIBILITY: " ).append( oldVisible ).append( " -> " ).append( newVisible );
		}
		if( isLocationChanged() ){
			if( isVisibleChanged() ){
				builder.append( ", " );
			}
			builder.append( "LOCATION: " ).append( oldLocation ).append( " -> " ).append( newLocation );
		}
		builder.append( "]" );
		return builder.toString();
	}
}
