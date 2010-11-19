/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.themes;

import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;

/**
 * An item that is used by {@link DockStation}s to change part of their 
 * look and feel depending on the current {@link DockTheme}.
 * @author Benjamin Sigg
 * @param <T> the kind of item this factory creates
 */
public interface StationThemeItem<T> {
	/**
	 * Gets the item in a form that is usable by <code>station</code>.
	 * @param station some station, not <code>null</code>
	 * @return the item that is to be used by <code>station</code>, maybe a new
	 * object or an object that is used many times, or <code>this</code>. 
	 * Whether <code>null</code> or not is allowed depends on the type of <code>T</code> and
	 * the use of this item.
	 */
	public T get( DockStation station );
}
