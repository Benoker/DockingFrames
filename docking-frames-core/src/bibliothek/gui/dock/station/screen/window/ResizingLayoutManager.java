/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import bibliothek.gui.dock.station.screen.ScreenDockWindow;

/**
 * This {@link LayoutManager} changes the size of a {@link ScreenDockWindow} such that it has
 * the same size as its preferred size, this update is only executed if the content of the window
 * was invalidated.<br>
 * This {@link LayoutManager} behaves like a {@link GridLayout} with one column and one row.
 * @author Benjamin Sigg
 */
public class ResizingLayoutManager extends GridLayout{
	private ScreenDockWindow window;
	private Component windowComponent;
	
	/**
	 * Creates a new layout manager
	 * @param window the window which is updated
	 * @param windowComponent the component whose preferred size will be used for <code>window</code>
	 */
	public ResizingLayoutManager( ScreenDockWindow window, Component windowComponent ){
		this.window = window;
		this.windowComponent = windowComponent;
	}
	
	@Override
	public void layoutContainer( Container parent ){
		Rectangle bounds = window.getWindowBounds();
		Dimension size = windowComponent.getPreferredSize();
		if( bounds.width != size.width || bounds.height != size.height ){
			window.setWindowBounds( new Rectangle( bounds.getLocation(), size ) );
		}
		
		super.layoutContainer( parent );
	}
}
