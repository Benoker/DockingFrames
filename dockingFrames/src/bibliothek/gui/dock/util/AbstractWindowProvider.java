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
 * An implementation of {@link WindowProvider} which just adds support
 * for {@link WindowProviderListener}s.
 * @author Benjamin Sigg
 */
public abstract class AbstractWindowProvider implements WindowProvider{
    /** list of known listeners */
    protected List<WindowProviderListener> listeners = new ArrayList<WindowProviderListener>();
    
    /**
     * Calls {@link WindowProviderListener#windowChanged(WindowProvider, Window)} on
     * all listeners known to this provider.
     * @param window the new window, might be <code>null</code>
     */
    protected void fireWindowChanged( Window window ){
        for( WindowProviderListener listener : listeners() )
            listener.windowChanged( this, window );
    }
    
    /**
     * Gets a list of all known listeners.
     * @return the list of listeners
     */
    protected WindowProviderListener[] listeners(){
        return listeners.toArray( new WindowProviderListener[ listeners.size() ] );
    }
    
    public void addWindowProviderListener( WindowProviderListener listener ) {
        if( listener == null )
            throw new IllegalArgumentException( "null is not allowed as listener" );
        
        listeners.add( listener );
    }
    
    public void removeWindowProviderListener( WindowProviderListener listener ) {
        listeners.remove( listener );
    }
}
