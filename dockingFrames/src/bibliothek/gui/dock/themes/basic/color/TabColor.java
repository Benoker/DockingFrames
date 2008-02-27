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
package bibliothek.gui.dock.themes.basic.color;

import java.awt.Color;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.util.color.AbstractDockColor;
import bibliothek.gui.dock.util.color.DockColor;

/**
 * Color related to a single {@link Dockable} on a {@link StackDockStation}.
 * @author Benjamin Sigg
 */
public abstract class TabColor extends AbstractDockColor{
    private StackDockStation station;
    private Dockable dockable;
    
    /**
     * Creates a new TabColor.
     * @param id the identifier of the color that is searched
     * @param kind the kind of {@link DockColor} this is.
     * @param station the station on which the color will be used
     * @param dockable the {@link Dockable} for whose tab this color is used
     * @param backup a backup color in case that no color can be found
     */
    public TabColor( String id, Class<? extends DockColor> kind, StackDockStation station, Dockable dockable, Color backup ){
        super( id, kind, backup );
        this.station = station;
        this.dockable = dockable;
    }
    
    public StackDockStation getStation() {
        return station;
    }
    
    public Dockable getDockable() {
        return dockable;
    }
}
