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

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.CombinedMenu;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.property.DynamicPropertyFactory;

/**
 * A popup menu often used by {@link CombinedMenu}s to show their content.
 * The items of a {@link CombinedMenuContent} are not  supposed to change while
 * the menu is visible and all state is forgotten once the menu is closed. 
 * A {@link CombinedMenuContent} might be used at the same time by more than one client.
 * @author Benjamin Sigg
 */
public interface CombinedMenuContent {
	/** Key that should be used to access the default menu content */
	public static final PropertyKey<CombinedMenuContent> MENU_CONTENT = new PropertyKey<CombinedMenuContent>(
			"dock.CombinedMenuContent", new DynamicPropertyFactory<CombinedMenuContent>(){
				public CombinedMenuContent getDefault( PropertyKey<CombinedMenuContent> key, DockProperties properties ){
					return new PopupCombinedMenuContent();
				}
			}, true );
	
	/**
	 * Describes one item of a menu.
	 * @author Benjamin Sigg
	 */
	public static class Item{
		private Dockable dockable;
		private String text;
		private String tooltip;
		private Icon icon;
		private boolean enabled;
		
		/**
		 * Creates a new {@link Item}.
		 * @param dockable the items value
		 * @param text the items text
		 * @param tooltip the items description
		 * @param icon the items icon
		 * @param enabled whether this item is selectable
		 */
		public Item( Dockable dockable, String text, String tooltip, Icon icon, boolean enabled ){
			if( dockable == null )
				throw new IllegalArgumentException( "dockable must not be null" );
			
			this.dockable = dockable;
			this.text = text;
			this.tooltip = tooltip;
			this.icon = icon;
			this.enabled = enabled;
		}
		
		/**
		 * Gets the element which is represented by this item.
		 * @return the element, may not be <code>null</code>
		 */
		public Dockable getDockable(){
			return dockable;
		}
		
		/**
		 * Gets the text to be shown to the user.
		 * @return the title, may be <code>null</code>
		 */
		public String getText(){
			return text;
		}
		
		/**
		 * Gets the description of this item.
		 * @return the description, may be <code>null</code>
		 */
		public String getToolTip(){
			return tooltip;
		}
		
		/**
		 * Gets the icon of this item.
		 * @return the icon, may be <code>null</code>
		 */
		public Icon getIcon(){
			return icon;
		}
		
		/**
		 * Tells whether this item can be selected.
		 * @return whether this item is enabled
		 */
		public boolean isEnabled(){
			return enabled;
		}
	}
	
	/**
	 * Adds a listener to this menu, the listener has to be informed when
	 * this menu is made visible or invisible.
	 * @param listener the new listener
	 */
	public void addCombinedMenuContentListener( CombinedMenuContentListener listener );
	
	/**
	 * Removes a listener from this menu.
	 * @param listener the listener to remove
	 */
	public void removeCombinedMenuContentListener( CombinedMenuContentListener listener );
	
	/**
	 * Shows this menu at the given location.
	 * @param controller the controller in whose realm this menu is used, 
	 * should not, but might, be <code>null</code>
	 * @param parent the component which serves as parent for any dialog,
	 * popup menu, etc...
	 * @param x the preferred x coordinate of the menu in respect to <code>component</code>
	 * @param y the preferred y coordinate of the menu in respect to <code>component</code> 
	 * @param content the content of the menu
	 */
	public void open( DockController controller, Component parent, int x, int y, Item[] content );
	
	/**
	 * Closes the menu if it is currently open
	 */
	public void cancel();
}
