/**
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

package bibliothek.gui.dock.themes.basic;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.displayer.DisplayerRequest;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayer.Location;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A {@link DisplayerFactory} that creates {@link DockableDisplayer} with
 * no special settings.
 * @author Benjamin Sigg
 */
public class BasicDisplayerFactory implements DisplayerFactory {
    /** The location of the title if a {@link Dockable} is sent to the factory */
    private DockableDisplayer.Location dockableLocation = DockableDisplayer.Location.TOP;
    /** The location of the title if a {@link DockStation} is sent to the factory */
    private DockableDisplayer.Location stationLocation = DockableDisplayer.Location.LEFT;
    
    public void request( DisplayerRequest request ){
    	Dockable dockable = request.getTarget();
    	DockStation station = request.getParent();
    	DockTitle title = request.getTitle();
    	
    	BasicDockableDisplayer displayer;
    	
        if( dockable instanceof DockStation )
            displayer = create( station, dockable, title, stationLocation );
        else
            displayer = create( station, dockable, title, dockableLocation );
        
        displayer.setDefaultBorderHint( true );
        displayer.setRespectBorderHint( true );
        displayer.setSingleTabShowInnerBorder( true );
        displayer.setSingleTabShowOuterBorder( true );
        
        request.answer( displayer );
    }
    
    /**
     * Creates a new displayer.
     * @param station the station for which this displayer is needed
     * @param dockable the element that is shown on the displayer
     * @param title the title of the displayer
     * @param location the location of <code>title</code>
     * @return the new displayer
     */
    protected BasicDockableDisplayer create( DockStation station, Dockable dockable, DockTitle title, Location location ){
        return new BasicDockableDisplayer( station, dockable, title, location );
    }
    
    /**
     * Gets the location where the {@link DockTitle} will be shown on the
     * {@link DockableDisplayer}, if a {@link Dockable} is used as child.
     * @return the location
     * @see #setDockableLocation(bibliothek.gui.dock.station.DockableDisplayer.Location)
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
     * @see #setDockableLocation(bibliothek.gui.dock.station.DockableDisplayer.Location)
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
