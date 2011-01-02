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

package bibliothek.gui.dock.util;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.util.icon.DefaultIconScheme;
import bibliothek.gui.dock.util.icon.DockIconBridge;
import bibliothek.gui.dock.util.icon.DockIcon;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Version;

/**
 * A map of icons which are used by various objects. The icon is stored
 * under a given String-key. Every icon can be stored with three different
 * {@link Priority priorities}, only the icon with the highest priority
 * will be returned.<br>
 * Clients have to add an {@link DockIcon} in order to read values from this
 * manager.
 * @author Benjamin Sigg
 */
public class IconManager extends UIProperties<Icon, DockIcon, DockIconBridge>{
    /**
     * Creates a new {@link IconManager}.
     * @param controller the owner of this map
     */
    public IconManager( DockController controller ){
    	super( controller );
    	
    	@Todo( compatibility=Compatibility.BREAK_MINOR, priority=Todo.Priority.MAJOR, target=Version.VERSION_1_1_0,
    			description="load these icons somewhere else, for example in the DockControllerFactory")
    	DefaultIconScheme scheme = new DefaultIconScheme( "data/icons.ini", controller );
    	scheme.link( PropertyKey.DOCKABLE_ICON, "dockable.default" );
    	scheme.link( PropertyKey.DOCK_STATION_ICON, "dockStation.default" );
    	setScheme( Priority.DEFAULT, scheme );
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
     	put( priority, key, icon );
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
}
