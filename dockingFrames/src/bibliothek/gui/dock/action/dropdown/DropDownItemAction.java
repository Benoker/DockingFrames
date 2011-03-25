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

package bibliothek.gui.dock.action.dropdown;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.themes.basic.action.dropdown.DropDownViewItem;

/**
 * An action that can be child of a {@link DropDownAction}. The properties
 * of this action are read by the view, for example if this action is wrapped into
 * a {@link DropDownViewItem}.
 * @author Benjamin Sigg
 */
public interface DropDownItemAction {
	/**
	 * Tells whether this action can be selected by a {@link DropDownAction},
	 * if it is shown for <code>dockable</code>.
	 * @param dockable the Dockable for which the action is shown
	 * @return <code>true</code> if the action can be selected
	 */
	public boolean isDropDownSelectable( Dockable dockable );

	/**
	 * Tells whether this action can be triggered if it is shown as child of
	 * a {@link DropDownAction}.
	 * @param dockable the Dockable for which the action is shown
	 * @param selected <code>true</code> if the action is selected (the action
	 * is triggered because the user clicks onto the {@link DropDownAction}),
	 * or <code>false</code> if this action is just in a menu.
	 * @return <code>true</code> if the action can be triggered
	 */
	public boolean isDropDownTriggerable( Dockable dockable, boolean selected );
}
