/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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

/**
 * This listener can be added to a {@link ButtonContentFilter} and will be informed if the filter changes
 * its behavior.
 * @author Benjamin Sigg
 */
public interface ButtonContentFilterListener {
	/**
	 * Informs this listener that the result of {@link ButtonContentFilter#showText(Dockable, DockAction)}
	 * changed.
	 * @param filter the source of the event
	 * @param dockable the dockable related to <code>action</code> or <code>null</code> to indicate
	 * that all occurences of <code>action</code> are affected
	 * @param action the action whose state changed or <code>null</code> as a wildcard telling that all
	 * actions that are connected to <code>dockable</code> are affected
	 */
	public void showTextChanged( ButtonContentFilter filter, Dockable dockable, DockAction action );
}
