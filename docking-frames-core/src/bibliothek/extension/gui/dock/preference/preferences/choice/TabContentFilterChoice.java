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
package bibliothek.extension.gui.dock.preference.preferences.choice;

import bibliothek.gui.dock.station.stack.tab.DefaultTabContentFilter;
import bibliothek.gui.dock.station.stack.tab.DefaultTabContentFilter.Behavior;
import bibliothek.gui.dock.station.stack.tab.TabContentFilter;
import bibliothek.gui.dock.util.DockProperties;

/**
 * A set of choices of {@link TabContentFilter}s.
 * @author Benjamin Sigg
 */
public class TabContentFilterChoice extends DefaultChoice<TabContentFilter> {
	/**
	 * Creates a new choice
	 * @param properties default settings
	 */
	public TabContentFilterChoice( DockProperties properties ){
		super( properties.getController() );
		
		addLinked( "all", "preference.layout.tabcontentfilter.all", null );
		addLinked( "icon", "preference.layout.tabcontentfilter.icon", new DefaultTabContentFilter( Behavior.ICON_ONLY ) );
		addLinked( "title", "preference.layout.tabcontentfilter.title", new DefaultTabContentFilter( Behavior.TEXT_ONLY ) );
		addLinked( "iconOrTitle", "preference.layout.tabcontentfilter.iconOrTitle", new DefaultTabContentFilter( Behavior.ALL, Behavior.TEXT_ONLY ) );
		addLinked( "titleOrIcon", "preference.layout.tabcontentfilter.titleOrIcon", new DefaultTabContentFilter( Behavior.ALL, Behavior.ICON_ONLY ) );
		
		if( getDefaultChoice() == null ){
			setDefaultChoice( "all" );
		}
	}
}
