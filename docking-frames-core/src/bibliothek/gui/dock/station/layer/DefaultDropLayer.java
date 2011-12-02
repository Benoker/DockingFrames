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
package bibliothek.gui.dock.station.layer;

import java.awt.Component;
import java.awt.Point;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * This {@link DockStationDropLayer} represents the base area of a {@link DockStation}, the area
 * with priority {@link LayerPriority#BASE} and whose {@link Component} is the component is the result of
 * {@link Dockable#getComponent()} (assuming the station is also a dockable). 
 * @author Benjamin Sigg
 */
public class DefaultDropLayer implements DockStationDropLayer{
	private DockStation station;
	private LayerPriority priority = LayerPriority.BASE;
	
	/**
	 * Creates a new layer.
	 * @param station the station which owns this layer
	 */
	public DefaultDropLayer( DockStation station ){
		this.station = station;
	}
	
	public boolean canCompare( DockStationDropLayer layer ){
		return false;
	}
	
	public int compare( DockStationDropLayer layer ){
		return 0;
	}
	
	public boolean contains( int x, int y ){
		Component component = getComponent();
		if( component == null ){
			return true;
		}
		Point point = new Point( x, y );
		SwingUtilities.convertPointFromScreen( point, component );
		return component.contains( point );
	}
	
	public Component getComponent(){
		Dockable dockable = station.asDockable();
		if( dockable == null ){
			return null;
		}
		return dockable.getComponent();
	}
	
	public LayerPriority getPriority(){
		return priority;
	}
	
	public void setPriority( LayerPriority priority ){
		this.priority = priority;
	}
	
	public DockStation getStation(){
		return station;
	}
	
	public DockStationDropLayer modify( DockStationDropLayer child ){
		return child;
	}
}
