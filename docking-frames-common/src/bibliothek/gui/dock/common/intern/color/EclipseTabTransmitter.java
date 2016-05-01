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

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.dock.themes.ColorBridgeFactory;
import bibliothek.gui.dock.themes.color.TabColor;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.util.Colors;

/**
 * A {@link ColorTransmitter} that connects {@link TabColor}s with the
 * {@link EclipseTheme}.
 * @author Benjamin Sigg
 */
public class EclipseTabTransmitter extends TabColorTransmitter {
    /**
     * A factory that creates {@link EclipseTabTransmitter}s.
     */
    public static final ColorBridgeFactory FACTORY =
        new ColorBridgeFactory(){
        public EclipseTabTransmitter create( ColorManager manager ){
            return new EclipseTabTransmitter( manager );
        }
    };

    private static final String KEYS[] = new String[]{
        "stack.tab.border", 
        "stack.tab.border.selected", 
        "stack.tab.border.selected.focused", 
        "stack.tab.border.selected.focuslost",

        "stack.tab.top",
        "stack.tab.top.selected",
        "stack.tab.top.selected.focused",
        "stack.tab.top.selected.focuslost",

        "stack.tab.bottom",
        "stack.tab.bottom.selected",
        "stack.tab.bottom.selected.focused",
        "stack.tab.bottom.selected.focuslost",

        "stack.tab.text",
        "stack.tab.text.selected",
        "stack.tab.text.selected.focused", 
        "stack.tab.text.selected.focuslost"
    };

    public EclipseTabTransmitter( ColorManager manager ){
        super( manager, KEYS );
    }

    @Override
    protected boolean isFocused( String id ) {
        return id.contains( "focused" ) || id.contains( "focuslost" );
    }

    @Override
    protected boolean isSelected( String id ) {
        return id.contains( "selected" );
    }

    @Override
    protected boolean isForeground( String id ) {
        return id.contains( "text" );
    }

    @Override
    protected Color convert( Color source, String key ) {
        if( isSelected( key ))
            return convertSelected( source, key );

        if( isFocused( key ))
            return convertFocused( source, key );

        if( "stack.tab.border".equals( key ))
            return source;

        if(  "stack.tab.top".equals( key ))
            return Colors.undiffMirror( source, 0.5 );

        if( "stack.tab.bottom".equals( key ))
            return source;

        if( "stack.tab.text".equals( key ))
            return Colors.diffMirror( source, 1.0 );

        return null;
    }

    @Override
    protected Color convertSelected( Color source, String key ) {
        if( isFocused( key ))
            return convertFocused( source, key );

        if( "stack.tab.border.selected".equals( key ))
            return source;

        if(  "stack.tab.top.selected".equals( key ))
            return Colors.undiffMirror( source, 0.5 );

        if( "stack.tab.bottom.selected".equals( key ))
            return source;

        if( "stack.tab.text.selected".equals( key ))
            return Colors.diffMirror( source, 1.0 );

        return null;
    }

    @Override
    protected Color convertFocused( Color source, String key ) {
        if( "stack.tab.border.selected.focused".equals( key ))
            return source;
        if( "stack.tab.border.selected.focuslost".equals( key ))
            return source;

        if( "stack.tab.top.selected.focused".equals( key ))
            return Colors.undiffMirror( source, 0.5 );
        if( "stack.tab.top.selected.focuslost".equals( key ))
            return Colors.undiffMirror( source, 0.5 );

        if( "stack.tab.bottom.selected.focused".equals( key ))
            return source;
        if( "stack.tab.bottom.selected.focuslost".equals( key ))
            return source;

        if( "stack.tab.text.selected.focused".equals( key ))
            return Colors.diffMirror( source, 1.0 ); 
        if( "stack.tab.text.selected.focuslost".equals( key ))
            return Colors.diffMirror( source, 1.0 );

        return null;
    }
}
