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

import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JRootPane;

import bibliothek.gui.dock.ScreenDockStation;

/**
 * An implementation of {@link ScreenDockWindow} which uses a {@link JDialog}.
 * The dialog can be used without decorations, in that case the resizing
 * system of {@link AbstractScreenDockWindow} is used.
 * @author Benjamin Sigg
 */
public class ScreenDockDialog extends AbstractScreenDockWindow {
    private JDialog dialog;
        
    /**
     * Creates a new dialog. Note that the constructors with
     * an owner window are preferred.
     * @param station the station to which this dialog is responsible
     * @param undecorated whether the dialog should suppress the default decorations
     */
    public ScreenDockDialog( ScreenDockStation station, boolean undecorated ){
        super( station );
        this.dialog = new JDialog();
        init( undecorated );
    }
    
    /**
     * Creates a new dialog.
     * @param station the station to which this dialog is responsible
     * @param frame the owner of the dialog
     * @param undecorated whether the dialog should suppress the default decorations
     */
    public ScreenDockDialog( ScreenDockStation station, Frame frame, boolean undecorated ){
        super( station );
        this.dialog = new JDialog( frame );
        init( undecorated );
    }

    /**
     * Creates a new dialog.
     * @param station the station to which this dialog is responsible
     * @param dialog the owner of this dialog
     * @param undecorated whether the dialog should suppress the default decorations
     */
    public ScreenDockDialog( ScreenDockStation station, Dialog dialog, boolean undecorated ){
        super( station );
        this.dialog = new JDialog( dialog );
        init( undecorated );
    }
    
    private void init( boolean undecorated ){
        if( undecorated ){
            dialog.setUndecorated( true );
            dialog.getRootPane().setWindowDecorationStyle( JRootPane.NONE );
        }
        
        dialog.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
        dialog.setModal( false );
        
        init( dialog, dialog.getContentPane(), undecorated );
    }
    
    /**
     * Gets the dialog which represents the window.
     * @return the window
     */
    public JDialog getDialog() {
        return dialog;
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
}
