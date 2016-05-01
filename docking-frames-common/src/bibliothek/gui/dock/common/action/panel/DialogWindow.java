/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.common.action.panel;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import bibliothek.gui.dock.common.action.CPanelPopup;

/**
 * A {@link PanelPopupWindow} managing a {@link JDialog}.
 * @author Benjamin Sigg
 */
public class DialogWindow extends AbstractPanelPopupWindow{
	/** the dialog which is managed by this window */
	private JDialog dialog;
	
	/** the owner of this dialog */
	private CPanelPopup panel;
	
	/** a listener to {@link #dialog} */
	private WindowListener listener = new WindowAdapter(){
		@Override
		public void windowClosed( WindowEvent e ){
			closing();
		}
		
		@Override
		public void windowClosing( WindowEvent e ){
			closing();
		}
		
		@Override
		public void windowDeactivated( WindowEvent e ){
			if( panel.isCloseOnFocusLost() ){
				close();
			}
		}
	};
	
	/**
	 * Creates a new window.
	 * @param owner the parent of the dialog
	 * @param panel the owner of the dialog
	 */
	public DialogWindow( Component owner, CPanelPopup panel ){
		this.panel = panel;
		dialog = createDialog( owner );
		dialog.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
		dialog.addWindowListener( listener );
	}
	
	/**
	 * Sets whether the dialog has a decoration or not.
	 * @param undecorated whether to decorate or not
	 * @see JDialog#setUndecorated(boolean)
	 */
	public void setUndecorated( boolean undecorated ){
		dialog.setUndecorated( undecorated );
	}
	
	/**
	 * Sets the contents of the dialog.
	 * @param content the contents, may be <code>null</code>
	 */
	public void setContent( Component content ){
		dialog.getContentPane().removeAll();
		if( content != null ){
			dialog.add( content );
		}
	}
	
	/**
	 * Shows the dialog at the given screen location.
	 * @param x the dialogs location
	 * @param y the dialogs location
	 */
	public void open( int x, int y ){
		dialog.pack();
		dialog.setLocation( x, y );
		validateBounds();
		dialog.setVisible( true );
	}
	
	/**
	 * Opens the dialog relative to <code>relativeTo</code>.
	 * @param relativeTo some component
	 */
	public void open( Component relativeTo ){
		dialog.pack();
		dialog.setLocationRelativeTo( relativeTo );
		validateBounds();
		dialog.setVisible( true );
	}
	
	private void validateBounds(){
		Rectangle bounds = dialog.getBounds();
		
		Point location = dialog.getLocation();
		
		GraphicsConfiguration bestConfiguration = null;
		int bestDistance = 0;
		
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		for( GraphicsDevice device : environment.getScreenDevices() ){
			GraphicsConfiguration configuration = device.getDefaultConfiguration();
			Rectangle screenBounds = configuration.getBounds();
			
			if( screenBounds.contains( location )){
				bestConfiguration = configuration;
				bestDistance = 0;
			}
			else{
				int dx;
				int dy;
				
				if( screenBounds.x <= location.x && screenBounds.x + screenBounds.width >= location.x ){
					dx = 0;
				}
				else{
					dx = Math.min(Math.abs(screenBounds.x - location.x), Math.abs(screenBounds.x + screenBounds.width - location.x));
				}
				
				if( screenBounds.y <= location.y && screenBounds.y + screenBounds.height >= location.y ){
					dy = 0;
				}
				else{
					dy = Math.min(Math.abs(screenBounds.y - location.y), Math.abs(screenBounds.y + screenBounds.height - location.y));
				}
				
				int delta = dx + dy;
				if( delta < bestDistance || bestConfiguration == null ){
					bestDistance = delta;
					bestConfiguration = configuration;
				}
			}
		}
		
		bounds = validateBounds( bounds, bestConfiguration );
		if( bounds != null ){
			dialog.setBounds( bounds );
		}
	}
	
	public boolean isOpen(){
		return dialog.isVisible();
	}
	
	public void close(){
		dialog.setVisible( false );
	}
	
	private void closing(){
		dialog.dispose();
		dialog.removeWindowListener( listener );
		fireClosed();
	}
	
	/**
	 * Creates a new dialog with the ancestor window of <code>owner</code>
	 * as owner.
	 * @param owner some component
	 * @return the new dialog
	 */
	protected JDialog createDialog( Component owner ){
		Window window = SwingUtilities.getWindowAncestor( owner );
		if( window instanceof Frame )
			return new JDialog( (Frame)window );
		if( window instanceof Dialog )
			return new JDialog( (Dialog)window );
		return new JDialog();
	}
}
