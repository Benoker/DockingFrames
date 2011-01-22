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

import java.util.ResourceBundle;

import bibliothek.gui.DockUI;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.util.TextManager;
import bibliothek.gui.dock.util.local.LocaleListener;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A set of resources available through the whole framework
 * @author Benjamin Sigg
 * @deprecated replaced by the {@link TextManager}
 */
@Deprecated
@Todo(compatibility=Compatibility.BREAK_MAJOR, priority=Priority.MAJOR, target=Version.VERSION_1_1_0,
		description="Find a mechanism that allows clients to easily change the text of any element, perhaps with an UIManager")
public class Resources {
    /** various text snippets */
    private static ResourceBundle bundle;
    
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
}
