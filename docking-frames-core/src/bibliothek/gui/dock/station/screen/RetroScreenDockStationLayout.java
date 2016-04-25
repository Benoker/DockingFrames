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

import java.util.ArrayList;
import java.util.List;

/**
 * Old version of {@link ScreenDockStationLayout}, kept for backwards compatibility. Should not 
 * be used by clients.
 * @author Benjamin Sigg
 */
public class RetroScreenDockStationLayout extends ScreenDockStationLayout {
	private List<Entry> entries = new ArrayList<Entry>();

    /**
     * Adds a new entry to this layout.
     * @param id the id of the entry
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width
     * @param height the height
     */
    public void add( int id, int x, int y, int width, int height ){
        Entry entry = new Entry();
        entry.id = id;
        entry.x = x;
        entry.y = y;
        entry.width = width;
        entry.height = height;
        entries.add( entry );
    }
    
    /**
     * Gets the number of entries in this layout.
     * @return the number of entries
     */
    public int size(){
        return entries.size();
    }
    
    /**
     * Gets the id of the index'th entry.
     * @param index the index of the entry
     * @return the id
     */
    public int id( int index ){
        return entries.get( index ).id;
    }

    /**
     * Gets the x coordinate of the index'th entry.
     * @param index the index of the entry
     * @return the coordinate
     */
    public int x( int index ){
        return entries.get( index ).x;
    }
    
    /**
     * Gets the y coordinate of the index'th entry.
     * @param index the index of the entry
     * @return the coordinate
     */
    public int y( int index ){
        return entries.get( index ).y;
    }
    
    /**
     * Gets the width of the index'th entry.
     * @param index the index of the entry
     * @return the width
     */
    public int width( int index ){
        return entries.get( index ).width;
    }
    
    /**
     * Gets the height of the index'th entry.
     * @param index the index of the entry
     * @return the height
     */
    public int height( int index ){
        return entries.get( index ).height;
    }
    
    /**
     * An entry of this layout
     * @author Benjamin Sigg
     */
    private static class Entry{
        /** the id of the entry */
        public int id;
        
        /** x-coordinate */
        public int x;
        /** y-coordinate */
        public int y;
        /** width in pixels */
        public int width;
        /** height in pixels */
        public int height;
    }
}
