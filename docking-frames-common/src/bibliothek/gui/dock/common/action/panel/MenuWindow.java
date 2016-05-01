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

import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * A wrapper around a {@link JPopupMenu}.
 * @author Benjamin Sigg
 */
public class MenuWindow extends AbstractPanelPopupWindow{
	/** the menu of this window */
	private JPopupMenu menu;
	
	/** whether the menu is closed */
	private boolean closed = false;
	
	/** a listener to {@link #menu} */
	private PopupMenuListener listener = new PopupMenuListener(){
		public void popupMenuCanceled( PopupMenuEvent e ){
			closing();
		}
		public void popupMenuWillBecomeInvisible( PopupMenuEvent e ){
			closing();
		}
		public void popupMenuWillBecomeVisible( PopupMenuEvent e ){
			// ignore
		}
	};
	
	/**
	 * Creates the new window.
	 * @param menu the menu that is managed by this window
	 */
	public MenuWindow( JPopupMenu menu ){
		this.menu = menu;
		menu.addPopupMenuListener( listener );
	}
	
	public boolean isOpen(){
		return !closed;
	}
	
	public void close(){
		menu.setVisible( false );
	}
	
	private void closing(){
		closed = true;
		menu.removePopupMenuListener( listener );
		menu.removeAll();
		fireClosed();
	}
}
