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
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;

/**
 * A listener that is added to an {@link ExpandableToolbarItemStrategy}.
 * 
 * @author Benjamin Sigg
 */
public interface ExpandableToolbarItemStrategyListener{
	/**
	 * Called if <code>item</code> was expanded.
	 * 
	 * @param item
	 *            the item whose state changed
	 */
	public void expanded( Dockable item );

	/**
	 * Called if <code>item</code> was stretched.
	 * 
	 * @param item
	 *            the item whose state changed
	 */
	public void stretched( Dockable item );

	/**
	 * Called if <code>item</code> was made small.
	 * 
	 * @param item
	 *            the item show state changed
	 */
	public void shrunk( Dockable item );

	/**
	 * Called if the result of
	 * {@link ExpandableToolbarItemStrategy#isEnabled(Dockable, ExpandedState)}
	 * changed for <code>item</code> and <code>state</code>.
	 * 
	 * @param item
	 *            the item whose enablement changed
	 * @param state
	 *            the state whose enablement changed
	 * @param enabled
	 *            whether <code>item</code> can be in mode <code>state</code>
	 */
	public void enablementChanged( Dockable item, ExpandedState state,
			boolean enabled );

}
