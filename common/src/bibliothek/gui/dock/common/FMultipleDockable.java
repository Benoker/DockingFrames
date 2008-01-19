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
package bibliothek.gui.dock.common;

import bibliothek.gui.dock.common.intern.FDockable;

/**
 * A <code>FMultipleDockable</code> is a {@link FDockable} which can have
 * many copies in an {@link FControl}. A {@link FMultipleDockable} can
 * be added or removed from an {@link FControl} at any time.<br>
 * <ul>
 * <li>If a {@link FControl} loads a layout, all {@link FMultipleDockable}s are removed and new instances
 * are created using a set of {@link FMultipleDockableFactory}s</li>
 * <li>Many copies of one {@link FMultipleDockable} can be part of a {@link FControl}</li>
 * <li>When saving a layout, the contents of a {@link FMultipleDockable} are written out by a {@link FMultipleDockableFactory}</li>
 * </ul>
 * @author Benjamin Sigg
 */
public interface FMultipleDockable extends FDockable{
	/**
	 * Gets the factory that created this dockable.
	 * @return the factory, not <code>null</code>
	 */
	public FMultipleDockableFactory getFactory();
	
	/**
	 * Tells whether this {@link FMultipleDockable} should be removed from the
	 * {@link FControl} when it is made invisible. If in doubt, return
	 * <code>true</code>.
	 * @return <code>true</code> if this dockable should be removed from
	 * the controller when made invisible.
	 */
	public boolean isRemoveOnClose();
}
