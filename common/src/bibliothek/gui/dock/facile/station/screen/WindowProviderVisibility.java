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
package bibliothek.gui.dock.facile.station.screen;

import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.util.WindowProvider;
import bibliothek.gui.dock.util.WindowProviderListener;

/**
 * Changes the visibility state of a {@link ScreenDockStation} according
 * to the state of an observed window.
 * @author Benjamin Sigg
 */
public class WindowProviderVisibility {
    /** the station whose visibility will be changed */
    private ScreenDockStation station;
    
    /** the window which is currently observed */
    private Window window;
    
    /** observes the visibility state of {@link #window} */
    private ComponentListener windowListener = new ComponentAdapter(){
        @Override
        public void componentShown( ComponentEvent e ) {
            station.setShowing( true );
        }
        
        @Override
        public void componentHidden( ComponentEvent e ) {
            station.setShowing( false );
        }
    };
    
    /** the source of the window */
    private WindowProvider provider;
    
    /** observes {@link #provider} in order to find the current window */
    private WindowProviderListener providerListener = new WindowProviderListener(){
        public void windowChanged( WindowProvider provider, Window window ) {
            changeTo( window );
        }
    };
    
    /**
     * Creates a new {@link WindowProviderVisibility}.
     * @param station the station whose visibility might be changed by this
     */
    public WindowProviderVisibility( ScreenDockStation station ){
        if( station == null )
            throw new IllegalArgumentException( "station must not be null" );
        this.station = station;
    }
    
    /**
     * Sets the window to observe
     * @param provider the new window, can be <code>null</code>
     */
    public void setProvider( WindowProvider provider ){
        if( this.provider != null ){
            this.provider.removeWindowProviderListener( providerListener );
        }
        
        this.provider = provider;
        
        if( this.provider != null ){
            this.provider.addWindowProviderListener( providerListener );
            changeTo( this.provider.searchWindow() );
        }
        else{
            changeTo( null );
        }
    }
    
    private void changeTo( Window window ){
        if( this.window != null )
            this.window.removeComponentListener( windowListener );
        
        this.window = window;
        
        if( this.window != null ){
            this.window.addComponentListener( windowListener );
            station.setShowing( this.window.isShowing() );
        }
        else{
            station.setShowing( false );
        }
    }
}
