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
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.action.actions.SimpleSelectableAction;
import bibliothek.gui.dock.event.SelectableDockActionListener;

/**
 * An action which is either selected or deselected.
 * @author Benjamin Sigg
 */
public abstract class FSelectableAction extends FDropDownItem{
    /** the internal representation of this action */
    private SimpleSelectableAction action;
    
    /**
     * Creates a new action
     * @param action the internal representation
     */
    protected FSelectableAction( SimpleSelectableAction action ){
        super( action );
        this.action = action;
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
        action.setSelected( selected );
    }
    
    /**
     * Tells whether this action is selected or not.
     * @return <code>true</code> if the action is selected
     */
    public boolean isSelected(){
        return action.isSelected();
    }
    
    /**
     * Sets the icon that will be shown when this action is selected.
     * @param icon the icon or <code>null</code>
     */
    public void setSelectedIcon( Icon icon ){
        action.setSelectedIcon( icon );
    }
    
    /**
     * Gets the icon that is shown when this action is selected.
     * @return the icon or <code>null</code>
     */
    public Icon getSelectedIcon(){
        return action.getSelectedIcon();
    }
}
