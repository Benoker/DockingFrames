/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.station.support;

import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;

import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A {@link DisplayerFactory} that creates {@link DockableDisplayer} with
 * no special settings.
 * @author Benjamin Sigg
 */
public class DefaultDisplayerFactory implements DisplayerFactory {
    /** The location of the title if a {@link Dockable} is sent to the factory */
    private DockableDisplayer.Location dockableLocation = DockableDisplayer.Location.TOP;
    /** The location of the title if a {@link DockStation} is sent to the factory */
    private DockableDisplayer.Location stationLocation = DockableDisplayer.Location.LEFT;
    
    public DockableDisplayer create( DockStation station, Dockable dockable,
            DockTitle title ) {

        DockableDisplayer displayer;
        if( dockable instanceof DockStation )
            displayer = new DockableDisplayer( dockable, title, stationLocation );
        else
            displayer = new DockableDisplayer( dockable, title, dockableLocation );
        displayer.setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ));
        return displayer;
    }
    
    /**
     * Gets the location where the {@link DockTitle} will be shown on the
     * {@link DockableDisplayer}, if a {@link Dockable} is used as child.
     * @return the location
     * @see #setDockableLocation(bibliothek.gui.dock.DockableDisplayer.Location)
     */
    public DockableDisplayer.Location getDockableLocation() {
        return dockableLocation;
    }
    
    /**
     * Sets the location where the {@link DockTitle} will be shown on a
     * {@link DockableDisplayer} if a {@link Dockable} is used as child.
     * @param dockableLocation the location
     */
    public void setDockableLocation( DockableDisplayer.Location dockableLocation ) {
        this.dockableLocation = dockableLocation;
    }
    
    /**
     * Gets the location where the {@link DockTitle} will be shown on the
     * {@link DockableDisplayer}, if a {@link DockStation} is used as child.
     * @return the location
     * @see #setDockableLocation(bibliothek.gui.dock.DockableDisplayer.Location)
     */
    public DockableDisplayer.Location getStationLocation() {
        return stationLocation;
    }
    
    /**
     * Sets the location where the {@link DockTitle} will be shown on a
     * {@link DockableDisplayer} if a {@link DockStation} is used as child.
     * @param stationLocation the location
     */
    public void setStationLocation( DockableDisplayer.Location stationLocation ) {
        this.stationLocation = stationLocation;
    }
}
