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
import bibliothek.gui.dock.event.SelectableDockActionListener;

/**
 * An action which has two states "selected" and "not selected".
 * @author Benjamin Sigg
 *
 */
public interface SelectableDockAction extends StandardDockAction, StandardDropDownItemAction {
    /**
     * Tells whether this DockAction is selected or not (in respect
     * to the given <code>dockable</code>).
     * @param dockable The {@link Dockable} for which this action may be selected
     * or not selected
     * @return <code>true</code> if this DockAction is selected, <code>false</code>
     * otherwise
     */
    public boolean isSelected( Dockable dockable );
	
    /**
     * Sets the selected state for <code>dockable</code>.
     * @param dockable the affected dockable
     * @param selected the new state
     */
	public void setSelected( Dockable dockable, boolean selected );
	
	/**
	 * Adds a listener to this action. The listener will be invoked whenever
	 * the selected state of a {@link Dockable} changes.
	 * @param listener the new listener
	 */
	public void addSelectableListener( SelectableDockActionListener listener );
	
	/**
	 * Removes a listener from this action.
	 * @param listener the listener to remove
	 */
	public void removeSelectableListener( SelectableDockActionListener listener );
}
