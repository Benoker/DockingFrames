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

package bibliothek.gui.dock.station.support;

import java.awt.Graphics;
import java.awt.Rectangle;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.themes.StationThemeItem;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.util.Path;

/**
 * A wrapper for a {@link StationPaint}. The wrapper can either use a delegate or installs itself as {@link UIValue}
 * on the current {@link ThemeManager}.
 * @author Benjamin Sigg
 */
public class StationPaintValue implements UIValue<StationThemeItem<StationPaint>> {
	/** Unique identifier describing this kind of {@link UIValue} */
	public static final Path KIND_DOCK_STATION_PAINT = new Path( "dock.dockStationPaint" );
	
    /** The delegate that is used if not equal to <code>null</code> */
    private StationPaint delegate;
    
    /** The unique identifier used for the {@link ThemeManager} to get the current {@link StationPaint} */
    private String id;
    
    /** the station that is painted by this wrapper */
    private DockStation station;

    /** the current controller */
    private DockController controller;
    
    /** the current item that paints */
    private StationThemeItem<StationPaint> item;
    
    /**
     * Creates a new wrapper.
     * @param id a unique identifier used on {@link ThemeManager} to get the current {@link StationPaint}
     * @param station the station that is painted by this wrapper
     */
    public StationPaintValue( String id, DockStation station ){
    	this.id = id;
    	this.station = station;
    }
    
    public void setController( DockController controller ){
    	if( this.controller != null ){
    		this.controller.getThemeManager().remove( this );
    	}
    	this.controller = controller;
    	if( controller != null ){
    		controller.getThemeManager().add( id, KIND_DOCK_STATION_PAINT, ThemeManager.STATION_PAINT_TYPE, this );
    	}
    }
    
    public void set( StationThemeItem<StationPaint> value ){
    	this.item = value;
    }
    
    /**
     * Gets the {@link StationPaint} to which calls to this paint are
     * forwarded.
     * @return the delegate or <code>null</code>
     * @see #setDelegate(StationPaint)
     */
    public StationPaint getDelegate() {
        return delegate;
    }
    
    /**
     * Sets the <code>delegate</code> property. If this property is set,
     * all calls to the Methods of {@link StationPaint} are forwarded to
     * it. Otherwise a default paint is used.
     * @param delegate the delegate or <code>null</code>
     */
    public void setDelegate( StationPaint delegate ) {
        this.delegate = delegate;
    }
    
    /**
     * Gets the {@link StationPaint} that should currently be used.
     * @return the current paint or <code>null</code>
     */
    public StationPaint get(){
    	if( delegate != null ){
    		return delegate;
    	}
    	if( item == null ){
    		return null;
    	}
    	return item.get( station );
    }

    /**
     * Calls {@link StationPaint#drawDivider(Graphics, DockStation, Rectangle)}
     * @param g the graphics context
     * @param bounds the boundaries of the divider
     */
    public void drawDivider( Graphics g, Rectangle bounds ) {
    	StationPaint paint = get();
    	if( paint != null ){
    		paint.drawDivider( g, station, bounds );
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
    		paint.drawInsertion( g, station, stationBounds, dockableBounds );
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
    		paint.drawInsertionLine( g, station, x1, y1, x2, y2 );
    	}
    }
}
