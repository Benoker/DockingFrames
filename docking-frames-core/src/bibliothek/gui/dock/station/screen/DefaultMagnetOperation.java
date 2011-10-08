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

import bibliothek.gui.dock.station.screen.MagnetRequest.Side;

/**
 * The {@link DefaultMagnetOperation} searches the nearest attracted {@link ScreenDockWindow} to a moved side
 * and if that other window is nearer than a given threshold then the moved window is attracted.
 * @author Benjamin Sigg
 */
public class DefaultMagnetOperation implements MagnetOperation{
	private int threshold;
	
	/** Tells which window is neighbor of which other window */
	private StickMagnetGraph graph;
	
	/**
	 * Creates a new operation
	 * @param threshold the maximum distance between two items in pixels allowing them to interact with each other
	 */
	public DefaultMagnetOperation( int threshold ){
		this.threshold = threshold;
	}
	
	public void attract( MagnetController controller, MagnetRequest request ){
		if( graph == null ){
			graph = new StickMagnetGraph( controller, request );
		}
		
		if( request.isMoved() ){
			moved( controller, request );
		}
		else if( request.isResized() ){
			resized( controller, request );
		}
	}
	
	public void destroy(){
		// nothing
	}
	
	/**
	 * Called if a window was moved.
	 * @param controller the caller
	 * @param request information about the window that was moved
	 */
	protected void moved( MagnetController controller, MagnetRequest request ){
		ScreenDockWindow[] partners = controller.getAttracted( request.getWindow() );
		
		int nearest = threshold;
		ScreenDockWindow nearestWindow = null;
		Side nearestSide = null;
		
		for( ScreenDockWindow partner : partners ){
			if( graph.getRoot().getNeighbor( partner ) == null ){
				for( Side side : Side.values() ){
					int distance = controller.distance( request.getWindow(), side, partner, side.opposite() );
					if( distance <= nearest ){
						boolean neighbors = false;
						switch( side ){
							case NORTH:
							case SOUTH:
								neighbors = controller.intersectVertically( request.getWindow(), partner );
								break;
							case EAST:
							case WEST:
								neighbors = controller.intersectHorizontally( request.getWindow(), partner );
								break;
						}
						if( neighbors ){
							nearest = distance;
							nearestWindow = partner;
							nearestSide = side;
						}
					}
				}
			}
		}
		
		if( nearestWindow != null ){
			request.movingAttraction( nearestWindow, nearestSide, nearestSide.opposite() );
			neighborMoved( controller, request, nearestWindow, nearestSide );
		}
	}
	
	private void neighborMoved( MagnetController controller, MagnetRequest request, ScreenDockWindow neighbor, Side side ){
		Side checkA;
		Side checkB;
		if( side == Side.NORTH || side == Side.SOUTH ){
			checkA = Side.EAST;
		}
		else{
			checkA = Side.NORTH;
		}
		checkB = checkA.opposite();
		
		int distA = controller.distance( neighbor, checkA, request.getWindow(), checkA );
		int distB = controller.distance( neighbor, checkB, request.getWindow(), checkB );
		
		if( distA <= distB && distA <= threshold ){
			request.movingAttraction( neighbor, checkA, checkA );
		}
		else if( distB <= distA && distB <= threshold ){
			request.movingAttraction( neighbor, checkB, checkB );
		}
	}
	
	/**
	 * Called if a window was resized.
	 * @param controller the caller
	 * @param request information about the window that was resized
	 */
	protected void resized( MagnetController controller, MagnetRequest request ){
		ScreenDockWindow[] partners = controller.getAttracted( request.getWindow() );
		
		ScreenDockWindow[] neighbors = new ScreenDockWindow[4];
		
		for( Side side : Side.values() ){
			int nearest = threshold+1;
			ScreenDockWindow window = null;
			
			for( ScreenDockWindow partner : partners ){
				if( graph.getRoot().getNeighbor( partner ) == null ){
					int distance = controller.distance( request.getWindow(), side, partner, side.opposite() );
					if( distance <= nearest || window == null ){
						boolean neighbor = false;
						switch( side ){
							case NORTH:
							case SOUTH:
								neighbor = controller.intersectVertically( request.getWindow(), partner );
								break;
							case EAST:
							case WEST:
								neighbor = controller.intersectHorizontally( request.getWindow(), partner );
								break;
						}
						if( neighbor ){
							nearest = distance;
							window = partner;
						}
					}
				}
			}
			
			if( window != null ){
				if( request.is( side ) && nearest <= threshold ){
					request.resizingAttraction( window, side, side.opposite() );
					neighbors[side.ordinal()] = window;
				}
				else if( nearest == 1 ){
					neighbors[side.ordinal()] = window;
				}
			}
		}
		
		for( Side side : Side.values() ){
			if( request.is( side )){
				neighborResized( controller, request, side, neighbors );
			}
		}
	}
	
	private void neighborResized( MagnetController controller, MagnetRequest request, Side side, ScreenDockWindow[] neighbors ){
		Side checkA = null;
		switch( side ){
			case NORTH:
			case SOUTH:
				checkA = Side.EAST;
				break;
			case EAST:
			case WEST:
				checkA = Side.NORTH;
				break;
		}
		Side checkB = checkA.opposite();
		
		int distanceA;
		if( neighbors[checkA.ordinal()] != null ){
			distanceA = controller.distance( request.getWindow(), side, neighbors[checkA.ordinal()], side );
		}
		else{
			distanceA = threshold+1;
		}
		
		int distanceB;
		if( neighbors[checkB.ordinal()] != null ){
			distanceB = controller.distance( request.getWindow(), side, neighbors[checkB.ordinal()], side );
		}
		else{
			distanceB = threshold+1;
		}
		
		if( distanceA <= distanceB && distanceA <= threshold ){
			request.resizingAttraction( neighbors[checkA.ordinal()], side, side );
		}
		else if( distanceB <= distanceA && distanceB <= threshold ){
			request.resizingAttraction( neighbors[checkB.ordinal()], side, side );
		}
	}
}
