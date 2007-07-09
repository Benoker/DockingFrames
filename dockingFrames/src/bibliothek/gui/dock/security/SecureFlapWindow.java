/**
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

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.flap.ButtonPane;
import bibliothek.gui.dock.station.flap.FlapWindow;

/**
 * A {@link FlapWindow} which inserts a {@link GlassedPane} between its
 * {@link Dockable} and the outer world. Adding and removing of the GlassPane
 * are handled automatically.
 * @author Benjamin Sigg
 */
public class SecureFlapWindow extends FlapWindow {
    /** The pane between Dockable and outer world */
    private GlassedPane pane;
    /** The component on which a {@link DockableDisplayer} will be added */
    private JComponent content;
    
    /**
     * Creates a new window
     * @param station the station which will use this window
     * @param buttonPane the visible part of the station
     * @param dialog the owner of this window
     */
    public SecureFlapWindow( FlapDockStation station, ButtonPane buttonPane, Dialog dialog ) {
        super(station, buttonPane, dialog);
    }

    /**
     * Creates a new window
     * @param station the station which will use this window
     * @param buttonPane the visible part of the station
     * @param frame the owner of this window
     */
    public SecureFlapWindow( FlapDockStation station, ButtonPane buttonPane, Frame frame ) {
        super(station, buttonPane, frame);
    }
    
    {
        pane = new GlassedPane();
        content = (JComponent)getContentPane();
        setContentPane( pane );
        pane.setContentPane( content );
        addWindowListener( new Listener() );
    }
    
    @Override
    protected Container getDisplayerParent() {
        return content;
    }
    
    /**
     * A listener of the enclosing Window. This listener adds or removes
     * the GlassPane if the enclosing Window is made visible/invisible. 
     * @author Benjamin Sigg
     */
    private class Listener extends WindowAdapter{
        @Override
        public void windowOpened( WindowEvent e ) {
            SecureMouseFocusObserver controller = (SecureMouseFocusObserver)getStation().getController().getFocusObserver();
            controller.addGlassPane( pane );
        }
        
        @Override
        public void windowClosed( WindowEvent e ) {
            DockStation station = getStation();
            if( station != null ){
                DockController controller = station.getController();
                if( controller != null ){
                    SecureMouseFocusObserver observer = (SecureMouseFocusObserver)controller.getFocusObserver();
                    observer.removeGlassPane( pane );
                }
            }
        }
    }
}
