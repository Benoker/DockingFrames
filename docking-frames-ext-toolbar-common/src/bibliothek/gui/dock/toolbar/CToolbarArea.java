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

package bibliothek.gui.dock.toolbar;

import bibliothek.gui.Orientation;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.AbstractCStation;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.common.perspective.CStationPerspective;
import bibliothek.gui.dock.toolbar.intern.CommonToolbarContainerDockStation;
import bibliothek.gui.dock.toolbar.location.CToolbarAreaHandle;
import bibliothek.gui.dock.toolbar.location.CToolbarAreaLocation;
import bibliothek.gui.dock.toolbar.location.CToolbarMode;
import bibliothek.gui.dock.toolbar.location.ToolbarMode;
import bibliothek.gui.dock.toolbar.perspective.CToolbarAreaPerspective;
import bibliothek.util.Path;

/**
 * The {@link CToolbarArea} acts as root {@link CStation} for toolbars. 
 * @author Benjamin Sigg
 */
public class CToolbarArea extends AbstractCStation<CommonToolbarContainerDockStation> {
	/** The result of {@link #getTypeId()} */
	public static final Path TYPE_ID = new Path( "dock", "CToolbarArea" );
	
	/** connection between <code>this</code> and the {@link CLocationModeManager} */
	private CToolbarAreaHandle handle;
	
	/**
	 * Creates a new container.
	 * @param id the unique identifier of this container, not <code>null</code>
	 * @param orientation whether the items are aligned horizontally or vertically, not <code>null</code>
	 */
	public CToolbarArea( String id, Orientation orientation ){
		CLocation location = new CToolbarAreaLocation( this );
		init( new CommonToolbarContainerDockStation( this, orientation ), id, location );
	}
	
	@Override
	public CToolbarAreaLocation getStationLocation(){
		return (CToolbarAreaLocation)super.getStationLocation();
	}
	
	@Override
	public CStationPerspective createPerspective(){
		return new CToolbarAreaPerspective( getUniqueId() );
	}

	@Override
	public Path getTypeId(){
		return TYPE_ID;
	}

	@Override
	protected void install( CControlAccess access ){
		handle = new CToolbarAreaHandle( this );
		CToolbarMode mode = (CToolbarMode)access.getLocationManager().getMode( ToolbarMode.IDENTIFIER );
		mode.add( handle );
	}

	@Override
	protected void uninstall( CControlAccess access ){
		CToolbarMode mode = (CToolbarMode)access.getLocationManager().getMode( ToolbarMode.IDENTIFIER );
		mode.remove( handle.getUniqueId() );
		handle = null;
	}
}
