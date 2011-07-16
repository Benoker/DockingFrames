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
package bibliothek.gui.dock.station.flap;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;

/**
 * A listener that is added to a {@link FlapLayoutManager}.
 * @author Benjamin Sigg
 */
public interface FlapLayoutManagerListener {
	/**
	 * This method is called by <code>manager</code> if the {@link FlapLayoutManagerListener#holdSwitchableChanged(FlapLayoutManager, FlapDockStation, Dockable)}
	 * method returns a new value.
	 * @param manager the source of the event
	 * @param station the station for which the value changed, <code>null</code> if the station is not specified
	 * @param dockable the element for which the value changed, <code>null</code> indicates that the property changed for an unknown number of dockables
	 */
	public void holdSwitchableChanged( FlapLayoutManager manager, FlapDockStation station, Dockable dockable );
}
