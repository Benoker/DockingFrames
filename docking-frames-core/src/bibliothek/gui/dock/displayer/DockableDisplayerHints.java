/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.displayer;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * Hints are used by components which are parents of {@link Dockable}s. Hints
 * tell the parent how to display its child, for example whether they should
 * paint a border around the child or not.<br>
 * There is no obligation for a parent to respect any hint.
 * @author Benjamin Sigg
 */
public interface DockableDisplayerHints {
	/**
	 * Gets the {@link DockStation} which is currently responsible for showing a
	 * {@link Dockable}.
	 */
	public DockStation getStation();
	
    /**
     * Tells whether to paint a border or not.
     * @param border <code>true</code> if the border should be painted,
     * <code>false</code> if not, <code>null</code> if the default setting
     * should be used
     */
    public void setShowBorderHint( Boolean border );
}
