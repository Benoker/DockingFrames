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

package bibliothek.gui.dock.themes.basic.action.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JComponent;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.StandardDockAction;
import bibliothek.gui.dock.action.actions.SimpleMenuAction;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.event.StandardDockActionListener;
import bibliothek.gui.dock.themes.basic.action.dropdown.DropDownViewItem;

/**
 * A handler that shows a {@link DropDownAction} in a menu. The action is 
 * shown as if it were an ordinary menu.
 * @author Benjamin Sigg
 *
 */
public class DropDownMenuHandler implements MenuViewItem<JComponent>{
	/** an action that represents the {@link DropDownAction} as a menu */
	private SimpleMenuAction menuAction;
	/** a handler that sets up the view for {@link #menuAction} */
	private MenuMenuHandler handler;
	
	/** the action observed by this handler */
	private DropDownAction action;
	/** a listener to the action */
	private Listener listener = new Listener();
	/** the {@link Dockable} for which this view was created */
	private Dockable dockable;
	
	/** 
	 * A listener added to {@link #handler}, gets informed if the user triggers
	 * one the child actions. 
	 */
	private ActionListener menuListener = new ActionListener(){
		public void actionPerformed( ActionEvent e ){
			DockAction child = (DockAction)e.getSource();
			DropDownViewItem item = (DropDownViewItem)handler.getViewFor( child );
			if( item != null ){
				if( item.isSelectable() ){
					action.setSelection( dockable, child );		
				}
			}
		}
	};
	
	/**
	 * Creates a new handler
	 * @param action the action to show
	 * @param dockable the Dockable for which the action is shown
	 */
	public DropDownMenuHandler( DropDownAction action, Dockable dockable ){
		this.action = action;
		this.dockable = dockable;
	}

	public void addActionListener( ActionListener listener ){
		// ignore
	}

	public void removeActionListener( ActionListener listener ){
		// ignore
	}

	public void bind(){
		action.bind( dockable );
		DockActionSource source = action.getSubActions( dockable );
		menuAction = new SimpleMenuAction( source );
		handler = new MenuMenuHandler( menuAction, dockable, ViewTarget.DROP_DOWN );
		
		menuAction.setText( action.getText( dockable ) );
		menuAction.setTooltip( action.getTooltipText( dockable ) );
		menuAction.setEnabled( action.isEnabled( dockable ) );
		
		for( ActionContentModifier modifier : action.getIconContexts( dockable )){
			menuAction.setIcon( modifier, action.getIcon( dockable, modifier ) );
		}
		
		handler.bind();
		handler.addChildrenActionListener( menuListener );
		action.addDockActionListener( listener );
	}

	public void unbind(){
		handler.removeChildrenActionListener( menuListener );
		handler.unbind();
		action.removeDockActionListener( listener );
		action.unbind( dockable );
		
		menuAction = null;
		handler = null;
	}	
	
	public DockAction getAction(){
		return action;
	}

	public JComponent getItem(){
		return handler.getItem();
	}
	
	/**
	 * A listener to the action. Changes of icon, text, etc.. are forwarded
	 * to {@link DropDownMenuHandler#menuAction}.
	 * @author Benjamin Sigg
	 */
	private class Listener implements StandardDockActionListener{
		public void actionEnabledChanged( StandardDockAction action, Set<Dockable> dockables ){
			if( dockables.contains( dockable ))
				menuAction.setEnabled( action.isEnabled( dockable ) );
		}

		public void actionIconChanged( StandardDockAction action, ActionContentModifier modifier, Set<Dockable> dockables ){
			if( dockables.contains( dockable )){	
				if( modifier == null ){
					for( ActionContentModifier index : action.getIconContexts( dockable )){
						menuAction.setIcon( index, action.getIcon( dockable, index ) );	
					}
				}
				else{
					menuAction.setIcon( modifier, action.getIcon( dockable, modifier ) );
				}
			}
		}

		public void actionTextChanged( StandardDockAction action, Set<Dockable> dockables ){
			if( dockables.contains( dockable ))
				menuAction.setText( action.getText( dockable ) );
		}

		public void actionTooltipTextChanged( StandardDockAction action, Set<Dockable> dockables ){
			if( dockables.contains( dockable ))
				menuAction.setTooltip( action.getTooltipText( dockable ) );
		}
		
		public void actionRepresentativeChanged( StandardDockAction action, Set<Dockable> dockables ){
			if( dockables.contains( dockables ))
				menuAction.setDockableRepresentation( action.getDockableRepresentation( dockable ) );
		}
	}
}
