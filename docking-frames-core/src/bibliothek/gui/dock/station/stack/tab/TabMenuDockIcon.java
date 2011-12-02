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
package bibliothek.gui.dock.station.stack.tab;

import bibliothek.gui.dock.util.UIValue;
import bibliothek.gui.dock.util.icon.DockIcon;
import bibliothek.util.Path;

/**
 * Represents the icon of a {@link TabMenu}.
 * @author Benjamin Sigg
 */
public abstract class TabMenuDockIcon extends DockIcon{
	/** the kind of this {@link UIValue} */
	public static final Path KIND_TAB_MENU = KIND_ICON.append( "tabMenu" );
	
	/** the menu which is represented by this {@link TabMenuDockIcon} */
	private TabMenu menu;
	
	/**
	 * Creates a new {@link DockIcon}
	 * @param id the unique identifier of this icon
	 * @param menu the menu which is represented by this icon
	 */
	public TabMenuDockIcon( String id, TabMenu menu ){
		super( id, KIND_TAB_MENU );
		this.menu = menu;
	}
	
	/**
	 * Gets the menu which is represented by this icon
	 * @return the menu
	 */
	public TabMenu getMenu(){
		return menu;
	}
}
