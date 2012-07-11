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
package bibliothek.gui.dock.station.stack;

import bibliothek.gui.Dockable;

/**
 * This listener can be added to a {@link StackDockComponent}. It will receive events if the
 * selection or the available tabs changes.
 * @author Benjamin Sigg
 */
public interface StackDockComponentListener {
	/**
	 * Called if the current selection on <code>stack</code> changed.
	 * @param stack the source of the event
	 */
	public void selectionChanged( StackDockComponent stack );
	
	/**
	 * Called if the result of {@link StackDockComponent#getTabAt(int)} changed
	 * for any index associated with <code>dockable</code>
	 * @param stack the source of the event
	 * @param dockable the element with a new value
	 */
	public void tabChanged( StackDockComponent stack, Dockable dockable );
}
