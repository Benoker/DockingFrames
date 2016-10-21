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
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.control.DockRelocator;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;

/**
 * The {@link Inserter} is used by the {@link DockRelocator} to build and execute
 * {@link StationDropOperation}s independent from the involved {@link DockStation}s and {@link Dockable}s. The
 * {@link Inserter} is a very powerful interface, as it can completely override any decision
 * that was made by a {@link DockStation}.<br>
 * An {@link Inserter} does not have access to the internals of a {@link DockStation}, nor does any {@link DockStation}
 * know of the existence of the {@link Inserter}. Some station may however offer special methods to create
 * fitting {@link StationDropOperation}s.
 * 
 * @author Benjamin Sigg
 */
public interface Inserter {
	/**
	 * This method is called if {@link DockStationDropLayer#contains(int, int)} confirmed that a point belongs
	 * to the layer, but before {@link DockStation#prepareDrop(bibliothek.gui.dock.station.StationDropItem)} was invoked.
	 * @param source information about the current position of the mouse, the invoked {@link DockElement}s and
	 * other things related to a drag and drop operation.
	 * @return a value of <code>null</code> if this {@link Inserter} is not interested in the event, a value
	 * not <code>null</code> will override {@link DockStation#prepareDrop(bibliothek.gui.dock.station.StationDropItem)} (the
	 * method will never be called), in this case {@link #after(InserterSource)} is not called either.
	 */
	public StationDropOperation before( InserterSource source );
	
	/**
	 * This method is called after {@link DockStation#prepareDrop(bibliothek.gui.dock.station.StationDropItem)} was executed, the
	 * method is called in any case independent of whether <code>prepareDrop</code> returned a {@link StationDropOperation}
	 * or not.
	 * @param source information about the current position of the mouse, the invoked {@link DockElement}s and
	 * other things related to a drag and drop operation. 
	 * @return a value of <code>null</code> if this {@link Inserter} is not interested in the event, a value
	 * not <code>null</code> will override the result of {@link DockStation#prepareDrop(bibliothek.gui.dock.station.StationDropItem)}
	 */
	public StationDropOperation after( InserterSource source );
}
