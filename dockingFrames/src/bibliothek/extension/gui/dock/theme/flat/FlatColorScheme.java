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
package bibliothek.extension.gui.dock.theme.flat;

import java.awt.Color;

import javax.swing.UIManager;

import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.gui.dock.themes.color.DefaultColorScheme;

/**
 * A color scheme for {@link FlatTheme}.
 * @author Benjamin Sigg
 */
public class FlatColorScheme extends DefaultColorScheme {
    public FlatColorScheme(){
        updateUI();
    }
    
    @Override
    public boolean updateUI(){
        setColor( "title.active.left", UIManager.getColor( "MenuItem.selectionBackground") );
        setColor( "title.inactive.left", UIManager.getColor( "MenuItem.background" ) );
        setColor( "title.active.right", UIManager.getColor( "Panel.background") );
        setColor( "title.inactive.right", UIManager.getColor( "Panel.background") );
        setColor( "title.active.text", UIManager.getColor( "MenuItem.selectionForeground") );
        setColor( "title.inactive.text", UIManager.getColor( "MenuItem.foreground") );
        
        setColor( "paint", Color.DARK_GRAY );
        setColor( "paint.insertion.area", Color.WHITE );
        
        Color border = UIManager.getColor( "Panel.background" );
        setColor( "stack.tab.border.center.selected", border.brighter() );
        setColor( "stack.tab.border.center.focused", border.brighter() );
        setColor( "stack.tab.border.center", border.darker() );
        setColor( "stack.tab.border", border );
                        
        setColor( "stack.tab.background.top.selected", border.brighter() );
        setColor( "stack.tab.background.top.focused", border.brighter() );
        setColor( "stack.tab.background", border );
            
        setColor( "stack.tab.foreground", UIManager.getColor( "Panel.foreground" ));
        
        return true;
    }
}
