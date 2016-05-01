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

import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.action.CDropDownButton;
import bibliothek.gui.dock.common.action.core.CommonDropDownItem;
import bibliothek.util.FrameworkOnly;

/**
 * An action which can be child of a dropdown-menu.
 * @author Benjamin Sigg
 * @param <A> the kind of action that represents this {@link CAction}
 */
@FrameworkOnly
public class CDropDownItem<A extends CommonDropDownItem> extends CDecorateableAction<A>{
    /**
     * Creates a new action
     * @param action the internal representation, can be <code>null</code> if
     * {@link #init(bibliothek.gui.dock.common.action.core.CommonDecoratableDockAction)} is called later
     */
    protected CDropDownItem( A action ) {
        super( null );
        if( action != null )
            init( action );
    }

    /**
     * Sets whether this item can get selected when it is a child of a
     * {@link CDropDownButton}.
     * @param dropDownSelectable <code>true</code> if this item can be selected
     */
    public void setDropDownSelectable( boolean dropDownSelectable ){
        intern().setDropDownSelectable( dropDownSelectable );
    }
    
    /**
     * Tells whether this item can be selected if it is a child of a 
     * {@link CDropDownButton}
     * @return <code>true</code> if this item can be selected
     */
    public boolean isDropDownSelectable(){
        return intern().isDropDownSelectable();
    }
    
    /**
     * Sets whether this item can be triggered when it is a child of a {@link CDropDownButton},
     * but is not selected by this {@link CDropDownButton}.
     * @param dropDownTriggerableNotSelected <code>true</code> if this item
     * can be triggered when it is not selected
     */
    public void setDropDownTriggerableNotSelected( boolean dropDownTriggerableNotSelected ){
    	intern().setDropDownTriggerableNotSelected( dropDownTriggerableNotSelected );
    }
    
    /**
     * Tells whether this item can be triggered when it is a child of a {@link CDropDownButton},
     * but is not selected by this {@link CDropDownButton}.
     * @return <code>true</code> if this item can be triggered even when it
     * is not selected
     */
    public boolean isDropDownTriggerableNotSelected(){
        return intern().isDropDownTriggerableNotSelected();
    }
    
    /**
     * Sets whether this item can be triggered when it is a child of a {@link CDropDownButton},
     * and is selected by this {@link CDropDownButton}.
     * @param dropDownTriggerableSelected <code>true</code> if this item
     * can be triggered when it is selected
     */
    public void setDropDownTriggerableSelected( boolean dropDownTriggerableSelected ){
    	intern().setDropDownTriggerableSelected( dropDownTriggerableSelected );
    }
    
    /**
     * Tells whether this item can be triggered when it is a child of a {@link CDropDownButton},
     * and is selected by this {@link CDropDownButton}.
     * @return <code>true</code> if this item can be triggered when it is selected
     */
    public boolean isDropDownTriggerableSelected(){
        return intern().isDropDownTriggerableSelected();
    }
}
