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
package bibliothek.gui.dock.station.stack.tab;

import java.awt.Dimension;
import java.awt.Point;

import bibliothek.gui.dock.station.stack.tab.layouting.LayoutBlock;

/**
 * A {@link TabLayoutManager} is responsible for managing the location and size
 * of {@link TabPane#putOnTab(bibliothek.gui.Dockable) tags}, 
 * {@link TabPane#createMenu() menus} and {@link TabPane#getInfoComponent() info-component} 
 * of a {@link TabPane}. The manager can make use of {@link LayoutBlock}s to simplify
 * its algorithms.
 * @author Benjamin Sigg
 * @see TabPane#LAYOUT_MANAGER
 */
public interface TabLayoutManager {
	/**
	 * Lays out the tabs on <code>pane</code>, this manager is free to
	 * make any layout it wishes. However, the result should allow the user
	 * to still select any tab.
	 * @param pane the pane whose tabs should be positioned
	 */
	public void layout( TabPane pane );
	
	/**
	 * Gets the minimal size that {@link TabPane#getAvailableArea()}
	 * should return.
	 * @param pane some panel
	 * @return the minimal size of the available area of <code>pane</code>
	 */
	public Dimension getMinimumSize( TabPane pane );

	/**
	 * Gets the preferred size that {@link TabPane#getAvailableArea()}
	 * should return.
	 * @param pane some panel
	 * @return the preferred size of the available area of <code>pane</code>
	 */
	public Dimension getPreferredSize( TabPane pane );
	
	/**
	 * Finds out which tab is below the mouse at location <code>mouseLocation</code>.
	 * @param pane the panel for which to search the tab
	 * @param mouseLocation the location of the mouse
	 * @return the index of the tab beneath <code>mouseLocation</code> or <code>null</code>
	 */
	public int getIndexOfTabAt( TabPane pane, Point mouseLocation );
	
	/**
	 * Informs this {@link TabLayoutManager} that from now on it will have
	 * to layout <code>pane</code>.
	 * @param pane a pane that will be given to {@link #layout(TabPane)}
	 */
	public void install( TabPane pane );
	
	/**
	 * Informs this {@link TabLayoutManager} that it has no longer to
	 * look after <code>pane</code>.
	 * @param pane a {@link TabPane} that will no longer be given to
	 * {@link #layout(TabPane)}
	 */
	public void uninstall( TabPane pane );
}
