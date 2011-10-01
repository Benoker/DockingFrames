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
package bibliothek.gui.dock.station.screen;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.AttractorStrategy.Attraction;
import bibliothek.gui.dock.station.screen.MagnetRequest.Side;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.SilentPropertyValue;
import bibliothek.util.FrameworkOnly;

/**
 * Each {@link ScreenDockStation} uses one {@link MagnetController} to calculate attractions
 * between its children. The {@link MagnetController} makes use of a {@link MagnetStrategy}
 * and of several {@link AttractorStrategy}s to modify the location and size of the currently
 * moved {@link ScreenDockWindow}.<br>
 * {@link ScreenDockWindow}s have to call {@link #start(ScreenDockWindow)} when they start
 * moving or resizing.
 * @author Benjamin Sigg
 */
public class MagnetController {
	/** the owner of this controller */
	private ScreenDockStation station;
	
	/** the currently executed operation */
	private Operation current;
	
	/** the current strategy to calculate the new boundaries */
	private PropertyValue<MagnetStrategy> strategy = new PropertyValue<MagnetStrategy>( ScreenDockStation.MAGNET_STRATEGY ){
		@Override
		protected void valueChanged( MagnetStrategy oldValue, MagnetStrategy newValue ){
			if( oldValue != null ){
				oldValue.uninstall( MagnetController.this );
			}
			if( newValue != null ){
				newValue.install( MagnetController.this );
			}
		}
	};

	/** the currently used {@link AttractorStrategy} */
	private PropertyValue<AttractorStrategy> attraction = new SilentPropertyValue<AttractorStrategy>( ScreenDockStation.ATTRACTOR_STRATEGY );
	
	/** the currently used {@link DockController} */
	private DockController controller;
	
	/**
	 * Creates a new {@link MagnetController}.
	 * @param station the station using this controller, not <code>null</code>
	 */
	public MagnetController( ScreenDockStation station ){
		if( station == null ){
			throw new IllegalArgumentException( "station must not be null" );
		}
		this.station = station;
	}
	
	/**
	 * Sets the {@link DockController} which is to be used by this {@link MagnetController}.
	 * @param controller the controller to use or <code>null</code>
	 */
	@FrameworkOnly
	public void setController( DockController controller ){
		if( this.controller != controller ){
			
			this.controller = controller;
			strategy.setProperties( controller );
			attraction.setProperties( controller );
		}
	}
	
	/**
	 * Gets the {@link DockController} that is currently used by this {@link MagnetController}.
	 * @return the controller, can be <code>null</code>
	 */
	public DockController getController(){
		return controller;
	}
	
	/**
	 * Gets the {@link ScreenDockStation} which is using this {@link MagnetController}.
	 * @return the owner of this controller, never <code>null</code>
	 */
	public ScreenDockStation getStation(){
		return station;
	}
	
	/**
	 * Starts a move or resize operation that involves <code>window</code>. Only
	 * one operation can be running at the same time.
	 * @param window the window which is moved or resized
	 * @return a callback that is to be informed whenever <code>window</code> further
	 * changes position or size
	 */
	public MagnetizedOperation start( ScreenDockWindow window ){
		if( current != null ){
			current.stop();
		}
		current = new Operation( window );
		return current;
	}
	
	/**
	 * Tells whether <code>fixed</code> and <code>moved</code> attract each other. 
	 * @param fixed the dockable that has not moved
	 * @param moved the dockable that has moved
	 * @return the attraction, the strongest result from all currently registered {@link AttractorStrategy}s
	 */
	public Attraction getAttraction( Dockable fixed, Dockable moved ){
		AttractorStrategy strategy = attraction.getValue();
		if( strategy == null ){
			return Attraction.NEUTRAL;
		}
		else{
			return strategy.attract( station, fixed, moved );
		}
	}
	
	/**
	 * Gets the window that is currently moved.
	 * @return the currently reshaped window, may be <code>null</code>
	 */
	public ScreenDockWindow getCurrent(){
		if( current == null ){
			return null;
		}
		return current.getWindow();
	}
	
	/**
	 * Gets an array containing all the {@link ScreenDockWindow}s that are currently shown by the {@link #getStation() station}.
	 * @return all the windows
	 */
	public ScreenDockWindow[] getWindows(){
		int count = station.getDockableCount();
		ScreenDockWindow[] windows = new ScreenDockWindow[ count ];
		for( int i = 0; i < count; i++ ){
			windows[i] = station.getWindow( i );
		}
		return windows;
	}
	
	/**
	 * Gets all the {@link ScreenDockWindow}s of the {@link #getStation() station} that are attracted to <code>window</code>.
	 * @param window the window that has moved and whose partners are searched
	 * @return all the partner windows, may be empty, is never <code>null</code>, does not contain <code>window</code>
	 */
	public ScreenDockWindow[] getAttracted( ScreenDockWindow window ){
		List<ScreenDockWindow> result = new ArrayList<ScreenDockWindow>();
		int count = station.getDockableCount();
		for( int i = 0; i < count; i++ ){
			ScreenDockWindow next = station.getWindow( i );
			if( next != window ){
				Attraction attraction = getAttraction( next.getDockable(), window.getDockable() );
				switch( attraction ){
					case STRONGLY_ATTRACTED:
					case ATTRACTED:
						result.add( next );
						break;
				}
			}
		}
		return result.toArray( new ScreenDockWindow[ result.size() ] );
	}
	
	/**
	 * Calculates the distance between <code>sideA</code> of <code>windowA</code> to <code>sideB</code> of <code>windowB</code>.
	 * If either window is the {@link #getCurrent() current} window, then its {@link MagnetRequest#getBounds() base boundaries}
	 * are used instead of its current boundaires.
	 * @param windowA the first window
	 * @param sideA the side of the window to check
	 * @param windowB the second window
	 * @param sideB the side of the second window to check
	 * @return the horizontall or vertical distance between the two sides, always a number greater or equal to 0
	 * @throws IllegalArgumentException if <code>sideA</code> and <code>sideB</code> are neither equal nor opposite
	 */
	public int distance( ScreenDockWindow windowA, MagnetRequest.Side sideA, ScreenDockWindow windowB, MagnetRequest.Side sideB ){
		if( sideA != sideB ){
			if( (sideA == Side.NORTH || sideA == Side.SOUTH) != (sideB == Side.NORTH || sideB == Side.SOUTH) ){
				throw new IllegalArgumentException( "sideA and sideB are neither equal nor opposite: " + sideA + ", " + sideB );
			}
		}
		
		int valueA = getValue( windowA, sideA );
		int valueB = getValue( windowB, sideB );
		
		return Math.abs( valueA - valueB );
	}
	
	/**
	 * Tells whether the <code>y</code> coordinate and the <code>height</code> of <code>windowA</code> and <code>windowB</code>
	 * are such that they have at least one pixel at the same height. 
	 * @param windowA the first window
	 * @param windowB the second window
	 * @return <code>true</code> if both windows have at least one pixel on the same height
	 */
	public boolean intersectHorizontally( ScreenDockWindow windowA, ScreenDockWindow windowB ){
		int yA1 = getValue( windowA, Side.NORTH );
		int yA2 = getValue( windowA, Side.SOUTH );
		
		int yB1 = getValue( windowB, Side.NORTH );
		int yB2 = getValue( windowB, Side.SOUTH );
		
		return between( yA1, yA2, yB1 ) || between( yA1, yA2, yB2 ) || between( yB1, yB2, yA1 ) || between( yB1, yB2, yA2 );
	}
	
	/**
	 * Tells whether the <code>x</code> coordinate and the <code>width</code> of <code>windowA</code> and <code>windowB</code>
	 * are such that they have at least one pixel at the same width. 
	 * @param windowA the first window
	 * @param windowB the second window
	 * @return <code>true</code> if both windows have at least one pixel on the same width
	 */
	public boolean intersectVertically( ScreenDockWindow windowA, ScreenDockWindow windowB ){
		int xA1 = getValue( windowA, Side.WEST );
		int xA2 = getValue( windowA, Side.EAST );
		
		int xB1 = getValue( windowB, Side.WEST );
		int xB2 = getValue( windowB, Side.EAST );
		
		return between( xA1, xA2, xB1 ) || between( xA1, xA2, xB2 ) || between( xB1, xB2, xA1 ) || between( xB1, xB2, xA2 );
	}
	
	private boolean between( int x1, int x2, int point ){
		return x1 <= point && point <= x2;
	}
	
	/**
	 * Gets the location of the side <code>side</code> of <code>window</code>. If <code>window</code> is the
	 * {@link #getCurrent() current window}, then its {@link MagnetRequest#getBounds() base boundaries} are used
	 * to calculate the coordinates, otherwise {@link ScreenDockWindow#getWindowBounds()} is used.  
	 * @param window some window
	 * @param side the side to read
	 * @return the x or y coordinate of <code>side</code>
	 */
	public int getValue( ScreenDockWindow window, Side side ){
		if( getCurrent() == window ){
			return getValue( current.getBounds(), side );
		}
		else{
			return getValue( window.getWindowBounds(), side );
		}
	}
	
	/**
	 * Gets the location of the side <code>side</code> of <code>rectangle</code>. 
	 * @param rectangle some rectangle
	 * @param side the side to read
	 * @return the x or y coordinate of <code>side</code>
	 */
	public int getValue( Rectangle rectangle, Side side ){
		switch( side ){
			case NORTH:
				return rectangle.y;
			case SOUTH:
				return rectangle.y + rectangle.height;
			case WEST:
				return rectangle.x;
			case EAST:
				return rectangle.x + rectangle.width;
			default:
				throw new IllegalStateException( "unknown side: " + side );
		}
	}
	
	/**
	 * Gets the {@link MagnetStrategy} that is currently used by this controller.
	 * @return the current strategy, <code>null</code> if no {@link DockController} is set
	 */
	public MagnetStrategy getStrategy(){
		return strategy.getValue();
	}
	
	/**
	 * Sets the {@link MagnetStrategy} that is to be used by this controller.
	 * @param strategy the strategy, a value of <code>null</code> reinstalles the default strategy
	 */
	public void setStrategy( MagnetStrategy strategy ){
		this.strategy.setValue( strategy );
	}
	
	/**
	 * Gets the currently used {@link AttractorStrategy}.
	 * @return the strategy defining which two {@link Dockable}s attract each other
	 */
	public AttractorStrategy getAttractorStrategy(){
		return attraction.getValue();
	}
	
	/**
	 * Sets the {@link AttractorStrategy} to use.
	 * @param strategy the strategy, a value of <code>null</code> reinstalles the default strategy
	 */
	public void setAttractorStrategy( AttractorStrategy strategy ){
		this.attraction.setValue( strategy );
	}
	
	/**
	 * Describes the reshaping of a window both for the {@link ScreenDockWindow} interface and for
	 * the {@link MagnetStrategy}.
	 * @author Benjamin Sigg
	 */
	private class Operation implements MagnetizedOperation, ScreenDockWindowListener, MagnetRequest{
		/** the window that is reshaped */
		private ScreenDockWindow window;
		
		/** the boundaries {@link #window} had before the operation started */
		private Rectangle initialBoundaries;
		
		/** the unmodified boundaries */
		private Rectangle baseBoundaries;
		
		/** the boundaries {@link #window} will have after this {@link MagnetRequest} has been executed */
		private Rectangle resultBoundaries;
		
		/**
		 * Creates a new operation.
		 * @param window the window that is reshaped
		 */
		public Operation( ScreenDockWindow window ){
			this.window = window;
			initialBoundaries = new Rectangle( window.getWindowBounds() );
			window.addScreenDockWindowListener( this );
		}
		
		public ScreenDockWindow getWindow(){
			return window;
		}

		public Rectangle getBounds(){
			return baseBoundaries;
		}
		
		public boolean isMoved(){
			return (isNorth() && isSouth()) || (isEast() && isWest());
		}

		public boolean isResized(){
			return !isMoved() && !initialBoundaries.equals( baseBoundaries );
		}

		public boolean isNorth(){
			return initialBoundaries.y != baseBoundaries.y;
		}

		public boolean isSouth(){
			return initialBoundaries.y + initialBoundaries.height != baseBoundaries.y + baseBoundaries.height;
		}

		public boolean isEast(){
			return initialBoundaries.x != baseBoundaries.x;
		}

		public boolean isWest(){
			return initialBoundaries.x + initialBoundaries.width != baseBoundaries.y + baseBoundaries.width;
		}

		public void resizingAttraction( ScreenDockWindow neighbor, Side windowSide, Side neighborSide ){
			checkArguments( neighbor, windowSide, neighborSide );
			
			int neighborValue = getValue( neighbor.getWindowBounds(), windowSide );
			int windowValue = getValue( resultBoundaries, neighborSide );
			
			int delta = neighborValue - windowValue;
			
			switch( windowSide ){
				case NORTH:
					resultBoundaries.y += delta;
					resultBoundaries.height -= delta;
					break;
				case SOUTH:
					resultBoundaries.height += delta;
					break;
				case EAST:
					resultBoundaries.x += delta;
					resultBoundaries.width -= delta;
					break;
				case WEST:
					resultBoundaries.width += delta;
			}
		}

		public void movingAttraction( ScreenDockWindow neighbor, Side windowSide, Side neighborSide ){
			checkArguments( neighbor, windowSide, neighborSide );

			int neighborValue = getValue( neighbor.getWindowBounds(), neighborSide );
			int windowValue = getValue( resultBoundaries, windowSide );
			
			int delta = neighborValue - windowValue;
			switch( windowSide ){
				case NORTH:
				case SOUTH:
					resultBoundaries.y += delta;
					break;
				case EAST:
				case WEST:
					resultBoundaries.x += delta;
			}
		}
		
		private void checkArguments( ScreenDockWindow neighbor, Side windowSide, Side neighborSide ){
			if( neighbor == null ){
				throw new IllegalArgumentException( "neighbor is null" );
			}
			if( neighbor == getWindow() ){
				throw new IllegalArgumentException( "neighbor is identical to window" );
			}
			if( windowSide == null ){
				throw new IllegalArgumentException( "windowSide is null" );
			}
			if( neighborSide == null ){
				throw new IllegalArgumentException( "neighborSide is null" );
			}
			if( windowSide != neighborSide ){
				switch( windowSide ){
					case EAST:
						if( neighborSide != Side.WEST ){
							throw new IllegalArgumentException( "windowSide and neighborSide not the same and not opposing: " + windowSide + " vs. " + neighborSide );
						}
						break;
					case WEST:
						if( neighborSide != Side.EAST ){
							throw new IllegalArgumentException( "windowSide and neighborSide not the same and not opposing: " + windowSide + " vs. " + neighborSide );
						}
						break;
					case SOUTH:
						if( neighborSide != Side.NORTH ){
							throw new IllegalArgumentException( "windowSide and neighborSide not the same and not opposing: " + windowSide + " vs. " + neighborSide );
						}
						break;
					case NORTH:
						if( neighborSide != Side.SOUTH ){
							throw new IllegalArgumentException( "windowSide and neighborSide not the same and not opposing: " + windowSide + " vs. " + neighborSide );
						}
						break;
				}
			}
		}

		public void directAttraction( Rectangle bounds ){
			resultBoundaries = new Rectangle( bounds );
		}

		public void visibilityChanged( ScreenDockWindow window ){
			// ignore
		}

		public void fullscreenStateChanged( ScreenDockWindow window ){
			// ignore
		}

		public void shapeChanged( ScreenDockWindow window ){
			// TODO Auto-generated method stub
			
		}

		public Rectangle attract( Rectangle bounds ){
			baseBoundaries = new Rectangle( bounds );
			resultBoundaries = new Rectangle( bounds );
			
			MagnetStrategy strategy = getStrategy();
			if( strategy != null ){
				strategy.attract( MagnetController.this, this );
			}
			
			return resultBoundaries;
		}

		public void stop(){
			window.removeScreenDockWindowListener( this );
			if( current == this ){
				current = null;
			}
		}
	}
}
