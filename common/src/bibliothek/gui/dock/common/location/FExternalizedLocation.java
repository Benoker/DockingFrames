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
package bibliothek.gui.dock.common.location;

import bibliothek.gui.dock.common.FControl;
import bibliothek.gui.dock.common.FLocation;
import bibliothek.gui.dock.common.intern.FDockable.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;

/**
 * A location representing an externalized element.
 * @author Benjamin Sigg
 */
public class FExternalizedLocation extends AbstractStackholdingLocation{
	/** the x-coordinate */
	private int x;
	/** the y-coordinate */
	private int y;
	/** the width in pixel */
	private int width;
	/** the height in pixel */
	private int height;
	
	/**
	 * Creates a new location.
	 * @param x the x-coordinate in pixel
	 * @param y the y-coordinate in pixel
	 * @param width the width in pixel
	 * @param height the height in pixel
	 */
	public FExternalizedLocation( int x, int y, int width, int height ){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public String findRoot(){
		return FControl.EXTERNALIZED_STATION_ID;
	}
	
	@Override
	public ExtendedMode findMode(){
		return ExtendedMode.EXTERNALIZED;
	}
	
	@Override
	public DockableProperty findProperty( DockableProperty successor ){
		ScreenDockProperty screen = new ScreenDockProperty( x, y, width, height );
		screen.setSuccessor( successor );
		return screen;
	}
	
	@Override
	public FLocation aside() {
	    return stack( 1 );
	}
	
	@Override
	public String toString() {
	    return "[externalized " + x + " " + y + " " + width + " " + height + "]";
	}
}
