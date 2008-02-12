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
package bibliothek.gui.dock.common.intern;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.common.FControl;
import bibliothek.gui.dock.common.FMultipleDockable;
import bibliothek.gui.dock.common.FMultipleDockableFactory;

/**
 * Gives access to the internal methods of a {@link bibliothek.gui.dock.common.FControl}
 * @author Benjamin Sigg
 */
public interface FControlAccess {
	/**
	 * Gets the control to which this object gives access.
	 * @return the owner
	 */
	public FControl getOwner();
	
	/**
     * Adds a dockable to this control. The dockable can be made visible afterwards.
     * @param <F> the type of the new element
     * @param dockable the new element to show
     * @param uniqueId id the unique id of the new element
     * @return <code>dockable</code>
     */
	public <F extends FMultipleDockable> F add( F dockable, String uniqueId );
	
	/**
	 * Makes <code>dockable</code> visible.
	 * @param dockable the element that will be made visible
	 */
	public void show( FDockable dockable );
	
	/**
	 * Makes <code>dockable</code> invisible.
	 * @param dockable the element that will be made invisible
	 */
	public void hide( FDockable dockable );
	
	/**
	 * Tells whether <code>dockable</code> is visible or not.
	 * @param dockable the dockable whose visibility-state is in question
	 * @return <code>true</code> if <code>dockable</code> is visible
	 */
	public boolean isVisible( FDockable dockable );
	
	/**
	 * Gets the id of <code>factory</code>.
	 * @param factory the factory to search
	 * @return the id or <code>null</code>
	 */
	public String getFactoryId( FMultipleDockableFactory<?,?> factory );
	
	/**
	 * Gets the manager that is responsible to change the states of the
	 * {@link Dockable}s.
	 * @return the manager
	 */
	public FStateManager getStateManager();
	
	/**
	 * Gets an action that closes <code>dockable</code> when clicked.
	 * @param dockable the element to close
	 * @return the action
	 */
	public DockAction createCloseAction( FDockable dockable );
	
	/**
	 * Gives or removes access to internal properties of an {@link FDockable}.
	 * @param dockable the element which changes its access
	 * @param access the new access, might be <code>null</code>
	 */
	public void link( FDockable dockable, FDockableAccess access );
	
	/**
	 * Grants access to the internal methods of a {@link FDockable}.
	 * @param dockable the element whose access is searched
	 * @return the access or <code>null</code>
	 */
	public FDockableAccess access( FDockable dockable );
}
