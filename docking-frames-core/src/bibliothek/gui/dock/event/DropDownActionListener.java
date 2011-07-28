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

package bibliothek.gui.dock.event;

import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DropDownAction;

/**
 * A listener of a {@link DropDownAction}. The listener gets informed whenever
 * the number of actions changes, or the selection changes.
 * @author Benjamin Sigg
 *
 */
public interface DropDownActionListener {	
	/**
	 * Called when the selection of <code>action</code> has changed.
	 * @param action the action whose selection is changed
	 * @param dockables the set of {@link Dockable Dockables} for which
	 * the selection has changed
	 * @param selection the new selected child, might be <code>null</code>
	 */
	public void selectionChanged( DropDownAction action, Set<Dockable> dockables, DockAction selection );
}
