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
package bibliothek.extension.gui.dock.theme.bubble;

import java.awt.Color;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.dock.themes.color.DefaultColorScheme;

/**
 * The standard color scheme for a {@link BubbleTheme}
 * @author Benjamin Sigg
 */
public class BubbleColorScheme extends DefaultColorScheme {
    /**
     * Creates a new color scheme
     */
    public BubbleColorScheme(){
        // stack
        setColor( "stack.tab.border.seleced",                   new Color( 150, 0, 0 ) );
        setColor( "stack.tab.border.selected.mouse",            new Color( 200, 100, 100 ) );
        setColor( "stack.tab.border.focused",                   new Color( 200, 0, 0 ) );
        setColor( "stack.tab.border.focused.mouse",             new Color( 255, 150, 150 ) );
        setColor( "stack.tab.border",                           new Color( 100, 100, 100 ) );
        setColor( "stack.tab.border.mouse",                     new Color( 100, 175, 100 ) );
        setColor( "stack.tab.background.top.selected",          new Color( 200, 0, 0 ) );
        setColor( "stack.tab.background.top.selected.mouse",    new Color( 255, 100, 100 ) );
        setColor( "stack.tab.background.top.focused",           new Color( 200, 100, 100 ) );
        setColor( "stack.tab.background.top.focused.mouse",     new Color( 255, 200, 200 ) );
        setColor( "stack.tab.background.top",                   new Color( 150, 150, 150 ) );
        setColor( "stack.tab.background.top.mouse",             new Color( 150, 255, 150 ) );
        setColor( "stack.tab.background.bottom.selected",       new Color( 255, 100, 100 ) );
        setColor( "stack.tab.background.bottom.selected.mouse", new Color( 255, 200, 200 ) );
        setColor( "stack.tab.background.bottom.focused",        new Color( 255, 200, 200 ) );
        setColor( "stack.tab.background.bottom.focused.mouse",  new Color( 255, 255, 255 ) );
        setColor( "stack.tab.background.bottom",                new Color( 200, 200, 200 ) );
        setColor( "stack.tab.background.bottom.mouse",          new Color( 220, 255, 220 ) );
        setColor( "stack.tab.foreground.selected",              new Color( 0, 0, 0 ));
        setColor( "stack.tab.foreground.selected.mouse",        new Color( 0, 0, 0 ));
        setColor( "stack.tab.foreground.focused",               new Color( 0, 0, 0 ));
        setColor( "stack.tab.foreground.focused.mouse",         new Color( 0, 0, 0 ));
        setColor( "stack.tab.foreground",                       new Color( 100, 100, 100 ));
        setColor( "stack.tab.foreground.mouse",                 new Color( 25, 25, 25 ));
        
        
        // title
        setColor( "title.background.top.active",               new Color( 200, 0, 0 ) );
        setColor( "title.background.top.active.mouse",         new Color( 255, 100, 100 ) );
        setColor( "title.background.top.inactive",             new Color( 150, 150, 150 ) );
        setColor( "title.background.top.inactive.mouse",       new Color( 150, 255, 150 ) );
        setColor( "title.background.bottom.active",            new Color( 255, 100, 100 ) );
        setColor( "title.background.bottom.active.mouse",      new Color( 255, 200, 200 ) );
        setColor( "title.background.bottom.inactive",          new Color( 200, 200, 200 ) );
        setColor( "title.background.bottom.inactive.mouse",    new Color( 220, 255, 220 ) );
        setColor( "title.foreground.active",              new Color( 0, 0, 0 ));
        setColor( "title.foreground.active.mouse",        new Color( 0, 0, 0 ));
        setColor( "title.foreground.inactive",            new Color( 100, 100, 100 ));
        setColor( "title.foreground.inactive.mouse",      new Color( 25, 25, 25 ));
        
        // display border
        setColor( "displayer.border.high.active",           new Color( 255, 100, 100 ));
        setColor( "displayer.border.high.inactive",         new Color( 200, 200, 200 ));
        setColor( "displayer.border.low.active",            new Color( 200, 100, 100 ));
        setColor( "displayer.border.low.inactive",          new Color( 100, 100, 100 ));
        
        // RoundButton
        setColor( "action.button",                                 new Color( 255, 255, 255 ));
        setColor( "action.button.enabled",                         new Color( 215, 215, 215 ));
        setColor( "action.button.selected",                        new Color( 200, 200, 255 ));
        setColor( "action.button.selected.enabled",                new Color( 150, 150, 210 ));
        setColor( "action.button.mouse.enabled",                   new Color( 255, 255, 100 ));
        setColor( "action.button.mouse.selected.enabled",          new Color( 100, 100, 255 ));
        setColor( "action.button.pressed.enabled",                 new Color( 255, 255, 0 ));
        setColor( "action.button.pressed.selected.enabled",        new Color( 0, 0, 255 ));

        // Round drop down button
        setColor( "action.dropdown",                                 new Color( 255, 255, 255 ));
        setColor( "action.dropdown.enabled",                         new Color( 215, 215, 215 ));
        setColor( "action.dropdown.selected",                        new Color( 200, 200, 255 ));
        setColor( "action.dropdown.selected.enabled",                new Color( 150, 150, 210 ));
        setColor( "action.dropdown.mouse.enabled",                   new Color( 255, 255, 100 ));
        setColor( "action.dropdown.mouse.selected.enabled",          new Color( 100, 100, 255 ));
        setColor( "action.dropdown.pressed.enabled",                 new Color( 255, 255, 0 ));
        setColor( "action.dropdown.pressed.selected.enabled",        new Color( 0, 0, 255 ));
        
        setColor( "action.dropdown.line",                            new Color( 150, 150, 150 ));
        setColor( "action.dropdown.line.enabled",                    new Color( 150, 150, 150 ));
        setColor( "action.dropdown.line.selected",                   new Color( 150, 150, 200 ));
        setColor( "action.dropdown.line.selected.enabled",           new Color( 120, 120, 175 ));
        setColor( "action.dropdown.line.mouse.enabled",              new Color( 200, 200, 100 ));
        setColor( "action.dropdown.line.mouse.selected.enabled",     new Color( 50, 50, 150 ));
        setColor( "action.dropdown.line.pressed.enabled",            new Color( 200, 200, 0 ));
        setColor( "action.dropdown.line.pressed.selected.enabled",   new Color( 0, 0, 200 ));
        
        // Paint
        setColor( "paint.divider",                            new Color( 0, 0, 0 ));
        setColor( "paint.insertion",                          new Color( 255, 0, 0 ));
        setColor( "paint.line",                               new Color( 0, 0, 0 ));
    }
}
