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

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.common.mode.CLocationMode;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.support.mode.ModeSetting;

/**
 * Represents a {@link CLocationMode} in a perspective.
 * @author Benjamin Sigg
 */
public interface LocationModePerspective {
	/**
	 * Informs this mode of what perspective is going to use it.
	 * @param perspective the perspective that uses this mode
	 */
	public void setPerspective( CPerspective perspective );
	
	/**
	 * Gets the unique identifier for this mode.
	 * @return the unique identifier, not <code>null</code>
	 */
	public ExtendedMode getIdentifier();
	
	/**
	 * Checks whether <code>dockable</code> currently is in <code>this</code> mode.
	 * @param dockable the element whose mode is searched
	 * @return <code>true</code> if this mode describes the situation of <code>dockable</code>
	 */
	public boolean isCurrentMode( PerspectiveDockable dockable );
	
	/**
	 * Checks whether the dockable at location <code>root</code>/<code>location</code>
	 * should be in the mode represented by <code>this</code>.
	 * @param root the unique identifer of the root {@link DockStation}
	 * @param location the location on the root station
	 * @return whether a dockable in this mode can have the described location 
	 */
	public boolean isCurrentMode( String root, DockableProperty location );
	
	/**
	 * Reads settings belonging to this mode from <code>setting</code>.
	 * @param setting some settings
	 */
	public void readSetting( ModeSetting<Location> setting );
	
	/**
	 * Writes settings that belong to this mode to <code>setting</code>.
	 * @param setting the settings to write
	 */
	public void writeSetting( ModeSetting<Location> setting );
}
