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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ButtonDockAction;

/**
 * A connection between a {@link ButtonDockAction} and a drop-down-button.
 * @author Benjamin Sigg
 */
public class ButtonDropDownHandler extends AbstractDropDownHandler<ButtonDockAction> {
	/** a listener to the menuitem of this handler */
	private Listener listener = new Listener();
	
	/**
	 * Creates a new handler.
	 * @param action the action to observe
	 * @param dockable the Dockable for which the action is shown
	 * @param item the item that represents the action, can be <code>null</code>
	 */
	public ButtonDropDownHandler( ButtonDockAction action, Dockable dockable, JMenuItem item ){
		super( action, dockable, item );
	}

	public void triggered(){
		action.action( dockable );
	}
	
	@Override
	public void bind(){
		super.bind();
		if( item != null ){
			item.addActionListener( listener );
		}
	}
	
	@Override
	public void unbind(){
		if( item != null ){
			item.removeActionListener( listener );
		}
		super.unbind();
	}
	
	/**
	 * A listener added to the menuitem. The listener calls
	 * {@link ButtonDropDownHandler#triggered()} whenever the menuitem
	 * is clicked.
	 * @author Benjamin Sigg
	 */
	private class Listener implements ActionListener{
		public void actionPerformed( ActionEvent e ){
			if( action.isDropDownTriggerable( dockable, false ))
				triggered();
		}
	}
}
