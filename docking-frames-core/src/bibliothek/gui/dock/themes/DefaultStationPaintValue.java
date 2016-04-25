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

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.util.UIValue;

/**
 * A wrapper for a {@link StationPaint}. The wrapper can either use a delegate or installs itself as {@link UIValue}
 * on the current {@link ThemeManager}.
 * @author Benjamin Sigg
 */
public class DefaultStationPaintValue extends StationThemeItemValue<StationPaint> implements StationPaintValue {
	    
    /**
     * Creates a new wrapper.
     * @param id a unique identifier used on {@link ThemeManager} to get the current {@link StationPaint}
     * @param station the station that is painted by this wrapper
     */
    public DefaultStationPaintValue( String id, DockStation station ){
    	super( id, KIND_STATION_PAINT, ThemeManager.STATION_PAINT_TYPE, station );
    }
    
    /**
     * Calls {@link StationPaint#drawDivider(Graphics, DockStation, Rectangle)}
     * @param g the graphics context
     * @param bounds the boundaries of the divider
     */
    public void drawDivider( Graphics g, Rectangle bounds ) {
    	StationPaint paint = get();
    	if( paint != null ){
    		paint.drawDivider( g, getStation(), bounds );
    	}
    }

    /**
     * Paints some markings when a {@link Dockable} is added to a {@link DockStation}.
     * @param g the graphics context used for painting
     * @param stationBounds the area on the station which will be affected by the insertion
     * @param dockableBounds the bounds that the new child will have
     */
    public void drawInsertion( Graphics g, Rectangle stationBounds, Rectangle dockableBounds ) {
    	StationPaint paint = get();
    	if( paint != null ){
    		paint.drawInsertion( g, getStation(), stationBounds, dockableBounds );
    	}
    }
    
    /**
     * Paints a single line from x1/y1 to x2/y2.
     * @param g the graphics context used for painting
     * @param x1 the x-coordinate of the first end of the line
     * @param y1 the y-coordinate of the first end of the line
     * @param x2 the x-coordinate of the second end of the line
     * @param y2 the y-coordinate of the second end of the line
     */
    public void drawInsertionLine( Graphics g, int x1, int y1, int x2, int y2 ) {
    	StationPaint paint = get();
    	if( paint != null ){
    		paint.drawInsertionLine( g, getStation(), x1, y1, x2, y2 );
    	}
    }
    
    /**
     * Paints some markings when a {@link Dockable} is removed from a {@link DockStation}.
     * @param g the graphics context used for painting
     * @param stationBounds the area on the station which will be affected by the removal
     * @param dockableBounds the bounds that the old child currently has
     */
    public void drawRemoval( Graphics g, Rectangle stationBounds, Rectangle dockableBounds ){
    	StationPaint paint = get();
    	if( paint != null ){
    		paint.drawRemoval( g, getStation(), stationBounds, dockableBounds );
    	}
    }
}
