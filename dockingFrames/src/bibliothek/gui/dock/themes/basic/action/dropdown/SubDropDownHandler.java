/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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

package bibliothek.gui.dock.themes.basic.action.dropdown;

import java.awt.event.ActionListener;

import javax.swing.JComponent;

import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.dropdown.DropDownView;
import bibliothek.gui.dock.themes.basic.action.menu.MenuViewItem;

/**
 * A handler that connects non-selectable, non-triggerable items with a 
 * drop-down-button.
 * @author Benjamin Sigg
 */
public class SubDropDownHandler implements DropDownViewItem {
	/** the view of the item in the menu */
	private MenuViewItem<JComponent> view;
	
	/**
	 * Creates a new handler.
	 * @param view the item as it will appear in the menu
	 */
	public SubDropDownHandler( MenuViewItem<JComponent> view ){
		this.view = view;
	}

	public void triggered(){
		// that will never happen
	}

	public void setView( DropDownView view ){
		// will never happen
	}
	
	public boolean isSelectable(){
		return false;
	}

	public boolean isTriggerable( boolean selected ){
		return false;
	}

	public void addActionListener( ActionListener listener ){
		view.addActionListener( listener );
	}

	public void removeActionListener( ActionListener listener ){
		view.removeActionListener( listener );
	}

	public void bind(){
		view.bind();
	}

	public DockAction getAction(){
		return view.getAction();
	}

	public JComponent getItem(){
		return view.getItem();
	}

	public void unbind(){
		view.unbind();
	}
}
