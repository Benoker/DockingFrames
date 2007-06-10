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

package bibliothek.extension.gui.dock.theme.flat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

/**
 * A {@link Border} which paints a 3d-effect. The owner of this
 * border seems to fly over its parent.
 * @author Benjamin Sigg
 */
public class FlatBorder implements Border{
    /** Factor to be multiplied with an RGB-value to darken a color */
    private static final float FACTOR = 0.85f;
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height){
        Color background = c.getBackground();
        Color light = darker( background );
        Color middle = darker( light );
        Color dark = darker( middle );
        
        g.setColor( dark );
        g.drawRect( x, y, width-3, height-3 );
        
        g.setColor( middle );
        g.drawLine( x+width-2, y+1, x+width-2, y+height-3 );
        g.drawLine( x+width-3, y, x+width-3, y );
        g.drawLine( x, y+height-3, x, y+height-3 );
        g.drawLine( x+1, y+height-2, x+width-3, y+height-2 );
        
        g.setColor( light );
        g.drawLine( x+width-1, y+1, x+width-1, y+height-2 );
        g.drawLine( x+width-2, y, x+width-2, y );
        g.drawLine( x, y+height-2, x, y+height-2 );
        g.drawLine( x+1, y+height-1, x+width-2, y+height-1 );
        g.drawLine( x+width-2, y+height-2, x+width-2, y+height-2 );
    }
    
    /**
     * Creates a darker version of <code>c</code>.
     * @param c the original color
     * @return a darker version of the color
     */
    private Color darker( Color c ){
        return new Color(Math.max((int)(c.getRed()*FACTOR), 0), 
             Math.max((int)(c.getGreen()*FACTOR), 0),
             Math.max((int)(c.getBlue()*FACTOR), 0));
    }

    public Insets getBorderInsets(Component c){
        return new Insets( 1, 1, 3, 3 );
    }

    public boolean isBorderOpaque(){
        return false;
    }
}
