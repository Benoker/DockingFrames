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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;

/**
 * The {@link MagnetStrategy} is used by the {@link MagnetController} to define how exactly
 * two {@link ScreenDockWindow}s behave that are attracting each other.
 * @author Benjamin Sigg
 */
public interface MagnetStrategy {
	/**
	 * Informs this {@link MagnetStrategy} that it will be used by <code>controller</code>.
	 * @param controller the controller using this strategy, not <code>null</code>
	 */
	public void install( MagnetController controller );
	
	/**
	 * Informs this {@link MagnetStrategy} that it is no longer used by <code>controller</code>.
	 * @param controller the controller which no longer uses this strategy
	 */
	public void uninstall( MagnetController controller );
	
	/**
	 * Called by <code>controller</code> after a {@link ScreenDockWindow} moved or changed
	 * its size (or both). This method creates a new {@link MagnetOperation} which is tied to
	 * <code>request</code>. The {@link MagnetOperation} will then find out which {@link Dockable}
	 * is attracted to which other {@link Dockable}.
	 * @param controller the caller of this method, may be used to find other {@link ScreenDockWindow}s
	 * @param request detailed information about the event
	 */
	public MagnetOperation start( MagnetController controller, MagnetRequest request );
}
