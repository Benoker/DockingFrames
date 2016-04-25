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
package bibliothek.gui.dock.common.intern.ui;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.group.CGroupMovement;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.util.FrameworkOnly;

/**
 * A {@link DockAcceptance} ensuring that the {@link CDockable#getExtendedMode() extended mode} property
 * of {@link CDockable} is respected on drag and drop operations.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class ExtendedModeAcceptance implements DockAcceptance {
	/** access to the {@link CControl} */
	private CControlAccess control;

	/**
	 * Creates a new acceptance.
	 * @param control access to the {@link CControl}
	 */
	public ExtendedModeAcceptance( CControlAccess control ){
		this.control = control;
	}

	public boolean accept( DockStation parent, Dockable child ) {
		CLocationModeManager manager = control.getLocationManager();
    	if( manager.isOnTransaction() ){
			CGroupMovement action = manager.getCurrentAction();
    		if( action == null || action.forceAccept( parent, child )){
    			return true;
    		}
		}

		CLocationModeManager locationManager = control.getLocationManager();

		ExtendedMode mode = locationManager.childsExtendedMode( parent );

		if( mode == null ){
			// the parent is not yet known to anyone, so just hope
			// that the developer has made the correct settings, because
			// we cannot check them here.
			return true;
		}

		return locationManager.isModeAvailable( child, mode );
	}

	public boolean accept( DockStation parent, Dockable child, Dockable next ) {
		return accept( parent, next );
	}
}
