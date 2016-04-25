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
package bibliothek.gui.dock.control.relocator;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.StationDropOperation;

/**
 * Describes the action that a {@link DefaultDockRelocator} will execute.
 * @author Benjamin Sigg
 */
public interface RelocateOperation {
	/**
	 * Gets the station which is the target of this operation
	 * @return the target, not <code>null</code>
	 */
	public DockStation getStation();
	
	/**
	 * Gets the operation that would be executed by the {@link #getStation() dock station}.
	 * @return the operation, not <code>null</code>
	 */
	public StationDropOperation getOperation();
	
	/**
	 * Informs this operation that it will never be used (again) and that it should release any
	 * resources it has acquired.
	 * @param next the operation that will be executed next, can be <code>null</code>
	 */
	public void destroy( RelocateOperation next );
	
	/**
	 * Gets the {@link Dockable}s whose parent will change due to this operation.
	 * @param selection the element that is moved around
	 * @return the items, must not be <code>null</code> but can be empty
	 */
	public Dockable[] getImplicit( Dockable selection );
	
	/**
	 * Executes this operation. This method must only call the methods 
	 * {@link VetoableDockRelocatorListener#dragging(DockRelocatorEvent) dragging} and
	 * {@link VetoableDockRelocatorListener#dragged(DockRelocatorEvent) dragged} of <code>listener</code>, all other
	 * methods will throw an {@link IllegalStateException}. 
	 * @param selection the element that is moved around
	 * @param listener a listener to be informed about events happening because of this operation
	 * @return <code>true</code> if the operation was a success, <code>false</code> if the operation was
	 * canceled
	 */
	public boolean execute( Dockable selection, VetoableDockRelocatorListener listener );
}
