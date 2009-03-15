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
package bibliothek.gui.dock.util.local;

import java.util.Locale;

import bibliothek.gui.DockUI;

/**
 * This listener can be added to a {@link DockUI} and will be informed if
 * the {@link Locale} changes.
 * @author Benjamin Sigg
 *
 */
public interface LocaleListener {
	/**
	 * Called when the {@link Locale} has changed.
	 * @param ui the caller
	 */
	public void localeChanged( DockUI ui );
	
	/**
	 * Called when the language bundle has changed, the language
	 * bundle may change because of a new locale or because the client
	 * directly sets the bundle.
	 * @param ui the caller
	 */
	public void bundleChanged( DockUI ui );
}
