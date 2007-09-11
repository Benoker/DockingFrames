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

package bibliothek.gui.dock.event;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * Contains information about the path of a {@link Dockable}.
 * @author Benjamin Sigg
 */
public class DockHierarchyEvent {
	/** The Dockable whose path is stored */
	private Dockable dockable;
	/** The parents of {@link #dockable} */
	private DockStation[] path;
	/** The controller of {@link #dockable} at the time when this event was created */
	private DockController controller;
	
	/**
	 * Creates a new event and sets up all properties.
	 * @param dockable the Dockable whose path has changed
	 */
	public DockHierarchyEvent( Dockable dockable ){
		this( dockable, dockable.getController() );
	}
	
	/**
	 * Creates a new event and sets up all properties.
	 * @param dockable the Dockable whose path has changed
	 * @param controller the {@link DockController} of <code>dockable</code>
	 */
	public DockHierarchyEvent( Dockable dockable, DockController controller ){
		this.dockable = dockable;
		this.controller = controller;
		
		int count = 0;
		DockStation station = dockable.getDockParent();
		while( station != null ){
			count++;
			Dockable stationDockable = station.asDockable();
			if( stationDockable == null )
				station = null;
			else
				station = stationDockable.getDockParent();
		}
		
		path = new DockStation[count];
		station = dockable.getDockParent();
		for( int i = count-1; i >= 0; i-- ){
			path[i] = station;
			if( i > 0 )
				station = station.asDockable().getDockParent();
		}
	}
	
	/**
	 * Gets the {@link Dockable} whose path has been changed.
	 * @return the source of the event
	 */
	public Dockable getDockable(){
		return dockable;
	}
	
	/**
	 * Gets the new path of {@link #getDockable() the source}.
	 * @return the parents
	 */
	public DockStation[] getPath(){
		return path;
	}
	
	/**
	 * Gets the controller which was in use the moment this event was created.
	 * @return the controller
	 */
	public DockController getController(){
		return controller;
	}
}
