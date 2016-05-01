/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.common.mode;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.action.predefined.CExternalizeAction;
import bibliothek.gui.dock.common.action.predefined.CUnmaximizeExternalizedAction;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.perspective.mode.CExternalizedModePerspective;
import bibliothek.gui.dock.common.perspective.mode.LocationModePerspective;
import bibliothek.gui.dock.facile.mode.ExternalizedMode;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationModeActionProvider;
import bibliothek.gui.dock.facile.mode.MappingLocationModeActionProvider;
import bibliothek.gui.dock.support.mode.Mode;

/**
 * Manages areas on which externalized dockables are shown.
 * @author Benjamin Sigg
 */
public class CExternalizedMode extends ExternalizedMode<CExternalizedModeArea> implements CLocationMode {
	/** The control in whose realm this externalized mode operates */
	private CControl control;
	
	/** actions to externalize an element */
	private LocationModeActionProvider externalize;
	
	/** actions to unmaximize an element */
	private LocationModeActionProvider unmaximize;
	
	/**
	 * Creates a new mode.
	 * @param control the owner of this mode
	 */
	public CExternalizedMode( CControl control ){
		this.control = control;
		externalize = new KeyedLocationModeActionProvider( CDockable.ACTION_KEY_EXTERNALIZE, new CExternalizeAction( control ));
		unmaximize = new KeyedLocationModeActionProvider( CDockable.ACTION_KEY_UNMAXIMIZE_EXTERNALIZED, new CUnmaximizeExternalizedAction( control ));
		
		setActionProvider( new MappingLocationModeActionProvider() {
			protected LocationModeActionProvider getProvider( Dockable dockable, Mode<Location> currentMode, DockActionSource currentSource ){
				if( currentMode instanceof CMaximizedMode ){
					if( ((CMaximizedMode)currentMode).getUnmaximizedMode( dockable ) instanceof CExternalizedMode ){
						return unmaximize;
					}
				}
				return externalize;
			}
		});
	}

	public CLocation getCLocation( Dockable dockable ){
		CExternalizedModeArea area = get( dockable );
		if( area == null )
			return null;
		
		return area.getCLocation( dockable );
	}
	
	public CLocation getCLocation( Dockable dockable, Location location ){
		CModeArea area = get( location.getRoot() );
		if( area == null )
			return null;
			
		return area.getCLocation( dockable, location );
	}
	
	public boolean isBasicMode(){
		return true;
	}
	
	public boolean respectWorkingAreas( DockStation station ){
		CModeArea area = get( station );
		if( area == null ){
			return true;
		}
		if( area.getStation() != station ){
			CStation<?> cstation = control.findStation( station );
			if( cstation == null || cstation.getStation() != area.getStation() ){
				return true;
			}
		}
		return area.respectWorkingAreas();
	}
	
	public LocationModePerspective createPerspective(){
		return new CExternalizedModePerspective();
	}
}
