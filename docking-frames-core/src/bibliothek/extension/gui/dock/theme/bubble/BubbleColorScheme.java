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
     * Tells how the primary, secondary and tertiary color components are
     * distributed.
     * @author Benjamin Sigg
     */
    public static enum Distribution{
        /**
         * primary: red<br>
         * secondary: green<br>
         * tertiary: blue
         */
        RGB, 

        /**
         * primary: red<br>
         * secondary: blue<br>
         * tertiary: green
         */
        RBG,
        
        /**
         * primary: blue<br>
         * secondary: red<br>
         * tertiary: green
         */
        BRG,
        
        /**
         * primary: blue<br>
         * secondary: green<br>
         * tertiary: red
         */
        BGR,
        
        /**
         * primary: green<br>
         * secondary: red<br>
         * tertiary: blue
         */
        GRB,
        
        /**
         * primary: green<br>
         * secondary: blue<br>
         * tertiary: red
         */
        GBR
    }
    
    /**
     * Creates a new color scheme
     */
    public BubbleColorScheme(){
        this( Distribution.RGB );
    }
        
    /**
     * Creates a new color scheme
     * @param distribution how to put up the colors
     */
    public BubbleColorScheme( Distribution distribution ){
        // stack
        setColor( "stack.tab.border.seleced",                   color( distribution, 150, 0, 0 ) );
        setColor( "stack.tab.border.selected.mouse",            color( distribution, 200, 100, 100 ) );
        setColor( "stack.tab.border.focused",                   color( distribution, 200, 0, 0 ) );
        setColor( "stack.tab.border.focused.mouse",             color( distribution, 255, 150, 150 ) );
        setColor( "stack.tab.border",                           color( distribution, 100, 100, 100 ) );
        setColor( "stack.tab.border.disabled",                  color( distribution, 150, 150, 150 ) );
        setColor( "stack.tab.border.mouse",                     color( distribution, 100, 175, 100 ) );
        setColor( "stack.tab.background.top.selected",          color( distribution, 160, 150, 150 ) );
        setColor( "stack.tab.background.top.selected.mouse",    color( distribution, 255, 100, 100 ) );
        setColor( "stack.tab.background.top.focused",           color( distribution, 200, 100, 100 ) );
        setColor( "stack.tab.background.top.focused.mouse",     color( distribution, 255, 200, 200 ) );
        setColor( "stack.tab.background.top",                   color( distribution, 150, 150, 150 ) );
        setColor( "stack.tab.background.top.disabled",          color( distribution, 200, 200, 200 ) );
        setColor( "stack.tab.background.top.mouse",             color( distribution, 150, 255, 150 ) );
        setColor( "stack.tab.background.bottom.selected",       color( distribution, 210, 200, 200 ) );
        setColor( "stack.tab.background.bottom.selected.mouse", color( distribution, 255, 200, 200 ) );
        setColor( "stack.tab.background.bottom.focused",        color( distribution, 255, 200, 200 ) );
        setColor( "stack.tab.background.bottom.focused.mouse",  color( distribution, 255, 255, 255 ) );
        setColor( "stack.tab.background.bottom",                color( distribution, 200, 200, 200 ) );
        setColor( "stack.tab.background.bottom.disabled",       color( distribution, 200, 200, 200 ) );
        setColor( "stack.tab.background.bottom.mouse",          color( distribution, 220, 255, 220 ) );
        setColor( "stack.tab.foreground.selected",              color( distribution, 0, 0, 0 ));
        setColor( "stack.tab.foreground.selected.mouse",        color( distribution, 0, 0, 0 ));
        setColor( "stack.tab.foreground.focused",               color( distribution, 0, 0, 0 ));
        setColor( "stack.tab.foreground.focused.mouse",         color( distribution, 0, 0, 0 ));
        setColor( "stack.tab.foreground",                       color( distribution, 100, 100, 100 ));
        setColor( "stack.tab.foreground.disabled",              color( distribution, 150, 150, 150 ));
        setColor( "stack.tab.foreground.mouse",                 color( distribution, 25, 25, 25 ));
        
        // stack menu
        setColor( "stack.menu.border",                           color( distribution, 100, 100, 100 ) );
        setColor( "stack.menu.border.mouse",                     color( distribution, 200, 100, 100 ) );
        setColor( "stack.menu.background.top",                   color( distribution, 150, 150, 150 ) );
        setColor( "stack.menu.background.top.mouse",             color( distribution, 255, 100, 100 ) );
        setColor( "stack.menu.background.bottom",                color( distribution, 200, 200, 200 ) );
        setColor( "stack.menu.background.bottom.mouse",          color( distribution, 255, 200, 200 ) );
        
        // title
        setColor( "title.background.top.active",               color( distribution, 200, 0, 0 ) );
        setColor( "title.background.top.active.mouse",         color( distribution, 255, 100, 100 ) );
        setColor( "title.background.top.inactive",             color( distribution, 150, 150, 150 ) );
        setColor( "title.background.top.inactive.mouse",       color( distribution, 150, 255, 150 ) );
        setColor( "title.background.top.disabled",             color( distribution, 200, 200, 200 ) );
        setColor( "title.background.bottom.active",            color( distribution, 255, 100, 100 ) );
        setColor( "title.background.bottom.active.mouse",      color( distribution, 255, 200, 200 ) );
        setColor( "title.background.bottom.inactive",          color( distribution, 200, 200, 200 ) );
        setColor( "title.background.bottom.inactive.mouse",    color( distribution, 220, 255, 220 ) );
        setColor( "title.background.bottom.disabled",          color( distribution, 200, 200, 200 ) );
        setColor( "title.foreground.active",              color( distribution, 0, 0, 0 ));
        setColor( "title.foreground.active.mouse",        color( distribution, 0, 0, 0 ));
        setColor( "title.foreground.inactive",            color( distribution, 100, 100, 100 ));
        setColor( "title.foreground.inactive.mouse",      color( distribution, 25, 25, 25 ));
        
        setColor( "title.background.top.active.flap",               color( distribution, 200, 0, 0 ) );
        setColor( "title.background.top.active.mouse.flap",         color( distribution, 255, 100, 100 ) );
        setColor( "title.background.top.inactive.flap",             color( distribution, 150, 150, 150 ) );
        setColor( "title.background.top.inactive.mouse.flap",       color( distribution, 150, 255, 150 ) );
        setColor( "title.background.top.selected.flap",             color( distribution, 150, 150, 150 ) );
        setColor( "title.background.top.disabled.flap",             color( distribution, 200, 200, 200 ) );
        setColor( "title.background.top.selected.mouse.flap",       color( distribution, 150, 255, 150 ) );
        setColor( "title.background.bottom.active.flap",            color( distribution, 255, 100, 100 ) );
        setColor( "title.background.bottom.active.mouse.flap",      color( distribution, 255, 200, 200 ) );
        setColor( "title.background.bottom.inactive.flap",          color( distribution, 200, 200, 200 ) );
        setColor( "title.background.bottom.inactive.mouse.flap",    color( distribution, 220, 255, 220 ) );
        setColor( "title.background.bottom.selected.flap",          color( distribution, 200, 200, 200 ) );
        setColor( "title.background.bottom.disabled.flap",          color( distribution, 200, 200, 200 ) );
        setColor( "title.background.bottom.selected.mouse.flap",    color( distribution, 220, 255, 220 ) );
        setColor( "title.foreground.active.flap",              color( distribution, 0, 0, 0 ));
        setColor( "title.foreground.active.mouse.flap",        color( distribution, 0, 0, 0 ));
        setColor( "title.foreground.inactive.flap",            color( distribution, 100, 100, 100 ));
        setColor( "title.foreground.inactive.mouse.flap",      color( distribution, 25, 25, 25 ));
        setColor( "title.foreground.selected.flap",            color( distribution, 100, 100, 100 ));
        setColor( "title.foreground.selected.mouse.flap",      color( distribution, 25, 25, 25 ));
        
    	setColor( "title.flap.active.knob.highlight", color( distribution, 255, 175, 175 ) );
    	setColor( "title.flap.active.knob.shadow", color( distribution, 150, 0, 0 ) );
    	setColor( "title.flap.active.mouse.knob.highlight", color( distribution, 255, 200, 200 ) );
    	setColor( "title.flap.active.mouse.knob.shadow", color( distribution, 255, 100, 100 ) );
    	setColor( "title.flap.inactive.knob.highlight", color( distribution, 200, 200, 200 ) );
    	setColor( "title.flap.inactive.knob.shadow", color( distribution, 100, 100, 100 ) );
    	setColor( "title.flap.inactive.mouse.knob.highlight", color( distribution, 200, 255, 200 ) );
    	setColor( "title.flap.inactive.mouse.knob.shadow", color( distribution, 0, 200, 0 ) );
    	setColor( "title.flap.selected.knob.highlight", color( distribution, 200, 200, 200 ) );
    	setColor( "title.flap.selected.knob.shadow", color( distribution, 100, 100, 100 ) );
    	setColor( "title.flap.selected.mouse.knob.highlight", color( distribution, 200, 255, 200 ) );
    	setColor( "title.flap.selected.mouse.knob.shadow", color( distribution, 100, 200, 100 ) );
    	setColor( "title.flap.disabled.knob.highlight", color( distribution, 225, 225, 225 ) );
    	setColor( "title.flap.disabled.knob.shadow", color( distribution, 175, 175, 175 ) );
        
        // display border
        setColor( "displayer.border.high.active",           color( distribution, 255, 100, 100 ));
        setColor( "displayer.border.high.active.mouse",     color( distribution, 255, 200, 200 ));
        setColor( "displayer.border.high.inactive",         color( distribution, 200, 200, 200 ));
        setColor( "displayer.border.high.inactive.mouse",   color( distribution, 220, 255, 220 ));
        setColor( "displayer.border.high.disabled",         color( distribution, 220, 200, 200 ));
        setColor( "displayer.border.low.active",            color( distribution, 200, 100, 100 ));
        setColor( "displayer.border.low.active.mouse",      color( distribution, 255, 150, 150 ));
        setColor( "displayer.border.low.inactive",          color( distribution, 100, 100, 100 ));
        setColor( "displayer.border.low.inactive.mouse",    color( distribution, 120, 150, 120 ));
        setColor( "displayer.border.low.disabled",          color( distribution, 180, 200, 200 ));
        
        // RoundButton
        setColor( "action.button",                                 color( distribution, 255, 255, 255 ));
        setColor( "action.button.enabled",                         color( distribution, 215, 215, 215 ));
        setColor( "action.button.selected",                        color( distribution, 200, 200, 255 ));
        setColor( "action.button.selected.enabled",                color( distribution, 150, 150, 210 ));
        setColor( "action.button.mouse.enabled",                   color( distribution, 255, 255, 100 ));
        setColor( "action.button.mouse.selected.enabled",          color( distribution, 100, 100, 255 ));
        setColor( "action.button.pressed.enabled",                 color( distribution, 255, 255, 0 ));
        setColor( "action.button.pressed.selected.enabled",        color( distribution, 0, 0, 255 ));

        setColor( "action.button.focus",                             color( distribution, 150, 150, 150 ));
        setColor( "action.button.enabled.focus",                     color( distribution, 150, 150, 150 ));
        setColor( "action.button.selected.focus",                    color( distribution, 150, 150, 200 ));
        setColor( "action.button.selected.enabled.focus",            color( distribution, 120, 120, 175 ));
        setColor( "action.button.mouse.enabled.focus",               color( distribution, 200, 200, 100 ));
        setColor( "action.button.mouse.selected.enabled.focus",      color( distribution, 50, 50, 150 ));
        setColor( "action.button.pressed.enabled.focus",             color( distribution, 200, 200, 0 ));
        setColor( "action.button.pressed.selected.enabled.focus",    color( distribution, 0, 0, 200 ));
        
        setNullColor( "action.button.text" );
        
        // Round drop down button
        setColor( "action.dropdown",                                 color( distribution, 255, 255, 255 ));
        setColor( "action.dropdown.enabled",                         color( distribution, 215, 215, 215 ));
        setColor( "action.dropdown.selected",                        color( distribution, 200, 200, 255 ));
        setColor( "action.dropdown.selected.enabled",                color( distribution, 150, 150, 210 ));
        setColor( "action.dropdown.mouse.enabled",                   color( distribution, 255, 255, 100 ));
        setColor( "action.dropdown.mouse.selected.enabled",          color( distribution, 100, 100, 255 ));
        setColor( "action.dropdown.pressed.enabled",                 color( distribution, 255, 255, 0 ));
        setColor( "action.dropdown.pressed.selected.enabled",        color( distribution, 0, 0, 255 ));

        setColor( "action.dropdown.focus",                           color( distribution, 150, 150, 150 ));
        setColor( "action.dropdown.enabled.focus",                   color( distribution, 150, 150, 150 ));
        setColor( "action.dropdown.selected.focus",                  color( distribution, 150, 150, 200 ));
        setColor( "action.dropdown.selected.enabled.focus",          color( distribution, 120, 120, 175 ));
        setColor( "action.dropdown.mouse.enabled.focus",             color( distribution, 200, 200, 100 ));
        setColor( "action.dropdown.mouse.selected.enabled.focus",    color( distribution, 50, 50, 150 ));
        setColor( "action.dropdown.pressed.enabled.focus",           color( distribution, 200, 200, 0 ));
        setColor( "action.dropdown.pressed.selected.enabled.focus",  color( distribution, 0, 0, 200 ));
        
        setColor( "action.dropdown.line",                            color( distribution, 150, 150, 150 ));
        setColor( "action.dropdown.line.enabled",                    color( distribution, 150, 150, 150 ));
        setColor( "action.dropdown.line.selected",                   color( distribution, 150, 150, 200 ));
        setColor( "action.dropdown.line.selected.enabled",           color( distribution, 120, 120, 175 ));
        setColor( "action.dropdown.line.mouse.enabled",              color( distribution, 200, 200, 100 ));
        setColor( "action.dropdown.line.mouse.selected.enabled",     color( distribution, 50, 50, 150 ));
        setColor( "action.dropdown.line.pressed.enabled",            color( distribution, 200, 200, 0 ));
        setColor( "action.dropdown.line.pressed.selected.enabled",   color( distribution, 0, 0, 200 ));
        
        setNullColor( "action.dropdown.text" );
        
        // Paint
        setColor( "paint.divider",                            color( distribution, 0, 0, 0 ));
        setColor( "paint.insertion",                          color( distribution, 255, 0, 0 ));
        setColor( "paint.line",                               color( distribution, 0, 0, 0 ));
    }
    
    private Color color( Distribution distribution, int p, int s, int t ){
        switch( distribution ){
            case BGR: return new Color( t, s, p );
            case BRG: return new Color( s, t, p );
            case GBR: return new Color( t, p, s );
            case GRB: return new Color( s, p, t );
            case RBG: return new Color( p, t, s );
            case RGB: return new Color( p, s, t );
            default:  return new Color( p, s, t );
        }
    }
}
