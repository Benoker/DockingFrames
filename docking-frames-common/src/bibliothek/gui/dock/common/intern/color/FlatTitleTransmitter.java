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
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.util.Colors;

/**
 * A connection between a {@link FlatTheme}-title and the {@link ColorMap}.
 * @author Benjamin Sigg
 */
public class FlatTitleTransmitter extends TitleColorTransmitter{
    private static final String[] KEYS = { 
        "title.active.left", "title.inactive.left", 
        "title.active.text", "title.inactive.text" };
        
    /**
     * Creates a new transmitter.
     * @param manager the source of colors
     */
    public FlatTitleTransmitter( ColorManager manager ){
        super( manager, KEYS );
    }
        
    @Override
    protected Color convert( Color source, String key ) {
        if( isFocused( key ))
            return convertFocused( source, key );
        
        if( "title.inactive.text".equals( key ))
            return Colors.diffMirror( source, 1.0 );
        
        return source;
    }

    @Override
    protected Color convertFocused( Color source, String key ) {
        if( "title.active.text".equals( key ))
            return Colors.diffMirror( source, 1.0 );
        
        return source;
    }

    @Override
    protected boolean isFocused( String id ) {
        return id.contains( "active" ) && !id.contains( "inactive" );
    }

    @Override
    protected boolean isForeground( String id ) {
        return id.contains( "text" );
    }
}
