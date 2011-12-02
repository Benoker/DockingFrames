/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;

/**
 * A restriction that ensures that each dialog is always visible on exactly one
 * screen.
 * @author Benjamin Sigg
 */
public class HardBoundaryRestriction extends AbstractBoundaryRestriction{
	@Override
	protected Rectangle checkSize( ScreenDockWindow window ){
		return checkSize( window, window.getWindowBounds() );
	}
	
	@Override
    protected Rectangle checkSize( ScreenDockWindow window, Rectangle target ){
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
    
        int x = target.x;
        int y = target.y;
        int width = target.width;
        int height = target.height;
        
        double fit = -1.0;
        GraphicsDevice best = null;
        
        for( GraphicsDevice screen : screens ){
            double check = measureBounds( window, x, y, width, height, screen );
            if( check > fit ){
                fit = check;
                best = screen;
            }
        }
        
        if( best == null )
            return null;
        else
            return boundsInDevice( window, x, y, width, height, best );
    }
    
    
    /**
     * Checks how good <code>window</code> fits into the screen <code>device</code>
     * @param window the window that is checked
     * @param x the desired x-coordinate
     * @param y the desired y-coordinate
     * @param width the desired width
     * @param height the desired height
     * @param device the targeted screen
     * @return a value between 0 and 1, where 0 means "does not fit" and
     * 1 means "perfect".
     */
    protected double measureBounds( ScreenDockWindow window, int x, int y, int width, int height, GraphicsDevice device ){
        if( width == 0 || height == 0 )
            return 0.0;
        
        Rectangle next = new Rectangle( x, y, width, height );
        Rectangle screen = device.getDefaultConfiguration().getBounds();
        
        Rectangle intersection = screen.intersection( next );
        
        if( intersection.width <= 0 || intersection.height <= 0 )
            return 0.0;
        
        return (intersection.width * intersection.height) / ((double)next.width * next.height);
    }
    
    /**
     * Calculates size and location of <code>dialog</code> such that it is
     * in <code>device</code>. 
     * @param window the window to check
     * @param x the desired x-coordinate
     * @param y the desired y-coordinate
     * @param width the desired width
     * @param height the desired height
     * @param device the screen in which to show this dialog
     * @return the new bounds, can be <code>null</code>
     */
    protected Rectangle boundsInDevice( ScreenDockWindow window, int x, int y, int width, int height, GraphicsDevice device ){
        Rectangle size = device.getDefaultConfiguration().getBounds();
            
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets( device.getDefaultConfiguration() );
        if( insets == null )
            insets = new Insets( 0,0,0,0 );
        
        width = Math.min( size.width-insets.left-insets.right, width );
        height = Math.min( size.height-insets.top-insets.bottom, height );
        
        x = Math.max( x, size.x+insets.left );
        y = Math.max( y, size.y+insets.right );
        
        x = Math.min( x, size.width - insets.left - insets.right - width + size.x );
        y = Math.min( y, size.height - insets.top - insets.bottom - height + size.y );
        
        return new Rectangle( x, y, width, height );
    }
    
}
