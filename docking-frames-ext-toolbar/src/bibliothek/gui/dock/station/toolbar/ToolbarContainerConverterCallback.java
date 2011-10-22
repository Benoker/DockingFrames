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

package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.Dockable;
import bibliothek.gui.Position;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.util.FrameworkOnly;

/**
 * An interface allowing a {@link ToolbarContainerConverter} to call some internal methods of {@link ToolbarContainerDockStation}.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public interface ToolbarContainerConverterCallback {
	/**
	 * Creates a new {@link StationChildHandle} for <code>dockable</code>, the new handle
	 * must not be added to any collection.
	 * @param area the area in which <code>dockable</code> will appear
	 * @param dockable the element to wrap
	 * @return the new {@link StationChildHandle}, must not be <code>null</code>
	 */
	public StationChildHandle wrap( Position area, Dockable dockable );
	
	/**
	 * Called before <code>dockable</code> is added to the list of dockables.
	 * @param area the side at which <code>dockable</code> is added
	 * @param dockable the new element, not <code>null</code>
	 */
	public void adding( Position area, StationChildHandle dockable );

	/**
	 * Called after <code>dockable</code> was added to the list of dockables.
	 * @param area the side at which <code>dockable</code> is added
	 * @param dockable the new element, not <code>null</code>
	 */
	public void added( Position area, StationChildHandle dockable );
	
	/**
	 * Replaces the list of dockables at <code>area</code>.
	 * @param area the affected position
	 * @param list the new list of dockables, not <code>null</code>
	 */
	public void setDockables( Position area, DockablePlaceholderList<StationChildHandle> list );
	
	/**
	 * Called after {@link #setDockables(Position, DockablePlaceholderList)} and after all children have
	 * been stored in <code>list</code>.
	 * @param list the list whose creation was finished
	 */
	public void finished( DockablePlaceholderList<StationChildHandle> list );
}