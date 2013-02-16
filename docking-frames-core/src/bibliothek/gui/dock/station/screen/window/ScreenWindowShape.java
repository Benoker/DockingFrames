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

import bibliothek.gui.dock.station.screen.ScreenDockWindow;

/**
 * The {@link ScreenWindowShape} is used by {@link ScreenDockWindow} to define which parts
 * of the window are visible and which parts are transparent. The {@link ScreenWindowShape} of any
 * window can be configured in {@link WindowConfiguration}.
 * @author Benjamin Sigg
 */
public interface ScreenWindowShape {
	/**
	 * Informs this shape about the {@link ScreenDockWindow} that can be configured by <code>this</code>. The methods
	 * of <code>callback</code> should be called by the EDT.<br>
	 * To set the shape this class can call {@link ScreenWindowShapeCallback#setShape(java.awt.Shape)} at any time.
	 * @param callback the window, or <code>null</code>
	 */
	public void setCallback( ScreenWindowShapeCallback callback );
	
	/**
	 * Called by the {@link ScreenDockWindow} if its size has changed, can lead to a call to {@link ScreenWindowShapeCallback#setShape(java.awt.Shape)}
	 */
	public void onResize();

	/**
	 * Called by the {@link ScreenDockWindow} if it was made visible, can lead to a call to {@link ScreenWindowShapeCallback#setShape(java.awt.Shape)}
	 */
	public void onShown();
}
