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
package bibliothek.gui.dock.control.focus;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.control.ControllerSetupCollection;
import bibliothek.gui.dock.control.DockRelocator;
import bibliothek.gui.dock.control.GlobalMouseDispatcher;
import bibliothek.gui.dock.control.relocator.DockRelocatorEvent;
import bibliothek.gui.dock.control.relocator.VetoableDockRelocatorAdapter;
import bibliothek.gui.dock.control.relocator.VetoableDockRelocatorListener;
import bibliothek.gui.dock.event.ControllerSetupListener;
import bibliothek.gui.dock.event.FocusVetoListener;
import bibliothek.gui.dock.event.FocusVetoListener.FocusVeto;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * This implementation of a {@link MouseFocusObserver} offers methods to handle
 * {@link FocusVetoListener}s and registers a {@link VetoableDockRelocatorListener}. On a
 * {@link VetoableDockRelocatorListener#dropped(DockRelocatorEvent) drop event} this observer
 * will automatically transfer the focus to the dropped {@link Dockable}.
 * @author Benjamin Sigg
 */
public class DefaultMouseFocusObserver implements MouseFocusObserver{
    
    /** The controller to be informed about changes */
    private DockController controller;
    
    /** 
     * Listener added to the {@link DockRelocator}, updates the focused {@link Dockable} after a 
     * drag and drop operation completed. 
     */
    private VetoableDockRelocatorListener relocatorListener = new VetoableDockRelocatorAdapter(){
    	public void dropped( final DockRelocatorEvent event ){
    		EventQueue.invokeLater( new Runnable(){
                public void run(){
                	FocusController focus = controller.getFocusController();
                	FocusStrategy strategy = focus.getStrategy();
                	if( strategy == null || strategy.shouldFocusAfterDrop( event.getDockable() )){
                		controller.setFocusedDockable( new DefaultFocusRequest( event.getDockable(), null, true ));
                	}
                }
            });
    	};
	};
	
	/** Listener added to the {@link GlobalMouseDispatcher} for registering any {@link MouseEvent} */
	private GlobalMouseListener listener = new GlobalMouseListener();

    /**
     * Creates a new FocusController.
     * @param controller the controller which will be informed about
     * focus-changes
     * @param setup an observable informing this object when <code>controller</code>
     * is set up.
     */
    public DefaultMouseFocusObserver( DockController controller, ControllerSetupCollection setup ){
        this.controller = controller;
        setup.add( new ControllerSetupListener(){
            public void done( DockController controller ) {
                controller.getRelocator().addVetoableDockRelocatorListener( relocatorListener );
                GlobalMouseDispatcher dispatcher = controller.getGlobalMouseDispatcher();
                dispatcher.addMouseListener( listener );
                dispatcher.addMouseMotionListener( listener );
                dispatcher.addMouseWheelListener( listener );
            }
        });
    }
    
    /**
     * Stops this FocusController. This controller will remove all
     * its listeners and become ready for the garbage collector. 
     */
    public void kill(){
        getController().getRelocator().removeVetoableDockRelocatorListener( relocatorListener );
        GlobalMouseDispatcher dispatcher = controller.getGlobalMouseDispatcher();
        dispatcher.removeMouseListener( listener );
        dispatcher.removeMouseMotionListener( listener );
        dispatcher.removeMouseWheelListener( listener );
    }
    
    /**
     * Gets the affected controller.
     * @return the controller
     */
    public DockController getController() {
        return controller;
    }
    
    /**
     * This method may be called at any time by any component that received 
     * the {@link MouseEvent} <code>event</code>.  This observer may transfer the
     * focus because of this call.<br>
     * If this application runs in a {@link DockController#isRestrictedEnvironment() restricted environment}
     * than any {@link DockStation} of this framework will call this method.
     * @param event the event to check
     */
    public void check( MouseEvent event ){
    	if( interact( event )){
    		check( (AWTEvent)event );
    	}
    }
    
    /**
     * This method may be called at any time by any component that received 
     * the {@link MouseWheelEvent} <code>event</code>.  This observer may transfer the
     * focus because of this call.<br>
     * If this application runs in a {@link DockController#isRestrictedEnvironment() restricted environment}
     * than any {@link DockStation} of this framework will call this method.
     * @param event the event to check
     */
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
    	DockElementRepresentative element = getDockable( component, event );
    	if( element == null ){
    		return;
    	}
        Dockable dock = element.getElement().asDockable();
        if( dock == null ){
        	return;
        }
        
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
						DockElementRepresentative element = getDockable( component, event );
						if( element != null ){
							Dockable dock = element.getElement().asDockable();
					        if( dock != null ){
					        	controller.setFocusedDockable( new DefaultFocusRequest( dock, component, false, ensureFocus, element.shouldTransfersFocus() ));
					        }
						}
					}
				});
    		}
        	else{
            	if( requestFocusInWindow ){
               		component.requestFocusInWindow();
               	}
                controller.setFocusedDockable( new DefaultFocusRequest( dock, component, false, ensureFocus, element.shouldTransfersFocus() ));
        	}
        }
    }
    
    /**
     * Gets the top-dockable which has <code>component</code> or 
     * parent of <code>component</code> as base Component.
     * @param component a Component
     * @param event the event that causes this check
     * @return a Dockable or <code>null</code> if nothing was found
     */
    protected DockElementRepresentative getDockable( Component component, AWTEvent event ){
        DockElementRepresentative element = controller.searchElement( component );
        if( element == null )
            return null;
        
        if( event instanceof MouseEvent || event instanceof MouseWheelEvent ){
        	if( !element.shouldFocus() ){
        		return null;
        	}
        }
        
        Dockable dockable = element.getElement().asDockable();
        if( dockable == null )
            return null;
        
        FocusVeto veto = controller.getFocusController().checkFocusedDockable( element );
        
        if( veto != null && veto != FocusVeto.NONE ){
        	handleVeto( event, veto );
        	return null;
        }
        
        return element;
    }
    
    /**
     * This listener forwards all {@link MouseEvent}s to the {@link DefaultMouseFocusObserver#check(MouseEvent)}
     * and {@link DefaultMouseFocusObserver#check(MouseWheelEvent)}.
     * @author Benjamin Sigg
     */
    private class GlobalMouseListener implements MouseListener, MouseMotionListener, MouseWheelListener{
		public void mouseWheelMoved( MouseWheelEvent e ){
			check( e );
		}

		public void mouseDragged( MouseEvent e ){
			check( e );	
		}

		public void mouseMoved( MouseEvent e ){
			check( e );
		}

		public void mouseClicked( MouseEvent e ){
			check( e );
		}

		public void mousePressed( MouseEvent e ){
			check( e );
		}

		public void mouseReleased( MouseEvent e ){
			check( e );
		}

		public void mouseEntered( MouseEvent e ){
			check( e );
		}

		public void mouseExited( MouseEvent e ){
			check( e );
		}
    }
}
