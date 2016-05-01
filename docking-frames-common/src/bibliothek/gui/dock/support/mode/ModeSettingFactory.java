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
package bibliothek.gui.dock.support.mode;

import bibliothek.gui.dock.facile.mode.LocationModeManager;
import bibliothek.util.Path;

/**
 * A factory creating new {@link ModeSetting}s.
 * @author Benjamin Sigg
 * @param <A> the kind of data used by the {@link LocationModeManager} to store
 * information
 */
public interface ModeSettingFactory<A> {
	/**
	 * Gets the unique identifier of the {@link Mode} which uses the {@link ModeSetting}
	 * of this factory.
	 * @return the unique identifier, not <code>null</code>
	 */
	public Path getModeId();
	
	/**
	 * Creates a new, empty {@link ModeSetting}.
	 * @return the new setting or <code>null</code> to indicate that there is nothing to store
	 */
	public ModeSetting<A> create();
}
