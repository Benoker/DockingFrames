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
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * A {@link Border} used by the {@link EclipseButtonTitle}.
 * @author Benjamin Sigg
 */
@ColorCodes({"flap.button.border.edges", "flap.button.border.inner", "flap.button.border.outer"})
public class EclipseButtonBorder extends AbstractEclipseBorder{
	/**
	 * Creates a new border
	 * @param controller the controller in whose realm this border is used
	 * @param fillEdges whether to fill the edges with a color
	 * @param edges which edges to paint round, a bitwise or of the constants defined in {@link AbstractEclipseBorder}
	 */
	public EclipseButtonBorder( DockController controller, boolean fillEdges, int edges ){
		super( controller, fillEdges, edges );
	}

	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ){
		int roundEdges = getRoundEdges();
		DockController controller = getController();
		
		g.translate( x, y );
		
		if( isFillEdges() ){
			Color color = controller.getColors().get( "flap.button.border.edges" );
			if( color == null )
			    color = c.getBackground();
			
    	    g.setColor( color );
    
    	    if( (roundEdges & TOP_LEFT) != 0 ){
    	    	g.drawLine( 0, 0, 4, 0 );
    	    	g.drawLine( 0, 1, 2, 1 );
    	    	g.drawLine( 0, 2, 1, 2 );
    	    	g.drawLine( 0, 3, 0, 4 );
    	    }
    
    	    if( (roundEdges & TOP_RIGHT ) != 0 ){
    	    	g.drawLine( width-1, 0, width-5, 0 );
    	    	g.drawLine( width-1, 1, width-3, 1 );
    	    	g.drawLine( width-1, 2, width-2, 2 );
    	    	g.drawLine( width-1, 3, width-1, 4 );
    	    }
    	    
    	    if( (roundEdges & BOTTOM_LEFT) != 0 ){
    	    	g.drawLine( 0, height-1, 4, height-1 );
    	    	g.drawLine( 0, height-2, 2, height-2 );
    	    	g.drawLine( 0, height-3, 1, height-3 );
    	    	g.drawLine( 0, height-4, 0, height-5 );
    	    }
    	    
    	    if( (roundEdges & BOTTOM_RIGHT) != 0 ){
    	    	g.drawLine( width-1, height-1, width-5, height-1 );
    	    	g.drawLine( width-1, height-2, width-3, height-2 );
    	    	g.drawLine( width-1, height-3, width-2, height-3 );
    	    	g.drawLine( width-1, height-4, width-1, height-5 );
    	    }
		}
		
		Color inner = controller.getColors().get( "flap.button.border.inner" );
		Color outer = controller.getColors().get( "flap.button.border.outer" );
		if( inner == null ){
		    inner = RexSystemColor.getBorderColor();
		}
		if( outer == null ){
			outer = RexSystemColor.getBorderColor();
		}
		
		// top left
		if( (roundEdges & TOP_LEFT) != 0 ){
			g.setColor( outer );
			g.drawLine( 1, 3, 1, 4 );
			g.drawLine( 2, 2, 2, 2 );
			g.drawLine( 3, 1, 4, 1 );
			
			g.setColor( inner );
			g.drawLine( 2, 3, 2, 4 );
			g.drawLine( 3, 3, 3, 3 );
			g.drawLine( 3, 2, 4, 2 );
		}
		else{
			g.setColor( outer );
			g.drawLine( 0, 1, 0, 4 );
			g.drawLine( 0, 0, 4, 0 );
			
			g.setColor( inner );
			g.drawLine( 1, 2, 1, 4 );
			g.drawLine( 1, 1, 4, 1 );
		}
		
		// top right
		if( (roundEdges & TOP_RIGHT) != 0 ){
			g.setColor( outer );
			g.drawLine( width-5, 1, width-4, 1 );
			g.drawLine( width-3, 2, width-3, 2 );
			g.drawLine( width-2, 3, width-2, 4 );
			
			g.setColor( inner );
			g.drawLine( width-5, 2, width-4, 2 );
			g.drawLine( width-4, 3,	width-4, 3 );
			g.drawLine( width-3, 3, width-3, 4 );
		}
		else{
			g.setColor( outer );
			g.drawLine( width-5, 0, width-2, 0 );
			g.drawLine( width-1, 0, width-1, 4 );
			
			g.setColor( inner );
			g.drawLine( width-5, 1, width-3, 1 );
			g.drawLine( width-2, 1, width-2, 4 );
		}
		
		// bottom left
		if( (roundEdges & BOTTOM_LEFT ) != 0 ){
			g.setColor( outer );
			g.drawLine( 1, height-5, 1, height-4 );
			g.drawLine( 2, height-3, 2, height-3 );
			g.drawLine( 3, height-2, 4, height-2 );
			
			g.setColor( inner );
			g.drawLine( 2, height-5, 2, height-4 );
			g.drawLine( 3, height-4, 3, height-4 );
			g.drawLine( 3, height-3, 4, height-3 );
		}
		else{
			g.setColor( outer );
			g.drawLine( 0, height-5, 0, height-2 );
			g.drawLine( 0, height-1, 4, height-1 );
			
			g.setColor( inner );
			g.drawLine( 1, height-5, 1, height-3 );
			g.drawLine( 1, height-2, 4, height-2 );
		}
		
		// bottom right
		if( (roundEdges & BOTTOM_RIGHT) != 0 ){
			g.setColor( outer );
			g.drawLine( width-5, height-2, width-4, height-2 );
			g.drawLine( width-3, height-3, width-3, height-3 );
			g.drawLine( width-2, height-4, width-2, height-5 );
			
			g.setColor( inner );
			g.drawLine( width-5, height-3, width-4, height-3 );
			g.drawLine( width-4, height-4, width-4, height-4 );
			g.drawLine( width-3, height-4, width-3, height-5 );
		}
		else{
			g.setColor( outer );
			g.drawLine( width-5, height-1, width-2, height-1 );
			g.drawLine( width-1, height-1, width-1, height-5 );
			
			g.setColor( inner );
			g.drawLine( width-5, height-2, width-3, height-2 );
			g.drawLine( width-2, height-2, width-2, height-5 );
		}
		
		// between edges
		g.setColor( outer );
		g.drawLine( 5, 0, width-6, 0 );
		g.drawLine( 5, height-1, width-6, height-1 );
		g.drawLine( 0, 5, 0, height-6 );
		g.drawLine( width-1, 5, width-1, height-6 );
		
		g.setColor( inner );
		g.drawLine( 5, 1, width-6, 1 );
		g.drawLine( 5, height-2, width-6, height-2 );
		g.drawLine( 1, 5, 1, height-6 );
		g.drawLine( width-2, 5, width-2, height-6 );

		g.translate( -x, -y );
	}
	
	public Insets getBorderInsets( Component c ){
		return new Insets( 2, 2, 2, 2 );
	}
}
