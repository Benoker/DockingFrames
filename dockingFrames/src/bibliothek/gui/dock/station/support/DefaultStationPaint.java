/**
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

package bibliothek.gui.dock.station.support;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.SystemColor;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.station.StationPaint;

/**
 * A simple implementation of {@link StationPaint}. This paint uses
 * one color to draw various elements.
 * @author Benjamin Sigg
 *
 */
public class DefaultStationPaint implements StationPaint {
    private Color color = SystemColor.textHighlight;
    
    /**
     * Gets the color that is used in this paint.
     * @return the color
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Sets the color which is used in this paint.
     * @param color the color
     */
    public void setColor( Color color ) {
        if( color == null )
            throw new IllegalArgumentException( "Color must not be null" );
        this.color = color;
    }
    
    public void drawDivider( Graphics g, DockStation station, Rectangle bounds ) {
        g.setColor( color );
        g.fillRect( bounds.x, bounds.y, bounds.width, bounds.height );
    }
    
    public void drawInsertion( Graphics g, DockStation station, Rectangle stationBounds, Rectangle dockableBounds ) {
        Color color = new Color( this.color.getRGB() );
        
        g.setColor( color );
        Graphics2D g2 = (Graphics2D)g;
        
        Composite old = g2.getComposite();
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.33f ));
        
        //g2.fillRect( stationBounds.x, stationBounds.y, stationBounds.width, stationBounds.height );
        g2.fillRect( dockableBounds.x, dockableBounds.y, dockableBounds.width, dockableBounds.height );
        
        int x = dockableBounds.x-1;
        int y = dockableBounds.y-1;
        int w = dockableBounds.width+2;
        int h = dockableBounds.height+2;
        
        g2.setComposite( old );
        
        drawInsertionLine( g, station, x, y, x+w, y );
        drawInsertionLine( g, station, x, y, x, y+h );
        drawInsertionLine( g, station, x+w, y+h, x, y+h );
        drawInsertionLine( g, station, x+w, y+h, x+w, y );
    }
    
    public void drawInsertionLine( Graphics g, DockStation station, int x1,
            int x2, int y1, int y2 ) {
        
        g.setColor( color );
        Graphics2D g2 = (Graphics2D)g;
        
        Stroke old = g2.getStroke();
        g2.setStroke( new BasicStroke( 3f ));
        g2.drawLine( x1, x2, y1, y2 );
        g2.setStroke( old );
    }
}
