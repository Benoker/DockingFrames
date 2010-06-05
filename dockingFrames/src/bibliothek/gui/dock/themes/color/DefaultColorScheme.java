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
package bibliothek.gui.dock.themes.color;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.dock.themes.ColorBridgeFactory;
import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.color.DockColor;
import bibliothek.util.Path;

/**
 * An implementation of {@link ColorScheme} that can guess colors
 * for ids that are not known. This scheme returns the one color whose
 * keys matches a given id best.
 * @author Benjamin Sigg
 */
public class DefaultColorScheme implements ColorScheme{
    private Map<String, Color> colors = new HashMap<String, Color>();
    private Map<Path, ColorBridgeFactory> bridges =
        new HashMap<Path, ColorBridgeFactory>();
    
    public boolean updateUI() {
        return false;
    }
    
    /**
     * Sets the value of some color.
     * @param id the identifier of the color
     * @param color the color or <code>null</code>
     */
    public void setColor( String id, Color color ){
        if( color == null )
            colors.remove( id );
        else
            colors.put( id, color );
    }
    
    /**
     * Sets the value of some provider.
     * @param kind the kind of {@link DockColor}s the provider works with
     * @param bridge the provider or <code>null</code>
     */
    public void setBridgeFactory( Path kind, ColorBridgeFactory bridge ){
        if( bridge == null )
            bridges.remove( kind );
        else
            bridges.put( kind, bridge );
    }
    
    public Color getColor( String id ) {
        Color color = colors.get( id );
        if( color != null )
            return color;
        
        int best = -1;
        for( Map.Entry<String, Color> entry : colors.entrySet() ){
            if( id.startsWith( entry.getKey() )){
                if( entry.getKey().length() > best ){
                    best = entry.getKey().length();
                    color = entry.getValue();
                }
            }
        }
        
        return color;
    }

    public ColorBridgeFactory getBridgeFactory( Path kind ) {
        while( kind != null ){
            ColorBridgeFactory factory = bridges.get( kind );
            if( factory != null )
                return factory;
            
            kind = kind.getParent();
        }
        
        return null;
    }
    
    public void transmitAll( Priority priority, ColorManager manager ) {
        try{
            manager.lockUpdate();
        
            for( Map.Entry<String, Color> entry : colors.entrySet() )
                manager.put( priority, entry.getKey(), entry.getValue() );
            
            for( Map.Entry<Path, ColorBridgeFactory> entry : bridges.entrySet() )
                manager.publish( 
                        priority, 
                        entry.getKey(), 
                        entry.getValue().create( manager ) );
        }
        finally{
            manager.unlockUpdate();
        }
    }
}
