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
package bibliothek.gui.dock.station.screen.window;

import java.awt.Rectangle;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItem;

/**
 * Represents a {@link ScreenDockWindow} in a {@link PlaceholderList}.
 * @author Benjamin Sigg
 */
public class ScreenDockWindowHandle implements PlaceholderListItem<Dockable>{
	/** the window represented by this handle */
	private ScreenDockWindow window;
	
	/** the configuration that was used to set up {@link #window} */
	private WindowConfiguration configuration;
	
	/** the element shown by {@link #window} */
	private Dockable dockable;
	
	/**
	 * Creates a new handle.
	 * @param dockable the element shown by <code>window</code>
	 * @param window the window which is represented by this handle, not <code>null</code>
	 * @param configuration the configuration which was used to create <code>window</code>, not <code>null</code>
	 */
	public ScreenDockWindowHandle( Dockable dockable, ScreenDockWindow window, WindowConfiguration configuration ){
		if( dockable == null ){
			throw new IllegalArgumentException( "dockable must not be null" );
		}
		
		if( window == null ){
			throw new IllegalArgumentException( "window must not be null" );
		}
		
		if( configuration == null ){
			throw new IllegalArgumentException( "configuration must not be null" );
		}
		
		this.dockable = dockable;
		this.window = window;
		this.configuration = configuration;
	}
	
	public Dockable asDockable() {
		return dockable;
	}
	
	/**
	 * Sets the item that is shown by this window.
	 * @param dockable the element to show, can be <code>null</code>
	 */
	public void setDockable( Dockable dockable ) {
		this.dockable = dockable;
		window.setDockable( dockable );
	}

	/**
	 * Gets the window which is represented by this handle.
	 * @return the window
	 */
	public ScreenDockWindow getWindow() {
		return window;
	}
	
	/**
	 * Gets the configuration which was used to set up {@link #getWindow() the window}.
	 * @return the configuration, not <code>null</code>
	 */
	public WindowConfiguration getConfiguration(){
		return configuration;
	}
	
	/**
	 * Sets the window which is represented by this handle.
	 * @param window the new window, not <code>null</code>
	 * @param configuration the configuration that was used to set up <code>window</code>
	 */
	public void setWindow( ScreenDockWindow window, WindowConfiguration configuration ){
		this.window.setDockable( null );
		this.window = window;
		this.configuration = configuration;
		window.setDockable( dockable );
	}
	
	/**
	 * Gets the current normal bounds of the window represented by this handle.
	 * @return the boundaries, not <code>null</code>
	 */
	public Rectangle getBounds(){
		Rectangle result = null;
		
		if( window != null ){
			if( window.isFullscreen() ){
				result = window.getNormalBounds();
			}
			if( result == null ){
				result = window.getWindowBounds();
			}
		}
		
		return result;
	}
}
