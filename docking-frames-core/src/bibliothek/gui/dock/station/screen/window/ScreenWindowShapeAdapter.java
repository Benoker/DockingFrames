/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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

import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.util.Workarounds;

/**
 * The {@link ScreenWindowShapeAdapter} makes a connection between a {@link ScreenDockWindow} and a {@link ScreenWindowShape}
 * forwarding events and calls from one of the to the other.
 * @author Benjamin Sigg
 */
public class ScreenWindowShapeAdapter {
	/** the window managed by this adapter */
	private ScreenDockWindow screenDockWindow;
	
	/** the window whose shape is changed */
	private Window window;
	
	/** the shape to apply, can be <code>null</code> */
	private ScreenWindowShape shape;
	
	/** whether this adapter actually forwards the {@link Shape}s */
	private boolean enabled = true;
	
	/** callback to {@link #shape} */
	private Callback callback = new Callback();
	
	/**
	 * Creates a new adapter.
	 * @param screenDockWindow the window representing <code>window</code>
	 * @param window the window whose shape is changed
	 */
	public ScreenWindowShapeAdapter( ScreenDockWindow screenDockWindow, Window window ){
		if( screenDockWindow == null ){
			throw new IllegalArgumentException( "screenDockWindow must not be null" );
		}
		if( window == null ){
			throw new IllegalArgumentException( "window must not be null" );
		}
		
		this.screenDockWindow = screenDockWindow;
		this.window = window;
	}
	
	/**
	 * Gets the {@link ScreenDockWindow} whose shape is changed.
	 * @return the window, not <code>null</code>
	 */
	public ScreenDockWindow getScreenDockWindow(){
		return screenDockWindow;
	}
	
	/**
	 * Gets the {@link Window} whose shape is changed.
	 * @return the window, not <code>null</code>
	 */
	public Window getWindow(){
		return window;
	}
	
	/**
	 * Whether this adapter actually forwards the shapes.
	 * @return whether the adapter is enabled
	 */
	public boolean isEnabled(){
		return enabled;
	}
	
	/**
	 * Disables this adapter, the {@link ScreenWindowShape} is set to <code>null</code> by this method.
	 */
	public void disable(){
		enabled = false;
		setShape( (ScreenWindowShape)null );
	}
	
	/**
	 * Sets the shape to apply, this method does nothing if this adapter is not {@link #isEnabled() enabled}. 
	 * @param shape the shape to apply or <code>null</code>
	 */
	public void setShape( ScreenWindowShape shape ){
		if( (isEnabled() || shape == null) && shape != this.shape ){
			if( this.shape != null ){
				this.shape.setCallback( null );
				window.removeComponentListener( callback );
			}
			
			this.shape = shape;
			
			if( this.shape != null ){
				this.shape.setCallback( callback );
				this.shape.onShown();
				window.addComponentListener( callback );
			}
		}
	}
	
	/**
	 * Sets the shape of {@link #getWindow() the window}. This method calls {@link Workarounds#setTransparent(Window, Shape)},
	 * if transparency cannot be set then {@link #disable()} is called.
	 * @param shape the shape to set, not <code>null</code>
	 */
	protected void setShape( Shape shape ){
		if( !Workarounds.getDefault().setTransparent( getWindow(), shape ) ){
			disable();
		}
	}
	
	/**
	 * This callback is forwarded to {@link ScreenWindowShapeAdapter#shape}
	 * @author Benjamin Sigg
	 */
	private class Callback extends ComponentAdapter implements ScreenWindowShapeCallback{
		public ScreenDockWindow getWindow(){
			return screenDockWindow;
		}
		
		public void setShape( Shape shape ){
			ScreenWindowShapeAdapter.this.setShape( shape );
		}
		
		@Override
		public void componentResized( ComponentEvent e ){
			if( shape != null ){
				shape.onResize();
			}
		}
		
		@Override
		public void componentShown( ComponentEvent e ){
			if( shape != null ){
				shape.onShown();
			}
		}
	}
}
