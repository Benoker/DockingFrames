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

package bibliothek.gui.dock.station.screen;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.window.WindowConfiguration;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;

/**
 * This class will configure {@link ScreenDockWindow}s such that grabbing the
 * title of a toolbar does not start a drag and drop operation directly, but
 * first allows the user to move around the entire window.
 * 
 * @author Benjamin Sigg
 */
public class ToolbarWindowConfiguration implements ScreenDockWindowConfiguration{
	private final DockController controller;

	/**
	 * Creates a new configuration
	 * 
	 * @param controller
	 *            the controller in whose realm this configuration is used
	 */
	public ToolbarWindowConfiguration( DockController controller ){
		this.controller = controller;
	}

	/**
	 * Gets the strategy which is used by this configuration.
	 * 
	 * @return the strategy used to identify toolbar items
	 */
	protected ToolbarStrategy getStrategy(){
		return controller.getProperties().get(ToolbarStrategy.STRATEGY);
	}

	@Override
	public WindowConfiguration getConfiguration( ScreenDockStation station, Dockable dockable ){
		if (getStrategy().isToolbarPart(dockable)){
			final WindowConfiguration configuration = new WindowConfiguration();
			configuration.setMoveOnTitleGrab(true);
			configuration.setAllowDragAndDropOnTitle(true);
			configuration.setResetOnDropable(false);
			configuration.setResizeable(false);
			configuration.setTransparent( true );
			configuration.setShape( new ToolbarScreenWindowShape() );
			return configuration;
		}

		return null;
	}

}
