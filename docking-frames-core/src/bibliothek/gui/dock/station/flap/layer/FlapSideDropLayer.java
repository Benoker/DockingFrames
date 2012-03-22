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
package bibliothek.gui.dock.station.flap.layer;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.FlapDockStation.Direction;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.layer.LayerPriority;

/**
 * A layer around a {@link FlapDockStation}, it has a high priority if there
 * are no children in the {@link FlapDockStation} (which makes the station very small).
 * @author Benjamin Sigg
 */
public class FlapSideDropLayer implements DockStationDropLayer{
	private FlapDockStation station;
	private LayerPriority priority = LayerPriority.OUTSIDE_HIGH;
	
	/**
	 * Creates a new layer.
	 * @param station the owner of this layer
	 */
	public FlapSideDropLayer( FlapDockStation station ){
		this.station = station;
	}
	
	public DockStation getStation(){
		return station;
	}

	public Component getComponent(){
		return station.getComponent();
	}

	public DockStationDropLayer modify( DockStationDropLayer child ){
		return child;
	}

	public boolean contains( int x, int y ){
		Point point = new Point( x, y );
		SwingUtilities.convertPointFromScreen( point, getComponent() );
		Rectangle bounds = getComponent().getBounds();
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
		
		if( station.getDirection() == Direction.NORTH || station.getDirection() == Direction.SOUTH ){
			if( deltaX > 0 ){
				return false;
			}
		}
		else{
			if( deltaY > 0 ){
				return false;
			}
		}
		
		int size = station.getBorderSideSnapSize();
		return deltaX <= size && deltaY <= size;
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
}
