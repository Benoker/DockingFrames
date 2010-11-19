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
import java.awt.EventQueue;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.event.ControllerSetupListener;
import bibliothek.gui.dock.event.DockRelocatorListener;
import bibliothek.gui.dock.event.FocusVetoListener;
import bibliothek.gui.dock.event.FocusVetoListener.FocusVeto;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A FocusController listens to all AWT-events. As soon as the mouse is pressed
 * over a {@link Dockable}, the FocusController will inform the {@link DockController}
 * about a new {@link DockController#setFocusedDockable(Dockable, boolean) front-dockable}.
 * @author Benjamin Sigg
 */
@Todo( compatibility=Compatibility.COMPATIBLE, priority=Priority.BUG, target=Version.VERSION_1_1_0,
		description="Dockables moving on ScreenDockStation: should keep focus" )
public abstract class MouseFocusObserver implements DockRelocatorListener {
    
    /** A list of listeners which can cancel a call to the controller */
    private List<FocusVetoListener> vetos = new ArrayList<FocusVetoListener>();
    
    /** The controller to be informed about changes */
    private DockController controller;
    
    /**
     * Creates a new FocusController.
     * @param controller the controller which will be informed about
     * focus-changes
     * @param setup an observable informing this object when <code>controller</code>
     * is set up.
     */
    public MouseFocusObserver( DockController controller, ControllerSetupCollection setup ){
        this.controller = controller;
        setup.add( new ControllerSetupListener(){
            public void done( DockController controller ) {
                controller.getRelocator().addDockRelocatorListener( MouseFocusObserver.this );
            }
        });
    }
    
    /**
     * Stops this FocusController. This controller will remove all
     * its listeners and become ready for the garbage collector. 
     */
    public void kill(){
        getController().getRelocator().removeDockRelocatorListener( this );
    }
    
    /**
     * Gets the affected controller.
     * @return the controller
     */
    public DockController getController() {
        return controller;
    }
    
    /**
     * Adds a listener to this controller which can cancel a call to
     * the {@link DockController}.
     * @param listener the new listener
     */
    public void addVetoListener( FocusVetoListener listener ){
        vetos.add( listener );
    }
    
    /**
     * Removes a listener from this controller
     * @param listener the listener to remove
     */
    public void removeVetoListener( FocusVetoListener listener ){
        vetos.remove( listener );
    }
    
    /**
     * Asks all {@link FocusVetoListener} through their method
     * {@link FocusVetoListener#vetoFocus(MouseFocusObserver, DockTitle)}
     * whether they want cancel a call to the {@link DockController}.
     * @param title the title which was hit by the mouse
     * @return the first veto
     */
    protected FocusVeto fireVetoTitle( DockTitle title ){
        for( FocusVetoListener listener : vetos.toArray( new FocusVetoListener[ vetos.size() ] )){
        	FocusVeto veto = listener.vetoFocus( this, title );
        	if( veto != FocusVeto.NONE )
        		return veto;
        }
        
        return FocusVeto.NONE;
    }
    
    /**
     * Asks all {@link FocusVetoListener} through their method
     * {@link FocusVetoListener#vetoFocus(MouseFocusObserver, Dockable)}
     * whether they want cancel a call to the {@link DockController}.
     * @param dockable the Dockable which was hit by the mouse
     * @return the first veto
     */    
    protected FocusVeto fireVetoDockable( Dockable dockable ){
    	for( FocusVetoListener listener : vetos.toArray( new FocusVetoListener[ vetos.size() ] )){
        	FocusVeto veto = listener.vetoFocus( this, dockable );
        	if( veto != FocusVeto.NONE )
        		return veto;
        }
        
        return FocusVeto.NONE;
    }
    
    /**
     * Lets check this controller whether the focus should change, or not. Clients
     * invoking this method should first check whether <code>event</code> is
     * allowed to change the focus or not. This check can be done through the
     * method {@link #interact(AWTEvent)}.
     * @param event The event to react on.
     * @see #interact(AWTEvent)
     */
    protected void check( AWTEvent event ){
        if( controller.getRelocator().isOnPut() || controller.isOnFocusing() )
            return;
        
        Object source = event.getSource();
        if( source instanceof Component ){
            Component component = (Component)source;
            if( event.getID() == MouseEvent.MOUSE_PRESSED ){
                if( component.isFocusable() && component.isEnabled()){
                	check( component, false, true, event );
                }
                else{
                    check( component, true, false, event );
                }
            }
            else{
                check( component, event );
            }
        }
    }
    
    /**
     * Tells whether this event should change the focus.
     * @param event the event
     * @return <code>true</code> if the focus could be changed
     */
    protected boolean interact( AWTEvent event ){
        int id = event.getID();
        
        return id == MouseEvent.MOUSE_PRESSED ||
            id == MouseEvent.MOUSE_WHEEL;
    }
    
    /**
     * Handles the veto that was given when trying to forward
     * <code>event</code>. The default implementation calls
     * {@link InputEvent#consume()} to get rid of the event.
     * @param event the event to handle
     * @param veto which veto was called by a {@link FocusVetoListener}
     */
    protected void handleVeto( AWTEvent event, FocusVeto veto ){
    	if( veto == FocusVeto.VETO ){
	    	if( event instanceof InputEvent ){
	    		((InputEvent)event).consume();
	    	}
    	}
    }
    
    /**
     * Tries to find the Dockable which owns <code>component</code>
     * and sets this Dockable to the focusedDockable. The method
     * only succeeds if no veto-listener reacts.
     * @param component the component whose dockable parent is to set
     * focused
     * @param event the event that causes this check
     */
    protected void check( Component component, AWTEvent event ){
        check( component, true, false, event );
    }
    
    /**
     * Tries to find the Dockable which owns <code>component</code>
     * and sets this Dockable to the focusedDockable. The method
     * only succeeds if no veto-listener reacts.
     * @param component the component whose dockable parent is to set
     * focused
     * @param ensureFocus whether the DockController should ensure
     * that the focus is set correctly or not.
     * @param requestFocusInWindow whether {@link Component#requestFocusInWindow()} should be
     * called or not
     * @param event the event that causes this check
     */
    protected void check( final Component component, final boolean ensureFocus, boolean requestFocusInWindow, final AWTEvent event ){
        Dockable dock = getDockable( component, event );
        if( dock != null ){
            Dockable focused = controller.getFocusedDockable();
            boolean change = true;
            if( focused != null )
                change = !DockUtilities.isAncestor( dock, focused );
            
            if( change ){
            	if( component instanceof FocusAwareComponent ){
            		FocusAwareComponent aware = (FocusAwareComponent)component;
            		if( requestFocusInWindow ){
            			aware.maybeRequestFocus();
            		}
            		aware.invokeOnFocusRequest(new Runnable(){
						public void run(){
							Dockable dock = getDockable( component, event );
					        if( dock != null ){					
					        	controller.setFocusedDockable( dock, false, ensureFocus );
					        }
						}
					});
        		}
            	else{
	            	if( requestFocusInWindow ){
	               		component.requestFocusInWindow();
	               	}
	                controller.setFocusedDockable( dock, false, ensureFocus );
            	}
            }
        }
    }
    
    public void init( DockController controller, Dockable dockable ) {
        // do nothing
    }
    
    public void cancel( DockController controller, Dockable dockable ) {
        // do nothing
    }
    
    public void drag( DockController controller, Dockable dockable, DockStation station ) {
        // do nothing
    }
    
    public void drop( final DockController controller, final Dockable dockable, DockStation station ) {
        EventQueue.invokeLater( new Runnable(){
            public void run(){
                controller.setFocusedDockable( dockable, true );
            }
        });
    }
    
    /**
     * Gets the top-dockable which has <code>component</code> or 
     * parent of <code>component</code> as base Component.
     * @param component a Component
     * @param event the event that causes this check
     * @return a Dockable or <code>null</code> if nothing was found or
     * a {@link FocusVetoListener} doesn't want to inform the controller
     */
    protected Dockable getDockable( Component component, AWTEvent event ){
        DockElementRepresentative element = controller.searchElement( component );
        if( element == null )
            return null;
        
        Dockable dockable = element.getElement().asDockable();
        if( dockable == null )
            return null;
        
        FocusVeto veto;
        
        if( element instanceof DockTitle ){
            veto = fireVetoTitle( (DockTitle)element );
        }
        else{
            veto = fireVetoDockable( dockable );
        }
        
        if( veto != FocusVeto.NONE ){
        	handleVeto( event, veto );
        	return null;
        }
        
        return dockable;
    }
}
