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
package bibliothek.gui.dock.common.perspective.mode;

import bibliothek.gui.dock.common.perspective.CStationPerspective;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.perspective.PerspectiveDockable;

/**
 * Represents a {@link CStationPerspective} in a {@link LocationModeManagerPerspective}.
 * @author Benjamin Sigg
 */
public interface CModeAreaPerspective {
	/**
	 * Gets a unique identifier for this perspective.
	 * @return the unique identifier
	 */
	public String getUniqueId();
	
	/**
	 * Tells whether the item <code>dockable</code> is a direct child of this station
	 * and whether <code>dockable</code> is in a mode that matches the mode that is
	 * represented by this area.
	 * @param dockable some dockable that might be a child of this station
	 * @return <code>true</code> if <code>dockable</code> is a child of this station and if
	 * the mode of <code>dockable</code> matches the mode that is described by this
	 * object 
	 */
	public boolean isChild( PerspectiveDockable dockable );
	
	/**
	 * Tells whether <code>location</code> could be the location of a child dockable of 
	 * this area.
	 * @param location the location of some dockable
	 * @return whether <code>location</code> describes a valid location on this area
	 */
	public boolean isChildLocation( DockableProperty location );
}
