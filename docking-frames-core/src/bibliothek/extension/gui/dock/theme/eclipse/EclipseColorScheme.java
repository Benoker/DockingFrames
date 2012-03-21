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
package bibliothek.extension.gui.dock.theme.eclipse;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.rex.RexSystemColor;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.themes.basic.BasicColorScheme;
import bibliothek.gui.dock.util.laf.LookAndFeelColors;
import bibliothek.util.Colors;

/**
 * A {@link ColorScheme} used by the {@link EclipseTheme}.
 * @author Benjamin Sigg
 */
public class EclipseColorScheme extends BasicColorScheme {
    /**
     * Creates the new color scheme
     */
    public EclipseColorScheme(){
        updateUI();
    }
    
    @Override
    protected void updateUI(){
        super.updateUI();
        
        setColor( "stack.tab.border",                   DockUI.getColor( LookAndFeelColors.PANEL_BACKGROUND ) );
        setColor( "stack.tab.border.selected",          RexSystemColor.getInactiveColorGradient() );
        setColor( "stack.tab.border.selected.focused",  RexSystemColor.getActiveColorGradient() );
        setColor( "stack.tab.border.selected.focuslost",RexSystemColor.getInactiveColor() );
        setColor( "stack.tab.border.disabled",          Colors.brighter( DockUI.getColor( LookAndFeelColors.PANEL_BACKGROUND ) ));
        
        setColor( "stack.tab.top",                      DockUI.getColor( LookAndFeelColors.PANEL_BACKGROUND ) );
        setColor( "stack.tab.top.disabled",             DockUI.getColor( LookAndFeelColors.PANEL_BACKGROUND ) );
        setColor( "stack.tab.top.selected",             RexSystemColor.getInactiveColor() );
        setColor( "stack.tab.top.selected.focused",     RexSystemColor.getActiveColor() );
        setColor( "stack.tab.top.selected.focuslost",   RexSystemColor.getInactiveColor() );
        setColor( "stack.tab.top.disabled",             Colors.brighter( DockUI.getColor( LookAndFeelColors.PANEL_BACKGROUND ) ));
        
        setColor( "stack.tab.bottom",                   DockUI.getColor( LookAndFeelColors.PANEL_BACKGROUND ) );
        setColor( "stack.tab.bottom.disabled",          DockUI.getColor( LookAndFeelColors.PANEL_BACKGROUND ) );
        setColor( "stack.tab.bottom.selected",          RexSystemColor.getInactiveColorGradient() );
        setColor( "stack.tab.bottom.selected.focused",  RexSystemColor.getActiveColorGradient() );
        setColor( "stack.tab.bottom.selected.focuslost",RexSystemColor.getInactiveColor() );
        setColor( "stack.tab.bottom.disabled",          Colors.brighter( DockUI.getColor( LookAndFeelColors.PANEL_BACKGROUND ) ));
        
        setColor( "stack.tab.text",                     DockUI.getColor( LookAndFeelColors.PANEL_FOREGROUND ) );
        setColor( "stack.tab.text.selected",            RexSystemColor.getInactiveTextColor() );
        setColor( "stack.tab.text.selected.focused",    RexSystemColor.getActiveTextColor() );
        setColor( "stack.tab.text.selected.focuslost",  RexSystemColor.getInactiveTextColor() );
        setColor( "stack.tab.text.disabled",            DockUI.getColor( LookAndFeelColors.PANEL_FOREGROUND ) );
        
        setColor( "stack.border",                       RexSystemColor.getBorderColor() );
        setColor( "stack.border.edges", 				DockUI.getColor( LookAndFeelColors.PANEL_BACKGROUND ) );
        
        setColor( "flap.button.border.inner", 			Colors.brighter( RexSystemColor.getBorderColor(), 0.7 ) );
        setColor( "flap.button.border.outer", 			RexSystemColor.getBorderColor() );
        setColor( "flap.button.border.edge", 			DockUI.getColor( LookAndFeelColors.PANEL_BACKGROUND ) );
        
        setColor( "selection.border",                   RexSystemColor.getBorderColor() );         
    }
}
