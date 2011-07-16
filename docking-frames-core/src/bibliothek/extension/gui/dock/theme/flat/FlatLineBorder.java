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
package bibliothek.extension.gui.dock.theme.flat;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

import bibliothek.util.Colors;

/**
 * A border used by {@link FlatButtonTitle} if the button is not selected or active. This border paints
 * only a thin, nearly invisible line around a {@link Component}.
 * @author Benjamin Sigg
 */
public class FlatLineBorder implements Border{
	/** a constant instance of {@link FlatLineBorder} */
	public static final FlatLineBorder INSTANCE = new FlatLineBorder();
	
	public Insets getBorderInsets( Component c ){
		return new Insets( 2, 2, 2, 2 );
	}

	public boolean isBorderOpaque(){
		return false;
	}

	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ){
		g.setColor( Colors.diffMirror( c.getBackground(), 0.15 ) );
		g.drawRect( x+1, y+1, width-3, height-3 );
	}
}
