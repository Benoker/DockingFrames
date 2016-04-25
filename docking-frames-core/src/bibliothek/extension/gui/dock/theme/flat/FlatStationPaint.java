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

package bibliothek.extension.gui.dock.theme.flat;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.themes.color.StationPaintColor;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * A paint which draws gray and white markings
 * @author Benjamin Sigg
 */
@ColorCodes({ "paint.line", "paint.divider", "paint.insertion.area", "paint.insertion.border", "paint.removal" })
public class FlatStationPaint implements StationPaint{
    private StationPaintColor color = new StationPaintColor( "", this, Color.WHITE ){
        @Override
        protected void changed( Color oldColor, Color newColor ) {
            // ignore
        }
    };
    
    public void drawInsertionLine( Graphics g, DockStation station, int x1, int y1, int x2, int y2 ) {
        color.setId( "paint.line" );
        color.setBackup( Color.DARK_GRAY );
        color.connect( station.getController() );
        
        g.setColor( color.value() );
        Graphics2D g2 = (Graphics2D)g;
        Stroke old = g2.getStroke();
        g2.setStroke( new BasicStroke( 3f ));
        g2.drawLine( x1, y1, x2, y2 );
        g2.setStroke( old );
        
        color.connect( null );
    }

    public void drawDivider( Graphics g, DockStation station, Rectangle bounds ) {
        color.setId( "paint.divider" );
        color.setBackup( Color.DARK_GRAY );
        color.connect( station.getController() );
        
        g.setColor( color.value() );
        Graphics2D g2 = (Graphics2D)g;
        Stroke old = g2.getStroke();
        g2.setStroke( new BasicStroke( 3f ));
        g2.fillRect( bounds.x, bounds.y, bounds.width, bounds.height );
        g2.setStroke( old );
        
        color.connect( null );
    }

    public void drawInsertion( Graphics g, DockStation station, Rectangle stationBounds, Rectangle dockableBounds ) {
        color.setId( "paint.insertion.area" );
        color.setBackup( Color.WHITE );
        color.connect( station.getController() );
        
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor( color.value() );
        Composite oldComposite = g2.getComposite();
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.5f ) );
        g.fillRect( dockableBounds.x+1, dockableBounds.y+1, dockableBounds.width-2, dockableBounds.height-2 );
        g2.setComposite( oldComposite );
        
        color.setId( "paint.insertion.border" );
        color.setBackup( Color.DARK_GRAY );
        g.setColor( color.value() );
        
        Stroke old = g2.getStroke();
        g2.setStroke( new BasicStroke( 2f ));
        g.drawRect( dockableBounds.x+1, dockableBounds.y+1, dockableBounds.width-2, dockableBounds.height-2 );
        g2.setStroke( old );
        
        color.connect( null );
    }
    
    public void drawRemoval( Graphics g, DockStation station, Rectangle stationBounds, Rectangle dockableBounds ){
    	color.setId( "paint.removal" );
        color.setBackup( Color.BLACK );
        color.connect( station.getController() );
        
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor( color.value() );
        Composite oldComposite = g2.getComposite();
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.5f ) );
        g.fillRect( dockableBounds.x+1, dockableBounds.y+1, dockableBounds.width-2, dockableBounds.height-2 );
        g2.setComposite( oldComposite );
                
        color.connect( null );
    }
}
