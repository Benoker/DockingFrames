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
import bibliothek.util.Colors;

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
        
        setColor( "title.flap.active", DockUI.getColor( LookAndFeelColors.TITLE_SELECTION_BACKGROUND ) );
        setColor( "title.flap.active.text", DockUI.getColor( LookAndFeelColors.TITLE_SELECTION_FOREGROUND ) );
        setColor( "title.flap.active.knob.highlight", Colors.brighter( DockUI.getColor( LookAndFeelColors.TITLE_SELECTION_BACKGROUND ) ) );
		setColor( "title.flap.active.knob.shadow", Colors.darker( DockUI.getColor( LookAndFeelColors.TITLE_SELECTION_BACKGROUND ) ) );
        setColor( "title.flap.inactive", DockUI.getColor( LookAndFeelColors.TITLE_BACKGROUND ) );
        setColor( "title.flap.inactive.text", DockUI.getColor( LookAndFeelColors.TITLE_FOREGROUND ) );
        setColor( "title.flap.inactive.knob.highlight", Colors.brighter( DockUI.getColor( LookAndFeelColors.TITLE_BACKGROUND ) ) );
		setColor( "title.flap.inactive.knob.shadow", Colors.darker( DockUI.getColor( LookAndFeelColors.TITLE_BACKGROUND ) ) );
        setColor( "title.flap.selected", DockUI.getColor( LookAndFeelColors.TITLE_BACKGROUND ) );
        setColor( "title.flap.selected.text", DockUI.getColor( LookAndFeelColors.TITLE_FOREGROUND ) );
        setColor( "title.flap.selected.knob.highlight", Colors.brighter( DockUI.getColor( LookAndFeelColors.TITLE_BACKGROUND ) ) );
		setColor( "title.flap.selected.knob.shadow", Colors.darker( DockUI.getColor( LookAndFeelColors.TITLE_BACKGROUND ) ) );
		
        setColor( "paint", Color.DARK_GRAY );
        setColor( "paint.insertion.area", Color.WHITE );
        setColor( "paint.removal", Color.GRAY );
        
        Color border = DockUI.getColor( LookAndFeelColors.PANEL_BACKGROUND );
        setColor( "stack.tab.border.center.selected", Colors.brighter( border ) );
        setColor( "stack.tab.border.center.focused", Colors.brighter( border ) );
        setColor( "stack.tab.border.center.disabled", Colors.brighter( border ) );
        setColor( "stack.tab.border.center", Colors.darker( border ) );
        setColor( "stack.tab.border", border );
                        
        setColor( "stack.tab.background.top.selected", Colors.diffMirror( border, 0.2 ) );
        setColor( "stack.tab.background.top.focused", Colors.diffMirror( border, 0.2 ) );
        setColor( "stack.tab.background.top.disabled", Colors.diffMirror( border, 0.1 ) );
        setColor( "stack.tab.background.top", border );
        setColor( "stack.tab.background.bottom.selected", Colors.diffMirror( border, 0.1 ) );
        setColor( "stack.tab.background.bottom.focused", Colors.diffMirror( border, 0.1 ) );
        setColor( "stack.tab.background.bottom.disabled", Colors.diffMirror( border, 0.1 ) );
        setColor( "stack.tab.background.bottom", border );
            
        setColor( "stack.tab.foreground", DockUI.getColor( LookAndFeelColors.PANEL_FOREGROUND ));
    }
}
