/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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

import bibliothek.gui.dock.station.stack.tab.layouting.AbstractTabsLayoutBlock;
import bibliothek.gui.dock.station.stack.tab.layouting.LayoutBlock;
import bibliothek.gui.dock.station.stack.tab.layouting.MenuLayoutBlock;
import bibliothek.gui.dock.station.stack.tab.layouting.TabsLayoutBlock;

/**
 * A factory creating various {@link LayoutBlock}s that are required by the {@link MenuLineLayout}. These objects
 * are responsible for laying out a subset of components, clients may replace this factory and return blocks that
 * behave different that the original blocks.
 * @author Benjamin Sigg
 */
public interface MenuLineLayoutFactory {
	/**
	 * Creates a new {@link LayoutBlock} representing the menu of <code>pane</code>. This method
	 * must call {@link MenuLayoutBlock#setMenu(TabMenu)} with an appropriate menu.
	 * @param layout the layout requesting the block
	 * @param pane the panel on which the menu will be shown
	 * @return the menu, not <code>null</code>
	 */
	public MenuLayoutBlock createMenu( MenuLineLayout layout, TabPane pane );
	
	/**
	 * Creates a new {@link LayoutBlock} representing the tabs of <code>pane</code>. This method
	 * may call {@link AbstractTabsLayoutBlock#setPane(TabPane)} with <code>pane</code>.
	 * @param layout the layout requesting the block
	 * @param pane the panel on which the tabs will be shown
	 * @return the tabs, not <code>null</code>
	 */
	public TabsLayoutBlock createTabs( MenuLineLayout layout, TabPane pane );
	
	/**
	 * Creates the {@link LayoutBlock} for the info component of <code>pane</code>. This method
	 * should just call {@link TabPane#getInfoComponent()} and {@link LonelyTabPaneComponent#toLayoutBlock()}.
	 * @param layout the layout requesting the block
	 * @param pane the panel on which the component will be shown
	 * @return the info block or <code>null</code>
	 */
	public LayoutBlock createInfo( MenuLineLayout layout, TabPane pane );
	
	/**
	 * Creates the order in which the {@link LayoutBlock}s should be presented.
	 * @param layout the layout requesting the order
	 * @param pane the panel on which the components will be shown
	 * @return the order, not <code>null</code>
	 */
	public MenuLineLayoutOrder createOrder( MenuLineLayout layout, TabPane pane );
}
