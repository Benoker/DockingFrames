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

import java.util.Set;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.layout.location.AsideRequest;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.support.mode.Mode;

/**
 * A {@link Mode} that is used by the {@link LocationModeManager}. A {@link LocationMode}
 * represents a state {@link Dockable}s are in depending on their current location in the tree
 * of {@link DockStation}s and {@link Dockable}s. Since {@link DockStation}s
 * may be nested, most algorithms working with them have to be recursive. Some of the
 * methods of {@link LocationMode} have a slightly different semantic than specified in the
 * {@link Mode} interface.  
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
	 * Calls {@link DockStation#aside(AsideRequest)} on a station that matches <code>location</code>.
	 * @param request the request to forward to a {@link DockStation} or to process
	 * @param location the source of the location whose neighbor is searched
	 * @return the neighbor or a value of <code>null</code> if the request could not be processed
	 */
	public Location aside( AsideRequest request, Location location );
	
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
	 * Gets all the unique identifiers that will yield a non-<code>null</code> result when calling {@link #getRepresentation(String)}. 
	 * @return the ids of all the available {@link DockStation}s, the result may not be modifiable
	 */
	public Set<String> getRepresentationIds();
	
	/**
	 * Adds a listener to this mode. The listener is to be called
	 * if {@link Mode#apply(Dockable, Object, AffectedSet) apply} starts or finishes.
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
	 * Ensures that no {@link Dockable} that has this mode hides <code>dockable</code>.
	 * Note that <code>dockable</code> may or may not be in this mode.
	 * @param dockable the element which must not be hidden
	 */
	public void ensureNotHidden( Dockable dockable );
	
	/**
	 * Tells the {@link LocationModeManager} whether the focus should be transferred to
	 * a {@link Dockable} that has this mode.
	 * @return whether auto-focus should be enabled or not
	 */
	public boolean shouldAutoFocus();
}