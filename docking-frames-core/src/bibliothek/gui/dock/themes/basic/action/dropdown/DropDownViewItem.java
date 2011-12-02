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

package bibliothek.gui.dock.themes.basic.action.dropdown;

import javax.swing.JComponent;

import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.dropdown.DropDownView;
import bibliothek.gui.dock.themes.basic.action.menu.MenuViewItem;

/**
 * An item that is shown in the menu of a drop-down-button and can be
 * selected by the button. Normally a {@link DropDownViewItem} is a 
 * wrapper for some {@link DockAction}, although there may exist
 * exceptions.
 * @author Benjamin Sigg
 */
public interface DropDownViewItem extends MenuViewItem<JComponent> {	
	/**
	 * Invoked if the item is triggered from outside. The item should
	 * call the method of its action that causes the action to execute
	 * its natural code (for example: a checkbox may change its selected-state).
	 */
	public void triggered();
	
	/**
	 * Sends the current settings of this item to the view. The values
	 * can be changed as long as the view is registered.
	 * @param view the view, might be <code>null</code>
	 */
	public void setView( DropDownView view );
	
	/**
	 * Tells whether this item can be selected by the button. Only selectable
	 * items can be shown directly on the button. Some items, like a separator,
	 * should return <code>false</code>.
	 * @return whether the item can be selected
	 */
	public boolean isSelectable();
	
	/**
	 * Tells whether the item can be triggered if it is on the button or
	 * in the menu.
	 * @param selected if <code>true</code>, then this item is currently shown
	 * directly on the main button, otherwise it is in the drop-down menu.
	 * @return <code>true</code> if the item can be triggered
	 */
	public boolean isTriggerable( boolean selected );
}
