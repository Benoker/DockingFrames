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

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.station.screen.ScreenDockWindowFactory;

/**
 * A {@link WindowConfiguration} describes some general aspects of a {@link ScreenDockWindow}. It is used during
 * creation of a window ({@link ScreenDockWindowFactory#createWindow(bibliothek.gui.dock.ScreenDockStation, WindowConfiguration)}),
 * and by the {@link ScreenDockStation} itself during drag and drop operations.
 * @author Benjamin Sigg
 */
public class WindowConfiguration {
	/** whether the window should move if the title is grabbed */
	private boolean moveOnTitleGrab = false;
	
	/**
	 * Sets whether the window should move if the title is dragged by the mouse.
	 * @param moveOnTitleGrab whether the window should move
	 */
	public void setMoveOnTitleGrab( boolean moveOnTitleGrab ){
		this.moveOnTitleGrab = moveOnTitleGrab;
	}
	
	/**
	 * Tells whether the window should move if the title is dragged by the mouse.
	 * @return <code>true</code> if the entire window should move
	 */
	public boolean isMoveOnTitleGrab(){
		return moveOnTitleGrab;
	}
}
