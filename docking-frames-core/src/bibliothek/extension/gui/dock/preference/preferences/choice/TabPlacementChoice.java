/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.extension.gui.dock.preference.preferences.choice;

import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.util.DockProperties;

/**
 * Lets the user choose a {@link TabPlacement}.
 * @author Benjamin Sigg
 *
 */
public class TabPlacementChoice extends DefaultChoice<TabPlacement> {
	/**
	 * Creates a new choice
	 * @param properties default settings
	 */
	public TabPlacementChoice( DockProperties properties ){
		super( properties.getController() );
		
		addLinked( "theme", "preference.layout.tabplacement.theme", null );
		addLinked( "top", "preference.layout.tabplacement.top", TabPlacement.TOP_OF_DOCKABLE );
		addLinked( "bottom", "preference.layout.tabplacement.bottom", TabPlacement.BOTTOM_OF_DOCKABLE );
		addLinked( "left", "preference.layout.tabplacement.left", TabPlacement.LEFT_OF_DOCKABLE );
		addLinked( "right", "preference.layout.tabplacement.right", TabPlacement.RIGHT_OF_DOCKABLE );
		
		if( getDefaultChoice() == null ){
			setDefaultChoice( "theme" );
		}
	}
}
