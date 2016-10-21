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
package bibliothek.gui.dock.control.relocator;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.DockRelocator;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.StationDropOperation;

/**
 * An algorithm used during drag and drop to merge two {@link DockStation}s. The {@link Merger} usually is not
 * accessed by the involved {@link DockStation}s or {@link Dockable}s directly, rather it is accessed
 * by the {@link DockRelocator} once a {@link StationDropOperation} has been found.
 * @author Benjamin Sigg
 * @see Combiner
 * @see Inserter
 */
public interface Merger {
	/**
	 * Checks whether {@link #merge(StationDropOperation, DockStation, DockStation)} will succeed or not. This
	 * method will only be called if <code>parent</code> would accept all children
	 * of <code>child</code>. This method may be called during "drop" and during "move" operations.
	 * @param operation the operation that would be executed, can be <code>null</code>
	 * @param parent an existing station
	 * @param child a station that is dragged around and may be dropped onto <code>parent</code>
	 * @return whether {@link #merge(StationDropOperation, DockStation, DockStation)} will succeed
	 */
	public boolean canMerge( StationDropOperation operation, DockStation parent, DockStation child );
	
	/**
	 * Moves all children of <code>child</code> to <code>parent</code>, leaving <code>child</code>
	 * empty. This method is only called if {@link #canMerge(StationDropOperation, DockStation, DockStation) canMerge}
	 * returned <code>true</code> and if the <code>accept</code>-methods allow the operation.<br>
	 * If the parent of <code>child</code> is not <code>parent</code>, then <code>child</code> will be removed
	 * from its parent. Otherwise the child remains, allowing this {@link Merger} to reuse it.
	 * @param operation the operation that would be executed, can be <code>null</code>
	 * @param parent the new parent of the children
	 * @param child the station to dissolve
	 */
	public void merge( StationDropOperation operation, DockStation parent, DockStation child );
}
