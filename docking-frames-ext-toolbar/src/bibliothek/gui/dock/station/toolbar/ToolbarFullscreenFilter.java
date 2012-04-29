/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockFullscreenFilter;
import bibliothek.gui.dock.util.SilentPropertyValue;

/**
 * This filter can be added to a {@link ScreenDockStation} and ensures that no
 * toolbar element can be in fullscreen mode
 * 
 * @author Benjamin Sigg
 */
public class ToolbarFullscreenFilter implements ScreenDockFullscreenFilter{
	private final DockController controller;

	/**
	 * Creates a new filter
	 * 
	 * @param controller
	 *            the controller in whose realm this filter will be used
	 */
	public ToolbarFullscreenFilter( DockController controller ){
		this.controller = controller;
	}

	@Override
	public boolean isFullscreenEnabled( Dockable dockable ){
		final SilentPropertyValue<ToolbarStrategy> value = new SilentPropertyValue<ToolbarStrategy>(
				ToolbarStrategy.STRATEGY, controller);
		final ToolbarStrategy strategy = value.getValue();
		value.setProperties((DockController) null);

		return !strategy.isToolbarPart(dockable);
	}
}
