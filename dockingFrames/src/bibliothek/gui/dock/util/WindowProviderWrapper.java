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

/**
 * A wrapper around another {@link WindowProvider}, allows to exchange
 * providers without the need to reattach {@link WindowProviderListener}s.
 * @author Benjamin Sigg
 */
public class WindowProviderWrapper extends AbstractWindowProvider{
    private WindowProvider delegate;
    
    private WindowProviderListener listener = new WindowProviderListener(){
        public void windowChanged( WindowProvider provider, Window window ) {
            fireWindowChanged( window );
        }
    };
    
    @Override
    public void addWindowProviderListener( WindowProviderListener listener ) {
        int previous = listeners.size();
        super.addWindowProviderListener( listener );
        if( previous == 0 && listeners.size() > 0 && delegate != null )
            delegate.addWindowProviderListener( this.listener );
    }
    
    @Override
    public void removeWindowProviderListener( WindowProviderListener listener ) {
        int previous = listeners.size();
        super.removeWindowProviderListener( listener );
        if( previous > 0 && listeners.size() == 0 && delegate != null )
            delegate.removeWindowProviderListener( this.listener );
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
            
            if( this.delegate != null )
                this.delegate.removeWindowProviderListener( listener );
            
            this.delegate = delegate;
            if( this.delegate != null )
                this.delegate.addWindowProviderListener( listener );
            
            Window newWindow = searchWindow();
            if( oldWindow != newWindow ){
                fireWindowChanged( newWindow );
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
}
