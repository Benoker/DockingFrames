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

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.gui.dock.common.perspective.mode.LocationModePerspective;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationMode;

/**
 * {@link LocationMode} offering methods to work with {@link CLocation}.
 * @author Benjamin Sigg
 */
public interface CLocationMode extends LocationMode{
	/**
	 * Gets the location of <code>dockable</code> which is in this mode.
	 * @param dockable the element whose location is asked
	 * @return the location or <code>null</code> if it cannot be determined
	 */
	public CLocation getCLocation( Dockable dockable );
	
	/**
	 * Gets the location of <code>dockable</code> which might or might not
	 * be a child of this mode. 
	 * @param dockable the element
	 * @param location the location <code>dockable</code> is supposed to be in
	 * if in this mode.
	 * @return the location or <code>null</code> if it cannot be determined
	 */
	public CLocation getCLocation( Dockable dockable, Location location );
	
	/**
	 * Tells whether this mode is a basic mode or not. There are basic
	 * and advanced modes. A basic and an advanced mode may share the same
	 * representation (the same {@link DockStation}s), but if in doubt the
	 * basic mode has more weight.
	 * @return <code>true</code> if this is a basic mode
	 */
	public boolean isBasicMode();
		
	/**
	 * Tells whether {@link Dockable}s which have this mode applied should
	 * respect the settings for {@link CWorkingArea}s.
	 * @param station the station which is the parent of the {@link Dockable}s
	 * @return <code>true</code> if the settings should be respected, <code>false</code>
	 * otherwise
	 */
	public boolean respectWorkingAreas( DockStation station );
	
	/**
	 * Gets an object that represents this type of mode in a {@link CPerspective}.
	 * @return the perspective version of <code>this</code>
	 */
	public LocationModePerspective createPerspective();
}
