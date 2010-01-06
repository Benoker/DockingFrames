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
package bibliothek.gui.dock.facile.mode.status;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.mode.LocationModeManager;

/**
 * Generic algorithm telling for {@link Dockable}s whether some {@link ExtendedMode} is
 * available or not.
 * @author Benjamin Sigg
 */
public interface ExtendedModeEnablement {
	/**
	 * Tells whether <code>mode</code> is available for <code>dockable</code>.<br>
	 * <b>Note:</b> for {@link ExtendedMode#NORMALIZED} the result must always be <code>true</code>.
	 * @param dockable some element, not <code>null</code>
	 * @param mode some mode, not <code>null</code>
	 * @return whether the mode is available, <code>true</code> if <code>mode</code> equals {@link ExtendedMode#NORMALIZED}
	 */
	public boolean isAvailable( Dockable dockable, ExtendedMode mode );
	
	/**
	 * Adds a listener to this enablement, the listener has be informed if the availability state of
	 * a mode in respect to a dockable has changed. Only {@link Dockable}s that are registered
	 * at the {@link LocationModeManager} have to be observed.
	 * @param listener the new listener
	 */
	public void addListener( ExtendedModeEnablementListener listener );
	
	/**
	 * Removes a listener from this enablement.
	 * @param listener the listener to remove
	 */
	public void removeListener( ExtendedModeEnablementListener listener );

	/**
	 * Informs this enablement that it is no longer of any use. The enablement
	 * should remove any listeners it added to any other object.
	 */
	public void destroy();
}
