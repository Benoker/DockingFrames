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
package bibliothek.gui.dock.station.split.layer;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.layer.LayerPriority;

/**
 * Describes the area around a {@link SplitDockStation} where the user can drop a {@link Dockable} such
 * that it appears at one side of the station
 * @author Benjamin Sigg
 */
public class SideSnapDropLayer implements DockStationDropLayer{
	private SplitDockStation station;
	private LayerPriority priority = LayerPriority.OUTSIDE_LOW;
	
	/**
	 * Creates a new layer
	 * @param station the owner of this level
	 */
	public SideSnapDropLayer( SplitDockStation station ){
		this.station = station;
	}
	
	public LayerPriority getPriority(){
		return priority;
	}
	
	public void setPriority( LayerPriority priority ){
		this.priority = priority;
	}
	
	public boolean canCompare( DockStationDropLayer level ){
		return false;
	}
	
	public int compare( DockStationDropLayer level ){
		return 0;
	}
	
	public Component getComponent(){
		return station.getComponent();
	}
	
	public DockStation getStation(){
		return station;
	}
	
	public DockStationDropLayer modify( DockStationDropLayer child ){
		return child;
	}
	
	public boolean contains( int x, int y ){
		if( !station.isAllowSideSnap() ){
			return false;
		}
		Point point = new Point( x, y );
		SwingUtilities.convertPointFromScreen( point, getComponent() );
		Rectangle bounds = getComponent().getBounds();
		bounds.x = 0;
		bounds.y = 0;
		if( bounds.contains( point )){
			return false;
		}
		
		x = point.x;
		y = point.y;
		
		int deltaX = Math.min( Math.abs( x ), Math.abs( x-bounds.width ));
		if( x > 0 && x < bounds.width ){
			deltaX = 0;
		}
		
		int deltaY = Math.min( Math.abs( y ), Math.abs( y-bounds.height ));
		if( y > 0 && y < bounds.height ){
			deltaY = 0;
		}
		
		int size = station.getBorderSideSnapSize();
		return deltaX <= size && deltaY <= size;
	}
}
