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
package bibliothek.gui.dock.common.intern.action.panel;

import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.action.CPanelPopup.PanelPopup;
import bibliothek.gui.dock.themes.basic.action.menu.AbstractMenuHandler;

/**
 * A menu showing a custom component instead of menu items.
 * @author Benjamin Sigg
 */
public class BasicPanelPopupMenuHandler extends AbstractMenuHandler<JMenu, PanelPopup>{
	/**
	 * Creates a new handler.
	 * @param action the action shown in this menu
	 * @param dockable the owner of the action
	 */
	public BasicPanelPopupMenuHandler( PanelPopup action, Dockable dockable ){
		super( action, dockable, new JMenu() );
		item.getPopupMenu().addPopupMenuListener( new PopupMenuListener(){
			public void popupMenuWillBecomeVisible( PopupMenuEvent e ){
				BasicPanelPopupMenuHandler.this.action.onMenuTrigger( item.getPopupMenu() );
			}
			
			public void popupMenuCanceled( PopupMenuEvent e ){
				item.getPopupMenu().removeAll();
			}
			
			public void popupMenuWillBecomeInvisible( PopupMenuEvent e ){
				item.getPopupMenu().removeAll();
			}
		});
	}

	public void addActionListener( ActionListener listener ){
		// ignore
	}

	public void removeActionListener( ActionListener listener ){
		// ignore
	}
}
