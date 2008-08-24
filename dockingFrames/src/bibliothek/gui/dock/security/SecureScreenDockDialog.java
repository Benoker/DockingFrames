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

package bibliothek.gui.dock.security;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JRootPane;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockDialog;

/**
 * The secure reimplementation of {@link ScreenDockDialog}.
 * @author Benjamin Sigg
 */
public class SecureScreenDockDialog extends SecureAbstractScreenDockWindow {
    private JDialog dialog;
    
    /**
     * Creates a new dialog.
     * @param station the station for which this dialog is shown
     */
    public SecureScreenDockDialog( ScreenDockStation station, boolean undecorated ) {
        super( station );
        this.dialog = new JDialog();
        init( undecorated );
    }
    
    /**
     * Creates a new dialog.
     * @param station the station for which this dialog is shown
     * @param dialog the owner of this dialog
     */
    public SecureScreenDockDialog( ScreenDockStation station, Dialog dialog, boolean undecorated ) {
        super( station );
        this.dialog = new JDialog( dialog );
        init( undecorated );
    }

    /**
     * Creates a new dialog.
     * @param station the station for which this dialog is shown
     * @param frame the owner of this dialog
     */
    public SecureScreenDockDialog( ScreenDockStation station, Frame frame, boolean undecorated ) {
        super( station );
        this.dialog = new JDialog( frame );
        init( undecorated );
    }
    
    private void init( boolean undecorated ){
        dialog.addWindowListener( new Listener() );
        
        if( undecorated ){
            dialog.setUndecorated( true );
            dialog.getRootPane().setWindowDecorationStyle( JRootPane.NONE );
        }
        
        dialog.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
        dialog.setModal( false );
        
        init( dialog, dialog.getContentPane(), undecorated );
    }

    public void destroy() {
        dialog.dispose();
    }

    public void toFront() {
        dialog.toFront();
    }

    @Override
    protected void updateTitleText() {
        dialog.setTitle( getTitleText() );
    }
    
    /**
     * A listener to the enclosing {@link SecureScreenDockDialog}. If the dialog
     * is shown, this listener will register the GlassPane. If the dialog is
     * disposed, then this listener will unregister the GlassPane.
     * @author Benjamin Sigg
     */
    private class Listener extends WindowAdapter{
        @Override
        public void windowOpened( WindowEvent e ) {
            ensureSecure( true );
        }
        
        @Override
        public void windowClosed( WindowEvent e ) {
            ensureSecure( false );
        }
    }
}
