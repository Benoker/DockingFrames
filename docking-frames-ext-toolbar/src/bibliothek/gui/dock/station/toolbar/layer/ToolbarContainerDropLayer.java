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

import javax.swing.SwingUtilities;

import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.station.layer.DefaultDropLayer;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.layer.LayerPriority;

/**
 * A {@link DockStationDropLayer} describing the region around a {@link ToolbarContainerDockStation}.
 * @author Benjamin Sigg
 */
public class ToolbarContainerDropLayer extends DefaultDropLayer {
	private ToolbarContainerDockStation station;
	
	public ToolbarContainerDropLayer( ToolbarContainerDockStation station ){
		super( station );
		this.station = station;
		setPriority( LayerPriority.OUTSIDE_LOW );
	}
	
	@Override
	public Component getComponent(){
		return station.getComponent();
	}
	
	@Override
	public boolean contains( int x, int y ){
		Component component = getComponent();
		Point point = new Point( x, y );
		SwingUtilities.convertPointFromScreen( point, component );
		int side = station.getSideSnapSize();
		
		if( station.getOrientation() == Orientation.HORIZONTAL ){
			return point.y >= -side && point.y <= component.getHeight() + side && point.x >= -side && point.x <= component.getWidth() + side;
		}
		else{
			return point.x >= -side && point.x <= component.getWidth() + side && point.y >= -side && point.y <= component.getHeight() + side;
		}
	}
}
