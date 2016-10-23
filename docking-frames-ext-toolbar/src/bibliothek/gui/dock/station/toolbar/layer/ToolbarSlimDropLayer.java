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

import java.awt.Point;

import javax.swing.SwingUtilities;

import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.layer.DefaultDropLayer;
import bibliothek.gui.dock.station.layer.LayerPriority;

/**
 * This layer slim the drop area of {@link ToolbarGroupDockStation}, as it take
 * into account means an area where no dockable can be dropped into the station.
 * 
 * @author Herve Guillaume
 */
public class ToolbarSlimDropLayer extends DefaultDropLayer{
	private final ToolbarDockStation station;

	/**
	 * Creates a new layer.
	 * 
	 * @param station
	 *            the station which owns this level
	 */
	public ToolbarSlimDropLayer( ToolbarDockStation station ){
		super(station);
		this.station = station;
		setPriority(LayerPriority.BASE);
	}

	@Override
	public boolean contains( int x, int y ){
//		System.out.print("Toolbar Slim: ");
		if (super.contains(x, y)){
			// The goal it to reduce the default layer so, only if the default
			// layer (parent of this layer) contains this coordinates we have to
			// check if this layer contains the same coordinate.
			final Point mouseCoord = new Point(x, y);
			SwingUtilities.convertPointFromScreen(mouseCoord, getComponent());
			final int size = station.getLateralNodropZoneSize();
			if (station.getOrientation() == Orientation.VERTICAL){
				if ((mouseCoord.x > size) && (mouseCoord.x < (getComponent().getWidth() - size -1))){
//					System.out.println("true");
					return true;
				} else{
//					System.out.println("false");
					return false;
				}
			} else{
				if ((mouseCoord.y > size) && (mouseCoord.y < getComponent().getHeight() - size - 1)){
//					System.out.println("true");
					return true;
				} else{
//					System.out.println("false");
					return false;
				}
			}
		} else{
//			System.out.println("false");
			return false;
		}
	}

}
