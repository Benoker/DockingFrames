/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package glass.eclipse.theme;

import java.awt.Color;

import bibliothek.gui.dock.common.intern.color.EclipseTabTransmitter;
import bibliothek.gui.dock.common.intern.color.TabColorTransmitter;
import bibliothek.gui.dock.common.theme.color.CColorBridge;
import bibliothek.gui.dock.themes.ColorBridgeFactory;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.util.Colors;


/**
 * A {@link ColorTransmitter} that connects {@link TabColor}s with the
 * {@link EclipseTheme}.
 * @author Benjamin Sigg
 */
public class GlassEclipseTabTransmitter extends TabColorTransmitter implements CColorBridge{
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
        "stack.tab.border.glass", 
        "stack.tab.border.selected.glass", 
        "stack.tab.border.selected.focused.glass", 
        "stack.tab.border.selected.focuslost.glass",

        "stack.tab.top.glass",
        "stack.tab.top.selected.glass",
        "stack.tab.top.selected.focused.glass",
        "stack.tab.top.selected.focuslost.glass",

        "stack.tab.bottom.glass",
        "stack.tab.bottom.selected.glass",
        "stack.tab.bottom.selected.focused.glass",
        "stack.tab.bottom.selected.focuslost.glass",

        "stack.tab.text.glass",
        "stack.tab.text.selected.glass",
        "stack.tab.text.selected.focused.glass", 
        "stack.tab.text.selected.focuslost.glass",
        
        "glass.selected.center",
        "glass.selected.light",
        "glass.selected.boundary",

        "glass.focused.center",
        "glass.focused.light",
        "glass.focused.boundary"
    };

    public GlassEclipseTabTransmitter( ColorManager manager ){
        super( manager, KEYS );
    }
    
    public boolean matches( String id ){
	    return id.startsWith( "glass." ) || id.endsWith( ".glass" );
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

        if( "stack.tab.border.glass".equals( key ))
            return source;

        if(  "stack.tab.top.glass".equals( key ))
            return Colors.undiffMirror( source, 0.5 );

        if( "stack.tab.bottom.glass".equals( key ))
            return source;

        if( "stack.tab.text.glass".equals( key ))
            return Colors.diffMirror( source, 1.0 );

        return null;
    }

    @Override
    protected Color convertSelected( Color source, String key ) {
        if( isFocused( key ))
            return convertFocused( source, key );

        if( "stack.tab.border.selected.glass".equals( key ))
            return source;

        if(  "stack.tab.top.selected.glass".equals( key ))
            return Colors.undiffMirror( source, 0.5 );

        if( "stack.tab.bottom.selected.glass".equals( key ))
            return source;

        if( "stack.tab.text.selected.glass".equals( key ))
            return Colors.diffMirror( source, 1.0 );

        if( "glass.selected.center".equals( key ))
        	return Colors.brighter( source, 0.25 );
        	
        if( "glass.selected.light".equals( key ))
        	return Colors.brighter( source, 0.5 );
        	
        if( "glass.selected.boundary".equals( key ))
        	return Colors.darker( source, 0.25 );

        
        return null;
    }

    @Override
    protected Color convertFocused( Color source, String key ) {
        if( "stack.tab.border.selected.focused.glass".equals( key ))
            return source;
        if( "stack.tab.border.selected.focuslost.glass".equals( key ))
            return source;

        if( "stack.tab.top.selected.focused.glass".equals( key ))
            return Colors.undiffMirror( source, 0.5 );
        if( "stack.tab.top.selected.focuslost.glass".equals( key ))
            return Colors.undiffMirror( source, 0.5 );

        if( "stack.tab.bottom.selected.focused.glass".equals( key ))
            return source;
        if( "stack.tab.bottom.selected.focuslost.glass".equals( key ))
            return source;

        if( "stack.tab.text.selected.focused.glass".equals( key ))
            return Colors.diffMirror( source, 1.0 ); 
        if( "stack.tab.text.selected.focuslost.glass".equals( key ))
            return Colors.diffMirror( source, 1.0 );

        if( "glass.focused.center".equals( key ))
        	return source;
        	
        if( "glass.focused.light".equals( key ))
        	return Colors.brighter( source, 0.5 );
        	
        if( "glass.focused.boundary".equals( key ))
        	return Colors.darker( source, 0.5 );
        
        return null;
    }
}
