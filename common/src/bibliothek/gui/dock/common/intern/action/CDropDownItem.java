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

import bibliothek.gui.dock.action.actions.SimpleDockAction;
import bibliothek.gui.dock.action.actions.SimpleDropDownItemAction;
import bibliothek.gui.dock.common.action.CDropDownButton;
import bibliothek.gui.dock.common.intern.CDecorateableAction;

/**
 * An action which can be child of a dropdown-menu.
 * @author Benjamin Sigg
 */
public class CDropDownItem extends CDecorateableAction{
    /** the internal representation */
    private SimpleDropDownItemAction action;
    
    /**
     * Creates a new action
     * @param action the internal representation, can be <code>null</code> if
     * {@link #init(SimpleDropDownItemAction)} is called later
     */
    protected CDropDownItem( SimpleDropDownItemAction action ) {
        super( null );
        if( action != null )
            init( action );
    }
    
    /**
     * Initializes this action, this method can be called only once.
     * @param action the internal representation
     */
    protected void init( SimpleDropDownItemAction action ){
        init( (SimpleDockAction)action );
        this.action = action;
    }

    /**
     * Sets whether this item can get selected when it is a child of a
     * {@link CDropDownButton}.
     * @param dropDownSelectable <code>true</code> if this item can be selected
     */
    public void setDropDownSelectable( boolean dropDownSelectable ){
        action.setDropDownSelectable( dropDownSelectable );
    }
    
    /**
     * Tells whether this item can be selected if it is a child of a 
     * {@link CDropDownButton}
     * @return <code>true</code> if this item can be selected
     */
    public boolean isDropDownSelectable(){
        return action.isDropDownSelectable();
    }
    
    /**
     * Sets whether this item can be triggered when it is a child of a {@link CDropDownButton},
     * but is not selected by this {@link CDropDownButton}.
     * @param dropDownTriggerableNotSelected <code>true</code> if this item
     * can be triggered when it is not selected
     */
    public void setDropDownTriggerableNotSelected( boolean dropDownTriggerableNotSelected ){
        action.setDropDownTriggerableNotSelected( dropDownTriggerableNotSelected );
    }
    
    /**
     * Tells whether this item can be triggered when it is a child of a {@link CDropDownButton},
     * but is not selected by this {@link CDropDownButton}.
     * @return <code>true</code> if this item can be triggered even when it
     * is not selected
     */
    public boolean isDropDownTriggerableNotSelected(){
        return action.isDropDownTriggerableNotSelected();
    }
    
    /**
     * Sets whether this item can be triggered when it is a child of a {@link CDropDownButton},
     * and is selected by this {@link CDropDownButton}.
     * @param dropDownTriggerableSelected <code>true</code> if this item
     * can be triggered when it is selected
     */
    public void setDropDownTriggerableSelected( boolean dropDownTriggerableSelected ){
        action.setDropDownTriggerableSelected( dropDownTriggerableSelected );
    }
    
    /**
     * Tells whether this item can be triggered when it is a child of a {@link CDropDownButton},
     * and is selected by this {@link CDropDownButton}.
     * @return <code>true</code> if this item can be triggered when it is selected
     */
    public boolean isDropDownTriggerableSelected(){
        return action.isDropDownTriggerableSelected();
    }
}
