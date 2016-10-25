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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.support.mode.Mode;

/**
 * A listener that can be added to a {@link ModeArea}. The {@link ModeArea} must
 * inform this listener if one of its children changed the location.
 * @author Benjamin Sigg
 */
public interface ModeAreaListener {
	/**
	 * To be called if a set of {@link Dockable}s, which are children of the
	 * {@link ModeArea} <code>source</code>, changed their location such that
	 * their {@link Mode} might change.<br>
	 * <b>Note:</b> this method gets only called if {@link Dockable#getDockParent()}
	 * did not change. Only {@link ModeArea}s which represent more than one
	 * {@link Mode} are required to call this listener.
	 * @param source the source of the event
	 * @param dockables all the element which might have changed their mode
	 */
	public void internalLocationChange( ModeArea source, Set<Dockable> dockables );
}
