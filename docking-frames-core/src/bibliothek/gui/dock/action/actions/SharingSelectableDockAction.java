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
package bibliothek.gui.dock.action.actions;

import javax.swing.Icon;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.util.FrameworkOnly;

/**
 * A {@link SelectableDockAction} whose properties are shared among all {@link Dockable}s, clients
 * can modify the properties.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public interface SharingSelectableDockAction extends SharingDropDownItemAction, SelectableDockAction{

    /**
     * Gets the selected-state of this action.
     * @return The current state
     * @see #setSelected(boolean)
     */
    public boolean isSelected();
	
    /**
     * Sets the state of this action. The action will notify all listeners
     * about the new state.
     * @param selected the new state
     */
	public void setSelected( boolean selected );
	
	/**
     * Gets the icon that is shown when this action is selected.
     * @param modifier tells in which context the icon is used
     * @return The selected-icon, may be <code>null</code>
     * @see #setSelectedIcon(ActionContentModifier, Icon)
     * @see #isSelected()
     */
    public Icon getSelectedIcon( ActionContentModifier modifier );
    
    /**
     * Sets the icon that will be shown, when this action is selected.
     * @param modifier tells in which context <code>icon</code> will be used
     * @param selectedIcon The icon, can be <code>null</code>
     * @see #setSelected(boolean)
     */
    public void setSelectedIcon( ActionContentModifier modifier, Icon selectedIcon );
}
