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

import bibliothek.gui.dock.common.ColorMap;
import bibliothek.gui.dock.common.event.ColorMapListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.ui.UITransmitter;
import bibliothek.gui.dock.util.color.ColorBridge;
import bibliothek.gui.dock.util.color.DockColor;

/**
 * A <code>ColorTransmitter</code> observes some {@link ColorMap}s
 * and transmits some {@link Color}s of these maps to a set of {@link DockColor}s.
 * @author Benjamin Sigg
 */
public abstract class ColorTransmitter extends UITransmitter<Color, DockColor> implements ColorBridge {
    /** listens to a {@link ColorMap} */
    private Listener listener = new Listener();
    
    /**
     * Creates a new {@link ColorTransmitter}.
     * @param keys the keys which should be monitored by this transmitter
     */
    public ColorTransmitter( String... keys ){
        super( keys );
    }
    
    /**
     * Gets the first non- <code>null</code> color of <code>map</code> that
     * matches a given key.
     * @param map a map of colors
     * @param keys some keys that will be read from index 0 upward.
     * @return the first {@link Color} that is not <code>null</code> or <code>null</code>
     */
    protected Color getFirstNonNull( ColorMap map, String...keys ){
        for( String key : keys ){
            Color color = map.getColor( key );
            if( color != null )
                return color;
        }
        return null;
    }
    
    @Override
    protected void connect( CDockable dockable ) {
        dockable.getColors().addListener( listener );
    }
    
    @Override
    protected void disconnect( CDockable dockable ) {
        dockable.getColors().removeListener( listener );
    }
    
    /**
     * A listener that gets informed when new maps join or some color in a map
     * changes.
     * @author Benjamin Sigg
     */
    private class Listener implements ColorMapListener{
        public void colorChanged( ColorMap map, String key, Color color ) {
            update( map.getDockable(), key, color );
        }
    }
}
