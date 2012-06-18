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

import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JDialog;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * This callback is delivered to a {@link CustomizationMenu}, it allows the menu to communicate
 * with its parent {@link DockStation}.
 * @author Benjamin Sigg
 */
public interface CustomizationMenuCallback {
	/**
	 * Gets the boundaries of the button that opened this menu. The button should remain
	 * visible.
	 * @return the location of the button, may be <code>null</code>
	 */
	public Rectangle getButton();
	
	/**
	 * Gets the parent {@link Component} of the menu, this {@link Component} can be used
	 * for example as parent of a {@link JDialog}.
	 * @return the parent {@link Component}, not <code>null</code>
	 */
	public Component getParent();
	
	/**
	 * Gets the owner of the menu.
	 * @return the owner, not <code>null</code>
	 */
	public DockStation getOwner();
	
	/**
	 * Adds <code>dockable</code> to the station or one of its sub stations.
	 * @param dockable the item to add
	 */
	public void append( Dockable dockable );
	
	/**
	 * Tells the menu whether it is currently allowed to close itself automatically.
	 * @return whether the menu is allowed to close itself
	 */
	public boolean isAutoCloseAllowed();
	
	/**
	 * To be called if the menu was closed.
	 */
	public void closed();
}
