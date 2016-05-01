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

import java.awt.Point;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.tab.Tab;
import bibliothek.gui.dock.station.stack.tab.TabPane;

/**
 * A block managing a group of {@link Tab}s. This {@link LayoutBlock} is always 
 * visible.<br>
 * <b>Note:</b> This {@link LayoutBlock} does not keep track of new or removed
 * {@link Dockable}s on the owning {@link TabPane}. This block may show tabs
 * which are no longer valid.
 * @author Benjamin Sigg
 */
public interface TabsLayoutBlock extends LayoutBlock{
	/**
	 * Tells whether this block would be able to show all tabs if <code>size</code> would
	 * be applied.
	 * @param size the size that might be applied
	 * @return <code>true</code> if all tabs could be shown
	 */
	public boolean isAllTabs( Size size );
	
	/**
	 * Tells how many tabs could be shown if <code>size</code> would be applied.
	 * @param size the size that might be applied 
	 * @return the number of tabs that could be shown
	 */
	public int getTabsCount( Size size );
	
	/**
	 * Gets all the tabs that would be shown if <code>size</code> would be applied.
	 * @param size the size that might be applied
	 * @return the tabs that would be shown
	 */
	public Tab[] getTabs( Size size );
	
	/**
	 * Searches the index of the tab beneath <code>mouseLocation</code>.
	 * @param mouseLocation the location of the mouse
	 * @return the index of the tab beneath <code>mouseLocation</code> or <code>-1</code>
	 */
	public int getIndexOfTabAt( Point mouseLocation );
}
