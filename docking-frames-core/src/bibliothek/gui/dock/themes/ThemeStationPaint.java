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
package bibliothek.gui.dock.themes;

import java.awt.Graphics;
import java.awt.Rectangle;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.station.StationPaint;

/**
 * A {@link StationPaint} which forwards its calls to the current {@link DockTheme}.
 * @author Benjamin Sigg
 */
public class ThemeStationPaint implements StationPaint{
	private DockController controller;
	
	/**
	 * Creates a new paint.
	 * @param controller the controller whose theme should be accessed
	 */
	public ThemeStationPaint( DockController controller ){
		this.controller = controller;
	}

	private StationPaint get( DockStation station ){
		return controller.getTheme().getPaint( station );
	}
	
	public void drawDivider( Graphics g, DockStation station, Rectangle bounds ){
		get( station ).drawDivider( g, station, bounds );
	}

	public void drawInsertion( Graphics g, DockStation station, Rectangle stationBounds, Rectangle dockableBounds ){
		get( station ).drawInsertion( g, station, stationBounds, dockableBounds );
	}

	public void drawInsertionLine( Graphics g, DockStation station, int x1, int y1, int x2, int y2 ){
		get( station ).drawInsertionLine( g, station, x1, y1, x2, y2 );
	}
	
	public void drawRemoval( Graphics g, DockStation station, Rectangle stationBounds, Rectangle dockableBounds ){
		get( station ).drawRemoval( g, station, stationBounds, dockableBounds );
	}
}
