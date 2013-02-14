/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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

import javax.swing.border.Border;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;

/**
 * This border can be used by {@link ScreenDockWindow}s to paint a border. The border itself may offer methods
 * to paint indications whether the user currently moves or presses the mouse over it. The states will be set by
 * the window itself.
 * @author Benjamin Sigg
 */
public interface ScreenDockWindowBorder extends Border{
	/** various positions where the user can grab this border */
    public static enum Position{ N, E, S, W, NE, SW, NW, SE, MOVE, NOTHING };
    
	/**
	 * Sets the {@link DockController} which should be monitored for receiving colors.
	 * @param controller the new controller, can be <code>null</code>
	 */
    public void setController( DockController controller );
    
	/**
	 * Sets where the mouse is currently pressed.
	 * @param mousePressed the position, <code>null</code> equals {@link Position#NOTHING}
	 */
	public void setMousePressed( Position mousePressed );
	
	/**
	 * Sets where the mouse is currently hovering.
	 * @param mouseOver the position, <code>null</code> equals {@link Position#NOTHING}
	 */
	public void setMouseOver( Position mouseOver );
	
	/**
	 * Sets the size of the corners in pixels. If the mouse is in a corner, then the window
	 * can be resized in two dimensions.<br>
	 * This property is only important if the window is {@link #setResizeable(boolean) resizeable}, otherwise
	 * it can be ignored
	 * @param cornerSize the size in pixels, at least 0
	 */
	public void setCornerSize( int cornerSize );
	
	/**
	 * Sets the size of the area at the top that can be grabbed and used to move the window.<br>
	 * This property is only important if the window is {@link #setMoveable(boolean) moveable}, otherwise
	 * it can be ignored. 
	 * @param moveSize the size, at least 0
	 */
	public void setMoveSize( int moveSize );
	
	/**
	 * Informs this border whether the window can be moved by grabbing <code>this</code>.
	 * @param moveable whether the window can be moved
	 */
	public void setMoveable( boolean moveable );
	
	/**
	 * Informs this border whether the window can be resized by grabbing <code>this</code>.
	 * @param resizeable whether the window can be resized
	 */
	public void setResizeable( boolean resizeable );
}
