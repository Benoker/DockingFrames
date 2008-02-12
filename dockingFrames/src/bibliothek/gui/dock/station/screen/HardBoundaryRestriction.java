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

import java.awt.*;

/**
 * A restriction that ensures that each dialog is always visible, even when some
 * screens can't be used because of that.
 * @author Benjamin Sigg
 */
public class HardBoundaryRestriction implements BoundaryRestriction{
    public Rectangle check( ScreenDockDialog dialog ) {
        return check( dialog, dialog.getBounds() );
    }
    
    public Rectangle check( ScreenDockDialog dialog, Rectangle target ) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
    
        int x = target.x;
        int y = target.y;
        int width = target.width;
        int height = target.height;
        
        double fit = -1.0;
        GraphicsDevice best = null;
        
        for( GraphicsDevice screen : screens ){
            double check = measureBounds( dialog, x, y, width, height, screen );
            if( check > fit ){
                fit = check;
                best = screen;
            }
        }
        
        if( best == null )
            return null;
        else
            return boundsInDevice( dialog, x, y, width, height, best );
    }
    
    
    /**
     * Checks how good <code>dialog</code> fits into the screen <code>device</code>
     * @param dialog the dialog that is checked
     * @param x the desired x-coordinate
     * @param y the desired y-coordinate
     * @param width the desired width
     * @param height the desired height
     * @param device the targeted screen
     * @return a value between 0 and 1, where 0 means "does not fit" and
     * 1 means "perfect".
     */
    protected double measureBounds( ScreenDockDialog dialog, int x, int y, int width, int height, GraphicsDevice device ){
        if( width == 0 || height == 0 )
            return 0.0;
        
        GraphicsConfiguration config = dialog.getGraphicsConfiguration();
        if( config == null )
            return 0.0;
        
        Rectangle screen = config.getBounds();
        Rectangle next = new Rectangle( x+screen.x, y+screen.y, width, height );
        screen = device.getDefaultConfiguration().getBounds();
        
        Rectangle intersection = screen.intersection( next );
        
        if( intersection.width <= 0 || intersection.height <= 0 )
            return 0.0;
        
        return (intersection.width * intersection.height) / ((double)next.width * next.height);
    }
    
    /**
     * Calculates size and location of <code>dialog</code> such that it is
     * in <code>device</code>. 
     * @param dialog the dialog to check
     * @param x the desired x-coordinate
     * @param y the desired y-coordinate
     * @param width the desired width
     * @param height the desired height
     * @param device the screen in which to show this dialog
     * @return the new bounds, can be <code>null</code>
     */
    protected Rectangle boundsInDevice( ScreenDockDialog dialog, int x, int y, int width, int height, GraphicsDevice device ){
        GraphicsConfiguration config = dialog.getGraphicsConfiguration();
        if( config != null ){
            Rectangle size = config.getBounds();
            x += size.x;
            y += size.y;
            
            size = device.getDefaultConfiguration().getBounds();
            
            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets( device.getDefaultConfiguration() );
            if( insets == null )
                insets = new Insets( 0,0,0,0 );
            
            width = Math.min( size.width-insets.left-insets.right, width );
            height = Math.min( size.height-insets.top-insets.bottom, height );
            
            x = Math.max( x, size.x+insets.left );
            y = Math.max( y, size.y+insets.right );
            
            x = Math.min( x, size.width - insets.left - insets.right - width + size.x );
            y = Math.min( y, size.height - insets.top - insets.bottom - height + size.y );
        }
        
        return new Rectangle( x, y, width, height );
    }
    
}
