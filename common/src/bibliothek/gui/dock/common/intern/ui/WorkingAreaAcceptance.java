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
package bibliothek.gui.dock.common.intern.ui;

import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.common.mode.CLocationMode;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.util.FrameworkOnly;

/**
 * A {@link DockAcceptance} ensuring that the {@link CDockable#getWorkingArea()}
 * property is respected.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class WorkingAreaAcceptance implements DockAcceptance {
    /** access to the inner parts of the {@link CControl} */
    private CControlAccess control;
    
    /**
     * Creates a new acceptance
     * @param control access to the {@link CControl}
     */
    public WorkingAreaAcceptance( CControlAccess control ){
        this.control = control;
    }

    public boolean accept( DockStation parent, Dockable child, Dockable next ) {
    	return accept( parent, next );
    }
    
    public boolean accept( DockStation parent, Dockable child ) {
    	CLocationModeManager manager = control.getLocationManager();
    	if( manager.isOnTransaction() )
            return true;
    	
    	
    	ExtendedMode extendedMode = manager.childsExtendedMode( parent );
    	if( extendedMode == null ){
    		extendedMode = manager.getMode( child );	
    		if( extendedMode == null ){
    			return true;
    		}
    	}
    	
    	CLocationMode mode = manager.getMode( extendedMode.getModeIdentifier() );
    	if( mode == null )
    		return true;
    	
    	if( !mode.respectWorkingAreas( parent ) ){
    		return true;
    	}
    	
    	CStation<?> area = searchArea( parent );
    	return match( area, child );
    }
    
    /**
     * Searches the first {@link CStation} with the woking-area property set to <code>true</code> in the path to the root.
     * @param element some element
     * @return the first {@link CStation} that occurs on the path from
     * <code>element</code> to the root and which is a working area
     */
    private CStation<?> searchArea( DockElement element ){
        Map<DockStation, CStation<?>> stations = new HashMap<DockStation, CStation<?>>();
        for( CStation<?> station : control.getOwner().getStations() ){
            if( station.isWorkingArea() ){
                stations.put( station.getStation(), station );
            }
        }
        
        DockStation station = element.asDockStation();
        Dockable dockable = element.asDockable();
        
        while( dockable != null || station != null ){
            if( station != null ){
                CStation<?> cstation = stations.get( station );
                if( cstation != null )
                    return cstation;
            }
            
            dockable = station == null ? null : station.asDockable();
            station = dockable == null ? null : dockable.getDockParent();
        }
        return null;
    }
    
    /**
     * Checks all {@link CDockable}s and compares their
     * {@link CDockable#getWorkingArea() working area}
     * with <code>area</code>.
     * @param area a possible new parent
     * @param dockable the root of the tree of elements to test
     * @return <code>true</code> if all elements have <code>area</code> as
     * preferred parent, <code>false</code> otherwise
     */
    private boolean match( CStation<?> area, Dockable dockable ){
        if( dockable instanceof CommonDockable ){
            CDockable fdockable = ((CommonDockable)dockable).getDockable();
            CStation<?> request = fdockable.getWorkingArea();
            if( request != area )
                return false;
        }
        
        DockStation station = dockable.asDockStation();
        if( station != null ){
        	if( dockable instanceof CommonDockable ){
        		CStation<?> cstation = ((CommonDockable)dockable).getStation();
        		if( cstation != null && cstation.isWorkingArea() ){
        			return true;
        		}
        	}
        	
            return match( area, station );
        }
        else
            return true;
    }
    
    /**
     * Checks all {@link CDockable}s and compares their
     * {@link CDockable#getWorkingArea() working area}
     * with <code>area</code>.
     * @param area a possible new parent
     * @param station the root of the tree of elements to test
     * @return <code>true</code> if all elements have <code>area</code> as
     * preferred parent, <code>false</code> otherwise
     */
    private boolean match( CStation<?> area, DockStation station ){
        for( int i = 0, n = station.getDockableCount(); i < n; i++ ){
            boolean result = match( area, station.getDockable( i ));
            if( !result )
                return false;
        }
        return true;
    }
}
