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


/**
 * A listener that can be added to an {@link ExpandableToolbarItem}, it will be informed
 * if the {@link ExpandedState}s change.
 * @author Benjamin Sigg
 */
public interface ExpandableToolbarItemListener{
	/**
	 * Called if the result of {@link ExpandableToolbarItem#isEnabled(ExpandedState)} changed
	 * for <code>item</code> and <code>state</code> to <code>enabled</code>.
	 * @param item the source of the event
	 * @param state the state which was changed
	 * @param enabled whether <code>state</code> is now enabled or not
	 */
	public void enablementChanged( ExpandableToolbarItem item, ExpandedState state, boolean enabled );
	
	/**
	 * Called if the state of <code>item</code> changed from
	 * <code>oldState</code> to <code>newState</code>.
	 * @param item the source of the event
	 * @param oldState the old state of <code>item</code>
	 * @param newState the new state of <code>item</code>
	 */
	public void changed( ExpandableToolbarItem item, ExpandedState oldState, ExpandedState newState );
}
