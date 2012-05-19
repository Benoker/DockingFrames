/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.station.toolbar.menu;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.ToolbarGroupDockStation;

/**
 * A {@link CustomizationMenu} is a menu that is shown belonging to a {@link ToolbarGroupDockStation},
 * the menu can offer various settings to customize the station.<br>
 * Clients should call {@link #setController(DockController)} and {@link #setContent(CustomizationMenuContent)} to 
 * set up the menu.
 * @author Benjamin Sigg
 */
public interface CustomizationMenu {
	/**
	 * Gets the contents of this menu.
	 * @return the contents, can be <code>null</code>
	 */
	public CustomizationMenuContent getContent();
	
	/**
	 * Sets the contents of this menu.
	 * @param content the new contents, can be <code>null</code>
	 */
	public void setContent( CustomizationMenuContent content );
	
	/**
	 * Sets the controller in whose realm this menu is used.
	 * @param controller the new controller, can be <code>null</code>
	 */
	public void setController( DockController controller );
	
	/**
	 * Opens the menu for <code>station</code>. The menus top left corner should be at coordinates
	 * <code>x,y</code>.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param callback allows communication with the station that opened this menu
	 * @throws IllegalArgumentException if <code>callback</code> is <code>null</code>
	 * @throws IllegalStateException if there is no content to show
	 */
	public void open( int x, int y, CustomizationMenuCallback callback );
	
	/**
	 * Closes this menu.
	 */
	public void close();
}
