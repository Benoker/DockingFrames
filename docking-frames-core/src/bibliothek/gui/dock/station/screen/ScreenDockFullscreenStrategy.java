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
package bibliothek.gui.dock.station.screen;

import bibliothek.gui.dock.ScreenDockStation;

/**
 * This strategy is used to manage the fullscreen mode of {@link ScreenDockWindow}s. This
 * strategy defines what "fullscreen" means, and offers methods to switch a window between
 * normal- and fullscreen-mode.
 * @author Benjamin
 */
public interface ScreenDockFullscreenStrategy {
	/**
	 * Informs this strategy that it will be used for <code>station</code>.
	 * @param station some station using this strategy
	 */
	public void install( ScreenDockStation station );
	
	/**
	 * Informs this strategy that it will no longer be used for <code>station</code>.
	 * @param station some station that is no longer using this strategy
	 */
	public void uninstall( ScreenDockStation station );
	
	/**
	 * Tells whether <code>window</code> is in fullscreen mode. If this strategy cannot handle
	 * <code>window</code> it returns <code>false</code>.
	 * @param window some window of a known station
	 * @return <code>true</code> if <code>window</code> is in fullscreen mode, <code>false</code> otherwise
	 */
	public boolean isFullscreen( ScreenDockWindow window );
	
	/**
	 * Changes the fullscreen mode of <code>window</code>, does nothing if this strategy cannot handle the
	 * type of <code>window</code>.
	 * @param window some window of a known station
	 * @param fullscreen the new state
	 */
	public void setFullscreen( ScreenDockWindow window, boolean fullscreen );
}
