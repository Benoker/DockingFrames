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

import javax.swing.JDialog;
import javax.swing.JRootPane;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockDialog;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * The secure reimplementation of {@link ScreenDockDialog}.
 * @author Benjamin Sigg
 * @deprecated this class is no longer necessary and will be removed in a future release
 */
@Deprecated
@Todo( compatibility=Compatibility.BREAK_MINOR, priority=Priority.MAJOR, target=Version.VERSION_1_1_1,
		description="remove this class, no replacement required" )
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
     * Gets the dialog which is used by this {@link ScreenDockWindow} to display
     * its content.
     * @return the dialog
     */
    public JDialog getDialog() {
		return dialog;
	}
}
