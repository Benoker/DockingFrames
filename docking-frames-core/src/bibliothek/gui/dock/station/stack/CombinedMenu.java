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
package bibliothek.gui.dock.station.stack;

import java.awt.Component;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.tab.TabMenu;

/**
 * A menu that is displayed on a {@link CombinedStackDockComponent}. Clients
 * should call {@link #setController(DockController)} when they use this menu,
 * they should set the <code>controller</code> to <code>null</code> when they
 * no longer use this menu.
 * @author Benjamin Sigg
 */
public interface CombinedMenu extends TabMenu{
	/**
	 * Sets the controller in whose realm this menu is used.
	 * @param controller the realm
	 */
	public void setController( DockController controller );
	
	/**
	 * Gets the component that paints and represents this menu.
	 * @return this menu
	 */
	public Component getComponent();
	
	/**
	 * Sets the text of this menu at <code>index</code>.
	 * @param index the entry that should be modified
	 * @param text the text to display
	 */
	public void setText( int index, String text );
	
	/**
	 * Sets the image of this menu at <code>index</code>.
	 * @param index the entry that should be modified
	 * @param icon an icon that should be shown, can be <code>null</code>
	 */
	public void setIcon( int index, Icon icon );
	
	/**
	 * Sets the tooltip of this menu at <code>index</code>.
	 * @param index the entry that should be modified
	 * @param tooltip the tooltip text, can be <code>null</code>
	 */
	public void setTooltip( int index, String tooltip );
	
	/**
	 * Enables or disables a menu entry. A disabled menu entry cannot be selected.
	 * @param index the index of the item that should be enabled or disabled
	 * @param enabled whether the item is active
	 */
	public void setEnabled( int index, boolean enabled );
	
	/**
	 * Inserts a new item at <code>index</code> in this menu.
	 * @param index the location of the new item
	 * @param dockable the new item
	 */
	public void insert( int index, Dockable dockable );
	
	/**
	 * Removes the item <code>dockable</code> from this menu
	 * @param dockable the item to remove
	 */
	public void remove( Dockable dockable );
}
