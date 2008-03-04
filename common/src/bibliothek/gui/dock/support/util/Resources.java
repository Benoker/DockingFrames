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
package bibliothek.gui.dock.support.util;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.Icon;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A set of resources available through the whole framework
 * @author Benjamin Sigg
 */
public class Resources {
    /** various text snippets */
    private static ResourceBundle bundle;
    
    /** the list of default-icons */
    private static Map<String, Icon> icons;
    
    static{
        // read the localized text
        bundle = ResourceBundle.getBundle( 
                "data.bibliothek.gui.dock.locale.common", 
                Locale.getDefault(), CControl.class.getClassLoader() );
        
        // read the icons
        icons = DockUtilities.loadIcons( "data/bibliothek/gui/dock/icons/icons.ini",
                "data/bibliothek/gui/dock/icons/", Resources.class.getClassLoader() );
    }
    
    /**
     * Gets localized text snippets. 
     * @return the text
     */
    public static ResourceBundle getBundle() {
        return bundle;
    }
    
    /**
     * Searches an icon that was stored with the given key. The keys
     * can be found in the file <code>bibliothek/gui/dock/icons/icons.ini</code>.
     * @param key the name of an icon
     * @return the icon or <code>null</code>
     */
    public static Icon getIcon( String key ){
        return icons.get( key );
    }
}
