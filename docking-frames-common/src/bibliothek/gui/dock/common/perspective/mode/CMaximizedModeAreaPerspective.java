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

import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.util.Path;

/**
 * Represents a station which can contain "maximized" children, this
 * representation is handled by a {@link CMaximizedModePerspective}.
 * @author Benjamin Sigg
 */
public interface CMaximizedModeAreaPerspective extends CModeAreaPerspective{
	/**
	 * Sets the origin of the currently maximized child of this area. If this area can function without
	 * such information, then this method can just be ignored.
	 * @param mode the mode that was used, can be <code>null</code>
	 * @param location the exact location, must match <code>mode</code>, can be <code>null</code>
	 */
	public void setUnmaximize( Path mode, Location location );
	
	/**
	 * Gets the mode the currently maximized item should use when unmaximized.
	 * @return the unmaximize mode, can be <code>null</code>
	 */
	public Path getUnmaximizeMode();
	
	/**
	 * Gets the location the currently maximized item should use when unmaximized.
	 * @return the unmaximize location, can be <code>null</code>
	 */
	public Location getUnmaximizeLocation();
}
