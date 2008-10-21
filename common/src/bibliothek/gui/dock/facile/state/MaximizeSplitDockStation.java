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
package bibliothek.gui.dock.facile.state;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.event.SplitDockListener;
import bibliothek.gui.dock.station.split.SplitDockTree;

/**
 * A {@link MaximizeArea} which works together with a {@link SplitDockStation}.
 * @author Benjamin Sigg
 */
public class MaximizeSplitDockStation implements MaximizeArea{
    /** unique identifier */
    private String uniqueId;
    /** delegate to show the elements */
    private SplitDockStation station;

    /** observers of this station */
    private List<MaximizeAreaListener> listeners = new ArrayList<MaximizeAreaListener>();
    
    /** listener to {@link #station} */
    private SplitDockListener stationListener = new SplitDockListener(){
	public void fullScreenDockableChanged( SplitDockStation station, Dockable oldFullScreen, Dockable newFullScreen ) {
	    MaximizeAreaListener[] array = listeners.toArray( new MaximizeAreaListener[ listeners.size() ] );
	    for( MaximizeAreaListener listener : array ){
		listener.maximizedChanged( MaximizeSplitDockStation.this, oldFullScreen, newFullScreen );
	    }
	}
    };
    
    /**
     * Creates a new area.
     * @param uniqueId the result of {@link #getUniqueId()}
     * @param station the result of {@link #getStation()}
     */
    public MaximizeSplitDockStation( String uniqueId, SplitDockStation station ){
	if( uniqueId == null )
	    throw new IllegalArgumentException( "uniqueId must not be null" );
	if( station == null )
	    throw new IllegalArgumentException( "station must not be null" );

	this.uniqueId = uniqueId;
	this.station = station;
    }

    public void addMaximizeAreaListener( MaximizeAreaListener listener ) {
	if( listener == null )
	    throw new IllegalArgumentException( "listener must not be null" );
	
	if( listeners.isEmpty() ){
	    station.addSplitDockStationListener( stationListener );
	}
	listeners.add( listener );
    }
    
    public void removeMaximizeAreaListener( MaximizeAreaListener listener ) {
        listeners.remove( listener );
        if( listeners.isEmpty() ){
            station.removeSplitDockStationListener( stationListener );
        }
    }
    
    public void dropAside( Dockable dockable ) {
	SplitDockTree tree = station.createTree();
	if( tree.getRoot() == null )
	    tree.root( dockable );
	else{
	    tree.root( tree.horizontal( tree.put( dockable ), tree.unroot() ) );
	}
	station.dropTree( tree, false );
    }

    public Dockable getMaximizedDockable() {
	return station.getFullScreen();
    }

    public DockStation getStation() {
	return station;
    }

    public String getUniqueId() {
	return uniqueId;
    }

    public void setMaximizedDockable( Dockable dockable ) {
	station.setFullScreen( dockable );
    }
}
