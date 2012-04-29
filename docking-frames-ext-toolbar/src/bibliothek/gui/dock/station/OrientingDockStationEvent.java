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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;

/**
 * An event fired by an {@link OrientedDockStation} if one or many children
 * changed their orientation.
 * 
 * @author Benjamin Sigg
 */
public class OrientingDockStationEvent{
	/** the source of the event */
	private final OrientingDockStation station;
	/** the children whose {@link Orientation} may have changed */
	private final Set<Dockable> children = new HashSet<Dockable>();

	/**
	 * Creates a new event, this is equivalent of calling
	 * {@link #OrientingDockStationEvent(OrientingDockStation, Dockable[])} with
	 * the <code>children</code> array set to <code>null</code>
	 * 
	 * @param station
	 *            the source of the event
	 */
	public OrientingDockStationEvent( OrientingDockStation station ){
		this(station, null);
	}

	/**
	 * Creates a new event.
	 * 
	 * @param station
	 *            the source of the event
	 * @param children
	 *            the affected children or <code>null</code>, if
	 *            <code>null</code> then all children of <code>station</code>
	 *            are affected
	 */
	public OrientingDockStationEvent( OrientingDockStation station,
			Dockable[] children ){
		this.station = station;

		if (children == null){
			for (int i = 0, n = station.getDockableCount(); i < n; i++){
				this.children.add(station.getDockable(i));
			}
		} else{
			for (final Dockable child : children){
				this.children.add(child);
			}
		}
	}

	/**
	 * Gets the source of the event.
	 * 
	 * @return the source
	 */
	public OrientingDockStation getStation(){
		return station;
	}

	/**
	 * Tells whether <code>dockable</code> was affected by the event.
	 * 
	 * @param dockable
	 *            some dockable which may or may not be affected
	 * @return <code>true</code> if <code>dockable</code> may have changed its
	 *         orientation
	 */
	public boolean isAffected( Dockable dockable ){
		return children.contains(dockable);
	}

	/**
	 * Gets all affected children, the collection is not modifiable.
	 * 
	 * @return all affected {@link Dockable}s
	 */
	public Collection<Dockable> getChildren(){
		return Collections.unmodifiableCollection(children);
	}
}
