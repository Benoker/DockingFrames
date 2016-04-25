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
package bibliothek.gui.dock.station.stack.tab.layouting;

import bibliothek.gui.dock.station.stack.tab.TabMenu;

/**
 * A wrapper around a {@link TabMenu}.
 * @author Benjamin Sigg
 */
public class MenuLayoutBlock extends ComponentLayoutBlock<TabMenu>{
	/**
	 * Sets the menu for this block.
	 * @param menu the menu, may be <code>null</code>
	 * @see ComponentLayoutBlock#setComponent(bibliothek.gui.dock.station.stack.tab.TabPaneComponent)
	 */
	public void setMenu( TabMenu menu ){
		setComponent( menu );
	}
	
	/**
	 * Gets the menu of this block.
	 * @return the menu, may be <code>null</code>
	 * @see #getComponent()
	 */
	public TabMenu getMenu(){
		return getComponent();
	}
}
