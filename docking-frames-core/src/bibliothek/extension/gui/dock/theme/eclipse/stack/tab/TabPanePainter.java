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
package bibliothek.extension.gui.dock.theme.eclipse.stack.tab;

import java.awt.Component;
import java.awt.Graphics;

import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseTabPane;
import bibliothek.gui.DockController;

/**
 * A {@link TabPanePainter} paints decorations on a {@link EclipseTabPane}.
 * @author Benjamin Sigg
 */
public interface TabPanePainter {
    /**
     * Sets the controller for which this painter is used.
     * @param controller the controller, can be <code>null</code>
     */
    public void setController( DockController controller );
    
    /**
     * Paints the decorations of the {@link EclipseTabPane} with this 
     * this painter is associated. This method is called before any {@link Component}
     * is painted. The graphics object is such that its point 0/0 falls together
     * with the point 0/0 of {@link EclipseTabPane#getAvailableArea()}.  
     * @param g graphics context to use for painting
     */
    public void paintBackground( Graphics g );
    
    /**
     * Paints the decorations of the {@link EclipseTabPane} with which
     * this painter is associated. This method is called after the {@link Component}s 
     * have been painted. The graphics object is such that
     * its point 0/0 falls together with the point 0/0 of
     * {@link EclipseTabPane#getAvailableArea()}. 
     * @param g the graphics used to paint on <code>tabStrip</code> 
     */
    public void paintForeground( Graphics g);
}
