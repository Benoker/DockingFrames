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
package bibliothek.gui.dock.station.toolbar;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

import bibliothek.gui.dock.station.DockableDisplayer.Location;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayer;

/**
 * A {@link Border} used by {@link ToolbarDockableDisplayer} to paint lines at all sides
 * except the side at which there is a title.
 * @author Benjamin Sigg
 */
public class ToolbarLineBorder implements Border{
	private BasicDockableDisplayer displayer;
	
	/**
	 * Creates a new border.
	 * @param displayer the displayer using this border
	 */
	public ToolbarLineBorder( BasicDockableDisplayer displayer ){
		this.displayer = displayer;
	}
	
	private Location hiddenSide(){
		if( displayer.getTitle() == null ){
			return null;
		}
		return displayer.getTitleLocation();
	}
	
	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ){
		Location hidden = hiddenSide();
		g.setColor( c.getForeground() );
		
		if( hidden != Location.BOTTOM ){
			g.drawLine( x, y+height-1, x+width, y+height-1 );
		}
		if( hidden != Location.TOP ){
			g.drawLine( x, y, x+width-1, y );
		}
		if( hidden != Location.LEFT ){
			g.drawLine( x, y, x, y+height-1 );
		}
		if( hidden != Location.RIGHT ){
			g.drawLine( x+width-1, y, x+width-1, y+height-1 );
		}
	}

	@Override
	public Insets getBorderInsets( Component c ){
		Insets insets = new Insets( 1, 1, 1, 1 );
		Location hidden = hiddenSide();
		if( hidden != null ){
			switch( hidden ){
				case BOTTOM:
					insets.bottom = 0;
					break;
				case TOP:
					insets.top = 0;
					break;
				case LEFT:
					insets.left = 0;
					break;
				case RIGHT:
					insets.right = 0;
					break;
			}
		}
		return insets;
	}

	@Override
	public boolean isBorderOpaque(){
		return false;
	}
}
