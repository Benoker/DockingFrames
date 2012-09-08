/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.toolbar.expand;

import bibliothek.gui.Dockable;

/**
 * An {@link ExpandableToolbarItem} is a part of a toolbar that can have
 * different shapes.
 * 
 * @author Benjamin Sigg
 */
public interface ExpandableToolbarItem extends Dockable{
	/**
	 * Adds the observer <code>listener</code> to this item.
	 * @param listener the new listener, not <code>null</code>
	 */
	public void addExpandableListener( ExpandableToolbarItemListener listener );

	/**
	 * Removes the observer <code>listener</code> from this item.
	 * @param listener the listener to remove
	 */
	public void removeExpandableListener( ExpandableToolbarItemListener listener );

	/**
	 * Tells whether this {@link ExpandableToolbarItem} likes to be in state <code>state</code>.
	 * @param state a possible state
	 * @return whether <code>this</code> item likes to be in <code>state</code>
	 */
	public boolean isEnabled( ExpandedState state );
	
	/**
	 * Changes the state of this item to <code>state</code>. Note that <code>state</code>
	 * can be any state, including those for which {@link #isEnabled(ExpandedState)} returned
	 * <code>false</code>.
	 * @param state the new state
	 */
	public void setExpandedState( ExpandedState state );

	/**
	 * Gets the current state of this item.
	 * @return the current state
	 */
	public ExpandedState getExpandedState();
}
