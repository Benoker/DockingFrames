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

import java.util.Set;

import javax.swing.Icon;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.action.core.CommonSelectableAction;
import bibliothek.gui.dock.event.SelectableDockActionListener;
import bibliothek.util.FrameworkOnly;

/**
 * An action which is either selected or deselected.
 * @author Benjamin Sigg
 * @param <A> the kind of action representing this {@link CAction}
 */
@FrameworkOnly
public abstract class CSelectableAction<A extends CommonSelectableAction> extends CDropDownItem<A>{
    /**
     * Creates a new action
     * @param action the internal representation
     */
    protected CSelectableAction( A action ){
    	super( null );
    	if( action != null ){
    		init( action );
    	}
    }
    
    protected void init(A action){
        super.init( action );
        action.addSelectableListener( new SelectableDockActionListener(){
            public void selectedChanged( SelectableDockAction action, Set<Dockable> dockables ) {
                changed();
            }
        });
    }
    
    /**
     * Called when {@link #isSelected() selected}-state of this action
     * changed.
     */
    protected abstract void changed();
    
    /**
     * Sets the selected-state.
     * @param selected the new state
     */
    public void setSelected( boolean selected ){
        intern().setSelected( selected );
    }
    
    /**
     * Tells whether this action is selected or not.
     * @return <code>true</code> if the action is selected
     */
    public boolean isSelected(){
        return intern().isSelected();
    }
    
    /**
     * Sets the icon that will be shown when this action is selected.
     * @param icon the icon or <code>null</code>
     */
    public void setSelectedIcon( Icon icon ){
    	intern().setSelectedIcon( ActionContentModifier.NONE, icon );
    }
    
    /**
     * Gets the icon that is shown when this action is selected.
     * @return the icon or <code>null</code>
     */
    public Icon getSelectedIcon(){
        return intern().getSelectedIcon( ActionContentModifier.NONE );
    }
    
    /**
     * Sets the icon which is used if the mouse is hovering over a button that represents this action and
     * if this action is selected.
     * @param icon the icon or <code>null</code>
     */
    public void setSelectedHoverIcon( Icon icon ){
    	intern().setSelectedIcon( ActionContentModifier.NONE_HOVER, icon );
    }
    
    /**
     * Gets the icon which is used if the mouse is hovering over a button that represents this action and
     * if this action is selected.
     * @return the icon or <code>null</code>
     */
    public Icon getSelectedHoverIcon(){
    	return intern().getSelectedIcon( ActionContentModifier.NONE_HOVER );
    }
    
    /**
     * Sets the icon which is used if the mouse is pressed over a button that represents this action and
     * if this action is selected.
     * @param icon the icon or <code>null</code>
     */
    public void setSelectedPressedIcon( Icon icon ){
    	intern().setSelectedIcon( ActionContentModifier.NONE_PRESSED, icon );
    }
    
    /**
     * Gets the icon which is used if the mouse is pressed over a button that represents this action and
     * if this action is selected.
     * @return the icon, can be <code>null</code>
     */
    public Icon getSelectedPressedIcon(){
    	return intern().getSelectedIcon( ActionContentModifier.NONE_PRESSED );
    }
    
    /**
     * Sets the icon which is used if the mouse is hovering over a button that represents this action and
     * if this action is disabled and selected.
     * @param icon the icon or <code>null</code>
     */
    public void setDisabledSelectedHoverIcon( Icon icon ){
    	intern().setSelectedIcon( ActionContentModifier.DISABLED_HOVER, icon );
    }
    
    /**
     * Gets the icon which is used if the mouse is hovering over a button that represents this action and
     * if this action is disabled and selected.
     * @return the icon or <code>null</code>
     */
    public Icon getDisabledSelectedHoverIcon(){
    	return intern().getSelectedIcon( ActionContentModifier.DISABLED_HOVER );
    }
    
    /**
     * Sets the icon which is used if the mouse is pressed over a button that represents this action and
     * if this action is disabled and selected.
     * @param icon the icon or <code>null</code>
     */
    public void setDisabledSelectedPressedIcon( Icon icon ){
    	intern().setSelectedIcon( ActionContentModifier.DISABLED_PRESSED, icon );
    }
    
    /**
     * Gets the icon which is used if the mouse is pressed over a button that represents this action and
     * if this action is disabled and selected.
     * @return the icon, can be <code>null</code>
     */
    public Icon getDisabledSelectedPressedIcon(){
    	return intern().getSelectedIcon( ActionContentModifier.DISABLED_PRESSED );
    }
}
