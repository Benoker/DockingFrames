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
package bibliothek.gui.dock.control;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.event.ControllerSetupListener;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * This default implementation of a {@link GlobalMouseDispatcher} uses an {@link AWTEventListener} is possible, or
 * just forwards events that are delivered to the {@link #dispatch(MouseEvent)} method.
 * @author Benjamin Sigg
 */
public class DefaultGlobalMouseDispatcher implements GlobalMouseDispatcher {
	/** The listener to all AWT events*/
	private AWTEventListener listener;

	private List<MouseListener> mouseListeners = new ArrayList<MouseListener>();
	private MouseListener[] mouseListenersCache = null;

	private List<MouseMotionListener> mouseMotionListeners = new ArrayList<MouseMotionListener>();
	private MouseMotionListener[] mouseMotionListenersCache = null;

	private List<MouseWheelListener> mouseWheelListeners = new ArrayList<MouseWheelListener>();
	private MouseWheelListener[] mouseWheelListenersCache = null;

	/** whether the application is in restricted mode or not */
	private PropertyValue<Boolean> restricted = new PropertyValue<Boolean>( DockController.RESTRICTED_ENVIRONMENT ){
		protected void valueChanged( Boolean oldValue, Boolean newValue ){
			updateRestricted();
		}
	};

	/**
	 * Creates a new dispatcher.
	 * @param controller the controller in whose realm this dispatcher works
	 * @param setup tells when the setup phase of <code>controller</code> is finished
	 */
	public DefaultGlobalMouseDispatcher( DockController controller, ControllerSetupCollection setup ){
		setup.add( new ControllerSetupListener(){
			public void done( DockController controller ){
				restricted.setProperties( controller );
				updateRestricted();
			}
		} );
	}

	public void dispatch( MouseEvent event ){
		switch( event.getID() ){
			case MouseEvent.MOUSE_CLICKED:
				for( MouseListener listener : getMouseListeners() ){
					listener.mouseClicked( event );
				}
				break;
			case MouseEvent.MOUSE_DRAGGED:
				for( MouseMotionListener listener : getMouseMotionListeners() ){
					listener.mouseDragged( event );
				}
				break;
			case MouseEvent.MOUSE_ENTERED:
				for( MouseListener listener : getMouseListeners() ){
					listener.mouseEntered( event );
				}
				break;
			case MouseEvent.MOUSE_EXITED:
				for( MouseListener listener : getMouseListeners() ){
					listener.mouseExited( event );
				}
				break;
			case MouseEvent.MOUSE_MOVED:
				for( MouseMotionListener listener : getMouseMotionListeners() ){
					listener.mouseMoved( event );
				}
				break;
			case MouseEvent.MOUSE_PRESSED:
				for( MouseListener listener : getMouseListeners() ){
					listener.mousePressed( event );
				}
				break;
			case MouseEvent.MOUSE_RELEASED:
				for( MouseListener listener : getMouseListeners() ){
					listener.mouseReleased( event );
				}
				break;
			case MouseEvent.MOUSE_WHEEL:
				for( MouseWheelListener listener : getMouseWheelListeners() ){
					listener.mouseWheelMoved( (MouseWheelEvent)event );
				}
				break;
			default:
				throw new IllegalStateException( "unknown type of MouseEvent: " + event );
		}
	}

	public synchronized void addMouseListener( MouseListener listener ){
		mouseListeners.add( listener );
		mouseListenersCache = null;
	}

	public synchronized void removeMouseListener( MouseListener listener ){
		mouseListeners.remove( listener );
		mouseListenersCache = null;
	}

	/**
	 * Gets all the {@link MouseListener}s that are currently registered.
	 * @return all the listeners
	 */
	protected synchronized MouseListener[] getMouseListeners(){
		if( mouseListenersCache == null ) {
			mouseListenersCache = mouseListeners.toArray( new MouseListener[mouseListeners.size()] );
		}
		return mouseListenersCache;
	}

	public synchronized void addMouseMotionListener( MouseMotionListener listener ){
		mouseMotionListeners.add( listener );
		mouseMotionListenersCache = null;
	}

	public synchronized void removeMouseMotionListener( MouseMotionListener listener ){
		mouseMotionListeners.remove( listener );
		mouseMotionListenersCache = null;
	}

	/**
	 * Gets all the {@link MouseMotionListener}s that are currently registered.
	 * @return all the listeners
	 */
	protected synchronized MouseMotionListener[] getMouseMotionListeners(){
		if( mouseMotionListenersCache == null ) {
			mouseMotionListenersCache = mouseMotionListeners.toArray( new MouseMotionListener[mouseMotionListeners.size()] );
		}
		return mouseMotionListenersCache;
	}
	
	public synchronized void addMouseWheelListener( MouseWheelListener listener ){
		mouseWheelListeners.add( listener );
		mouseWheelListenersCache = null;
	}

	public synchronized void removeMouseWheelListener( MouseWheelListener listener ){
		mouseWheelListeners.remove( listener );
		mouseWheelListenersCache = null;
	}

	/**
	 * Gets all the {@link MouseWheelListener}s that are currently registered.
	 * @return all the listeners
	 */
	protected synchronized MouseWheelListener[] getMouseWheelListeners(){
		if( mouseWheelListenersCache == null ) {
			mouseWheelListenersCache = mouseWheelListeners.toArray( new MouseWheelListener[mouseWheelListeners.size()] );
		}
		return mouseWheelListenersCache;
	}

	private void updateRestricted(){
		if( restricted.getProperties() != null ) {
			if( !restricted.getValue() ) {
				if( listener == null ) {
					listener = createListener();

					try {
						Toolkit.getDefaultToolkit().addAWTEventListener( listener,
								AWTEvent.MOUSE_EVENT_MASK |
								AWTEvent.MOUSE_MOTION_EVENT_MASK |
								AWTEvent.MOUSE_WHEEL_EVENT_MASK );
					}
					catch( SecurityException ex ) {
						System.err.println( "Can't register AWTEventListener, support for global MouseEvents disabled" );
						ex.printStackTrace();
					}
				}
			}
			else {
				if( listener != null ) {
					Toolkit.getDefaultToolkit().removeAWTEventListener( listener );
					listener = null;
				}
			}
		}
	}

	public void kill(){
		if( listener != null ) {
			Toolkit.getDefaultToolkit().removeAWTEventListener( listener );
			listener = null;
		}
		restricted.setProperties( (DockController) null );
	}

	/**
	 * Creates a listener which will receive mouse-events.
	 * @return the listener
	 */
	protected AWTEventListener createListener(){
		return new AWTEventListener(){
			public void eventDispatched( AWTEvent event ){
				if( event instanceof MouseEvent ) {
					dispatch( (MouseEvent) event );
				}
			}
		};
	}
}
