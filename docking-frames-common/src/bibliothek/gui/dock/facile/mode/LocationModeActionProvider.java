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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.support.mode.Mode;

/**
 * Is associated with one {@link LocationMode}, provides a (set of) actions
 * to set the mode of a {@link Dockable} to the owner of this provider.<br>
 * The actions provided by this interface are only shown if the mode
 * of a {@link Dockable} is not the same as the owner mode, and if the
 * owner mode is available to the element.
 * @author Benjamin Sigg
 */
public interface LocationModeActionProvider {
	/**
	 * Called if the element <code>dockable</code>, which is currently in 
	 * mode <code>mode</code>, should have some additional {@link DockAction}s
	 * related to the owner of this {@link LocationModeActionProvider}.
	 * @param dockable the element for which an action source is required
	 * @param currentMode the current mode of <code>dockable</code>
	 * @param currentSource the source that was returned by this method in the previous call. May
	 * be <code>null</code> either if this method returned <code>null</code> or was not yet
	 * called for <code>dockable</code>
	 * @return the source or <code>null</code> if the default value should be used
	 */
	public DockActionSource getActions( Dockable dockable, Mode<Location> currentMode, DockActionSource currentSource );
	
	/**
	 * Called if <code>dockable</code> is no longer in use and all references
	 * to <code>dockable</code> are to be removed.
	 * @param dockable the element which is no longer handled by the owning mode
	 * @param source the last result of {@link #getActions(Dockable, Mode, DockActionSource) getActions}, may be <code>null</code>
	 */
	public void destroy( Dockable dockable, DockActionSource source );
}
