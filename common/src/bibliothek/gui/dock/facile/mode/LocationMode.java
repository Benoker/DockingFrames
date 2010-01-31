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
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.common.mode.LocationModeManager;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.support.mode.Mode;

/**
 * A {@link Mode} that is used by the {@link LocationModeManager}. Since {@link DockStation}s
 * may be nested, most algorithms working with them have to be recursive. Some of the
 * methods of {@link LocationMode} have a slightly different semantic than their original.  
 */
public interface LocationMode extends Mode<Location>{
	/**
	 * Sets the manager which is using this mode.
	 * @param manager the manager or <code>null</code>
	 * @throws IllegalStateException if <code>manager</code> is not <code>null</code>
	 * and another manager is already set 
	 */
	public void setManager( LocationModeManager<?> manager );
	
	/**
	 * Connects this mode with a controller. It is up to the mode
	 * to add or remove listeners if necessary. This mode should
	 * also forward the controller to its {@link ModeArea}s.
	 * @param controller the new controller or <code>null</code>
	 */
	public void setController( DockController controller );
	
	/**
	 * Gets the manager which currently works with this mode.
	 * @return the owner or <code>null</code>
	 */
	public LocationModeManager<?> getManager();
	
	/**
	 * If this method is not able to clearly find out whether <code>dockable</code>
	 * has this mode, it returns <code>false</code>. The {@link LocationModeManager}
	 * will ask again with the parent station of <code>dockable</code>.
	 */
	public boolean isCurrentMode( Dockable dockable );
	
	/**
	 * Tells whether this mode knows <code>station</code> and represents the mode
	 * children of <code>station</code> are in. 
	 * @param station the station which is to be tested
	 * @return whether this mode is represented by <code>station</code>
	 */
	public boolean isRepresenting( DockStation station );
	
	/**
	 * Gets the one {@link DockStation} that is known with the root-id <code>uniqueId</code>.
	 * The <code>uniqueId</code> is the same id as used returned by {@link Location#getRoot()}
	 * of {@link Location}s issued by this mode.
	 * @param uniqueId the id of some station
	 * @return the station or <code>null</code>
	 */
	public DockStation getRepresentation( String uniqueId );
	
	/**
	 * Adds a listener to this mode. The listener is to be called
	 * if {@link #apply(Dockable, Location, AffectedSet) apply} starts or finishes.
	 * @param listener the new listener
	 */
	public void addLocationModeListener( LocationModeListener listener );
	
	/**
	 * Removes <code>listener</code> from this mode.
	 * @param listener the listener to remove
	 */
	public void removeLocationModeListener( LocationModeListener listener );
	
	/**
	 * Gets the unique identifier of this mode.
	 * @return the unique identifier
	 */
	public ExtendedMode getExtendedMode();
	
	/**
	 * Tells whether {@link Dockable}s which have this mode applied should
	 * respect the settings for {@link CWorkingArea}s.
	 * @param station the station which is the parent of the {@link Dockable}s
	 * @return <code>true</code> if the settings should be respected, <code>false</code>
	 * otherwise
	 */
	public boolean respectWorkingAreas( DockStation station );
	
	/**
	 * Ensures that no {@link Dockable} that has this mode hides <code>dockable</code>.
	 * Note that <code>dockable</code> may or may not be in this mode.
	 * @param dockable the element which must not be hidden
	 */
	public void ensureNotHidden( Dockable dockable );
}