/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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

import bibliothek.gui.DockController;
import bibliothek.gui.dock.StackDockStation;

/**
 * Generic algorithm allowing {@link StackDockComponent}s to inform the client if the user drags some
 * data over a tab. The interface itself only offers an {@link #install(StackDockStation, StackDockComponent)} and 
 * {@link #uninstall(StackDockComponent)} method - what exactly the subclass does with the 
 * {@link StackDockComponent} is not defined.<br>
 * An implementation of this interface is automatically installed if the application runs with
 * Java 1.6 or higher.
 * @author Benjamin Sigg
 */
public interface StackDnDAutoSelectSupport {
	/**
	 * Called if a {@link StackDockComponent} has been bound to a {@link DockController}.
	 * @param station the station on which <code>component</code> is shown
	 * @param component the component that was bound
	 */
	public void install( StackDockStation station, StackDockComponent component );
	
	/**
	 * Called if a {@link StackDockComponent} has been removed from a {@link DockController}.
	 * @param component the component that was removed
	 */
	public void uninstall( StackDockComponent component );
}
