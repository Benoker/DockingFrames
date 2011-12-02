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
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.event.ComponentHierarchyObserverEvent;
import bibliothek.gui.dock.event.ComponentHierarchyObserverListener;
import bibliothek.gui.dock.event.ControllerSetupListener;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * A {@link KeyboardController} that can either use a global {@link AWTEventListener} or
 * a set of {@link KeyListener}s to receive {@link KeyEvent}s.
 * @author Benjamin Sigg
 */
public class DefaultKeyboardController extends AbstractKeyboardController {
	private AWTEventListener awtListener;
	private KeyListener keyListener;
	private ComponentHierarchyObserverListener hierarchyListener;
	
    /** whether the application is in restricted mode or not */
    private PropertyValue<Boolean> restricted = new PropertyValue<Boolean>( DockController.RESTRICTED_ENVIRONMENT ){
		protected void valueChanged( Boolean oldValue, Boolean newValue ){
			updateRestricted();
		}
	};
	
	/**
	 * Creates a new controller
	 * @param controller the realm in which this controller operates
	 * @param setup an observer that informs this object when <code>controller</code> is set up.
	 */
	public DefaultKeyboardController( DockController controller, ControllerSetupCollection setup ){
		super( controller );
		
		setup.add( new ControllerSetupListener(){
			public void done( DockController controller ){
				restricted.setProperties( controller );
				updateRestricted();
			}
		});
	}
	
	private void updateRestricted(){
		if( restricted.getProperties() != null ){
			boolean restricted = this.restricted.getValue();
			setListeningAWT( !restricted );
			setListeningKey( restricted );
		}
	}
	
	private void setListeningAWT( boolean listening ){
		if( listening ){
			if( awtListener == null ){
				awtListener = createAwtListener();
				try{
					Toolkit.getDefaultToolkit().addAWTEventListener( awtListener, AWTEvent.KEY_EVENT_MASK );
				}
				catch( SecurityException ex ){
					System.err.println( "Can't register AWTEventListener, support for global KeyEvents disabled" );
					ex.printStackTrace();
				}
			}
		}
		else{
			if( awtListener != null ){
				try{
					Toolkit.getDefaultToolkit().removeAWTEventListener( awtListener );
				}
				catch( SecurityException ex ){
					// ignore
				}
				awtListener = null;
			}
		}
	}
	
	private void setListeningKey( boolean listening ){
		if( listening ){
			if( keyListener == null ){
				keyListener = createKeyListener();
				hierarchyListener = createHierarchyListener();
				
				for( Component component : getController().getComponentHierarchyObserver().getComponents()){
	                component.addKeyListener( keyListener );
	            }
				
				getController().getComponentHierarchyObserver().addListener( hierarchyListener );
			}
		}
		else{
			if( keyListener != null ){
				getController().getComponentHierarchyObserver().removeListener( hierarchyListener );
				
				for( Component component : getController().getComponentHierarchyObserver().getComponents()){
	                component.removeKeyListener( keyListener );
	            }
				hierarchyListener = null;
				keyListener = null;
			}
		}
	}

	public void kill(){
		setListeningAWT( false );
		setListeningKey( false );
		restricted.setProperties( (DockController)null );
	}
	
	private AWTEventListener createAwtListener(){
		return new AWTEventListener(){
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
	}
	
	private KeyListener createKeyListener(){
		return new KeyListener(){
	        public void keyPressed( KeyEvent e ) {
	            fireKeyPressed( e );
	        }

	        public void keyReleased( KeyEvent e ) {
	            fireKeyReleased( e );
	        }

	        public void keyTyped( KeyEvent e ) {
	            fireKeyTyped( e );
	        }
		};
	}
	
	private ComponentHierarchyObserverListener createHierarchyListener(){
		return new ComponentHierarchyObserverListener(){
            public void added( ComponentHierarchyObserverEvent event ) {
                List<Component> components = event.getComponents();
                if( keyListener != null ){
                    for( Component component : components ){
                        component.addKeyListener( keyListener );
                    }
                }
            }
            public void removed( ComponentHierarchyObserverEvent event ) {
                List<Component> components = event.getComponents();
                if( keyListener != null ){
                    for( Component component : components ){
                        component.removeKeyListener( keyListener );
                    }
                }
            }
        };
	}
}
