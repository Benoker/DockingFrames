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

package bibliothek.gui.dock.themes.basic;

import java.awt.*;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.themes.color.StationPaintColor;
import bibliothek.gui.dock.util.color.ColorCodes;
import bibliothek.gui.dock.util.color.ColorManager;

/**
 * A simple implementation of {@link StationPaint}. This paint uses
 * one color to draw various elements.
 * @author Benjamin Sigg
 *
 */
@ColorCodes({ "paint.line", "paint.divider", "paint.insertion" })
public class BasicStationPaint implements StationPaint {
    private StationPaintColor color = new StationPaintColor( "", StationPaintColor.class, this, SystemColor.textHighlight ){
        @Override
        protected void changed( Color oldColor, Color newColor ) {
            // ignore
        }
    };
    
    /**
     * Gets the color that is used in this paint.
     * @return the color
     */
    public Color getColor() {
        return color.color();
    }
    
    /**
     * Sets the color which is used in this paint.
     * @param color the color or <code>null</code> to use the default-color
     * @deprecated better use the {@link ColorManager} with keys
     * <code>paint.line</code>, <code>paint.divider</code>, <code>paint.insertion</code>
     */
    public void setColor( Color color ) {
        this.color.setValue( color );
    }
    
    public void drawDivider( Graphics g, DockStation station, Rectangle bounds ) {
        color.setId( "paint.divider" );
        color.connect( station.getController() );
        
        g.setColor( color.color() );
        g.fillRect( bounds.x, bounds.y, bounds.width, bounds.height );
        
        color.connect( null );
    }
    
    public void drawInsertion( Graphics g, DockStation station, Rectangle stationBounds, Rectangle dockableBounds ) {
        color.setId( "paint.insertion" );
        color.connect( station.getController() );
        
        g.setColor( color.color() );
        Graphics2D g2 = (Graphics2D)g;
        
        Composite old = g2.getComposite();
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.33f ));
        
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
        
        color.connect( null );
    }
    
    public void drawInsertionLine( Graphics g, DockStation station, int x1,
            int x2, int y1, int y2 ) {
        
        color.setId( "paint.line" );
        color.connect( station.getController() );
        
        g.setColor( color.color() );
        Graphics2D g2 = (Graphics2D)g;
        
        Stroke old = g2.getStroke();
        g2.setStroke( new BasicStroke( 3f ));
        g2.drawLine( x1, x2, y1, y2 );
        g2.setStroke( old );
        
        color.connect( null );
    }
}
