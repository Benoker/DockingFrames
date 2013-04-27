/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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
package bibliothek.gui.dock.station.stack;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;

/**
 * Request forwarded to a {@link DndAutoSelectStrategy} if a potential drop event was detected.
 * @author Benjamin Sigg
 */
public interface DndAutoSelectStrategyRequest {
	/**
	 * Gets the station which detected the event.
	 * @return the source of the event, never <code>null</code>
	 */
	public StackDockStation getStation();
	
	/**
	 * Gets the {@link Dockable} over whose tab the mouse currently hovers.
	 * @return the tab beneath the mouse, never <code>null</code>
	 */
	public Dockable getDockable();
	
    /**
     * Returns the data flavors for this transfer.
     * @return the data flavors for this transfer
     */
	public DataFlavor[] getDataFlavors();

    /**
     * Returns the <code>Transferable</code> associated with this transfer.
     * @return the <code>Transferable</code> associated with this transfer
     */
	public Transferable getTransferable();
	
	/**
	 * Selects the {@link Dockable} beneath the mouse, moves it to the front and tries to focus it.
	 */
	public void toFront();
}
