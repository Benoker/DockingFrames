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

import java.awt.Point;

import javax.swing.SwingUtilities;

import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.layer.DefaultDropLayer;
import bibliothek.gui.dock.station.layer.LayerPriority;
import bibliothek.gui.dock.station.split.Root;

/**
 * Represents the {@link LayerPriority#OVERRIDE_GUESS override} area of
 * a {@link SplitDockStation} as defined by {@link Root#isInOverrideZone(int, int)}. 
 * @author Benjamin Sigg
 */
public class SplitOverrideDropLayer extends DefaultDropLayer{
	private SplitDockStation station;
	
	/**
	 * Creates a new layer.
	 * @param station the station which owns this level
	 */
	public SplitOverrideDropLayer( SplitDockStation station ){
		super( station );
		this.station = station;
		setPriority( LayerPriority.OVERRIDE_GUESS );
	}
	
	@Override
	public boolean contains( int x, int y ){
		if( station.isFullScreen() ){
			return false;
		}
		
		if( super.contains( x, y )){
			Point point = new Point( x, y );
			SwingUtilities.convertPointFromScreen( point, getComponent() );
			return station.getRoot().isInOverrideZone( point.x, point.y );
		}
		return false;
	}
}
