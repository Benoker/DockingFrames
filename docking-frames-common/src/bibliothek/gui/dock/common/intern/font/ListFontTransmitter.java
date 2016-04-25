/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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

import bibliothek.gui.dock.common.FontMap;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.util.font.DockFont;
import bibliothek.gui.dock.util.font.FontManager;
import bibliothek.gui.dock.util.font.FontModifier;

/**
 * A {@link FontTransmitter} that has a simple 1 to 1 relationship between
 * keys.
 * @author Benjamin Sigg
 *
 */
public abstract class ListFontTransmitter extends FontTransmitter{
    private String[] source;
    private String[] destination;
    
    /**
     * Creates a new transmitter. The arrays <code>sourceKeys</code> and
     * <code>destinationKeys</code> must have the same length.
     * @param manager the list of default values
     * @param sourceKeys keys used to read from a {@link FontMap}
     * @param destinationKeys keys used to write into a {@link DockFont}
     */
    public ListFontTransmitter( FontManager manager, String[] sourceKeys, String[] destinationKeys ){
        super( manager, destinationKeys );
        this.source = sourceKeys;
        this.destination = destinationKeys;
    }
    

    @Override
    protected FontModifier get( FontModifier value, String id, DockFont observer ) {
        CDockable dockable = getDockable( observer );
        if( dockable == null )
            return value;
        
        return get( value, id, dockable );
    }
    
    @Override
    protected boolean isObservedMapKey( String key ) {
        for( String observer : source ){
            if( observer.equals( key ))
                return true;
        }
        return false;
    }
    
    private FontModifier getFirstNonNull( FontMap fonts, int index ){
        for( int i = index, n = source.length; i<n; i++ ){
            FontModifier font = fonts.getFont( source[i] );
            if( font != null )
                return font;
        }
        return null;
    }

    @Override
    protected FontModifier get( FontModifier value, String id, CDockable dockable ){
        FontModifier result = null;
        
        for( int i = 0, n = destination.length; i<n; i++ ){
            if( destination[i].equals( id )){
                result = getFirstNonNull( dockable.getFonts(), i );
                break;
            }
        }
        
        if( result == null )
            result = value;
        
        return result;
    }
}
