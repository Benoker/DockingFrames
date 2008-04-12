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

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;

import bibliothek.gui.DockController;

/**
 * A {@link KeyboardController} that listens to all events through 
 * an {@link java.awt.event.AWTEventListener}.
 * @author Benjamin Sigg
 *
 */
public class DefaultKeyboardController extends KeyboardController {
	private AWTEventListener listener = new AWTEventListener(){
		public void eventDispatched( AWTEvent event ){
			if( event instanceof KeyEvent ){
				KeyEvent key = (KeyEvent)event;
				if( key.getID() == KeyEvent.KEY_PRESSED )
					fireKeyPressed( key );
				else if( key.getID() == KeyEvent.KEY_RELEASED )
					fireKeyReleased( key );
				else if( key.getID() == KeyEvent.KEY_TYPED )
					fireKeyTyped( key );
			}
		}
	};
	
	/**
	 * Creates a new controller
	 * @param controller the realm in which this controller operates
	 */
	public DefaultKeyboardController( DockController controller ){
		super( controller );
		
		try{
			Toolkit.getDefaultToolkit().addAWTEventListener( listener, AWTEvent.KEY_EVENT_MASK );
		}
		catch( SecurityException ex ){
			System.err.println( "Can't register AWTEventListener, support for global KeyEvents disabled" );
		}
	}

	@Override
	public void kill(){
		try{
			Toolkit.getDefaultToolkit().removeAWTEventListener( listener );
		}
		catch( SecurityException ex ){
			// ignore
		}
	}
}
