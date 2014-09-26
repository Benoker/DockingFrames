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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Timer;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.control.focus.AbstractFocusController;
import bibliothek.gui.dock.control.focus.DefaultFocusRequest;
import bibliothek.gui.dock.control.focus.EnsuringFocusRequest;
import bibliothek.gui.dock.control.focus.FocusController;
import bibliothek.gui.dock.control.focus.FocusRequest;
import bibliothek.gui.dock.control.focus.FocusStrategy;
import bibliothek.gui.dock.control.focus.FocusStrategyRequest;
import bibliothek.gui.dock.event.FocusVetoListener;
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
    
    /** all the requests waiting for their execution */
    private List<Request> pendingRequests = new ArrayList<Request>();
    
    /** {@link Runnable}s that will be executed once {@link #pendingRequests} is empty */
    private List<Runnable> pendingCompletionRequests = new LinkedList<Runnable>();
    
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
    
    public FocusVeto setFocusedDockable( DockElementRepresentative source, Component component, boolean force, boolean ensureFocusSet, boolean ensureDockableFocused ){
    	DefaultFocusRequest request = new DefaultFocusRequest( source, component, force, ensureFocusSet, ensureDockableFocused );
    	focus( request );
    	return request.getVeto();
    }
    
    public void ensureFocusSet( boolean dockableOnly ){
    	Dockable dockable = focusedDockable;
    	if( dockable != null ){
    		focus( new EnsuringFocusRequest( dockable, dockableOnly ));
    	}
    }
    
    /**
     * Requests focus for the {@link Component} that is described by <code>request</code>. The request is either
     * executed now (if {@link FocusRequest#getDelay() delay} is 0) or in the near future. The request may be canceled either
     * because another request is executed first, because of a {@link FocusVetoListener}, or because the request contains
     * invalid data.
     * @param request the request
     */
    public void focus( FocusRequest request ){
    	Request next = new Request( request, false );
    	next.enqueue();
    }
    
    public void onFocusRequestCompletion( final Runnable run ) {
    	if( EventQueue.isDispatchThread() ){
    		synchronized( pendingRequests ) {
    			if( pendingRequests.isEmpty() || isFrozen() ){
    				run.run();
    			}
    			else{
    				pendingCompletionRequests.add( run );
    			}
    		}
    	}
    	else{
    		try {
				EventQueue.invokeAndWait( new Runnable() {
					public void run() {
						onFocusRequestCompletion( run );
					}
				} );
			} catch( InterruptedException e ) {
				// there really is not much we can do here
				e.printStackTrace();
			} catch( InvocationTargetException e ) {
				// there really is not much we can do here
				e.printStackTrace();
			}
    	}
    }
    
    private void checkCompletionRequests(){
    	EventQueue.invokeLater( new Runnable() {
			public void run() {
				synchronized ( pendingRequests ) {
					Iterator<Runnable> completion = pendingCompletionRequests.iterator();
					while( pendingRequests.isEmpty() && completion.hasNext() ){
						Runnable completionRequest = completion.next();
						completion.remove();
						completionRequest.run();
					}
				}
			}
		});
    }
    
    /**
     * Decides whether to execute or to refuse <code>request</code>.
     * @param request the request to check
     * @param dockable the dockable that would receive the focus through this request
     * @return the accepted {@link Component} or <code>null</code> if the
     * request is to be refused
     */
    protected Component accept( final FocusRequest request, final Dockable dockable ){
    	if( isFrozen() ){
    		return null;
    	}
    	
    	if( !request.validate( this ) ){
    		return null;
    	}
    	
    	FocusVeto veto = checkFocusedDockable( request.getSource() );
    	if( veto == null ){
    		veto = FocusVeto.NONE;
    	}
    	request.veto( veto );
    	if( veto != FocusVeto.NONE ){
    		return null;
    	}
    	
    	FocusStrategy strategy = getStrategy();
    	Component component = request.getComponent();
    	
        if( strategy != null && dockable != null ){
        	Component replacement = strategy.getFocusComponent( new FocusStrategyRequest(){
				public Component getMouseClicked(){
					return request.getComponent();
				}
				
				public Dockable getDockable(){
					return dockable;
				}
				
				public boolean excluded( Component component ){
					return !request.acceptable( component );
				}
			});
        	if( replacement != null ){
        		component = replacement;
        	}
        }
        
        if( component == null && dockable != null ){
        	component = dockable.getComponent();
        }
        
        if( component != null && pendingRequests.size() > 1 ){
        	if( !request.isHardRequest() ){
	        	if( !component.isVisible() || !component.isShowing() ){
	        		component = null;
	        	}
        	}
        }
        
        return component;
    }
    
    /**
     * Called if {@link #accept(FocusRequest, Dockable)} accepted <code>request</code>.
     * @param request the request to execute
     * @param dockable the element that will receive the focus
     * @param component the {@link Component} that is to be focused
     */
    protected void execute( final FocusRequest request, Dockable dockable, final Component component ){
    	// clean up
    	synchronized( pendingRequests ){
	    	for( Request pending : pendingRequests ){
	    		pending.cancel();
	    	}
	    	pendingRequests.clear();
    	}

    	boolean active = true;
    	
    	// execute
    	if( EventQueue.isDispatchThread() ){
    		active = grant( request, component );
    	}
    	else{
    		EventQueue.invokeLater( new Runnable() {
				public void run(){
					grant( request, component );
				}
			});
    	}
        
        if( active && dockable != focusedDockable ){
    		Dockable oldFocused = focusedDockable;
    		focusedDockable = dockable;
    		fireDockableFocused( oldFocused, focusedDockable );
        }
        
        checkCompletionRequests();
    }
    
    private boolean grant( FocusRequest request, Component component ){
    	FocusRequest next;
    	
    	try{
	    	onFocusing = true;
	    	next = request.grant( component );
    	}
    	finally{
    		onFocusing = false;
    	}
    	if( next != null ){
        	boolean accepted = request.getSource() == next.getSource() && component == next.getComponent();
        	Request nextRequest = new Request( next, accepted );
        	return nextRequest.enqueue();
        }
    	return true;
    }
    
    private class Request implements ActionListener{
    	/** whether this request is accepted and can be executed */
    	private boolean accepted = false;
    	
    	/** the request to execute */
    	private FocusRequest request;
    	
    	/** whether this request should silently fail */
    	private boolean canceled = false;
    	
    	/**
    	 * Creates a new request.
    	 * @param request the request to execute
    	 * @param accepted whether <code>request</code> has already been accepted
    	 */
    	public Request( FocusRequest request, boolean accepted ){
    		this.request = request;
    		this.accepted = accepted;
    		
    		synchronized( pendingRequests ) {
    			pendingRequests.add( this );
			}
    	}
    	
    	/**
    	 * Starts this request
    	 * @return <code>true</code> if the request has already been handled
    	 * <code>false</code> otherwise
    	 */
    	public boolean enqueue(){
    		if( request.getDelay() <= 0 ){
    			run();
    			return true;
    		}
    		else{
	    		Timer timer = new Timer( request.getDelay(), this );
	    		timer.setRepeats( false );
	    		timer.start();
	    		return false;
    		}
    	}
    	
    	/**
    	 * Stop this request from ever being executed
    	 */
    	public void cancel(){
    		canceled = true;
    	}
    	
    	/**
    	 * Gets the {@link Dockable} which receives the focus through this request.
    	 * @return the dockable or <code>null</code>
    	 */
    	public Dockable getDockable(){
    		DockElementRepresentative source = request.getSource();
    		if( source == null ){
    			return null;
    		}
    		return source.getElement().asDockable();
    	}
    	
    	private Component accept(){
    		if( accepted ){
    			return request.getComponent();
    		}
    		else{
    			return DefaultFocusController.this.accept( request, getDockable() );
    		}
    	}
    	
    	public void actionPerformed( ActionEvent e ){
    		run();
    	}
    	
    	private void run(){
	    	if( !canceled ){
	    		Component component = accept();
		    	if( component != null ){
		    		execute( request, getDockable(), component );
		    	}
		    	else if( request.getSource() == null && request.getComponent() == null && pendingRequests.size() == 1 ){
		    		execute( request, null, null );
		    	}
		    	else{
		    		synchronized( pendingRequests ) {
						pendingRequests.remove( this );
					}
		    	}
	    	}
    	}
    }
}
