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
package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

import bibliothek.extension.gui.dock.theme.eclipse.rex.RexSystemColor;
import bibliothek.gui.DockController;

/**
 * This border paints a thin line. At the bottom, two hard edges are painted,
 * at the top the line makes a little curve.
 * @author Benjamin Sigg
 *
 */
public class RectEclipseBorder implements Border {
    /** whether to fill the top edges */
    private boolean fillEdges;
    
    /** the source for colors */
    private DockController controller;
    
    /**
     * Creates a new border.
     * @param controller the controller for which this border will be used
     * @param fillEdges whether the top edges should be filled with the
     * background color or let empty.
     */
    public RectEclipseBorder( DockController controller, boolean fillEdges ){
        this.controller = controller;
        this.fillEdges = fillEdges;
    }
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if( fillEdges ){
            g.setColor( c.getBackground() );
    
            // top left corner
            g.drawLine( 0, 0, 1, 0 );
            g.drawLine( 0, 1, 0, 1 );
            
            g.drawLine( width-2, 0, width-1, 0 );
            g.drawLine( width-1, 1, width-1, 1 );
        }
        
        Color color = controller.getColors().get( "stack.border" );
        if( color == null )
            color = RexSystemColor.getBorderColor();
        
        g.setColor( color );
        
        // top left corner
        g.drawLine( 1, 1, 1, 1 );
        // top right corner
        g.drawLine( width-2, 1, width-2, 1 );
        
        // rest
        // top
        g.drawLine( 2, 0, width-3, 0 );
        // left
        g.drawLine( 0, 2, 0, height - 1);
        // right
        g.drawLine( width-1, 2, width-1, height - 1);
        // bottom
        g.drawLine( 0, height-1, width-1, height-1 );
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(1, 1, 1, 1);
    }

    public boolean isBorderOpaque() {
        return false;
    }
}
