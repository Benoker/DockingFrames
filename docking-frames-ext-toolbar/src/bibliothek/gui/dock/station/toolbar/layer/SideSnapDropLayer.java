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

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.layer.LayerPriority;

/**
 * Describes the area around a {@link ToolbarDockStation} where the user can
 * drop a {@link Dockable} into.
 * 
 * @author Herve Guillaume
 */
public class SideSnapDropLayer implements DockStationDropLayer{
	private final ToolbarGroupDockStation station;
	private LayerPriority priority = LayerPriority.OUTSIDE_LOW;

	/**
	 * Creates a new layer
	 * 
	 * @param station
	 *            the owner of this level
	 */
	public SideSnapDropLayer( ToolbarGroupDockStation station ){
		this.station = station;
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
	public boolean canCompare( DockStationDropLayer level ){
		return false;
	}

	@Override
	public int compare( DockStationDropLayer level ){
		return 0;
	}

	@Override
	public Component getComponent(){
		return station.getComponent();
	}

	@Override
	public DockStation getStation(){
		return station;
	}

	@Override
	public DockStationDropLayer modify( DockStationDropLayer child ){
		return child;
	}

	@Override
	public boolean contains( int x, int y ){
		if (!station.isAllowSideSnap()){
			return false;
		}
		final Point point = new Point(x, y);
		SwingUtilities.convertPointFromScreen(point, getComponent());
		final Rectangle bounds = getComponent().getBounds();
		if (bounds.contains(point)){
			// if the mouse in inside component, so it is not inside the snap
			// extended zone
			return false;
		}
		final int size = station.getBorderSideSnapSize();
		final Rectangle extendedBounds = new Rectangle();
		extendedBounds.setBounds(bounds.x - size, bounds.y - size, bounds.width
				+ (size * 2), bounds.height + (size * 2));

		// DEBUG:
		// int deltaX = Math.min(Math.abs(point.x),
		// Math.abs(point.x - bounds.width));
		// int deltaY = Math.min(Math.abs(point.y),
		// Math.abs(point.y - bounds.height));
		// System.out.println("Mouse : " + point.x + " / deltaX :" + deltaX
		// + " / deltaY :" + deltaY);
		// if (extendedBounds.contains(point)){
		// System.out.println("TRUETRUETRUETRUE");
		// } else{
		// System.out.println("FALSEFALSEFALSE");
		// }

		return extendedBounds.contains(point);

	}
}
