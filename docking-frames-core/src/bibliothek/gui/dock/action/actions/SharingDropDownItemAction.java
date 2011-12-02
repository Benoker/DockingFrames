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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.dropdown.DropDownItemAction;
import bibliothek.util.FrameworkOnly;

/**
 * A {@link DropDownItemAction} whose properties are shared among all {@link Dockable}s, the
 * properties can be modified by the client.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public interface SharingDropDownItemAction extends SharingStandardDockAction, DropDownItemAction{
    /**
     * Sets whether this action can be selected if it is a child of a
     * {@link DropDownAction} or not.
     * @param dropDownSelectable <code>true</code> if this action can
     * be selected
     */
    public void setDropDownSelectable( boolean dropDownSelectable );
    
    /**
     * Tells whether this action can be selected.
     * @return <code>true</code> if it can be selected
     * @see #setDropDownSelectable(boolean)
     */
    public boolean isDropDownSelectable();
    
    /**
     * Sets whether this action can be triggered when shown on, and selected by, a
     * {@link DropDownAction} or not.
     * @param dropDownTriggerableSelected <code>true</code> if this action
     * can be triggered
     */
    public void setDropDownTriggerableSelected( boolean dropDownTriggerableSelected );
    
    /**
     * Tells whether this action can be triggered when shown on and selected by a
     * {@link DropDownAction} or not.
     * @return <code>true</code> if the action can be triggered
     */
    public boolean isDropDownTriggerableSelected();
    
    /**
     * Sets whether this action can be triggered when shown on, but not selected 
     * by, a {@link DropDownAction} or not.
     * @param dropDownTriggerableNotSelected <code>true</code> if this action
     * can be triggered
     */
    public void setDropDownTriggerableNotSelected( boolean dropDownTriggerableNotSelected );
    
    /**
     * Tells whether this action can be triggered when shown on, but not selected 
     * by, a {@link DropDownAction} or not.
     * @return <code>true</code> if the action can be triggered
     */
    public boolean isDropDownTriggerableNotSelected();
}
