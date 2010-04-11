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

import bibliothek.extension.gui.dock.preference.preferences.choice.DefaultChoice;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.common.layout.ThemeMap;
import bibliothek.gui.dock.themes.ThemeFactory;

/**
 * A choice offering all the {@link ThemeFactory}s a {@link ThemeMap} provides.
 * @author Benjamin Sigg
 */
public class ThemeChoice extends DefaultChoice<ThemeFactory>{
    /**
     * Creates a new set of choices.
     * @param themes the map to read
     * @param controller the controller in whose realm this choice is required, can be <code>null</code>
     */
    public ThemeChoice( ThemeMap themes, DockController controller ){
    	super( controller );
    	
        setNullEntryAllowed( false );
        
        for( int i = 0, n = themes.size(); i<n; i++ ){
            ThemeFactory factory = themes.getFactory( i );
            add( themes.getKey( i ), factory.getName(), factory );
        }
    }
}
