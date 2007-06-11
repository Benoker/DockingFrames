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

import java.awt.Graphics;
import java.awt.Rectangle;

import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.station.StationPaint;

/**
 * A wrapper for a {@link StationPaint}. The wrapper uses a delegate,
 * and if no delegate is known, the {@link DockUI} is asked to provide
 * a paint appropriate to the current {@link DockTheme}.
 * @author Benjamin Sigg
 */
public class StationPaintWrapper implements StationPaint {
    /** The delegate that is used if not equal to <code>null</code> */
    private StationPaint delegate;

    /**
     * Gets the {@link StationPaint} to which calls to this paint are
     * forwarded.
     * @return the delegate or <code>null</code>
     * @see #setDelegate(StationPaint)
     */
    public StationPaint getDelegate() {
        return delegate;
    }
    
    /**
     * Sets the <code>delegate</code> property. If this property is set,
     * all calls to the Methods of {@link StationPaint} are forwarded to
     * it. Otherwise a default paint is used.
     * @param delegate the delegate or <code>null</code>
     */
    public void setDelegate( StationPaint delegate ) {
        this.delegate = delegate;
    }
    
    public void drawDivider( Graphics g, DockStation station, Rectangle bounds ) {
        DockUI.getPaint( delegate, station ).drawDivider( g, station, bounds );
    }

    public void drawInsertion( Graphics g, DockStation station,
            Rectangle stationBounds, Rectangle dockableBounds ) {
        DockUI.getPaint( delegate, station ).drawInsertion( g, station, stationBounds, dockableBounds );
    }

    public void drawInsertionLine( Graphics g, DockStation station, int x1,
            int y1, int x2, int y2 ) {
        DockUI.getPaint( delegate, station ).drawInsertionLine( g, station, x1, y1, x2, y2 );
    }

}
