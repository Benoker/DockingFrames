/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.station.toolbar.menu;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * The default {@link CustomizationMenu} makes use of a {@link JDialog} to show its contents.
 * @author Benjamin Sigg
 */
@Todo( compatibility=Compatibility.COMPATIBLE, target=Version.VERSION_1_1_2, priority=Priority.MINOR,
	description="This class is almost identical with 'DialogWindow' from Common, maybe these two classes can be merged?")
public class DefaultCustomizationMenu extends AbstractCustomizationMenu{
	/** the dialog that is this menu */
	private JDialog dialog;
	
	/** whether the dialog has any decorations at all */
	private boolean undecorated = true;
	
	/** whether to close this menu if the focus is lost */
	private boolean closeOnFocusLost = true;
	
	/**
	 * Makes the dialog that is used by this menu undecorated. Calling this method has no effect if
	 * the menu is already visible.
	 * @param undecorated whether to {@link JDialog#setUndecorated(boolean) undecorate} the dialog
	 */
	public void setUndecorated( boolean undecorated ){
		this.undecorated = undecorated;
	}
	
	/**
	 * Tells whether the {@link JDialog} of this menu is undecorated.
	 * @return the result of {@link JDialog#isUndecorated()}
	 */
	public boolean isUndecorated(){
		return undecorated;
	}
	
	/**
	 * Automatically closes this menu if the dialog loses focus.
	 * @param closeOnFocusLost whether to automatically close the menu
	 */
	public void setCloseOnFocusLost( boolean closeOnFocusLost ){
		this.closeOnFocusLost = closeOnFocusLost;
	}
	
	/**
	 * Whether the menu automatically is closed if it loses focus.
	 * @return whether to automatically close the menu
	 */
	public boolean isCloseOnFocusLost(){
		return closeOnFocusLost;
	}
	
	@Override
	protected void doOpen( int x, int y, Component content ){
		dialog = createDialog( getCallback().getParent() );
		dialog.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
		dialog.addWindowListener( new WindowAdapter(){
			@Override
			public void windowClosed( WindowEvent e ){
				closed();
			}
			
			public void windowClosing( WindowEvent e ){
				close();
			}
			
			@Override
			public void windowDeactivated( WindowEvent e ){
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						CustomizationMenuCallback callback = getCallback();
						
						if( isCloseOnFocusLost() && (callback == null || callback.isAutoCloseAllowed()) ){
							close();
						}	
					}
				});
			}
		});
		dialog.setUndecorated( undecorated );
		dialog.add( content, BorderLayout.CENTER );
		dialog.pack();
		dialog.setLocation( x, y );
		validateBounds();
		dialog.setVisible( true );
	}

	@Override
	protected void doClose(){
		dialog.dispose();
		dialog.getContentPane().removeAll();
		dialog = null;
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
	
	/**
	 * Should be called before this window is made visible, ensure that the boundaries are valid.
	 * @param bounds the proposed boundaries
	 * @param configuration the screen on which this window is going to be visible, might be <code>null</code>
	 * @return the actual boundaries, can be <code>null</code> to indicate that <code>bounds</code> is valid
	 */
	protected Rectangle validateBounds( Rectangle bounds, GraphicsConfiguration configuration ){
		if( configuration == null ){
			return null;
		}
		
		Rectangle screen = configuration.getBounds();
		bounds = new Rectangle( bounds );
		
		bounds.width = Math.min( bounds.width, screen.width );
		bounds.height = Math.min( bounds.height, screen.height );
		bounds.x = Math.min( Math.max( bounds.x, screen.x ), screen.x + screen.width - bounds.width );
		bounds.y = Math.min( Math.max( bounds.y, screen.y ), screen.y + screen.height - bounds.height );
		
		return bounds;
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
