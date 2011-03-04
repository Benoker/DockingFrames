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
package bibliothek.gui.dock.common.group;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.LocationModeManager;

/**
 * A group movement describes the movement of an undefined number of {@link Dockable}s
 * by a {@link LocationModeManager}. The moved {@link Dockable}s have to be somehow in a group,
 * and that group has to be described by one {@link Dockable}. A logical choice would be that
 * all {@link Dockable}s are children of some {@link DockStation} and the {@link DockStation}
 * represents the entire group.
 * @author Benjamin Sigg
 */
public interface CGroupMovement {
	/**
	 * Executes this movement. While this operation runs, focus management is disabled. The 
	 * framework will choose a new focused {@link Dockable} once this method finished.<br>
	 * <b>Note:</b> While the various <code>apply</code>-methods of {@link LocationModeManager}
	 * only take one argument, more than one {@link Dockable} might be moved because of the 
	 * internal logic of some {@link LocationMode}s. Implementations should assume that any
	 * cached information about any {@link Dockable} is invalid once a {@link Dockable} has been
	 * moved.
	 * @param callback can be used by this object to freely move around any
	 * {@link Dockable}
	 */
	public void apply( CGroupBehaviorCallback callback );

	/**
	 * Tells some {@link DockAcceptance}s whether a check for <code>child</code> becoming a child of 
	 * <code>parent</code> needs to be performed. The default result of this method should be <code>true</code>. 
	 * A value of <code>true</code> does not prevent custom {@link DockAcceptance}s from preventing the
	 * operation.
	 * @param parent the future parent of <code>child</code>
	 * @param child the future child of <code>parent</code>
	 * @return <code>true</code> if this relation can be allowed without further checks
	 */
	public boolean forceAccept( DockStation parent, Dockable child );
}
