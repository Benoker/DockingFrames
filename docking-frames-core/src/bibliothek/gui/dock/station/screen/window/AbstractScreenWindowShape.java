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

import bibliothek.gui.dock.station.screen.ScreenDockWindow;

/**
 * This implementation of {@link ScreenWindowShape} does nothing on its own, but keeps track of the
 * {@link ScreenWindowShapeCallback}.<br>
 * Subclasses should call {@link #reshape()} to update the {@link Shape} of the {@link ScreenDockWindow}. 
 * @author Benjamin Sigg
 */
public abstract class AbstractScreenWindowShape implements ScreenWindowShape{
	/** information about the window which is shaped */
	private ScreenWindowShapeCallback callback;
	
	public void setCallback( ScreenWindowShapeCallback callback ){
		this.callback = callback;
	}

	/**
	 * Gets information about the window whose shape is changed. 
	 * @return the callback or <code>null</code>
	 */
	public ScreenWindowShapeCallback getCallback(){
		return callback;
	}
	
	/**
	 * First checks that the {@link #getCallback() callback} is not <code>null</code>, then calls
	 * {@link #getShape()} and sets the result.
	 */
	public void reshape(){
		if( callback != null ){
			callback.setShape( getShape() );
		}
	}
	
	/**
	 * Calculates the current shape of the window.
	 * @return the current shape, can be <code>null</code>
	 */
	protected abstract Shape getShape();
	
	public void onResize(){
		reshape();
	}

	public void onShown(){
		reshape();
	}
}
