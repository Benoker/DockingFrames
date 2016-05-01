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
package bibliothek.gui.dock.themes.color;

import java.awt.Color;

import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.util.color.AbstractDockColor;
import bibliothek.gui.dock.util.color.DockColor;
import bibliothek.util.Path;

/**
 * A color used by a {@link StationPaint}.
 * @author Benjamin Sigg
 */
public abstract class StationPaintColor extends AbstractDockColor{
    /** the kind of color {@link StationPaintColor} is */
    public static final Path KIND_STATION_PAINT_COLOR = KIND_DOCK_COLOR.append( "StationPaintColor" );
    
    /** the paint that uses this color */
    private StationPaint paint;
    
    /**
     * Creates a new {@link DockColor}
     * @param id the identifier of this color
     * @param kind what kind of color this is
     * @param paint the {@link StationPaint} that uses this color
     * @param backup a backup used when no color was found
     */
    public StationPaintColor( String id, Path kind, StationPaint paint, Color backup ){
        super( id, kind, backup );
        this.paint = paint;
    }
    

    /**
     * Creates a new {@link DockColor}
     * @param id the identifier of this color
     * @param paint the {@link StationPaint} that uses this color
     * @param backup a backup used when no color was found
     */
    public StationPaintColor( String id, StationPaint paint, Color backup ){
        this( id, KIND_DOCK_COLOR, paint, backup );
    }
    
    /**
     * Gets the {@link StationPaint} that uses this color.
     * @return the paint that uses <code>this</code>
     */
    public StationPaint getPaint() {
        return paint;
    }
}
