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

import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.common.action.core.CommonDockAction;
import bibliothek.gui.dock.common.action.core.CommonSimpleMenuAction;
import bibliothek.gui.dock.common.intern.action.CDecorateableAction;

/**
 * A collection of {@link CAction}s which are shown in a menu.
 * @author Benjamin Sigg
 *
 */
public class CMenu extends CDecorateableAction<CommonSimpleMenuAction> {
    /** the internal representation */
    private DefaultDockActionSource menu;
    
    /**
     * Creates a new menu
     */
    public CMenu() {
        super( null );
        menu = new DefaultDockActionSource();
        init( new CommonSimpleMenuAction( this, menu ));
    }
    
    /**
     * Creates a new menu.
     * @param text the text of this menu
     * @param icon the icon of this menu
     */
    public CMenu( String text, Icon icon ){
        this();
        setText( text );
        setIcon( icon );
    }
    
    /**
     * Adds an action to this menu.
     * @param action the new action
     */
    public void add( CAction action ){
        menu.add( action.intern() );
    }

    /**
     * Adds an action to the menu.
     * @param index the location of the action
     * @param action the new action
     */
    public void insert( int index, CAction action ){
        menu.add( index, action.intern() );
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
    	return menu.getDockActionCount();
    }
    
    /**
     * Gets the <code>index</code>'th action of this menu.
     * @param index the index of the action
     * @return the action or <code>null</code> if the <code>index</code>'th 
     * {@link DockAction} is not a {@link CommonDockAction} (and hence no {@link CAction}
     * can be found)
     */
    public CAction getAction( int index ){
    	DockAction action = menu.getDockAction( index );
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
        menu.remove( index );
    }
    
    /**
     * Removes an action from this menu
     * @param action the action to remove
     */
    public void remove( CAction action ){
        menu.remove( action.intern() );
    }
}
