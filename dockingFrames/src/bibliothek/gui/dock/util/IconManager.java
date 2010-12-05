/**
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

package bibliothek.gui.dock.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import bibliothek.gui.dock.event.IconManagerListener;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Version;

/**
 * A map of icons which are used by various objects. The icon is stored
 * under a given String-key. Every icon can be stored with three different
 * {@link Priority priorities}, only the icon with the highest priority
 * will be returned when {@link #getIcon(String)} is called. It is possible
 * to register a {@link IconManagerListener} which will receive an event
 * whenever an icon changes.
 * @author Benjamin Sigg
 * @see IconManagerListener
 */
@Todo( priority=Todo.Priority.ENHANCEMENT, compatibility=Compatibility.BREAK_MINOR, target=Version.VERSION_1_1_0,
		 description="IconManager extends UIManager, just like ColorManager and FontManager" )
public class IconManager {
    /** all icons known to this manager */
    private Map<String, Entry> icons = new HashMap<String, Entry>();
    
    /**
     * Searches or creates an entry for the given key.
     * @param key the key for which an entry is searched
     * @return the entry
     */
    private Entry getEntry( String key ){
        Entry entry = icons.get( key );
        if( entry == null ){
            entry = new Entry( key );
            icons.put( key, entry );
        }
        return entry;
    }
    
    /**
     * Stores an icon.
     * @param key the key of the icon
     * @param priority the priority, where {@link Priority#CLIENT} is the
     * highest, {@link Priority#DEFAULT} the lowest. Icons stored with the
     * same key but another priority are not deleted.
     * @param icon the icon, <code>null</code> if the icon should be removed
     */
    public void setIcon( String key, Priority priority, Icon icon ){
        getEntry( key ).setIcon( priority, icon );
    }

    /**
     * Sets an icon with default-priority.
     * @param key the key of the icon
     * @param icon the icon or <code>null</code>
     */
    public void setIconDefault( String key, Icon icon ){
        setIcon( key, Priority.DEFAULT, icon );
    }

    /**
     * Sets an icon with theme-priority.
     * @param key the key of the icon
     * @param icon the icon or <code>null</code>
     */
    public void setIconTheme( String key, Icon icon ){
        setIcon( key, Priority.THEME, icon );
    }
    
    /**
     * Sets an icon with client-priority.
     * @param key the key of the icon
     * @param icon the icon or <code>null</code>
     */
    public void setIconClient( String key, Icon icon ){
        setIcon( key, Priority.CLIENT, icon );
    }
    
    /**
     * Gets the icon under <code>key</code> with the highest priority.
     * @param key the key of the icon
     * @return the icon or <code>null</code>
     */
    public Icon getIcon( String key ){
        return getEntry( key ).getIcon();
    }
    
    /**
     * Adds a listener to this manager. The listener will be informed when
     * the icon under <code>key</code> has been changed.
     * @param key the key which is observed
     * @param listener the new listener
     */
    public void add( String key, IconManagerListener listener ){
        getEntry( key ).add( listener );
    }
    
    /**
     * Removes a listener from this manager.
     * @param key the observed key
     * @param listener the listener to remove
     */
    public void remove( String key, IconManagerListener listener ){
        getEntry( key ).remove( listener );
    }
    
    /**
     * Removes all icons which have the priority {@link Priority#THEME THEME}.
     */
    public void clearThemeIcons(){
        for( Entry entry : icons.values())
            entry.setIcon( Priority.THEME, null );
    }
    
    /**
     * A set of icons with different priority and a set of listeners.
     * @author Benjamin Sigg
     */
    private static class Entry extends PriorityValue<Icon>{
        private String key;
        private List<IconManagerListener> listeners = new ArrayList<IconManagerListener>();
        
        /**
         * Creates a new entry.
         * @param key the key of this entry
         */
        public Entry( String key ){
            this.key = key;
        }
        
        /**
         * Gets the current icon of this entry.
         * @return the icon with the highest priority
         */
        public Icon getIcon(){
            return get();
        }
        
        /**
         * Replaces the current icon of this entry.
         * @param priority the priority, where to store the icon
         * @param icon the new icon or <code>null</code>
         */
        public void setIcon( Priority priority, Icon icon ){
            Icon oldIcon = getIcon();
            set( priority, icon );
            Icon newIcon = getIcon();
            
            if( oldIcon != newIcon ){
                for( IconManagerListener listener : listeners.toArray( new IconManagerListener[ listeners.size() ] ))
                    listener.iconChanged( key, newIcon );
            }
        }
        
        /**
         * Adds a listener to this entry
         * @param listener the new listener
         */
        public void add( IconManagerListener listener ){
            if( listener == null )
                throw new IllegalArgumentException( "Listener must not be null" );
            listeners.add( listener );
        }
        
        /**
         * Removes a listener from this entry
         * @param listener the listener to remove
         */
        public void remove( IconManagerListener listener ){
            listeners.remove( listener );
        }
    }
}
