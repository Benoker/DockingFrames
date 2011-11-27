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
	
	/** only required if {@link #moveOnTitleGrab}, starts a drag and drop operation if the mouse is over an area where dropping is possible */
	private boolean dropIfPossible = true;
	
	/** only required if {@link #moveOnTitleGrab} and {@link #dropIfPossible}, the window jumps back into its original position if dropping is possible */
	private boolean jumpIfDropable = true;
	
	/** whether the user can change the size of the window */
	private boolean resizeable = true;
	
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
	
	/**
	 * Sets whether a drag and drop operation starts if the mouse is over an area where drag and drop would be possible.
	 * This setting only has an effect if {@link #isMoveOnTitleGrab()} returns <code>true</code>.
	 * @param dropIfPossible whether drag and drop should be possible even if the window is already moved around
	 */
	public void setDropIfPossible( boolean dropIfPossible ){
		this.dropIfPossible = dropIfPossible;
	}
	
	/**
	 * Tells whether a drag and drop operation starts whenever possible, only has an effect if {@link #isMoveOnTitleGrab()}
	 * is <code>true</code>.
	 * @return whether the operation starts
	 * @see #setDropIfPossible(boolean)
	 */
	public boolean isDropIfPossible(){
		return dropIfPossible;
	}
	
	/**
	 * Sets whether a window jumps back into its starting position if dropping is possible, only has an effect if 
	 * {@link #isMoveOnTitleGrab()} and {@link #isDropIfPossible()} return <code>true</code>.
	 * @param jumpIfDropable whether the window jumps back to its original position
	 */
	public void setJumpIfDropable( boolean jumpIfDropable ){
		this.jumpIfDropable = jumpIfDropable;
	}
	
	/**
	 * Tells whether a window jumps back to its starting position.
	 * @return whether the window jumps back
	 * @see #setJumpIfDropable(boolean)
	 */
	public boolean isJumpIfDropable(){
		return jumpIfDropable;
	}
	
	/**
	 * Sets whether the user can resize the window. Otherwise the window will always have the preferred size
	 * of its children {@link Component}s.
	 * @param resizeable whether the user can resize the window
	 */
	public void setResizeable( boolean resizeable ){
		this.resizeable = resizeable;
	}
	
	/**
	 * Tells whether the user can resize the window.
	 * @return whether the window is resizeable
	 * @see #setResizeable(boolean)
	 */
	public boolean isResizeable(){
		return resizeable;
	}
}
