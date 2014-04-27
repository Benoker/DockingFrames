/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.action.popup;

import java.awt.Component;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.themes.basic.action.menu.MenuMenuHandler;

/**
 * This is the default implementation of {@link ActionPopupMenu}, this implementation
 * makes use of a {@link MenuMenuHandler} to create and update its content.
 * @author Benjamin Sigg
 */
public class DefaultActionPopupMenu implements ActionPopupMenu{
	/** the element for which this menu is shown */
	private Dockable dockable;
	
	/** the actual menu that is shown to the user */
	private JPopupMenu menu;
	
	/** this handler manages the items of {@link #menu} */
	private MenuMenuHandler handler;
	
	/** all the listeners that were added to this menu */
	private List<ActionPopupMenuListener> listeners = new ArrayList<ActionPopupMenuListener>();
	
	/** whether the {@link #menu} is currently open */
	private boolean showing = false;
	
	public DefaultActionPopupMenu( Dockable dockable, DockActionSource actions ){
		this( dockable, actions, new JPopupMenu() );
	}
	
	public DefaultActionPopupMenu( Dockable dockable, DockActionSource actions, JPopupMenu menu ){
		this.menu = menu;
		handler = new MenuMenuHandler( actions, dockable, menu );
		menu.addPopupMenuListener( new PopupMenuListener(){
			public void popupMenuWillBecomeInvisible( PopupMenuEvent e ){
            	EventQueue.invokeLater( new Runnable(){
            		public void run(){
            			// Delay destruction of handler to the time after the action is executed. This way
            			// events depending on the view can still be processed, e.g. change the selection of
            			// a SimpleDropDownButton
            			handler.unbind();
            			showing = false;
            			
            			fireClosed();
            		}
            	});
			}
			
			public void popupMenuWillBecomeVisible( PopupMenuEvent e ){
			}
			
			public void popupMenuCanceled( PopupMenuEvent e ){
				
			}
		});
	}
	
	private void fireClosed(){
		for( ActionPopupMenuListener listener : listeners.toArray( new ActionPopupMenuListener[ listeners.size() ] )){
			listener.closed( this );
		}
	}
	
	public void addListener( ActionPopupMenuListener listener ){
		listeners.add( listener );
	}

	public Dockable getDockable(){
		return dockable;
	}

	public void removeListener( ActionPopupMenuListener listener ){
		listeners.remove( listener );
	}

	public void show( Component owner, int x, int y ){
		if( !isShowing() ){
			handler.bind();
			menu.show( owner, x, y );
			showing = true;
		}
	}
	
	/**
	 * Tells whether the menu is currently open.
	 * @return <code>true</code> if the menu is open
	 */
	public boolean isShowing(){
		return showing;
	}
}
