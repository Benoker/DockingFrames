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

package bibliothek.gui.dock.action.dropdown;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DropDownAction;

/**
 * A factory that creates {@link DropDownFilter}.
 * @author Benjamin Sigg
 */
public interface DropDownFilterFactory {
	/**
	 * Creates a new filter. The filters <code></code>-methods will be
	 * called by some unknown source, and the filter can then decide which
	 * of the values to forward to the <code>view</code>.
	 * @param action the action for which the filter will be used
	 * @param dockable the owner of the <code>action</code>
	 * @param view the view where the filter should write its properties into
	 * @return the new filter
	 */
	public DropDownFilter createView( DropDownAction action, Dockable dockable, DropDownView view );
}
