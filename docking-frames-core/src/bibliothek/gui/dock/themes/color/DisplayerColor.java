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

import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.util.color.AbstractDockColor;
import bibliothek.util.Path;

/**
 * A color used on a {@link DockableDisplayer}.
 * @author Benjamin Sigg
 */
public abstract class DisplayerColor extends AbstractDockColor {
    /** the kind of color {@link DisplayerColor} is */
    public static final Path KIND_DISPLAYER_COLOR = KIND_DOCK_COLOR.append( "DisplayerColor" );
    
    /** the element for which the color is needed */
    private DockableDisplayer displayer;
    
    /**
     * Creates a new {@link DisplayerColor}
     * @param id the identifier of the color
     * @param kind which kind of color this is
     * @param displayer the element for which the color is used
     * @param backup a backup color
     */
    public DisplayerColor( String id, Path kind, DockableDisplayer displayer, Color backup ){
        super( id, kind, backup );
        this.displayer = displayer;
    }
    
    /**
     * Creates a new {@link DisplayerColor}
     * @param id the identifier of the color
     * @param displayer the element for which the color is used
     * @param backup a backup color
     */
    public DisplayerColor( String id, DockableDisplayer displayer, Color backup ){
       this( id, KIND_DISPLAYER_COLOR, displayer, backup ); 
    }
    
    /**
     * Gets the element for which this color is used.
     * @return the element
     */
    public DockableDisplayer getDisplayer() {
        return displayer;
    }
}
