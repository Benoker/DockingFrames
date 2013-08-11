/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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
package bibliothek.gui.dock.component;

import java.awt.Component;

import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DockableDisplayer;

/**
 * This root handler stops traversing {@link Component}s when discovering a {@link DockableDisplayer}
 * @author Benjamin Sigg
 */
public class DefaultDockStationComponentRootHandler extends DockComponentRootHandler{
	/**
	 * All the known displayers.
	 */
	private DisplayerCollection displayers;
	
	/**
	 * Creates a new handler.
	 * @param root the components represented by <code>this</code>, not <code>null</code>
	 * @param displayers all the {@link DockableDisplayer}s, not <code>null</code>
	 */
	public DefaultDockStationComponentRootHandler( DockComponentRoot root, DisplayerCollection displayers ){
		super( root );
		this.displayers = displayers;
	}
	
	@Override
	protected TraverseResult shouldTraverse( Component component ) {
		if( displayers.isDisplayerComponent( component )){
			return TraverseResult.EXCLUDE;
		}
		return TraverseResult.INCLUDE_CHILDREN;
	}
}
