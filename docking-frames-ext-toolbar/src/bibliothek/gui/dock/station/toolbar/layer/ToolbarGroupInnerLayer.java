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

package bibliothek.gui.dock.station.toolbar.layer;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.layer.LayerPriority;
import bibliothek.gui.dock.station.toolbar.layout.ToolbarGridLayoutManager;

/**
 * This layer is used to define the non rectangular surface of a
 * {@link ToolbarGroupDockStation}, it means the surface which is really
 * occupied by dockables.
 * 
 * @author Herve Guillaume
 */
public class ToolbarGroupInnerLayer implements DockStationDropLayer{
	private final ToolbarGroupDockStation station;
	private LayerPriority priority = LayerPriority.BASE;
	private Component component;
	
	/**
	 * Creates a new layer.
	 * @param station the station which owns this layer
	 * @param component the component which actually shows the dockables.
	 */
	public ToolbarGroupInnerLayer( ToolbarGroupDockStation station, Component component ){
		this.station = station;
		this.component = component;
	}

	@Override
	public boolean canCompare( DockStationDropLayer layer ){
		return false;
	}

	@Override
	public int compare( DockStationDropLayer layer ){
		return 0;
	}

	@Override
	public boolean contains( int x, int y ){
		final Point mouseCoord = new Point(x, y);
		SwingUtilities.convertPointFromScreen(mouseCoord, component);
		final ToolbarGridLayoutManager<StationChildHandle> layout = station.getLayoutManager();
		if (station.columnCount() == 0){
			// if there's no dockable inside the station, the shape of the layer
			// is computed with regards to the station component
			return component.contains(mouseCoord);
		} else {
			int count = station.columnCount();
			
			// check if the point is *inside* a child
			for( int i = 0; i < count; i++ ){
				Rectangle bound = layout.getBounds( i );
				if( bound.contains( mouseCoord )){
					return true;
				}
			}
			
			// check if the point is *between* two children
			for( int i = 0; i <= count; i++ ){
				Rectangle bound = layout.getGapBounds( i, true );
				if( bound.contains( mouseCoord )){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Component getComponent(){
		return component;
	}

	@Override
	public LayerPriority getPriority(){
		return priority;
	}

	@Override
	public void setPriority( LayerPriority priority ){
		this.priority = priority;
	}

	@Override
	public ToolbarGroupDockStation getStation(){
		return station;
	}

	@Override
	public DockStationDropLayer modify( DockStationDropLayer child ){
		return child;
	}
}
