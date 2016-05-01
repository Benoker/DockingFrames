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

import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.util.Colors;

/**
 * A {@link ColorTransmitter} connecting the {@link BasicTheme} with the
 * individual color requests of common-project elements.
 * @author Benjamin Sigg
 */
public class BasicTabTransmitter extends TabColorTransmitter {
    private static final String[] KEYS = {
        "stack.tab.foreground",
        "stack.tab.foreground.selected",
        "stack.tab.foreground.focused",
        "stack.tab.background",
        "stack.tab.background.selected",
        "stack.tab.background.focused"
    };
    
    /**
     * Creates a new transmitter.
     * @param manager the source of the colors
     */
    public BasicTabTransmitter( ColorManager manager ){
        super( manager, KEYS );
    }
    
    @Override
    protected Color convert( Color source, String key ) {
        if( isForeground( key ))
            return Colors.diffMirror( source, 1.0 );
        
        return source;
    }

    @Override
    protected Color convertFocused( Color source, String key ) {
        if( isForeground( key ))
            return Colors.diffMirror( source, 1.0 );
        
        return source;
    }

    @Override
    protected Color convertSelected( Color source, String key ) {
        if( isForeground( key ))
            return Colors.diffMirror( source, 1.0 );
        
        return source;
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
