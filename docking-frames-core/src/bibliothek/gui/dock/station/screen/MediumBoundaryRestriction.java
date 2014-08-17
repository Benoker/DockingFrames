/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * A restriction that ensures that the title of each dialog is always visible.
 * @author Benjamin Sigg
 */
public class MediumBoundaryRestriction extends AbstractBoundaryRestriction{
	@Override
	public Rectangle checkSize( ScreenDockWindow window ){
		Point center = window.getTitleCenter();
		if( center == null ){
			Rectangle target = window.getWindowBounds();
			center = new Point( target.width/2, target.height/2 );
		}
		return validate( window, window.getWindowBounds(), center, center );
	}

	@Override
	public Rectangle checkSize( ScreenDockWindow window, Rectangle target ){
		Point center = window.getTitleCenter();
		Point search;
		
		if( center == null ){
			center = new Point( target.width/2,  target.height/2 );
			search = center;
		}
		else{
			Rectangle bounds = window.getWindowBounds();
			search = new Point( center );
			search.x += target.x - bounds.x;
			search.y += target.y - bounds.y;
		}
		return validate( window, target, center, search );
	}
	
	/**
	 * Ensures that <code>center</code> will be in a visible part of the screen.
	 * @param window the window whose boundaries are checked
	 * @param target the next boundaries for a window
	 * @param center the point that should remain visible
	 * @param search the point used to find the best matching screen device
	 * @return a set of boundaries that is as close to <code>target</code> as possible
	 */
	protected Rectangle validate( ScreenDockWindow window, Rectangle target, Point center, Point search ) {
		Rectangle screen = findDevice( search.x + target.x, search.y + target.y );
		if( screen == null ){
			return null;
		}

		Rectangle original;
		if( center.equals( search )){
			original = screen;
		}
		else {
			original = findDevice( center.x + target.x, center.y + target.y );
		}
		
		if( !original.equals( screen )){
			center = search;
		}
		
		Rectangle result = new Rectangle( target );

		Dimension minimum = getMinimumSize( window );
		
		result.width = Math.max( minimum.width, result.width );
		result.height = Math.max( minimum.height, result.height );
		
		result.x = Math.max( screen.x - center.x, result.x );
		result.y = Math.max( screen.y - center.y, result.y );
		
		result.x = Math.min( screen.x + screen.width - center.x, result.x );
		result.y = Math.min( screen.y + screen.height - center.y, result.y );

		return result;
    }
	
	/**
	 * Gets the minimum size of <code>window</code>, the default implementation just calls
	 * {@link ScreenDockWindow#getMinimumWindowSize()}, but subclasses may override this method
	 * to use another algorithm for finding the minimum window size.
	 * @param window the window whose minimum size is required
	 * @return the minimum size, must not be <code>null</code>
	 */
	protected Dimension getMinimumSize( ScreenDockWindow window ){
		return window.getMinimumWindowSize();
	}
	
	/**
	 * Finds and returns the boundaries of the screen in which <code>x/y</code> are.
	 * @param x some x coordinate
	 * @param y some y coordinate
	 * @return the boundaries of the nearest screen containing <code>x/y</code> or <code>null</code>
	 */
	protected Rectangle findDevice( int x, int y ){
		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		
		GraphicsDevice best = null;
		int bestDist = 0;
		
		for( GraphicsDevice device : devices ){
			Rectangle bounds = device.getDefaultConfiguration().getBounds();
			int dist = dist( bounds.x, bounds.width, x ) + dist( bounds.y, bounds.height, y );
			if( best == null || dist < bestDist ){
				best = device;
				bestDist = dist;
			}
		}
		
		if( best == null ){
			return null;
		}
		return best.getDefaultConfiguration().getBounds();
	}
	

	private int dist( int x, int width, int pos ){
		if( pos < x ){
			return x - pos;
		}
		if( pos > x + width ){
			return pos - x - width;
		}
		return 0;
	}
}
