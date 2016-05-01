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
package bibliothek.gui.dock.common.action.panel;

import bibliothek.gui.dock.common.action.CPanelPopup;

/**
 * A {@link PanelPopupWindow} is a wrapper around the element (window, dialog,
 * menu...) which shows the content of a {@link CPanelPopup}.
 * @author Benjamin Sigg
 */
public interface PanelPopupWindow {
	/**
	 * Makes this window invisible
	 */
	public void close();
	
	/**
	 * Tells whether this window is visible or not.
	 * @return <code>true</code> if visible
	 */
	public boolean isOpen();
	
	/**
	 * Adds a listener to this window.
	 * @param listener the new listener
	 */
	public void addListener( PanelPopupWindowListener listener );
	
	/**
	 * Removes a listener from this window.
	 * @param listener the listener
	 */
	public void removeListener( PanelPopupWindowListener listener );
}
