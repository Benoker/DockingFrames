/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.util;

import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of {@link WindowProvider} which adds support
 * for {@link WindowProviderListener}s. This provider also observes the
 * visibility state of the associated window. If subclasses should either
 * call {@link #fireWindowChanged(Window)} or {@link #updateVisibility()}
 * as soon as the window changes.
 * @author Benjamin Sigg
 */
public abstract class AbstractWindowProvider implements WindowProvider{
    /** list of known listeners */
    protected List<WindowProviderListener> listeners = new ArrayList<WindowProviderListener>();
   
    /** the currently observed window */
    private Window window = null;
    
    /** the last known state of whether the window is showing or not */
    private boolean windowShowing = false;
    
    /** observes the visibility state of {@link #window} */
    private ComponentListener windowListener = new ComponentAdapter(){
        @Override
        public void componentShown( ComponentEvent e ) {
        	updateVisibility();
        }
        
        @Override
        public void componentHidden( ComponentEvent e ) {
        	updateVisibility();
        }
    };
    
    /**
     * Updates the visibility state and listeners that observe the 
     * visibility state.
     */
    protected void updateVisibility(){
    	Window current = searchWindow();
    	if( window != current ){
    		if( listeners.size() > 0 ){
	    		if( window != null )
	    			window.removeComponentListener( windowListener );
	    		if( current != null )
	    			current.addComponentListener( windowListener );
    		}
    		window = current;
    	}
    	
    	boolean showing = isShowing();
    	if( windowShowing != showing ){
    		windowShowing = showing;
    		fireVisibilityChanged( showing );
    	}
    }
    
    /**
     * Calls {@link WindowProviderListener#windowChanged(WindowProvider, Window)} on
     * all listeners known to this provider.
     * @param window the new window, might be <code>null</code>
     */
    protected void fireWindowChanged( Window window ){
    	updateVisibility();
        for( WindowProviderListener listener : listeners() )
            listener.windowChanged( this, window );
    }
    
    /**
     * Calls {@link WindowProviderListener#visibilityChanged(WindowProvider, boolean)}
     * on all listeners known to this provider.
     * @param showing the new state
     */
    protected void fireVisibilityChanged( boolean showing ){
    	for( WindowProviderListener listener : listeners() ){
    		listener.visibilityChanged( this, showing );
    	}
    }
    
    /**
     * Gets a list of all known listeners.
     * @return the list of listeners
     */
    protected WindowProviderListener[] listeners(){
        return listeners.toArray( new WindowProviderListener[ listeners.size() ] );
    }
    
    /**
     * Tells whether at least one {@link WindowProviderListener} is registered.
     * @return whether this provider is monitored
     */
    protected boolean hasListeners(){
    	return listeners.size() > 0;
    }
    
    public void addWindowProviderListener( WindowProviderListener listener ) {
        if( listener == null )
            throw new IllegalArgumentException( "null is not allowed as listener" );
        
        if( listeners.size() == 0 ){
        	updateVisibility();
        	if( window != null ){
        		window.addComponentListener( windowListener );
        		// updateVisibility();
        	}
        }
        
        listeners.add( listener );
    }
    
    public void removeWindowProviderListener( WindowProviderListener listener ) {
        listeners.remove( listener );
        
        if( listeners.size() == 0 ){
        	if( window != null ){
        		window.removeComponentListener( windowListener );
        	}
        }
    }
    
    public boolean isShowing(){
    	Window window = searchWindow();
    	if( window == null )
    		return false;
    	return window.isShowing();
    }
}
