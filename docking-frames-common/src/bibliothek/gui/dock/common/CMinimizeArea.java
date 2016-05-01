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
import java.awt.Component;

import javax.swing.JPanel;

import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.common.event.ResizeRequestListener;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.station.CFlapDockStation;
import bibliothek.gui.dock.common.intern.station.CommonDockStation;
import bibliothek.gui.dock.common.intern.station.CommonStationDelegate;
import bibliothek.gui.dock.common.intern.station.FlapResizeRequestHandler;
import bibliothek.gui.dock.common.location.CMinimizeAreaLocation;
import bibliothek.gui.dock.common.mode.CMinimizedModeArea;
import bibliothek.gui.dock.common.mode.station.CFlapDockStationHandle;
import bibliothek.gui.dock.common.perspective.CMinimizePerspective;
import bibliothek.gui.dock.common.perspective.CStationPerspective;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.util.Path;

/**
 * An area where {@link CDockable}s can be stored in their minimized state. This class is a subclass of {@link JPanel} and 
 * can be added anywhere in any frame or dialog. This {@link JPanel} uses a {@link BorderLayout} and clients may add
 * additional {@link Component}s to it, the {@link Component}s the size and location of the opening window will always
 * depend on this {@link JPanel}.
 * @author Benjamin Sigg
 */
public class CMinimizeArea extends JPanel implements CStation<CFlapDockStation>{
	/** The result of {@link #getTypeId()} */
	public static final Path TYPE_ID = new Path( "dock", "CMinimizeArea" );
	
    private CommonDockStation<FlapDockStation,CFlapDockStation> station;
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
    	init( control, uniqueId );
    }
    
    /**
     * Default constructor doing nothing, subclasses must call {@link #init(CControl, String)}
     * after calling this method
     */
    protected CMinimizeArea(){
    	// nothing
    }
    
    /**
     * Initializes the new area, should be called only once by subclasses
     * @param control the control for which this area will be used
     * @param uniqueId the unique uniqueId of this area
     */
    protected void init( CControl control, String uniqueId ){
        this.uniqueId = uniqueId;
        
        setLayout( new BorderLayout() );
        
        station = control.getFactory().createFlapDockStation( this, new CommonStationDelegate<CFlapDockStation>(){
			public boolean isTitleDisplayed( DockTitleVersion title ){
				return true;
			}
			
			public CStation<CFlapDockStation> getStation(){
				return CMinimizeArea.this;
			}
			
			public DockActionSource[] getSources(){
				return new DockActionSource[]{};
			}
			
			public CDockable getDockable(){
				return null;
			}
		});
        
        request = new FlapResizeRequestHandler( station.getDockStation() );
        
        add( getStation().getComponent(), BorderLayout.CENTER );
        
        setDirection( null );
        
        area = new CFlapDockStationHandle( this );
    }
    
    public void setControlAccess( CControlAccess access ) {
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

	public Path getTypeId(){
		return TYPE_ID;
	}
	
    public CFlapDockStation getStation(){
        return station.asDockStation();
    }
    
    public CStationPerspective createPerspective(){
    	return new CMinimizePerspective( getUniqueId(), getTypeId() );
    }
    
    public CLocation getStationLocation() {
        return new CMinimizeAreaLocation( this );
    }
    
    public CLocation getDropLocation(){
    	if( access == null ){
    		return null;
    	}
    	return access.getLocationManager().getDropLocation( this );
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
    	FlapDockStation station = getStation();
    	
        if( direction == null ){
            station.setAutoDirection( true );
        }
        else{
            station.setAutoDirection( false );
            station.setDirection( direction );
        }
    }
}
