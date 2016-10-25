/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.station;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;

/**
 * The {@link OrientationObserver} adds itself as {@link OrientingDockStationListener} to the first
 * {@link OrientingDockStation} that is found as parent of a specific {@link Dockable}.
 * @author Benjamin Sigg
 */
public abstract class OrientationObserver implements OrientingDockStationListener{
	private Dockable dockable;
	
	/** the station that is currently monitored */
	private OrientingDockStation station;
	
	private DockHierarchyListener listener = new DockHierarchyListener(){
		@Override
		public void hierarchyChanged( DockHierarchyEvent event ){
			updateHierarchy();
		}
		
		@Override
		public void controllerChanged( DockHierarchyEvent event ){
			// ignore	
		}
	};
	
	/**
	 * Creates a new observer
	 * @param dockable the element whose parent {@link OrientingDockStation} should be observed
	 */
	public OrientationObserver( Dockable dockable ){
		this.dockable = dockable;
		dockable.addDockHierarchyListener( listener );
		updateHierarchy();
	}
	
	/**
	 * Destroys this observer, all resources are prepared for garbage collection.
	 */
	public void destroy(){
		dockable.removeDockHierarchyListener( listener );
	}
	
	private void updateHierarchy(){
		Dockable item = dockable;
		DockStation parent = item.getDockParent();
		
		OrientingDockStation newParent = null;
		
		while( parent != null ){
			if( parent instanceof OrientingDockStation ){
				newParent = (OrientingDockStation)parent;
				break;
			}
			item = parent.asDockable();
			if( item == null ){
				parent = null;
			}
			else{
				parent = item.getDockParent();
			}
		}
		
		if( newParent != station ){
			if( station != null ){
				station.removeOrientingDockStationListener( this );
			}
			if( newParent != null ){
				newParent.addOrientingDockStationListener( this );
			}
			orientationChanged( getOrientation() );
		}
	}
	
	/**
	 * Gets the {@link Dockable} which is monitored by this {@link OrientationObserver}.
	 * @return the element, not <code>null</code>
	 */
	public Dockable getDockable(){
		return dockable;
	}
	
	/**
	 * Called if the orientation of {@link #getDockable() the dockable} changed. This method may be called several
	 * times with the same argument.
	 * @param current the new orientation, can <code>null</code> if the dockable does not have
	 * an {@link OrientingDockStation} as parent
	 */
	protected abstract void orientationChanged( Orientation current );
	
	/**
	 * Gets the {@link Orientation} that should currently be applied to {@link #getDockable() the dockable},
	 * @return the current orientation or <code>null</code> if the dockable does not have an {@link OrientingDockStation}
	 * as parent.
	 */
	public Orientation getOrientation(){
		Dockable item = dockable;
		DockStation parent = item.getDockParent();
		
		while( parent != null ){
			if( parent instanceof OrientingDockStation ){
				return ((OrientingDockStation)parent).getOrientationOf( item );
			}
			item = parent.asDockable();
			if( item == null ){
				parent = null;
			}
			else{
				parent = item.getDockParent();
			}
		}
		
		return null;
	}
	
	@Override
	public void changed( OrientingDockStationEvent event ){
		if( event.isAffected( dockable )){
			orientationChanged( getOrientation() );
		}
	}

}
