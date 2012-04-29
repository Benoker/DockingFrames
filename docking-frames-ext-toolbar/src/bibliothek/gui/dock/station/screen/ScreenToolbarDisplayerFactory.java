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
import bibliothek.gui.dock.displayer.DisplayerRequest;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.themes.DefaultDisplayerFactoryValue;
import bibliothek.gui.dock.themes.ThemeManager;

/**
 * A {@link DisplayerFactory} creating special {@link DockableDisplayer}s for
 * items where {@link ToolbarStrategy#isToolbarPart(bibliothek.gui.Dockable)}
 * returns <code>true</code>. This factory forwards any calls to a
 * {@link DisplayerFactory} that is registered at the {@link ThemeManager} with
 * a key "toolbar.screen".
 * 
 * @author Benjamin Sigg
 */
public class ScreenToolbarDisplayerFactory implements DisplayerFactory {
	private final DockController controller;

	/**
	 * Creates a new factory
	 * 
	 * @param controller
	 *            the controller in whose realm this factory is used
	 */
	public ScreenToolbarDisplayerFactory( DockController controller ){
		this.controller = controller;
	}

	@Override
	public void request( DisplayerRequest request ){
		final ToolbarStrategy strategy = controller.getProperties().get( ToolbarStrategy.STRATEGY );
		if( strategy.isToolbarPart( request.getTarget() ) ) {
			final DefaultDisplayerFactoryValue value = new DefaultDisplayerFactoryValue( ThemeManager.DISPLAYER_FACTORY + ".toolbar.screen", request.getParent() );
			try {
				value.setController( request.getController() );
				final DisplayerFactory factory = value.get();
				if( factory != null ) {
					factory.request( request );
				}
			}
			finally {
				value.setController( null );
			}
		}
	}
}
