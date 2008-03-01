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
package bibliothek.gui.dock.common.intern.color;

import java.awt.Color;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.extension.gui.dock.theme.bubble.BubbleStackDockComponent;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.ColorMap;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.themes.ColorProviderFactory;
import bibliothek.gui.dock.themes.color.TabColor;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.util.Colors;

/**
 * A transmitter handling {@link TabColor}s related to a {@link BubbleTheme}.
 * @author Benjamin Sigg
 */
public class BubbleTabTransmitter extends ColorTransmitter<TabColor> {
    /**
     * A factory that creates {@link BubbleTabTransmitter}s.
     */
    public static final ColorProviderFactory<TabColor, BubbleTabTransmitter> FACTORY =
        new ColorProviderFactory<TabColor, BubbleTabTransmitter>(){
        public BubbleTabTransmitter create( ColorManager manager ){
            return new BubbleTabTransmitter( manager );
        }
    };
    
    private static final String[] KEYS = new String[]{ 
        "stack.tab.background.top.mouse",
        "stack.tab.background.bottom.mouse",
        "stack.tab.border.mouse",
        "stack.tab.foreground.mouse",
        
        "stack.tab.background.top",
        "stack.tab.background.bottom",
        "stack.tab.border",
        "stack.tab.foreground",
        
        "stack.tab.background.top.selected.mouse",
        "stack.tab.background.bottom.selected.mouse",
        "stack.tab.border.selected.mouse",
        "stack.tab.foreground.selected.mouse",
        
        "stack.tab.background.top.selected",
        "stack.tab.background.bottom.selected",
        "stack.tab.border.selected",
        "stack.tab.foreground.selected",
        
        "stack.tab.background.top.focused.mouse",
        "stack.tab.background.bottom.focused.mouse",
        "stack.tab.border.focused.mouse",
        "stack.tab.foreground.focused.mouse",
        
        "stack.tab.background.top.focused",
        "stack.tab.background.bottom.focused",
        "stack.tab.border.focused",
        "stack.tab.foreground.focused"
        };
    
    private ColorManager manager;
    
    /**
     * Creates a new transmitter.
     * @param manager the source of all colors
     */
    public BubbleTabTransmitter( ColorManager manager ){
        super( KEYS );
        
        this.manager = manager;
    }
    
    /**
     * Changes a background color such that it can be used on a {@link BubbleStackDockComponent}
     * @param source the original color
     * @param key the key for which the color is needed
     * @return the new color
     */
    protected Color convert( Color source, String key ){
        if( key.contains(  "selected" ))
            return convertSelected( Colors.undiffMirror( source, 0.2 ), key );
        
        if( key.contains( "focused" ))
            return convertFocused( Colors.diffMirror( source, 0.2 ), key );
        
        if( "stack.tab.background.top.mouse".equals( key ))
            return Colors.fuller( source, 0.3 );
        if( "stack.tab.background.bottom.mouse".equals( key ))
            return Colors.fuller( Colors.diffMirror( source, 0.3 ), 0.3 );
        if( "stack.tab.border.mouse".equals( key ))
            return Colors.fuller( Colors.diffMirror( source, 0.6 ), 0.3 );
        if( "stack.tab.foreground.mouse".equals( key ))
            return Colors.fuller( Colors.diffMirror( source, 0.9 ), 0.3 );
        
        if( "stack.tab.background.top".equals( key ))
            return source;
        if( "stack.tab.background.bottom".equals( key ))
            return Colors.diffMirror( source, 0.3 );
        if( "stack.tab.border".equals( key ))
            return Colors.diffMirror( source, 0.6 );
        if( "stack.tab.foreground".equals( key ))
            return Colors.diffMirror( source, 0.9 );
        
        return null;
    }
    
    /**
     * Changes a background color such that it can be used on a {@link BubbleStackDockComponent} 
     * @param source the original color
     * @param key the key for which the color is needed, can be one of
     * the selected or focused kind
     * @return the new color
     */
    protected Color convertSelected( Color source, String key ){
        if( key.contains( "focused" ))
            return convertFocused( Colors.diffMirror( source, 0.2 ), key );
        
        if( "stack.tab.background.top.selected.mouse".equals( key ))
            return Colors.fuller( source, 0.3 );
        if( "stack.tab.background.bottom.selected.mouse".equals( key ))
            return Colors.fuller( Colors.diffMirror( source, 0.3 ), 0.3 );
        if( "stack.tab.border.selected.mouse".equals( key ))
            return Colors.fuller( Colors.diffMirror( source, 0.6 ), 0.3 );
        if( "stack.tab.foreground.selected.mouse".equals( key ))
            return Colors.fuller( Colors.diffMirror( source, 0.9 ), 0.3 );
        
        if( "stack.tab.background.top.selected".equals( key ))
            return source;
        if( "stack.tab.background.bottom.selected".equals( key ))
            return Colors.diffMirror( source, 0.3 );
        if( "stack.tab.border.selected".equals( key ))
            return Colors.diffMirror( source, 0.6 );
        if( "stack.tab.foreground.selected".equals( key ))
            return Colors.diffMirror( source, 0.9 );
        
        return null;
    }
    
    /**
     * Changes a background color such that it can be used on a {@link BubbleStackDockComponent} 
     * @param source the original color
     * @param key the key for which the color is needed, can only be one
     * of the focused kind
     * @return the new color
     */
    protected Color convertFocused( Color source, String key ){
        if( "stack.tab.background.top.focused.mouse".equals( key ))
            return Colors.fuller( source, 0.3 );
        if( "stack.tab.background.bottom.focused.mouse".equals( key ))
            return Colors.fuller( Colors.diffMirror( source, 0.3 ), 0.3 );
        if( "stack.tab.border.focused.mouse".equals( key ))
            return Colors.fuller( Colors.diffMirror( source, 0.6 ), 0.3 );
        if( "stack.tab.foreground.focused.mouse".equals( key ))
            return Colors.fuller( Colors.diffMirror( source, 0.9 ), 0.3 );
        
        if( "stack.tab.background.top.focused".equals( key ))
            return source;
        if( "stack.tab.background.bottom.focused".equals( key ))
            return Colors.diffMirror( source, 0.3 );
        if( "stack.tab.border.focused".equals( key ))
            return Colors.diffMirror( source, 0.6 );
        if( "stack.tab.foreground.focused".equals( key ))
            return Colors.diffMirror( source, 0.9 );
        
        return null;
    }
    
    @Override
    protected Color get( Color color, String id, TabColor observer ) {
        CDockable dockable = getDockable( observer );
        if( dockable != null ){
            return get( color, id, dockable );
        }
        
        return color;
    }
    
    /**
     * Searches the color <code>id</code> for <code>dockable</code>.
     * @param color the color to be returned if the search for <code>id</code> fails.
     * @param id the identifier of the color to search
     * @param dockable the element for which the color will be used
     * @return some color
     */
    protected Color get( Color color, String id, CDockable dockable ){
        Color check = null;
        ColorMap colors = dockable.getColors();
        
        boolean foreground = id.contains( "foreground" );
        boolean selected = id.contains( "selected" );
        boolean focused = id.contains( "focused" );
        
        if( foreground ){
            if( focused ){
                check = colors.getColor( ColorMap.COLOR_KEY_TAB_FOREGROUND_FOCUSED );
            }
            if( (focused && check == null) || selected ){
                check = colors.getColor( ColorMap.COLOR_KEY_TAB_FOREGROUND_SELECTED );
            }
            
            if( check == null ){
                check = colors.getColor( ColorMap.COLOR_KEY_TAB_FOREGROUND );
            }
        }
        
        if( check == null ){
            if( focused ){
                check = colors.getColor( ColorMap.COLOR_KEY_TAB_BACKGROUND_FOCUSED );
                if( check != null )
                    check = convertFocused( check, id );
            }
            
            if( (focused && check == null) || selected ){
                check = colors.getColor( ColorMap.COLOR_KEY_TAB_BACKGROUND_SELECTED );
                if( check != null )
                    check = convertSelected( check, id );
            }
            
            if( check == null ){
                check = colors.getColor( ColorMap.COLOR_KEY_TAB_BACKGROUND );
                if( check != null )
                    check = convert( check, id );
            }
        }
        
        if( check != null )
            return check;
        
        return color;
    }
    
    @Override
    protected void update( CDockable dockable, String key, Color color ) {
        boolean change =
            ColorMap.COLOR_KEY_TAB_BACKGROUND.equals( key ) ||
            ColorMap.COLOR_KEY_TAB_BACKGROUND_FOCUSED.equals( key ) ||
            ColorMap.COLOR_KEY_TAB_BACKGROUND_SELECTED.equals( key ) ||
            ColorMap.COLOR_KEY_TAB_FOREGROUND.equals( key ) ||
            ColorMap.COLOR_KEY_TAB_FOREGROUND_FOCUSED.equals( key ) ||
            ColorMap.COLOR_KEY_TAB_FOREGROUND_SELECTED.equals( key );
        
        if( change ){
            for( String check : KEYS )
                set( check, get( manager.get( check ), check, dockable ), dockable );
        }
    }
    
    @Override
    protected CDockable getDockable( TabColor observer ) {
        Dockable dockable = observer.getDockable();
        if( dockable instanceof CommonDockable )
            return ((CommonDockable)dockable).getDockable();
        
        return null;
    }
}
