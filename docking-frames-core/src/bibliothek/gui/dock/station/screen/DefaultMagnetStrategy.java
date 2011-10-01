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
 * The {@link DefaultMagnetStrategy} searches the nearest attracted {@link ScreenDockWindow} to a moved side
 * and if that other window is nearer than a given threashold then the moved window is attracted.
 * @author Benjamin Sigg
 */
public class DefaultMagnetStrategy implements MagnetStrategy{
	/** how many pixels two sides can be appart and still attract each other */
	private int threshold = 15;
	
	/**
	 * Sets how many pixels two sides can be appart and still attract each other.
	 * @param threshold the maximal distance, at least 1
	 */
	public void setThreshold( int threshold ){
		if( threshold < 1 ){
			throw new IllegalArgumentException( "the threshold must be at least 1" );
		}
		this.threshold = threshold;
	}
	
	/**
	 * Tells how many pixels two sides can be appart and still attract each other.
	 * @return the maximal distance, at least 1
	 */
	public int getThreshold(){
		return threshold;
	}
	
	public void install( MagnetController controller ){
		// ignore
	}

	public void uninstall( MagnetController controller ){
		// ignore
	}

	public void attract( MagnetController controller, MagnetRequest request ){
		if( request.isMoved() ){
			moved( controller, request );
		}
		else if( request.isResized() ){
			resized( controller, request );
		}
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
		
		if( nearestWindow != null ){
			request.movingAttraction( nearestWindow, nearestSide, nearestSide.opposite() );
		}
	}
	
	/**
	 * Called if a window was resized.
	 * @param controller the caller
	 * @param request information about the window that was resized
	 */
	protected void resized( MagnetController controller, MagnetRequest request ){
		
	}
}
