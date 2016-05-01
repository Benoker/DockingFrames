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
import bibliothek.gui.dock.common.action.predefined.CNormalizeAction;
import bibliothek.gui.dock.common.action.predefined.CUnexternalizeAction;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.perspective.mode.LocationModePerspective;
import bibliothek.gui.dock.common.perspective.mode.CNormalModePerspective;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationModeActionProvider;
import bibliothek.gui.dock.facile.mode.MappingLocationModeActionProvider;
import bibliothek.gui.dock.facile.mode.NormalMode;
import bibliothek.gui.dock.support.mode.Mode;

/**
 * A mode managing {@link CNormalModeArea}s.
 * @author Benjamin Sigg
 */
public class CNormalMode extends NormalMode<CNormalModeArea> implements CLocationMode{
	/** the default action for normalizing an element */
	private LocationModeActionProvider normalize;
	/** the action for normalizing an externalized element */
	private LocationModeActionProvider unexternalize;
	
	/**
	 * Creates a new mode.
	 * @param control the owner of this mode
	 */
	public CNormalMode( CControl control ){
		normalize = new KeyedLocationModeActionProvider( CDockable.ACTION_KEY_NORMALIZE, new CNormalizeAction( control ));
		unexternalize = new KeyedLocationModeActionProvider( CDockable.ACTION_KEY_UNEXTERNALIZE, new CUnexternalizeAction( control ));
		
		setActionProvider( new MappingLocationModeActionProvider() {
			protected LocationModeActionProvider getProvider( Dockable dockable, Mode<Location> currentMode, DockActionSource currentSource ){
				if( currentMode instanceof CMaximizedMode ){
					currentMode = ((CMaximizedMode)currentMode).getUnmaximizedMode( dockable );
				}
				
				if( currentMode instanceof CExternalizedMode ){
					return unexternalize;
				}
				
				return normalize;
			}
		});
	}
	
	public CLocation getCLocation( Dockable dockable ){
		CNormalModeArea area = get( dockable );
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
	
	/**
	 * Gets a location on station that is not a working-area. 
	 * @return the location, <code>null</code> if not available
	 */
	public CLocation getDefaultLocation(){
		CNormalModeArea area = getDefaultArea();
		if( area == null || area.isWorkingArea() ){
			for( CNormalModeArea check : this ){
				if( !check.isWorkingArea() ){
					area = check;
					break;
				}
			}
		}
		if( area == null )
			return null;
		
		return area.getBaseLocation();
	}
	
	public boolean respectWorkingAreas( DockStation station ){
		CModeArea area = get( station );
		if( area == null ){
			return true;
		}
		return area.respectWorkingAreas();
	}
	
	public LocationModePerspective createPerspective(){
		return new CNormalModePerspective();
	}
}
