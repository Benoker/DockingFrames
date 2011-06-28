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

package bibliothek.gui.dock.title;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;

/**
 * A {@link DockTitleEvent} used to inform a {@link DockTitle} about a set of {@link DockAction}s that should be used.
 * @author Benjamin Sigg
 */
public class ActionsDockTitleEvent extends DockTitleEvent {
	private DockActionSource suggestions;

	/**
	 * Creates a new event.
     * @param dockable the {@link Dockable} for which the target-title
     * is rendered
	 * @param suggestions the set of actions to use, can be <code>null</code>
	 */
	public ActionsDockTitleEvent( Dockable dockable, DockActionSource suggestions ){
		super( dockable );
		this.suggestions = suggestions;
	}
	
	/**
	 * Creates a new event.
     * @param station the station on which the target-title is displayed
     * @param dockable the {@link Dockable} for which the target-title
     * is rendered
	 * @param suggestions the set of actions to use, can be <code>null</code>
	 */
	public ActionsDockTitleEvent( DockStation station, Dockable dockable, DockActionSource suggestions ){
		super( station, dockable );
		this.suggestions = suggestions;
	}

	/**
	 * Gets the set of {@link DockAction}s that should be used.
	 * @return the actions, can be <code>null</code>
	 */
	public DockActionSource getSuggestions(){
		return suggestions;	
	}
}
