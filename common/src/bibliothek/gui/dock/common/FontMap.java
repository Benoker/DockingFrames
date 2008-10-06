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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.dock.common.event.FontMapListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.util.font.FontModifier;

/**
 * A map containing {@link FontModifier}s. Each <code>FontMap</code> is associated
 * with exactly one {@link CDockable}. This map is used to set the font
 * of various elements like titles or tabs. Changes in the map will
 * immediately be forwarded and applied.
 * @author Benjamin Sigg
 */
public class FontMap {
    /** key for font used in titles */
    public static final String FONT_KEY_TITLE = "dock.title";
    
    /** key for font used in titles if the title is focused */
    public static final String FONT_KEY_TITLE_FOCUSED = "dock.title.focused";
    
    /** key for font used on the button for a minimized dokable */
    public static final String FONT_KEY_MINIMIZED_BUTTON = "dock.minimized";
    
    /** key for font used on the focused button for a minimized dokable */
    public static final String FONT_KEY_MINIMIZED_BUTTON_FOCUSED = "dock.minimized.focused";
    
    /** the map of fonts associated with {@link #dockable} */
    private Map<String, FontModifier> fonts = new HashMap<String, FontModifier>();
    
    /** listeners to be informed when {@link #fonts} changes */
    private List<FontMapListener> listeners = new ArrayList<FontMapListener>();
    
    /** the element for which this map is used */
    private CDockable dockable;
    
    /**
     * Creates a new map
     * @param dockable the owner of this map
     */
    public FontMap( CDockable dockable ){
        if( dockable == null )
            throw new IllegalArgumentException( "Dockable must not be null" );
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
     * Adds a listener to this map.
     * @param listener the new listener
     */
    public void addListener( FontMapListener listener ){
        if( listener == null )
            throw new NullPointerException( "listener must not be null" );
        listeners.add( listener );
    }
    
    /**
     * Removes <code>listener</code> from this map.
     * @param listener the listener to remove
     */
    public void removeListener( FontMapListener listener ){
        listeners.remove( listener );
    }
    
    /**
     * Gets the font which is associated with <code>key</code>.
     * @param key the key of the font
     * @return the font or <code>null</code>
     */
    public FontModifier getFont( String key ){
        return fonts.get( key );
    }
    
    /**
     * Sets the font for <code>key</code>. 
     * @param key the key of the font
     * @param font the new value or <code>null</code> to set
     * the default value
     */
    public void setFont( String key, FontModifier font ){
        FontModifier old;
        if( font == null )
            old = fonts.remove( key );
        else
            old = fonts.put( key, font );
        
        if( (old == null && font != null) || (old != null && !old.equals( font )) ){
            for( FontMapListener listener : listeners.toArray( new FontMapListener[ listeners.size() ] ))
                listener.fontChanged( this, key, font );
        }
    }
}
