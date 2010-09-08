/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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

import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Window;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.security.SecureScreenDockDialog;

/**
 * This default implementation of a {@link ScreenDockFullscreenStrategy} just works with
 * the boundaries of a {@link ScreenDockWindow}. It assumes that either {@link ScreenDockDialog}
 * or {@link ScreenDockFrame} is used. Subclasses may extend this list, the algorithms of this
 * strategy should work with most implementations that are based on a {@link Window}.
 * @author Benjamin
 */
public class DefaultScreenDockFullscreenStrategy implements ScreenDockFullscreenStrategy {
	public void install( ScreenDockStation station ) {
		// ignore
	}
	
	public void uninstall( ScreenDockStation station ) {
		// ignore
	}

	public boolean isFullscreen( ScreenDockWindow window ) {
		if( window instanceof ScreenDockDialog ){
			return isFullscreen( window, ((ScreenDockDialog)window).getDialog() );
		}
		if( window instanceof SecureScreenDockDialog ){
			return isFullscreen( window, ((SecureScreenDockDialog)window).getDialog() );
		}
		if( window instanceof ScreenDockFrame ){
			return isFullscreen( window, ((ScreenDockFrame)window).getFrame() );
		}
		
		return false;
	}


	/**
	 * Tells whether the frame or dialog <code>window</code> is in fullscreen mode.
	 * @param wrapper the wrapper around <code>window</code>
	 * @param window the window to check
	 * @return the current mode
	 */
	public boolean isFullscreen( ScreenDockWindow wrapper, Window window ){
		GraphicsConfiguration configuration = window.getGraphicsConfiguration();
		if( configuration == null ){
			return false;
		}
		
		Rectangle fullscreen = configuration.getBounds();
		Rectangle current = window.getBounds();
		current = new Rectangle( current.x - 1, current.y - 1, current.width + 2, current.height + 2 );
		
		return current.contains( fullscreen ) && wrapper.getNormalBounds() != null;
	}
	
	public void setFullscreen( ScreenDockWindow window, boolean fullscreen ) {
		if( window instanceof ScreenDockDialog ){
			setFullscreen( window, ((ScreenDockDialog)window).getDialog(), fullscreen );
		}
		if( window instanceof SecureScreenDockDialog ){
			setFullscreen( window, ((SecureScreenDockDialog)window).getDialog(), fullscreen );
		}
		if( window instanceof ScreenDockFrame ){
			setFullscreen( window, ((ScreenDockFrame)window).getFrame(), fullscreen );
		}
	}
	
	/**
	 * Sets the fullscreen mode of <code>window</code>.
	 * @param wrapper the wrapper around <code>window</code>
	 * @param window the window whose state is to be changed
	 * @param fullscreen the new state
	 */
	public void setFullscreen( ScreenDockWindow wrapper, Window window, boolean fullscreen ){
		if( isFullscreen( wrapper, window ) != fullscreen ){
			if( fullscreen ){
				GraphicsConfiguration configuration = window.getGraphicsConfiguration();
				if( configuration != null ){
					Rectangle bounds = configuration.getBounds();
					wrapper.setNormalBounds( wrapper.getWindowBounds() );
					window.setBounds( bounds.x-1, bounds.y-1, bounds.width+2, bounds.height+2 );
				}
			}
			else{
				Rectangle bounds = wrapper.getNormalBounds();
				if( bounds != null ){
					window.setBounds( bounds );
					wrapper.setNormalBounds( null );
				}
			}
		}
	}
}
