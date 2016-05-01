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
package bibliothek.gui.dock.station.stack.menu;

import bibliothek.gui.Dockable;

/**
 * A listener added to a {@link CombinedMenuContent}.
 * @author Benjamin Sigg
 */
public interface CombinedMenuContentListener {
	/**
	 * Called when <code>menu</code> is made visible.
	 * @param menu the source of the event
	 */
	public void opened( CombinedMenuContent menu );
	
	/**
	 * Called after <code>menu</code> was made invisible without making
	 * any selection.
	 * @param menu the source of the event
	 */
	public void canceled( CombinedMenuContent menu );
	
	/**
	 * Called after <code>menu</code> was made invisible with making
	 * a selection.
	 * @param menu the source of the event
	 * @param selection the selection made on the menu
	 */
	public void selected( CombinedMenuContent menu, Dockable selection );
}
