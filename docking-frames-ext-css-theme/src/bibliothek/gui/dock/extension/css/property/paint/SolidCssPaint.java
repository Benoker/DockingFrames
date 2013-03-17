/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.property.paint;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyContainerListener;
import bibliothek.gui.dock.extension.css.property.shape.CssShape;

/**
 * This paint just fills an area with one color.
 * @author Benjamin Sigg
 */
public class SolidCssPaint implements CssPaint{
	private Color color;
	private Component component;
	
	private ColorCssProperty colorProperty = new ColorCssProperty(){
		@Override
		public void set( Color value ){
			color = value;
			if( component != null ){
				component.repaint();
			}
		}
	};
	
	@Override
	public void init( Component component ){
		this.component = component;
	}
	
	@Override
	public String[] getPropertyKeys(){
		return new String[]{ "color" };
	}

	@Override
	public CssProperty<?> getProperty( String key ){
		if( "color".equals( key )){
			return colorProperty;
		}
		return null;
	}

	@Override
	public void addPropertyContainerListener( CssPropertyContainerListener listener ){
		// ignore
	}

	@Override
	public void removePropertyContainerListener( CssPropertyContainerListener listener ){
		// ignore
	}

	@Override
	public void paintArea( Graphics g, Component c, CssShape shape ){
		if( color == null ){
			g.setColor( c.getBackground() );
		}
		else{
			g.setColor( color );
		}
		
		Shape clip = null;
		
		if( shape != null ){
			shape.setSize( c.getWidth(), c.getHeight() );
			clip = shape.toShape();
		}
		
		if( clip != null ){
			Graphics2D g2 = (Graphics2D)g;
			g2.fill( clip );
		}
		else{
			g.fillRect( 0, 0, c.getWidth(), c.getHeight() );
		}
	}

	@Override
	public void paintBorder( Graphics g, Component c, CssShape shape ){
		// ignore
	}
	
	@Override
	public String toString(){
		return getClass().getSimpleName() + "[color=" + color + "]";
	}
}
