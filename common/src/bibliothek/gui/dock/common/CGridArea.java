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
package bibliothek.gui.dock.common;

import javax.swing.JComponent;

import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.common.event.ResizeRequestListener;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.station.SplitResizeRequestHandler;
import bibliothek.gui.dock.common.location.CGridAreaLocation;

/**
 * In a {@link CGridArea} normalized {@link CDockable} can be shown. Clients
 * should use {@link #getComponent()} to gain access to a {@link JComponent} that
 * represents this area.
 * @author Benjamin Sigg
 */
public class CGridArea implements CStation{
    private SplitDockStation station;
    private ResizeRequestListener request;
    private CControlAccess access;
    private String uniqueId;
    private boolean workingArea;
    
    /**
     * Creates a new grid area.
     * @param control the control for which this area will be used
     * @param uniqueId a unique uniqueId
     * @param workingArea whether this is a {@link CStation#isWorkingArea() working area}
     */
    public CGridArea( CControl control, String uniqueId, boolean workingArea ){
        this.uniqueId = uniqueId;
        
        station = control.getFactory().createSplitDockStation();
        
        request = new SplitResizeRequestHandler( station );
    }
    
    /**
     * Gets the {@link JComponent} which represents this station.
     * @return the component
     */
    public JComponent getComponent(){
        return station;
    }
    
    public void setControl( CControlAccess access ) {
        if( this.access != null ){
            this.access.getOwner().removeResizeRequestListener( request );
            this.access.getStateManager().remove( uniqueId );
        }
        
        this.access = access;
        
        if( this.access != null ){
            this.access.getOwner().addResizeRequestListener( request );
            this.access.getStateManager().add( uniqueId, station );
        }
    }
    
    /**
     * Gets the unique id of this area.
     * @return the unique id
     */
    public String getUniqueId() {
        return uniqueId;
    }
    
    public SplitDockStation getStation(){
        return station;
    }
    
    public CLocation getStationLocation() {
        return new CGridAreaLocation( this );
    }
    
    public boolean isWorkingArea() {
        return workingArea;
    }
    
    public CDockable asDockable() {
        return null;
    }
    
    /**
     * Adds all the elements in <code>grid</code> to this area.
     * @param grid the new layout of this area
     */
    public void deploy( CGrid grid ){
        station.dropTree( grid.toTree() );
    }
}
