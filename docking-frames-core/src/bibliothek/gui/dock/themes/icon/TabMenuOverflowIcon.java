/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.themes.icon;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;

import bibliothek.gui.dock.util.font.GenericFontModifier;

/**
 * This icon shows an arrow and a number indicating the size of some menu. This icon always
 * has a size of 16x16 pixels.
 * @author Benjamin Sigg
 */
public class TabMenuOverflowIcon implements Icon{
	/** The size of the menu */
	private int size;
	
	/**
	 * Creates a new icon.
	 * @param size the number to show
	 */
	public TabMenuOverflowIcon( int size){
		this.size = size;
	}
	
	public int getIconWidth(){
		return 16;
	}
	
	public int getIconHeight(){
		return 16;
	}
	
	public void paintIcon( Component c, Graphics g, int x, int y ){
		g.setColor( c.getForeground() );
		
		drawArrow( g, x+1, y+1 );
		drawArrow( g, x+4, y+1 );
		
		String text;
		if( size > 100 ){
			text = "+";
		}
		else{
			text = String.valueOf( size );
		}
		
		Font font = g.getFont();
		GenericFontModifier modifier = new GenericFontModifier();
		modifier.setSizeDelta( false );
		modifier.setSize( 8 );
		g.setFont( modifier.modify( font ) );
		
		TextLayout layout = new TextLayout( text, g.getFont(), ((Graphics2D)g).getFontRenderContext() );
		Rectangle2D bounds = layout.getBounds();
		
		layout.draw( (Graphics2D)g, 
				(float)(x + getIconWidth() - bounds.getWidth() - bounds.getX() - 1), 
				(float)(y + getIconHeight() - bounds.getHeight() - bounds.getY() - 1 ));
		
		g.setFont( font );
	}
	
	private void drawArrow( Graphics g, int x, int y ){
		g.drawLine( x, y, x+1, y );
		g.drawLine( x+1, y+1, x+2, y+1 );
		g.drawLine( x+2, y+2, x+3, y+2 );
		g.drawLine( x+1, y+3, x+2, y+3 );
		g.drawLine( x, y+4, x+1, y+4 );
	}
}
