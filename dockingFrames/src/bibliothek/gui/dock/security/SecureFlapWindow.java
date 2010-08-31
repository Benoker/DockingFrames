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

package bibliothek.gui.dock.security;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.station.flap.ButtonPane;
import bibliothek.gui.dock.station.flap.DefaultFlapWindow;
import bibliothek.gui.dock.station.flap.FlapWindow;

/**
 * A {@link FlapWindow} which inserts a {@link GlassedPane} between its
 * {@link Dockable} and the outer world. Adding and removing of the GlassPane
 * are handled automatically.
 * @author Benjamin Sigg
 */
public class SecureFlapWindow extends DefaultFlapWindow {
    /** The pane between Dockable and outer world */
    private GlassedPane pane;
    
    /** the container painting this window */
    private Parent window;
    
    /**
     * Creates a new window
     * @param station the station which will use this window
     * @param buttonPane the visible part of the station
     * @param window the parent of this window
     */
    public SecureFlapWindow( FlapDockStation station, ButtonPane buttonPane, Parent window ) {
        super( station, buttonPane, window );
        this.window = window;
    }
    
    {
        pane = new GlassedPane();
        JComponent content = (JComponent)window.getContentPane();
        window.setContentPane( pane );
        pane.setContentPane( content );
        
        content.addComponentListener( new Listener() );
    }
    
    /**
     * A listener of the component. This listener adds or removes
     * the GlassPane if the component is made visible/invisible. 
     * @author Benjamin Sigg
     */
    private class Listener extends ComponentAdapter{
        private SecureMouseFocusObserver controller;
        
        @Override
        public void componentShown( ComponentEvent e ){
            if( controller == null ){
                controller = (SecureMouseFocusObserver)getStation().getController().getFocusObserver();
                controller.addGlassPane( pane );
            }
        }
        
        @Override
        public void componentHidden( ComponentEvent e ){
            if( controller != null ){
                controller.removeGlassPane( pane );
                controller = null;
            }
        }
    }
}
