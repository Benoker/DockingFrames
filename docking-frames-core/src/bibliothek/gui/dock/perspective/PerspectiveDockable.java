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
package bibliothek.gui.dock.perspective;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.util.Path;

/**
 * A representation of a {@link Dockable} in a {@link Perspective}.
 * @author Benjamin Sigg
 */
public interface PerspectiveDockable extends PerspectiveElement, PlaceholderListItem<PerspectiveDockable>{
	/**
	 * Gets the placeholder which is associated with this {@link Dockable}.
	 * @return the placeholder, can be <code>null</code>
	 */
	public Path getPlaceholder();

	/**
	 * Gets the parent {@link DockStation} of this {@link Dockable}.
	 * @return the parent, may be <code>null</code>
	 */
	public PerspectiveStation getParent();
	
	/**
	 * Sets the parent {@link DockStation} of this {@link Dockable}.
	 * @param parent the new parent, can be <code>null</code>
	 */
	public void setParent( PerspectiveStation parent );
}
