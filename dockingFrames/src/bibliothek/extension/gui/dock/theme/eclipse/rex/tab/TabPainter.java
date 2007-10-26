/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import java.awt.Component;
import java.awt.Graphics;

import bibliothek.extension.gui.dock.theme.eclipse.rex.RexTabbedComponent;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * @author Janni Kovacs
 */
public interface TabPainter {
    /**
     * Paints the background of <code>tabStrip</code>.
     * @param tabbedComponent the {@link RexTabbedComponent} for which <code>tabStrip</code> is shown
     * @param tabStrip the tabs of <code>tabbedComponent</code>
     * @param g the graphics used to paint on <code>tabStrip</code> 
     */
	public void paintTabStrip( RexTabbedComponent tabbedComponent, Component tabStrip, Graphics g);
	
	/**
	 * Creates a new entry for the tab-strip above the contents of a 
	 * {@link RexTabbedComponent}.
	 * @param controller the current controller, never <code>null</code>
	 * @param component the owner of the tab, clients might need
	 * {@link RexTabbedComponent#getStation() component.getStation()} to get
	 * access to the {@link DockStation} for which the tab is intended
	 * @param dockable the element for which the tab is shown
	 * @param index the initial location of the tab
	 * @return the new tab, never <code>null</code>
	 */
	public TabComponent createTabComponent( DockController controller, RexTabbedComponent component, Dockable dockable, int index );
}
