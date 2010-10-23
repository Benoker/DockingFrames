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
 * A <code>SingleCDockable</code> is a dockable which is added once to
 * the {@link CControl} and remains there until it is removed by the application.
 * A <code>SingleCDockable</code> is never removed automatically from the
 * {@link CControl}.<br>
 * <ul>
 * <li>If a {@link SingleCDockable} is missing when the {@link CControl} loads a layout, then a new <code>Dockable</code> can be created using
 * a {@link SingleCDockableFactory}. If no such factory is present, no <code>Dockable</code> is shown</li>
 * <li>No two {@link SingleCDockable} can have the same id if they are added to the same {@link CControl}</li>
 * <li>When saving a layout, only the unique id of a {@link SingleCDockable} is written out. Everything else has
 * to be saved by the client itself.</li>
 * </ul>
 * @author Benjamin Sigg
 */
public interface SingleCDockable extends CDockable{
	
	/**
	 * Gets the id of this dockable. The id is unique if among all dockables
	 * which are added to the same {@link CControl}.
	 * @return the unique id
	 */
	public String getUniqueId();
}
