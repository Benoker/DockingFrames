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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.util.Workarounds;

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
     * @param configuration the configuration to apply during creation of this window
     * @param undecorated whether the dialog should suppress the default decorations
     */
    public ScreenDockDialog( ScreenDockStation station, WindowConfiguration configuration, boolean undecorated ){
        super( station, configuration );
        this.dialog = new JDialog();
        init( undecorated, configuration );
    }
    
    /**
     * Creates a new dialog.
     * @param station the station to which this dialog is responsible
     * @param configuration the configuration to apply during creation of this window
     * @param frame the owner of the dialog
     * @param undecorated whether the dialog should suppress the default decorations
     */
    public ScreenDockDialog( ScreenDockStation station, WindowConfiguration configuration, Frame frame, boolean undecorated ){
        super( station, configuration );
        this.dialog = new JDialog( frame );
        init( undecorated, configuration );
    }

    /**
     * Creates a new dialog.
     * @param station the station to which this dialog is responsible
     * @param configuration the configuration to apply during creation of this window
     * @param dialog the owner of this dialog
     * @param undecorated whether the dialog should suppress the default decorations
     */
    public ScreenDockDialog( ScreenDockStation station, WindowConfiguration configuration, Dialog dialog, boolean undecorated ){
        super( station, configuration );
        this.dialog = new JDialog( dialog );
        init( undecorated, configuration );
    }
    
    private void init( boolean undecorated, WindowConfiguration configuration ){
        if( undecorated ){
            dialog.setUndecorated( true );
            dialog.getRootPane().setWindowDecorationStyle( JRootPane.NONE );
        }
        
        dialog.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
        dialog.setModal( false );
        dialog.addWindowListener( new WindowAdapter(){
        	@Override
        	public void windowClosing( WindowEvent e ){
        		fireWindowClosing();
        	}
		});
        
        boolean translucency = false;
        
        if( configuration.isTransparent() ){
    		JPanel panel = new JPanel();
    		panel.setOpaque( false );
    		dialog.setContentPane( panel );
    		
        	translucency = Workarounds.getDefault().setTranslucent( dialog );
        }
        init( dialog, dialog.getContentPane(), configuration, undecorated );
        if( !translucency && configuration.getShape() != null ){
        	setShape( dialog, configuration.getShape() );
        }
    }
    
    public void setPreventFocusStealing( boolean prevent ){
    	dialog.setFocusableWindowState( !prevent );
    }
    
    /**
     * Gets the dialog which represents the window.
     * @return the window
     */
    public JDialog getDialog() {
        return dialog;
    }

    public void destroy() {
    	super.destroy();
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
