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
package bibliothek.extension.gui.dock.theme.bubble;

import static bibliothek.util.Colors.brighter;
import static bibliothek.util.Colors.darker;
import static bibliothek.util.Colors.diffMirror;
import static bibliothek.util.Colors.fuller;
import static bibliothek.util.Colors.middle;
import static bibliothek.util.Colors.undiffMirror;

import java.awt.Color;

import javax.swing.LookAndFeel;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.themes.ColorBridgeFactory;
import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.themes.color.DefaultColorScheme;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.laf.LookAndFeelColors;

/**
 * A color scheme that generates most of its colors from a small starting set of
 * colors.
 * @author Benjamin Sigg
 */
public class SimpleBubbleColorScheme extends DefaultColorScheme {
    /** A scheme that uses black, white, green and red */
    public static final SimpleBubbleColorScheme BLOPS =
        new SimpleBubbleColorScheme( Color.BLACK, Color.WHITE, new Color( 100, 255, 100 ), new Color( 255, 100, 100 ), Color.DARK_GRAY, 0.3, 0.6, 0.9 );
    

    /** A scheme that uses black, white, green and red */
    public static final SimpleBubbleColorScheme BRIGHT =
        new SimpleBubbleColorScheme( Color.LIGHT_GRAY, Color.WHITE, new Color( 200, 200, 200 ), new Color( 200, 200, 255 ), new Color( 100, 100, 100 ), 0.3, 0.6, 0.9 );
    
    /** A scheme that uses colors from the {@link LookAndFeel} */
    public static final ColorScheme LOOK_AND_FEEL =
        new ColorScheme(){
            private ColorScheme delegate;
        
            {
                updateUI();
            }
            
            public Color getColor( String id ) {
                return delegate.getColor( id ); 
            }
            
            public ColorBridgeFactory getBridgeFactory( Path kind ) {
                return delegate.getBridgeFactory( kind );
            }
            
            public void transmitAll( Priority priority, ColorManager manager ) {
                delegate.transmitAll( priority, manager );
            }

            public boolean updateUI() {
                delegate = new SimpleBubbleColorScheme( 
                        DockUI.getColor( LookAndFeelColors.TITLE_SELECTION_BACKGROUND ),
                        DockUI.getColor( LookAndFeelColors.TITLE_BACKGROUND ),
                        
                        
                        
                        diffMirror( DockUI.getColor( LookAndFeelColors.TITLE_BACKGROUND ), 0.25 ),
                        diffMirror( DockUI.getColor( LookAndFeelColors.TITLE_SELECTION_BACKGROUND ), 0.4 ),
                        undiffMirror( DockUI.getColor( LookAndFeelColors.TITLE_BACKGROUND ), 0.75 ),
                        
                        DockUI.getColor( LookAndFeelColors.TITLE_SELECTION_FOREGROUND ),
                        DockUI.getColor( LookAndFeelColors.TITLE_FOREGROUND ),
                        0.3, 0.6, 0.9 );
                
                return true;
            }
    };
        


    /**
     * Creates a scheme using only one base color.
     * @param color the base color
     */
    public SimpleBubbleColorScheme( Color color ){
        this( 
                fuller( color, 0.5 ), brighter( color, 0.9 ), 
                color, brighter( color, 0.9 ), darker( color, 0.9 ),
                0.3, 0.6, 0.9 );
    }
    
    /**
     * Creates a new color scheme
     * @param active color for active elements
     * @param inactive color for inactive elements
     * @param enabled color for enabled buttons
     * @param selected color for selected buttons
     * @param disabled color for disabled buttons
     * @param factorWeak factor for weak color transformation
     * @param factor factor for color transformation
     * @param factorStrong factor for strong color transformation
     */
    public SimpleBubbleColorScheme( Color active, Color inactive, Color enabled, Color selected, Color disabled, double factorWeak, double factor, double factorStrong ){
        this( active, inactive, enabled, selected, disabled, diffMirror( active, 1.0 ), diffMirror( inactive, 1.0 ), factorWeak, factor, factorStrong );
    }
        
    /**
     * Creates a new color scheme
     * @param active color for active elements
     * @param inactive color for inactive elements
     * @param enabled color for enabled buttons
     * @param selected color for selected buttons
     * @param disabled color for disabled buttons
     * @param activeText color for text on active elements
     * @param inactiveText color for text on inactive elements
     * @param factorWeak factor for weak color transformation
     * @param factor factor for color transformation
     * @param factorStrong factor for strong color transformation
     */
    public SimpleBubbleColorScheme( Color active, Color inactive, Color enabled, Color selected, Color disabled, Color activeText, Color inactiveText, double factorWeak, double factor, double factorStrong ){
        // stack        
        setColor( "stack.tab.background.top.mouse",               brighter( inactive, factor ));
        setColor( "stack.tab.background.bottom.mouse",            darker( inactive, factor ) );
        setColor( "stack.tab.border.mouse",                       middle( diffMirror( inactive, factorWeak ), diffMirror( active, factorWeak )) );
        setColor( "stack.tab.foreground.mouse",                   diffMirror( inactive, 1.0 ));
        
        setColor( "stack.tab.background.top",                     brighter( inactive, factorWeak ));
        setColor( "stack.tab.background.bottom",                  darker( inactive, factorWeak ) );
        setColor( "stack.tab.border",                             diffMirror( inactive, factorWeak ));
        setColor( "stack.tab.foreground",                         diffMirror( inactive, 1.0 ));
        
        setColor( "stack.tab.background.top.selected.mouse",      brighter( active, factor ));
        setColor( "stack.tab.background.bottom.selected.mouse",   darker( active, factor ) );
        setColor( "stack.tab.border.selected.mouse",              diffMirror( active, factor ));
        setColor( "stack.tab.foreground.selected.mouse",          diffMirror( active, 1.0 ));
        
        setColor( "stack.tab.background.top.selected",            brighter( active, factorWeak ));
        setColor( "stack.tab.background.bottom.selected",         darker( active, factorWeak ) );
        setColor( "stack.tab.border.selected",                    diffMirror( active, factorWeak ));
        setColor( "stack.tab.foreground.selected",                diffMirror( active, 1.0 ));
        
        setColor( "stack.tab.background.top.focused.mouse",       fuller( brighter( active, factor ), factor ));
        setColor( "stack.tab.background.bottom.focused.mouse",    fuller( darker( active, factor ), factor ) );
        setColor( "stack.tab.border.focused.mouse",               diffMirror( active, factor ));
        setColor( "stack.tab.foreground.focused.mouse",           diffMirror( active, 1.0 ));
        
        setColor( "stack.tab.background.top.focused",             fuller( brighter( active, factorWeak ), factor ));
        setColor( "stack.tab.background.bottom.focused",          fuller( darker( active, factorWeak ), factor ) );
        setColor( "stack.tab.border.focused",                     diffMirror( active, factorWeak ));
        setColor( "stack.tab.foreground.focused",                 diffMirror( active, 1.0 ));
        
        // title
        setColor( "title.background.top.active",               brighter( active, factorWeak ));
        setColor( "title.background.top.active.mouse",         brighter( active, factor ));
        setColor( "title.background.top.inactive",             brighter( inactive, factorWeak ));
        setColor( "title.background.top.inactive.mouse",       brighter( inactive, factor ));
        setColor( "title.background.bottom.active",            darker( active, factorWeak ) );
        setColor( "title.background.bottom.active.mouse",      darker( active, factor ) );
        setColor( "title.background.bottom.inactive",          darker( inactive, factorWeak ) );
        setColor( "title.background.bottom.inactive.mouse",    darker( inactive, factor ) );
        setColor( "title.foreground.active",              activeText );
        setColor( "title.foreground.active.mouse",        activeText );
        setColor( "title.foreground.inactive",            inactiveText );
        setColor( "title.foreground.inactive.mouse",      inactiveText );

        setColor( "title.background.top.active.flap",               brighter( active, factorWeak ));
        setColor( "title.background.top.active.mouse.flap",         brighter( active, factor ));
        setColor( "title.background.top.inactive.flap",             brighter( inactive, factorWeak ));
        setColor( "title.background.top.inactive.mouse.flap",       brighter( inactive, factor ));
        setColor( "title.background.top.selected.flap",             brighter( inactive, factorWeak ));
        setColor( "title.background.top.selected.mouse.flap",       brighter( inactive, factor ));
        setColor( "title.background.bottom.active.flap",            darker( active, factorWeak ) );
        setColor( "title.background.bottom.active.mouse.flap",      darker( active, factor ) );
        setColor( "title.background.bottom.inactive.flap",          darker( inactive, factorWeak ) );
        setColor( "title.background.bottom.inactive.mouse.flap",    darker( inactive, factor ) );
        setColor( "title.background.bottom.selected.flap",          darker( inactive, factorWeak ) );
        setColor( "title.background.bottom.selected.mouse.flap",    darker( inactive, factor ) );
        setColor( "title.foreground.active.flap",              activeText );
        setColor( "title.foreground.active.mouse.flap",        activeText );
        setColor( "title.foreground.inactive.flap",            inactiveText );
        setColor( "title.foreground.inactive.mouse.flap",      inactiveText );
        setColor( "title.foreground.selected.flap",            inactiveText );
        setColor( "title.foreground.selected.mouse.flap",      inactiveText );
        
        // display border
        setColor( "displayer.border.high.active",           brighter( active, factorWeak ));
        setColor( "displayer.border.high.active.mouse",     brighter( active, factor ));
        setColor( "displayer.border.high.inactive",         brighter( inactive, factorWeak ));
        setColor( "displayer.border.high.inactive.mouse",   brighter( inactive, factor ));
        setColor( "displayer.border.low.active",            darker( active, factorWeak ));
        setColor( "displayer.border.low.active.mouse",      darker( active, factor ));
        setColor( "displayer.border.low.inactive",          darker( inactive, factorWeak ));
        setColor( "displayer.border.low.inactive.mouse",    darker( inactive, factor ));
        
        // RoundButton
        setColor( "action.button",                                 disabled );
        setColor( "action.button.focus",                           diffMirror( disabled, factorWeak ));
        setColor( "action.button.enabled",                         enabled );
        setColor( "action.button.enabled.focus",                   diffMirror( enabled, factorWeak ));
        setColor( "action.button.selected",                        middle( disabled, middle( selected, disabled ) ));
        setColor( "action.button.selected.focus",                  diffMirror( middle( disabled, middle( selected, disabled ) ), factorWeak ));
        setColor( "action.button.selected.enabled",                selected );
        setColor( "action.button.selected.enabled.focus",          diffMirror( selected, factorWeak ));
        setColor( "action.button.mouse.enabled",                   undiffMirror( enabled, factor ) );
        setColor( "action.button.mouse.enabled.focus",             undiffMirror( diffMirror( enabled, factor ), factorWeak ));
        setColor( "action.button.mouse.selected.enabled",          undiffMirror( selected, factor ) );
        setColor( "action.button.mouse.selected.enabled.focus",    undiffMirror( diffMirror( selected, factor ), factorWeak ));
        setColor( "action.button.pressed.enabled",                 undiffMirror( enabled, factorStrong ) );
        setColor( "action.button.pressed.enabled.focus",           undiffMirror( diffMirror( enabled, factorStrong ), factorWeak ));
        setColor( "action.button.pressed.selected.enabled",        undiffMirror( selected, factorStrong ) );
        setColor( "action.button.pressed.selected.enabled.focus",  undiffMirror( diffMirror( selected, factorStrong ), factorWeak ));

        // Round drop down button
        setColor( "action.dropdown",                                 disabled );
        setColor( "action.dropdown.enabled",                         enabled );
        setColor( "action.dropdown.selected",                        middle( disabled, middle( selected, disabled ) ));
        setColor( "action.dropdown.selected.enabled",                selected );
        setColor( "action.dropdown.mouse.enabled",                   undiffMirror( enabled, factor ) );
        setColor( "action.dropdown.mouse.selected.enabled",          undiffMirror( selected, factor ) );
        setColor( "action.dropdown.pressed.enabled",                 undiffMirror( enabled, factorStrong ) );
        setColor( "action.dropdown.pressed.selected.enabled",        undiffMirror( selected, factorStrong ) );
        
        setColor( "action.dropdown.focus",                           diffMirror( disabled, factorWeak ));
        setColor( "action.dropdown.enabled.focus",                   diffMirror( enabled, factorWeak ));
        setColor( "action.dropdown.selected.focus",                  diffMirror( middle( disabled, middle( selected, disabled ) ), factorWeak ));
        setColor( "action.dropdown.selected.enabled.focus",          diffMirror( selected, factorWeak ));
        setColor( "action.dropdown.mouse.enabled.focus",             undiffMirror( diffMirror( enabled, factor ), factorWeak ));
        setColor( "action.dropdown.mouse.selected.enabled.focus",    undiffMirror( diffMirror( selected, factor ), factorWeak ));
        setColor( "action.dropdown.pressed.enabled.focus",           undiffMirror( diffMirror( enabled, factorStrong ), factorWeak ));
        setColor( "action.dropdown.pressed.selected.enabled.focus",  undiffMirror( diffMirror( selected, factorStrong ), factorWeak ));
        
        setColor( "action.dropdown.line",                            diffMirror( disabled, factorWeak ));
        setColor( "action.dropdown.line.enabled",                    diffMirror( enabled, factorWeak ));
        setColor( "action.dropdown.line.selected",                   diffMirror( middle( disabled, middle( selected, disabled ) ), factorWeak ));
        setColor( "action.dropdown.line.selected.enabled",           diffMirror( selected, factorWeak ));
        setColor( "action.dropdown.line.mouse.enabled",              undiffMirror( diffMirror( enabled, factor ), factorWeak ));
        setColor( "action.dropdown.line.mouse.selected.enabled",     undiffMirror( diffMirror( selected, factor ), factorWeak ));
        setColor( "action.dropdown.line.pressed.enabled",            undiffMirror( diffMirror( enabled, factorStrong ), factorWeak ));
        setColor( "action.dropdown.line.pressed.selected.enabled",   undiffMirror( diffMirror( selected, factorStrong ), factorWeak ));
        
        // Paint
        setColor( "paint.divider",                            disabled );
        setColor( "paint.insertion",                          selected );
        setColor( "paint.line",                               selected );
    }
}
