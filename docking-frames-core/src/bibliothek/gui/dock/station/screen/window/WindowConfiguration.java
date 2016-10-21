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

import javax.swing.border.Border;

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
	
	/** whether moving is possible by grabbing the border */
	private boolean moveOnBorder = true;
	
	/** only required if {@link #moveOnTitleGrab}, starts a drag and drop operation if the mouse is over an area where dropping is possible */
	private boolean allowDragAndDropOnTitle = true;
	
	/** only required if {@link #moveOnTitleGrab} and {@link #allowDragAndDropOnTitle}, the window jumps back into its original position if dropping is possible */
	private boolean resetOnDropable = true;
	
	/** whether the user can change the size of the window */
	private boolean resizeable = true;
	
	/** whether the window itself is transparent */
	private boolean transparent = false;
	
	/** which parts of the window are visible */
	private ScreenWindowShape shape = null;
	
	/** creates new borders */
	private ScreenDockWindowBorderFactory borderFactory = DefaultScreenDockWindowBorder.FACTORY;
	
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
	 * Sets whether moving the window is possible by grabbing the border.
	 * @param moveOnBorder whether moving the window is possible
	 */
	public void setMoveOnBorder( boolean moveOnBorder ){
		this.moveOnBorder = moveOnBorder;
	}
	
	/**
	 * Tells whether moving the window is possible by grabbing the border.
	 * @return whether the border can be grabbed
	 */
	public boolean isMoveOnBorder(){
		return moveOnBorder;
	}
	
	/**
	 * Sets whether a drag and drop operation starts if the mouse is over an area where drag and drop would be possible.
	 * This setting only has an effect if {@link #isMoveOnTitleGrab()} returns <code>true</code>.
	 * @param allowDragAndDropOnTitle whether drag and drop should be possible even if the window is already moved around
	 */
	public void setAllowDragAndDropOnTitle( boolean allowDragAndDropOnTitle ){
		this.allowDragAndDropOnTitle = allowDragAndDropOnTitle;
	}
	
	/**
	 * Tells whether a drag and drop operation starts whenever possible, only has an effect if {@link #isMoveOnTitleGrab()}
	 * is <code>true</code>.
	 * @return whether the operation starts
	 * @see #setAllowDragAndDropOnTitle(boolean)
	 */
	public boolean isAllowDragAndDropOnTitle(){
		return allowDragAndDropOnTitle;
	}
	
	/**
	 * Sets whether a window jumps back into its starting position if dropping is possible, only has an effect if 
	 * {@link #isMoveOnTitleGrab()} and {@link #isAllowDragAndDropOnTitle()} return <code>true</code>.
	 * @param resetOnDropable whether the window jumps back to its original position
	 */
	public void setResetOnDropable( boolean resetOnDropable ){
		this.resetOnDropable = resetOnDropable;
	}
	
	/**
	 * Tells whether a window jumps back to its starting position.
	 * @return whether the window jumps back
	 * @see #setResetOnDropable(boolean)
	 */
	public boolean isResetOnDropable(){
		return resetOnDropable;
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
	
	/**
	 * Tells whether the window is transparent. Whether transparency affects decorations such as a {@link Border}
	 * depends on the {@link ScreenDockWindow} itself.<br>
	 * Note that either {@link #isTransparent() transparency} or {@link #getShape() shape} of a window can be
	 * set, but not both at the same time. If both are set, then {@link #isTransparent() transparency} is considered to be more
	 * important, and {@link #getShape() the shape} is used as fallback.
	 * @param transparent whether the window is transparent
	 */
	public void setTransparent( boolean transparent ){
		this.transparent = transparent;
	}
	
	/**
	 * Tells whether the window is transparent.
	 * @return whether the window is transparent
	 * @see #setTransparent(boolean)
	 */
	public boolean isTransparent(){
		return transparent;
	}
	
	/**
	 * Tells which parts of the window are visible. A value of <code>null</code> indicates that the entire window
	 * should be visible.
	 * Note that either {@link #isTransparent() transparency} or {@link #getShape() shape} of a window can be
	 * set, but not both at the same time. If both are set, then {@link #isTransparent() transparency} is considered to be more
	 * important, and {@link #getShape() the shape} is used as fallback.
	 * @param shape the shape of the window, or <code>null</code>
	 */
	public void setShape( ScreenWindowShape shape ){
		this.shape = shape;
	}
	
	/**
	 * Tells which parts of the {@link ScreenDockWindow} are visible.
	 * @return the visible part of the window, can be <code>null</code>
	 */
	public ScreenWindowShape getShape(){
		return shape;
	}
	
	/**
	 * Sets a factory which might be used to create a border for the new window. There is no guarantee that the
	 * border is actually created, most windows will not create a window if {@link #isResizeable()} returns <code>false</code>.
	 * @param borderFactory the factory or <code>null</code> if no border should be created
	 */
	public void setBorderFactory( ScreenDockWindowBorderFactory borderFactory ){
		this.borderFactory = borderFactory;
	}
	
	/**
	 * Gets the current factory for creating the border of the window.
	 * @return the current factory, can be <code>null</code>
	 * @see #setBorderFactory(ScreenDockWindowBorderFactory)
	 */
	public ScreenDockWindowBorderFactory getBorderFactory(){
		return borderFactory;
	}
}
