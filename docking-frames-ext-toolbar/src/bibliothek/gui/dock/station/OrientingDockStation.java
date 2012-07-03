/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.station;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;

/**
 * A {@link DockStation} where the children have an orientation.
 * 
 * @author Benjamin Sigg
 */
public interface OrientingDockStation extends DockStation{
	/**
	 * Tells what orientation <code>child</code> has.
	 * @param child a child of this station
	 * @return the orientation, never <code>null</code>
	 * @throws IllegalArgumentException if <code>child</code> is not a child
	 */
	public Orientation getOrientationOf( Dockable child );

	/**
	 * Adds the observer <code>listener</code> to this station. The observer
	 * receives an event if the orientation of a child of this station changed.
	 * The observer may or may not receive an event upon dropping a new
	 * {@link Dockable} onto this station.
	 * @param listener the new observer, not <code>null</code>
	 */
	public void addOrientingDockStationListener( OrientingDockStationListener listener );

	/**
	 * Removes the observer <code>listener</code> from this station.
	 * @param listener the listener to remove
	 */
	public void removeOrientingDockStationListener( OrientingDockStationListener listener );
}
