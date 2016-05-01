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
package bibliothek.gui.dock.station.stack.menu;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;

/**
 * A {@link CombinedMenuContent} that opens a normal {@link JPopupMenu} to
 * display the selection.
 * @author Benjamin Sigg
 */
public class PopupCombinedMenuContent implements CombinedMenuContent{
	/** currently open menu */
	private JPopupMenu menu;
	
	/** the observers of this menu */
	private List<CombinedMenuContentListener> listeners = new ArrayList<CombinedMenuContentListener>();
	
	public void addCombinedMenuContentListener( CombinedMenuContentListener listener ){
		listeners.add( listener );
	}

	public void removeCombinedMenuContentListener( CombinedMenuContentListener listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Gets all the listeners that are currently registered.
	 * @return all the observers
	 */
	protected CombinedMenuContentListener[] listeners(){
		return listeners.toArray( new CombinedMenuContentListener[ listeners.size() ] );
	}

	public void open( DockController controller, Component parent, int x, int y, Item[] content ){
		cancel();
		
		menu = new JPopupMenu();
		menu.addPopupMenuListener( new PopupMenuListener(){
			public void popupMenuCanceled( PopupMenuEvent e ){
				cancel();
			}
			public void popupMenuWillBecomeInvisible( PopupMenuEvent e ){
				// ignore
			}
			public void popupMenuWillBecomeVisible( PopupMenuEvent e ){
				// ignore
			}
		});
		
		for( Item item : content ){
			menu.add( new ItemAction( item ) );
		}
		
		menu.show( parent, x, y );
		
		for( CombinedMenuContentListener listener : listeners() ){
			listener.opened( this );
		}
	}
	
	public void cancel(){
		if( menu != null ){	
			JPopupMenu menu = this.menu;
			this.menu = null;
			menu.setVisible( false );
			
			for( CombinedMenuContentListener listener : listeners() ){
				listener.canceled( this );
			}
		}
	}
	
	/**
	 * Closes this menu and informs all listeners that <code>dockable</code>
	 * has been selected.
	 * @param dockable the newly selected element
	 */
	public void select( Dockable dockable ){
		if( menu != null ){
			JPopupMenu menu = this.menu;
			this.menu = null;
			menu.setVisible( false );
			
			for( CombinedMenuContentListener listener : listeners() ){
				listener.selected( this, dockable );
			}
		}
	}

	/**
	 * A single item of this menu.
	 * @author Benjamin Sigg
	 */
	private class ItemAction extends AbstractAction{
		private Dockable dockable;
		
		/**
		 * Creates a new action.
		 * @param content text, icon and value of this action
		 */
		public ItemAction( Item content ){
			this.dockable = content.getDockable();
			putValue( NAME, content.getText() );
			putValue( SHORT_DESCRIPTION, content.getToolTip() );
			putValue( SMALL_ICON, content.getIcon() );
			setEnabled( content.isEnabled() );
		}
		
		public void actionPerformed( ActionEvent e ){
			select( dockable );
		}
	}
}
