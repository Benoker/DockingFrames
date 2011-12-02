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
package bibliothek.gui.dock.common.group;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.LocationModeManager;

/**
 * This most simple {@link CGroupBehavior} just set the mode of the one
 * {@link Dockable} whose mode should change anyway.
 * @author Benjamin Sigg
 */
public class TopMostGroupBehavior implements CGroupBehavior{
	public CGroupMovement prepare( LocationModeManager<? extends LocationMode> manager, Dockable dockable, ExtendedMode target ){
		return new SingleGroupMovement( dockable, target );
	}
	
	public Dockable getGroupElement( LocationModeManager<? extends LocationMode> manager, Dockable dockable, ExtendedMode mode ){
		return dockable;
	}
	
	public Dockable getReplaceElement( LocationModeManager<? extends LocationMode> manager, Dockable old, Dockable dockable, ExtendedMode mode ){
		if( dockable == old )
			return null;
		return old;
	}
	
	public boolean shouldForwardActions( LocationModeManager<? extends LocationMode> manager, DockStation station, Dockable dockable, ExtendedMode mode ){
		return false;
	}
}
