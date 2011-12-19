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
package bibliothek.gui.dock.station.screen.window;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;

/**
 * A {@link ScreenDockWindow} that has a {@link JDesktopPane} as parent.
 * @author Benjamin Sigg
 */
public class InternalDockDialog extends AbstractScreenDockWindow{
	/** the parent */
	private JDesktopPane desktop;
	/** the {@link Component} that actually servers as dialog */
	private JPanel dialog;
	
	/**
	 * Creates the new dialog
	 * @param station the owner of this dialog
	 * @param configuration the configuration to apply during creation of this window
	 * @param desktop the parent of this dialog
	 */
	public InternalDockDialog( ScreenDockStation station, WindowConfiguration configuration, JDesktopPane desktop ){
		super( station, configuration );
		this.desktop = desktop;
		initDialog( configuration );
	}
	
	private void initDialog( WindowConfiguration configuration ){
		dialog = new JPanel();
		dialog.setVisible( false );
		desktop.add( dialog );
		desktop.setLayer( dialog, JDesktopPane.MODAL_LAYER );
		
		if( configuration.isTransparent() ){
			dialog.setOpaque( false );
		}
		
		init( dialog, dialog, configuration, true );
	}
	
	@Override
	protected void convertPointToScreen( Point point, Component component ){
		Point result = SwingUtilities.convertPoint( component, point, desktop );
		point.x = result.x;
		point.y = result.y;
	}
	
	@Override
	public void setWindowBounds( Rectangle bounds, boolean screenCoordinates ){
		if( screenCoordinates ){
			Point location = bounds.getLocation();
			SwingUtilities.convertPointFromScreen( location, desktop );
			bounds = new Rectangle( location, bounds.getSize() );
		}
		super.setWindowBounds( bounds, false );
	}
	
	public void destroy(){
		dialog.setVisible( false );
		desktop.remove( dialog );
	}

	public void toFront(){
		desktop.moveToFront( dialog );
	}
	
	public void setPreventFocusStealing( boolean prevent ){
    	// ignore
    }
}
