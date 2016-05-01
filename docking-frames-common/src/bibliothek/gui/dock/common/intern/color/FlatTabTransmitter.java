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

import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.gui.dock.common.ColorMap;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.color.ColorBridge;
import bibliothek.util.Colors;

/**
 * A {@link ColorBridge} for {@link FlatTheme} used in combination with
 * {@link CDockable} and its {@link ColorMap}.
 * @author Benjamin Sigg
 *
 */
public class FlatTabTransmitter extends TabColorTransmitter {
    private static final String[] KEYS = {
        "stack.tab.border.out.selected", 
        "stack.tab.border.center.selected",
        "stack.tab.border.out.focused", 
        "stack.tab.border.center.focused",
        "stack.tab.border.out", 
        "stack.tab.border.center",
                    
        "stack.tab.background.top.selected", 
        "stack.tab.background.bottom.selected",
        "stack.tab.background.top.focused", 
        "stack.tab.background.bottom.focused",
        "stack.tab.background.top", 
        "stack.tab.background.bottom",
        
        "stack.tab.foreground.selected",
        "stack.tab.foreground.focused",
        "stack.tab.foreground"
    };
    
    /**
     * Creates a new transmitter
     * @param manager the source for colors
     */
    public FlatTabTransmitter( ColorManager manager ) {
        super( manager, KEYS );
    }

    @Override
    protected Color convert( Color source, String key ) {
        if( key.contains( "focused" ))
            return convertFocused( Colors.diffMirror( source, 0.6 ), key );
        
        if( key.contains( "selected" ))
            return convertSelected( Colors.diffMirror( source, 0.3 ), key );

        if( "stack.tab.border.out".equals( key ))
            return null;
        if( "stack.tab.border.center".equals( key ))
            return null;
        
        if( "stack.tab.background.top".equals( key ))
            return Colors.darker( source, 0.3 );
        if( "stack.tab.background.bottom".equals( key ))
            return Colors.brighter( source, 0.3 );
        
        if( "stack.tab.foreground".equals( key ))
            return Colors.diffMirror( source, 1.0 );

        return null;
    }

    @Override
    protected Color convertSelected( Color source, String key ) {
        if( key.contains( "focused" ))
            return convertFocused( Colors.diffMirror( source, 0.3 ), key );

        if( "stack.tab.border.out.selected".equals( key ))
            return null;
        if( "stack.tab.border.center.selected".equals( key ))
            return null;
        
        if( "stack.tab.background.top.selected".equals( key ))
            return Colors.darker( source, 0.3 );
        if( "stack.tab.background.bottom.selected".equals( key ))
            return Colors.brighter( source, 0.3 );
        
        if( "stack.tab.foreground.selected".equals( key ))
            return Colors.diffMirror( source, 1.0 );

        return null;
    }

    @Override
    protected Color convertFocused( Color source, String key ) {
        if( "stack.tab.border.out.focused".equals( key ))
            return null;
        if( "stack.tab.border.center.focused".equals( key ))
            return null;
        
        if( "stack.tab.background.top.focused".equals( key ))
            return Colors.darker( source, 0.3 );
        if( "stack.tab.background.bottom.focused".equals( key ))
            return Colors.brighter( source, 0.3 );
        
        if( "stack.tab.foreground.focused".equals( key ))
            return Colors.diffMirror( source, 1.0 );

        return null;
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
}
