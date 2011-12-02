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

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

/**
 * This abstract class offers methods to ensure a reasonable maximum size for {@link ScreenDockWindow}s.
 * @author Benjamin Sigg
 */
public abstract class AbstractBoundaryRestriction implements BoundaryRestriction{
	public Rectangle check( ScreenDockWindow window ){
		Rectangle target = checkSize( window );
		return validate( window, target );
	}

	public Rectangle check( ScreenDockWindow window, Rectangle target ){
		target = checkSize( window, target );
		return validate( window, target );
	}
	
	/**
	 * Checks the size and location of <code>window</code>
	 * @param window the window whose boundaries are checked
	 * @return the new boundaries or <code>null</code> if the current boundaries of <code>window</code> are valid
	 */
	protected abstract Rectangle checkSize( ScreenDockWindow window );

	/**
	 * Checks the size and location of <code>window</code> assuming that <code>window</code> will 
	 * be give the boundaries <code>target</code>.
	 * @param window the window whose boundaries are checked
	 * @param target the boundaries that are requested
	 * @return the boundaries to use or <code>null</code> if <code>target</code> is valid
	 */
	protected abstract Rectangle checkSize( ScreenDockWindow window, Rectangle target );

	private Rectangle validate( ScreenDockWindow window, Rectangle target ){
		Dimension max = getMaximumSize( window );
		
		if( target == null ){
			target = window.getWindowBounds();
		}
		
		return new Rectangle( target.x, target.y, Math.min( max.width, target.width ), Math.min( max.height, target.height ) );
	}
	
	/**
	 * Finds the maximum size that <code>window</code> is allowed to have.
	 * @param window the window whose maximum size is searched
	 * @return the maximum size
	 */
	protected Dimension getMaximumSize( ScreenDockWindow window ){
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		int width = 0;
		int height = 0;
		
        for( GraphicsDevice sc : ge.getScreenDevices() ){
        	Rectangle bounds = sc.getDefaultConfiguration().getBounds();
        	width = Math.max( width, bounds.width );
        	height = Math.max( height, bounds.height );
        }
        
        return new Dimension( width+5, height+5 );
	}
}
