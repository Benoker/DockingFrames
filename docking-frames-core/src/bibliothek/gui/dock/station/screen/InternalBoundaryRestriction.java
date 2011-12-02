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
package bibliothek.gui.dock.station.screen;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JDesktopPane;

import bibliothek.gui.dock.station.screen.window.InternalScreenDockWindowFactory;

/**
 * This boundary restriction is based on a {@link JDesktopPane}. It makes sure that the top border part of a
 * window is always visible.
 * @author Benjamin Sigg
 * @see InternalScreenDockWindowFactory
 */
public class InternalBoundaryRestriction implements BoundaryRestriction{
	private JDesktopPane desktop;
	
	/** the size {@link #desktop} had when calling code the last time */
	private Dimension lastSize = null;
	
	public InternalBoundaryRestriction( JDesktopPane desktop ){
		this.desktop = desktop;
	}
	
	public Rectangle check( ScreenDockWindow window ){
		return validate( window.getWindowBounds(), window.getTitleCenter() );
	}

	public Rectangle check( ScreenDockWindow window, Rectangle target ){
		return validate( target, window.getTitleCenter() );
	}
	
	protected Rectangle validate( Rectangle destination, Point center ){
		if( desktop.isVisible() ){
			if( lastSize == null || lastSize.width == 0 || lastSize.height == 0 ){
				lastSize = desktop.getSize();
			}
			else{
				if( center == null ){
					center = new Point( destination.width/2, destination.height/2 );
				}
				
				Rectangle result = new Rectangle( destination );
				
				result.x = Math.max( -center.x, result.x );
				result.x = Math.min( result.x, desktop.getWidth() - center.x );
				
				result.y = Math.max( -center.y, result.y );
				result.y = Math.min( desktop.getHeight() - center.y, result.y );
				
				return result;
			}
		}
		return null;
	}
}
