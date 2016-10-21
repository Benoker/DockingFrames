/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.station.screen;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.window.WindowConfiguration;

/**
 * A {@link ScreenDockWindowConfiguration} is a strategy creating {@link WindowConfiguration}s 
 * for all children of a {@link ScreenDockStation}.
 * @author Benjamin Sigg
 */
public interface ScreenDockWindowConfiguration {
	/**
	 * Creates a new configuration for a {@link ScreenDockWindow} that is going to show
	 * <code>dockable</code>. This configuration is only used to set up the new {@link ScreenDockWindow},
	 * any further modifications will not affect the window. A {@link ScreenDockWindowFactory} or a 
	 * {@link ScreenDockWindow} might ignore some parts or the entire configuration, although the default
	 * implementations all try to follow the configuration.
	 * @param station the future or current parent of <code>dockable</code>
	 * @param dockable the element which is going to be shown
	 * @return the new configuration, or <code>null</code> if a default configuration should be used
	 */
	public WindowConfiguration getConfiguration( ScreenDockStation station, Dockable dockable );
}
