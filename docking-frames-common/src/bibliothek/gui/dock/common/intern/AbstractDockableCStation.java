/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.common.intern;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.station.CommonDockStation;

/**
 * An abstract implementation of {@link CStation} that can be docked like a {@link CDockable}.
 * @author Benjamin Sigg
 * @param <S> the kind of station represented by this wrapper
 */
public abstract class AbstractDockableCStation<S extends CommonDockStation<?, ?>> extends AbstractCDockable implements CStation<S>{
    private CLocation location;
    private String id;
    private S station;
    
    /**
     * Creates a new station.
     * @param station the internal representation of this station
     * @param id the unique id of this station
     * @param location a location that points directly to this station
     * @param dockable how this station appears as dockable
     */
    public AbstractDockableCStation( S station, String id, CLocation location, CommonDockable dockable ){
    	init( station, id, location, dockable );
    }
    
    /**
     * Creates a new station but does not yet initialize its fields. Subclasses
     * should call {@link #init(CommonDockStation, String, CLocation, CommonDockable)}.
     */
    protected AbstractDockableCStation(){
    	// nothing
    }
    
    /**
     * Initializes the fields of this station.
     * @param station the internal representation of this station
     * @param id the unique id of this station
     * @param location a location that points directly to this station
     * @param dockable how this station appears as dockable
     */
    protected void init( S station, String id, CLocation location, CommonDockable dockable ){
    	if( station == null )
    		throw new IllegalArgumentException( "station must not be null" );
    	
    	if( id == null )
    		throw new IllegalArgumentException( "id must not be null" );
    	
    	if( location == null )
    		throw new IllegalArgumentException( "location must not be null" );
    	
    	super.init( dockable );
    	
        this.station = station;
        this.id = id;
        this.location = location;	
    }
    
    @Override
    public String toString(){
	    return getClass().getSimpleName() + "[id=" + getUniqueId() + "]";
    }
    
    @Override
    protected CommonDockable createCommonDockable(){
	    throw new IllegalStateException( "common-dockable should have been set by the constructor" );
    }
    
    public CLocation getStationLocation() {
        return location;
    }
    
    public CLocation getDropLocation(){
    	CControlAccess control = getControlAccess();
    	if( control == null ){
    		return null;
    	}
    	return control.getLocationManager().getDropLocation( this );
    }

    public String getUniqueId() {
        return id;
    }

    public S getStation() {
        return station;
    }
    
    public CStation<?> asStation(){
    	return this;
    }

    public void setControlAccess( CControlAccess access ) {
    	CControlAccess control = getControlAccess();
    	super.setControlAccess( access );
        if( control != access ){
            if( control != null )
                uninstall( control );
            
            control = access;
            if( control != null )
                install( control );
        }
    }
    
    public boolean isWorkingArea() {
        return false;
    }

    public CDockable asDockable() {
        return null;
    }
    
    /**
     * Called when this station is added to a {@link CControl}.
     * @param access access to the internals of the new owner
     */
    protected abstract void install( CControlAccess access );
    
    /**
     * Called when this station is removed from a {@link CControl}.
     * @param access access to the internals of the old owner
     */
    protected abstract void uninstall( CControlAccess access );
}
