/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
import java.awt.event.MouseWheelEvent;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.event.ControllerSetupListener;
import bibliothek.gui.dock.event.DockRelocatorListener;
import bibliothek.gui.dock.event.FocusVetoListener;
import bibliothek.gui.dock.event.FocusVetoListener.FocusVeto;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * This abstract implementation of a {@link MouseFocusObserver} offers methods to handle
 * {@link FocusVetoListener}s and registers itself as {@link DockRelocatorListener}. On a
 * {@link DockRelocatorListener#drop(DockController, Dockable, DockStation) drop event} this observer
 * will automatically transfer the focus to the dropped {@link Dockable}.  
 * @author Benjamin Sigg
 */
public abstract class AbstractMouseFocusObserver implements MouseFocusObserver, DockRelocatorListener{
    
    /** The controller to be informed about changes */
    private DockController controller;

    /**
     * Creates a new FocusController.
     * @param controller the controller which will be informed about
     * focus-changes
     * @param setup an observable informing this object when <code>controller</code>
     * is set up.
     */
    public AbstractMouseFocusObserver( DockController controller, ControllerSetupCollection setup ){
        this.controller = controller;
        setup.add( new ControllerSetupListener(){
            public void done( DockController controller ) {
                controller.getRelocator().addDockRelocatorListener( AbstractMouseFocusObserver.this );
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
    
    
    public void check( MouseEvent event ){
    	if( interact( event )){
    		check( (AWTEvent)event );
    	}
    }
    
    public void check( MouseWheelEvent event ){
    	if( interact( event )){
    		check( (AWTEvent)event );
    	}
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
        
        return id == MouseEvent.MOUSE_PRESSED || id == MouseEvent.MOUSE_WHEEL;
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
     * @return a Dockable or <code>null</code> if nothing was found
     */
    protected Dockable getDockable( Component component, AWTEvent event ){
        DockElementRepresentative element = controller.searchElement( component );
        if( element == null )
            return null;
        
        Dockable dockable = element.getElement().asDockable();
        if( dockable == null )
            return null;
        
        FocusVeto veto = controller.getFocusController().checkFocusedDockable( element );
        
        if( veto != null && veto != FocusVeto.NONE ){
        	handleVeto( event, veto );
        	return null;
        }
        
        return dockable;
    }
}
