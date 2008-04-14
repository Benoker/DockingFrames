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
package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import bibliothek.extension.gui.dock.theme.eclipse.rex.RexTabbedComponent;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.util.color.AbstractDockColor;
import bibliothek.gui.dock.util.color.ColorCodes;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.color.DockColor;

/**
 * Paints the background of the tab by just painting a single line.
 * @author Benjamin Sigg
 */
@ColorCodes( "stack.border" )
public class LineStripPainter implements TabStripPainter {
    private AbstractDockColor color = new AbstractDockColor( "stack.border", DockColor.class, Color.BLACK ){
        @Override
        protected void changed( Color oldColor, Color newColor ) {
            tabbedComponent.repaint();
        }        
    };
    
    private RexTabbedComponent tabbedComponent;
    
    /**
     * Creates a new painter.
     * @param tabbedComponent the component for which this painter will work
     */
    public LineStripPainter( RexTabbedComponent tabbedComponent ){
        this.tabbedComponent = tabbedComponent;
    }
    
    public void paintTabStrip( Component tabStrip, Graphics g ) {

        int selectedIndex = tabbedComponent.getSelectedIndex();
        if (selectedIndex != -1) {
            Rectangle selectedBounds = tabbedComponent.getBoundsAt(selectedIndex);
            int to = selectedBounds.x;
            int from = selectedBounds.x + selectedBounds.width-1;
            int end = tabStrip.getWidth();
            
            g.setColor( color.color() );
            int y = tabStrip.getHeight()-1;
            
            if (to != 0)
                g.drawLine(-1, y, to-1, y);
            if( from != end )
                g.drawLine(from, y, end, y);
        }
    }

    public void setController( DockController controller ) {
        ColorManager colors = controller == null ? null : controller.getColors();
        color.setManager( colors );
    }
}
