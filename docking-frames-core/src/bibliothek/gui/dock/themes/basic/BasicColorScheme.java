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

import bibliothek.gui.DockUI;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.themes.color.DefaultColorScheme;
import bibliothek.gui.dock.util.laf.LookAndFeelColors;
import bibliothek.util.Colors;

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
    protected void updateUI(){
        setColor( "title.active.left", DockUI.getColor( LookAndFeelColors.TITLE_SELECTION_BACKGROUND ) );
        setColor( "title.inactive.left", DockUI.getColor( LookAndFeelColors.TITLE_BACKGROUND ));
        setColor( "title.disabled.left", DockUI.getColor( LookAndFeelColors.PANEL_BACKGROUND ));
        setColor( "title.active.right", DockUI.getColor( LookAndFeelColors.TITLE_SELECTION_BACKGROUND ));
        setColor( "title.inactive.right", DockUI.getColor( LookAndFeelColors.TITLE_BACKGROUND ));
        setColor( "title.disabled.right", DockUI.getColor( LookAndFeelColors.PANEL_BACKGROUND ));
        setColor( "title.active.text", DockUI.getColor( LookAndFeelColors.TITLE_SELECTION_FOREGROUND ));
        setColor( "title.inactive.text", DockUI.getColor( LookAndFeelColors.TITLE_FOREGROUND ));
        
        setColor( "title.station.active", DockUI.getColor( LookAndFeelColors.TITLE_SELECTION_BACKGROUND ));
        setColor( "title.station.active.text",  DockUI.getColor( LookAndFeelColors.TITLE_SELECTION_FOREGROUND ));
        setColor( "title.station.inactive",  DockUI.getColor( LookAndFeelColors.TITLE_BACKGROUND ));
        setColor( "title.station.inactive.text",  DockUI.getColor( LookAndFeelColors.TITLE_FOREGROUND ));
        setColor( "title.station.disabled", DockUI.getColor( LookAndFeelColors.PANEL_BACKGROUND ));
        
        setColor( "title.flap.active", DockUI.getColor( LookAndFeelColors.TITLE_SELECTION_BACKGROUND ) );
        setColor( "title.flap.active.text", DockUI.getColor( LookAndFeelColors.TITLE_SELECTION_FOREGROUND ) );
        setColor( "title.flap.active.knob.highlight", Colors.brighter( DockUI.getColor( LookAndFeelColors.TITLE_SELECTION_BACKGROUND ) ) );
		setColor( "title.flap.active.knob.shadow", Colors.darker( DockUI.getColor( LookAndFeelColors.TITLE_SELECTION_BACKGROUND )) );
        setColor( "title.flap.inactive", DockUI.getColor( LookAndFeelColors.TITLE_BACKGROUND ) );
        setColor( "title.flap.inactive.text", DockUI.getColor( LookAndFeelColors.TITLE_FOREGROUND ) );
        setColor( "title.flap.inactive.knob.highlight", Colors.brighter( DockUI.getColor( LookAndFeelColors.TITLE_BACKGROUND ) ) );
		setColor( "title.flap.inactive.knob.shadow", Colors.darker( DockUI.getColor( LookAndFeelColors.TITLE_BACKGROUND ) ) );
        setColor( "title.flap.selected", DockUI.getColor( LookAndFeelColors.TITLE_BACKGROUND ) );
        setColor( "title.flap.selected.text", DockUI.getColor( LookAndFeelColors.TITLE_FOREGROUND ) );
        setColor( "title.flap.selected.knob.highlight", Colors.brighter( DockUI.getColor( LookAndFeelColors.TITLE_BACKGROUND ) ) );
		setColor( "title.flap.selected.knob.shadow", Colors.darker( DockUI.getColor( LookAndFeelColors.TITLE_BACKGROUND ) ) );
        
		setColor( "stack.tab.foreground", null );
		setColor( "stack.tab.foreground.selected", null );
        setColor( "stack.tab.foreground.focused", null );
        setColor( "stack.tab.background", null );
        setColor( "stack.tab.background.selected", null );
        setColor( "stack.tab.background.focused", null );
		
        setColor( "station.screen.border.hover", DockUI.getColor( LookAndFeelColors.SELECTION ) );
        
        setColor( "paint", DockUI.getColor( LookAndFeelColors.SELECTION ));
        setColor( "paint.removal", DockUI.getColor( LookAndFeelColors.PANEL_BACKGROUND ));
    }
}
