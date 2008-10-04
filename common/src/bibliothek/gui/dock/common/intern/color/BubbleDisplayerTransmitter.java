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

import bibliothek.extension.gui.dock.theme.bubble.BubbleDisplayer;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.ColorMap;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.themes.color.DisplayerColor;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.color.DockColor;
import bibliothek.util.Colors;

/**
 * A transmitter connecting {@link ColorMap} and {@link BubbleDisplayer}.
 * @author Benjamin Sigg
 */
public class BubbleDisplayerTransmitter extends ColorTransmitter {
    private static final String[] KEYS = {
        "displayer.border.high.active",
        "displayer.border.high.active.mouse",
        "displayer.border.high.inactive",
        "displayer.border.high.inactive.mouse",
        "displayer.border.low.active",
        "displayer.border.low.active.mouse",
        "displayer.border.low.inactive",
        "displayer.border.low.inactive.mouse"
    };
    
    private ColorManager manager;
    
    /**
     * Creates a new transmitter.
     * @param manager the source for new colors
     */
    public BubbleDisplayerTransmitter( ColorManager manager ){
        super( KEYS );
        this.manager = manager;
    }
    
    @Override
    protected Color get( Color color, String id, DockColor observer ) {
        CDockable dockable = getDockable( observer );
        return get( color, id, dockable );
    }
    
    /**
     * Searches for a color that fits for the color with name <code>id</code>.
     * @param color the original color
     * @param id the name of the requested color
     * @param dockable the element for which the color will be used
     * @return either a new color or <code>color</code>
     */
    protected Color get( Color color, String id, CDockable dockable ){
        if( dockable == null )
            return color;
        
        ColorMap map = dockable.getColors();
        Color check = null;
        
        if( id.contains( "active" ) && !id.contains( "inactive" )){
            check = map.getColor( ColorMap.COLOR_KEY_TITLE_BACKGROUND_FOCUSED );
            if( check != null )
                check = convertFocused( check, id );
        }
        
        if( check == null ){
            check = map.getColor( ColorMap.COLOR_KEY_TITLE_BACKGROUND );
            if( check != null )
                check = convert( check, id );
        }
        
        if( check != null )
            return check;
        
        return color;
    }
    
    @Override
    protected void update( CDockable dockable, String key, Color color ) {
        boolean change = ColorMap.COLOR_KEY_TITLE_BACKGROUND.equals( key ) ||
        ColorMap.COLOR_KEY_TITLE_BACKGROUND_FOCUSED.equals( key );

    if( change ){
        for( String check : KEYS )
            set( check, get( manager.get( check ), check, dockable ), dockable );
        }
    }
    
    /**
     * Converts a color given by a {@link ColorMap} to the color needed 
     * on the displayer.
     * @param source the source color
     * @param key the color that is needed
     * @return the converted <code>source</code>, can be <code>null</code>
     */
    protected Color convert( Color source, String key ) {
        if( key.contains( "active" ) && !key.contains( "inactive" ) )
            return convertFocused( Colors.diffMirror( source, 0.2 ), key );
        
        if( "displayer.border.low.inactive".equals( key ))
            return Colors.darker( source, 0.3 );
        if( "displayer.border.high.inactive".equals( key ))
            return Colors.brighter( source, 0.3 );
        
        if( "displayer.border.low.inactive.mouse".equals( key ))
            return Colors.fuller( Colors.darker( source, 0.3 ), 0.3 );
        if( "displayer.border.high.inactive.mouse".equals( key ))
            return Colors.fuller( Colors.brighter( source, 0.3 ), 0.3 );
        
        return source;
    }

    /**
     * Converts a color given by a {@link ColorMap} to the color needed 
     * on the displayer.
     * @param source the source color
     * @param key the color that is needed, the color is needed for a 
     * focused view
     * @return the converted <code>source</code>, can be <code>null</code>
     */
    protected Color convertFocused( Color source, String key ) {
        if( "displayer.border.low.active".equals( key ))
            return Colors.darker( source, 0.3 );
        if( "displayer.border.high.active".equals( key ))
            return Colors.brighter( source, 0.3 );
        
        if( "displayer.border.low.active.mouse".equals( key ))
            return Colors.fuller( Colors.darker( source, 0.3 ), 0.3 );
        if( "displayer.border.high.active.mouse".equals( key ))
            return Colors.fuller( Colors.brighter( source, 0.3 ), 0.3 );
        
        return source;
    }

    @Override
    protected CDockable getDockable( DockColor observer ) {
        Dockable dockable = ((DisplayerColor)observer).getDisplayer().getDockable();
        if( dockable == null )
            return null;
        
        if( dockable instanceof CommonDockable )
            return ((CommonDockable)dockable).getDockable();
        
        return null;
    }
}
