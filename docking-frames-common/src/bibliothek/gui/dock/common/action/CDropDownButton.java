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

import javax.swing.Icon;

import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.common.action.core.CommonDockAction;
import bibliothek.gui.dock.common.action.core.CommonSimpleDropDownAction;
import bibliothek.gui.dock.common.intern.action.CDecorateableAction;

/**
 * A dropdown-button, the last action which was selected by the user will be
 * marked graphically.
 * @author Benjamin Sigg
 */
public class CDropDownButton extends CDecorateableAction<CommonSimpleDropDownAction>{
    /**
     * Creates a new dropdown-button
     */
    public CDropDownButton(){
    	super( null );
        init( new CommonSimpleDropDownAction( this ));
    }
    
    /**
     * Creates a new dropdown-button
     * @param text the text of this button
     * @param icon the icon of this button
     */
    public CDropDownButton( String text, Icon icon ){
        this();
        setText( text );
        setIcon( icon );
    }
    
    /**
     * Adds an action to this menu.
     * @param action the new action
     */
    public void add( CAction action ){
       intern().add( action.intern() );
    }

    /**
     * Adds an action to the menu.
     * @param index the location of the action
     * @param action the new action
     */
    public void insert( int index, CAction action ){
    	intern().insert( index, action.intern() );
    }
    
    /**
     * Adds a separator at the end of this menu.
     */
    public void addSeparator(){
        add( CSeparator.SEPARATOR );
    }
    
    /**
     * Adds a separator. 
     * @param index the location of the new separator
     */
    public void insertSeparator( int index ){
        insert( index, CSeparator.SEPARATOR );
    }
    
    /**
     * Gets the number of {@link DockAction}s that were added to this menu.
     * @return the number of actions
     */
    public int getActionCount(){
    	return intern().size();
    }
    
    /**
     * Gets the <code>index</code>'th action of this menu.
     * @param index the index of the action
     * @return the action or <code>null</code> if the <code>index</code>'th 
     * {@link DockAction} is not a {@link CommonDockAction} (and hence no {@link CAction}
     * can be found)
     */
    public CAction getAction( int index ){
    	DockAction action = intern().getDockAction( index );
    	if( action instanceof CommonDockAction ){
    		return ((CommonDockAction)action).getAction();
    	}
    	return null;
    }
    
    /**
     * Removes the action at location <code>index</code>.
     * @param index the location of the element to remove
     */
    public void remove( int index ){
    	intern().remove( index );
    }
    
    /**
     * Removes an action from this menu
     * @param action the action to remove
     */
    public void remove( CAction action ){
    	intern().remove( action.intern() );
    }
    
    /**
     * Sets the current selected action.
     * @param action the action to select.
     */
    public void setSelection( CAction action ){
    	intern().setSelection( action.intern() );
    }
    
    /**
     * Gets the currently selected action.
     * @return the selected action or <code>null</code>
     */
    public CAction getSelection(){
    	DockAction action = intern().getSelection();
    	if( action instanceof CommonDockAction ){
    		return ((CommonDockAction)action).getAction();
    	}
    	return null;
    }
}
