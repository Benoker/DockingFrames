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
package bibliothek.gui.dock.station.screen.magnet;

import java.awt.Rectangle;

import bibliothek.gui.dock.station.screen.BoundaryRestriction;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;

/**
 * Describes the movement of a {@link ScreenDockWindow}. Also offers
 * methods to define attractions.
 * @author Benjamin Sigg
 */
public interface MagnetRequest {
	/**
	 * Describes one side of a {@link ScreenDockWindow}.
	 * @author Benjamin Sigg
	 */
	public static enum Side{
		/** the top side */
		NORTH,
		/** the bottom side */
		SOUTH, 
		/** the right side */
		EAST,
		/** the left side */
		WEST;
		
		/**
		 * Gets the side that is opposite to <code>this</code>.
		 * @return the opposite side, never <code>null</code>
		 */
		public Side opposite(){
			switch( this ){
				case NORTH: return SOUTH;
				case SOUTH: return NORTH;
				case EAST: return WEST;
				case WEST: return EAST;
				default: throw new IllegalStateException( "unknown side: " + this );
			}
		}
	}
	
	/**
	 * Gets the window that has changed.
	 * @return the modified window
	 */
	public ScreenDockWindow getWindow();
	
	/**
	 * Gets the boundaries the {@link #getWindow() window} would have if there would
	 * be no outside influences (like magnetism) to the boundaries.
	 * @return the unmodified boundaries, not <code>null</code>
	 * @see #getResultBounds()
	 */
	public Rectangle getBounds();
	
	/**
	 * Gets the boundaries the {@link #getWindow() window} would actually have if 
	 * the operation would end right now.
	 * @return the modified boundaries, not <code>null</code>
	 * @see #getBounds()
	 */
	public Rectangle getResultBounds();
	
	/**
	 * Gets the boundaries of <code>window</code> when this request was created.
	 * @param window some window 
	 * @return the initial boundaries of <code>window</code>
	 * @throws IllegalArgumentException if <code>window</code> is not known to this request
	 */
	public Rectangle getInitialBounds( ScreenDockWindow window );
	
	/**
	 * Tells whether the entire {@link #getWindow() window} was moved. A window
	 * either is moved, resized or nothing happened.
	 * @return <code>true</code> if the entire window was moved
	 * @see #isResized()
	 */
	public boolean isMoved();
	
	/**
	 * Tells whether the {@link #getWindow() window} changed its size. A window
	 * either is moved, resized or nothing happened.
	 * @return <code>true</code> if the window was resized
	 * @see #isMoved()
	 */
	public boolean isResized();
	
	/**
	 * Tells whether the location of the north side of the {@link #getWindow() window}
	 * changed. This may be due to a movement of the entire window or because the window
	 * was resized.
	 * @return <code>true</code> if the north side changed its position
	 */
	public boolean isNorth();

	/**
	 * Tells whether the location of the south side of the {@link #getWindow() window}
	 * changed. This may be due to a movement of the entire window or because the window
	 * was resized.
	 * @return <code>true</code> if the south side changed its position
	 */
	public boolean isSouth();
	
	/**
	 * Tells whether the location of the east side of the {@link #getWindow() window}
	 * changed. This may be due to a movement of the entire window or because the window
	 * was resized.
	 * @return <code>true</code> if the east side changed its position
	 */
	public boolean isEast();
	
	/**
	 * Tells whether the location of the west side of the {@link #getWindow() window}
	 * changed. This may be due to a movement of the entire window or because the window
	 * was resized.
	 * @return <code>true</code> if the west side changed its position
	 */
	public boolean isWest();
	
	/**
	 * Calls {@link #isWest()}, {@link #isEast()}, {@link #isNorth()} or {@link #isSouth()} depending
	 * on <code>side</code>.
	 * @param side the side to ask whether it changed
	 * @return <code>true</code> if the side changed its position
	 */
	public boolean is( Side side );
	
	/**
	 * Resizes the {@link #getWindow() window} such that its side <code>windowSide</code> is at
	 * the same location as the side <code>neighborSide</code> from <code>neighbor</code>. 
	 * @param neighbor the window to which {@link #getWindow() window} is attracted
	 * @param windowSide the side of the {@link #getWindow() window} which has to be moved
	 * @param neighborSide the side of <code>neighbor</code> which has to be matched
	 * @throws IllegalArgumentException if <code>neighbor</code> is <code>null</code> or
	 * is the {@link #getWindow() window} itself, if either <code>windowSide</code> or
	 * <code>neighborSide</code> is <code>null</code>, if <code>windowSide</code> and
	 * <code>neighborSide</code> are not the same and not the opposite (e.g. north and west)
	 */
	public void resizingAttraction( ScreenDockWindow neighbor, Side windowSide, Side neighborSide );
	
	/**
	 * Moves the {@link #getWindow() window} such that its side <code>windowSide</code> is at
	 * the same location as the side <code>neighborSide</code> from <code>neighbor</code>. 
	 * @param neighbor the window to which {@link #getWindow() window} is attracted
	 * @param windowSide the side of the {@link #getWindow() window} which has to be moved
	 * @param neighborSide the side of <code>neighbor</code> which has to be matched
	 * @throws IllegalArgumentException if <code>neighbor</code> is <code>null</code> or
	 * is the {@link #getWindow() window} itself, if either <code>windowSide</code> or
	 * <code>neighborSide</code> is <code>null</code>, if <code>windowSide</code> and
	 * <code>neighborSide</code> are not the same and not the opposite (e.g. north and west)
	 */
	public void movingAttraction( ScreenDockWindow neighbor, Side windowSide, Side neighborSide );
	
	/**
	 * Directly changes the size and location of the {@link #getWindow() window} to match
	 * <code>bounds</code>.<br>
	 * Note that the current {@link BoundaryRestriction} may modify <code>bounds</code> 
	 * @param bounds the new boundaries to use
	 */
	public void directAttraction( Rectangle bounds );
}
