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

package bibliothek.gui.dock.control;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.event.KeyboardListener;
import bibliothek.gui.dock.event.LocatedListenerList;

/**
 * An observer of all KeyEvents, forwarding them to registered listeners. The
 * listeners are only informed about events that occurred in a part of the 
 * dock-tree below them.
 * @author Benjamin Sigg
 *
 */
public abstract class KeyboardController {
	/** the list of listeners */
	private LocatedListenerList<KeyboardListener> keyListeners = 
		new LocatedListenerList<KeyboardListener>();
	
	/** the listeners which will be informed about any events */
	private List<KeyListener> globalListeners = new ArrayList<KeyListener>();
	
	/** the controller in whose realm this {@link KeyboardController} works */
	private DockController controller;
	
	/**
	 * Creates a new {@link KeyboardController}.
	 * @param controller the controller in whose realm this <code>KeyBoardController</code>
	 * will work.
	 */
	public KeyboardController( DockController controller ){
		this.controller = controller;
	}
	
	/**
	 * Adds a global key listener to this controller. Global {@link KeyListener}s
	 * will receive a notification for every event that this controller handles.
	 * The listeners will also be informed about events that are consumed.
	 * @param listener the new listener
	 */
	public void addGlobalListener( KeyListener listener ){
	    if( listener == null )
	        throw new IllegalArgumentException( "listener must not be null" );
	    globalListeners.add( listener );
	}
	
	/**
	 * Removes a listener from this controller.
	 * @param listener the listener to remove
	 */
	public void removeGlobalListener( KeyListener listener ){
	    globalListeners.remove( listener );
	}
	
	/**
	 * Adds a listener to this controller. The listener will be invoked
	 * when a {@link java.awt.event.KeyEvent} occurs in the subtree below
	 * the listeners {@link bibliothek.gui.dock.DockElement}.
	 * @param listener the new listener
	 */
	public void addListener( KeyboardListener listener ){
	    if( listener == null )
	        throw new IllegalArgumentException( "listener must not be null" );
	    
		keyListeners.addListener( listener );
	}
	
	/**
	 * Removes a listener from this controller.
	 * @param listener the listener to remove
	 */
	public void removeListener( KeyboardListener listener ){
		keyListeners.removeListener( listener );
	}
	
	/**
	 * Gets the {@link DockController} in whose realm this {@link KeyboardController} works.
	 * @return the owner of this controller
	 */
	public DockController getController() {
        return controller;
    }
	
	/**
	 * Stops this controller. This controller has to remove any resources
	 * it uses and has not to be useful any further.
	 */
	public abstract void kill();
	
	/**
	 * Forwards <code>event</code> to all listeners whose 
	 * {@link bibliothek.gui.dock.DockElement} is above the component
	 * on which the event occurred.
	 * @param event the event to send
	 */
	protected void fireKeyPressed( KeyEvent event ){
		if( !event.isConsumed() ){
    		DockElement element = controller.searchElement( event.getComponent() );
    		if( element != null ){
        		List<KeyboardListener> list = keyListeners.affected( element );
        		loop:for( KeyboardListener listener : list ){
        			if( listener.keyPressed( element, event )){
        				event.consume();
        				break loop;
        			}
        		}
    		}
		}
		
		for( KeyListener listener : globalListeners.toArray( new KeyListener[ globalListeners.size() ] )){
		    listener.keyPressed( event );
		}
	}
	
	/**
	 * Forwards <code>event</code> to all listeners whose 
	 * {@link bibliothek.gui.dock.DockElement} is above the component
	 * on which the event occurred.
	 * @param event the event to send
	 */
	protected void fireKeyReleased( KeyEvent event ){
		if( !event.isConsumed() ){
			DockElement element = controller.searchElement( event.getComponent() );
    		if( element != null ){
        		List<KeyboardListener> list = keyListeners.affected( element );
        		loop:for( KeyboardListener listener : list ){
        			if( listener.keyReleased( element, event )){
        				event.consume();
        				break loop;
        			}
        		}
    		}
		}

		for( KeyListener listener : globalListeners.toArray( new KeyListener[ globalListeners.size() ] )){
		    listener.keyReleased( event );
		}
	}
	
	/**
	 * Forwards <code>event</code> to all listeners whose 
	 * {@link bibliothek.gui.dock.DockElement} is above the component
	 * on which the event occurred.
	 * @param event the event to send
	 */
	protected void fireKeyTyped( KeyEvent event ){
		if( !event.isConsumed() ){
			DockElement element = controller.searchElement( event.getComponent() );
    		if( element != null ){
        		List<KeyboardListener> list = keyListeners.affected( element );
        		loop:for( KeyboardListener listener : list ){
        			if( listener.keyTyped( element, event )){
        				event.consume();
        				break loop;
        			}
        		}
    		}
		}

		for( KeyListener listener : globalListeners.toArray( new KeyListener[ globalListeners.size() ] )){
		    listener.keyTyped( event );
		}
	}
}
