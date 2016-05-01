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
package bibliothek.extension.gui.dock.theme.bubble;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.DockStation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.themes.color.StationPaintColor;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * A {@link StationPaint} for a {@link BubbleTheme}
 * @author Benjamin Sigg
 *
 */
@ColorCodes({ "paint.divider", "paint.insertion", "paint.line" })
public class BubbleStationPaint implements StationPaint {
    private StationPaintColor color = new StationPaintColor( "paint", this, Color.RED ){
        @Override
        protected void changed( Color oldColor, Color newColor ) {
            // ignore
        }
    };
    
    public void drawDivider( Graphics g, DockStation station, Rectangle bounds ) {
        color.setId( "paint.divider" );
        color.connect( station.getController() );
        
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setColor( color.value() );
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_ATOP, 0.4f ) );
        
        g2.fillRect( bounds.x, bounds.y, bounds.width, bounds.height );
        
        g2.dispose();
        color.connect( null );
    }

    public void drawInsertion( Graphics g, DockStation station, Rectangle stationBounds, Rectangle dockableBounds ) {
        color.setId( "paint.insertion" );
        color.connect( station.getController() );
        
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setColor( color.value() );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setStroke( new BasicStroke( 3f ));
        
        int x = dockableBounds.x-1;
        int y = dockableBounds.y-1;
        int w = dockableBounds.width-2;
        int h = dockableBounds.height-2;
        
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_ATOP, 0.4f ) );
        g2.drawRoundRect( x, y, w, h, 50, 50 );
        g2.fillRoundRect( x, y, w, h, 50, 50 );
        
        g2.dispose();
        
        color.connect( null );
    }

    public void drawInsertionLine( Graphics g, DockStation station, int x1, int y1, int x2, int y2 ) {
        color.setId( "paint.line" );
        color.connect( station.getController() );
        
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setColor( color.value() );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setStroke( new BasicStroke( 3f ));
        g2.drawLine( x1, y1, x2, y2 );
        g2.dispose();
        
        color.connect( null );
    }
    
    public void drawRemoval( Graphics g, DockStation station, Rectangle stationBounds, Rectangle dockableBounds ){
    	// ignore
    }
}
