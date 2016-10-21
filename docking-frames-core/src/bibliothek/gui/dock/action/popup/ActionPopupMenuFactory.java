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
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.MenuDockAction;
import bibliothek.gui.dock.control.PopupController;

/**
 * This interface is used by the {@link PopupController} to create new popup menus
 * when the user right clicks on some {@link Component}.
 * @author Benjamin Sigg
 */
public interface ActionPopupMenuFactory {
	/**
	 * Creates a new menu using <code>actions</code> as content. 
	 * @param owner the {@link Component} over which the menu is going to be seen
	 * @param dockable the {@link Dockable} for which the menu is shown
	 * @param actions the actions that are to be shown in the new menu
	 * @param source the object which is responsible for showing the menu, this could be
	 * a {@link DockElementRepresentative}, <code>dockable</code> itself, a {@link MenuDockAction},
	 * or another unspecified object, may be <code>null</code>
	 * @return the new popup menu or <code>null</code> if no menu should be shown for the given configuration
	 */
	public ActionPopupMenu createMenu( Component owner, Dockable dockable, DockActionSource actions, Object source );
}
