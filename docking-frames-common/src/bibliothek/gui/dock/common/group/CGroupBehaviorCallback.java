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
package bibliothek.gui.dock.common.group;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.LocationModeManager;

/**
 * A set of information and methods for the {@link CGroupBehavior}.
 * @author Benjamin Sigg
 */
public interface CGroupBehaviorCallback {
	/**
	 * Gets the {@link LocationModeManager} which is handling this callback.
	 * @return the manager, not <code>null</code>
	 */
	public LocationModeManager<? extends LocationMode> getManager();
	
	/**
	 * Sets the {@link ExtendedMode} of <code>dockable</code> using all available
	 * history information.<br>
	 * <b>Note</b>: {@link LocationMode}s may decide that more than one {@link Dockable} must be moved
	 * in order to accomplish the goal, any cached location information about {@link Dockable}s should be
	 * considered invalid once this method has been executed.
	 * @param dockable the element whose mode is going to change 
	 * @param mode the new mode
	 */
	public void setMode( Dockable dockable, ExtendedMode mode );
	
	/**
	 * Gets the current location of <code>dockable</code>.
	 * @param dockable some item whose location is requested
	 * @return the location, may be <code>null</code>
	 */
	public Location getLocation( Dockable dockable );
	
	/**
	 * Sets the location of <code>dockable</code>.<br>
	 * <b>Note</b>: {@link LocationMode}s may decide that more than one {@link Dockable} must be moved
	 * in order to accomplish the goal, any cached location information about {@link Dockable}s should be
	 * considered invalid once this method has been executed.
	 * @param dockable the element whose location is going to be set
	 * @param location the new location
	 */
	public void setLocation( Dockable dockable, Location location );
}
