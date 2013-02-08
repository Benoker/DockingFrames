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

import javax.swing.JDesktopPane;
import javax.swing.JLayeredPane;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.InternalBoundaryRestriction;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.station.screen.ScreenDockWindowFactory;

/**
 * A simple factory creating new instances of {@link InternalDockDialog}.
 * @author Benjamin Sigg
 * @see InternalBoundaryRestriction
 */
public class InternalScreenDockWindowFactory implements ScreenDockWindowFactory{
	/** the parent for new windows */
	private JDesktopPane desktop;
	
	/** the layer in which new {@link InternalDockDialog}s will appear */
	private int screenDockWindowLayer = JDesktopPane.MODAL_LAYER;
	
	/**
	 * Creates the factory. All windows will have <code>desktop</code> as parent.
	 * @param desktop the parent for new windows
	 */
	public InternalScreenDockWindowFactory( JDesktopPane desktop ){
		if( desktop == null ){
			throw new IllegalArgumentException( "desktop must not be null" );
		}
		this.desktop = desktop;
	}
	
	/**
	 * Sets the layer in which new {@link InternalDockDialog}s will appear. The default value
	 * for this property is {@link JLayeredPane#MODAL_LAYER}. Please have a look at
	 * {@link JLayeredPane#setLayer(java.awt.Component, int)} to learn more about the meaning
	 * of this integer.
	 * @param screenDockWindowLayer the layer, a constant like {@link JLayeredPane#MODAL_LAYER}
	 */
	public void setScreenDockWindowLayer( int screenDockWindowLayer ){
		this.screenDockWindowLayer = screenDockWindowLayer;
	}
	
	/**
	 * Gets the layer in which new {@link InternalDockDialog}s will appear.
	 * @return the layer
	 * @see #setScreenDockWindowLayer(int)
	 */
	public int getScreenDockWindowLayer(){
		return screenDockWindowLayer;
	}
	
	public ScreenDockWindow updateWindow( ScreenDockWindow window, WindowConfiguration configuration, ScreenDockStation station ){
		return null;
	}
	
	public ScreenDockWindow createWindow( ScreenDockStation station, WindowConfiguration configuration ){
		return new InternalDockDialog( station, configuration, desktop, getScreenDockWindowLayer() );
	}
}
