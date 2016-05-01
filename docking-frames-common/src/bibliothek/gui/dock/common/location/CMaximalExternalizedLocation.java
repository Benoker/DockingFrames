/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.common.location;

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;

/**
 * A location representing an maximized externalized element. 
 * @author Benjamin Sigg
 */
public class CMaximalExternalizedLocation extends CExternalizedLocation{
	/**
	 * Creates a new location.
	 * @param x the x-coordinate in pixel
	 * @param y the y-coordinate in pixel
	 * @param width the width in pixel
	 * @param height the height in pixel
	 */
	public CMaximalExternalizedLocation( int x, int y, int width, int height ) {
		super( x, y, width, height );
	}
	
	/**
	 * Creates a new location.
	 * @param parent the parent location, can be <code>null</code>
	 * @param x the x-coordinate in pixel
	 * @param y the y-coordinate in pixel
	 * @param width the width in pixel
	 * @param height the height in pixel
	 */
	public CMaximalExternalizedLocation( CLocation parent, int x, int y, int width, int height ) {
		super( parent, x, y, width, height );
	}

	@Override
	public ExtendedMode findMode(){
		CLocation parent = getParent();
		if( parent != null ){
			return parent.findMode();
		}
		
		return ExtendedMode.MAXIMIZED;
	}
	
	@Override
	public DockableProperty findProperty( DockableProperty successor ){
		ScreenDockProperty screen = new ScreenDockProperty( getX(), getY(), getWidth(), getHeight(), null, true );
		screen.setSuccessor( successor );
		
		CLocation parent = getParent();
		if( parent != null ){
			return parent.findProperty( screen );
		}
		
		return screen;
	}
	
	@Override
	public String toString() {
	    return "[maximized " + getX() + " " + getY() + " " + getWidth() + " " + getHeight() + "]";
	}
}
