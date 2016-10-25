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

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;

/**
 * A {@link CustomizationMenuContent} is a part of the {@link CustomizationMenu}. A menu shows
 * exactly one {@link CustomizationMenuContent}, but the items themselves may consists of several
 * sub items. 
 * @author Benjamin Sigg
 */
public interface CustomizationMenuContent {
	/**
	 * Gets a {@link Component} which represents this item. If {@link #bind()} has not yet
	 * been called, or {@link #unbind()} has been called, then a result of <code>null</code> is valid.
	 * @return the view or <code>null</code> if this item is not {@link #bind() bound}.
	 */
	public Component getView();
	
	/**
	 * Informs this content about the {@link DockController} in whose realm it is used.
	 * @param controller the controller, can be <code>null</code>
	 */
	public void setController( DockController controller );
	
	/**
	 * Informs this item that is going to be used. This method must not be called twice in a row.
	 * @param callback access to more detailed information about the {@link DockStation} that is
	 * showing the menu
	 */
	public void bind( CustomizationMenuCallback callback );
	
	/**
	 * Informs this item that it is no longer used. This method must not be called twice in a row, it
	 * must be called after a call to {@link #bind()}.
	 */
	public void unbind();
}
