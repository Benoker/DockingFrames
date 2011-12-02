/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.action.popup;

import java.awt.Component;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.MenuDockAction;

/**
 * A menu that shows some {@link DockAction}s and is opened when the user 
 * performs a right click onto a {@link DockElementRepresentative} or invokes
 * a {@link MenuDockAction}.
 * @author Benjamin Sigg
 */
public interface ActionPopupMenu {
	/**
	 * Gets the {@link Dockable} for which this menu is shown.
	 * @return the owner of this menu
	 */
	public Dockable getDockable();
	
	/**
	 * Opens this menu assuming the mouse is currently over <code>owner</code>
	 * at location <code>x/y</code>.
	 * @param owner the {@link Component} over which the menu should appear
	 * @param x the x coordinate of the position
	 * @param y the y coordinate of the position
	 */
	public void show( Component owner, int x, int y );
	
	/**
	 * Adds a listener to this menu, the listener is to be informed if
	 * the menu closes.
	 * @param listener the new listener
	 */
	public void addListener( ActionPopupMenuListener listener );
	
	/**
	 * Removes a listener from this menu.
 	 * @param listener the listener to remove
	 */
	public void removeListener( ActionPopupMenuListener listener );
}
