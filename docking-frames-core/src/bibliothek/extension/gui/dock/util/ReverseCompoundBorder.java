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
package bibliothek.extension.gui.dock.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

/**
 * Behaves like its super-class but the painting order of the
 * borders is reversed.
 * @author Benjamin Sigg
 */
public class ReverseCompoundBorder extends CompoundBorder{
	public ReverseCompoundBorder( Border outsideBorder, Border insideBorder ){
		super( outsideBorder, insideBorder );
	}

	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ){
		Insets  nextInsets;
		int px, py, pw, ph;

		px = x;
		py = y;
		pw = width;
		ph = height;

		if( outsideBorder != null ) {
			nextInsets = outsideBorder.getBorderInsets(c);
			px += nextInsets.left;
			py += nextInsets.top;
			pw = pw - nextInsets.right - nextInsets.left;
			ph = ph - nextInsets.bottom - nextInsets.top;
		}
		if( insideBorder != null ) 
			insideBorder.paintBorder( c, g, px, py, pw, ph );		
		
		if( outsideBorder != null ) {
			outsideBorder.paintBorder(c, g, x, y, width, height );		
		}
	}
}
