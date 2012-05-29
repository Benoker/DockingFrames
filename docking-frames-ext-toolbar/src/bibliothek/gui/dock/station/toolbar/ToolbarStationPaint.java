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

package bibliothek.gui.dock.station.toolbar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.themes.basic.BasicStationPaint;

/**
 * This {@link StationPaint} is used to draw content on all the toolbar-
 * {@link DockStation}s.
 * 
 * @author Benjamin Sigg
 */
public class ToolbarStationPaint extends BasicStationPaint{
	private Color dragColor;

	public ToolbarStationPaint( Color dropColor, Color dragColor ){
		setColor(dropColor);
		this.dragColor = dragColor;
	}

	@Override
	public void drawRemoval( Graphics g, DockStation station,
			Rectangle stationBounds, Rectangle dockableBounds ){
		Color color = getColor();
		if (dragColor == null) {
			setColor(Color.GRAY);
		} else {
			setColor(dragColor);
		}
		
		super.drawRemoval(g, station, stationBounds, dockableBounds);
		setColor(color);
	}
}
