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

import bibliothek.extension.gui.dock.theme.eclipse.rex.RexSystemColor;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * A border that has round edges.
 * @author Janni Kovacs
 * @author Benjamin Sigg
 */
@ColorCodes( {"stack.border", "stack.border.edges"} )
public class EclipseBorder extends AbstractEclipseBorder {
	/**
	 * Creates a new border
	 * @param controller the owner of this border
	 * @param fillEdges whether to paint over the edges
	 */
	public EclipseBorder( DockController controller, boolean fillEdges ){
		super( controller, fillEdges, TOP_LEFT | TOP_RIGHT );
	}
	
	/**
	 * Creates a new border
	 * @param controller the owner of this border
	 * @param fillEdges whether to paint over the edges
	 * @param edges the edges that are painted round, or-ed from {@link #TOP_LEFT},
	 * {@link #TOP_RIGHT}, {@link #BOTTOM_LEFT} and {@link #BOTTOM_RIGHT}
	 */
	public EclipseBorder( DockController controller, boolean fillEdges, int edges ){
	    super( controller, fillEdges, edges );
	}
	
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		int roundEdges = getRoundEdges();
		DockController controller = getController();
		
		if( isFillEdges() ){
			Color color = controller.getColors().get( "stack.border.edges" );
			if( color == null )
			    color = c.getBackground();
			
    	    g.setColor( color );
    
    	    if( (roundEdges & TOP_LEFT) != 0 ){
    	    	g.fillRect( 0, 0, 6, 1 );
    	    	g.fillRect( 0, 1, 4, 1 );
    	    	g.fillRect( 0, 2, 3, 1 );
    	    	g.fillRect( 0, 3, 2, 1 );
    	    	g.fillRect( 0, 4, 1, 2 );
    	    }
    
    	    if( (roundEdges & TOP_RIGHT ) != 0 ){
	    	    g.fillRect( width-6, 0, 6, 1 );
	            g.fillRect( width-4, 1, 4, 1 );
	            g.fillRect( width-3, 2, 3, 1 );
	            g.fillRect( width-2, 3, 2, 1 );
	            g.fillRect( width-1, 4, 1, 2 );
    	    }
    	    
    	    if( (roundEdges & BOTTOM_LEFT) != 0 ){
    	    	g.fillRect( 0, height-1, 6, 1 );
    	    	g.fillRect( 0, height-1-1, 4, 1 );
    	    	g.fillRect( 0, height-1-2, 3, 1 );
    	    	g.fillRect( 0, height-1-3, 2, 1 );
    	    	g.fillRect( 0, height-2-4, 1, 2 );
    	    }
    	    
    	    if( (roundEdges & BOTTOM_RIGHT) != 0 ){
    	    	g.fillRect( width-6, height-1, 6, 1 );
    	    	g.fillRect( width-4, height-1-1, 4, 1 );
    	    	g.fillRect( width-3, height-1-2, 3, 1 );
    	    	g.fillRect( width-2, height-1-3, 2, 1 );
    	    	g.fillRect( width-1, height-2-4, 1, 2 );
    	    }
		}
		
		Color color = controller.getColors().get( "stack.border" );
		if( color == null )
		    color = RexSystemColor.getBorderColor();
		
		g.setColor( color );
		if( (roundEdges & TOP_LEFT) != 0 ){
			g.drawLine(4, 1, 5, 1);
			g.drawLine(3, 2, 3, 2);
			g.drawLine(2, 3, 2, 3);
			g.drawLine(1, 4, 1, 5);
		}
		else{
			g.drawLine( 0, 0, 6, 0 );
			g.drawLine( 0, 1, 0, 6 );
		}
		if( (roundEdges & TOP_RIGHT) != 0 ){
			g.drawLine(width - 5, 1, width - 6, 1);
			g.drawLine(width - 4, 2, width - 4, 2);
			g.drawLine(width - 3, 3, width - 3, 3);
			g.drawLine(width - 2, 4, width - 2, 5);
		}
		else{
			g.drawLine( width-6, 0, width-1, 0 );
			g.drawLine( width-1, 1, width-1, 6 );
		}
		if( (roundEdges & BOTTOM_LEFT ) != 0 ){
			g.drawLine(4, height-2, 5, height-2);
			g.drawLine(3, height-3, 3, height-3);
			g.drawLine(2, height-4, 2, height-4);
			g.drawLine(1, height-5, 1, height-6);
		}
		else{
			g.drawLine( 0, height-1, 0, height-6 );
			g.drawLine( 1, height-1, 6, height-1 );
		}
		if( (roundEdges & BOTTOM_RIGHT) != 0 ){
			g.drawLine(width-5, height-2, width-6, height-2);
			g.drawLine(width-4, height-3, width-4, height-3);
			g.drawLine(width-3, height-4, width-3, height-4);
			g.drawLine(width-2, height-5, width-2, height-6);
		}
		else{
			g.drawLine( width-1, height-1, width-6, height-1 );
			g.drawLine( width-1, height-2, width-1, height-6 );
		}
		// between edges
		g.drawLine(0, 6, 0, height-7);
		g.drawLine(6, height-1, width-7, height-1);
		g.drawLine(width-1, 6, width-1, height-7);
		g.drawLine(6, 0, width-7, 0);
	}

	public Insets getBorderInsets(Component c) {
		return new Insets( 1, 1, 1, 1 );
	}
}
