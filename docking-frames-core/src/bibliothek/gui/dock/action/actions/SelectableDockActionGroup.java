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

package bibliothek.gui.dock.action.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.event.SelectableDockActionListener;

/**
 * This class ensures that from a group of {@link SelectableDockAction} only one
 * is selected.
 * @author Benjamin Sigg
 */
public class SelectableDockActionGroup {
	/** The actions to observe */
	private List<SelectableDockAction> actions = new ArrayList<SelectableDockAction>();
	
	/** A listener to all actions */
	private SelectableDockActionListener listener = new SelectableDockActionListener(){
		public void selectedChanged( SelectableDockAction action, Set<Dockable> dockables ){
			for( Dockable dockable : dockables ){
				if( action.isSelected( dockable )){
					for( SelectableDockAction change : actions ){
						if( change != action ){
							change.setSelected( dockable, false );
						}
					}
				}
			}
		}
	};
	
	/**
	 * Adds an action that has to be observed
	 * @param action the new action
	 */
	public void addAction( SelectableDockAction action ){
		actions.add( action );
		action.addSelectableListener( listener );
	}
	
	/**
	 * Removes an action. That action will no longer be observed.
	 * @param action the action to remove
	 */
	public void removeAction( SelectableDockAction action ){
		action.removeSelectableListener( listener );
		actions.remove( action );
	}
}
