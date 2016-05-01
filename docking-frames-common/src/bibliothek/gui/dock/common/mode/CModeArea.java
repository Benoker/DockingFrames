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
package bibliothek.gui.dock.common.mode;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.ModeArea;

/**
 * Describes a {@link ModeArea} that can work with {@link CLocation}s.
 * @author Benjamin Sigg
 *
 */
public interface CModeArea extends ModeArea{
	/**
	 * Assuming <code>dockable</code> is a child of this area, returns
	 * the location of <code>dockable</code>.
	 * @param dockable some child
	 * @return the location, <code>null</code> if not found
	 */
	public CLocation getCLocation( Dockable dockable );
	
	/**
	 * Assuming <code>dockable</code> would be at location <code>location</code>
	 * if it would be a child of this station, returns the {@link CLocation} that
	 * matches <code>location</code>.
	 * @param dockable some element which may or may not be a child of this station
	 * @param location the location <code>dockable</code> would have if it would
	 * be a child of this station
	 * @return the location, <code>null</code> if not found
	 */
	public CLocation getCLocation( Dockable dockable, Location location );
	
	/**
	 * Tells whether children of this area have to respect the settings for
	 * {@link CWorkingArea}s.
	 * @return whether the settings are to be respected
	 */
	public boolean respectWorkingAreas();
}
