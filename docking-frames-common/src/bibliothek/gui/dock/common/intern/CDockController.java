/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.common.intern;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.control.DockControllerFactory;
import bibliothek.gui.dock.control.focus.FocusRequest;

/**
 * The {@link DockController} that is usually used by a {@link CControl}.
 * @author Benjamin Sigg
 */
public class CDockController extends DockController{
	/** the control which uses this controller */
	private CControl owner;
	
	/**
	 * Creates a new controller
	 * @param owner the owner of this controller
	 */
	public CDockController( CControl owner ){
		this.owner = owner;
	}
	
	/**
	 * Creates a new controller
	 * @param owner the owner of this controller
	 * @param factory tells this controller how to initialize several subsystems
	 */
	public CDockController( CControl owner, DockControllerFactory factory ){
		super( factory );
		this.owner = owner;
	}
	
	@Override
	public void setFocusedDockable( FocusRequest request ){
		if( request.getSource() != null ){
			Dockable focusedDockable = request.getSource().getElement().asDockable();
			CLocationModeManager states = owner.getLocationManager();
			if( states != null && !states.isOnTransaction() && focusedDockable != null ){
				states.ensureNotHidden( focusedDockable );
			}
		}
		super.setFocusedDockable( request );
	}
	
	@Override
	protected void showCoreWarning(){
		// do not show the warning
	}
}
