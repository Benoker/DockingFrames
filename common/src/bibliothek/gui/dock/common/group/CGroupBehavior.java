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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.mode.LocationMode;

/**
 * A {@link CGroupBehavior} allows to define groups of {@link CDockable}. Groups normaly
 * act together when changing the {@link LocationMode}: e.g. if one {@link CDockable} is minimized, all the other 
 * {@link CDockable}s follow.
 * @author Benjamin Sigg
 */
public interface CGroupBehavior {
	/**
	 * Changes the mode of <code>dockable</code> such that it matches <code>target</code>. This method may also change
	 * the {@link ExtendedMode} or location of other {@link Dockable}s to keep the group together. While this
	 * method runs, focus management is disabled. The focus will be transfered to <code>dockable</code> if
	 * <code>target</code> represents a {@link LocationMode} that requires focus transfer.
	 * {@link Dockable}s to change their mode. 
	 * @param dockable the element that was clicked by the user
	 * @param target the extended mode intended for <code>dockable</code>
	 * @param callback a set of information and methods that may be needed to apply all the necessary changes to
	 * <code>dockable</code> and maybe other {@link Dockable}s as well
	 */
	public void forward( Dockable dockable, ExtendedMode target, CGroupBehaviorCallback callback );
}
