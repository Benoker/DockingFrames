/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.station.screen;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.dock.AbstractDockableProperty;
import bibliothek.gui.dock.station.ScreenDockStation;

/**
 * This property is used on {@link ScreenDockStation ScreenDockStations}
 * to determine the bounds of the {@link ScreenDockDialog dialogs}.
 * @author Benjamin Sigg
 */
public class ScreenDockProperty extends AbstractDockableProperty {
    private int x, y, width, height;
    
    public String getFactoryID() {
        return ScreenDockPropertyFactory.ID;
    }
    
    /**
     * Constructs a new property
     */
    public ScreenDockProperty(){
    	// do nothing
    }
    
    /**
     * Constructs a new property
     * @param x the x-coordinate of the dialog
     * @param y the y-coordinate of the dialog
     * @param width the width of the dialog
     * @param height the height of the dialog
     */
    public ScreenDockProperty( int x, int y, int width, int height ){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void store( DataOutputStream out ) throws IOException {
        out.writeInt( x );
        out.writeInt( y );
        out.writeInt( width );
        out.writeInt( height );
    }

    public void load( DataInputStream in ) throws IOException {
        x = in.readInt();
        y = in.readInt();
        width = in.readInt();
        height = in.readInt();
    }

    /**
     * Gets the height of the dialog.
     * @return the height
     * @see #setHeight(int)
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height of the dialog.
     * @param height the height
     */
    public void setHeight( int height ) {
        this.height = height;
    }

    /**
     * Gets the width of the dialog.
     * @return the width
     * @see #setWidth(int)
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the width of the dialog.
     * @param width the new width
     */
    public void setWidth( int width ) {
        this.width = width;
    }

    /**
     * Gets the x-coordinate of the dialog.
     * @return the x-coordinate
     * @see #setX(int)
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the location of the left size of the dialog.
     * @param x the x-coordinate
     */
    public void setX( int x ) {
        this.x = x;
    }

    /**
     * Gets the y-coordinate of the dialog
     * @return the y-coordinate
     * @see #setY(int)
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the location of the top side of the dialog.
     * @param y the y-coordinate
     */
    public void setY( int y ) {
        this.y = y;
    }
}
