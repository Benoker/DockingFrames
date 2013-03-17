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

import java.awt.Graphics2D;
import java.awt.Shape;

import bibliothek.gui.dock.extension.css.CssPropertyContainer;

/**
 * A {@link CssShape} describes some area, only content within that area is considered
 * to be part of the item that is using this area. One shape can only be used by one 
 * item at a time.
 * @author Benjamin Sigg
 */
public interface CssShape extends CssPropertyContainer{
	/**
	 * Informs this shape what size the underlying item has. This method may be called often.
	 * @param width the maximum width of this shape
	 * @param height the maximum height of this shape
	 */
	public void setSize( int width, int height );
	
	/**
	 * Tells whether the point <code>x/y</code> is within this shape.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return whether <code>x/y</code> is within this shape
	 */
	public boolean contains( int x, int y );
	
	/**
	 * Converts this shape into a form that can be used for painting. The new {@link Shape}
	 * should support {@link Graphics2D#setClip(Shape)}.
	 * @return the shape for painting, or <code>null</code>
	 */
	public Shape toShape();
}
