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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.MenuDockAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.disable.DisablingStrategy;

/**
 * An action that provides a menu which contains other actions.
 * @author Benjamin Sigg
 */
public class SimpleMenuAction extends SimpleDockAction implements MenuDockAction{
	/** the menu */
	private DockActionSource menu;
	
	/**
	 * Creates a new action. The menu has to be set later.
	 * @see #setMenu(DockActionSource)
	 */
	public SimpleMenuAction(){
		this( true );
	}
	
	/**
	 * Creates a new action, the method has to be set later.
	 * @param monitorDisabling whether the current {@link DisablingStrategy} will be monitored
	 */
	public SimpleMenuAction( boolean monitorDisabling ){
		super( monitorDisabling );
	}
	
	/**
	 * Creates a new action.
	 * @param menu the menu that is shown for this action
	 */
	public SimpleMenuAction( DockActionSource menu ){
		this( true );
		setMenu( menu );
	}

	/**
     * Sets a menu that will be displayed instead of this action. Note that
     * this call might not have an immediate effect.
     * @param menu the menu, may be <code>null</code>
     */
    public void setMenu( DockActionSource menu ) {
    	this.menu = menu;
    }
    
    public DockActionSource getMenu( Dockable dockable ){
    	return menu;
    }
    
    /**
     * Gets the menu of this action
     * @return the menu
     */
    public DockActionSource getMenu() {
        return menu;
    }
	
	public <V> V createView( ViewTarget<V> target, ActionViewConverter converter, Dockable dockable ){
		return converter.createView( ActionType.MENU, this, target, dockable );
	}
	
	public boolean trigger( Dockable dockable ) {
	    // can't do anything
	    return false;
	}
}
