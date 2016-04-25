/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.station.stack.tab.TabContentFilter;

/**
 * A listener to a {@link TabContentFilter}.
 * @author Benjamin Sigg
 */
public interface TabContentFilterListener {
	/**
	 * Informs this listener that icon, title and/or tooltip of <code>dockable</code> changed.
	 * @param dockable the affected element
	 */
	public void contentChanged( Dockable dockable );
	
	/**
	 * Informs this listener that all icons, titles and tooltips of all children
	 * of <code>station</code> have changed.
	 * @param station the affected station
	 */
	public void contentChanged( StackDockStation station );
	
	/**
	 * Informs this listener that all icons, titles and tooltips of all children
	 * of <code>component</code> have changed.
	 * @param component the affected station
	 */
	public void contentChanged( StackDockComponent component );
	
	/**
	 * Informs this listener that all icons, titles and tooltips have changed.
	 */
	public void contentChanged();
}
