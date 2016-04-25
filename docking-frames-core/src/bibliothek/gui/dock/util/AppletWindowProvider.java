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
package bibliothek.gui.dock.util;

import java.applet.Applet;
import java.awt.Component;

/**
 * A {@link WindowProvider} designed to work with {@link java.applet.Applet}s. Clients
 * should call {@link #start()} and {@link #stop()} from the methods
 * {@link Applet#start()} and {@link Applet#stop()}.
 * @author Benjamin Sigg
 */
public class AppletWindowProvider extends ComponentWindowProvider{
	/** whether the applet has been started or not */
	private boolean running = false;
	
	/**
	 * Creates a new window provider
	 * @param component some component of the applet or the applet itself,
	 * can be <code>null</code>
	 */
	public AppletWindowProvider( Component component ){
		super( component );
	}
	
	/**
	 * Informs this provider that the applet started.
	 */
	public void start(){
		running = true;
		fireVisibilityChanged( isShowing() );
	}
	
	/**
	 * Informs this provider that the applet stopped.
	 */
	public void stop(){
		running = false;
		fireVisibilityChanged( isShowing() );
	}
    
    public boolean isShowing(){
    	return running;
    }
}
