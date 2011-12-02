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
package bibliothek.gui.dock.station.screen.window;

import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.gui.dock.util.color.DockColor;
import bibliothek.util.Path;

/**
 * Represents a color that is used to paint something of a {@link ScreenDockWindow}.
 * @author Benjamin Sigg
 */
public interface ScreenDockWindowColor extends DockColor{
	/** the type of this {@link UIValue} */
	public static final Path KIND_SCREEN_WINDOW_COLOR = DockColor.KIND_DOCK_COLOR.append( "screenWindow" );
	
	/**
	 * Gets the window for which this color is used.
	 * @return the window, not <code>null</code>
	 */
	public ScreenDockWindow getWindow();
}
