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
package bibliothek.gui.dock.common.perspective;

import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;

/**
 * An abstract implementation of {@link CDockablePerspective} providing some general methods. 
 * @author Benjamin Sigg
 * 
 */
public abstract class AbstractCDockablePerspective implements CDockablePerspective{
	private CStationPerspective workingArea;
	
	private LocationHistory history = new LocationHistory();
	
	public CStationPerspective getParent(){
		PerspectiveDockable dockable = intern().asDockable();
		
		while( dockable != null ){
			PerspectiveStation parent = dockable.getParent();
			if( parent == null ){
				return null;
			}
			if( parent instanceof CommonElementPerspective ){
				CElementPerspective cparent = ((CommonElementPerspective)parent).getElement();
				CStationPerspective station = cparent.asStation();
				if( station != null ){
					return station;
				}
			}
			dockable = parent.asDockable();
		}
		
		return null;
	}
	
	public void setWorkingArea( CStationPerspective workingArea ){
		this.workingArea = workingArea;
	}
	
	public CStationPerspective getWorkingArea(){
		return workingArea;
	}
	
	public LocationHistory getLocationHistory(){
		return history;
	}

	/**
	 * Removes this dockable from its parent (if there is a parent). This method
	 * tries to call {@link CPerspective#storeLocation(CDockablePerspective)} which will
	 * allow the dockable to store its last location, leave a placeholder, and make the
	 * perspective remember that there is an invisible dockable.  
	 */
	public void remove(){
		CStationPerspective parentStation = getParent();
		if( parentStation != null ){
			CPerspective perspective = parentStation.getPerspective();
			if( perspective != null ){
				perspective.storeLocation( this );
			}
		}
		
		PerspectiveStation parent = intern().asDockable().getParent();
		if( parent != null ){
			parent.remove( intern().asDockable() );
		}
	}
}
