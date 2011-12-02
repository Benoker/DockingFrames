/**
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
import java.util.Set;

import javax.swing.JMenuItem;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.action.dropdown.DropDownView;
import bibliothek.gui.dock.event.SelectableDockActionListener;

/**
 * A connection between a {@link SelectableDockAction} and a 
 * drop-down-button.
 * @author Benjamin Sigg
 */
public class SelectableDropDownHandler extends AbstractDropDownHandler<SelectableDockAction> {
	/** a listener to the action and the view */
	private Listener listener = new Listener();
	
	/**
	 * Creates a new handler.
	 * @param action the action to observe
	 * @param dockable the Dockable for which the action is shown
	 * @param item the graphical representation of the action
	 */
	public SelectableDropDownHandler( SelectableDockAction action, Dockable dockable, JMenuItem item ){
		super( action, dockable, item );
		item.addActionListener( listener );
	}
	
	@Override
	public void bind(){
		super.bind();
		boolean selected = action.isSelected( dockable );
		item.setSelected( selected );
		action.addSelectableListener( listener );
		if( getView() != null )
			getView().setSelected( selected );
	}
	
	@Override
	public void unbind(){
		action.removeSelectableListener( listener );
		super.unbind();
	}
	
	@Override
	public void setView( DropDownView view ){
		super.setView( view );
		if( view != null )
			view.setSelected( action.isSelected( dockable ) );
	}

	/**
	 * Called if the menuitem was clicked. The default implementation forwards
	 * the call to {@link #triggered()}. This method is not called if the item
	 * if in a drop-down-menu and cannot be triggered in the unselected-state.
	 */
	protected void itemTriggered(){
	    triggered();
	}
	
	public void triggered() {
	    action.trigger( dockable );
	    item.setSelected( action.isSelected( dockable ) );
	}
	
	/**
	 * A listener that ensures, that the menuitem and the action have
	 * always the same selected-state.
	 * @author Benjamin Sigg
	 */
	private class Listener implements SelectableDockActionListener, ActionListener{
		public void actionPerformed( ActionEvent e ){
			if( action.isDropDownTriggerable( dockable, false ))
				itemTriggered();
			else
				item.setSelected( action.isSelected( dockable ));
		}
		
		public void selectedChanged( SelectableDockAction action, Set<Dockable> dockables ){
			if( dockables.contains( dockable ) ){
				boolean selected = action.isSelected( dockable );
				
				if( getView() != null ){
					getView().setSelected( selected );
				}
				
				item.setSelected( selected );
			}
		}
	}
}
