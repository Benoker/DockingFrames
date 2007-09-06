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
import bibliothek.gui.dock.station.OverpaintablePanel;
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
    /** The observer to which the {@link #pane} of this dialog has been added */
    private SecureMouseFocusObserver observer;
    
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
    protected OverpaintablePanel createContent() {
        OverpaintablePanel overpaint = super.createContent();
        
        pane = new GlassedPane();
        overpaint.setContentPane( pane );
        content = pane.getContentPane();
        
        return overpaint;
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
            if( observer != null ){
                observer.removeGlassPane( pane );
            }
            
            observer = (SecureMouseFocusObserver)getStation().getController().getFocusObserver();
            observer.addGlassPane( pane );
        }
        
        @Override
        public void windowClosed( WindowEvent e ) {
            if( observer != null ){
                observer.removeGlassPane( pane );
                observer = null;
            }
        }
    }
}
