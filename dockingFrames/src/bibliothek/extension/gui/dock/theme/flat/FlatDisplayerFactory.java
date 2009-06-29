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

package bibliothek.extension.gui.dock.theme.flat;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayer.Location;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A factory for instances of {@link DockableDisplayer}. This
 * factory either sets the border of its created displayers
 * to none or to a {@link FlatBorder}.
 * @author Benjamin Sigg
 */
public class FlatDisplayerFactory implements DisplayerFactory{
    /** Whether the created displayers should have a border */
    private boolean border;
    
    /**
     * Creates a new factory
     * @param border Whether the displayers should have a border or not
     */
    public FlatDisplayerFactory( boolean border ){
        this.border = border;
    }
    
    public DockableDisplayer create( DockStation station, Dockable dockable, DockTitle title ) {
        Location location;
        
        if( dockable.asDockStation() != null )
            location = DockableDisplayer.Location.LEFT;
        else
            location = DockableDisplayer.Location.TOP;

        if( border ){
            return new FlatDockableDisplayer( station, dockable, title, location );
        }
        
        BasicDockableDisplayer displayer = new BasicDockableDisplayer( station, dockable, title, location );
        displayer.setRespectBorderHint( false );
        displayer.setDefaultBorderHint( false );
        displayer.setSingleTabShowInnerBorder( false );
        displayer.setSingleTabShowOuterBorder( false );
        return displayer;
    }
}
