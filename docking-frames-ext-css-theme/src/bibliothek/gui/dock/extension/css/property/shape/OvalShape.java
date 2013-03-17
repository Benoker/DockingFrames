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
package bibliothek.gui.dock.extension.css.property.shape;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import bibliothek.gui.dock.extension.css.property.SimpleCssPropertyContainer;

/**
 * An {@link OvalShape} is an oval touching the borders of the available space.
 * @author Benjamin Sigg
 */
public class OvalShape extends SimpleCssPropertyContainer implements CssShape{
	private Ellipse2D shape;
	
	@Override
	public void setSize( int width, int height ){
		if( shape == null || shape.getWidth() != width || shape.getHeight() != height ){
			shape = new Ellipse2D.Double( 0, 0, width, height );
		}
	}

	@Override
	public boolean contains( int x, int y ){
		if( shape == null ){
			return false;
		}
		return shape.contains( x, y );
	}

	@Override
	public Shape toShape(){
		return shape;
	}
}
