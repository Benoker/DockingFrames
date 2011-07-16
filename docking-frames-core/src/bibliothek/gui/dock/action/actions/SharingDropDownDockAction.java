/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.util.FrameworkOnly;

/**
 * A {@link SharingDropDownDockAction} is a {@link DropDownAction} whose properties are shared by
 * all {@link Dockable}s and whose properties can be set by the client.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public interface SharingDropDownDockAction extends SharingStandardDockAction, DropDownAction{
	/**
	 * Adds an action to the menu.
	 * @param action the action to add
	 */
	public void add( DockAction action );
	
	/**
	 * Inserts an action into the menu.
	 * @param index the location of the action
	 * @param action the new action
	 */
	public void insert( int index, DockAction action );
	
	/**
	 * Inserts a list of actions into the menu.
	 * @param index the location of the first action
	 * @param action the actions to add
	 */
	public void insert( int index, DockAction... action );
	
	/**
	 * Removes an action from the menu.
	 * @param index the location of the action
	 */
	public void remove( int index );
	
	/**
	 * Gets the number of actions shown in the menu.
	 * @return the number of actions
	 */
	public int size();
	
	/**
	 * Removes <code>action</code> from the menu.
	 * @param action the action to remove
	 */
	public void remove( DockAction action );
}
