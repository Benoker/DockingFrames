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
package bibliothek.gui.dock.common.intern.font;

import java.awt.Color;

import bibliothek.gui.dock.common.FontMap;
import bibliothek.gui.dock.common.event.FontMapListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.ui.UITransmitter;
import bibliothek.gui.dock.util.font.DockFont;
import bibliothek.gui.dock.util.font.FontBridge;
import bibliothek.gui.dock.util.font.FontManager;
import bibliothek.gui.dock.util.font.FontModifier;

/**
 * A {@link FontTransmitter} observes some {@link FontMap} and transmits
 * {@link FontModifier}s of these maps to a set of {@link DockFont}s.
 * @author Benjamin Sigg
 */
public abstract class FontTransmitter extends UITransmitter<FontModifier, DockFont> implements FontBridge{
    /** listens to a {@link FontMap} */
    private Listener listener = new Listener();
    
    /** source of unspoiled values */
    private FontManager manager;
    
    /** the observed keys */
    private String[] keys;
    
    /**
     * Creates a new {@link FontTransmitter}.
     * @param manager the source of original values
     * @param keys the keys which should be monitored by this transmitter
     */
    public FontTransmitter( FontManager manager, String... keys ){
        super( keys );
        this.manager = manager;
        this.keys = keys;
    }
    
    /**
     * Gets the first non- <code>null</code> color of <code>map</code> that
     * matches a given key.
     * @param map a map of colors
     * @param keys some keys that will be read from index 0 upward.
     * @return the first {@link Color} that is not <code>null</code> or <code>null</code>
     */
    protected FontModifier getFirstNonNull( FontMap map, String...keys ){
        for( String key : keys ){
            FontModifier font = map.getFont( key );
            if( font != null )
                return font;
        }
        return null;
    }
    
    @Override
    protected void connect( CDockable dockable ) {
        dockable.getFonts().addListener( listener );
    }
    
    @Override
    protected void disconnect( CDockable dockable ) {
        dockable.getFonts().removeListener( listener );
    }
    
    @Override
    protected void update( CDockable dockable, String key, FontModifier value ) {
        if( isObservedMapKey( key )){
            for( String check : keys ){
                set( check, get( manager.get( check ), check, dockable ), dockable );
            }
        }
    }
    
    /**
     * Tells whether <code>key</code> is one of the keys observed in
     * the {@link FontMap}.
     * @param key the key which might be observed
     * @return <code>true</code> if a change of <code>key</code> can
     * result in a change of fonts
     */
    protected abstract boolean isObservedMapKey( String key );
    
    /**
     * Transforms <code>value</code> into the form that should be used together
     * with <code>dockable</code>.
     * @param value some default value for <code>id</code>
     * @param id some of the keys specified for observation
     * @param dockable the element for which the value would be used
     * @return either <code>value</code> or a transformed version of <code>value</code>
     */
    protected abstract FontModifier get( FontModifier value, String id, CDockable dockable );
    
    /**
     * A listener that gets informed when new maps join or some color in a map
     * changes.
     * @author Benjamin Sigg
     */
    private class Listener implements FontMapListener{
        public void fontChanged( FontMap map, String key, FontModifier font ) {
            update( map.getDockable(), key, font );
        }
    }
}
