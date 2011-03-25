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

package bibliothek.gui.dock.action;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.dropdown.DropDownFilterFactory;
import bibliothek.gui.dock.event.DropDownActionListener;

/**
 * A DockAction that consists of other actions. The user can either open
 * a popup menu and select one of the subactions, or click onto this action
 * to trigger the last selected action again. 
 * @author Benjamin Sigg
 */
public interface DropDownAction extends StandardDockAction{	
	/**
	 * Gets the last selected action, which may be <code>null</code>.
	 * @param dockable the Dockable for which the last action is requested
	 * @return the action, may be <code>null</code>
	 */
	public DockAction getSelection( Dockable dockable );
	
	/**
	 * Sets the current selection. The item <code>selection</code> should
	 * be part of the {@link #getSubActions(Dockable) menu} 
	 * it should be selectable. The behavior of this method is not
	 * defined if those two conditions are not fulfilled. 
	 * @param dockable the dockable for which the selection has been changed
	 * @param selection the new selection, may be <code>null</code>
	 */
	public void setSelection( Dockable dockable, DockAction selection );
		
	/**
	 * Gets the actions that should be shown for this action.
	 * @param dockable the {@link Dockable} for which the actions are requested
	 * @return the children
	 */
	public DockActionSource getSubActions( Dockable dockable );
	
	/**
	 * Adds a listener to this action. The listener will be informed if
	 * actions are added or removed, or if the selected action changes.
	 * @param listener the listener
	 */
	public void addDropDownActionListener( DropDownActionListener listener );
	
	/**
	 * Removes a listener from this action.
	 * @param listener the listener to remove
	 */
	public void removeDropDownActionListener( DropDownActionListener listener );
	
	/**
	 * Gets a filter that chooses the values that will be shown for this
	 * action and its selection.
	 * @param dockable the dockable for which all values will be used
	 * @return the filter
	 */
	public DropDownFilterFactory getFilter( Dockable dockable );
}
