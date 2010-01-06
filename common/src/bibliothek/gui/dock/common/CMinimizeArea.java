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

import java.awt.BorderLayout;

import javax.swing.JPanel;

import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.common.event.ResizeRequestListener;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.station.FlapResizeRequestHandler;
import bibliothek.gui.dock.common.location.CMinimizeAreaLocation;
import bibliothek.gui.dock.common.mode.CMinimizedModeArea;
import bibliothek.gui.dock.common.mode.station.CFlapDockStationHandle;

/**
 * An area where {@link CDockable}s can be stored in their minimized state.
 * This class is a subclass of {@link JPanel} and can be added anywhere in any
 * frame or dialog.
 * @author Benjamin Sigg
 */
public class CMinimizeArea extends JPanel implements CStation<FlapDockStation>{
    private FlapDockStation station;
    private ResizeRequestListener request;
    private CControlAccess access;
    private String uniqueId;
    private CMinimizedModeArea area;
    
    /**
     * Creates a new minimize area.
     * @param control the control for which this area will be used
     * @param uniqueId the unique uniqueId of this area
     */
    public CMinimizeArea( CControl control, String uniqueId ){
        this.uniqueId = uniqueId;
        
        setLayout( new BorderLayout() );
        
        station = control.getFactory().createFlapDockStation( this );
        request = new FlapResizeRequestHandler( station );
        
        add( station.getComponent(), BorderLayout.CENTER );
        
        setDirection( null );
        
        area = new CFlapDockStationHandle( this );
    }
    
    public void setControl( CControlAccess access ) {
        if( this.access != null ){
            this.access.getOwner().removeResizeRequestListener( request );
            this.access.getLocationManager().getMinimizedMode().remove( area.getUniqueId() );
        }
        
        this.access = access;
        
        if( this.access != null ){
            this.access.getOwner().addResizeRequestListener( request );
            this.access.getLocationManager().getMinimizedMode().add( area );
        }
    }

    /**
     * Gets the unique uniqueId of this area.
     * @return the unique uniqueId
     */
    public String getUniqueId() {
        return uniqueId;
    }
    
    public FlapDockStation getStation(){
        return station;
    }
    
    public CLocation getStationLocation() {
        return new CMinimizeAreaLocation( this );
    }
    
    public boolean isWorkingArea() {
        return false;
    }
    
    public CDockable asDockable() {
        return null;
    }
    
    /**
     * Sets the direction into which the tab opens.
     * @param direction the direction or <code>null</code> to let the system
     * decide automatically
     */
    public void setDirection( FlapDockStation.Direction direction ){
        if( direction == null ){
            station.setAutoDirection( true );
        }
        else{
            station.setAutoDirection( false );
            station.setDirection( direction );
        }
    }
}
