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
package bibliothek.gui.dock.station.screen.magnet;

import java.awt.Rectangle;

import bibliothek.gui.dock.station.screen.ScreenDockWindow;


/**
 * A callback used by {@link ScreenDockWindow}s to calculate the attraction between two 
 * {@link ScreenDockWindow}s.
 * @author Benjamin Sigg
 */
public interface MagnetizedOperation {
	
	/**
	 * To be called by a {@link ScreenDockWindow} if the user updates the size or 
	 * location of the window. This method calculates then to which other windows
	 * an attraction may exist.
	 * @param bounds the boundaries the window would have if the operation would stop
	 * right now.
	 * @return the boundaries the window would have if attraction is respected, never <code>null</code>
	 */
	public Rectangle attract( Rectangle bounds );
	
	/**
	 * To be called by a {@link ScreenDockWindow} once moving or resizing is finished.
	 */
	public void stop();
}
