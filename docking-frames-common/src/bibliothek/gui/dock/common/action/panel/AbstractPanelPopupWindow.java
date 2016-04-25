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

import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation of {@link PanelPopupWindow}, handles
 * the {@link PanelPopupWindowListener}s.<br>
 * Subclasses should call {@link #validateBounds(Rectangle, GraphicsConfiguration)} before they get visible
 * @author Benjamin Sigg
 */
public abstract class AbstractPanelPopupWindow implements PanelPopupWindow{
	private List<PanelPopupWindowListener> listeners = new ArrayList<PanelPopupWindowListener>();
	
	public void addListener( PanelPopupWindowListener listener ){
		if( listener == null )
			throw new IllegalArgumentException( "listener must not be null" );
		listeners.add( listener );
	}
	
	public void removeListener( PanelPopupWindowListener listener ){
		listeners.remove( listener );	
	}
	
	/**
	 * Returns an array containing all the listeners of this {@link AbstractPanelPopupWindow}.
	 * @return all the listeners
	 */
	protected PanelPopupWindowListener[] listeners(){
		return listeners.toArray( new PanelPopupWindowListener[ listeners.size() ] );
	}
	
	/**
	 * Informs all listeners that this window has been closed
	 */
	protected void fireClosed(){
		for( PanelPopupWindowListener listener : listeners() ){
			listener.closed( this );
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
	
}
