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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.MouseInputAdapter;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.event.ControllerSetupListener;
import bibliothek.gui.dock.event.DockControllerRepresentativeListener;
import bibliothek.gui.dock.event.DoubleClickListener;
import bibliothek.gui.dock.event.LocatedListenerList;
import bibliothek.gui.dock.title.DockTitle;

/**
 * Adds a {@link MouseListener} to all {@link Dockable}s, {@link DockTitle}s
 * and other {@link DockElementRepresentative}s, informs the registered {@link DoubleClickListener}s
 * whenever the user clicks twice on such an element.
 * @author Benjamin Sigg
 */
public class DoubleClickController {
    /** the list of all observers */
    private LocatedListenerList<DoubleClickListener> observers =
    	new LocatedListenerList<DoubleClickListener>();
    
    /** A map that tells which listener was added to which {@link Dockable} */
    private Map<DockElementRepresentative, GlobalDoubleClickListener> listeners = 
        new HashMap<DockElementRepresentative, GlobalDoubleClickListener>();
    
    /**
     * Creates a new <code>DoubleClickController</code>.
     * @param setup an observable that informs this object when <code>controller</code>
     * is set up.
     */
    public DoubleClickController( ControllerSetupCollection setup ){
        setup.add( new ControllerSetupListener(){
            public void done( DockController controller ) {
                controller.addRepresentativeListener( new DockControllerRepresentativeListener(){
                    public void representativeAdded( DockController controller,
                            DockElementRepresentative representative ) {
                    
                        Dockable dockable = representative.getElement().asDockable();
                        if( dockable != null ){
                            GlobalDoubleClickListener listener = new GlobalDoubleClickListener( dockable );
                            representative.addMouseInputListener( listener );
                            listeners.put( representative, listener );
                        }
                    }
                    
                    public void representativeRemoved(
                            DockController controller,
                            DockElementRepresentative representative ) {
                     
                        Dockable dockable = representative.getElement().asDockable();
                        if( dockable != null ){
                            GlobalDoubleClickListener listener = listeners.remove( representative );
                            if( listener != null ){
                                representative.removeMouseInputListener( listener );
                            }
                        }
                    }
                });
            }
        });
    }
    
    /**
     * Adds a listener to this controller.
     * @param listener the new observer
     */
    public void addListener( DoubleClickListener listener ){
        observers.addListener( listener );
    }
    
    /**
     * Removes a listener from this controller.
     * @param listener the observer to remove
     */
    public void removeListener( DoubleClickListener listener ){
        observers.removeListener( listener );
    }
    
    /**
     * Fires an event to the {@link DoubleClickListener}s whose location in the
     * tree is equal or below <code>dockable</code>. The order in which the
     * observers receive the event depends on their distance to the <code>dockable</code>.
     * @param dockable the dockable which was selected by the user
     * @param event the cause of the invocation, its click count must be 2.
     */
    public void send( Dockable dockable, MouseEvent event ){
        if( dockable == null )
            throw new NullPointerException( "dockable must not be null" );
        
        if( event == null )
            throw new NullPointerException( "event must not be null" );
        
        if( event.getClickCount() != 2 )
            throw new IllegalArgumentException( "click count must be equal to 2" );
        
        List<DoubleClickListener> list = observers.affected( dockable );
        for( DoubleClickListener observer : list ){
            if( observer.process( dockable, event )){
                event.consume();
                break;
            }
        }
    }
    

    /**
     * A listener which waits for a double-click-event.
     * @author Benjamin Sigg
     */
    protected class GlobalDoubleClickListener extends MouseInputAdapter{
        /** The Dockable for which this listener is waiting */
        private Dockable dockable;
        
        /**
         * Constructs a new listener.
         * @param dockable the element that will become the source
         * of the forwarded event
         */
        public GlobalDoubleClickListener( Dockable dockable ){
            this.dockable = dockable;
        }
        
        @Override
        public void mousePressed( MouseEvent event ) {
            if( !event.isConsumed() && event.getClickCount() == 2 ){
                send( dockable, event );
            }
        }
    }
}
