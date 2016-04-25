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
import bibliothek.gui.dock.themes.ColorBridgeFactory;
import bibliothek.gui.dock.themes.color.TabColor;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.util.Colors;

/**
 * A transmitter handling {@link TabColor}s related to a {@link BubbleTheme}.
 * @author Benjamin Sigg
 */
public class BubbleTabTransmitter extends TabColorTransmitter {
    /**
     * A factory that creates {@link BubbleTabTransmitter}s.
     */
    public static final ColorBridgeFactory FACTORY =
        new ColorBridgeFactory(){
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
    
    /**
     * Creates a new transmitter.
     * @param manager the source of all colors
     */
    public BubbleTabTransmitter( ColorManager manager ){
        super( manager, KEYS );
    }
    
    @Override
    protected boolean isFocused( String id ) {
        return id.contains( "focused" );
    }
    
    @Override
    protected boolean isForeground( String id ) {
        return id.contains( "foreground" );
    }
    
    @Override
    protected boolean isSelected( String id ) {
        return id.contains( "selected" );
    }
    
    @Override
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
    
    @Override
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
    
    @Override
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
    
}
