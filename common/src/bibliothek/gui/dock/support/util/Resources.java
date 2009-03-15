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

import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.Icon;

import bibliothek.gui.DockUI;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.local.LocaleListener;

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
        updateBundle();
        
        DockUI.getDefaultDockUI().addLocaleListener( new LocaleListener(){
        	public void localeChanged( DockUI ui ){
        		updateBundle();
        	}
        	public void bundleChanged( DockUI ui ){
        		// ignore
        	}
        });
        
        // read the icons
        icons = DockUtilities.loadIcons( "data/bibliothek/gui/dock/icons/icons.ini",
                "data/bibliothek/gui/dock/icons/", Resources.class.getClassLoader() );
    }
    
    private static void updateBundle(){
    	bundle = ResourceBundle.getBundle( 
                "data.bibliothek.gui.dock.locale.common", 
                DockUI.getDefaultDockUI().getLocale(),
                CControl.class.getClassLoader() );
    }
    
    /**
     * Gets localized texts. 
     * @return the text
     */
    public static ResourceBundle getBundle() {
        return bundle;
    }
    
    /**
     * Gets a localized text.
     * @param key the key for the text
     * @return the text
     */
    public static String getString( String key ){
    	return bundle.getString( key );
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
