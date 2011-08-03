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
package bibliothek.gui.dock.common;

/**
 * This listener is added to a {@link CStationContainer} and receives an event if
 * either {@link CStation}s are added or removed from the container.
 * @author Benjamin Sigg
 */
public interface CStationContainerListener {
	/**
	 * Called after <code>station</code> has been added to <code>source</code>.
	 * @param source the source of the event
	 * @param station the newly added station
	 */
	public void added( CStationContainer source, CStation<?> station );
	
	/**
	 * Called after <code>station</code> has been removed from <code>source</code>.
	 * @param source the source of the event
	 * @param station the station that was removed
	 */
	public void removed( CStationContainer source, CStation<?> station );
}
