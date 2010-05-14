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

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;

/**
 * A location representing an externalized element.
 * @author Benjamin Sigg
 */
public class CExternalizedLocation extends AbstractStackholdingLocation{
    /**
     * A location describing the externalize station with the id
     * {@link CControl#EXTERNALIZED_STATION_ID}.
     */
    public static final CLocation STATION = new CLocation(){
        @Override
        public CLocation aside() {
            return this;
        }

        @Override
        public CLocation expandProperty( DockableProperty property ) {
            if( property instanceof ScreenDockProperty ){
                ScreenDockProperty screen = (ScreenDockProperty)property;
                CLocation location;
                if( screen.isFullscreen() ){
                	location = new CMaximalExternalizedLocation( screen.getX(), screen.getY(), screen.getWidth(), screen.getHeight() );
                }
                else {
                	location = new CExternalizedLocation( screen.getX(), screen.getY(), screen.getWidth(), screen.getHeight() );
                }
                DockableProperty successor = property.getSuccessor();
                if( successor != null )
                    location = location.expandProperty( successor );
                return location;
            }
            return null;
        }

        @Override
        public ExtendedMode findMode() {
            return ExtendedMode.EXTERNALIZED;
        }

        @Override
        public DockableProperty findProperty( DockableProperty successor ) {
            return null;
        }

        @Override
        public String findRoot() {
            return CControl.EXTERNALIZED_STATION_ID;
        }
    };
    
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
	public CExternalizedLocation( int x, int y, int width, int height ){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public String findRoot(){
		return CControl.EXTERNALIZED_STATION_ID;
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
	
	/**
	 * Returns a location describing an element with the coordinates of this
	 * location but that was maximized.
	 * @return the maximized version of this location
	 */
	public CMaximalExternalizedLocation maximize(){
		return new CMaximalExternalizedLocation( x, y, width, height );
	}
	
	@Override
	public CLocation aside() {
	    return stack( 1 );
	}
	
	@Override
	public String toString() {
	    return "[externalized " + x + " " + y + " " + width + " " + height + "]";
	}
	
	/**
	 * Gets the left end of the element.
	 * @return the x coordinate
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Gets the top end of the element.
	 * @return the y coordinate
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Gets the width of the element.
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Gets the height of the element.
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
}
