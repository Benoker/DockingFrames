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

import bibliothek.gui.dock.station.stack.tab.MenuLineLayoutOrder.Item;
import bibliothek.gui.dock.station.stack.tab.layouting.LayoutBlock;
import bibliothek.gui.dock.station.stack.tab.layouting.LineTabsLayoutBlock;
import bibliothek.gui.dock.station.stack.tab.layouting.MenuLayoutBlock;
import bibliothek.gui.dock.station.stack.tab.layouting.TabsLayoutBlock;

/**
 * Default implementation of {@link MenuLineLayoutFactory}.
 * @author Benjamin Sigg
 */
public class DefaultMenuLineLayoutFactory implements MenuLineLayoutFactory{
	public LayoutBlock createInfo( MenuLineLayout layout, TabPane pane ){
		LonelyTabPaneComponent infoComponent = pane.getInfoComponent();
		if( infoComponent != null ){
			return infoComponent.toLayoutBlock();
		}
		return null;
	}
	
	public MenuLayoutBlock createMenu( MenuLineLayout layout, TabPane pane ){
		MenuLayoutBlock block = new MenuLayoutBlock();
		block.setMenu( pane.createMenu() );
		return block;
	}
	
	public TabsLayoutBlock createTabs( MenuLineLayout layout, TabPane pane ){
		LineTabsLayoutBlock block = new LineTabsLayoutBlock();
		block.setPane( pane );
		return block;
	}
	
	public MenuLineLayoutOrder createOrder( MenuLineLayout layout, TabPane pane ){
		MenuLineLayoutOrder order = new MenuLineLayoutOrder( Item.TABS, Item.MENU, Item.INFO );
		order.setConstraints( Item.TABS, 0.0f, 0.0f, 0.0f );
		order.setConstraints( Item.MENU, 1.0f, 0.0f, 0.0f );
		order.setConstraints( Item.INFO, 1.0f, 1.0f, 0.0f );
		return order;
	}
}
