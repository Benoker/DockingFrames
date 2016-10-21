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
package bibliothek.gui.dock.themes.icon;

import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;

import bibliothek.gui.dock.station.stack.tab.TabMenu;
import bibliothek.gui.dock.station.stack.tab.TabMenuDockIcon;
import bibliothek.gui.dock.station.stack.tab.TabMenuListener;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.icon.DockIcon;
import bibliothek.gui.dock.util.icon.DockIconBridge;

/**
 * This {@link DockIconBridge} searches for {@link TabMenuDockIcon}s and sets a custom icon using the
 * current size of the {@link TabMenu} to select the icon.<br>
 * This bridge must be installed at the {@link IconManager} using  {@link TabMenuDockIcon#KIND_TAB_MENU}
 * as path of the type. Clients may subclass this bridge and override {@link #createIcon(TabMenu)} in order
 * to use a custom icon. This bridge assumes that {@link #ICON_KEY} is not set, the bridges disables itself
 * if the icon is set by the client.
 * @author Benjamin Sigg
 */
public class TabMenuOverflowIconBridge implements DockIconBridge {
	/** The key of the icon that is observed by this bridge */
	public static final String ICON_KEY = "dock.menu.overflow";
	
	/** all menus that are currently registered */
	private Set<TabMenuDockIcon> menus = new HashSet<TabMenuDockIcon>();
	
	/** all menus whose icon has been set from outside */
	private Set<TabMenuDockIcon> protectedMenus = new HashSet<TabMenuDockIcon>();
	
	/** Observers the menus known to this bridge and updates their icons if necessary */
	private TabMenuListener listener = new Listener();
	
	public void add( String id, DockIcon icon ){
		if( id.equals( ICON_KEY )){
			TabMenuDockIcon menu = (TabMenuDockIcon)icon;
			menus.add( menu );
			menu.getMenu().addTabMenuListener( listener );
		}
	}

	public void remove( String id, DockIcon icon ){
		if( id.equals( ICON_KEY )){
			TabMenuDockIcon menu = (TabMenuDockIcon)icon;
			menus.remove( menu );
			protectedMenus.remove( menu );
			menu.getMenu().removeTabMenuListener( listener );
			icon.set( null );
		}
	}

	public void set( String id, Icon value, DockIcon icon ){
		if( id.equals( ICON_KEY )){
			if( value == null ){
				value = createIcon( ((TabMenuDockIcon)icon).getMenu() );
				protectedMenus.remove( icon );
			}
			else{
				protectedMenus.add( (TabMenuDockIcon)icon );
			}
			icon.set( value );
		}
	}

	private void update( TabMenu menu ){
		for( TabMenuDockIcon icon : menus ){
			if( icon.getMenu() == menu ){
				if( !protectedMenus.contains( icon )){
					icon.set( createIcon( menu ) );
					return;
				}
			}
		}
	}
	
	/**
	 * Returns an icon that represents <code>menu</code> in its current state. This method is called
	 * every time when the number of children of <code>menu</code> changes.
	 * @param menu the menu for which an icon is required
	 * @return the icon, can (but should not) be <code>null</code>
	 */
	protected Icon createIcon( TabMenu menu ){
		return new TabMenuOverflowIcon( menu.getDockableCount() );
	}
	
	private class Listener implements TabMenuListener{
		public void dockablesAdded( TabMenu source, int offset, int length ){
			update( source );
		}
		
		public void dockablesRemoved( TabMenu source, int offset, int length ){
			update( source );
		}
	}
}
