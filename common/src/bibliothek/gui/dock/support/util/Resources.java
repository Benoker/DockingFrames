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

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import bibliothek.gui.dock.common.FControl;

/**
 * A set of resources available through the whole framework
 * @author Benjamin Sigg
 */
public class Resources {
    /** various text snippets */
    private static ResourceBundle bundle;
    
    /** the list of default-icons */
    private static Map<String, Icon> icons = new HashMap<String, Icon>();
    
    static{
        // read the localized text
        bundle = ResourceBundle.getBundle( 
                "data.bibliothek.gui.dock.locale.common", 
                Locale.getDefault(), FControl.class.getClassLoader() );
        
        // read the icons
        try{
            Properties properties = new Properties();
            InputStream in = Resources.class.getResourceAsStream( "/data/bibliothek/gui/dock/icons/icons.ini" );
            properties.load( in );
            in.close();
            
            ClassLoader loader = Resources.class.getClassLoader();
            for( Map.Entry<Object, Object> entry : properties.entrySet() ){
                ImageIcon icon = new ImageIcon( ImageIO.read( loader.getResource( "data/bibliothek/gui/dock/icons/" + entry.getValue()) ));
                icons.put( (String)entry.getKey(), icon );
            }
        }
        catch( IOException ex ){
            ex.printStackTrace();
        }
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
