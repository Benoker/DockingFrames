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

package bibliothek.gui.dock.action.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.ButtonDockAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.disable.DisablingStrategy;

/**
 * A {@link ButtonDockAction} that has the same properties for all 
 * {@link Dockable Dockables} which use the action. <br>
 * @author Benjamin Sigg
 *
 */
public class SimpleButtonAction extends SimpleDropDownItemAction implements ButtonDockAction{
	/** A set of listeners observing this action */
	private List<ActionListener> listeners = new ArrayList<ActionListener>();
	
	/** A command delivered in each ActionEvent created by this action */
	private String command;
	
	/**
	 * Creates a new action
	 */
	public SimpleButtonAction(){
		this( true );
	}
	
	/**
	 * Creates a new action
	 * @param monitorDisabling whether to monitor the current {@link DisablingStrategy}
	 */
	public SimpleButtonAction( boolean monitorDisabling ){
		super( monitorDisabling );
	}
	
	public <V> V createView( ViewTarget<V> target, ActionViewConverter converter, Dockable dockable ){
		return converter.createView( ActionType.BUTTON, this, target, dockable );
	}
	
	/**
	 * Sets the command of this action. The <code>command</code> will be 
	 * set in each {@link ActionEvent} that is created and fired by
	 * this action.
	 * @param command the command, might be <code>null</code>
	 */
	public void setCommand( String command ){
		this.command = command;
	}
	
	/**
	 * Gets the command of this action.
	 * @return the command, might be <code>null</code>
	 * @see #setCommand(String)
	 */
	public String getCommand(){
		return command;
	}
	
	/**
	 * Adds a listener to this action. The listener will be notified whenever
	 * this action is triggered.
	 * @param listener the new listener
	 */
	public void addActionListener( ActionListener listener ){
		listeners.add( listener );
	}
	
	/**
	 * Removes a listener from this action.
	 * @param listener the listener to remove
	 */
	public void removeActionListener( ActionListener listener ){
		listeners.remove( listener );
	}
	
	public void action( Dockable dockable ){
		ActionEvent event = new ActionEvent( this, ActionEvent.ACTION_PERFORMED, command );
		
		for( ActionListener listener : listeners.toArray( new ActionListener[ listeners.size() ]))
			listener.actionPerformed( event );
	}
	
	public boolean trigger( Dockable dockable ) {
	    if( !isEnabled( dockable ))
	        return false;
	    
	    action( dockable );
	    return true;
	}
}
