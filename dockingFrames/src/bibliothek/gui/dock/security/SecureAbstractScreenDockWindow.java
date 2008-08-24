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

import java.awt.Container;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.station.screen.AbstractScreenDockWindow;

/**
 * An {@link AbstractScreenDockWindow} that uses a {@link GlassedPane}
 * in order to catch all {@link MouseEvent}s and to report them to the
 * {@link SecureMouseFocusObserver}. Subclasses should call {@link #ensureSecure(boolean)}
 * whenever the visibility of the window changes.
 * @author Benjamin Sigg
 */
public abstract class SecureAbstractScreenDockWindow extends AbstractScreenDockWindow {
    /** The panel on which the {@link DockableDisplayer} is added */
    private JComponent content;
    /** The panel used to catch MouseEvents */
    private GlassedPane pane;
    /** The observer to which the {@link #pane} of this dialog has been added */
    private SecureMouseFocusObserver observer;
    
    /**
     * Creates a new window
     * @param station the owner of this window
     */
    public SecureAbstractScreenDockWindow( ScreenDockStation station ){
        super( station );
    }

    @Override
    protected OverpaintablePanel createContent() {
        OverpaintablePanel overpaint = super.createContent();
        
        pane = new GlassedPane();
        overpaint.setBasePane( pane );
        content = pane.getContentPane();
        
        return overpaint;
    }
    
    @Override
    protected Container getDisplayerParent() {
        return content;
    }
    
    /**
     * Ensures that this window is connected to the {@link SecureMouseFocusObserver}
     * of this secure environment.
     * @param visible whether the window is visible or not
     */
    protected void ensureSecure( boolean visible ){
        if( observer != null ){
            observer.removeGlassPane( pane );
            observer = null;
        }
        
        if( visible ){
            observer = (SecureMouseFocusObserver)getController().getFocusObserver();
            observer.addGlassPane( pane );
        }
    }
}
