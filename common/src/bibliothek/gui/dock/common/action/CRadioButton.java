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
package bibliothek.gui.dock.common.action;

import java.util.Set;

import javax.swing.Icon;
import javax.swing.JRadioButton;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.action.actions.SimpleSelectableAction;
import bibliothek.gui.dock.common.intern.action.CSelectableAction;
import bibliothek.gui.dock.event.SelectableDockActionListener;

/**
 * An action which behaves like a {@link JRadioButton}.<br>
 * Several {@link CRadioButton}s can be added to a {@link CRadioGroup}, only
 * one {@link CRadioButton} of such a group will be selected.
 * @author Benjamin Sigg
 */
public abstract class CRadioButton extends CSelectableAction {
    /** group to which this button belongs */
    private CRadioGroup group;
    
    /**
     * Creates a new radiobutton
     */
    public CRadioButton() {
        super( new SimpleSelectableAction.Radio() );
        ((SimpleSelectableAction)intern()).addSelectableListener( new SelectableDockActionListener(){
            public void selectedChanged( SelectableDockAction action, Set<Dockable> dockables ) {
                if( isSelected() && group != null )
                    group.selected( CRadioButton.this );
            }
        });
    }
    
    /**
     * Creates a new radiobutton
     * @param text the text of this button
     * @param icon the icon of this button
     */
    public CRadioButton( String text, Icon icon ){
        this();
        setText( text );
        setIcon( icon );
    }
    
    /**
     * Sets the group to which this button belongs.
     * @param group the group
     */
    void setGroup( CRadioGroup group ) {
        this.group = group;
    }
}
