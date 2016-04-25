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
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper around another {@link WindowProvider}, allows to exchange
 * providers without the need to reattach {@link WindowProviderListener}s.
 * @author Benjamin Sigg
 */
public class WindowProviderWrapper implements WindowProvider{
    private WindowProvider delegate;
    
    private List<WindowProviderListener> listeners = new ArrayList<WindowProviderListener>();
    
    /** the last remembered state of the visibility of the window of this {@link WindowProvider} */
    private boolean showing = false;
    
    private WindowProviderListener listener = new WindowProviderListener(){
        public void windowChanged( WindowProvider provider, Window window ) {
            fireWindowChanged( window );
        }
        public void visibilityChanged( WindowProvider provider, boolean showing ){
        	WindowProviderWrapper.this.showing = showing;
        	fireVisibilityChanged( showing );
        }
    };
    
    public void addWindowProviderListener( WindowProviderListener listener ) {
        int previous = listeners.size();
        listeners.add( listener );
        if( previous == 0 && listeners.size() > 0 && delegate != null )
            delegate.addWindowProviderListener( this.listener );
    }
    
    public void removeWindowProviderListener( WindowProviderListener listener ) {
        int previous = listeners.size();
        listeners.remove( listener );
        if( previous > 0 && listeners.size() == 0 && delegate != null )
            delegate.removeWindowProviderListener( this.listener );
    }
    
    /**
     * Gets all currently registered listeners.
     * @return the list of listeners.
     */
    protected WindowProviderListener[] listeners(){
    	return listeners.toArray( new WindowProviderListener[ listeners.size() ] );
    }
    
    /**
     * Informs all listeners that the window has changed.
     * @param window the new window, might be <code>null</code>
     */
    protected void fireWindowChanged( Window window ){
    	for( WindowProviderListener listener : listeners() ){
    		listener.windowChanged( this, window );
    	}
    }
    
    /**
     * Informs all listeners that the windows visibility has changed.
     * @param showing the new visibility state
     */
    protected void fireVisibilityChanged( boolean showing ){
    	for( WindowProviderListener listener : listeners() ){
    		listener.visibilityChanged( this, showing );
    	}
    }
    
    /**
     * Sets the provider which will be used to find a window.
     * @param delegate the new provider, can be <code>null</code>
     */
    public void setDelegate( WindowProvider delegate ) {
        if( listeners.size() == 0 ){
            this.delegate = delegate;
        }
        else{
            Window oldWindow = searchWindow();
            boolean oldShowing = isShowing();
            
            if( this.delegate != null )
                this.delegate.removeWindowProviderListener( listener );
            
            this.delegate = delegate;
            if( this.delegate != null )
                this.delegate.addWindowProviderListener( listener );
            
            Window newWindow = searchWindow();
            boolean newShowing = isShowing();
            if( oldWindow != newWindow ){
                fireWindowChanged( newWindow );
            }
            if( oldShowing != newShowing || showing != newShowing ){
            	fireVisibilityChanged( newShowing );
            	showing = newShowing;
            }
        }
    }
    
    /**
     * Gets the provider which is be used by this to find a window.
     * @return the provider, can be <code>null</code>
     */
    public WindowProvider getDelegate() {
        return delegate;
    }
    
    public Window searchWindow() {
        if( delegate == null )
            return null;
        
        return delegate.searchWindow();
    }
    
    public boolean isShowing(){
    	if( delegate == null )
    		return false;
    	
    	return delegate.isShowing();
    }
}
