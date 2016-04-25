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
package bibliothek.extension.gui.dock.theme.eclipse.stack.tab;

import javax.swing.border.Border;

import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseTabPane;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * A {@link TabPainter} is used to setup the basic graphical elements
 * of a {@link EclipseTabPane}.
 * @author Janni Kovacs
 * @author Benjamin Sigg
 */
public interface TabPainter {
    /**
	 * Creates a new painter for the component which contains the tabs.
	 * @param pane the panel for which this painter will be used
	 * @return the new painter, can be <code>null</code>
	 */
	public TabPanePainter createDecorationPainter( EclipseTabPane pane );
	
	/**
	 * Creates a new tab for an {@link EclipseTabPane}. At the time this method
	 * is called the {@link EclipseTabPane} is connected to a {@link DockController}
	 * which will not change as long as the created {@link TabComponent} is 
	 * in use. The {@link EclipseTabPane} is also connected to a {@link DockStation}
	 * which will neither change as long as the tab is in use.
	 * @param pane the panel for which this tab is required
	 * @param dockable the element for which the tab is shown
	 * @return the new tab, never <code>null</code>
	 */
	public TabComponent createTabComponent( EclipseTabPane pane, Dockable dockable );
	
	/**
	 * Creates a new invisible tab for <code>pane</code> representing <code>dockable</code>.
	 * @param pane the owner of the new tab
	 * @param dockable what the new tab represents
	 * @return the new tab, never <code>null</code>
	 */
	public InvisibleTab createInvisibleTab( InvisibleTabPane pane, Dockable dockable );
	
	/**
	 * Gets the border which will be around <code>pane</code> when <code>dockable</code>
	 * is selected.
	 * @param owner the component on which the border will be shown
	 * @param controller the current controller, never <code>null</code>
	 * @param dockable the element which might influence the border, never <code>null</code>
	 * @return the border of <code>dockable</code> or <code>null</code>
	 */
	public Border getFullBorder( BorderedComponent owner, DockController controller, Dockable dockable );
}
