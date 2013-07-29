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
package bibliothek.gui.dock.station.screen.window;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.Workarounds;

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
     * @param configuration the configuration to apply during creation of this window
     * @param undecorated whether the dialog should suppress the default decorations
     */
    public ScreenDockFrame( ScreenDockStation station, WindowConfiguration configuration, boolean undecorated ){
        super( station, configuration );
        this.frame = new JFrame();
        init( undecorated, configuration );
    }
        
    private void init( boolean undecorated, WindowConfiguration configuration ){
        if( undecorated ){
            frame.setUndecorated( true );
            frame.getRootPane().setWindowDecorationStyle( JRootPane.NONE );
        }
        
        frame.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
        frame.addWindowListener( new WindowAdapter(){
        	@Override
        	public void windowClosing( WindowEvent e ){
        		fireWindowClosing();
        	}
		});
        
        init( frame, frame.getContentPane(), configuration, undecorated );
        
        boolean translucency = false;
        if( configuration.isTransparent() ){
        	translucency = Workarounds.getDefault().setTranslucent( frame );
        }
        if( !translucency && configuration.getShape() != null ){
        	setShape( frame, configuration.getShape() );
        }
    }

    /**
     * Gets the frame which represents the window.
     * @return the window
     */
    public JFrame getFrame() {
        return frame;
    }
    
    public void destroy() {
    	super.destroy();
        frame.dispose();
    }

    public void toFront() {
        frame.toFront();
    }

    @Override
    protected void updateTitleText() {
        frame.setTitle( getTitleText() );
    }
    
    public void setPreventFocusStealing( boolean prevent ){
    	frame.setFocusableWindowState( !prevent );
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