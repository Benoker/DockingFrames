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

import bibliothek.gui.dock.support.mode.Mode;


/**
 * A listener added to a {@link LocationMode}. The listener gets informed
 * if the {@link Mode#apply(bibliothek.gui.Dockable, Object, bibliothek.gui.dock.support.mode.AffectedSet) apply}
 * method is called.
 * @author Benjamin Sigg
 */
public interface LocationModeListener {
	/**
	 * Called by a {@link LocationMode} right before the 
	 * {@link Mode#apply(bibliothek.gui.Dockable, Object, bibliothek.gui.dock.support.mode.AffectedSet) apply}
	 * method starts.
	 * @param event detailed information about the event
	 */
	public void applyStarting( LocationModeEvent event );

	/**
	 * Called by a {@link LocationMode} after the 
	 * {@link Mode#apply(bibliothek.gui.Dockable, Object, bibliothek.gui.dock.support.mode.AffectedSet) apply}
	 * method has done its work.
	 * @param event detailed information about the event
	 */
	public void applyDone( LocationModeEvent event );
}
