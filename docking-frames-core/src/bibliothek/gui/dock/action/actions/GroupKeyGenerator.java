/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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

package bibliothek.gui.dock.action.actions;

import bibliothek.gui.Dockable;

/**
 * A converter creating keys for {@link Dockable}s. Several {@link Dockable}s can
 * have the same key. Used by a {@link GroupedDockAction} to build groups of {@link Dockable}s
 * (in each group all <code>Dockable</code>s have the same key).
 * 
 * @author Benjamin Sigg
 *
 * @param <K> the type of key generate by this class
 */
public interface GroupKeyGenerator<K> {
	/**
	 * Generates a new key. Every value except <code>null</code> is a valid
	 * result.
	 * @param dockable the dockable for which a key is requested.
	 * @return the new key
	 */
	public K generateKey( Dockable dockable );
}
