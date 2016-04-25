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
 * This border paints a thin line. The edges are either painted as little
 * curve or as hard edge, depending on the arguments used on
 * {@link #setRoundEdges(int)}.
 * @author Benjamin Sigg
 *
 */
public class RectEclipseBorder implements Border {
    /** whether to fill the top edges */
    private boolean fillEdges;
    
    /** the source for colors */
    private DockController controller;
    
	
	/** constant indicating the top left edge has to be painted round */
	public static final int TOP_LEFT = 1;
	/** constant indicating the top right edge has to be painted round */
	public static final int TOP_RIGHT = 2;
	/** constant indicating the bottom left edge has to be painted round */
	public static final int BOTTOM_LEFT = 4;
	/** constant indicating the bottom right edge has to be painted round */
	public static final int BOTTOM_RIGHT = 8;
	
	/** which edges to paint round */
	private int roundEdges;

    /**
     * Creates a new border.
     * @param controller the controller for which this border will be used
     * @param fillEdges whether the top edges should be filled with the
     * background color or let empty.
     */
    public RectEclipseBorder( DockController controller, boolean fillEdges ){
    	this( controller, fillEdges, TOP_LEFT | TOP_RIGHT );
    }
    
    /**
     * Creates a new border.
     * @param controller the controller for which this border will be used
     * @param fillEdges whether the top edges should be filled with the
     * background color or let empty.
     * @param roundEdges which edges to paint round
     */
    public RectEclipseBorder( DockController controller, boolean fillEdges, int roundEdges ){
        this.controller = controller;
        this.fillEdges = fillEdges;
    }
    
    /**
     * Sets the edges which have to be painted round, see {@link #TOP_LEFT},
     * {@link #TOP_RIGHT}, {@link #BOTTOM_LEFT} and {@link #BOTTOM_RIGHT}.
     * @param roundEdges the round edges
     */
    public void setRoundEdges( int roundEdges ){
		this.roundEdges = roundEdges;
	}
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if( fillEdges ){
            g.setColor( c.getBackground() );
    
            // top left corner
            if( (roundEdges & TOP_LEFT) != 0 ){
            	g.drawLine( 0, 0, 1, 0 );
            	g.drawLine( 0, 1, 0, 1 );
            }
            
            if( (roundEdges & TOP_RIGHT) != 0 ){
            	g.drawLine( width-2, 0, width-1, 0 );
            	g.drawLine( width-1, 1, width-1, 1 );
            }
            
            if( (roundEdges & BOTTOM_LEFT) != 0 ){
            	g.drawLine( 0, height-1, 1, height-1 );
            	g.drawLine( 0, height-2, 0, height-2 );
            }
            
            if( (roundEdges & BOTTOM_RIGHT) != 0 ){
            	g.drawLine( width-2, height-1, width-1, height-1 );
            	g.drawLine( width-1, height-2, width-1, height-2 );
            }
        }
        
        Color color = controller.getColors().get( "stack.border" );
        if( color == null )
            color = RexSystemColor.getBorderColor();
        
        g.setColor( color );
        
        // top left corner
        if( (roundEdges & TOP_LEFT) != 0 ){
        	g.drawLine( 1, 1, 1, 1 );
        }
        else{
        	g.drawLine( 0, 0, 1, 0 );
        	g.drawLine( 0, 1, 0, 1 );
        }
        
        if( (roundEdges & TOP_RIGHT) != 0 ){
        	g.drawLine( width-2, 1, width-2, 1 );
        }
        else{
        	g.drawLine( width-2, 0, width-1, 0 );
        	g.drawLine( width-1, 1, width-1, 1 );
        }
        
        if( (roundEdges & BOTTOM_LEFT) != 0 ){
        	g.drawLine( 1, height-2, 1, height-2 );
        }
        else{
        	g.drawLine( 0, height-2, 0, height-1 );
        	g.drawLine( 1, height-1, 1, height-1 );
        }
        
        if( (roundEdges & BOTTOM_RIGHT ) != 0 ){
        	g.drawLine( width-2, height-2, width-2, height-2 );
        }
        else{
        	g.drawLine( width-1, height-2, width-1, height-1 );
        	g.drawLine( width-2, height-1, width-2, height-1 );
        }
        
        // rest
        // top
        g.drawLine( 2, 0, width-3, 0 );
        // left
        g.drawLine( 0, 2, 0, height - 3);
        // right
        g.drawLine( width-1, 2, width-1, height - 3);
        // bottom
        g.drawLine( 2, height-1, width-3, height-1 );
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(1, 1, 1, 1);
    }

    public boolean isBorderOpaque() {
        return false;
    }
}
