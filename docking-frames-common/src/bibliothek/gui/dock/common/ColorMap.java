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
package bibliothek.gui.dock.common;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.dock.common.event.ColorMapListener;
import bibliothek.gui.dock.common.intern.CDockable;

/**
 * A map containing several {@link Color}s. A <code>ColorMap</code> is
 * associated with exactly one {@link CDockable}. The colors in the map are
 * used to change the standard colors that normally used for example to paint
 * a title or to draw a tab. The entries of this map can be changed at any
 * time. Changes of any entry in this map will be forwarded instantaneously
 * to the element that uses the entry.
 * @author Benjamin Sigg
 */
public class ColorMap {
    /** a key for colors that are used on unselected tabs as background */
    public static final String COLOR_KEY_TAB_BACKGROUND = "tab.background";
    /** a key for colors that are used on unselected tabs as foreground */
    public static final String COLOR_KEY_TAB_FOREGROUND = "tab.foreground";
    
    /** a key for colors that are used on selected, but not focused, tabs as background */
    public static final String COLOR_KEY_TAB_BACKGROUND_SELECTED = "tab.background.selected";
    /** a key for colors that are used on selected, but not focused, tabs as foreground */
    public static final String COLOR_KEY_TAB_FOREGROUND_SELECTED = "tab.foreground.selected";
    
    /** a key for colors that are used on focused tabs as background */
    public static final String COLOR_KEY_TAB_BACKGROUND_FOCUSED = "tab.background.focused";
    /** a key for colors that are used on focused tabs a foreground */ 
    public static final String COLOR_KEY_TAB_FOREGROUND_FOCUSED = "tab.foreground.focused";
    
    /** a key for colors that are used on titles as background */
    public static final String COLOR_KEY_TITLE_BACKGROUND = "title.background";
    /** a key for colors that are used on titles as foreground */
    public static final String COLOR_KEY_TITLE_FOREGROUND = "title.foreground";
    
    /** a key for colors that are used on focused titles as background */
    public static final String COLOR_KEY_TITLE_BACKGROUND_FOCUSED = "title.background.focused";
    /** a key for colors that are used on focused titles as foreground */
    public static final String COLOR_KEY_TITLE_FOREGROUND_FOCUSED = "title.foreground.focused";
    
    /** a key for colors that are used on the button of the minimized-area as background */
    public static final String COLOR_KEY_MINIMIZED_BUTTON_BACKGROUND = "minimized.background";
    /** a key for colors that are used on the button of the minimized-area as foreground */
    public static final String COLOR_KEY_MINIMIZED_BUTTON_FOREGROUND = "minimized.foreground";
    
    /** a key for colors that are used on the button of the minimized-area as background if focused */
    public static final String COLOR_KEY_MINIMIZED_BUTTON_BACKGROUND_FOCUSED = "minimized.background.focused";
    /** a key for colors that are used on the button of the minimized-area as foreground if focused */
    public static final String COLOR_KEY_MINIMIZED_BUTTON_FOREGROUND_FOCUSED = "minimized.foreground.focused";

    /** a key for colors that are used on the button of the minimized-area as background if selected */
    public static final String COLOR_KEY_MINIMIZED_BUTTON_BACKGROUND_SELECTED = "minimized.background.selected";
    /** a key for colors that are used on the button of the minimized-area as foreground if selected */
    public static final String COLOR_KEY_MINIMIZED_BUTTON_FOREGROUND_SELECTED = "minimized.foreground.selected";
    
    /** the list of observers of this map */
    private List<ColorMapListener> listeners = new ArrayList<ColorMapListener>();
    
    /** the map of colors */
    private Map<String, Color> colors = new HashMap<String, Color>();
    
    /** the owner of this map */
    private CDockable dockable;
    
    /**
     * Creates a new map.
     * @param dockable the owner of this map
     */
    public ColorMap( CDockable dockable ){
        if( dockable == null )
            throw new IllegalArgumentException( "dockable must not be null" );
        this.dockable = dockable;
    }
    
    /**
     * Gets the owner of this map.
     * @return the owner
     */
    public CDockable getDockable() {
        return dockable;
    }
    
    /**
     * Adds a listener to this map, the listener will be informed whenever
     * a color of this map changes.
     * @param listener the new listener
     */
    public void addListener( ColorMapListener listener ){
        listeners.add( listener );
    }
    
    /**
     * Removes a listener from this map.
     * @param listener the listener to remove
     */
    public void removeListener( ColorMapListener listener ){
        listeners.remove( listener );
    }

    /**
     * Returns a color that was stored in this map. 
     * @param key the name of the color
     * @return the color or <code>null</code>
     */
    public Color getColor( String key ){
        return colors.get( key );
    }
    
    /**
     * Sets a color in this map.
     * @param key the name of the color
     * @param color the new color, can be <code>null</code> to return to
     * the default color
     */
    public void setColor( String key, Color color ){
        Color old;
        if( color == null )
            old = colors.remove( key );
        else
            old = colors.put( key, color );
        
        if( (old == null && color != null) || (old != null && !old.equals( color )) ){
            for( ColorMapListener listener : listeners.toArray( new ColorMapListener[ listeners.size() ] ))
                listener.colorChanged( this, key, color );
        }
    }
}
