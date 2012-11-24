/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.common.util;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.station.CommonDockStation;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A set of utility methods usefull in the Common project
 * @author Benjamin Sigg
 * @see DockUtilities
 */
public class CDockUtilities extends DockUtilities{
	/**
	 * Starting with <code>station</code>, and traversing the tree upwards, searches
	 * the first {@link CStation} which is a {@link CStation#isWorkingArea() working area}. 
	 * @param station the station on which to start the search
	 * @return the first working area (closest to <code>station</code>)
	 */
	public static CStation<?> getFirstWorkingArea( CStation<?> station ){
		DockStation parent = station.getStation();
		while( parent != null ){
			if( parent instanceof CommonDockStation<?, ?> ){
				CStation<?> cstation = ((CommonDockStation<?, ?>)parent).getStation();
				if( cstation.isWorkingArea() ){
					return cstation;
				}
			}
			Dockable child = parent.asDockable();
			parent = child == null ? null : child.getDockParent();
		}
		return null;
	}
}
