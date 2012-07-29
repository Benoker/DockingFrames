/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.toolbar.location;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.mode.CLocationMode;
import bibliothek.gui.dock.common.perspective.mode.LocationModePerspective;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.toolbar.perspective.CToolbarModePerspective;

/**
 * This {@link CLocationMode} describes the areas that are part of a toolbar.
 * @author Benjamin Sigg
 */
public class CToolbarMode extends ToolbarMode<CToolbarModeArea> implements CLocationMode{
	/**
	 * Creates a new mode
	 * @param control the control in whose realm this mode is used
	 */
	public CToolbarMode( CControl control ){
		super( control.getController() );
	}

	public CLocation getCLocation( Dockable dockable ){
		CToolbarModeArea area = get( dockable );
		if( area == null )
			return null;
			
		return area.getCLocation( dockable );
	}
	
	public CLocation getCLocation( Dockable dockable, Location location ){
		CToolbarModeArea area = get( location.getRoot() );
		if( area == null )
			return null;
			
		return area.getCLocation( dockable, location );
	}

	@Override
	public boolean isBasicMode(){
		return true;
	}

	@Override
	public boolean respectWorkingAreas( DockStation station ){
		return true;
	}

	@Override
	public LocationModePerspective createPerspective(){
		return new CToolbarModePerspective();
	}
}
