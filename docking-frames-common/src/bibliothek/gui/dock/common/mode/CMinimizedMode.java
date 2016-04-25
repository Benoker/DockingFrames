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
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.action.predefined.CMinimizeAction;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.perspective.mode.LocationModePerspective;
import bibliothek.gui.dock.common.perspective.mode.CMinimizedModePerspective;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.MinimizedMode;

/**
 * Manages areas that show only a title of a minimized dockable.
 * @author Benjamin Sigg
 */
public class CMinimizedMode extends MinimizedMode<CMinimizedModeArea> implements CLocationMode {
	/**
	 * Creates a new mode
	 * @param control the owner of this mode
	 */
	public CMinimizedMode( CControl control ){
		setActionProvider( new KeyedLocationModeActionProvider(
				CDockable.ACTION_KEY_MINIMIZE,
				new CMinimizeAction( control )) );
	}
	
	public CLocation getCLocation( Dockable dockable ){
		CMinimizedModeArea area = get( dockable );
		if( area == null )
			return null;
			
		return area.getCLocation( dockable );
	}
	
	public CLocation getCLocation( Dockable dockable, Location location ){
		CMinimizedModeArea area = get( location.getRoot() );
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
		return area.respectWorkingAreas();
	}
	
	public LocationModePerspective createPerspective(){
		return new CMinimizedModePerspective();
	}
}
