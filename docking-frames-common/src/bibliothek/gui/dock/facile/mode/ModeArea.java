/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.facile.mode;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CStation;

/**
 * A representation of some area that can show {@link Dockable}s,
 * it is a wrapper for a {@link DockStation}.
 * @author Benjamin Sigg
 */
public interface ModeArea {
	/**
	 * Gets a unique identifier for this area.
	 * @return the unique identifier
	 * @see CStation#getUniqueId()
	 */
	public String getUniqueId();
	
	/**
	 * Tells whether this {@link ModeArea} can automatically become
	 * the default area of some mode.
	 * @return <code>true</code> if this can be a default area, <code>false</code> if not
	 */
	public boolean autoDefaultArea();
	
	/**
	 * Tells whether this area can be used as root in a {@link Location}. An area that is not
	 * a location root will (usually) not be used to set the location of a child.<br>
	 * Most implementations of {@link ModeArea} should return <code>true</code>.
	 * @return whether this {@link ModeArea} agrees on seeing its {@link #getUniqueId()} in a {@link Location}
	 */
	public boolean isLocationRoot();
	
	/**
	 * Tells whether <code>dockable</code> is a direct child of this station.
	 * @param dockable some element
	 * @return <code>true</code> if and only if the parent of <code>dockable</code>
	 * is identical to this station
	 */
	public boolean isChild( Dockable dockable );
	
	/**
	 * Gets the station which is represented by this area.
	 * @return the station, not <code>null</code>
	 */
	public DockStation getStation();
	
	/**
	 * Connects this area with a controller. It's up to the area to
	 * add or remove listeners if necessary.
	 * @param controller the controller or <code>null</code>
	 */
	public void setController( DockController controller );
	
	/**
	 * Informs this area about the mode that uses it.
	 * @param mode the owner of this area, can be <code>null</code>
	 */
	public void setMode( LocationMode mode );
	
	/**
	 * Adds a listener to this area.
	 * @param listener the new listener
	 */
	public void addModeAreaListener( ModeAreaListener listener );
	
	/**
	 * Removes a listener from this area.
	 * @param listener the listener to remove
	 */
	public void removeModeAreaListener( ModeAreaListener listener );
}
