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

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

import javax.swing.FocusManager;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.control.focus.AbstractFocusController;
import bibliothek.gui.dock.control.focus.FocusController;
import bibliothek.gui.dock.control.focus.FocusStrategy;
import bibliothek.gui.dock.event.FocusVetoListener.FocusVeto;
import bibliothek.gui.dock.title.DockTitle;

/**
 * Default implementation of {@link FocusController}.
 * @author Benjamin Sigg
 */
public class DefaultFocusController extends AbstractFocusController {

    /** the Dockable which has currently the focus, can be <code>null</code> */
    private Dockable focusedDockable = null;
    
    /** <code>true</code> while the controller actively changes the focus */
    private boolean onFocusing = false;
    
    /**
     * Creates a new focus-controller
     * @param controller the owner of this controller
     */
    public DefaultFocusController( DockController controller ){
    	super( controller );
    }
    
    public boolean isOnFocusing(){
	    return onFocusing;
    }
    
    public Dockable getFocusedDockable(){
    	return focusedDockable;
    }
    
    public FocusVeto checkFocusedDockable( DockElementRepresentative source ){
    	if( source == null ){
    		return null;
    	}
    	Dockable dockable = source.getElement().asDockable();
    	if( dockable == null ){
    		return null;
    	}
    	
    	FocusVeto veto;
    	if( source instanceof DockTitle ){
    		veto = fireVetoTitle( (DockTitle)source );
    	}
    	else{
    		veto = fireVetoDockable( dockable );
    	}
    	if( veto == null ){
    		return FocusVeto.NONE;
    	}
    	return veto;
    }
    
    public FocusVeto setFocusedDockable( DockElementRepresentative source, boolean force, boolean ensureFocusSet, final boolean ensureDockableFocused ){
    	// ignore more than one call
    	if( onFocusing )
    		return null;
    	
    	FocusVeto veto = checkFocusedDockable( source );
    	if( veto != null && veto != FocusVeto.NONE ){
    		return veto;
    	}
    	
    	Dockable focusedDockable = null;
    	if( source != null ){
    		focusedDockable = source.getElement().asDockable();
    	}
    	
    	try{
	        onFocusing = true;
	        
	        if( force || this.focusedDockable != focusedDockable ){
	            Dockable oldFocused = this.focusedDockable;
	            this.focusedDockable = focusedDockable;
	            
	            if( ensureFocusSet || ensureDockableFocused ){
	                if( EventQueue.isDispatchThread() ){
    	                SwingUtilities.invokeLater( new Runnable(){
    	                    public void run() {
    	                        ensureFocusSet( ensureDockableFocused );
    	                    }
    	                });
	                }
	                else{
	                    // we are in the wrong Thread, but we can try...
	                    ensureFocusSet( ensureDockableFocused );
	                }
	            }
	            
	            if( oldFocused != focusedDockable )
	                fireDockableFocused( oldFocused, focusedDockable );
	        }
    	}
    	finally{
    		onFocusing = false;
    	}
    	
    	return FocusVeto.NONE;
    }
    
    public void ensureFocusSet( boolean dockableOnly ){
        Dockable focusedDockable = this.focusedDockable;
        if( focusedDockable != null ){
            Stack<Dockable> front = new Stack<Dockable>();            
            
            Dockable temp = focusedDockable;
            
            while( temp != null ){
                DockStation parent = temp.getDockParent();
                if( parent != null )
                    front.push( temp );
                
                temp = parent == null ? null : parent.asDockable();
            }
            
            while( !front.isEmpty() ){
                Dockable element = front.pop();
                element.getDockParent().setFrontDockable( element );
            }
        
            if( !dockableOnly ){
	            DockTitle[] titles = focusedDockable.listBoundTitles();
	            Component focused = FocusManager.getCurrentManager().getFocusOwner();
	            if( focused != null ){
	                if( SwingUtilities.isDescendingFrom( focused, focusedDockable.getComponent() ) )
	                    return;
	                
	                for( DockTitle title : titles )
	                    if( SwingUtilities.isDescendingFrom( focused, title.getComponent() ))
	                        return;
	            }
            }
            
            FocusStrategy strategy = getStrategy();
            Component component = null;
            if( strategy != null ){
            	component = strategy.getFocusComponent( focusedDockable );
            }
            
            if( component == null ){
            	component = focusedDockable.getComponent();
            }
            
            if( component.isFocusable() ){
                component.requestFocus();
                component.requestFocusInWindow();
                focus( component, 10, 20 );
            }
            else{
                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent( component );
            }
        }
    }
    
    /**
     * Ensures that <code>component</code> has the focus and is on the 
     * active window. This is done by waiting <code>delay</code> milliseconds
     * and then checking the current focus owner. If the owner is not <code>component</code>,
     * then the focus is transfered. Checking stops after <code>component</code>
     * is found to be the focus owner, or <code>loops</code> failures were reported.<br>
     * Note: this awkward method to change the focus is necessary because on some
     * systems - like Linux - Java does not handle focus very well.
     * @param component the component which should have the focus
     * @param delay how much time to wait between two checks of the focus
     * @param loops how many times to check
     */
    private void focus( final Component component, int delay, final int loops ){
        final Timer timer = new Timer( delay, null );
        timer.addActionListener( new ActionListener(){
            private int remaining = loops;
            
            public void actionPerformed( ActionEvent e ) {
                remaining--;

                KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                if( manager.getPermanentFocusOwner() != component ){
                    manager.clearGlobalFocusOwner();
                    component.requestFocus();
                    
                    if( remaining > 0 ){
                        timer.restart();
                    }
                }
            }
        });
        
        timer.setRepeats( false );
        timer.start();
    }
}
