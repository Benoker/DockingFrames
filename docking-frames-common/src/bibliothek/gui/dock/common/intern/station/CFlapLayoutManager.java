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
package bibliothek.gui.dock.common.intern.station;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.FlapDockStation.Direction;
import bibliothek.gui.dock.common.event.CDockableAdapter;
import bibliothek.gui.dock.common.event.CDockablePropertyListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.station.flap.AbstractFlapLayoutManager;
import bibliothek.gui.dock.station.flap.FlapLayoutManager;
import bibliothek.util.FrameworkOnly;

/**
 * A {@link FlapLayoutManager} that uses the properties of {@link CDockable} to
 * find and store size and hold values.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class CFlapLayoutManager extends AbstractFlapLayoutManager implements FlapLayoutManager {
    /**
     * A listener added to each {@link FlapDockStation}.
     */
    private DockStationListener stationListener = new DockStationAdapter(){
        @Override
        public void dockableAdded( DockStation station, Dockable dockable ) {
            if( dockable instanceof CommonDockable ){
                ((CommonDockable)dockable).getDockable().addCDockablePropertyListener( propertyListener );
            }
        }
        @Override
        public void dockableRemoved( DockStation station, Dockable dockable ) {
            if( dockable instanceof CommonDockable ){
                ((CommonDockable)dockable).getDockable().removeCDockablePropertyListener( propertyListener );
            }
            else{
                holds.remove( dockable );
                sizes.remove( dockable );
            }
        }
    };
    
    /**
     * A listener added to each {@link CDockable}.
     */
    private CDockablePropertyListener propertyListener = new CDockableAdapter(){
        @Override
        public void stickyChanged( CDockable dockable ) {
            DockStation parent = dockable.intern().getDockParent();
            if( parent instanceof FlapDockStation ){
                ((FlapDockStation)parent).updateHold( dockable.intern() );
            }
        }
        @Override
        public void minimizeSizeChanged( CDockable dockable ) {
            DockStation parent = dockable.intern().getDockParent();
            if( parent instanceof FlapDockStation ){
                ((FlapDockStation)parent).updateWindowSize( dockable.intern() );
            }
        }
        public void stickySwitchableChanged( CDockable dockable ){
        	DockStation parent = dockable.intern().getDockParent();
            if( parent instanceof FlapDockStation ){
                fireHoldSwitchableChanged( (FlapDockStation)parent, dockable.intern() );
            }
        }
    };
    
    /** temporary storage of holds for non CommonDockables */
    private Map<Dockable, Boolean> holds = new HashMap<Dockable, Boolean>();
    
    /** temporary storage of sizes for non CommonDockables */
    private Map<Dockable, Integer> sizes = new HashMap<Dockable, Integer>();
    
    public void install( FlapDockStation station ) {
        station.addDockStationListener( stationListener );
        for( int i = 0, n = station.getDockableCount(); i<n; i++ )
            stationListener.dockableAdded( station, station.getDockable( i ) );
    }

    public void uninstall( FlapDockStation station ) {
        station.removeDockStationListener( stationListener );
        for( int i = 0, n = station.getDockableCount(); i<n; i++ )
            stationListener.dockableRemoved( station, station.getDockable( i ) );
    }

    public int getSize( FlapDockStation station, Dockable dockable ) {
        Direction direction = station.getDirection();
        int size = getMaxSize( dockable, direction == Direction.NORTH || direction == Direction.SOUTH );
        if( size < 0 ){
            Integer value = sizes.get( dockable );
            if( value != null )
                return value.intValue();
            
            return station.getDefaultWindowSize();
        }
        return size;
    }
    
    private int getMaxSize( Dockable dockable, boolean horizontal ){
        if( dockable instanceof CommonDockable ){
            CDockable cdock = ((CommonDockable)dockable).getDockable();
            Dimension size = cdock.getMinimizedSize();
            if( horizontal )
                return size.height;
            else
                return size.width;
        }
        if( dockable instanceof StackDockStation ){
            StackDockStation station = (StackDockStation)dockable;
            int size = -1;
            for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
                size = Math.max( size, getMaxSize( station.getDockable( i ), horizontal ));
            }
            return size;
        }
        return -1;
    }
    
    public void setSize( FlapDockStation station, Dockable dockable, int size ) {
        if( dockable instanceof CommonDockable ){
            Direction direction = station.getDirection();
            boolean horizontal = direction == Direction.NORTH || direction == Direction.SOUTH;
            CDockable cdock = ((CommonDockable)dockable).getDockable();
            Dimension dimension = cdock.getMinimizedSize();
            if( horizontal ){
            	cdock.setMinimizedSize( new Dimension( dimension.width, size ) );
            }
            else{
            	cdock.setMinimizedSize( new Dimension( size, dimension.height ) );
            }
        }
        else{
            sizes.put( dockable, size );
        }
    }

    public boolean isHold( FlapDockStation station, Dockable dockable ) {
        if( dockable instanceof CommonDockable ){
            return ((CommonDockable)dockable).getDockable().isSticky();
        }
        else{
            return Boolean.TRUE.equals( holds.get( dockable ));
        }
    }

    public void setHold( FlapDockStation station, Dockable dockable, boolean hold ) {
        if( dockable instanceof CommonDockable ){
            ((CommonDockable)dockable).getDockable().setSticky( hold );
        }
        else{
            holds.put( dockable, hold );
        }
    }
    
    public boolean isHoldSwitchable( FlapDockStation station, Dockable dockable ){
    	if( dockable instanceof CommonDockable ){
    		return ((CommonDockable)dockable).getDockable().isStickySwitchable();
    	}
    	else{
    		return true;
    	}
    }
}
