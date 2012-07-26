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
package bibliothek.gui.dock.toolbar.perspective;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.ToolbarItemDockable;
import bibliothek.gui.dock.frontend.FrontendPerspectiveCacheExtension;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerDockPerspective;
import bibliothek.gui.dock.station.toolbar.ToolbarDockPerspective;
import bibliothek.gui.dock.station.toolbar.ToolbarGroupDockPerspective;

/**
 * This extension adds new types of {@link PerspectiveElement}s to the {@link DockFrontend}.
 * @author Benjamin Sigg
 */
public class ToolbarFrontendPerspectiveCacheExtension implements FrontendPerspectiveCacheExtension{
	@Override
	public PerspectiveElement get( String id, DockElement element, boolean isRootStation ){
		if( element instanceof ToolbarContainerDockStation ){
			return new ToolbarContainerDockPerspective();
		}
		if( element instanceof ToolbarGroupDockStation ){
			return new ToolbarGroupDockPerspective();
		}
		if( element instanceof ToolbarDockStation ){
			return new ToolbarDockPerspective();
		}
		if( element instanceof ToolbarItemDockable ){
			return new FrontendToolbarItemPerspective( id );
		}
		return null;
	}
	
	@Override
	public String get( PerspectiveElement element ){
		return null;
	}
}
