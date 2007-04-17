/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.security;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.station.ScreenDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockDialog;

/**
 * A {@link ScreenDockDialog} that inserts a {@link GlassedPane} between its
 * {@link Dockable} and the outer world. The GlassPane is added and removed
 * from the {@link SecureFocusController} automatically.
 * @author Benjamin Sigg
 */
public class SecureScreenDockDialog extends ScreenDockDialog {
    /** The panel on which the {@link DockableDisplayer} is added */
    private JComponent content;
    /** The panel used to catch MouseEvents */
    private GlassedPane pane;
    
    /**
     * Creates a new dialog.
     * @param station the station for which this dialog is shown
     * @param dialog the owner of this dialog
     */
    public SecureScreenDockDialog( ScreenDockStation station, Dialog dialog ) {
        super(station, dialog);
    }

    /**
     * Creates a new dialog.
     * @param station the station for which this dialog is shown
     * @param frame the owner of this dialog
     */
    public SecureScreenDockDialog( ScreenDockStation station, Frame frame ) {
        super(station, frame);
    }
    
    {
        addWindowListener( new Listener() );
    }
    
    @Override
    protected JComponent createContent() {
        pane = new GlassedPane();
        content = super.createContent();
        pane.setContentPane( content );
        return pane;
    }
    
    @Override
    protected Container getDisplayerParent() {
        return content;
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
            SecureFocusController controller = (SecureFocusController)getStation().getController().getFocusController();
            controller.addGlassPane( pane );
        }
        
        @Override
        public void windowClosed( WindowEvent e ) {
            SecureFocusController controller = (SecureFocusController)getStation().getController().getFocusController();
            controller.removeGlassPane( pane );
        }
    }
}
