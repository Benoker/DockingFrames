/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2015 Benjamin Sigg
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
package bibliothek.gui.dock.common.grouping;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.group.CGroupBehavior;
import bibliothek.gui.dock.common.mode.ExtendedMode;

/**
 * {@link CGroupingBehavior} allows clients to define how {@link Dockable}s organize themselves 
 * in groups. A {@link CGroupingBehavior} is able to rewrite the location of {@link Dockable}s,
 * hence every time the user changes the {@link ExtendedMode} of a {@link Dockable} the 
 * grouping behavior can put the {@link Dockable} at a new location.<br>
 * There is a distinction between {@link CGroupingBehavior} and {@link CGroupBehavior}: this class
 * is all about bringing groups together, while {@link CGroupBehavior} defines how groups of
 * {@link Dockable}s move around together.
 * @author Benjamin Sigg
 */
public interface CGroupingBehavior {
	/**
	 * Gets the grouping algorithm that should be used for <code>dockable</code>. This method
	 * may be called multiple times for the same <code>dockable</code>. It should either always
	 * return the same {@link DockableGrouping}, or it should return an object that does not
	 * contains any state.
	 * @param dockable the element whose grouping information is requested
	 * @return the grouping information, or <code>null</code> if there is no special behavior
	 * defined for <code>dockable</code>
	 */
	public DockableGrouping getGrouping( Dockable dockable );
}
