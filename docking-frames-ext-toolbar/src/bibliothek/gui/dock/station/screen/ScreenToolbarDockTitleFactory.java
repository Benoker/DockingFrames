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
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.title.NullTitleFactory;

/**
 * This {@link DockTitleFactory} creates special new titles for any
 * {@link Dockable} that is a {@link ToolbarStrategy#isToolbarPart(Dockable)}.
 * To be more exact: if a toolbar part is detected, the {@link DockFactory} with
 * key {@link #TITLE_ID} is called.
 * 
 * @author Benjamin Sigg
 */
public class ScreenToolbarDockTitleFactory implements DockTitleFactory {
	/** unique identifier for the {@link DockTitleVersion} used by this factory */
	public static final String TITLE_ID = "toolbar.screen";

	private final DockController controller;
	private final DockTitleVersion version;

	/**
	 * Creates a new factory.
	 * 
	 * @param controller
	 *            the controller in whose realm the titles are used
	 */
	public ScreenToolbarDockTitleFactory( DockController controller ){
		this.controller = controller;
		version = controller.getDockTitleManager().getVersion( TITLE_ID, NullTitleFactory.INSTANCE );
	}

	@Override
	public void install( DockTitleRequest request ){
		// ignored
	}

	@Override
	public void uninstall( DockTitleRequest request ){
		// ignored
	}

	@Override
	public void request( DockTitleRequest request ){
		final ToolbarStrategy strategy = controller.getProperties().get( ToolbarStrategy.STRATEGY );
		if( strategy.isToolbarPart( request.getTarget() ) ) {
			version.request( request );
		}
	}
}
