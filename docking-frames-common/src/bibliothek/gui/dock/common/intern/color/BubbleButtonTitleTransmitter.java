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
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.util.Colors;

/**
 * A transmitter setting the colors of the flap button titles when using
 * a {@link BubbleTheme}.
 * @author Benjamin Sigg
 */
public class BubbleButtonTitleTransmitter extends MinimizedButtonColorTransmitter {
    private static final String[] KEYS = { 
            "title.background.top.active.mouse.flap",
            "title.background.top.active.flap",
            "title.background.top.inactive.mouse.flap",
            "title.background.top.inactive.flap",
            "title.background.top.selected.mouse.flap",
            "title.background.top.selected.flap",
            
            "title.background.bottom.active.mouse.flap",
            "title.background.bottom.active.flap",
            "title.background.bottom.inactive.mouse.flap",
            "title.background.bottom.inactive.flap",
            "title.background.bottom.selected.mouse.flap",
            "title.background.bottom.selected.flap",
            
            "title.foreground.active.mouse.flap",
            "title.foreground.active.flap",
            "title.foreground.inactive.mouse.flap",
            "title.foreground.inactive.flap",
            "title.foreground.selected.mouse.flap",
            "title.foreground.selected.flap" };


    /**
     * Creates a new transmitter
     * @param manager the manager of all colors
     */
    public BubbleButtonTitleTransmitter( ColorManager manager ){
        super( manager, KEYS );
    }
    
    @Override
    protected Color convert( Color source, String key ) {
        if( isFocused( key ))
            return convertFocused( Colors.diffMirror( source, 0.2 ), key );
        
        if( isSelected( key ))
            return convertSelected( Colors.diffMirror( source, 0.2 ), key );
        
        if( key.contains( "foreground" ) )
            return Colors.diffMirror( source, 1.0 );
        
        if( "title.background.top.inactive.flap".equals( key ))
            return Colors.darker( source, 0.3 );
        if( "title.background.bottom.inactive.flap".equals( key ))
            return Colors.brighter( source, 0.3 );
        
        if( "title.background.top.inactive.mouse.flap".equals( key ))
            return Colors.fuller( Colors.darker( source, 0.3 ), 0.3 );
        if( "title.background.bottom.inactive.mouse.flap".equals( key ))
            return Colors.fuller( Colors.brighter( source, 0.3 ), 0.3 );
        
        return source;
    }

    @Override
    protected Color convertFocused( Color source, String key ) {
        if( key.contains( "foreground" ) )
            return Colors.diffMirror( source, 1.0 );
        
        if( "title.background.top.active.flap".equals( key ))
            return Colors.darker( source, 0.3 );
        if( "title.background.bottom.active.flap".equals( key ))
            return Colors.brighter( source, 0.3 );
        
        if( "title.background.top.active.mouse.flap".equals( key ))
            return Colors.fuller( Colors.darker( source, 0.3 ), 0.3 );
        if( "title.background.bottom.active.mouse.flap".equals( key ))
            return Colors.fuller( Colors.brighter( source, 0.3 ), 0.3 );
        
        return source;
    }
    
    @Override
    protected Color convertSelected( Color source, String key ) {
        if( key.contains( "foreground" ) )
            return Colors.diffMirror( source, 1.0 );
        
        if( "title.background.top.selected.flap".equals( key ))
            return Colors.darker( source, 0.3 );
        if( "title.background.bottom.selected.flap".equals( key ))
            return Colors.brighter( source, 0.3 );
        
        if( "title.background.top.selected.mouse.flap".equals( key ))
            return Colors.fuller( Colors.darker( source, 0.3 ), 0.3 );
        if( "title.background.bottom.selected.mouse.flap".equals( key ))
            return Colors.fuller( Colors.brighter( source, 0.3 ), 0.3 );
        
        return source;
    }

    @Override
    protected boolean isFocused( String id ) {
        return id.contains( "active" ) && !id.contains( "inactive" );
    }

    @Override
    protected boolean isForeground( String id ) {
        return id.contains( "foreground" );
    }

    @Override
    protected boolean isSelected( String id ) {
        return id.contains( "selected" );
    }
}
