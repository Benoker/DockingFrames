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
package bibliothek.gui.dock.themes.basic;

import javax.swing.UIManager;

import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.themes.color.DefaultColorScheme;

/**
 * The {@link ColorScheme} that is used by the {@link BasicTheme}.
 * @author Benjamin Sigg
 */
public class BasicColorScheme extends DefaultColorScheme{
    /**
     * Creates a new color scheme
     */
    public BasicColorScheme(){
        updateUI();
    }
    
    @Override
    public boolean updateUI(){
        setColor( "title.active.left", UIManager.getColor( "MenuItem.selectionBackground") );
        setColor( "title.inactive.left", UIManager.getColor( "MenuItem.background" ) );
        setColor( "title.active.right", UIManager.getColor( "MenuItem.selectionBackground") );
        setColor( "title.inactive.right", UIManager.getColor( "MenuItem.background") );
        setColor( "title.active.text", UIManager.getColor( "MenuItem.selectionForeground") );
        setColor( "title.inactive.text", UIManager.getColor( "MenuItem.foreground") );
        
        setColor( "paint", UIManager.getColor( "TextField.selectionBackground" ) );
        
        return true;
    }
}
