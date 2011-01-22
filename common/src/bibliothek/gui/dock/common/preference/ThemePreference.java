/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.common.preference;

import bibliothek.extension.gui.dock.preference.DefaultPreference;
import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.util.Path;

/**
 * A preference allowing the user to select one {@link DockTheme}. This preference
 * uses a {@link ThemeMap} to read and store its content.
 * @author Benjamin Sigg
 */
public class ThemePreference extends DefaultPreference<String>{
    private ThemeMap themes;
    private DockController controller;
    
    /**
     * Creates a new preference.
     * @param properties the default settings
     * @param themes a list of themes to show
     */
    public ThemePreference( DockProperties properties, ThemeMap themes ){
        super( Path.TYPE_STRING_CHOICE_PATH, new Path( "dock.theme" ));
        this.themes = themes;
        this.controller = properties.getController();
        
        setValueInfo( new ThemeChoice( themes, controller ) );
        
        setLabelId( "preference.layout.theme.label" );
        setDescriptionId( "preference.layout.theme.description" );
        
        setNatural( true );
    }
    
    public void read() {
        setValueInfo( new ThemeChoice( themes, controller ) );
        setValue( themes.getSelectedKey() );
    }
    
    public void write() {
        themes.select( getValue() );
    }
}
