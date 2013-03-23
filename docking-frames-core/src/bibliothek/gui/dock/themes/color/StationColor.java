/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.util.color.AbstractDockColor;
import bibliothek.gui.dock.util.color.DockColor;
import bibliothek.util.Path;

/**
 * A color used for painting a {@link DockStation}.
 * @author Benjamin Sigg
 */
public abstract class StationColor extends AbstractDockColor{
	/** the path describing this kind of color */
	public static final Path KIND_STATION_COLOR = DockColor.KIND_DOCK_COLOR.append( "StationColor" );
	
	/** the station for which this color is used*/
	private DockStation station;
	
    /**
     * Creates a new {@link DockColor}.
     * @param id the identifier of this color
     * @param kind which kind of color this is
     * @param station the station for which this color will be used
     * @param backup a backup in case a color is missing
     */
    public StationColor( String id, Path kind, DockStation station, Color backup ){
        super( id, kind, backup );
        this.station = station;
    }
    
    
    /**
     * Creates a new {@link DockColor}.
     * @param id the identifier of this color
     * @param station the station for which this color will be used
     * @param backup a backup in case a color is missing
     */
    public StationColor( String id, DockStation station, Color backup ){
        this( id, KIND_STATION_COLOR, station, backup );
    }
    
    /**
     * Gets the {@link DockStation} for which this color is used. 
     * @return the station, not <code>null</code>
     */
    public DockStation getStation(){
		return station;
	}
}
