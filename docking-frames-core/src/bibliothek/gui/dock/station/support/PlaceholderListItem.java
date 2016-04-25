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
package bibliothek.gui.dock.station.support;

import bibliothek.gui.Dockable;

/**
 * An item in a {@link PlaceholderList}.<br>
 * Clients should notice that there are no restrictions on what <code>D</code> actually is. While the default implementation
 * assumes that <code>D</code> is some kind of {@link Dockable}, it could as well be something else, e.g. a list of dockables. 
 * @author Benjamin Sigg
 * @param <D> the representation of a {@link Dockable}, may or may not be a subinterface of {@link Dockable}
 */
public interface PlaceholderListItem<D> {
	/**
	 * Gets the {@link Dockable} that is associated with this item.
	 * @return the dockable, not <code>null</code>
	 */
	public D asDockable();
}
