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
package bibliothek.gui.dock.common.intern.action;

import bibliothek.gui.dock.action.actions.SimpleDropDownItemAction;
import bibliothek.gui.dock.common.action.FDropDownButton;
import bibliothek.gui.dock.common.intern.FDecorateableAction;

/**
 * An action which can be child of a dropdown-menu.
 * @author Benjamin Sigg
 */
public class FDropDownItem extends FDecorateableAction{
    /** the internal representation */
    private SimpleDropDownItemAction action;
    
    /**
     * Creates a new action
     * @param action the internal representation
     */
    protected FDropDownItem( SimpleDropDownItemAction action ) {
        super( action );
        this.action = action;
    }

    /**
     * Sets whether this item can get selected when it is a child of a
     * {@link FDropDownButton}.
     * @param dropDownSelectable <code>true</code> if this item can be selected
     */
    public void setDropDownSelectable( boolean dropDownSelectable ){
        action.setDropDownSelectable( dropDownSelectable );
    }
    
    /**
     * Tells whether this item can be selected if it is a child of a 
     * {@link FDropDownButton}
     * @return <code>true</code> if this item can be selected
     */
    public boolean isDropDownSelectable(){
        return action.isDropDownSelectable();
    }
    
    /**
     * Sets whether this item can be triggered when it is a child of a {@link FDropDownButton},
     * but is not selected by this {@link FDropDownButton}.
     * @param dropDownTriggerableNotSelected <code>true</code> if this item
     * can be triggered when it is not selected
     */
    public void setDropDownTriggerableNotSelected( boolean dropDownTriggerableNotSelected ){
        action.setDropDownTriggerableNotSelected( dropDownTriggerableNotSelected );
    }
    
    /**
     * Tells whether this item can be triggered when it is a child of a {@link FDropDownButton},
     * but is not selected by this {@link FDropDownButton}.
     * @return <code>true</code> if this item can be triggered even when it
     * is not selected
     */
    public boolean isDropDownTriggerableNotSelected(){
        return action.isDropDownTriggerableNotSelected();
    }
    
    /**
     * Sets whether this item can be triggered when it is a child of a {@link FDropDownButton},
     * and is selected by this {@link FDropDownButton}.
     * @param dropDownTriggerableSelected <code>true</code> if this item
     * can be triggered when it is selected
     */
    public void setDropDownTriggerableSelected( boolean dropDownTriggerableSelected ){
        action.setDropDownTriggerableSelected( dropDownTriggerableSelected );
    }
    
    /**
     * Tells whether this item can be triggered when it is a child of a {@link FDropDownButton},
     * and is selected by this {@link FDropDownButton}.
     * @return <code>true</code> if this item can be triggered when it is selected
     */
    public boolean isDropDownTriggerableSelected(){
        return action.isDropDownTriggerableSelected();
    }
}
