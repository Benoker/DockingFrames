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
package bibliothek.gui.dock.station.screen;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * An implementation of {@link ScreenDockWindow} which uses a {@link JFrame}.
 * The dialog can be used without decorations, in that case the resizing
 * system of {@link AbstractScreenDockWindow} is used.
 * @author Benjamin Sigg
 */
public class ScreenDockFrame extends AbstractScreenDockWindow {
    private JFrame frame;
        
    /**
     * Creates a new dialog. Note that the constructors with
     * an owner window are preferred.
     * @param station the station to which this dialog is responsible
     * @param undecorated whether the dialog should suppress the default decorations
     */
    public ScreenDockFrame( ScreenDockStation station, boolean undecorated ){
        super( station );
        this.frame = new JFrame();
        init( undecorated );
    }
        
    private void init( boolean undecorated ){
        if( undecorated ){
            frame.setUndecorated( true );
            frame.getRootPane().setWindowDecorationStyle( JRootPane.NONE );
        }
        
        frame.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
        
        init( frame, frame.getContentPane(), undecorated );
    }

    /**
     * Gets the frame which represents the window.
     * @return the window
     */
    public JFrame getFrame() {
        return frame;
    }
    
    public void destroy() {
        frame.dispose();
    }

    public void toFront() {
        frame.toFront();
    }

    @Override
    protected void updateTitleText() {
        frame.setTitle( getTitleText() );
    }
    
    @Override
    protected void updateTitleIcon() {
        Icon icon = getTitleIcon();
        if( icon == null ){
            frame.setIconImage( null );
        }
        else{
            frame.setIconImage( DockUtilities.iconImage( icon ) );
        }
    }
}