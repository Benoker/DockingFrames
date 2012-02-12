/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.common.intern.AbstractCStation;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.station.CScreenDockStation;
import bibliothek.gui.dock.common.intern.station.CommonDockStation;
import bibliothek.gui.dock.common.intern.station.CommonStationDelegate;
import bibliothek.gui.dock.common.intern.station.ScreenResizeRequestHandler;
import bibliothek.gui.dock.common.location.CExternalizedLocation;
import bibliothek.gui.dock.common.mode.CExternalizedMode;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.common.mode.CMaximizedMode;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.common.mode.station.CScreenDockStationHandle;
import bibliothek.gui.dock.common.perspective.CExternalizePerspective;
import bibliothek.gui.dock.common.perspective.CStationPerspective;
import bibliothek.gui.dock.facile.station.screen.WindowProviderVisibility;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.util.Path;

/**
 * This {@link CStation} handles those {@link CDockable}s whose mode is
 * {@link ExtendedMode#EXTERNALIZED}.
 * @author Benjamin Sigg
 */
public class CExternalizeArea extends AbstractCStation<CScreenDockStation> {
	/** The result of {@link #getTypeId()} */
	public static final Path TYPE_ID = new Path( "dock", "CExternalizeArea" );
	
	/** responsible for handling resize requests */
    private ScreenResizeRequestHandler handler;
    /** responsible for representing this in the {@link CLocationModeManager} */
    private CScreenDockStationHandle handle;
    /** keeps track of the visibility of the parent window */
    private WindowProviderVisibility visibility;
    
    /**
     * Creates a new area.
     * @param control the owner of this area
     * @param id the unique identifier of this area
     */
    public CExternalizeArea( CControl control, String id ){
    	init( control, id );
    }
    
    
    private void init( CControl control, String id ){
    	CommonDockStation<ScreenDockStation,CScreenDockStation> station = control.getFactory().createScreenDockStation( control.getRootWindow(), new CommonStationDelegate<CScreenDockStation>(){
			public boolean isTitleDisplayed( DockTitleVersion title ){
				return false;
			}
			
			public CStation<CScreenDockStation> getStation(){
				return CExternalizeArea.this;
			}
			
			public DockActionSource[] getSources(){
				return new DockActionSource[]{};
			}
			
			public CDockable getDockable(){
				return null;
			}
		});
    	
    	init( station.asDockStation(), id, CExternalizedLocation.STATION );
    	
    	handler = new ScreenResizeRequestHandler( getStation() );
    	visibility = new WindowProviderVisibility( getStation() );
    	getStation().setShowing( false );
    }
    
    @Override
    protected void install( CControlAccess access ) {
        access.getOwner().addResizeRequestListener( handler );
        
        visibility.setProvider( access.getOwner().getRootWindow() );
        
        if( handle == null ){
        	handle = new CScreenDockStationHandle( this, access.getLocationManager() );
        }
        
        CExternalizedMode externalizedMode = access.getLocationManager().getExternalizedMode();
        CMaximizedMode maximizedMode = access.getLocationManager().getMaximizedMode();
        
        externalizedMode.add( handle.getExternalizedModeArea() );
        if( externalizedMode.getDefaultArea() == null ){
        	externalizedMode.setDefaultArea( handle.getExternalizedModeArea() );
        }
        
        maximizedMode.add( handle.getMaximizedModeArea() );
    }
    @Override
    protected void uninstall( CControlAccess access ) {
    	visibility.setProvider( null );
    	getStation().setShowing( false );
    	
        access.getOwner().removeResizeRequestListener( handler );
        
        access.getLocationManager().getExternalizedMode().remove( handle.getExternalizedModeArea().getUniqueId() );
        access.getLocationManager().getMaximizedMode().remove( handle.getMaximizedModeArea().getUniqueId() );
    }
    
    public CStationPerspective createPerspective(){
	    return new CExternalizePerspective( getUniqueId(), getTypeId() );
    }
    
    public Path getTypeId(){
	    return TYPE_ID;
    }
}
