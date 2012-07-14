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
package bibliothek.gui.dock.control.relocator;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.StationDropItem;
import bibliothek.gui.dock.station.StationDropOperation;

/**
 * Contains information about a drop and drop operation that needs to be examined by
 * an {@link Inserter}.
 * @author Benjamin Sigg
 */
public interface InserterSource {
	/**
	 * Gets the {@link DockStation} which might be the next parent of {@link #getItem()}.
	 * @return the future parent, never <code>null</code>
	 */
	public DockStation getParent();
	
	/**
	 * Gets information about the item that is dropped.
	 * @return detailed information about the dropping {@link Dockable}
	 */
	public StationDropItem getItem();
	
	/**
	 * Gets the {@link StationDropOperation} that was created by {@link DockStation#prepareDrop(StationDropItem)},
	 * this might be <code>null</code> if the station was not yet asked or if the station does not
	 * accept the new child.
	 * @return the pending operation, can be <code>null</code>
	 */
	public StationDropOperation getOperation();
}
