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

import java.awt.Component;
import java.awt.Graphics;

import bibliothek.gui.dock.extension.css.CssPropertyContainer;
import bibliothek.gui.dock.extension.css.property.shape.CssShape;

/**
 * An algorithm used to paint an area or border.
 * @author Benjamin Sigg
 */
public interface CssPaint extends CssPropertyContainer{
	/**
	 * Informs this paint about the {@link Component} it will paint.
	 * @param component the owner of this paint
	 */
	public void init( Component component );
	
	/**
	 * Paints the area inside of <code>shape</code>.
	 * @param g the graphics context to use
	 * @param c the {@link Component} on which this paint is painting
	 * @param shape the shape of the area to paint, can be <code>null</code>
	 */
	public void paintArea( Graphics g, Component c, CssShape shape );
	
	/**
	 * Paints the border of <code>shape</code>.
	 * @param g the graphics context to use
	 * @param c the {@link Component} on which this paint is painting
	 * @param shape the shape of the area whose border is painted, can be <code>null</code>
	 */
	public void paintBorder( Graphics g, Component c, CssShape shape );
}
