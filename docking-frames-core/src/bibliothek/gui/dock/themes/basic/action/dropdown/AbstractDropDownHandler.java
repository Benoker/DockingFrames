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
import java.util.Set;

import javax.swing.JMenuItem;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.StandardDockAction;
import bibliothek.gui.dock.action.StandardDropDownItemAction;
import bibliothek.gui.dock.action.dropdown.DropDownView;
import bibliothek.gui.dock.event.StandardDockActionListener;
import bibliothek.gui.dock.themes.basic.action.menu.AbstractMenuHandler;

/**
 * A handler that connects a {@link StandardDropDownItemAction} with a
 * drop-down-button.
 * @author Benjamin Sigg
 *
 * @param <S> the type of action handled by this handler
 */
public abstract class AbstractDropDownHandler<S extends StandardDropDownItemAction>
		extends AbstractMenuHandler<JMenuItem, S> 
		implements DropDownViewItem{

	/** a view where the handler may write some properties */
	private DropDownView view;
	/** a listener to the action */
	private Listener listener = new Listener();
	
	/**
	 * Creates an new handler.
	 * @param action the action to observe
	 * @param dockable the {@link Dockable} for which the action is shown.
	 * @param item the item that represents the action
	 */
	public AbstractDropDownHandler( S action, Dockable dockable, JMenuItem item ){
		super( action, dockable, item );
	}
	
	/**
	 * Gets the view that can be used to send properties directly to the drop-down-button.
	 * @return the view, can be <code>null</code>
	 */
	public DropDownView getView(){
		return view;
	}

	public void addActionListener( ActionListener listener ){
		if( item != null ){
			item.addActionListener( listener );
		}
	}

	public boolean isSelectable(){
		return action.isDropDownSelectable( dockable );
	}

	public boolean isTriggerable( boolean selected ){
		return action.isDropDownTriggerable( dockable, selected );
	}

	public void removeActionListener( ActionListener listener ){
		if( item != null ){
			item.removeActionListener( listener );
		}
	}

	public void setView( DropDownView view ){
		this.view = view;
		if( view != null ){
			view.setText( action.getText( dockable ) );
			view.setTooltip( action.getTooltipText( dockable ) );
			for( ActionContentModifier modifier : action.getIconContexts( dockable )){
				view.setIcon( modifier, action.getIcon( dockable, modifier ) );
			}
			view.setEnabled( action.isEnabled( dockable ) );
		}
	}
	
	@Override
	public void bind(){
		super.bind();
		action.addDockActionListener( listener );
		
		if( view != null ){
			view.setText( action.getText( dockable ) );
			view.setTooltip( action.getTooltipText( dockable ) );
			for( ActionContentModifier modifier : action.getIconContexts( dockable )){
				view.setIcon( modifier, action.getIcon( dockable, modifier ) );
			}
			view.setEnabled( action.isEnabled( dockable ) );
		}
	}
	
	@Override
	public void unbind(){
		action.removeDockActionListener( listener );
		super.unbind();
	}
	
	/**
	 * A listener to the action of this handler. Forwards changes to the
	 * {@link AbstractDropDownHandler#getView() view}.
	 * @author Benjamin Sigg
	 */
	private class Listener implements StandardDockActionListener{
		public void actionEnabledChanged( StandardDockAction action, Set<Dockable> dockables ){
			if( view != null && dockables.contains( dockable ))
				view.setEnabled( action.isEnabled( dockable ) );
		}
		
		public void actionRepresentativeChanged( StandardDockAction action, Set<Dockable> dockables ){
			if( view != null && dockables.contains( dockables ))
				view.setDockableRepresentation( action.getDockableRepresentation( dockable ) );
		}
		
		public void actionIconChanged( StandardDockAction action, ActionContentModifier modifier, Set<Dockable> dockables ){
			if( view != null && dockables.contains( dockable )){	
				if( modifier == null ){
					for( ActionContentModifier index : action.getIconContexts( dockable )){
						view.setIcon( index, action.getIcon( dockable, index ) );	
					}
				}
				else{
					view.setIcon( modifier, action.getIcon( dockable, modifier ) );
				}
			}
		}

		public void actionTextChanged( StandardDockAction action, Set<Dockable> dockables ){
			if( view != null && dockables.contains( dockable ))
				view.setText( action.getText( dockable ) );
		}

		public void actionTooltipTextChanged( StandardDockAction action, Set<Dockable> dockables ){
			if( view != null && dockables.contains( dockable ))
				view.setTooltip( action.getTooltipText( dockable ) );
		}
		
	}
}
