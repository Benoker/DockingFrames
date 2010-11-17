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

import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.themes.color.DefaultColorScheme;
import bibliothek.gui.dock.util.laf.LookAndFeelColors;

/**
 * A color scheme for {@link FlatTheme}.
 * @author Benjamin Sigg
 */
public class FlatColorScheme extends DefaultColorScheme {
    public FlatColorScheme(){
        updateUI();
    }
    
    @Override
    protected void updateUI(){
        setColor( "title.active.left", DockUI.getColor( LookAndFeelColors.TITLE_SELECTION_BACKGROUND ));
        setColor( "title.inactive.left", DockUI.getColor( LookAndFeelColors.TITLE_BACKGROUND ) );
        setColor( "title.active.right", DockUI.getColor( LookAndFeelColors.PANEL_BACKGROUND ) );
        setColor( "title.inactive.right", DockUI.getColor( LookAndFeelColors.PANEL_BACKGROUND ) );
        setColor( "title.active.text", DockUI.getColor( LookAndFeelColors.TITLE_SELECTION_FOREGROUND ) );
        setColor( "title.inactive.text", DockUI.getColor( LookAndFeelColors.TITLE_FOREGROUND ) );
        
        setColor( "paint", Color.DARK_GRAY );
        setColor( "paint.insertion.area", Color.WHITE );
        
        Color border = DockUI.getColor( LookAndFeelColors.PANEL_BACKGROUND );
        setColor( "stack.tab.border.center.selected", border.brighter() );
        setColor( "stack.tab.border.center.focused", border.brighter() );
        setColor( "stack.tab.border.center", border.darker() );
        setColor( "stack.tab.border", border );
                        
        setColor( "stack.tab.background.top.selected", border.brighter() );
        setColor( "stack.tab.background.top.focused", border.brighter() );
        setColor( "stack.tab.background", border );
            
        setColor( "stack.tab.foreground", DockUI.getColor( LookAndFeelColors.PANEL_FOREGROUND ));
        
        setColor( "stack.menu.edge", null );
        setColor( "stack.menu.middle", null );
        setColor( "stack.menu.edge.selected", border.darker() );
        setColor( "stack.menu.middle.selected", border.brighter() );
    }
}
