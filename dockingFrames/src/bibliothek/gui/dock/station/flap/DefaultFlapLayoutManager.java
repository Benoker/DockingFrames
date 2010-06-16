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
package bibliothek.gui.dock.station.flap;

import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.event.DockStationAdapter;

/**
 * The default implementation of {@link FlapLayoutManager}. Uses the same size for all
 * children of one {@link FlapDockStation}.
 * @author Benjamin Sigg
 */
public class DefaultFlapLayoutManager implements FlapLayoutManager{
    /**
     * The properties of different stations.
     */
    private Map<FlapDockStation, Station> stations = new HashMap<FlapDockStation, Station>();

    public void install( FlapDockStation station ) {
        Station properties = new Station();
        station.addDockStationListener( properties );
        properties.size = station.getDefaultWindowSize();
        stations.put( station, properties );
    }

    public void uninstall( FlapDockStation station ) {
        Station properties = stations.remove( station );
        if( properties != null ){
            station.removeDockStationListener( properties );
        }
    }
    
    public int getSize( FlapDockStation station, Dockable dockable ) {
        return stations.get( station ).size;
    }
    
    public boolean isHold( FlapDockStation station, Dockable dockable ) {
        return Boolean.TRUE.equals( stations.get( station ).hold.get( dockable ) );
    }

    public void setHold( FlapDockStation station, Dockable dockable, boolean hold ) {
        stations.get( station ).hold.put( dockable, hold );
    }

    public void setSize( FlapDockStation station, Dockable dockable, int size ) {
        stations.get( station ).size = size;
    }

    /**
     * A set of properties used for one {@link FlapDockStation}
     * @author Benjamin Sigg
     */
    private static class Station extends DockStationAdapter{
        /** which children are hold open */
        public Map<Dockable, Boolean> hold = new HashMap<Dockable, Boolean>();
        /** the size of the station */
        public int size;
        
        @Override
        public void dockableRemoved( DockStation station, Dockable dockable ) {
            hold.remove( dockable );
        }
    }
}
