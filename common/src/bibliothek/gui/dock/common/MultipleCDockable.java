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

import bibliothek.gui.dock.common.intern.CDockable;

/**
 * A <code>MultipleCDockable</code> is a {@link CDockable} which can have
 * many copies in an {@link CControl}. A {@link MultipleCDockable} can
 * be added or removed from an {@link CControl} at any time.<br>
 * <ul>
 * <li>If a {@link CControl} loads a layout, all {@link MultipleCDockable}s are removed and new instances
 * are created using a set of {@link MultipleCDockableFactory}s</li>
 * <li>Many copies of one {@link MultipleCDockable} can be part of a {@link CControl}</li>
 * <li>When saving a layout, the contents of a {@link MultipleCDockable} are written out by a {@link MultipleCDockableFactory}</li>
 * </ul>
 * @author Benjamin Sigg
 */
public interface MultipleCDockable extends CDockable{
	/**
	 * Gets the factory that created this dockable.
	 * @return the factory, must not be <code>null</code>
	 */
	public MultipleCDockableFactory<?,?> getFactory();
	
	/**
	 * Tells whether this {@link MultipleCDockable} should be removed from the
	 * {@link CControl} when it is made invisible. If in doubt, return
	 * <code>true</code>.
	 * @return <code>true</code> if this dockable should be removed from
	 * the controller when made invisible.
	 */
	public boolean isRemoveOnClose();
}
