/**
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

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.dropdown.DropDownItemAction;

/**
 * An action that can be shown as child of a {@link DropDownAction} and
 * which has advanced information for the {@link DropDownAction}.
 * @author Benjamin Sigg
 */
public abstract class SimpleDropDownItemAction extends SimpleDockAction implements DropDownItemAction{

    /** Whether this action can be selected in a {@link DropDownAction} or not. */
    private boolean dropDownSelectable = true;
    
    /** Whether this action can be triggered when shown on a {@link DropDownAction} or not if it is selected */
    private boolean dropDownTriggerableSelected = true;
    
    /** Whether this action can be triggered when shown on a {@link DropDownAction} or not if it is not selected */
    private boolean dropDownTriggerableNotSelected = true;

    /**
     * Sets whether this action can be selected if it is a child of a
     * {@link DropDownAction} or not.
     * @param dropDownSelectable <code>true</code> if this action can
     * be selected
     */
    public void setDropDownSelectable( boolean dropDownSelectable ){
		this.dropDownSelectable = dropDownSelectable;
	}
    
    public boolean isDropDownSelectable( Dockable dockable ){
    	return dropDownSelectable;
    }
    
    /**
     * Tells whether this action can be selected.
     * @return <code>true</code> if it can be selected
     * @see #setDropDownSelectable(boolean)
     */
    public boolean isDropDownSelectable(){
    	return dropDownSelectable;
    }
    
    /**
     * Sets whether this action can be triggered when shown on, and selected by, a
     * {@link DropDownAction} or not.
     * @param dropDownTriggerableSelected <code>true</code> if this action
     * can be triggered
     */
    public void setDropDownTriggerableSelected( boolean dropDownTriggerableSelected ){
		this.dropDownTriggerableSelected = dropDownTriggerableSelected;
	}
    
    /**
     * Tells whether this action can be triggered when shown on and selected by a
     * {@link DropDownAction} or not.
     * @return <code>true</code> if the action can be triggered
     */
    public boolean isDropDownTriggerableSelected(){
		return dropDownTriggerableSelected;
	}
    /**
     * Sets whether this action can be triggered when shown on, but not selected 
     * by, a {@link DropDownAction} or not.
     * @param dropDownTriggerableNotSelected <code>true</code> if this action
     * can be triggered
     */
    public void setDropDownTriggerableNotSelected( boolean dropDownTriggerableNotSelected ){
		this.dropDownTriggerableNotSelected = dropDownTriggerableNotSelected;
	}
    
    /**
     * Tells whether this action can be triggered when shown on, but not selected 
     * by, a {@link DropDownAction} or not.
     * @return <code>true</code> if the action can be triggered
     */
    public boolean isDropDownTriggerableNotSelected(){
		return dropDownTriggerableNotSelected;
	}
    
    public boolean isDropDownTriggerable( Dockable dockable, boolean selected ){
    	if( selected )
    		return dropDownTriggerableSelected;
    	else
    		return dropDownTriggerableNotSelected;
    }
}
