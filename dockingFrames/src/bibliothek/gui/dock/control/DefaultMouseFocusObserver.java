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

import bibliothek.gui.DockController;
import bibliothek.gui.dock.control.focus.AbstractMouseFocusObserver;
import bibliothek.gui.dock.event.ControllerSetupListener;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A focus controller which adds an {@link AWTEventListener} to the 
 * {@link Toolkit} to receive events which may change the focus. 
 * @author Benjamin Sigg
 */
@Todo( compatibility=Compatibility.COMPATIBLE, priority=Priority.MAJOR, target=Version.VERSION_1_1_0,
		description="Some kind of strategy to decide which child Component of a Dockable should be focused")
public class DefaultMouseFocusObserver extends AbstractMouseFocusObserver{
    /** The listener to all AWT events*/
    private AWTEventListener listener;
    
    /** whether the application is in restricted mode or not */
    private PropertyValue<Boolean> restricted = new PropertyValue<Boolean>( DockController.RESTRICTED_ENVIRONMENT ){
		protected void valueChanged( Boolean oldValue, Boolean newValue ){
			updateRestricted();
		}
	};
    
    /**
     * Creates a new focus controller
     * @param controller the owner of this controller
     * @param setup an observer that informs this object when <code>controller</code>
     * is set up.
     */
    public DefaultMouseFocusObserver( DockController controller, ControllerSetupCollection setup ){
        super( controller, setup );
        
        setup.add( new ControllerSetupListener(){
			public void done( DockController controller ){
				restricted.setProperties( controller );
				updateRestricted();
			}
		});
    }
    
    private void updateRestricted(){
    	if( restricted.getProperties() != null ){
	    	if( !restricted.getValue() ){
	    		if( listener == null ){
	    			listener = createListener();
	    	
	    			try{
	    	            Toolkit.getDefaultToolkit().addAWTEventListener( listener,
	    	                    AWTEvent.MOUSE_EVENT_MASK | 
	    	                    AWTEvent.MOUSE_WHEEL_EVENT_MASK );
	    	        }
	    	        catch( SecurityException ex ){
	    	        	System.err.println( "Can't register AWTEventListener, support for global MouseEvents disabled" );
	    	        	ex.printStackTrace();
	    	        }
	    		}
	    	}
	    	else{
	    		if( listener != null ){
	    			Toolkit.getDefaultToolkit().removeAWTEventListener( listener );
	    			listener = null;
	    		}
	    	}
    	}
    }
    
    @Override
    public void kill(){
        super.kill();
        if( listener != null ){
        	Toolkit.getDefaultToolkit().removeAWTEventListener( listener );
        	listener = null;
        }
        restricted.setProperties( (DockController)null );
    }
    
    /**
     * Creates a listener which will receive mouse-events.
     * @return the listener
     */
    protected AWTEventListener createListener(){
        return new AWTEventListener(){
            public void eventDispatched( AWTEvent event ){
                if( interact( event )){
                    check( event );
                }
            }
        };
    }
}