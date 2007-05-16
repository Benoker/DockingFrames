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

package bibliothek.gui.dock.themes.flat;

import java.awt.*;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.station.StationPaint;

/**
 * A paint which draws gray and white markings
 * @author Benjamin Sigg
 */
public class FlatStationPaint implements StationPaint{
    public void drawInsertionLine( Graphics g, DockStation station, int x1, int y1, int x2, int y2 ) {
        g.setColor( Color.DARK_GRAY );
        Graphics2D g2 = (Graphics2D)g;
        Stroke old = g2.getStroke();
        g2.setStroke( new BasicStroke( 3f ));
        g2.drawLine( x1, y1, x2, y2 );
        g2.setStroke( old );
    }

    public void drawDivider( Graphics g, DockStation station, Rectangle bounds ) {
        g.setColor( Color.DARK_GRAY );
        Graphics2D g2 = (Graphics2D)g;
        Stroke old = g2.getStroke();
        g2.setStroke( new BasicStroke( 3f ));
        g2.fillRect( bounds.x, bounds.y, bounds.width, bounds.height );
        g2.setStroke( old );
    }

    public void drawInsertion( Graphics g, DockStation station, Rectangle stationBounds, Rectangle dockableBounds ) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor( Color.WHITE );
        Composite oldComposite = g2.getComposite();
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.5f ) );
        g.fillRect( dockableBounds.x+1, dockableBounds.y+1, dockableBounds.width-2, dockableBounds.height-2 );
        g2.setComposite( oldComposite );
        
        g.setColor( Color.DARK_GRAY );
        
        Stroke old = g2.getStroke();
        g2.setStroke( new BasicStroke( 2f ));
        g.drawRect( dockableBounds.x+1, dockableBounds.y+1, dockableBounds.width-2, dockableBounds.height-2 );
        g2.setStroke( old );
    }
}