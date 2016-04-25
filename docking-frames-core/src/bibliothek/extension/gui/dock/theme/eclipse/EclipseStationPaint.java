/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.themes.color.StationPaintColor;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * @author Janni Kovacs
 */
@ColorCodes({ "paint.line", "paint.divider", "paint.insertion" })
public class EclipseStationPaint implements StationPaint {
    private StationPaintColor color = new StationPaintColor( "", this, Color.BLACK ){
        @Override
        protected void changed( Color oldColor, Color newColor ) {
            // ignore
        }
    };
	
	public void drawInsertionLine(Graphics g, DockStation station, int x1, int y1, int x2, int y2) {
		color.setId( "paint.line" );
		color.connect( station.getController() );
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor( color.value() );
		g2d.setStroke(new BasicStroke(2f));
		g2d.drawLine(x1, y1, x2, y2);
		
		color.connect( null );
	}

	public void drawDivider(Graphics g, DockStation station, Rectangle bounds) {
		if (station instanceof SplitDockStation && !((SplitDockStation) station).isContinousDisplay()) {
			color.setId( "paint.divider" );
			color.connect( station.getController() );
			
			g.setColor( color.value() );
			g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
			
			color.connect( null );
		}
	}

	public void drawInsertion(Graphics g, DockStation station, Rectangle stationBounds, Rectangle dockableBounds) {
		color.setId( "paint.insertion" );
		color.connect( station.getController() );
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor( color.value() );
		g2d.setStroke(new BasicStroke(2f));
		g2d.drawRect(dockableBounds.x+1, dockableBounds.y+1, dockableBounds.width-2, dockableBounds.height-2 );
		
		color.connect( null );
	}
	
	public void drawRemoval( Graphics g, DockStation station, Rectangle stationBounds, Rectangle dockableBounds ){
		// ignore
	}
}
