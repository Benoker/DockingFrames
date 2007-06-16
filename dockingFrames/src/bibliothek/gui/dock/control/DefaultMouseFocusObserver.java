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

/**
 * A focus controller which adds an {@link AWTEventListener} to the 
 * {@link Toolkit} to receive events which may change the focus. 
 * @author Benjamin Sigg
 */
public class DefaultMouseFocusObserver extends MouseFocusObserver{
    /** The listener to all AWT events*/
    private AWTEventListener listener;
    
    /**
     * Creates a new focus controller
     * @param controller the owner of this controller
     */
    public DefaultMouseFocusObserver( DockController controller ){
        super( controller );
        
        listener = createListener();
        
        try{
            Toolkit.getDefaultToolkit().addAWTEventListener( listener,
                    AWTEvent.MOUSE_EVENT_MASK | 
                    AWTEvent.MOUSE_WHEEL_EVENT_MASK );
        }
        catch( SecurityException ex ){
            throw new SecurityException( "Can't setup the focus controller properly, please have a look at SecureDockController", ex );
        }
    }
    
    @Override
    public void kill(){
        Toolkit.getDefaultToolkit().removeAWTEventListener( listener );
        getController().removeDockControllerListener( this );
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
